#!/usr/bin/perl
#
# build changes from svn 1.7+ tree
#
# args:
# --target-base=path  path to destination dir
# --source-base=path  trunk or a branch
# --runners=N         number of runners to build stuff: default 8

use Getopt::Long;
use File::Basename;
use Cwd 'abs_path';

my $script_path;
BEGIN {
    $script_path = abs_path dirname $0;
    $script_path =~ /(.*)/;
    $script_path = $1;
    unshift @INC, "$script_path/lib";
}

use ASF::Util 'parse_filename';
use ASF::SVNUtil;
use strict;
use warnings;

umask 022;

$|=1;

my $CMS_PROP_NAME = "cms:source-revision";

my ($target_base, $source_base, $runners);

GetOptions ( "target-base=s", \$target_base,
             "source-base=s", \$source_base,
             "runners=i", \$runners,
);

die <<USAGE unless defined $target_base and -d $source_base;
Usage: $0 --source-base /path/to/trunk/or/a/branch --target-base /path/to/target [--runners=N]
USAGE

$_ = abs_path($_) and s!/+$!! for $source_base, $target_base;
$runners ||= 8; # educated guess

die "Path contains '-char!\n" if grep /'/, $source_base, $target_base, $script_path;

chdir $source_base or die "Can't chdir to $source_base: $!\n";

fork or (svn_up($target_base), exit 0) if is_version_controlled($target_base);
my %modified = svn_up($source_base);
my $status = 0;

if (is_version_controlled($target_base)) {
    wait;
    $status = $?;

    my %seen;
    for my $del (@{$modified{delete}}) {

        my ($filename, $dirname, $ext) = parse_filename $del;

        -e "$target_base/$del" && svn_rm("$target_base/$del"), next
            if ! defined $ext or -e "$target_base/$del" or $del !~ /^(?:content|cgi-bin)/;

        local $_ = "$dirname$filename.*";

        my %candidates = map +($_ => 1), grep -f && ! $seen{$_}++, glob "$target_base/$_";
        if (my $c = join "' '", map {s/'/'\\''/g;$_} glob) {
            delete @candidates{
                map /^Built to (.+)\.$/,
                    `'$script_path/build_file.pl' --source-base '$source_base' --target-base '$target_base' '$c'`
            };
            die "Can't build_file.pl --source-base '$source_base' --target-base '$target_base' '$c': $?\n"
                if $?;
        }
        svn_rm(keys %candidates);
    }
}

my $built_site = 0;

for (@{$modified{delete}}, @{$modified{add}}, grep -f, @{$modified{update}}) {
    if (@{$modified{delete}} or m!^templates/! or m!^lib/!)  {

        system "$script_path/build_site.pl",
            "--source-base", $source_base, "--target-base", $target_base,
                "--runners", $runners
                    and $status = -1;
        $built_site = 1;
        last;
    }
}

unless ($built_site) {
    my @sources;

    unshift @INC, "$source_base/lib";
    require path;
    no warnings 'once';
    my %deps = %path::dependencies;
    $_ = [@$_] for values %deps;
    flatten_and_uniquify(\%deps); # converts %deps values to hashrefs as well

    my %seen;
    for my $file (grep -f && /^content|^cgi-bin/, @{$modified{update}}, @{$modified{add}}) {
        my @s;
        if ($file =~ m!^(.+)\.page/!) {
            push @s, grep -f && ! $seen{$_}++, glob "$1.*";
        }
        else {
            push @s, $file if ! $seen{$file}++;
        }
        push @sources, @s;

        for my $key (keys %deps) {
            delete $deps{$key} and next if grep $_ eq "content$key", @s;

            if (do {my $f; for my $path (grep s/^content//, map "$_", @s) {
                $f++, last if exists $deps{$key}->{$path}} $f}) {

                push @sources, "content$key" if ! $seen{"content$key"}++;
                delete $deps{$key};
            }
        }
    }
    my $accel = "";
    $accel = "-P $runners" if $^O =~ /freebsd|linux/i; # xargs parallelization
    open my $builder, "| xargs -0 $accel -n $runners '$script_path/build_file.pl' "
        . "--source-base '$source_base' --target-base '$target_base'"
            or die "Can't popen xargs -0 $accel -n $runners $script_path/build_file.pl ...: $?";

    print $builder "$_\0" for @sources;

    close $builder;
    $status = -1 if $?;

}

if (is_version_controlled($target_base) and not $status) {
    my %status = svn_status($target_base);
    svn_add(@{$status{unversioned}});
    svn_rm(@{$status{missing}});
    svn_ps("$target_base/content", $CMS_PROP_NAME, $modified{revision}) if -d "$target_base/content";
    svn_ps("$target_base/cgi-bin", $CMS_PROP_NAME, $modified{revision}) if -d "$target_base/cgi-bin";
    warn "$_ is conflicted!\n" for @{$status{conflicted}};
}

exit $status;

# now supports loops in the dependency graph

sub flatten_and_uniquify {
    my $deps = shift;
    my %d = %$deps;
    $_ = [@$_] for values %d;
    for my $k (keys %$deps) {
        my $s = 0;
        my %seen;
        undef @seen{$k, @{$deps->{$k}}};
        until ($s == @{$deps->{$k}}) {
            if (exists $d{$deps->{$k}->[$s]}) {
                push @{$deps->{$k}}, grep {! exists $seen{$_}} @{$d{$deps->{$k}->[$s]}};
                undef @seen{@{$d{$deps->{$k}->[$s]}}};
            }
            $s++;
            die "way too many transitive dependencies (loop?)\n" if $s > 100_000;
        }
        delete $seen{$k};
        $deps->{$k} = \%seen;
    }
}

=head1 LICENSE

           Licensed to the Apache Software Foundation (ASF) under one
           or more contributor license agreements.  See the NOTICE file
           distributed with this work for additional information
           regarding copyright ownership.  The ASF licenses this file
           to you under the Apache License, Version 2.0 (the
           "License"); you may not use this file except in compliance
           with the License.  You may obtain a copy of the License at

             http://www.apache.org/licenses/LICENSE-2.0

           Unless required by applicable law or agreed to in writing,
           software distributed under the License is distributed on an
           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
           KIND, either express or implied.  See the License for the
           specific language governing permissions and limitations
           under the License.

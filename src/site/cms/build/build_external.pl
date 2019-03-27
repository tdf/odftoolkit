#!/usr/bin/perl
#
# build site using external build system
# requires svn 1.7 working copies
#
# args:
# --target-base=path  path to destination dir
# --source-base=path  trunk or a branch
# --type=$type        maven or ant or forrest or shell or ...
# --nonukes           don't prenuke the build targets
# --noprops           don't add a cms property to the target

use File::Copy;
use File::Path qw/rmtree mkpath/;
use Getopt::Long;
use Cwd 'abs_path';
use File::Basename;

BEGIN {
    my $script_path = dirname($0);
    $script_path = abs_path($script_path);
    $script_path =~ /(.*)/;
    $script_path = $1;
    unshift @INC, "$script_path/lib";
}

use ASF::SVNUtil;
use strict;
use warnings;

umask 022;

$|=1;

my $CMS_PROP_NAME = "cms:source-revision";

my ($target_base, $source_base, $type, $nonukes, $noprops);

GetOptions ( "target-base=s", \$target_base,
             "source-base=s", \$source_base,
             "type=s",        \$type,
             "nonukes",       \$nonukes,
             "noprops",       \$noprops,
);

die <<USAGE unless defined $target_base and -d $source_base and defined $type;
Usage: $0 --source-base /path/to/trunk/or/a/branch --target-base /path/to/target --type [maven|ant|forrest|shell|...] [--nonukes] [--noprops]
USAGE

$_ = abs_path($_) and s!/+$!! for $source_base, $target_base;

chdir $source_base or die "Can't chdir to $source_base: $!\n";

fork or (svn_up($target_base), exit 0) if is_version_controlled($target_base);
my %modified = svn_up($source_base);
my $status = 0;

if (is_version_controlled($target_base)) {
    wait;
    $status = $?;
}

my @system_args;

if ($type eq "maven") {
    my $simple_copy = 1;
    for (grep -f, @{$modified{add}}, @{$modified{update}}) {
        next if m!^content/resources/!;
        $simple_copy = 0, last;
    }
    if ($simple_copy and not @{$modified{delete}}) {
        for (grep -f, @{$modified{add}}) {
            my $t = $_;
            $t = "$target_base/$t";
            $t =~ s!/content/resources/!/content/!;
            my $p = $t;
            $p =~ s!/[^/]+$!!;
            mkpath $p;
            copy $_, $t;
        }
        for (grep -f, @{$modified{update}}) {
            my $t = $_;
            $t = "$target_base/$t";
            $t =~ s!/content/resources/!/content/!;
            copy $_, $t;
        }
    }
    else {
        @system_args = ("mvn", "-Dsite.output=$target_base/content", "site");
        rmtree("$target_base/content") unless $nonukes;
        rmtree("$target_base/cgi-bin") unless $nonukes;
    }
}
elsif ($type eq "ant") {
    @system_args = ("ant", "-Ddocs.dest=$target_base/content", "site");
    rmtree("$target_base/content") unless $nonukes;
    rmtree("$target_base/cgi-bin") unless $nonukes;
}
elsif ($type eq "forrest") {
    @system_args = (qw/forrest site/, ); # XXX what to invoke for forrest?
    rmtree("$target_base/content") unless $nonukes;
    rmtree("$target_base/cgi-bin") unless $nonukes;
}
elsif ($type eq "shell") {
    # This is a catch-all for situations like if maven or ant cannot be trivially modified
    # to support the necessary CLI.  Simply let those build systems do their thing
    # and rsync their internal target directories over to $target_base/content.  Not exactly
    # efficient, but hey you're using java ;-)
    #
    # Hybrid builds consisting of some or all of the above build systems are also possible here.
    #
    @system_args = ("./build_cms.sh", $source_base, $target_base);
    rmtree("$target_base/content") unless $nonukes;
    rmtree("$target_base/cgi-bin") unless $nonukes;
}
else {
    die "Unsupported type: $type\n";
}

system @system_args and $status = -1 if @system_args;

if (is_version_controlled($target_base) and not $status) {
    my %status = svn_status($target_base);
    svn_add(@{$status{unversioned}});
    svn_rm(@{$status{missing}});
    unless ($noprops) {
        svn_ps("$target_base/content", $CMS_PROP_NAME, $modified{revision}) if -d "$target_base/content";
        svn_ps("$target_base/cgi-bin", $CMS_PROP_NAME, $modified{revision}) if -d "$target_base/cgi-bin";
    }
    warn "$_ is conflicted!\n" for @{$status{conflicted}};
}

exit $status;

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

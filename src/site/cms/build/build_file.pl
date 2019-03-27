#!/usr/bin/perl

# build individual urls
#
# args:
# --target-base=path  path to target dir
# --source-base=path  path to base of source tree
#                     (either trunk or a branch)
# --source=path       (relative to source-base) path to source file/dir
# --offline           don't process "dynamic" content from ASF::Value::*

use File::Basename;
use Cwd 'abs_path';

BEGIN {
    my $script_path = dirname($0);
    $script_path = abs_path($script_path);
    $script_path =~ /(.*)/;
    $script_path = $1;
    unshift @INC, "$script_path/lib";
    $ENV{MARKDOWN_SOCKET} ||= "/usr/local/cms/logs/markdown.socket";
}

use strict;
use warnings;
use Getopt::Long;
use File::Path;
use ASF::Util qw/copy_if_newer parse_filename/;
use POSIX qw/_exit/;

my ($target_base, $source_base, $source, $offline);

GetOptions ( "target-base=s" => \$target_base,
             "source-base=s" => \$source_base,
             "source=s"      => \$source,
             "offline"       => \$offline,
);

die <<USAGE unless defined $target_base and -d $source_base;
Usage: $0 --source-base /path/to/trunk/or/a/branch [--source rel/path/to/source/file] --target-base /path/to/target [--offline] [files relative to source base  ...]
USAGE

$_ = abs_path($_) and s!/+$!! for $source_base, $target_base;

chdir $source_base or die "Can't chdir to $source_base: $!\n";

my @sources = @ARGV;
unshift @sources, $source if defined $source;

-f $_ or die "Can't locate source file: $_\n" for @sources;
$ENV{TARGET_BASE} = $target_base;

unshift @INC, "$source_base/lib";
require path;
require view;

{
    no warnings 'once';
    $ASF::Value::Offline = 1 if $offline;
}

$|=1;

for my $s (@sources) {

    defined(my $pid = fork) or die "Can't fork: $!";
    next if $pid;
    # in child
    print "Building $s ...\n";

    my ($filename, $dirname) = parse_filename $s;
    my $target_file = $dirname . $filename;

    mkpath "$target_base/$dirname";

    if ( -d "$target_file.page") {
        my $content_dir = "$target_file.page";
        mkpath "$target_base/$content_dir";
        for my $file (grep -f,  glob "$content_dir/*") {
            copy_if_newer $file, "$target_base/$file";
        }
    }

    my $path = $s;
    $path =~ s!^content!!;

    my $matched = 0;
    my $status = 0;

    no warnings 'once';
    for my $p (@path::patterns) {
        my ($re, $method, $args) = @$p;
        next unless $path =~ $re;
        eval {
            # no need to deepcopy $args since forks take care of that
            my $sub = view->can($method) or die "Can't locate method: $method\n";
            my ($content, $ext) = $sub->(path => $path, %$args);
            $s = "$target_file.$ext";
            open my $fh, ">", "$target_base/$s"
                or die "Can't open $target_base/$s: $!\n";
            print $fh $content;
        };
        $status = -1, warn $@ if $@;
        $matched = 1;
        last;
    }

    unless ($matched) {
        copy_if_newer $s, "$target_base/$s";
    }

    print "Built to $target_base/$s.\n" unless $status;
    _exit $status; # work around segfaults during global cleanup
}

# only parent gets this far
my $status = 0;
$? && $status++ while wait > 0;
_exit $status;

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


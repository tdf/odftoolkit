#!/usr/bin/perl
#
# build site
#
# args:
# --target-base=path  path to destination dir
# --source-base=path  trunk or a branch
# --runners=N         number of runners to use (default 8)
# --offline           don't process "dynamic" content from ASF::Value::*

use File::Basename;
use Cwd 'abs_path';
use POSIX qw/_exit/;
use IO::Select;
use List::Util qw/shuffle/;
use Socket;

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
use Data::Dumper ();

my ($target_base, $source_base, $runners, $offline);

GetOptions ( "target-base=s", \$target_base,
             "source-base=s", \$source_base,
             "runners=i", \$runners,
             "offline", \$offline,
);

die <<USAGE unless defined $target_base and -d $source_base;
Usage: $0 --source-base /path/to/trunk/or/a/branch --target-base /path/to/target [ --runners N ] [--offline]
USAGE

$_ = abs_path($_) and s!/+$!! for $source_base, $target_base;
$runners ||= 8; # 8 is arbitrary but educated guess

chdir $source_base or die "Can't chdir to $source_base: $!\n";
$ENV{TARGET_BASE} = $target_base;

unshift @INC, "$source_base/lib";
require path;
require view;

{
    no warnings 'once';
    $ASF::Value::Offline = 1 if $offline;
}

my @errors;
my @dirqueue = ("cgi-bin", "content");

$runners = $path::runners if defined $path::runners and $path::runners < $runners;
my @runners = map fork_runner(), 1..$runners;
my @fd2rid;
$fd2rid[fileno $runners[$_]->{socket}] = $_ for 0..$#runners;

my $sockets = IO::Select->new;
$sockets->add(map $_->{socket}, @runners);

$|=1;
print "Building site (runners = $runners)...\n";
my $saw_error = 0;

LOOP: while (@dirqueue) {
    my $would_block = 1;

    for my $p (shuffle $sockets->can_write(0)) {
        $would_block = 0;
        my $dir = shift @dirqueue or last;

        if (syswrite_all($p, "$dir\n") <= 0) {
            warn "syswrite_all failed: $! ", fileno $p;
            unshift @dirqueue, $dir;
            $sockets->remove($p);
            $runners[$fd2rid[fileno $p]]->{wait} = 1;
            close $p;
            $saw_error++;
            next;
        }
        $runners[$fd2rid[fileno $p]]->{wait} = 0;
    }
    last if $would_block;
}

for my $p ($sockets->can_read(3)) {
    local $_ = '';
    my $bytes;

    while (($bytes = sysread $p, $_, 4096, length) > 0) {
        last if substr($_, -1, 1) eq "\n";
    }
    if ($bytes <= 0) {
        warn "sysread failed: $! ", fileno $p;
        $sockets->remove($p);
        $runners[$fd2rid[fileno $p]]->{wait} = 1;
        close $p;
        $saw_error++;
        next;
    }
    push @dirqueue, grep length && $_ ne "working...", split /\n/;
    $runners[$fd2rid[fileno $p]]->{wait} = /(?:^$)\Z/m;
}

goto LOOP if @dirqueue or grep !$_->{wait}, @runners;

shutdown $_, 1 for map $_->{socket}, @runners;
$? && ++$saw_error while wait > 0; # if our assumptions are wrong, we'll know here
print "All done.\n";
_exit -1 if $saw_error;
_exit 0; # avoid global cleanup segfault

sub process_dir {
    my ($root, $wtr, $final) = @_;
    opendir my $dir, $root or warn "Can't open $root [skipping]: $!" and return;
    my $made_target_dir;

    no warnings 'uninitialized';
    for (map $_->[0], sort {$b->[1] <=> $a->[1]} map [$_, -d],# dirs first, schwartzian xform
         map "$root/$_", grep $_ ne "." && $_ ne ".." && $_ ne ".svn", readdir $dir) {

        if (-d and not $final) {
            if (m!\.page$!) {
                process_dir($_, $wtr, "final");
                next;
            }
            if (syswrite_all($wtr, "$_\n") <= 0) {
                die "syswrite_all failed: $!";
            }
            next;
        }
        if (-f _) {
            mkpath "$target_base/$root" unless $made_target_dir++;
            eval { process_file($_) };
            push @errors, [$_, $@] if $@;
        }
        else {
            warn "skipping unrecognized entry: $_\n";
        }
    }
}

sub process_file {
    my $file = shift;
    my ($filename, $dirname) = parse_filename $file;

    if ($dirname =~ m!\b\.page/$!) {
        copy_if_newer $file, "$target_base/$file";
        return;
    }

    my $target_file = $dirname . $filename;

    my $path = $file;
    $path =~ s!^content!!;

    my $matched;

    no warnings 'once';
    for my $p (@path::patterns) {
        my ($re, $method, $args) = @$p;
        next unless $path =~ $re;
        if ($args->{headers}) {
            my $d = Data::Dumper->new([$args->{headers}], ['$args->{headers}']);
            $d->Deepcopy(1)->Purity(1);
            eval $d->Dump;
        }
        my $s = view->can($method) or die "Can't locate method: $method\n";
        my ($content, $ext) = $s->(path => $path, %$args);
        open my $fh, ">", "$target_base/$target_file.$ext"
            or die "Can't open $target_base/$target_file.$ext: $!\n";
        print $fh $content;
        $matched = 1;
        last;
    }

    unless ($matched) {
        copy_if_newer $file, "$target_base/$file";
    }
}

sub fork_runner {
    socketpair my $child, my $parent, AF_UNIX, SOCK_STREAM, PF_UNSPEC
        or die "socketpair: $!";
    binmode $_ for $child, $parent;
    defined(my $pid = fork) or die "Can't fork: $!\n";
    if ($pid) {
        # in parent
        close $parent;
        return { pid => $pid, socket => $child, wait => 1 };
    }
    # in child
    close $child;
    my $r = IO::Select->new;
    $r->add($parent);

    while (1) {
        my ($p) = $r->can_read();
        # minor race condition: this issue seems inherent to any attempts
        # to communicate process state via sockets, and since we aren't
        # building software, but websites, the bang-for-the-buck tradeoff is
        # well worth the risks.

        # notify parent we are beginning work
        if (syswrite_all($parent, "working...\n") <= 0) {
            die "syswrite_all failed: $!";
        }

        local $_ = '';
        my $bytes;
        while (($bytes = sysread $p, $_, 4096, length) > 0) {
            last if substr($_, -1, 1) eq "\n";
        }
        process_dir($_, $parent) for split /\n/;
        last if $bytes <= 0;

        # notify parent we are waiting for more input
        if (syswrite_all($parent, "\n") <= 0) {
            die "syswrite_all failed: $!";
        }
    }
    warn "File $_->[0] had processing errors: $_->[1]" for @errors;
    _exit -1 if @errors;
    _exit 0; # avoid segfault on global cleanup
}

sub syswrite_all {
    my ($fh, $data) = @_;
    my $bytes;
    my $total = 0;
    while (($bytes = syswrite($fh, substr($data, $total))) > 0) {
        $total += $bytes;
        return $total if $total == length $data;
    }
    return $bytes;
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

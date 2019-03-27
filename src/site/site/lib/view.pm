package view;

#
# BUILD CONSTRAINT:  all views must return $content, $extension.
# additional return values (as seen below) are optional.  However,
# careful use of symlinks and dependency management in path.pm can
# resolve most issues with this constraint.
#

use strict;
use warnings;
use Dotiac::DTL qw/Template *TEMPLATE_DIRS/;
use Dotiac::DTL::Addon::markup;
use ASF::Util qw/read_text_file shuffle/;
use File::Temp qw/tempfile/;
use LWP::Simple;

push our @TEMPLATE_DIRS, "templates";

# This is most widely used view.  It takes a
# 'template' argument and a 'path' argument.
# Assuming the path ends in foo.mdtext, any files
# like foo.page/bar.mdtext will be parsed and
# passed to the template in the "bar" (hash)
# variable.

sub single_narrative {
    my %args = @_;
    my %styleargs = @_;
    my $file = "content$args{path}";
    my $template = $args{template};
    $args{path} =~ s/\.mdtext$/\.html/;
    $args{breadcrumbs} = breadcrumbs($args{path});

    read_text_file $file, \%args;

    my $page_path = $file;
    $page_path =~ s/\.[^.]+$/.page/;
    if (-d $page_path) {
        for my $f (grep -f, glob "$page_path/*.mdtext") {
            $f =~ m!/([^/]+)\.mdtext$! or die "Bad filename: $f\n";
            $args{$1} = {};
            read_text_file $f, $args{$1};
        }
    }
    
    $args{content} = sort_tables($args{content});
    $args{sidenav} = 1;

    my $style_path = $file;
    $style_path =~ s/\.[^.]+$/.style/;
    if (-f $style_path) {
	read_text_file $style_path, \%styleargs;
	$args{scriptstyle} = $styleargs{content};
    }

    return Template($template)->render(\%args), html => \%args;
}

sub sitemap {
    my %args = @_;
    my $template = "content$args{path}";
    $args{breadcrumbs} .= breadcrumbs($args{path});
    my $dir = $template;
    $dir =~ s!/[^/]+$!!;
    opendir my $dh, $dir or die "Can't opendir $dir: $!\n";
    my %data;
    for (map "$dir/$_", grep $_ ne "." && $_ ne ".." && $_ ne ".svn", readdir $dh) {
        if (-f and /\.mdtext$/) {
            my $file = $_;
            $file =~ s/^content//;
            no warnings 'once';
            for my $p (@path::patterns) {
                my ($re, $method, $args) = @$p;
                next unless $file =~ $re;
                my $s = view->can($method) or die "Can't locate method: $method\n";
                my ($content, $ext, $vars) = $s->(path => $file, %$args);
                $file =~ s/\.mdtext$/.$ext/;
                $data{$file} = $vars;
                last;
            }
        }
    }

    my $content = "";

    for (sort keys %data) {
        $content .= "- [$data{$_}->{headers}->{title}]($_)\n";
        for my $hdr (grep /^#/, split "\n", $data{$_}->{content}) {
            $hdr =~ /^(#+)\s+([^#]+)?\s+\1\s+\{#([^}]+)\}$/ or next;
            my $level = length $1;
            $level *= 4;
            $content .= " " x $level;
            $content .= "- [$2]($_#$3)\n";
        }
    }
    $args{content} = $content;
    return Template($template)->render(\%args), html => \%args;
}

sub breadcrumbs {
    my @path = split m!/!, shift;
    pop @path;
    my @rv;
    my $relpath = "";
    for (@path) {
        $relpath .= "$_/";
        $_ ||= "Home";
        push @rv, qq(<a href="$relpath">\u$_</a>);
    }
    return join "&nbsp;&raquo&nbsp;", @rv;
}

# arbitrary number of tables supported, but only one col per table may be sorted

sub sort_tables {
    my @orig = split /\n/, shift, -1;
    my @out;
    while (defined(local $_ = shift @orig))  {
        push @out, $_;
        /^(\|[ :vn^-]+)+\|$/ or next;
        my($data, $col, $direction, $cur, $numeric);
        $cur = 0;
        while (/\|([ :vn^-]+)/g) {
            $data = $1;
            if ($data =~ tr/v/v/) {
                $col = $cur;
                $direction = -1;
                last;
            }
            elsif ($data =~ tr/^/^/) {
                $col = $cur;
                $direction = 1;
                last;
            }
            $cur++;
        }
        unless (defined $col) {
            push @out, shift @orig while @orig and $orig[0] =~ /^\|/;
            next;
        }
        $numeric = 1 if $data =~ tr/n/n/;
        my @rows;
        push @rows, [split /\s*\|\s*/, shift(@orig), -1]
            while @orig and $orig[0] =~ /^\|/;
        shift @$_, pop @$_ for @rows; # dump empty entries at ends
        @rows = $numeric
            ? sort { $a->[$col] <=> $b->[$col] } @rows
            : sort { $a->[$col] cmp $b->[$col] } @rows;
        @rows = reverse @rows if $direction == -1;
        push @out, map "| " . join(" | ", @$_) . " |", @rows;
    }

    return join "\n", @out;
}

1;

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

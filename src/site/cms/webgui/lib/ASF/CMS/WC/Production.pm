package ASF::CMS::WC::Production;
use strict;
use warnings;

my $BASE_DIR = $ASF::CMS::BASE_DIR;
my $DOMAIN   = $ASF::CMS::DOMAIN;
my $SVN_URL  = $ASF::CMS::SVN_URL;

sub get {
    my ($self, $r) = @_;
    my $project    = $self->{project};
    my $path       = $self->{wc_path};
    my $uri        = $r->uri;
    my $subpath    = "";

    if ($project =~ /^(.+)-([^-]+)$/ and -d "$BASE_DIR/wc/$1") {
        $project = $1;
        $subpath = "$2/";
        $path = s!/content/!/content/$subpath!;
    }

    $path =~ s!^.*?/content/!! or do {
        $r = $r->main unless $r->is_initial_req;
        $r->headers_out->set(Location => "http://$project.$DOMAIN/$subpath");
        return Apache2::Const::REDIRECT;
    };


    $path =~ s!^(.+)\.page/.*!$1!; # drop attachments info

    my $file = "production/$project/content/$path";
    my @ls = ASF::CMS::run_shell_command svn => [qw/ls/], "$SVN_URL/$file";

    if ($?) {
        $file =~ s!\.vm$!!;
        $file =~ s!/$!! if $path =~ m!/$!;
        ($file, my $dir) = ASF::Util::parse_filename $file;
        $file .= "/" if $path =~ m!/$!;
        @ls = ASF::CMS::run_shell_command svn => [qw/ls/], "$SVN_URL/$dir";
        if ($?) {
            $dir =~ s!/content/[^/]+/!/content/!;
            @ls = ASF::CMS::run_shell_command svn => [qw/ls/], "$SVN_URL/$dir";
            if ($?) {
                @ls = ();
            }
        }
        chomp @ls;
        my $path_set;
        for (grep {$_ eq $file or m!^\Q$file.!} @ls) {
            $path = "$dir$_";
            ++$path_set;
            # prefer either an exact match or an html variant
            last if $_ eq $file or /\.html$/;
        }
        $path = "" unless $path_set;
        $path =~ s!^.*?/content/!!;
    }

    if ($path =~ m!^$project/!) {
        $project = "incubator";
    }
    elsif (grep m!/repos/asf/incubator/!, grep /^URL:\s+/,
           ASF::CMS::run_shell_command svn => [qw/info/],
                                           "$BASE_DIR/wc/$project/original") {
        $project .= ".incubator";
    }
    if ($project =~ m!^httpd-docs-(.*)$!) {
        $project = "httpd";
        $path = "/docs/$1/" . $path;
    }
    if ($project eq "lucene.net") {
        $project = "lucenenet";
    }

    $r = $r->main unless $r->is_initial_req;
    $r->headers_out->set(Location =>
                         URI->new("http://$project.$DOMAIN/$path"));
    return Apache2::Const::REDIRECT;
}

*head = *get;

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

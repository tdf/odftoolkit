package ASF::CMS::WC::Staged;
use strict;
use warnings;

my $BASE_DIR = $ASF::CMS::STAGE_DIR;
my $DOMAIN   = $ASF::CMS::DOMAIN;

sub get {
    my ($self, $r) = @_;
    my $project    = $self->{project};
    my $path       = $self->{wc_path};
    $path =~ s!^.*?/content/!! or do {
        $r = $r->main unless $r->is_initial_req;
        $r->headers_out->set(Location => "http://$project.staging.$DOMAIN/");
        return Apache2::Const::REDIRECT;
    };

    $path =~ s!^(.+)\.page/.*!$1!; # drop attachments info

    if ($path =~ m!/$!) {
        my $dir = "$BASE_DIR/$project/trunk/content/$path";
        unless (-d $dir) {
            $dir =~ s!/content/[^/]+/!/content/!;
        }
        $path = -d $dir ? $dir : "";
        $path =~ s!^.*?/content/!!;
    }
    else {
        my $file = "$BASE_DIR/$project/trunk/content/$path";
        unless (-e $file) {
            $file =~ s!\.vm$!!;
            my ($filename, $dirname) = ASF::Util::parse_filename $file;
            $file = $dirname . $filename;
            my ($path_set, $retry_once);
        LOOK_FOR_FILE:
            for (grep -f, glob "$file.*") {
                $path = $_;
                $path_set++;
                # prefer html variant
                last if /\.html$/;
            }
            unless ($path_set or $retry_once++) {
                $file =~ s!/content/[^/]+/!/content/!;
                goto LOOK_FOR_FILE;
            }
            $path = "" unless $path_set;
            $path =~ s!^.*?/content/!!;
        }
    }

    $r = $r->main unless $r->is_initial_req;
    $r->headers_out->set(Location =>
                         URI->new(URI::Escape::uri_escape "http://$project.staging.$DOMAIN/$path", $ASF::CMS::URIc));
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

package ASF::CMS::WC::Browse;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;

    return Apache2::Const::NOT_FOUND if $r->path_info;

    my $filename = $r->filename;

    if ($filename =~ m!/$!) {
        # is a directory, look for index.r stuff
        no warnings 'uninitialized';
        my ($index_file, @extras) = grep -f, glob $filename . "index.*";
        $index_file = ASF::CMS::negotiate_file $r, $filename . "index"
            if @extras;
        my $attachments_dir = $filename . "index.page/";
        my $attachments;
        my $file_url = $index_file;
        $file_url =~ s/^\Q$filename//;

        if (-d $attachments_dir) {
            $attachments = ASF::CMS::process_as_dir $r, $attachments_dir;
            foreach my $e (@$attachments) {
                # fixup paths to point at attachments dir
                $e->{path} = "index.page/$e->{path}";
            }
            shift @$attachments; # ignore parent dirent
        }

        my ($status) = unpack "A8", ASF::CMS::run_shell_command
                       svn => [qw/status --depth empty/], $filename;
        chomp $status;
        $status =~ tr/ /_/ if $status;

        my ($title, $is_image, $data);
        ($title, $is_image, $data) = ASF::CMS::get_file_info $r, $index_file
            if $index_file;

        $file_url = URI->new(URI::Escape::uri_escape $file_url, $ASF::CMS::URIc);
        return ASF::CMS::render $r, "wc/browse_get_dir.html",
                    attachments => $attachments,
                          title => $title,
                       file_url => "$file_url",
                       is_image => $is_image,
                   file_content => $data->{content},
                         status => $status;
    }

    # deal with 3 cases for files (ongoing) need to look for attachments
    my $fileroot = File::Basename::basename $filename;
    $fileroot =~ s/\.[^.]+$//;
    my $attachments_dir = File::Basename::dirname($filename) . "/$fileroot.page/";
    my $attachments;

    my ($status) = unpack "A8", ASF::CMS::run_shell_command
                   svn => [qw/status --depth empty/], $filename;
    chomp $status;
    $status =~ tr/ /_/ if $status;

    if (-d $attachments_dir) {
        $attachments = ASF::CMS::process_as_dir $r, $attachments_dir;
        foreach my $e (@$attachments) {
            # fixup paths to point at attachments dir
            $e->{path} = "$fileroot.page/$e->{path}";
        }
        shift @$attachments; # ignore parent dirent
    }

    my $base_url = $r->uri;

    $base_url =~ s!/browse/!/staged/!i;
    $base_url =~ s!/([^/]+)\.page/.*!/$1.html!;
    $base_url = URI->new($r->construct_url($base_url));

    my ($title, $is_image, $data) = ASF::CMS::get_file_info $r, $filename;
    return ASF::CMS::render $r, "wc/browse_get_file.html",
                        dir => 0,
                attachments => $attachments,
                      title => $title,
                   is_image => $is_image,
               file_content => $data->{content},
                     status => $status,
                       base => "$base_url";
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

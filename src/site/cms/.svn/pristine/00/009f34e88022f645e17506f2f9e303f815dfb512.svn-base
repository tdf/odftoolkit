package ASF::CMS::WC::Edit;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    my $filename   = $r->filename;
    my $apreq = APR::Request::Apache2->handle($r);

    return Apache2::Const::NOT_FOUND if $r->path_info;

    my ($status) = unpack "A8", ASF::CMS::run_shell_command
                       svn => [qw/status --depth empty/], $filename;
    chomp $status;
    $status =~ tr/ /_/ if $status;

    my ($url) = map { chomp; $_ }
                grep { s/^URL: // }
                ASF::CMS::run_shell_command
                svn => [qw/info/], $filename;

    if ($filename =~ m!/$!) {
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


        my ($title, $is_image, $data);
        ($title, $is_image, $data) = ASF::CMS::get_file_info $r, $index_file
            if $index_file;

        $file_url = URI->new(URI::Escape::uri_escape $file_url, $ASF::CMS::URIc);
        return ASF::CMS::render $r, "wc/edit_get_dir.html",
                    attachments => $attachments,
                          title => $title,
                       file_url => "$file_url",
                       is_image => $is_image,
                   file_content => $data->{content},
                         status => $status;
    }

    my $attachments_dir = $filename;
    $attachments_dir =~ s!/([^/]+)\.[^./]+$!/$1.page/!;
    my $root = $1;
    my $attachments;

    if (-d $attachments_dir) {
        $attachments = ASF::CMS::process_as_dir $r, $attachments_dir;
        foreach my $e (@$attachments) {
            # fixup paths to point at attachments dir
            $e->{path} = "$root.page/$e->{path}";
        }
        shift @$attachments; # ignore parent dirent
    }

    my $subr = $r->lookup_file($filename);
    my ($headers, $content, $title, $is_image);

    if (($subr->content_type || "text/plain") =~ /^text/) {
        ($headers, $content) = ASF::CMS::separate_content $filename;
        ($title, $is_image) = ASF::CMS::get_file_info $r, $filename;
    }
    elsif ($subr->content_type =~ /^image/) {
        $is_image = 1;
    }

    my $base_url = $r->uri;

    $base_url =~ s!/edit/!/staged/!i;
    $base_url =~ s!/([^/]+)\.page/.*!/$1.html!;
    $base_url = URI->new($r->construct_url($base_url));

    my $list = ASF::CMS::dev_list $filename;
    my $jar = $apreq->jar;
    $jar->cookie_class("ASF::CMS::Cookie");
    my ($name) = map $_->thaw, $jar->get("name");
    return ASF::CMS::render $r, "wc/edit_get_file.html",
                     status => $status,
                    headers => $headers,
                    content => $content,
                      title => $title,
                   is_image => $is_image,
                attachments => $attachments,
                        dir => 0,
                        url => "$url",
                       base => "$base_url",
                       list => $list,
                       name => $name;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $filename   = $r->filename;
    my $uri        = $r->uri;

    if ($r->path_info) {
        return ASF::CMS::conflict $r, "Resource does not exist.";
    }
    elsif ($filename =~ m!/$!) {
        return ASF::CMS::conflict $r, "Cannot actually edit a directory.";
    }

    my $apreq      = APR::Request::Apache2->handle($r);
    my $body       = $apreq->body;
    my ($content, $headers) = map {my $x=$_; $x=~s/\r//g if defined; $x}
        @$body{qw/content headers/};
    my $file = $apreq->upload("file");
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    if ($file) {
        unlink $filename
            and $file->upload_link($filename)
                or die "Couldn't exchange files: $!\n";
    }
    elsif (defined $content or defined $headers) {
        open my $fh, ">", $filename
            or die "Can't open file $filename: $!\n";
        print $fh ASF::CMS::join_content($headers, $content);
    }
    else {
        return ASF::CMS::bad_request $r, "No content was sent";
    }

    return ASF::CMS::render $r, "wc/edit_post.html", dir => 0
        unless $body->{commit} or $body->{mail};

    undef $lock; # need to release it before running commit

    if ($body->{commit}) {
        $uri =~ s!/edit/!/commit/!i
            or die "Can't run quick-commit: uri is peculiar";
    }
    elsif ($body->{mail}) {
        $uri =~ s!/edit/!/mail/!i
            or die "Can't run quick-mail: uri is peculiar";
    }
    $uri .= "?as_json=1"
        if $apreq->args("as_json");
    my $subr = $r->lookup_method_uri(POST => URI->new($uri),
                                     $r->output_filters);
    my $rc = $subr->run_map_to_storage;
    return $rc unless $rc == Apache2::Const::OK or $rc == Apache2::Const::DECLINED;
    return $subr->run;
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

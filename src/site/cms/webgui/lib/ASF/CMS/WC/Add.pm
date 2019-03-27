package ASF::CMS::WC::Add;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    my $uri        = $r->uri;
    my $path_info  = $r->path_info;
    unless ($path_info) {
        # resource exists, naive add will fail
        my $dest_uri = $uri;
        $dest_uri =~ s!/add/!/edit/!i;
        return ASF::CMS::conflict $r, "Resource Exists.", $dest_uri;
    }

    unless ($r->filename =~ m!/$! and $path_info !~ m!./.!) {
        return ASF::CMS::conflict $r, "Invalid path";
    }

    # have path_info and filename is a dir

    if ($path_info =~ m/\.page$/) {
        # points at an attachment dir
        # treat as directory and redirect
        $r = $r->main unless $r->is_initial_req;
        $r->headers_out->set(
            Location => $r->construct_url(URI->new("$uri/"))
        );
        return Apache2::Const::REDIRECT;
    }

    my $base_url = $uri;

    $base_url =~ s!/add/!/staged/!i;
    $base_url =~ s!/([^/]+)\.page/.*!/$1.html!;
    $base_url = URI->new($r->construct_url(URI::Escape::uri_escape $base_url, $ASF::CMS::URIc));

    $path_info =~ s!^/!!;

    my $list = ASF::CMS::dev_list $r->filename;

    return ASF::CMS::render $r, "wc/add_get.html",
                        dir => 0,
                  path_info => $path_info,
                       base => "$base_url",
                       list => $list;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    my $svn        = ASF::CMS::get_svn_client $r;
    my $body       = $apreq->body;
    my $path_info  = $r->path_info;

    unless ($path_info) {
        return ASF::CMS::conflict $r, "Resource already exists.";
    }

    unless ($r->filename =~ m!/$! and $path_info !~ m!./.!) {
        return ASF::CMS::conflict $r, "Invalid path";
    }

    my $new_path   = $r->filename . $path_info;
    ASF::Util::normalize_svn_path $new_path;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    if ($path_info =~ m!/$!) {
        my $file = $apreq->upload("file");

        if ($file and $file->upload_filename =~ /\.tar\.gz$/) {

            my ($temp_fh, $temp_name) = File::Temp::tempfile "cms-XXXXXX",
                DIR => $r->filename, SUFFIX => ".tar.gz";
            close $temp_fh;
            unlink $temp_name;
            $file->upload_link($temp_name);

            my $name = File::Basename::basename $new_path;
            for (ASF::CMS::run_shell_command tar => [qw/-tzf/, $temp_name]) {
                next if m!^\Q$name/! and not m!/\.\./!;
                unlink $temp_name;
                die "tar failed: $?\n$_" if $?;
                return ASF::CMS::bad_request $r,
                    "tarball had improper path in it: $_";
            }

            my $dirlock = ASF::CMS::get_lock "$ASF::CMS::BASE_DIR/locks/cwd-$$";
            my ($dir) = map { /(.*)/ && $1 } Cwd::getcwd;
            chdir $r->filename;
            my $tar = ASF::CMS::run_shell_command tar => [qw/-xzf/, $temp_name];
            chdir $dir;
            undef $dirlock;
            unlink $temp_name;
            if ($?) {
                die "tar failed: $?\n$tar";
            }
            if (-l $new_path) {
                unlink $new_path;
                return ASF::CMS::forbidden $r,
                    "Symlinks are not supported, sorry";
            }
            elsif (-d _) {
                $svn->add($new_path, 1);
            }
            else {
                die "Extraction path did not meet server's expectations, sorry";
            }
        }
        elsif ($file) {
            return ASF::CMS::bad_request $r,
                "File doesn't appear to be a tarball"
                    . " (name doesn't end in .tar.gz)";
        }
        else {
            $svn->mkdir($new_path);
        }
    }
    else {
        my ($content, $headers) = map {my $x=$_; $x=~s/\r//g if defined; $x}
            @$body{qw/content headers/};
        my $file = $apreq->upload("file");

        if ($file) {
            $file->upload_link($new_path)
                or die "link to $new_path failed: $!\n";
        }
        elsif (defined $content or defined $headers) {
            open my $fh, ">", $new_path
                or die "Can't open file $new_path: $!\n";

            print $fh ASF::CMS::join_content($headers, $content);
        } else {
            return ASF::CMS::bad_request $r, "No content was sent";
        }

        $svn->add($new_path, 0);
        my $subr = $r->lookup_file($new_path);
        no warnings 'uninitialized';
        if ($subr->content_type =~ /(^text|xml$)/) {
            $svn->propset("svn:eol-style", "native", $new_path, 0);
        }
    }

    return ASF::CMS::render $r, "wc/add_post.html",
                        dir => 0,
                http_status => Apache2::Const::HTTP_CREATED,
        unless $body->{commit} or $body->{mail};

    undef $lock;
    my $uri = $r->uri;

    if ($body->{commit}) {
        $uri =~ s!/add/!/commit/!i
            or die "Can't run quick-commit: uri is peculiar";
    }
    elsif ($body->{mail}) {
        $uri =~ s!/add/!/mail/!i
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

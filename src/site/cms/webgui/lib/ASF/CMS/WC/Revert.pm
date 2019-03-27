package ASF::CMS::WC::Revert;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    my $filename   = $r->filename;

    return Apache2::Const::NOT_FOUND if $r->path_info;

    my $status     = ASF::CMS::run_shell_command svn => [qw/status/], $filename;
    $status =~ s/$filename//g;

    return ASF::CMS::render $r, "wc/revert_get.html",
                    project => $self->{project},
                     status => $status,
                        dir => 0;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);

    return Apache2::Const::NOT_FOUND if $r->path_info;

    unless ($apreq->body("submit")) {
        return ASF::CMS::bad_request $r, "Invalid POST request";
    }

    my $svn        = ASF::CMS::get_svn_client $r;

    my $filename   = $r->filename;
    ASF::Util::normalize_svn_path $filename;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    my $status     = ASF::CMS::run_shell_command
                 svn => [qw/status --depth empty/], $filename;
    chomp $status;
    $svn->revert($filename, 1);

    if ($status =~ /^A/) {
        if ($r->filename =~ m!/$!) {
            File::Path::rmtree $filename;
        }
        else {
            unlink $filename;
        }
    }

    return ASF::CMS::render $r, "wc/revert_post.html", dir => 0;
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

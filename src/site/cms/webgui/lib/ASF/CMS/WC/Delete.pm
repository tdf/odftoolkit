package ASF::CMS::WC::Delete;
use strict;
use warnings;

#
# This package is like Move.pm in that it deletes stuff directly
# from the repo instead of just the working copy.  First it
# deletes from the staging tree, then the original working copy
# (and then commits the change).

my $BASE_DIR = $ASF::CMS::STAGE_DIR;

sub get {
    my ($self, $r) = @_;
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $status     = ASF::CMS::run_shell_command svn => [qw/status/], $filename;
    $status =~ s/$filename//g;
    return ASF::CMS::render $r, "wc/delete_get.html", status => $status, dir => 0;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    my $project    = $self->{project};

    return Apache2::Const::NOT_FOUND if $r->path_info;

    unless ($apreq->body("submit")) {
        return ASF::CMS::bad_request $r, "Invalid POST request";
    }

    my $svn = ASF::CMS::get_svn_client $r;

    my $filename = $r->filename;

    ASF::Util::normalize_svn_path $filename;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    $svn->delete($filename, 1);

    return ASF::CMS::render $r, "wc/delete_post.html",
                    project => $project,
                        dir => 0,
                     is_dir => scalar $r->filename =~ m!/$!,
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

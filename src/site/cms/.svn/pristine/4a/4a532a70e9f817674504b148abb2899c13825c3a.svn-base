package ASF::CMS::WC::Update;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    return Apache2::Const::NOT_FOUND if $r->path_info;
    return ASF::CMS::render $r, "wc/update_get.html", dir => 0;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $svn        = ASF::CMS::get_svn_client $r;

    ASF::Util::normalize_svn_path $filename;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);
    my $dir = $r->filename;
    $dir = File::Basename::dirname $dir unless $dir =~ s!/$!!;
    ASF::Util::normalize_svn_path $dir;
    my ($wc_root) = $dir =~ m!^(/.*?/wc/\Q$self->{project}\E/[^/]+)!;
    $svn->cleanup($wc_root);

    my $update     = ASF::CMS::run_shell_command svn => [qw/update/], $filename;
    $update        =~ s/$filename//g;
    return ASF::CMS::render $r, "wc/update_post.html",
                     update => $update,
                        dir => 0;
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

package ASF::CMS::WC::Resolve;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    my $filename   = $r->filename;

    return Apache2::Const::NOT_FOUND if $r->path_info;

    my ($status)   = unpack "A8", ASF::CMS::run_shell_command
        svn => [qw/status --depth empty/], $filename;
    $status =~ tr/ /_/ if $status;

    return ASF::CMS::render $r, "wc/resolve_get.html",
                    project => $self->{project},
                     status => $status,
                   conflict => scalar $status =~ /C/,
                        dir => 0;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);

    return Apache2::Const::NOT_FOUND if $r->path_info;

    my $body       = $apreq->body;
    my $accept     = $body->{accept};

    $accept =~ /^([\w-]+)$/
        or return ASF::CMS::bad_request $r, "Invalid accept data: $accept";
    $accept = $1;

    my $filename   = $r->filename;
    ASF::Util::normalize_svn_path $filename;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    my $resolution = ASF::CMS::run_shell_command
                     svn => [qw/resolve --accept/, $accept], $filename;
    chomp $resolution;
    return ASF::CMS::render $r, "wc/resolve_post.html",
                        dir => 0,
                 resolution => $resolution;
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

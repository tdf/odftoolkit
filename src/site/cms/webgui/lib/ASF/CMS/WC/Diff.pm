package ASF::CMS::WC::Diff;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $diff       = ASF::CMS::run_shell_command svn => [qw/diff/], $filename;

    if ($apreq->args("as_download")) {
        $r->content_type('text/x-patch; charset="utf-8"');
        $r->main->content_type($r->content_type)
            unless $r->is_initial_req;
        ASF::CMS::fixup_code $filename, undef, $diff;
        $r->print($diff);
        return Apache2::Const::OK;
    }

    ASF::CMS::fixup_code $filename, !ASF::CMS::client_wants_json($r) && "diff", $diff;

    return ASF::CMS::render $r, "wc/diff_get.html", diff => $diff, dir => 0;
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

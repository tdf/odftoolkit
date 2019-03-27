package ASF::CMS::WC::Copy;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $status     = ASF::CMS::run_shell_command svn => [qw/status/], $filename;
    $status =~ s/$filename//g;
    return ASF::CMS::render $r, "wc/copy_get.html", status => $status, dir => 0;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    my $project    = $self->{project};

    return Apache2::Const::NOT_FOUND if $r->path_info;

    my $body       = $apreq->body;
    my $target     = $body->{target};

    if ($target =~ m!^/!) {
        return ASF::CMS::bad_request $r,
            "Invalid target: $target (must be a relative path)";
    }

    my $filename   = $r->filename;

    my $dir        = $filename;
    $dir           = File::Basename::dirname $dir unless $dir =~ m!/$!;
    $dir =~ s!/+$!!;

    my $target_path = "$dir/$target";
    1 while $target_path =~ s!/[^/]+/\.\./!/!;
    $target_path =~ /(.*)/;
    $target_path = $1;

    my ($wc_dir, $wc_path) = $self->{wc_path} =~ m!/([^/]+)/(.*)!;

    ASF::Util::normalize_svn_path $filename, $target_path, $wc_path;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    my $svn = ASF::CMS::get_svn_client $r;

    $svn->copy($filename, undef, $target_path);

    $target = URI->new(URI::Escape::uri_escape $target, $ASF::CMS::URIc);

    return ASF::CMS::render $r, "wc/copy_post.html",
                     target => "$target",
                    project => $self->{project},
                        dir => 0,
                     is_dir => scalar $r->filename =~ m!/$!
                         unless $body->{commit};

    $target_path .= "/" if -d $target_path;
    $target_path =~ s!^\Q$ASF::CMS::BASE_DIR/wc/$self->{project}/!!
        or die "Unrecognized target_path\n";

    my $uri = "/$self->{project}/wc/commit/$target_path";
    $uri .= "?as_json=1"
        if $apreq->args("as_json");
    undef $lock;

    my $subr = $r->lookup_method_uri(POST => URI->new(URI::Escape::uri_escape $uri, $ASF::CMS::URIc),
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

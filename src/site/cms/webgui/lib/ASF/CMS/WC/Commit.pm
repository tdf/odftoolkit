package ASF::CMS::WC::Commit;
use strict;
use warnings;

sub get {
    my ($self, $r) = @_;
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $status     = ASF::CMS::run_shell_command svn => [qw/status/], $filename;
    $status =~ s/$filename//g;

    return ASF::CMS::render $r, "wc/commit_get.html",
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

    die "Commits administratively disabled\n" if -f $ASF::CMS::NO_COMMIT;

    my $msg        = $apreq->body("message");
    if ($msg) {
        $msg =~ s/\r//g;
        # need to untaint $msg because svn's swig hates it
        $msg =~ /(.*)/s;
        $msg = $1;
    }
    else {
	$msg = "CMS commit to $self->{project} by " . $r->user;
    }

    my ($wc_dir)   = $self->{wc_path} =~ m!/([^/]+)!;

    my $svn        = ASF::CMS::get_svn_client $r;

    $svn->log_msg(sub { ${$_[0]} = $msg });

    my $filename = $r->filename;
    ASF::Util::normalize_svn_path $filename;

    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    my $rv = eval { $svn->commit($filename, 0) };
    my $ran_update;
    unless ($rv) {
        my $dir = $r->filename;
        $dir = File::Basename::dirname $dir unless $dir =~ s!/$!!;
        ASF::Util::normalize_svn_path $dir;
        my ($wc_root) = $dir =~ m!^(/.*?/wc/\Q$self->{project}\E/[^/]+)!;
        $svn->cleanup($wc_root);
        $svn->update($filename, "HEAD", 1);
        ++$ran_update;
        $rv = eval { $svn->commit($filename, 0) };
        unless ($rv) {
            my $uri = $r->uri;
            $uri =~ s/commit/resolve/i;
            $@ =~ /conflict/i     and return ASF::CMS::conflict  $r, $@, $uri;
            $@ =~ /403 Forbidden/ and return ASF::CMS::forbidden $r, $@;
            die $@;
        }
    }

    ASF::Util::touch "$ASF::CMS::BASE_DIR/wc/$self->{project}/$wc_dir";

    my $new_cookie = ASF::CMS::Cookie->new($r,
              name => "$self->{project}/wc",
             value => { path => $wc_dir },
    );

    $new_cookie->bake($r);

    my $base = "";
    if ($r->filename !~ /\Q$self->{wc_path}\E$/) {
        $base = $r->filename;
        $base =~ s!^.*?/wc/($self->{project})/!/$1/wc/commit/!;
        $base = URI->new($r->construct_url(URI::Escape::uri_escape $base, $ASF::CMS::URIc));
    }

    return ASF::CMS::render $r, "wc/commit_post.html",
                    project => $self->{project},
                   revision => $rv->revision,
                       base => "$base",
                        dir => 0,
                     update => $ran_update,
                http_status => Apache2::Const::HTTP_ACCEPTED;
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

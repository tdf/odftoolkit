package ASF::CMS::WC::Move;
use strict;
use warnings;
# This is by far the most complex package in the system.
# I considered not implementing it at all, but figured the
# challenge would be worthwhile.
#
# There are several steps involved.
# First note that move is really a copy + delete operation.
# We will need to determine the target directory for the move,
# and note the oddness of having to move a directory from "within" it.
#

my $BASE_DIR = $ASF::CMS::STAGE_DIR;

sub get {
    my ($self, $r) = @_;
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $status     = ASF::CMS::run_shell_command svn => [qw/status/], $filename;
    $status =~ s/$filename//g;
    return ASF::CMS::render $r, "wc/move_get.html", status => $status, dir => 0;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    my $project    = $self->{project};

    return Apache2::Const::NOT_FOUND if $r->path_info;

    die "Commits administratively disabled\n" if -f $ASF::CMS::NO_COMMIT;

    my $body       = $apreq->body;
    my ($target, $message) = @$body{qw/target message/};

    if ($target =~ m!^/!) {
        return ASF::CMS::bad_request $r,
            "Invalid target: $target (must be a relative path)";
    }

    if ($message) {
        $message =~ s/\r//g;
    }
    else {
        $message = "CMS move to $target by " . $r->user;
    }
    # need to untaint $message because svn's swig hates it
    $message =~ /(.*)/s;
    $message = $1;

    my $filename = $r->filename;

    my $dir = $filename;
    $dir = File::Basename::dirname $dir unless $dir =~ m!/$!;
    $dir =~ s!/+$!!;

    my $target_path = "$dir/$target";
    1 while $target_path =~ s!/[^/]+/\.\./!/!;
    $target_path =~ /(.*)/;
    $target_path = $1;

    my ($wc_dir, $wc_path) = $self->{wc_path} =~ m!/([^/]+)/(.*)!;

    ASF::Util::normalize_svn_path $filename, $target_path, $wc_path, $dir;
    my $lock = ASF::Util::get_lock("$ASF::CMS::BASE_DIR/locks/$self->{project}-wc-" . $r->user);

    my $svn = ASF::CMS::get_svn_client $r;

    $svn->log_msg(sub { ${$_[0]} = $message });

    $svn->update($dir, "HEAD", 1);
    $svn->move($filename, undef, $target_path, 1);

    my $rv = eval { $svn->commit([$filename, $target_path], 0) };
    unless ($rv) {
        $@ =~ /conflict/i     and return ASF::CMS::conflict  $r, $@;
        $@ =~ /403 Forbidden/ and return ASF::CMS::forbidden $r, $@;
        die $@;
    }

    ASF::Util::touch "$ASF::CMS::BASE_DIR/wc/$project/$wc_dir";

    my $new_cookie = ASF::CMS::Cookie->new($r,
              name => "$self->{project}/wc",
             value => { path => $wc_dir },
    );
    $new_cookie->bake($r);

    $target = URI->new(URI::Escape::uri_escape $target, $ASF::CMS::URIc);

    return ASF::CMS::render $r, "wc/move_post.html",
                     target => "$target",
                    project => $self->{project},
                   revision => $rv->revision,
                        dir => 0,
                     is_dir => scalar $r->filename =~ m!/$!,
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

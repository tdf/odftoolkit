package ASF::CMS::MapToStorage;
use APR::Finfo;
use APR::Const -compile => qw/FINFO_NORM/;
use Apache2::URI;
use Apache2::Const -compile => qw/M_TRACE :common/;
use Apache2::RequestRec;
use ASF::CMS;
use strict;
use warnings;

our $VERSION = "1.3";

sub handler {
    my $r   = shift;
    my $uri = $r->uri;

    Apache2::URI::unescape_url($uri);

    return Apache2::Const::DECLINED
        if $r->method_number == Apache2::Const::M_TRACE;

    if ($uri =~ m!^/($ASF::CMS::PROJ_PAT)/wc/\w+/(.+)$! and $uri !~ m!/\.svn/!) {
        no warnings 'uninitialized';

        my @path = ("$ASF::CMS::BASE_DIR/wc", $1, split "/", $2, -1);
        my @path_info;

        unshift @path_info, pop @path until -e join "/", @path;
        push    @path,      ""        if -d _ and $path[-1] ne "";
        unshift @path_info, ""        if @path_info;

        $r->path_info(join "/", @path_info);
        $r->filename (join "/", @path);
        $r->finfo(APR::Finfo::stat $r->filename, APR::Const::FINFO_NORM, $r->pool)
            unless @path_info;

        while (@path) {
            return Apache2::Const::FORBIDDEN if -l join "/", @path;
            pop @path;
        }
        return Apache2::Const::OK;
    }

    return Apache2::Const::DECLINED;
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

=head1 NOTES

Rules for wc paths:

=over 4

=item 1.

r->filename will always exist, and be the longest such path represented by
the uri.

=item 2.

r->filename will end with a "/" if and only if it is a directory.

=item 3.

r->path_info will always represent the remainder of the uri.

=item 4.

If r->path_info is not "" it will always be prefixed with "/".

=item 5.

If this handler is successful, it eliminates dirwalk and
filewalk on the request, so directives in Directory and Files
containers won't work unless you are running a lookup_file
subrequest (in which case this handler will return DECLINED
and the default MapToStorage handler will run dirwalk and filewalk).

=item 6.

No part of the path to r->filename may be a symlink.

=back

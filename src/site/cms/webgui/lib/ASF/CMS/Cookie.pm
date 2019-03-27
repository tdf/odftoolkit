package ASF::CMS::Cookie;
use strict;
use warnings;
use base 'APR::Request::Cookie';
use Digest::MD5;
use FreezeThaw ();
use Apache2::RequestRec;
use Apache2::RequestUtil;
use APR::Table;
use APR::Request qw/encode decode/;

our $VERSION = "1.1";

sub data_is_valid;
sub gen_hash;

sub bake {
    my ($c, $r) = @_;
    $r = $r->main unless $r->is_initial_req;
    $r->err_headers_out->add("Set-Cookie", $c->as_string);
}

sub new {
    my $class    = shift;
    my $r        = shift;
    my %defaults = (
                  path => "/",
               expires => ($r->dir_config("CookieExpires") || 7) . "d",
                secure => 1,
              httponly => 1,
    );
    $r = $r->main unless $r->is_initial_req;
    return $class->APR::Request::Cookie::new($r->pool, %defaults, @_);
}

sub freeze {
    my ($class, $value) = @_;
    my $r               = Apache2::RequestUtil->request;
    my $expiration_days = $r->dir_config("CookieExpires") || 7;
    my %h               = (
        data    => FreezeThaw::freeze($value),
        user    => $r->user,
        time    => $r->request_time,
        expires => $r->request_time + 86400 * $expiration_days,
    );
    $h{hash} = gen_hash \%h, $r;
    return join "&", map {encode($_) . "=" . encode($h{$_})} sort keys %h;
}

sub thaw {
    my ($cookie, $value) = @_;
    $value               = $cookie->value if @_ == 1;
    my %h                = map decode($_), split /[&=]/, $value;
    my $r                = Apache2::RequestUtil->request;
    unless (data_is_valid \%h, $r) {
        if ($value eq $cookie->value) {
            my $new_cookie = ASF::CMS::Cookie->new($r,
                      name => $cookie->name,
                     value => "1",
                   expires => "now",
            );
            $new_cookie->bake($r);
        }
        return;
    }
    return FreezeThaw::thaw $h{data};
}

sub data_is_valid {
    my ($data, $r) = @_;
    my $hash = gen_hash $data, $r;
    return unless           $hash eq $data->{hash};
    return unless        $r->user eq $data->{user};
    return unless $r->request_time < $data->{expires};
    return 1;
}

sub gen_hash {
    my ($data, $r) = @_;
    my $secret     = $r->dir_config("CookieSecret")
        or die "Missing PerlSetVar CookieSecret in httpd config!\n";
    my $hash       = Digest::MD5->new->add(
        join ":", $secret, @$data{qw/time expires user data/}
    )->hexdigest;
    return Digest::MD5->new->add(join ":", $secret, $hash)->hexdigest;
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

This package expects two C<PerlSetVar> entries in the httpd config: one for
C<CookieExpires> and one for C<CookieSecret>.  The CookieExpires setting
should be the same as that of calls to C<new()> which set the C<expires>
cookie attribute (just to keep things matching up well- if apreq exposed the
cookie's max_age attribute to perl we wouldn't need this contortion).
CookieSecret should be kept private to the installation as it is the means
by which the cryptographic signature in each cookie is generated (and
validated).  Since the cookies are tied into C<< $r->user >>, even if a valid
cookie is stolen by another it is worthless to the thief (unless the cookie
itself contains sensitive information- this package does not generate
encrypted cookies).

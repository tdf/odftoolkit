package ASF::CMS::WC::Mail;
use strict;
use warnings;

my $LDAP_SERVER = $ASF::CMS::LDAP_SERVER;
my $LDAP_CAFILE = $ASF::CMS::LDAP_CAFILE;
my $LDAP_BASE   = $ASF::CMS::LDAP_BASE;

sub get {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    return Apache2::Const::NOT_FOUND if $r->path_info;
    my $filename   = $r->filename;
    my $diff       = ASF::CMS::run_shell_command svn => [qw/diff/], $filename;

    ASF::CMS::fixup_code $filename, !ASF::CMS::client_wants_json($r) && "diff", $diff;
    my $list = ASF::CMS::dev_list $r->filename;
    my $jar = $apreq->jar;
    $jar->cookie_class("ASF::CMS::Cookie");
    my ($name) = map $_->thaw, $jar->get("name");

    return ASF::CMS::render $r, "wc/mail_get.html",
                       diff => $diff,
                        dir => 0,
                       list => $list,
                       name => $name;
}

*head = *get;

sub post {
    my ($self, $r) = @_;
    my $apreq      = APR::Request::Apache2->handle($r);
    my $user       = $r->user;
    my $body       = $apreq->body;

    my ($to, $subject, $content) = @$body{qw/to subject mailmsg/};
    s/\r//g for $to, $subject, $content;
    s/\n//g for $to, $subject;
    $to =~ /^[\w.-]+\@[\w.-]*\bapache\.org$/
        or return ASF::CMS::bad_request $r, "Improper 'To' address";

    my $cn;
    if ($user eq "anonymous") {
        $cn = "Anonymous CMS User";
        if ($content and not $r->is_initial_req) {
            $content =~ tr/\n//d;
            $cn = $content;
            if ($cn =~ s/([^^A-Za-z0-9\-_.,!~*' ])/sprintf "=%02X", ord $1/ge) {
                $cn =~ tr/ /_/;
                $cn = "=?utf-8?Q?$cn?=";
            }
            else {
                $cn = qq("$cn");
            }
            my $cookie = ASF::CMS::Cookie->new($r,
                         name => "name",
                        value => $content,
                      expires => "1y",
                         path => "/",
            );
            $cookie->bake($r);
        }
    }
    else {
        my $lock = ASF::Util::get_lock "$ASF::CMS::BASE_DIR/locks/ssleay-$$";
        my $ldap       = Net::LDAP->new(
            "ldaps://$LDAP_SERVER",
            onerror => 'die',
#            verify  => 'require', # XXX !!! fscking perl idiocy that this stopped working
            cafile  => $LDAP_CAFILE,
        ) or die "Can't connect to ldaps://$LDAP_SERVER\n";

        $ldap->bind;

        my $response = $ldap->search(
            base  => "uid=$user,$LDAP_BASE",
            attrs => ['cn'],
            scope => 'base',
            filter=> 'cn=*',
        );

        for my $e ($response->entries) {
            $cn = $e->get_value('cn');
        }

        $ldap->unbind;

        if ($cn =~ s/([^^A-Za-z0-9\-_.,!~*' ])/sprintf "=%02X", ord $1/ge) {
            $cn =~ tr/ /_/;
            $cn = "=?utf-8?Q?$cn?=";
        }
        else {
            $cn = qq("$cn");
        }
    }

    my $diff = ASF::CMS::run_shell_command svn => [qw/diff/], $r->filename;
    ASF::CMS::fixup_code $r->filename, undef, $diff;
    my $date = gmtime;

    my $path = $self->{wc_path};
    $path =~ s!.*trunk/content/!! or $path = "";
    $path = APR::Request::encode $path;
    my $clone_url = <<EOT;
Clone URL (Committers only):
https://cms.$ASF::CMS::DOMAIN/redirect?new=$user;action=diff;uri=http://$self->{project}.apache.org/$path
EOT
    my ($wc_dir)   = $self->{wc_path} =~ m!/([^/]+)!;
    ASF::Util::touch "$ASF::CMS::BASE_DIR/wc/$self->{project}/$wc_dir";

    my $new_cookie = ASF::CMS::Cookie->new($r,
              name => "$self->{project}/wc",
             value => { path => $wc_dir },
    );

    $new_cookie->bake($r);

    local %ENV;
    open my $sendmail, "|-", "/usr/sbin/sendmail -oi -t"
        or die "Can't open sendmail: $!";
    print $sendmail <<EOT;
To: <$to>
From: $cn <$user\@$ASF::CMS::DOMAIN>
Subject: $subject
Date: $date +0000
Content-Type: text/plain; charset="utf-8"

$clone_url
$content

$diff
EOT
    close $sendmail or die "Sendmail failed: " . ($! || $? >> 8) . "\n";

    return ASF::CMS::render $r, "wc/mail_post.html", dir => 0;
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

#!/usr/bin/perl

# simple publication client
#
# Usage:
# publish.pl $project [$user]  -- will prompt for log message
# publish.pl $project [$user] < /path/to/log_message -- takes it from stdin
# publish.pl            -- will prompt for project, user as well
#
# Note: the point of doing the GET first is to acquire the publication
#       key.  The key is provided both as a Cookie named $project/publish,
#       and as a hidden form input named "key".  As we use json now we can
#       just pull the key (and the diff info) out of the GET response.
#
# Prereqs: just LWP, IO::Socket::SSL, Term::ReadKey, and JSON::XS.
#       These modules are available from all the usual places including CPAN.
#
# Also: If you don't want to bother with the prereqs, this script is
#       installed on people.apache.org at /usr/local/bin/publish.pl:
#
#       % ssh -t $user@people.apache.org publish.pl $project $user
#
# Be sure the executing shell exports an EDITOR env variable for best results.

use IO::Socket::SSL;
use Net::SSLeay;
BEGIN {
    my ($ca_file, $ca_path) = qw(/etc/ssl/cert.pem /etc/ssl/certs);
    # enable ca cert validation
    IO::Socket::SSL::set_ctx_defaults
        verify_mode => Net::SSLeay->VERIFY_PEER(),
        (-f $ca_file ? (ca_file => $ca_file) : ()),
        (-d $ca_path ? (ca_path => $ca_path) : ())
            unless $ENV{IGNORE_CERT};
}

use strict;
use warnings;
use feature 'say';
use LWP::UserAgent;
use Term::ReadKey;
use POSIX 'ctermid';
use File::Temp 'tempfile';
use JSON::XS ();

sub getproj;
sub getuser;
sub getpw;
sub getdiff;
sub confirm;
sub askretry;
sub pollbuild;
sub checkcompat;

our $VERSION = "1.10";
my $REALM    = "ASF Committers";
my $HOST     = "cms.apache.org";
my $BUILDHOST= "ci.apache.org";

my $project  = shift // getproj;
my $uri      = "https://$HOST/$project/publish";
my $ua       = LWP::UserAgent->new;
my $json     = JSON::XS->new->utf8;
my $tries    = 0;
my $message  = "";

$ua->default_header(Accept => "application/json");

credentials:

$ua->credentials("$HOST:443", $REALM, getuser, getpw);

try:

say "Client API Version: $VERSION";
getdiff;

while (pollbuild) {
    my $response = $ua->get("http://$BUILDHOST/builders/$project-site-staging");
    unless ($response->is_success) {
        warn "Can't poll $BUILDHOST: " . $response->status_line . "\n";
        next;
    }
    my $html = $response->decoded_content;
    if ($html =~ /No current builds/) {
        $html =~ /Recent Builds:.*?<td class="(\w+)">/s;
        my $result = $1 // "unknown";
        say "Build complete: status=$result.";
        confirm unless $result eq "success";
        last;
    }
    $html =~ / (ETA .*)/;
    my $eta = $1 // "ETA unknown";
    say "Still building...($eta)";
}

my $response = $ua->get($uri);
my $status   = $response->status_line;

unless ($response->is_success) {
    my $msg  = "Bad GET response: $status";
    die "$msg\n" if ++$tries == 3 or $status ne "401 Authorization Required";
    warn "$msg, retrying...\n";
    goto credentials;
}

checkcompat $ua, $json;

my $get_data = $json->decode($response->decoded_content);

if ($ENV{EDITOR}) {

    my ($diff_fh, $diff_filename) = tempfile "cms-XXXXXX", SUFFIX => ".diff", TMPDIR => 1;
    my $source_url = $get_data->{source_url};
    my $from_revision = 1 + `svn propget --strict cms:source-revision https://svn.apache.org/repos/infra/websites/staging/$project/trunk/content\@$get_data->{key}`;
    my $to_revision = `svn propget --strict cms:source-revision https://svn.apache.org/repos/infra/websites/production/$project/content\@$get_data->{key}`;
    print $diff_fh `svn log -q $source_url\@$to_revision -r $from_revision:$to_revision` if $from_revision <= $to_revision;
    print $diff_fh "\n\n";
    print $diff_fh $get_data->{diff} || "";
    close $diff_fh;

    my ($log_fh, $log_filename)   = tempfile "cms-XXXXXX", SUFFIX => ".txt", TMPDIR => 1;
    print $log_fh "$message\n# Log message (comment lines ignored)\n";
    close $log_fh;

    system $ENV{EDITOR} => $diff_filename, $log_filename;
    if ($?) {
        unlink $diff_filename, $log_filename;
        goto noeditor;
    }
    open $log_fh, "<", $log_filename or die "Can't reopen $log_filename: $!\n";
    $message = join "", grep !/^#/, <$log_fh>;
    close $log_fh;

    unlink $diff_filename, $log_filename;
}
else {

 noeditor:

    my ($diff_fh, $diff_filename) = tempfile "cms-XXXXXX", TMPDIR => 1;
    print $diff_fh $get_data->{diff} || "";
    close $diff_fh;
    system less => $diff_filename;
    unlink $diff_filename;

    unless ($message) {
        say "Log message (^D to end message):" if -t STDIN;
        $message = join "", <STDIN>;
    }
}
chomp $message;

confirm;

my $form_data = [
    message => $message,
    key     => $get_data->{key},
    submit  => "Submit",
];

say "Awaiting server response to publication request...";
$response    = $ua->post($uri, $form_data);
say $response->status_line;

my $post_data = $json->decode($response->decoded_content);

if ($response->is_success) {
    say "Committed revision $post_data->{revision}.";
    exit 0;
}
else {
    say "Error message:\n$post_data->{error}";
    askretry;
    goto try;
}

sub runterm (&) {
    my $tty  = ctermid or die "Can't locate controlling terminal: $!\n";
    open my $term, "+<", $tty or die "Can't open $tty: $!\n";
    # block these to avoid leaving $tty in a non-echo state
    local $SIG{INT} = local $SIG{QUIT} = local $SIG{TSTP} = "IGNORE";
    return shift->($term);
}

sub getproj {

    runterm {
        my $term = shift;
        print $term "Project Name (^D aborts): ";
        no warnings 'uninitialized';
        chomp(my $proj = ReadLine 0, $term);

        $! = 1, die "Operation cancelled.\n" unless defined $proj;
        return lc $proj;
    }
}

sub getdiff {

    runterm {
        my $term = shift;
        ReadMode raw => $term;
        my $answer;
        do {
            print $term "View unified diff of changes (y/n)? ";
            $answer = lc ReadKey 0, $term;
            no warnings 'uninitialized';
            print $term "$answer\n";
        } while (defined $answer and $answer !~ /y|n/);
        ReadMode restore => $term;

        $uri .= "?diff=1"     if $answer eq "y" and $uri !~ /diff=1$/;
        $uri =~ s/\?diff=1$// if $answer eq "n" and $uri =~ /diff=1$/;
    }
}

my ($_getuser_cache);
sub getuser {
    $_getuser_cache
    //= shift
    // $ENV{AVAILID}
    // ($< && `hostname` =~ /\.apache\.org$/ ? getpwuid($<) : undef )
    // runterm {
        my $term = shift;
        print $term "LDAP User (^D aborts): ";
        no warnings 'uninitialized';
        chomp(my $user = ReadLine 0, $term);

        $! = 1, die "Operation cancelled.\n" unless defined $user;
        return $user;
    }
}

sub getpw {

    runterm {
        my $term = shift;
        ReadMode noecho => $term;
        printf $term "LDAP Password for '%s' (^D aborts): ", getuser;
        no warnings 'uninitialized';
        chomp(my $passwd = ReadLine 0, $term);
        print $term "\n";
        ReadMode restore => $term;

        $! = 1, die "Operation cancelled.\n" unless defined $passwd;
        return $passwd;
    }
}

sub confirm {

    runterm {
        my $term = shift;
        ReadMode raw => $term;
        my $answer;
        do {
            print $term "OK to continue (y/n)? ";
            $answer = lc ReadKey 0, $term;
            no warnings 'uninitialized';
            print $term "$answer\n";
        } while (defined $answer and $answer !~ /y|n/);
        ReadMode restore => $term;

        $! = 1, die "Operation cancelled.\n"
            unless defined $answer and $answer eq "y";
    }
}

sub askretry {

    runterm {
        my $term = shift;
        ReadMode raw => $term;
        my $answer;
        do {
            print $term "OK to retry (y/n)? ";
            $answer = lc ReadKey 0, $term;
            no warnings 'uninitialized';
            print $term "$answer\n";
        } while (defined $answer and $answer !~ /y|n/);
        ReadMode restore => $term;

        $! = 1, die "Operation cancelled.\n"
            unless defined $answer and $answer eq "y";
    }
}

sub pollbuild {

    runterm {
        my $term = shift;
        ReadMode raw => $term;
        my $answer;
        do {
            print $term "OK to poll build (y/n)? ";
            $answer = lc ReadKey 0, $term;
            no warnings 'uninitialized';
            print $term "$answer\n";
        } while (defined $answer and $answer !~ /y|n/);
        ReadMode restore => $term;

        $! = 1, die "Operation cancelled.\n"
            unless defined $answer;

        return $answer eq "y";
    }
}

sub checkcompat {
    my ($ua, $json) = @_;
    my $uri         = "https://$HOST/compat?version=$VERSION";
    my $response    = $ua->get($uri);
    return if $response->is_success;
    my $compat_data = $json->decode($response->decoded_content);
    die "Server API Version ($compat_data->{version}) is incompatible"
        . " with this client!\n";
}

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

package ASF::Value::Twitter;
use Net::Twitter;
use strict;
use warnings;

sub get_credentials {
    open my $fh, "<", "/usr/local/cms/auth/$_[0].pwd"
        or die "Can't open $_[0].pwd: $!";
    chomp(my $data = <$fh>);
    return split /:/, $data;
}

sub new {
    my $class = shift;
    my %args = @_;

    bless {
        args => \%args,
        limit => $args{limit},
    }, $class;
}

sub init {
    my $self = shift;
    return if $self->{initialized};

    my ($app_id, $app_secret) = get_credentials("twitter-app");
    my ($user, $pw) = get_credentials("twitter");
    my $nt = Net::Twitter->new(
        traits          => [qw/API::REST API::Search OAuth/],
        consumer_key    => $app_id,
        consumer_secret => $app_secret,
        apiurl          => 'https://api.twitter.com/1.1',
        ssl             => 1,
    );
    $nt->access_token($user);
    $nt->access_token_secret($pw);
    $self->{nt} = $nt;

    $self->{initialized} = 1;
}

sub list {
    return if $ASF::Value::Offline;
    my $self = shift;
    $self->init;
    my $nt = $self->{nt};
    die "Twitter Authz failed!" unless $nt->authorized;
    my $json;
    if ($self->{args}->{name}) {
        $json = $nt->user_timeline({screen_name => $self->{args}->{name}});
    }
    else {
        $json = $nt->search($self->{args}->{search})->{results};
    }

    my @rv;
    foreach my $s ((@$json)[0..$self->{limit}-1]) {
        my $url = "https://twitter.com/$s->{user}->{screen_name}/statuses/$s->{id}";
        my $text = $s->{text};
        utf8::encode($text);
        $text = "$s->{user}->{screen_name}: $text";
        $text =~ s!\b(https?://\S+)!<a href="$1">$1</a>!g;
        $text =~ s!\@(\w+)\b!<a href="http://twitter.com/$1">\@$1</a>!g;
        $text =~ s!\#(\S+)!<a href="http://twitter.com/search?q=%23$1">#$1</a>!g;
        $text =~ s!^(\S+):!<a href="http://twitter.com/$1">$1</a>:!;
        my $timestamp = $s->{created_at};
        # Sat May 11 18:24:51 +0000 2013
        $timestamp =~ s/^(\w+) (\w+) (\d+) (\d+):(\d+):(\d+) ([+-]\d\d\d\d) (\d\d\d\d)/$3 $2, $4:$5/;
        push @rv, { url => $url, title => $text, time => $timestamp };
    }
    return \@rv;
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

package ASF::Value::Snippet;
use LWP::UserAgent;
use URI;
use ASF::Util qw/fixup_code/;
use strict;
use warnings;

sub new {
    my $class = shift;
    my %args = @_;
    # args: type=>git or svn, path=>..., token=>..., lang=>..., prefix=>...,
    #       branch=>branch, repo=>repo, revision=>revision, lines=>lines, numbers=>1

    $args{branch} //= "HEAD";
    $args{type} //= $args{repo} ? "git" : "svn";
    my $basehost = "git-wip-us.apache.org";
    my $gitbox = ($args{repo} =~ m/gitbox\/(.+)/);
    if ($gitbox) {
        $basehost = "gitbox.apache.org";
        $args{repo} = $1;
    }
    my $uri = $args{type} eq "svn" ? "http://svn.apache.org/repos/asf/$args{path}"
        : $args{type} eq "git"
        ? "http://$basehost/repos/asf?p=$args{repo};a=blob_plain;f=$args{path};hb=$args{branch}"
        : undef;

    if (exists $args{revision} and $args{type} eq "svn") {
        $uri .= "?p=$args{revision}";
    }

    return bless {
        uri     => $uri,
        path    => $args{path},
        token   => $args{token},
        lang    => $args{lang},
        prefix  => $args{prefix},
        type    => $args{type},
        lines   => $args{lines} && [$args{lines} =~ m/(\d+)/g],
        numbers => $args{numbers},
    }, $class;
}

my %cache;

sub fetch {
    return if $ASF::Value::Offline;
    my $self = shift;

    my $content = $cache{$self->{uri}} //= do {
        die "Unsupported repo type: $self->{type}" unless defined $self->{uri};
        my $response = LWP::UserAgent->new->get(URI->new($self->{uri}));
        die "Can't fetch $self->{uri}: " . $response->status_line unless $response->is_success;
        $response->decoded_content;
    };

    if (defined $self->{token}) {
        $content =~ /\Q$self->{token}\E.*\n((?s:.*?))^.*\Q$self->{token}/m
            or die "Can't find $self->{token} block at $self->{uri}";
        $content = $1;
    }
    elsif ($self->{lines}) {
        $content = join "\n", grep {defined || ! warn "Missing lines from $self->{uri}"}
            (undef, split /\n/, $content)
                [$self->{lines}->[0] .. ($self->{lines}->[1] // $content =~ y/\n//)];
    }

    fixup_code($self->{prefix}, $self->{lang}, $content);
    $content =~ s/^(\s+):::/$1#!/ if $self->{numbers};
    return $content;
}

sub pretty_uri {
    my $self = shift;
    my $uri = $self->{uri};
    $uri =~ s!repos/asf!viewvc!    if $self->{type} eq "svn";
    $uri =~ s!a=blob_plain!a=blob! if $self->{type} eq "git";
    return $uri;
}

sub DESTROY {
    undef %{shift()};
}

sub AUTOLOAD {
    my ($attr) = our $AUTOLOAD =~ /::(\w+)$/;
    die "$attr attribute not found" unless exists $_[0]->{$attr};
    no strict 'refs';
    *{$AUTOLOAD} = sub { shift->{$attr} };
    goto &$AUTOLOAD;
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

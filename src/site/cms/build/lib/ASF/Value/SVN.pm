package ASF::Value::SVN;
use SVN::Client;
use CGI;
use ASF::Util 'normalize_svn_path';

sub new {
    my $class = shift;
    my %args = @_;
    bless {
        ctx => SVN::Client->new,
        limit => $args{limit},
        project => $args{project},
    }, $class;
}

sub init {
    my $self = shift;
    my $ctx = $self->{ctx};
    return if $self->{initialized};

    my @rv;

    my $log_receiver = sub {
        my ($changed_paths, $revision, $author, $date, $message, $pool) = @_;
        $message = CGI->escapeHTML($message);
        $message =~ s!\b([A-Z]{3,}-\d+)\b!<a href="https://issues.apache.org/jira/browse/$1">$1</a>!g;
        my %projects = map {m!([^/]+)!; ($1 eq "infrastructure" ? $1 : qq(<a href="http://$1.apache.org/">$1</a>)) => 1} keys %$changed_paths;

        push @rv, { revision => $revision, author => $author,
                    date => $date, message => $message, projects => join ", ", keys %projects };
    };

    my $url = "http://svn.apache.org/repos/asf";
    $url .= "/$self->{project}" if defined $self->{project};

    normalize_svn_path $url;

    $ctx->log2($url, "HEAD", 0, $self->{limit}, 1, 0, $log_receiver);

    $self->{data} = \@rv;
    $self->{initialized} = 1;
}

sub list {
    return if $ASF::Value::Offline;
    my $self = shift;
    $self->init;
    return $self->{data};
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

package ASF::Value::Jira;
use XML::RSS::Parser::Lite;
use LWP::Simple;
use CGI;
use strict;
use warnings;

my %jira;

sub new {
    my $class = shift;
    my %args = @_;
    bless {
        url => $args{url},
        limit => $args{limit},
    }, $class;
}


sub list {
    return if $ASF::Value::Offline;
    my $self = shift;
    $jira{$self->{url}} ||= get($self->{url}) or
        die "get $self->{url} failed.\n";
    my $rp = XML::RSS::Parser::Lite->new();
    $jira{$self->{url}} =~ s/^(<!--.*?\n-->\s*)+/<?xml version="1.0" ?>/s;
    $rp->parse($jira{$self->{url}});

    my @rv;
    for (my $i = 0; $i < $rp->count and $i < $self->{limit}; ++$i) {
        my $e = $rp->get($i);
        my $title = $e->get('title');
        my $url = $e->get('url');
#        my $date = $e->date;
        my $content = $e->get('description');
        s/&lt;/</g, s/&gt;/>/g, s/&quot;/"/g, s/&amp;/&/g for $content;
        push @rv, { url => $url, title => $title, content => $content };
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

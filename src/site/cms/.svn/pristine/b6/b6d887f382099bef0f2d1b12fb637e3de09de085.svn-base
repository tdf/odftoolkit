package ASF::Value::Mail;
use XML::Atom::Feed;
use XML::Atom::Entry;
use URI;
use Encode;
use strict;
use warnings;

sub new {
    my $class = shift;
    my %args = @_;
    my $path = "http://mail-archives.us.apache.org/mod_mbox";

    for ($args{list}) {
        my ($name, $domain) = split /\@/;
        $domain =~ s/\.?apache\.org$//;
        $domain ||= "www";
        $path .= "/$domain-$name";
    }

    bless {
        url => URI->new("$path/?format=atom"),
        limit => $args{limit},
    }, $class;
}

sub list {
    return if $ASF::Value::Offline;
    my $self = shift;
    my $feed = eval { XML::Atom::Feed->new($self->{url}) };
    warn $@ and return if $@;
    my @rv;
    foreach my $e (($feed->entries)[0..$self->{limit}-1]) {
        my $url = $e->link->href;
        my $title = $e->title;
        if ($title =~ /=\?utf-8\?Q\?(.+)\?=/) {
           $title = $1;
           s/=\?utf-8\?Q\?//g, s/\?=//g for $title;
           $title =~ tr/_/ /;
           $title =~ s/=(\w\w)/chr hex $1/ge;
        }
        my $content = eval { $e->content->as_xml_utf8 };
        warn $@ and next if $@;
        s/&lt;/</g, s/&gt;/>/g, s/&amp;/&/g for $content;
        $content =~ s/<\/?\?xml.*\?>\s*//g;
        $content =~ s/<\/?content.*?>\s*//g;
        $content =~ s| xmlns="http://www.w3.org/1999/xhtml">|>|g;

        $content =~ s#\b(?<!")(https?://\S+)#<a href="$1">$1</a>#g;
        $content =~ s!.*?<pre>\s*(.*)</pre>.*!$1!sg;
        $content =~ s!\n\s*\n!<br />&nbsp;<br />!g;

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

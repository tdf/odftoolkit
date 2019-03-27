package ASF::Value::Blogs;
use XML::Atom::Feed;
use XML::Atom::Entry;
use URI;
use strict;
use warnings;

sub new {
    my $class = shift;
    my %args = @_;
    my $path = "http://blogs.apache.org";

    bless {
        url => URI->new("$path/$args{blog}/feed/entries/atom"),
        limit => $args{limit},
    }, $class;
}

sub list {
    return if $ASF::Value::Offline;
    my $self = shift;
    my $feed = XML::Atom::Feed->new($self->{url});
    # Try to fix the following error building site:
    # Can't call method "entries" on an undefined value at /usr/local/cms/build/lib/ASF/Value/Blogs.pm line 24.
    return unless defined $feed;
    my @rv;
    foreach my $e (($feed->entries)[0..$self->{limit}-1]) {
        my $url = $e->link->href;
        my $title = $e->title;
#        my $date = $e->date;
        my $content = $e->content->as_xml_utf8;
        s/&lt;/</g, s/&gt;/>/g,  s/&quot;/"/g, s/&amp;/&/g for $content;
        $content =~ s/<\/?\?xml.*\?>\s*//g;
        $content =~ s/<\/?content.*?>\s*//g;
        $content =~ s| xmlns="http://www.w3.org/1999/xhtml">|>|g;

        # Remove styling
        $content =~ s/<\/?font.*?>//g;
        $content =~ s{<pre>(.*?)</pre>}{
            my $text = $1;
            $text =~ s/\n\n/<br \/>\n<br \/>\n/g;
            $text;
        }egs;
        $content =~ s{<(\w+)((\s+\w+=("|').*?\4)*)>}{
            my $name = $1;
            my $attrs = $2;
            $attrs =~ s{(\s+(\w+)=("|').*?\3)}{
                if ($2 eq 'style' || $2 eq 'class') {
                    ''
                } else {
                    $1
                }
            }ge;
            "<$name$attrs>"
        }ge;

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


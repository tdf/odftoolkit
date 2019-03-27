#!/usr/bin/perl
# Spiders a site using the cwiki export, and generates markdown files
#  for each page in cwiki
use strict;
use warnings;

use RPC::XML;
use RPC::XML::Client;

my $confluence = "https://cwiki.apache.org/confluence/";
my $RPCURL = $confluence."rpc/xmlrpc";

my $siteName = shift;
my $username = shift;
my $password = shift;
unless($siteName && $username && $password) {
   print "Use:\n";
   print "   $0 <cwiki site name> <username> <password>\n";
   print "\n";
   print " eg for https://cwiki.apache.org/confluence/display/COMDEVxSITE\n";
   print "    $0 COMDEVxSITE jim securePassword\n";
   exit 1;
}

# Check we're in the right place
if(-d "content" && -d "lib") {
} else {
   die("Must be run from root of site, containing /content/ and /lib/\n");
}
my ($binPath) = ($0 =~ /^(.*)\/(.*?)$/);

# Connect to their icky API. (What's wrong with REST we ask...?)
my $client = RPC::XML::Client->new($RPCURL);
my $response = $client->send_request('confluence1.login', $username, $password);
$response->is_fault and die "ERROR: could not login as $username: ", $response->value->{faultString};
my $token = $response->value;

# Get a list of all the pages
$response = $client->send_request('confluence1.getPages', $token, $siteName);
$response->is_fault and die "ERROR: could not get pages for $siteName: ", $response->value->{faultString};

my @pages = @{$response->value};

foreach my $page (@pages) {
   my $title = $page->{title};

   print "Fetching $title (".$page->{id}.")\n";
   $response = $client->send_request('confluence1.getPage', $token, $siteName, $title);
   $response->is_fault and die "ERROR: could not get page details: ", $response->value->{faultString};

   my %details = %{$response->value};
   my $content = $details{content};

   print "Processing $title from ".$page->{url}."\n";

   my $page = $title;
   $page =~ s/\s/-/g;

   my $cwikiFile = "content/".lc($page).".cwiki";
   open(CWIKI, ">$cwikiFile");
   print CWIKI $content;
   close CWIKI;

   my $mdFile = $cwikiFile;
   $mdFile =~ s/\.cwiki/.mdtext/;
   
   print "  Generating markdown file\n";
   `$binPath/convert_cwiki_markup.pl "$title" $cwikiFile $mdFile`;

   print "  Finished processing $title\n\n";
}

print "\n";
print "Done!\n";

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

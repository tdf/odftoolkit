#!/usr/bin/perl
# Converts a CWiki export template to a DTL template
use strict;
use warnings;

my $source = shift;
unless($source && -f $source) {
   print "Use:\n";
   print "  $0 <cwiki template.xml> [export.html] [export_markdown.html]\n";
   exit 1;
}

my $dest = shift;
unless($dest) {
   $dest = $source;
   $dest =~ s/\.xml/\.html/;
}
if(-f $dest) {
   print "Destination file $dest already exists\n";
   exit 1;
}

my $mdest = shift;
unless($mdest) {
   $mdest = $dest;
   $mdest =~ s/\.html/_markdown.html/;
}
if(-f $mdest) {
   print "Destination file $mdest already exists\n";
   exit 1;
}

# Prepare to convert
open(INP, "<$source");
open(OUT, ">$dest");
open(MOUT, ">$mdest");

print MOUT "{% extends \"$dest\" %}\n";

my $done_title = 0;
my $done_body = 0;
while(my $line = <INP>) {
   if($line =~ /^#/) { next; }

   if($line =~ /\$page.title/) {
      $done_title++;
      if($done_title > 1) {
         warn("Found \$page.title multiple times, but only converting once\n");
         $line =~ s/\$page.title//;
      } else {
         $line =~ s/\$page.title/{% block title %}{% endblock %}/;
         print MOUT '{% block title %}{{ headers.title }}{% endblock %}'."\n";
      }
   }

   if($line =~ /\$body/) {
      $done_body++;
      if($done_body > 1) {
         warn("Found \$body multiple times, but only converting once\n");
         $line =~ s/\$body//;
      } else {
         $line =~ s/\$body/{% block content %}{% endblock %}/;
         print MOUT '{% block content %}{{ content|markdown }}{% endblock %}'."\n";
      }
   }

   $line =~ s/\$autoexport.breadcrumbs\(\$page\)/{{ breadcrumbs|safe }}/;

   print OUT $line;
}

close INP;
close OUT;
close MOUT;

print "Generated $dest\n";
print "Generated $mdest\n";

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

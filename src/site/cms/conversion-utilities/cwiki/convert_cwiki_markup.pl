#!/usr/bin/perl
# Converts a CWiki markup to MarkDown syntax
use strict;
use warnings;

use Text::Wrap qw/wrap $huge/;
$huge = "overflow";

my $pageName = shift;
my $compressedPageName = $pageName;
$compressedPageName =~ s/\s//g;

my $source = shift;
unless($source && -f $source) {
   print "Use:\n";
   print "  $0 <page name> <cwiki template> [export.html]\n";
   exit 1;
}
my $dest = shift;
unless($dest) {
   $dest = $source;
   $dest .= ".html";
}

sub convertURL {
   my $url = shift;
   if($url =~ /^http/) {
      return $url;
   }
   $url =~ s/\s/-/g;
   $url = lc($url).".html";
   return $url;
}

open(INP, "<$source");
open(OUT, ">$dest");

print OUT "Title: $pageName\n";

# Load the file into a temporary array, and do some line wrapping on
#  the paragraphs
my @contents;
while(my $line = <INP>) {
   $line =~ s/\r//;
   unless($line =~ /^\s*$/) {
      my @parts = split(/( \[)/, $line);
      $line = "";
      foreach my $p (@parts) {
         if($p =~ /^(.*?\])(.*)$/) {
            $p = $1."\n".wrap("","",$2);
         } else {
            $p = wrap("","",$p);
         }
         $line .= $p;
      }
      unless($line =~ /\n$/s) { $line .= "\n"; }
   }
   push(@contents, $line);
}

# Convert it
my $in = "";
foreach my $line (@contents) {
   if($in eq "noformat") {
      if($line =~ /^\s*^{noformat}/) {
         print OUT "\n";
         $in = "";
      } else {
         print OUT "    ".$line;
      }
      next;
   } elsif($in eq "code:xml" || $in eq "code:text") {
      if($line =~ /^\s*^{code}/) {
         print OUT "\n";
         $in = "";
      } else {
         print OUT "    ".$line;
      }
      next;
   } elsif($in eq "table") {
      if($line =~ /^\s*$/) {
         print OUT "</table>\n";
         $in = "";
      }
   } elsif($in) {
      warn("Unexpected block '$in' for $line");
      $in = "";
   }

   # Numbered lists
   if($line =~ /^\s*#/) {
      $line =~ s/^\s*#\s*/1. /;
   }

   # Headings
   if($line =~ /^h(\d)\.\s?(.*)$/) {
      # Build the heading entry
      $line = ("#"x$1)." ".$2."\n";
      # Now replicate the a name
      my $name = $2;
      $name =~ s/\s//g;
      $line = "<a name=\"$compressedPageName-$name\"></a>\n".$line;
   }

   # Links
   if($line =~ /(\[(.*?)\])/) {
      my ($all,$text,$link) = ($1,$2,"");
      if($text =~ /^(.*?)\|(.*)/) {
         ($text,$link) = ($1,$2);
      }

      my $newlink;
      if($link) {
         $newlink = "[$text](".convertURL($link).")";
      } else {
         $newlink = "[$text](".convertURL($text).")";
      }
      $line =~ s/\Q$all\E/$newlink/;
   }

   # Old-style bold / italic
   $line =~ s/\{\{(.*?)\}\}/*$1*/g;
   $line =~ s/\{\{\{(.*?)\}\}\}/**$1**/g;

   # No-Format
   if($line =~ /^\s*^{noformat}/) {
      $in = "noformat";
      $line = "\n";
   }

   # Code blocks
   if($line =~ /^\s*^{code:xml}/) {
      $in = "code:xml";
      $line = "\n";
   }
   if($line =~ /^\s*^{code:title=(.*?)}/) {
      $in = "code:text";
      $line = '<DIV class="code panel" style="border-style: solid;border-width: 1px;"><DIV class="codeHeader panelHeader" style="border-bottom-width: 1px;border-bottom-style: solid;"><B>'.$1.'</B></DIV><DIV class="codeContent panelContent">'."\n";
   }
   if($line =~ /^\s*^{code}/) {
      $in = "code:text";
      $line = "\n";
   }

   # Forced breaks
   if($line =~ /\\\\/) {
      $line = "  \n  \n";
   }

   # Tables
   if($line =~ /^\|/) {
      unless($in eq "table") {
         print OUT "<table class=\"table\">\n";
         $in = "table";
      }
      $line =~ s/\|\|/<\/th><th>/g;
      $line =~ s/\|/<\/td><td>/g;
      $line =~ s/<t[dh]>\s*$//gs;
      $line =~ s/^\s*<\/t[dh]>//gs;
      $line = "<tr>".$line."</tr>\n";
   }

   # Table of contents
   if($line =~ /\s*^{toc}/) {
      my @headings = grep(/^h/, @contents);
      $line = "";
      foreach my $h (@headings) { 
         $h =~ /^\s*h(\d).\s+(.*?)\s*$/s;
         my $hnum = $1;
         my $text = $2;
         my $name = $2;
         $name =~ s/\s//g;

         my $l = "   "x($hnum-1);
         $l .= "* [$text](#$compressedPageName-$name)\n";
         $line .= $l;
      }
   }

   print OUT $line;
}

close INP;
close OUT;

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

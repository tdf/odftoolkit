#!/usr/bin/perl -ln
use Text::Wrap qw/wrap $huge/;
$huge = "overflow";

unless (/^#/ or 1../^$/ or /^\|/) {
    s/^(\s*)-  \*/$1- */;   # <li><b> is all too common
    s/ \././g;              # periods after links
    s/^\s+<\/pre>/<\/pre>/; # oddly indented pre blocks

    s/^(:?)(\s*)//;
    my $indent = length($2) > 1 ? $2 : "";
    $indent = "> $indent" if s/^> //;
    $_ = wrap($1 . $indent, $1 ? " $indent" : $indent, $_);
}

# remove consecutive blank lines, add blank lines before sections
next if /^$/ and $prev =~ /^\s*$/;
$_ = "\n$_" if /^#/ and $prev !~ /^$/;
$prev = $_;

if (/^$/ and not $notice++) {
    $_ = <<NOTICE;
Notice:    Licensed to the Apache Software Foundation (ASF) under one
           or more contributor license agreements.  See the NOTICE file
           distributed with this work for additional information
           regarding copyright ownership.  The ASF licenses this file
           to you under the Apache License, Version 2.0 (the
           "License"); you may not use this file except in compliance
           with the License.  You may obtain a copy of the License at
           .
             http://www.apache.org/licenses/LICENSE-2.0
           .
           Unless required by applicable law or agreed to in writing,
           software distributed under the License is distributed on an
           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
           KIND, either express or implied.  See the License for the
           specific language governing permissions and limitations
           under the License.
NOTICE
}

print

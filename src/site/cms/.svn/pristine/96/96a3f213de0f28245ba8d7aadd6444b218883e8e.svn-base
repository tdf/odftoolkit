package ASF::CMS::WC;
use File::stat;
use ASF::Util qw/unload_package/;
use strict;
use warnings;

our $VERSION = "1.2";

my %mtime;

sub delegate {
    my ($class, %args) = @_;
    my $pkg            = $class . "::\u$args{action}";
    my $file           = $pkg;
    s!::!/!g and $_ .= ".pm" for $file;

    my $modtime;

    if (exists $INC{$file}) {
        $modtime = stat($INC{$file})->mtime;

        if ($modtime > $mtime{$file}) {
            unload_package $pkg;  # We can do this because of the constraints
                                  # (specifically no symbol imports/exports/
                                  # external references) on these WC modules.
                                  # See below for details.
        }
        else {
            return bless \%args, $pkg;
        }
    }

    require $file;
    $mtime{$file} = $modtime || stat($INC{$file})->mtime;
    return bless \%args, $pkg;
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

=head1 NOTES

A key part of the design of the WC:: packages is that their symbol tables
consist only of uppercase symbols, and the lower-cased HTTP supported methods.
This constraint eliminates symbol imports, which basically implies that all the
prerequisite packages need to be provided by loading them in ASF::CMS.  Hence
the C<<use>> calls are limited to C<<strict>> and C<<warnings>>.  It also
necessitates this package, which follows the factory pattern by simply loading
and creating new objects in the corresponding package for a given request.

Since normal OO method lookups will pollute the symbol table with cache entries
(on failed lookups), special care must be taken to lookup the appropriate
method directly via the package stash, and to do so without any compile-time
caching of the stash lookup (in order to remain compatible with the reload
feature).

Think of the WC:: modules as CGI scripts running under ModPerl::Registry
if it helps, because that also informs the design.  IOW you don't need to
restart the server when making modifications to these modules: they will be
reloaded automatically.

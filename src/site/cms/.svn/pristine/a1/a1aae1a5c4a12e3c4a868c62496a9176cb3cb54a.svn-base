package ASF::CMS::WC::Static;
use strict;
use warnings;

sub get {
    return Apache2::Const::DECLINED;
}

*head = *get;

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

Returning DECLINED triggers mod_perl to reset r->handler to r->content_type
(if set).  If we ensure the autoindex handler runs after the "modperl" handler
does (by recompiling mod_perl with the "modperl" handler running APR_HOOK_FIRST),
this will allow autoindex to pick up directories (since mod_dir sets the
content_type string to DIR_MAGIC_TYPE in the fixup phase).  The default Options
setting is "All", so autoindex will run even though none of the per-dir config
settings are available.

Ordinary files are handled similarly by the default handler, but nothing
special is required for those.

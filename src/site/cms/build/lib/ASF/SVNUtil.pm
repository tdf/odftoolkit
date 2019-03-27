package ASF::SVNUtil;

# assumes svn 1.7+, but will mostly work with prior versions

use SVN::Client;
use SVN::Wc;
use strict;
use warnings;
use base 'Exporter';

our @EXPORT = qw/svn_up svn_status svn_add svn_rm svn_ps is_version_controlled/;
our $VERSION = "1.0";

sub svn_up {
    my $svn_base = shift;
    my $ctx = SVN::Client->new;
    my (@add, @delete, @restore, @update);
    my %dispatch = (
        $SVN::Wc::Notify::Action::add           => \@add,
        $SVN::Wc::Notify::Action::update_add    => \@add,
        $SVN::Wc::Notify::Action::update_delete => \@delete,
        $SVN::Wc::Notify::Action::restore       => \@restore,
        $SVN::Wc::Notify::Action::update_update => \@update,
    );

    $ctx->notify( sub {
        my ($path, $action) = @_;
        $path =~ s!^\Q$svn_base/!!;
        push @{$dispatch{$action}}, $path if exists $dispatch{$action};
    });

    my $revision = eval { $ctx->update($svn_base, "HEAD", 1) };
    if ($@) {
        my ($wc_root_path) = map /^Working Copy Root Path: (.*)$/, `svn info '$svn_base'`
            or die "Can't find Working Copy Root Path!\n";
        $ctx->cleanup($wc_root_path);
        $revision = $ctx->update($svn_base, "HEAD", 1);
    }

    print "Updated $svn_base to revision $revision.\n";
    return add => \@add, delete => \@delete, restore => \@restore, update => \@update,
           revision => $revision;
}

my %st_dispatch = (
    'M'  => "modified",
    'A'  => "added",
    'D'  => "deleted",
    '?'  => "unversioned",
    '!'  => "missing",
    'C'  => "conflicted",
    'I'  => "ignored",
    'R'  => "replaced",
    'X'  => "external",
    '~'  => "obstructed",
);

sub svn_status {
    my $svn_base = shift;
    chomp(my @status = `svn status '$svn_base'`);
    my %rv;
    # ignores property mods, etc.; turns out we don't need them for our use-case
    /^([MAD?!CIRX~]).{7}(.+)$/ and push @{$rv{$st_dispatch{$1}}}, $2 for @status;
    return %rv;
}

sub svn_add {
    my $ctx = SVN::Client->new;
    print "Adding $_.\n" and $ctx->add($_, 1) for @_;
}

sub svn_rm {
    my $ctx = SVN::Client->new;
    print "Removing $_.\n" and $ctx->delete($_, 1) for @_;
}

sub svn_ps {
    my $ctx = SVN::Client->new;
    my ($target, $propname, $propval) = @_;
    print "Setting '$propname' on $target.\n"
        and $ctx->propset($propname, $propval, $target, 0);
}

my %vc_cache;
sub is_version_controlled {
    $vc_cache{$_[0]} //= (-d "$_[0]/.svn" or `svn info '$_[0]' 2>&1` !~ / is not a working copy/) || 0;
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

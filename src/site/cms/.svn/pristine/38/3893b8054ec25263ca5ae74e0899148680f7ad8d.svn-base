#!/usr/bin/perl
use strict;
use warnings;

my $project = shift;
my $build_dir = "/home/cmsslave/slave15/$project-site-staging";
my $staging_dir = "/usr/local/websites/$project";

-d "$build_dir/build" and warn "build dir '$build_dir/build' exists: skipping $project\n" and exit 0;

die "Setup the CMS for $project first by visiting https://cms.apache.org/$project/setup\n"
    unless -d "/usr/local/cms/wc/$project";

mkdir $build_dir;
mkdir "/usr/local/websites";
my ($cmsslave_uid, $cmsslave_gid) = (getpwnam("cmsslave"))[2,3];
chown $cmsslave_uid, $cmsslave_gid, "/usr/local/websites";

system "rsync -aq /usr/local/cms/wc/$project/original/ $build_dir/build/";
die "rsync failed: $?" if $?;
system "chown -R cmsslave:cmsslave $build_dir";

-d $staging_dir and warn "staging dir '$staging_dir' exists for $project: skipping" and exit 0;

system "svn co https://svn.apache.org/repos/infra/websites/staging/$project $staging_dir";
die "svn co failed: $?" if $?;
system "chown -R cmsslave:cmsslave $staging_dir";

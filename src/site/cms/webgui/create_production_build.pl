#!/usr/bin/perl
use strict;
use warnings;

my $project = shift;
my $build_dir = "/home/cmsslave/slave15/$project-site-production";
my $production_dir = "/usr/local/websites/production/$project";

-d "$build_dir/build" and warn "build dir exists: skipping $project\n" and exit 0;

die "Setup the CMS for $project first by visiting https://cms.apache.org/$project/setup\n"
    unless -d "/usr/local/cms/wc/$project";

mkdir $build_dir;
mkdir "/usr/local/websites/production";
my ($cmsslave_uid, $cmsslave_gid) = (getpwnam("cmsslave"))[2,3];
chown $cmsslave_uid, $cmsslave_gid, "/usr/local/websites/production";

system "rsync -aq /usr/local/cms/wc/$project/original/ $build_dir/build/";
die "rsync failed: $?" if $?;
system "chown -R cmsslave:cmsslave $build_dir";

-d $production_dir and warn "production dir exists for $project: skipping" and exit 0;

system "svn co https://svn.apache.org/repos/infra/websites/production/$project $production_dir";
die "svn co failed: $?" if $?;
system "chown -R cmsslave:cmsslave $production_dir";

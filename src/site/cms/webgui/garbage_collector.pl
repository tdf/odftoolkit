#!/usr/bin/perl
use File::Path qw/rmtree/;
use File::Basename;
use Cwd 'abs_path';
our $BASE_DIR;
BEGIN {
    $BASE_DIR = dirname dirname abs_path $0;
}
use lib "$BASE_DIR/build/lib";
use ASF::Util qw/get_lock/;
$ENV{PATH} = "/sbin:/usr/bin:/usr/local/bin";
$ENV{LANG} = "en_US.UTF-8";
$ENV{HOME} = "/home/www";
chomp(our $ZROOT = `zfs list -Ho name -- $BASE_DIR/wc`);

my $days = shift;
for (grep -M > $days, glob "$BASE_DIR/wc/*/*-*") {
    my $basename = basename $_;
    my ($project) = m!/wc/([^/]+)/!;
    `sudo zfs destroy -R $ZROOT/$project/$basename`;
    `sudo zfs destroy -R $ZROOT/$project/original\@$basename`;
    rmtree $_;
}

rmtree $_ for grep -M > $days, glob "$BASE_DIR/locks/*-*";
rmtree $_ for grep -M > $days, glob "$BASE_DIR/tmp/*";

for (glob "$BASE_DIR/wc/*/*-*") {
    # this isn't right for users with a - in their id, oh well.
    my ($project, $user) = m!/([^/]+)/([^-/]+)-[^/]+$!;
    my $lock = get_lock("$BASE_DIR/locks/$project-wc-$user");
    system "svn", "cleanup", $_;
    system "svn", "up", "-q", "--non-interactive", $_ if $user eq "anonymous";
}

for (glob "$BASE_DIR/wc/*/original") {
    my ($project) = m!/([^/]+)/original$!;
    my $lock = get_lock("$BASE_DIR/locks/$project-wc-original");
    system "svn", "cleanup", $_;
    system "svn", "up", "-q", "--non-interactive", $_;
}

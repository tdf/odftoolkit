package ASF::Util;

use base 'Exporter';
use YAML::XS;
use File::Basename;
use File::Copy;
use File::Find;
use Cwd;
use File::stat;
use Fcntl ":flock";
use strict;
use warnings;

our @EXPORT_OK = qw/read_text_file copy_if_newer get_lock shuffle sort_tables fixup_code
                    unload_package purge_from_inc touch normalize_svn_path parse_filename
                    walk_content_tree Load Dump/;
our $VERSION = "1.9";

# utility for parsing txt files with headers in them
# and passing the args along to a hashref (in 2nd arg)

sub read_text_file {
    my ($file, $out, $headers_only) = @_;
    open my $fh, "<", $file or die "Can't open file $file: $!\n";

    my $headers = 1;
    local $_;
    my $content = "";
    my $BOM = "\xEF\xBB\xBF";

 LOOP:
    while (<$fh>) {
        if ($headers) {
            if ($. == 1) {
                s/^$BOM//;
                if (/^---\s+$/) {
                    my $yaml = "";
                    while (<$fh>) {
                        last if /^---\s+$/;
                        $yaml .= $_;
                    }
                    $out->{headers} = Load($yaml);
                    $headers = 0, next LOOP;
                }
            }
            $headers = 0, next if /^\r?\n$/;
            my ($name, $val) = split /:\s+/, $_, 2;
            $headers = 0, redo LOOP
                unless $name =~ /^[\w-]+$/ and defined $val;
            $name =~ tr/A-Z-/a-z_/;
            chomp $val;
            while (<$fh>) {
                $out->{headers}->{$name} = $val, redo LOOP
                    unless s/^\s+(?=\S)/ /;
                chomp;
                $val .= $_;
            }
            $out->{headers}->{$name} = $val;
        }
        last LOOP if $headers_only;
        no warnings 'uninitialized';
        $content .= $_;
    }
    if (exists $out->{headers}->{atom}) {
        for ($out->{headers}->{atom}) {
            if (/^(\S+)\s*(?:"([^"]+)")?\s*$/)  {
                $_ = { url => $1, title => $2 || "" };
            }
        }
    }

    $out->{content} = $content unless $headers_only;
}

sub copy_if_newer {
    my ($src, $dest) = @_;
    die "Undefined arguments to copy($src, $dest)\n"
        unless defined $src and defined $dest;
    copy $src, $dest unless -f $dest and stat($src)->mtime < stat($dest)->mtime;
    chmod 0755, $dest if -x $src or $dest =~ /\.cgi$/;
}

# NOTE: This will break your runtime if you call this on a package
# that imports/exports symbols or has any other external references
# to its available symbols.  The package also should be a leaf package,
# ie not have any subpackages within its namespace.

sub obliterate_package {
    my $pkg = shift;

    # expand to full symbol table name if needed

    unless ($pkg =~ /^main::.*::$/) {
        $pkg = "main$pkg"       if      $pkg =~ /^::/;
        $pkg = "main::$pkg"     unless  $pkg =~ /^main::/;
        $pkg .= '::'            unless  $pkg =~ /::$/;
    }

    no strict 'refs';

    my($stem, $leaf) = $pkg =~ m/(.*::)(\w+::)$/;
    my $stem_symtab = *{$stem}{HASH};
    return unless defined $stem_symtab and exists $stem_symtab->{$leaf};

    # free all the symbols and types in the package
    my $leaf_symtab = *{$stem_symtab->{$leaf}}{HASH};
    foreach my $name (keys %$leaf_symtab) {
        my $fullname = $pkg . $name;
        undef $$fullname;
        undef @$fullname;
        undef %$fullname;
        undef &$fullname;
        undef *$fullname;
    }

    # delete the symbol table

    %$leaf_symtab = ();
    delete $stem_symtab->{$leaf};
}

sub unload_package {
    for my $package (@_) {
        obliterate_package $package;
        my $modpath = $package;
        s!::!/!g and $_ .= ".pm" for $modpath;
        delete $INC{$modpath};
    }
}

sub purge_from_inc {
    for my $d (@_) {
        for my $id (grep $INC[$_] eq $d, reverse 0..$#INC) {
            splice @INC, $id, 1;
        }
    }
}

sub get_lock {
    my $lockfile = shift;
    open my $lockfh, "+>", $lockfile
        or die "Can't open lockfile $lockfile: $!\n";
    flock $lockfh, LOCK_EX
        or die "Can't get exclusive lock on $lockfile: $!\n";
    return $lockfh;
}

sub touch {
    @_ or push @_, $_;
    for (@_) {
        utime undef, undef, $_ and next;
        open my $fh, ">>", $_
            or die "Can't open $_: $!\n";
        close $fh;
        utime undef, undef, $_
            or die "Can't touch $_: $!\n";
    }
}

sub normalize_svn_path {
    for (@_) {
        tr!/!/!s;
        s!/$!!;
        s!^(https?):/!$1://!;
    }
}

sub shuffle {
    my $deck = shift;  # $deck is a reference to an array
    return unless @$deck; # must not be empty!
    my $i = @$deck;
    while (--$i) {
        my $j = int rand ($i+1);
        @$deck[$i,$j] = @$deck[$j,$i];
    }
}

# arbitrary number of tables supported, but only one col per table may be sorted

sub sort_tables {
    my @orig = split /\n/, shift, -1;
    my @out;
    while (defined(local $_ = shift @orig))  {
        push @out, $_;
        /^(\|[ :vn^-]+)+\|$/ or next;
        my($data, $col, $direction, $cur, $numeric);
        $cur = 0;
        while (/\|([ :vn^-]+)/g) {
            $data = $1;
            if ($data =~ tr/v/v/) {
                $col = $cur;
                $direction = -1;
                last;
            }
            elsif ($data =~ tr/^/^/) {
                $col = $cur;
                $direction = 1;
                last;
            }
            $cur++;
        }
        unless (defined $col) {
            push @out, shift @orig while @orig and $orig[0] =~ /^\|/;
            next;
        }
        $numeric = 1 if $data =~ tr/n/n/;
        my @rows;
        push @rows, [split /\s*\|\s*/, shift(@orig), -1]
            while @orig and $orig[0] =~ /^\|/;
        shift @$_, pop @$_ for @rows; # dump empty entries at ends
        @rows = $numeric
            ? sort { $a->[$col] <=> $b->[$col] } @rows
            : sort { $a->[$col] cmp $b->[$col] } @rows;
        @rows = reverse @rows if $direction == -1;
        push @out, map "| " . join(" | ", @$_) . " |", @rows;
    }

    return join "\n", @out;
}

sub parse_filename {
    my ($f) = (@_, $_);
    my ($filename, $dirname, $ext) = fileparse $f, qr/\.[^.]*/;
    undef $ext unless $ext =~ tr/.//d;
    return $filename, $dirname, $ext;
}

sub fixup_code {
    my $prefix = shift;
    my $type   = shift;

    for (@_) {
        s/^\Q$prefix//mg if defined $prefix;
        s/^/    :::$type\n/,  s/\n/\n    /g, s/    $//
            if defined $type;
    }
}

sub walk_content_tree (&) {
    my $wanted = shift;
    my $cwd = cwd;
    local $_; # filepath that $wanted sub should inspect, rooted in content/ dir
    find({ wanted => sub {
               $File::Find::prune = 1, return if -d and m!\.page$!;
               return unless -f;
               s!^\Q$cwd/content!!;
               $wanted->();
           }, no_chdir => 1 }, "$cwd/content");
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

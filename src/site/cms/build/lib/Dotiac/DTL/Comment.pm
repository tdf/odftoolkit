###############################################################################
#Comment.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Comment.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL::Comment;
use strict;
use warnings;
use base qw/Dotiac::DTL::Tag/;

our $VERSION = 0.8;



sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	$self->{comment}=shift();
	$self->{n}=shift;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	return $self->{p}.$self->{n}->string(@_);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{p}],["\$text$id"]));
	return $self->{n}->perl($fh,$id+1,@_);	
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	return $self->{n}->perlinit($fh,$id+1,@_);	
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $in="\t" x $level;
	print $fh $in,"print \$text$id;\n";
	my $c=$self->{comment};
	$c=~s/\n/\n$in#/g;
	print $fh "$in# $c\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $in="\t" x $level;
	print $fh $in,"\$r.=\$text$id;\n";
	my $c=$self->{comment};
	$c=~s/\n/\n$in#/g;
	print $fh "$in# $c\n";
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	return $self->{n}->perleval($fh,$id+1,@_);	
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	return $self->{n}->perlcount($id+1);
}
sub eval {
	return;
}
1;
__END__

=head1 NAME

Dotiac::DTL::Comment - Stores a Django template comment tag.

=head1 SYNOPSIS

Template file

	Some text.... 
	{# A comment, #}
	Some other Text.{# Another comment
	over multiple lines #} Some more text.

=head1 DESCRIPTION

Everything between the starting {# and the next #} is skipped while generating
the output of the template.

During Template compiling it is converted to normal perl comment tags:

	Template code...
	{# Some comment
	more comment #}
	Template code..

Will be converted to:

	Perl code...
	# Some comment
	# more comment
	Perl code...

The module itself has no real use, it's just used by the Dotiac::DTL 
parser to store those comments.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 BUGS

If you find a bug, please report it.

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

#comment.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#comment.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::comment;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{content}=$obj->parse($data,$pos,\$found,"endcomment");
	bless $self,$class;
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
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{content}->perlcount($id+1,@_);
	return $self->{n}->perlcount($id+1);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	$id = $self->{content}->perl($fh,$id+1,@_);
	return $self->{n}->perl($fh,$id+1,@_)
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	#$self->SUPER::perl($fh,$id,@_);
	$id = $self->{content}->perlinit($fh,$id+1,@_);
	return $self->{n}->perlinit($fh,$id+1,@_)
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t" x $level,"if (0) {\n";
	$id = $self->{content}->perlprint($fh,$id+1,$level+1,@_);
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh "\t" x $level,"if (0) {\n";
	$id = $self->{content}->perlstring($fh,$id+1,$level+1,@_);
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	print $fh "\t" x $level,"if (0) {\n";
	$id=$self->{content}->perleval($fh,$id+1,$level+1,@_);
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perleval($fh,$id+1,$level,@_);
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
sub eval {
	my $self=shift;
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::comment - The {% comment %} tag

=head1 SYNOPSIS

Template file:

	{% comment %}
		This text will never be seen
	{% endcomment %}

=head1 DESCRIPTION

Ignores everything between {% comment %} and {% endcomment %}.

=head1 BUGS AND DIFFERENCES TO DJANGO

The part between {% comment %} and {% endcomment %} still has to be valid.

Use {# ... #} for another type of comment.

Not really a bug, but everything in this tags will also be compiled to perl, but optimized away by the perl parser.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

#debug.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#debug.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::debug;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;
use Data::Dumper;
use Carp;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	print "<pre>\n",Data::Dumper->Dump([\@_,Carp::longmess()],[qw/Parameter Stack/]),"</pre>\n";
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	return $self->{p}."<pre>\n".Data::Dumper->Dump([\@_,Carp::longmess()],[qw/Parameter Stack/])."</pre>\n".$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "use Data::Dumper;\n";
	print $fh "use Carp;\n";
	return $self->{n}->perl($fh,$id+1,@_);
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t" x $level,'print "<pre>\n",Data::Dumper->Dump([[$vars,$escape,@_],Carp::longmess()],[qw/Parameter Stack/]),"</pre>\n";',"\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh "\t" x $level,'$r.="<pre>\n".Data::Dumper->Dump([[$vars,$escape,@_],Carp::longmess()],[qw/Parameter Stack/])."</pre>\n";',"\n";
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->{n}->perleval($fh,$id+1,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->{n}->perlinit($fh,$id+1,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	return $self->{n}->perlcount($id+1);
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

Dotiac::DTL::Tag::debug - The {% debug %} tag

=head1 SYNOPSIS

Template file:
	
	{% debug %}

=head1 DESCRIPTION

Prints some debugging information about autoescape status and variables.

=head1 BUGS AND DIFFERENCES TO DJANGO

This doesn't work at all like Django's debug, but it provides similar information.

This tag shouldn't be used in production systems anyways.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

#firstof.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#firstof.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::firstof;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	$self->{vars}=[Dotiac::DTL::get_variables(shift())];
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	foreach my $v (@{$self->{vars}}) {
		my $r = Dotiac::DTL::devar($v,@_);
		print $r and last if $r;
	}
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;foreach my $v (@{$self->{vars}}) {
		my $r = Dotiac::DTL::devar($v,@_);
		return $self->{p}.$r.$self->{n}->string(@_) if $r;
	}
	return $self->{p}.$self->{n}->string(@_);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{vars}],["\$vars$id"]));
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	my $in="\t" x $level;
	print $fh $in,"print ",join(" || ",map {"Dotiac::DTL::devar(\$vars$id"."->[$_],\$vars,\$escape,\@_)"} (0 .. $#{$self->{vars}})),";\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	my $in="\t" x $level;
	print $fh $in,"\$r.=",join(" || ",map {"Dotiac::DTL::devar(\$vars$id"."->[$_],\$vars,\$escape,\@_)"} (0 .. $#{$self->{vars}})),";\n";
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	return $self->{n}->perlcount($id+1);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	return $self->{n}->perleval($fh,$id+1,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	return $self->{n}->perlinit($fh,$id+1,@_);
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

Dotiac::DTL::Tag::firstof - The {% firstof VARIABLE1[|VARIABLE2[|VARIABLE3[|...]]] %} tag

=head1 SYNOPSIS

Template file:

	{% firstof var1 var2 "default text" %}

=head1 DESCRIPTION

Outputs the first true value from its argument list.

This is the same as:

	{% if var1 %}
		{{ var1 }}
	{% else %}
		{% if var2 %}
			{{ var2 }}
		{% else %}
			default text
		{% endif %}
	{% endif %}


=head1 BUGS AND DIFFERENCES TO DJANGO

If you find one, please report it.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

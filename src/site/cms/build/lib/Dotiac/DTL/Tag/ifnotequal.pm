#ifnotequal.pm
##Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#ifnotequal.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::ifnotequal;
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
	$self->{true}=$obj->parse($data,$pos,\$found,"else","endifnotequal");
	if ($found eq "else") {
		$self->{false}=$obj->parse($data,$pos,\$found,"endifnotequal");
	}
	($self->{var1},$self->{var2},undef)=Dotiac::DTL::get_variables($name);
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $r=Dotiac::DTL::devar_raw($self->{var1},@_)->true();
	my $v=Dotiac::DTL::devar_raw($self->{var2},@_)->true();
	if ($r ne $v) {
		$self->{true}->print(@_);
	}
	else {
		$self->{false}->print(@_) if $self->{false};
	}
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	my $r=Dotiac::DTL::devar_raw($self->{var1},@_)->true();
	my $v=Dotiac::DTL::devar_raw($self->{var2},@_)->true();
	if ($r ne $v) {
		return $self->{p}.$self->{true}->string(@_).$self->{n}->string(@_);
	}
	return $self->{p}.($self->{false}?$self->{false}->string(@_):"").$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{var1}],["\$vara$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{"var2"}],["\$varb$id"]));
	$id = $self->{true}->perl($fh,$id+1,@_);
	$id = $self->{false}->perl($fh,$id+1,@_) if $self->{false};
	return $self->{n}->perl($fh,$id+1,@_)
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id = $self->{true}->perlinit($fh,$id+1,@_);
	$id = $self->{false}->perlinit($fh,$id+1,@_) if $self->{false};
	return $self->{n}->perlinit($fh,$id+1,@_)
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t" x $level,"if (Dotiac::DTL::devar_raw(\$vara$id,\$vars,\$escape.\@_)->true() ne Dotiac::DTL::devar_raw(\$varb$id,\$vars,\$escape.\@_)->true()) {\n";
	$id = $self->{true}->perlprint($fh,$id+1,$level+1,@_);
	if ($self->{false}) {
		print $fh "\t" x $level,"} else {\n";
		$id = $self->{false}->perlprint($fh,$id+1,$level+1,@_);
	}
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh "\t" x $level,"if (Dotiac::DTL::devar_raw(\$vara$id,\$vars,\$escape.\@_)->true() ne Dotiac::DTL::devar_raw(\$varb$id,\$vars,\$escape.\@_)->true()) {\n";
	$id = $self->{true}->perlstring($fh,$id+1,$level+1,@_);
	if ($self->{false}) {
		print $fh "\t" x $level,"} else {\n";
		$id = $self->{false}->perlstring($fh,$id+1,$level+1,@_);
	}
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{true}->perleval($fh,$id+1,@_);
	$id=$self->{false}->perleval($fh,$id+1,@_) if $self->{false};
	$self->{n}->perleval($fh,$id+1,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{true}->perlcount($id+1,@_);
	$id=$self->{false}->perlcount($id+1,@_) if $self->{false};
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

Dotiac::DTL::Tag::ifnotequal - The {% ifnotequal VARIABLE1 VARIABLE2 %} tag

=head1 SYNOPSIS

	{% ifnotequal post.date comment.date %}
		{{ comment.date|timesice:post.date }} ago
	{% else %}
		At the same time
	{% endifnotequal %}

=head1 DESCRIPTION

Compoares two variables, and if they are NOT equal, the content is rendered. 

If they are not equal and an optional {% else %} block is found, that block is rendered.

=head1 BUGS AND DIFFERENCES TO DJANGO

If given an array or hash, it will only compare the length (like perl does), since there is no default array or hash comparision (This will change with perl6)

So you can write:

	{% ifnotequal loop 3 %}
		Loop has not three elements
	{% endifnotequal %}

But to stay compatible with Django, you should write:

	{% ifnotequal loop|length 3 %}
		Loop has not three elements
	{% endifnotequal %}

If you want to compare the content, use this:

	{% ifnotequal loop|stringformat:"s" otherloop|stringformat:"s" %}
		...
	{% endifqual %}

B<Warning: This might be quite slow>, that's why it isn't default.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

#if.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#if.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::if;
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
	$self->{true}=$obj->parse($data,$pos,\$found,"else","endif");
	if ($found eq "else") {
		$self->{false}=$obj->parse($data,$pos,\$found,"endif");
	}
	my @cond=Dotiac::DTL::get_variables($name);
	my $c="cond";
	foreach my $e (@cond) {
		if ($e eq "and") {
			$c="cond";
		}
		elsif ($e eq "or") {
			$self->{"or"}=1;
			$c="cond";
		}
		elsif ($e eq "not") {
			$c="not";
		}
		else {
			push @{$self->{$c}},$e;
			$c="cond";
		}
	}
	bless $self,$class;
	return $self;
}
sub print {
	#TODO Keep the order
	my $self=shift;
	print $self->{p};
	if ($self->{"or"}) {
		foreach my $e (@{$self->{"cond"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			if ($r) {
				$self->{true}->print(@_);
				$self->{n}->print(@_);
				return
			}
		}
		foreach my $e (@{$self->{"not"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			if (not $r) {
				$self->{true}->print(@_);
				$self->{n}->print(@_);
				return
			}
		}
		$self->{false}->print(@_) if $self->{false};
		$self->{n}->print(@_);
	}
	else {
		#die "'".join("','",@{$self->{"cond"}})."'\n";
		foreach my $e (@{$self->{"cond"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			if (not $r) {
				$self->{false}->print(@_) if $self->{false};
				$self->{n}->print(@_);
				return;
			}
		}
		foreach my $e (@{$self->{"not"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			if ($r) {
				$self->{false}->print(@_) if $self->{false};
				$self->{n}->print(@_);
				return;
			}
		}
		$self->{true}->print(@_);
		$self->{n}->print(@_);
	}
	
}
sub string {
	my $self=shift;
	if ($self->{"or"}) {
		foreach my $e (@{$self->{"cond"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			return $self->{p}.$self->{true}->string(@_).$self->{n}->string(@_) if $r;
		}
		foreach my $e (@{$self->{"not"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			return $self->{p}.$self->{true}->string(@_).$self->{n}->string(@_) if not $r;
		}
		return $self->{p}.($self->{false}?$self->{false}->string(@_):"").$self->{n}->string(@_);
	}
	else {
		foreach my $e (@{$self->{"cond"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			return $self->{p}.($self->{false}?$self->{false}->string(@_):"").$self->{n}->string(@_) if not $r;
		}
		foreach my $e (@{$self->{"not"}}) {
			my $r=Dotiac::DTL::devar_raw($e,@_)->true();
			return $self->{p}.($self->{false}?$self->{false}->string(@_):"").$self->{n}->string(@_) if $r;
		}
		return $self->{p}.$self->{true}->string(@_).$self->{n}->string(@_);
	}
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{cond}],["\$cond$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{"not"}],["\$not$id"]));
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
	print $fh "\t" x $level,"if (",join(($self->{"or"}?" or ":" and "),(map {"Dotiac::DTL::devar_raw(\$cond$id"."->[$_],\$vars,\$escape.\@_)->true()"} (0 .. $#{$self->{"cond"}})),map {"not Dotiac::DTL::devar_raw(\$not$id"."->[$_],\$vars,\$escape.\@_)->true()"} (0 .. $#{$self->{"not"}})),") {\n";
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
	print $fh "\t" x $level,"if (",join(($self->{"or"}?" or ":" and "),(map {"Dotiac::DTL::devar_raw(\$cond$id"."->[$_],\$vars,\$escape.\@_)->true()"} (0 .. $#{$self->{"cond"}})),map {"not Dotiac::DTL::devar_raw(\$not$id"."->[$_],\$vars,\$escape.\@_)->true()"} (0 .. $#{$self->{"not"}})),") {\n";
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

Dotiac::DTL::Tag::if - The {% if [not ]VARIABLE1[ or [not ]VARIABLE2[ or ..]]|[and [not ]VARIABLE2[ and ..]] %} tag

=head1 SYNOPSIS

Template file:

	{% if var %}
		var is true
	{% endif %}
	{% if var %}
		var is true
	{% else %}
		var is not true
	{% endif %}
	{% if not var %}
		var is not true
	{% endif %}
	{% if not var %}
		var is not true
	{% else %}
		var is true
	{% endif %}
	{% if var and var2 and not var3 %}
		....
	{% endif %}
	{% if var or not var2 or var3 %}
		....
	{% endif %}


=head1 DESCRIPTION

Conditional rendering of templates, everything between {% if .. %} and {% else %} or {% endif %} is only rendered if the condition in the if clause is true.

The part between {% else %} and {% endif %}, if exists, is only rendered if the condition is false.

=head2 The condition

You can link conditions with either "and" or "or", but not both (there is a problem with precedence), you have to use two {% if %}'s for that.

	{% if var1 %}
		{% if var2 or var3 %}
			This would be the same as "if var1 and (var2 or var3)", if that would work.
		{% endif %}
	{% endif %}

You can negate a variable in any case with a "not" before it:
	
	{% if not var %}
		....
	{% endif %}
	
	{% if not var1 and var2 and not var3 %}
		...
	{% endif %}
	{% if not var1 or var2 or not var3 %}
		...
	{% endif %}

=head2 False values

False is: 

	0 # The number 0
	0.00 # The number 0.0
	"" # An empty string
	"0" # A string containing a null
	undef # A null value
	{} # An empty hash
	[] # An empty list/array
	An unknown variable, even if $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID is set to something true.
	not "a true value" # not negates true to false and false to true.

=head2 True values

Everything else is true, including references to objects which are false and references to empty strings.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 BUGS AND DIFFERENCES TO DJANGO

If you find any, please report them

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut


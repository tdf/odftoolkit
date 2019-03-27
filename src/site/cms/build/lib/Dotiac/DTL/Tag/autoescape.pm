#autoescape.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#autoescape.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################



package Dotiac::DTL::Tag::autoescape;
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
	$name="" unless $name;
	$self->{content}=$obj->parse($data,$pos,\$found,"endautoescape");
	$self->{escape}=0 if $name =~ /off/i;
	$self->{escape}=1 if $name =~ /on/i;
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $vars=shift;
	my $escape=shift;
	if (defined $self->{escape}) {
		$self->{content}->print($vars,$self->{escape},@_);
	}
	else {
		$self->{content}->print($vars,$escape,@_);
	}
	$self->{n}->print($vars,$escape,@_);
}
sub string {
	my $self=shift;
	my $vars=shift;
	my $escape=shift;
	my $r="";
	if (defined $self->{escape}) {
		$r=$self->{content}->string($vars,$self->{escape},@_);
	}
	else {
		$r=$self->{content}->string($vars,$escape,@_);
	}
	return $self->{p}.$r.$self->{n}->string($vars,$escape,@_);
	
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
	$id = $self->{content}->perlinit($fh,$id+1,@_);
	return $self->{n}->perlinit($fh,$id+1,@_)
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	if (defined $self->{escape}) {
		print $fh "\t" x $level,"{\n";
		$level++;
		print $fh "\t" x $level,"my \$escape=",$self->{escape},";\n";
	}
	$id = $self->{content}->perlprint($fh,$id+1,$level,@_);
	if (defined $self->{escape}) {
		$level--;
		print $fh "\t" x $level,"}\n";
		
	}
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	if (defined $self->{escape}) {
		print $fh "\t" x $level,"{\n";
		$level++;
		print $fh "\t" x $level,"my \$escape=",$self->{escape},";\n";
	}
	$id = $self->{content}->perlstring($fh,$id+1,$level,@_);
	if (defined $self->{escape}) {
		$level--;
		print $fh "\t" x $level,"}\n";
		
	}
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{content}->perleval($fh,$id+1,@_);
	$self->{n}->perleval($fh,$id+1,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{content}->perlcount($id+1,@_);
	return $self->{n}->perlcount($id+1);
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
sub eval {
	my $self=shift;
	$self->{content}->eval(@_);
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::autoescape - The {% autoescape [on/off] %} tag

=head1 SYNOPSIS

Template file:

	{% autoescape on %}
		This variable will be HTML escaped: {{ "&'\"" }}
	{% endautoescape %}
	{% autoescape off %}
		This variable will NOT be HTML escaped: {{ "&'\"" }}
	{% endautoescape %}

=head1 DESCRIPTION

Controls the autoescape behavior of an area. 

=head2 Parameter:

=head3 [on/off]

Optional parameter:

=head4 on

Autoescaping is on for that whole area till {% endautoescape %}

=head4 off

Autoescaping is on for that whole area till {% endautoescape %}

=head4 [default]

Defaults to no change at all.

=head1 BUGS AND DIFFERENCES TO DJANGO

=head2 autoescape in extend without a block

This won't work around blocks in an extend:

	{% extend "main.html %}
	{% autoescape off %}
		{% block content %}
		This variable will be HTML escaped: {{ "&'\"" }}, even if there is an autoescape tag setting it off around it.
		{% endblock content %}
	{% endautoescape %}

If you but the autoescape tags into the block, it will work:

	{% extend "main.html %}
	{% block content %}
		{% autoescape off %}
		This variable will NOT be HTML escaped: {{ "&'\"" }}
		{% endautoescape %}
	{% endblock content %}

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

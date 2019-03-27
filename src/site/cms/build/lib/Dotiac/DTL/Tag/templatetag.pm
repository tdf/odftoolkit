#templatetag.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#templatetag.pm is published under the terms of the MIT license, which 
#basically means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL::Tag::templatetag;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

my %tags=(
	openblock=>"{%",
	closeblock=>"%}",
	openvariable=>"{{",
	closevariable=>"}}",
	openbrace=>"{",
	closebrace=>"}",
	opencomment=>"{#",
	closecomment=>"#}"
);

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	my $name=shift;
	die "Unknown templatetag \"\"" unless $tags{$name};
	$self->{out}=$tags{$name};
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p},$self->{out};	
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	return $self->{p}.$self->{out}.$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t" x $level,"print \"$self->{out}\";\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh "\t" x $level,"\$r.=\"$self->{out}\";\n";
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

Dotiac::DTL::Tag::templatetag - The {% templatetag openblock|closeblock|openvariable|closevariable|openbrace|closebrace|opencomment|closecomment %} tag

=head1 SYNOPSIS

Template file:

	{% templatetag openblock %} templatetag {% templatetag closeblock %}
	{% templatetag openvariable %} var1 {% templatetag closevariable %}
	{% templatetag openbrace %} somebrace {% templatetag closebrace %}
	{% templatetag opencomment %} no comment {% templatetag closecomment %}

This will result in:

	{% templatetag %}
	{{ var1 }}
	{ somebrace }
	{# no comment #}

=head1 DESCRIPTION

Inserts the special tags used by Django Templates into the rendered output.

=head2 The tags

These are the tags which can be used.

=over

=item openblock

The tag that opens a tag.

	{%

=item closeblock

The tag that closes a tag.

	%}

=item openvariable

The tag that opens a variable.

	{{

=item closevariable

The tag that closes a variable.

	}}

=item openbrace

This is not needed in this implementation, since you can include braces in the template, if they are not followed by another {, % or #.

	{

=item closebrace

Also not needed in this implementation.

	}

=item opencomment

The tag that starts a comment.

	{%

=item closecomment

The tag that ends a comment.

	%}

=back

=head1 BUGS AND DIFFERENCES TO DJANGO

If you find any, please let me know.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

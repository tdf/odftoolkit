###############################################################################
#Template.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Template.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Template;
require Dotiac::DTL::Core;
require Dotiac::DTL::Addon;

our $VERSION = 0.8;

use strict;
use warnings;
use Carp;
 
sub new {
	my $class=shift;
	my $self={};
	bless $self,$class;
	$self->{vars}={};
	$self->{first}=shift;
	$self->{currentdir}=shift(@_) || $Dotiac::DTL::CURRENTDIR;
	$self->{parser}=shift(@_) || $Dotiac::DTL::PARSER;
	if (@_) {
		#$self->{params}=[keys %{shift(@_)}];
		$self->{params}=[grep {substr($_,0,1) ne "`"} sort keys %{shift(@_)}];
	}
	else {
		#$self->{params}=[keys %Dotiac::DTL::params];
		$self->{params}=[grep {substr($_,0,1) ne "`"} sort keys %Dotiac::DTL::params];
	}
	return $self;
}

sub param {
	my $self=shift;
	my $name="";
	return @{$self->{params}} unless @_;
	while (@_) {
		$name=shift;
		$self->{vars}->{$name}=shift if @_;
	}
	return $self->{vars}->{$name} if $name;
	
}

sub string {
	my $self=shift;
	my $vars=shift || {};	
	%Dotiac::DTL::blocks=();
	%Dotiac::DTL::cycle=();
	%Dotiac::DTL::globals=();
	$Dotiac::DTL::currentdir=$self->{currentdir};
	my $p=$Dotiac::DTL::PARSER;
	$Dotiac::DTL::PARSER=$self->{parser};
	my $ret="";
	eval {
		$ret=$self->{first}->string({%{$self->{vars}},%{$vars}},$Dotiac::DTL::AUTOESCAPING);
		1;
	} or do {
		croak "Error while rendering output to string\n $@\n.";
		undef $@;
	};
	Dotiac::DTL::Addon::restore();
	$Dotiac::DTL::PARSER=$p;
	return $ret;
}

sub render {
	my $self=shift;
	return $self->string(@_);
}
sub output {
	my $self=shift;
	return $self->string(@_);
}

sub print {
	my $self=shift;
	my $vars=shift || {};
	%Dotiac::DTL::blocks=();
	%Dotiac::DTL::cycle=();
	%Dotiac::DTL::globals=();
	$Dotiac::DTL::currentdir=$self->{currentdir};
	my $p=$Dotiac::DTL::PARSER;
	$Dotiac::DTL::PARSER=$self->{parser};
	eval {
		$self->{first}->print({%{$self->{vars}},%{$vars}},$Dotiac::DTL::AUTOESCAPING);
		1;
	} or do {
		croak "Error while printing output\n $@\n.";
		undef $@;
	};
	Dotiac::DTL::Addon::restore();
	$Dotiac::DTL::PARSER=$p;
	return;
}

1;

__END__

=head1 NAME

Dotiac::DTL::Template - A Dotiac/Django template.

=head1 SYNOPSIS

	require Dotiac::DTL;
	$t=Dotiac::DTL->new("file.html")
	$t->print();

=head2 Static methods

=head3 new(FILE) or new(FILE,COMPILE)

Creates a new empty Dotiac::DTL::Template, don't use this, use Dotiac::DTL->new(FILE,COMPILE).

=head2 Methods

=head3 param(NAME, VALUE)

Works like HTML::Templates param() method, will set a param that will be used for output generation.

	my $t=Dotiac::DTL->new("file.html");
	$t->param(FOO=>"bar");
	$t->print();
	#Its the same as:
	my $t=Dotiac::DTL->new("file.html");
	$t->print({FOO=>"bar"});

=over

=item NAME

Name of the parameter.

=item VALUE

Value to set the parameter to.

=back

Returns the value of the param NAME if VALUE is skipped.

=head3 string(HASHREF)

Returns the templates output.

=over

=item HASHREF

Parameters to give to the template. See Variables below.

=back

=head3 output(HASHREF) and render(HASHREF)

Same as string(HASHREF) just for HTML::Template and Django syntax.

=head3 print(HASHREF) 

You can think of these two being equal:

	print $t->string(HASHREF);
	$t->print(HASHREF);

But string() can cause a lot of memory to be used (on large templates), so print() will print to the default output handle as soon as it has some data, which uses a lot less memory.

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

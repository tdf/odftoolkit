###############################################################################
#Value.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Value.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Value;
use strict;
use warnings;
require Scalar::Util;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $data=shift;
	my $safe=shift;
	$safe=0 unless $safe;
	$safe=1 if $safe;
	my $r=Scalar::Util::reftype($data);
	$r="" unless $r;
	my $self=[$data,$safe,$r,Scalar::Util::blessed($data),($r?0:Scalar::Util::looks_like_number($data))];
	bless $self,$class;
	return $self;

}

sub safe {
	my $self=shift;
	return Dotiac::DTL::Value->new(shift(@_),1) unless ref $self;
	if (@_ and $_[0]) {
		$self->[1]=1;
	}
	return $self->[1];
}

sub escape {
	my $self=shift;
	return Dotiac::DTL::Value->new(shift(@_),0) unless ref $self;
	if (@_ and $_[0]) {
		$self->[1]=0;
	}
	return !$self->[1];
}

sub array {
	return $_[0]->[2] eq "ARRAY"
}

sub hash {
	return $_[0]->[2] eq "HASH"
}

sub object {
	return !(!$_[0]->[3]);
}

sub number {
	return $_[0]->[4];
}

sub undef {
	return !defined $_[0]->[0];
}

sub defined {
	return defined $_[0]->[0];
}

sub scalar {
	return !$_[0]->[2] && defined $_[0]->[0];
}

sub true {
	return "" unless $_[0]->[0];
	return $_[0]->[0] unless $_[0]->[2];
	return $_[0]->[0]->count() if $_[0]->[3] and $_[0]->[0]->can("count");
	return 1 if $_[0]->[3];
	# return scalar @{$_[0]->[0]} if $_[0]->[2] eq "ARRAY";
	# return scalar keys %{$_[0]->[0]} if $_[0]->[2] eq "HASH";
	return 1;
}

sub get {
	return $_[0]->[0];
}

sub content {
	return $_[0]->[0];
}

#Recursive string rendering
sub str {
	my $data=shift;
	my $level=shift;
	return unless $level;
	if (not defined $data) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID;
	}
	if (not ref $data) {
		return $data;
	}
	if (Scalar::Util::blessed($data)) {
		return $data->string() if $data->can("string");
		return $data->__str__() if $data->can("__str__");
		return $data->ToString() if $data->can("ToString");
		return $data->repr() if $data->can("repr");
		return $data->__repr__() if $data->can("__repr__");
		return $data;
	}
	if (Scalar::Util::reftype($data) eq "HASH" and $level > 1) {
		return join(" ",map {str($data->{$_},$level-1)} sort keys %{$data});
	}
	if (Scalar::Util::reftype($data) eq "ARRAY" and $level > 1) {
		return join(" ",map {str($_,$level-1)} @{$data});
	}
	return $data;
}

sub string {
	my $self=shift;
	my $data=$self->[0];
	my $value="Error";
	if ($self->undef()) {
		$value=$Dotiac::DTL::TEMPLATE_STRING_IF_INVALID;
		$self->[1]=1;
	}
	elsif ($self->scalar()) {
		$value=$data;
	}
	elsif ($self->object and $data->can("string")) {
		$value=$data->string();
	}
	elsif ($self->object and $data->can("__str__")) {
		return $data->__str__();
	}
	elsif ($self->object and $data->can("ToString")) {
		return $data->ToString();
	}
	elsif ($self->object and $data->can("repr")) {
		return $data->repr();
	}
	elsif ($self->object and $data->can("__repr__")) {
		return $data->__repr__();
	}
	elsif ($self->array) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID unless $Dotiac::DTL::Max_Depth;
		$value=join(" ",map {str($_,$Dotiac::DTL::Max_Depth)} @{$data});
	}
	elsif ($self->hash) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID unless $Dotiac::DTL::Max_Depth;
		$value=join(" ",map {str($data->{$_},$Dotiac::DTL::Max_Depth)} sort keys  %{$data});
	}
	unless ($self->[1]) {
		$value=~s/&/&amp;/g;
		$value=~s/</&lt;/g;
		$value=~s/>/&gt;/g;
		$value=~s/\"/&quot;/g;
		$value=~s/\'/&#39;/g;
	}
	return $value;
}


#Recursive repr rendering
sub rep {
	my $data=shift;
	my $level=shift;
	return unless $level;
	if (not defined $data) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID;
	}
	if (not ref $data) {
		return $data;
	}
	if (Scalar::Util::blessed($data)) {
		return $data->string() if $data->can("string");
		return $data->__str__() if $data->can("__str__");
		return $data->ToString() if $data->can("ToString");
		return $data->repr() if $data->can("repr");
		return $data->__repr__() if $data->can("__repr__");
		return $data;
	}
	if (Scalar::Util::reftype($data) eq "HASH" and $level > 1) {
		return join(" ",map {rep($data->{$_},$level-1)} sort keys %{$data});
	}
	if (Scalar::Util::reftype($data) eq "ARRAY" and $level > 1) {
		return join(" ",map {rep($_,$level-1)} @{$data});
	}
	return $data;
}

sub repr {
	my $self=shift;
	my $data=$self->[0];
	if ($self->undef()) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID;
	}
	elsif ($self->scalar()) {
		return $data;
	}
	elsif ($self->object and $data->can("string")) {
		return $data->string();
	}
	elsif ($self->object and $data->can("__str__")) {
		return $data->__str__();
	}
	elsif ($self->object and $data->can("ToString")) {
		return $data->ToString();
	}
	elsif ($self->object and $data->can("repr")) {
		return $data->repr();
	}
	elsif ($self->object and $data->can("__repr__")) {
		return $data->__repr__();
	}
	elsif ($self->array) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID unless $Dotiac::DTL::Max_Depth;
		return join(" ",map {rep($_,$Dotiac::DTL::Max_Depth)} @{$data});
	}
	elsif ($self->hash) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID unless $Dotiac::DTL::Max_Depth;
		return join(" ",map {rep($data->{$_},$Dotiac::DTL::Max_Depth)} sort keys %{$data});
	}
	return $data;
}


sub pyrep {
	my $data=shift;
	my $level=shift;
	return unless $level;
	if (not defined $data) {
		return "null";
	}
	if (not ref $data) {
		return $data if Scalar::Util::looks_like_number($data);
		return '"'.$data.'"';
	}
	if (Scalar::Util::blessed($data)) {
		return $data->repr() if $data->can("repr");
		return $data->__repr__() if $data->can("__repr__");
		return $data->string() if $data->can("string");
		return $data->__str__() if $data->can("__str__");
		return $data->ToString() if $data->can("ToString");
		return $data;
	}
	if (Scalar::Util::reftype($data) eq "HASH" and $level > 1) {
		return "(".join(", ",map {"$_:".pyrep($data->{$_},$level-1)} sort keys %{$data}).")";
	}
	if (Scalar::Util::reftype($data) eq "ARRAY" and $level > 1) {
		return "(".join(", ",map {pyrep($_,$level-1)} @{$data}).")";
	}
	return $data;
}

sub pyrepr {
	my $self=shift;
	my $data=$self->[0];
	if ($self->undef()) {
		return "null";
	}
	elsif ($self->number()) {
		return $data;
	}
	elsif ($self->scalar()) {
		return '"'.$data.'"';
	}	
	elsif ($self->object and $data->can("repr")) {
		return $data->repr();
	}
	elsif ($self->object and $data->can("__repr__")) {
		return $data->__repr__();
	}
	elsif ($self->object and $data->can("string")) {
		return $data->string();
	}
	elsif ($self->object and $data->can("__str__")) {
		return $data->__str__();
	}
	elsif ($self->object and $data->can("ToString")) {
		return $data->ToString();
	}
	elsif ($self->array) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID unless $Dotiac::DTL::Max_Depth;
		return "(".join(", ",map {pyrep($_,$Dotiac::DTL::Max_Depth)} @{$data}).")";
	}
	elsif ($self->hash) {
		return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID unless $Dotiac::DTL::Max_Depth;
		return "(".join(", ",map {"$_:".pyrep($data->{$_},$Dotiac::DTL::Max_Depth)} sort keys %{$data}).")";
	}
	return $data;
}


sub stringnodefault {
	my $self=shift;
	if ($self->undef) {
		return undef;
	}
	return $self->string();
}

sub set {
	my $self=shift;
	my $data=shift;
	$self->[0]=$data;
	my $r=Scalar::Util::reftype($data);
	$r="" unless $r;
	$self->[2]=$r;
	$self->[3]=Scalar::Util::blessed($data);
	$self->[4]=($r?0:Scalar::Util::looks_like_number($data));
	return $self;
}

1;

__END__

=head1 NAME

Dotiac::DTL::Value - Saves Dotiac::DTL-value.

=head1 SYNOPSIS

	my $v=Dajango::Template::Value->safe($data);
	if ($v->array) {
	}
	if ($v->number) {
	}
	print $v->content();

=head1 DESCRIPTION

Stores a value in an object, for use in filters. This marks the value safe for output or needs escaping.

=head2 Creation

=head3 new(VALUE, SAFE)

Creates a new Dajango::Template::Value with the contents VALUE. It will be marked as safe for output if SAFE is set to true.

	$value=Dajango::Template::Value->new($data,!$autoescape);

=head3 safe(VALUE) 

Creates a new Dajango::Template::Value with the contents VALUE and marks it as safe for output.

	$value=Dajango::Template::Value->safe($data);

=head3 escape(VALUE) 

Creates a new Dajango::Template::Value with the contents VALUE and marks it for escaping during output.

	$value=Dajango::Template::Value->escape($data);

=head2 Object methods.

These can be called on the created Dajango::Template::Value objects:

=head3 safe()

Returns true if this Dajango::Template::Value is safe for output

	if ($value->safe) {
		...
	}

=head3 safe(1)

Marks this Dajango::Template::Value as safe without changing its contents.

	if ($time > 1)  { #Or something
		$value->safe(1);
	}

=head3 escape()

Returns true if this Dajango::Template::Value needs escaping for output

	if ($value->escape) {
		...
	}

=head3 escape(1)

Marks this Dajango::Template::Value as unsafe without changing its contents.

	if ($time < 1)  { #Or something
		$value->escape(1);
	}

=head3 array()

Returns true if the contained value is an array.

	if ($value->array) {
		foreach (@{$value->content}) {
			...
		}
	}

=head3 hash()

Returns true if the contained value is a hash.

	if ($value->hash) {
		foreach (keys %{$value->content}) {
			...
		}
	}

=head3 object()

Returns true if the contained value is an object.

	if ($value->object) {
		die "Can't work with blessed variables";
	}

=head3 number()

Returns true if the contained value is a number.

	if ($value->number) {
		$value->set($value->content*20);
	}

=head3 undef()

Returns true if the contained value is not defined.

	if ($value->undef) {
		return Dotiac::DTL::Value->safe("Error");
	}

=head3 defined()

Returns true if the contained value is defined.

	if ($value->defined) {
		return $value;
	}
	return Dotiac::DTL::Value->safe("No Text found");

=head3 scalar()

Returns true if the contained value is defined and not a reference.

	if ($value->scalar) {
		return Dotiac::DTL::Value->safe(lc $value->content);
	}

=head3 true(OBJECT)

Returns a true Value if the object is true or filled (for array and hashrefs) and "" otherwise.

It will return the size of the referenced container of an arrayref or hashref.

It will also try to run the count() method of an object, if available.

Should be about the same result as L<Dotiac::DTL::Core>'s Dotiac::DTL::Conditional($var->content);

=head3 content()

Returns the contained value.

	print $value->content;

=head3 get()

Returns the contained value.

	print $value->get;


=head3 string()

Returns the contained value in a readable way and escapes the output if it's marked as unsafe.

Calls the string() method on a contained object that provides one.

	print $value->string;

=head3 repr()

Same as string()

Returns the contained value in a readable way, but don't escapes, even if the content is unsafe.

Calls the string() or the repr() method on a contained object that provides one of them.

This is useful if you need to escape the value yourself.

	print CGI::escape($value->repr()); #URLencode

=head3 pyrepr()

Same as repr(), but calls the repr() method on a contained object before it calls string(); 

=head3 stringnodefault()

Same as string(), but returns undef if the content is not defined.

=head3 set(VAR)

Changes the contained value without changing the safe/escape status.

If you want to apply some functions on the value, regardless of content or escape/safe status, use set() and repr():

	$value->set(uc $value->repr()); #Applies uc() on the content of $value, but doesn't change the output mode.

Returns itself, so you can write (in a filter):

	return $value->set("Foo") if $foo;


=head3 pyrep

Rekursive generator for pyrepr.

=head3 rep

Rekursive generator for repr.

=head3 str

Rekursive generator for string.

=head1 BUGS

If you find a bug, please report it.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut


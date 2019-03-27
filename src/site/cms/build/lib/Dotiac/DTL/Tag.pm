###############################################################################
#Tag.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Addon.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag;
#Default Tags
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
}
sub print {
	print $_[0]->{p};
}
sub string {
	return $_[0]->{p};
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{p}],["\$text$id"]));
	return $id;
	
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	use Carp;confess unless int $level;
	print $fh "\t" x $level,"print \$text$id;\n";
	return $id;
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	print $fh "\t" x $level,"\$r.=\$text$id;\n";
	return $id;
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	#return $self->{n}->perleval(@_) if $self->{n};
	return $id;
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	return $id;
}
sub eval {
	my $self=shift;
	$self->{n}->eval(@_) if $self->{n};
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag - Terminates a template list

=head1 DESCRIPTION

The Dotiac::DTL::Tag is always the last element in a template list which just contains some text.

It also serves as a base class for all the other tags. 

=head2 Included Tags

=over 

=item L<Dotiac::DTL::Tag::autoescape>

Changes autoescaping for an area.

=item L<Dotiac::DTL::Tag::block>

Defines a block with a name to be overwritten by {% extends %}.

=item L<Dotiac::DTL::Tag::comment>

Ignores everything between {% comment %} and {% endcomment %}.

=item L<Dotiac::DTL::Tag::cycle>

Alternate between values in {% for %} or normal text. Useful for different rowcolors in a table.

=item L<Dotiac::DTL::Tag::debug>

Prints out a some debugging information.

=item L<Dotiac::DTL::Tag::extends>

Includes another template and overwrites named blocks.

=item L<Dotiac::DTL::Tag::filter>

Apply filters to everything from {% filter %} to {% endfilter %}.

=item L<Dotiac::DTL::Tag::firstof>

Selects the first valid value.

=item L<Dotiac::DTL::Tag::for>

Iterate over datastructures.

=item L<Dotiac::DTL::Tag::if>

Conditional output based on true or false.

=item L<Dotiac::DTL::Tag::ifchanged>

Print only if output or a variable are changed.

=item L<Dotiac::DTL::Tag::ifequal>

Conditional output if some values are equal.

=item L<Dotiac::DTL::Tag::ifnotequal>

Conditional output if some values are not equal.

=item L<Dotiac::DTL::Tag::include>

Include other templates.

=item L<Dotiac::DTL::Tag::load>

Load additional filters, tags or locales.

=item L<Dotiac::DTL::Tag::now>

Output the current time in a specified format.

=item L<Dotiac::DTL::Tag::regroup>

Group lists of hashes or objects by a property.

=item L<Dotiac::DTL::Tag::spaceless>

Remove whitespace between HTML Tags.

=item L<Dotiac::DTL::Tag::ssi>

Include unsafe files from anywhere.

=item L<Dotiac::DTL::Tag::templatetag>

Output special chars like {{, {%, {#, #}, %} or }}.

=item L<Dotiac::DTL::Tag::widthratio>

Calculate a width for bars and such.

=item L<Dotiac::DTL::Tag::with>

Change variable names and apply filters just once.

=back

=head2 How to write your own tags

A tag is basically a simple perl module that provides 12 methods and should inherit from Dotiac::DTL::Tag (this module).

The tags are just a list, the last tag in the list is always a Dotiac::DTL::Tag (this module).

Every tag with an endtag has a list of tags it surronds, also ending with a Dotiac::DTL::Tag.

	  _______         _______         _______         _______         _______
	 /       \       /       \       /       \       /       \       |       |
	 | load  |------>| {{ }} |------>|  for  |------>| {{ }} |------>|  Tag  |
	 \_______/       \_______/       \_______/       \_______/       |_______|
	                                     |
	                                     |
					  ___V___
					 /       \
					 | {{ }} |
					 \_______/
	                                     |
	                                     |
					  ___V___
					 |       |
					 |  Tag  |
					 |_______|


Here is a small example of a tag that does nothing:

	package Dotiac::DTL::Tag::mytag; #The tag will be called {% mytag %}
	use base qw/Dotiac::DTL::Tag/; #You don't need this, but it is highly recomended
	use strict;
	use warnings;
	sub new {
		my $class=shift;
		my $self={p=>shift()}; # First argument to new (after the classname) is the text that came before it. This has to be called "p" if you want to use Dotiac::DTL::Tag's methods.
		bless $self,$class;
		return $self;
	}
	sub next {
		my $self=shift;
		$self->{n}=shift; #The next tag in tag list.
	}
	sub print {
		my $self=shift;
		print $self->{p}; #print the text that came before me.
		$self->{n}->print(@_); #Call print on the next tag, ALWAYS give @_ to it.
	}
	sub string {
		my $self=shift;
		return $self->{p}.$self->{n}->string(@_); #Just the same thing as print() does only return it. Don't forget @_.
		
	}
	sub eval {
		my $self=shift;
		$self->{n}->eval(@_); #ALWAYS give @_ to it.
	}
	#Starting here are the compiler methods, you won't need these, but they are quite useful to have for faster templates.
	#Don't forget, this module might not be loaded when the compiled template runs. You have to save all your variables.
	sub perl {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		$self->SUPER::perl($fh,$id,@_); #Use Dotiac::DTL::Tag's perl() method for the $self->{p} text.
		return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
		return $id;
	}
	sub perlprint {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->SUPER::perlprint($fh,$id,$level,@_); #Use Dotiac::DTL::Tag's perlprint() method so you don't need to care about the $self->{p} text.
		return $self->{n}->perlprint($fh,$id+1,$level,@_);
	}
	sub perlstring {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->SUPER::perlstring($fh,$id,$level,@_); #Use Dotiac::DTL::Tag's perlstring() method so you don't need to care about the $self->{p} text.
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

=head3 Normal method reference:

Here are what those methods should do: (All methods get either the class or the object as the first argument)

=head4 $class->new ( PRETEXT, ARGUMENTS, PARSEROBJ, PARSERTEXT, PARSERPOSITION )

Creates a new tag-object.

B<Parameter>

=over

=item PRETEXT

The text the parser found before it found this tag

Take this example:

	Hello {% mytag %}

The PRETEXT is then "Hello "

This should always be saved in $self->{p}, since Dotiac::DTL::Tag expects it there.

=item ARGUMENTS

The text that is in the tag, without the tagname:

	Hello {% mytag foo bar %}

In that case ARGUMENTS is going to be "foo bar".

If those are variables, use Dotiac::DTL::get_variables to parse them:

	my @a = Dotiac::DTL::get_variables('"foo\"" bar'); #@a=('`foo"`','bar').

That returns a list of variables to give to Dotiac::DTL::devar and friends.

If you have barewords you can also use get_variables:

	{% save var1 var2 with stuff in foo %}
	my @a = Dotiac::DTL::get_variables($arguments); #@a=("var1","var2","with","stuff","in","foo"). # This is not what you want.
	my %b = Dotiac::DTL::get_variables($arguments,qw/with in/); #%b=(""=>["var1","var2"],with=>["stuff"],in=>["foo"]). # THIS is what you want.

=item PARSEROBJ

Contains the current parser, which supports $PARSEROBJ->parse($PARSERTEXT,$PARSERPOSITION,[FOUND,ENDTAG,ENDTAG...]) and $PARSEROBJ->unparsed($PARSERTEXT,$PARSERPOSITION,[FOUND,STARTTAG,ENDTAG,ENDTAG...]) (See L<Dotiac::DTL::Core>)

Parse till next endtag:

	my $found="";
	$self->{content}=$obj->parse($PARSERTEXT,$PARSERPOSITION,\$found,"endmytag","else");
	if ($found eq "else") { # $found contains the name of the tag found.
		$self->{else}=$obj->parse($data,$pos,\$found,"endmytag");
	}

=item PARSERTEXT

Contains a reference to the current text. You won't ever need this unless you are giving it to $PARSEROBJ->parse or $PARSEROBJ->unparsed

=item PARSERPOSITION

Contains a reference to the current text. You won't ever need this unless you are giving it to $PARSEROBJ->parse or $PARSEROBJ->unparsed

=back

=head4 $obj->next ( TAG )

This method is called by the parser (void context) to save the next tag.

Just save this in $self->{n} or somewhere else and all is well

=head4 $obj->string ( VARS, AUTOESCAPESTATUS, SOME_MORE_STUFF )

This method should return the rendered text of this tags and all the next ones.

This is easier done than said...

	sub string {
		my $self=shift;
		my $vars=shift;
		my $escape=shift;
		my $r="Some text";
		#.. Do something to fill $r. Modify $vars and $escape if needed.
		# And if this tag has some content: (/endtag)
		# $r.=$self->{content}->string($vars,$escape,@_); 
		return $self->{p}.$r.$self->{n}->string($vars,$escape,@_); #NEVER forget @_ here.
	}


B<Parameter>

All parameters should be given the next string()/print() call.

=over

=item VARS

A reference to a hash containing all the variables given by calling $template->print({var=>...}) or $template->string({var=>...}).

A tag can modify this if it needs.

	$vars=shift;
	$vars->{foo=>"1"}; # {{ foo }} will now render to 1

B<Beware, if you want the new variable only be valid in this tags content you will have to copy it>

	$vars2={%{$vars}};

=item AUTOESCAPESTATUS

This is either "1" (on) or "0" (off) and controls the autoescaping.

=item SOME_MORE_STUFF

Contains nothing for now, but it might in the future.

Always give @_ to the next tags (and content tags) print() or string(). 

=back

=head4 $obj->print ( VARS, AUTOESCAPESTATUS, SOME_MORE_STUFF )

About the same as string, but should just print the content it generates.

This is easier done than said...

	sub string {
		my $self=shift;
		my $vars=shift;
		my $escape=shift;
		print $self->{p}; # Printing the PRETEXT (see new())
		#print something:
		# print "Hello World";
		# And if this tag has some content: (/endtag)
		# $self->{content}->print($vars,$escape,@_); 
		$self->{n}->print($vars,$escape,@_); #Call print() or the next tag. NEVER forget @_ here.
	}


B<Parameter>

All parameters should be given the next string()/print() call.

=over

=item VARS

A reference to a hash containing all the variables given by calling $template->print({var=>...}) or $template->string({var=>...}).

A tag can modify this if it needs.

	$vars=shift;
	$vars->{foo=>"1"}; # {{ foo }} will now render to 1

B<Beware, if you want the new variable only be valid in this tags content you will have to copy it>

	$vars2={%{$vars}};

=item AUTOESCAPESTATUS

This is either "1" (on) or "0" (off) and controls the autoescaping.

=item SOME_MORE_STUFF

Contains nothing for now, but it might in the future.

Always give @_ to the next tags (and content tags) print() or string(). 

=back

=head4 $obj->eval ( VARS, AUTOESCAPESTATUS, SOME_MORE_STUFF )

This is called to set block tags.

If this tag changes variables or the autoescape stuff, you will have to code a special an eval() and perleval() method, otherwise just copy this one.

This should be enough for starters:

	sub eval {
		my $self=shift;
		$self->{n}->eval(@_);
	}

B<Parameter>

All parameters should be given the next string()/print() call.

=over

=item VARS

A reference to a hash containing all the variables given by calling $template->print({var=>...}) or $template->string({var=>...}).

=item AUTOESCAPESTATUS

This is either "1" (on) or "0" (off) and controls the autoescaping.

=item SOME_MORE_STUFF

Contains nothing for now, but it might in the future.

Always give @_ to the next tags (and content tags) eval().

=back

=head3 Methods for the compiler.

The compiler is quite simple, it works just as the renderer only it creates another type of output (perl code).

Almost all methods involved have the same arguments/parameter, so they are explained once here.

B<Parameter>

=over

=item FILEHANDLE

The output must be printed into this filehandle.

=item ID

This is for unique variable names. It must be increased by one for the next tag. And if you have to render content it has also to be counted along.

	sub perl {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		$id=$self->{content}->perl($fh,$id+1,@_); #Id will be increased.
		return $self->{n}->perl($fh,$id+1,@_); #Always write this.
	}

B<Every method must handle/increase the ID excatly the same>

=item LEVEL

Level of indentation:

	print $fh "\t" x $level,"content;\n";

I<Not perl(), perlinit() and perlcount()>

=item MD5 

MD5 sum of the file. Used by block/cycle/ifchanged tags to save their global content.

=item ALL OTHER

All other arguments must also be transported. Just use @_ for that.

=back

All methods have to return the ID of last tag:

	sub perl {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		return $self->{n}->perl($fh,$id+1,@_); #Always write this.
	}

=head4 perl ( FILEHANDLE, ID, MD5, ... )

This is the first method called, it should save the variables the tag needs (using id to make them unique)

To save the PRETEXT ($self->{p} you can use SUPER::perl, it will be saved as "\$text$id".

This easy using Data::Dumper, which already loaded:

	sub perl {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		$self->SUPER::perl($fh,$id,@_); #Save the \$text$id variable.
		print $fh "my ";
		print $fh Data::Dumper->Dump([$self->{var}],["\$var$id"]);
		# $id = $self->{content}->perl($fh,$id+1,@_); # When this tag has content.
		return $self->{n}->perl($fh,$id+1,@_)
	}

=head4 perlinit ( FILEHANDLE, ID, MD5, ... )

This is called after perl() and useful if you want to make sure a module is loaded or work with other tags variables. Those are now all there.

Most tags won't need this and can just write:

	sub perlinit {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		# $id = $self->{content}->perlinit($fh,$id+1,@_); #with content...
		return $self->{n}->perlinit($fh,$id+1,@_)
	}

=head4 perlcount ( ID )

This is used to skip ahead with it ID-numbering scheme, without any output generated. This won't be called from the included tags or the parser.

This should be enough for those tags, you have to adapt it for your content of course:

	sub perlcount {
		my $self=shift;
		my $id=shift;
		# $id=$self->{content}->perlcount($id+1,@_); #One line for each content variable you have saved.
		return $self->{n}->perlcount($id+1);
	}

=head4 perlprint ( FILEHANDLE, ID, LEVEL, MD5 )

This generates the output that C<print>'s the rendered content.

There are two variables already defined, C<$vars> and C<$escape> which contain the same stuff as in print().

sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	# 
	# The Code goes here
	#
	# If you have content and want to get it's code, use this (increase level)
	# print $fh "\t" x $level,"if (...) {\n;" #or whatever you want with this code.
	# $id = $self->{content}->perlprint($fh,$id+1,$level+1,@_); 
	# print $fh "\t" x $level,"}\n;"
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}

=head4 perlstring ( FILEHANDLE, ID, LEVEL, MD5 )

Same as perlprint(), but instead of C<print> you have to append to the variable $r:

	print $fh "\t" x $level,"\$r.=\"Hello World\";\n";

=head4 perleval ( FILEHANDLE, ID, LEVEL, MD5 )

Same as perlprint() and perlstring(), but here goes the code that is run on eval.

If you change variables or the autoescape stuff, you will have to code a special an eval() and perleval() method, otherwise just copy this one.

	sub perleval {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		# $id = $self->{content}->perleval($fh,$id+1,@_); # If you have content...
		return $self->{n}->perleval($fh,$id+1,@_)
	}

=head2 Some example tags

These are all quite useless, but they show how to do it.

=head3 The {% variable %} tag. 

This just renders a variable it gets, much like {{ variable }}, but less optimized...

Almost all tags need to call Dotiac::DTL::devar to get variable contents. You should also ALWAYS give @_ to devar, just in case in the future it is needed.

	{% variable "variable" %} {# puts "variable" into the output #}
	{% variable var %} {# puts the content of var into the output #}

	package Dotiac::DTL::Tag::variable;
	use base qw/Dotiac::DTL::Tag/;
	use strict;
	use warnings;

	sub new {
		my $class=shift;
		my $self={p=>shift()};
		my $name=shift;
		my ($var,undef)=Dotiac::DTL::get_variables($name); #We just care about the first, the other get ignored
		die "This tag needs a variable" unless $var; # Die when it is not there.
		$self->{var}=$var; #Save it.
		bless $self,$class;
		return $self;
	}
	sub next {
		my $self=shift;
		$self->{n}=shift;
	}
	sub print {
		my $self=shift;
		print $self->{p};
		my $vars=shift; #We also could just have used @_.
		print Dotiac::DTL::devar($self->{var},$vars,@_);
		$self->{n}->print($vars,@_);
	}
	sub string {
		my $self=shift;
		my $vars=shift;
		return $self->{p}.Dotiac::DTL::devar($self->{var},$vars,@_).$self->{n}->string($vars,@_); #About the same as print()
		
	}
	sub eval {
		my $self=shift;
		$self->{n}->eval(@_); #Nothing to do here, we don't change anything.
	}
	sub perl {
		my $self=shift;
		my $fh=shift;
		my $id=shift; 
		$self->SUPER::perl($fh,$id,@_); #Call Dotiac::DTL::Tag's stuff to save $self->{p};
		print $fh "my "; #My is needed, since all compiled templates contain a use strict;
		print $fh Data::Dumper->Dump([$self->{var}],["\$var$id"]);
		return $self->{n}->perl($fh,$id+1,@_)
	}
	sub perlinit {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		return $self->{n}->perlinit($fh,$id+1,@_)
	}
	sub perlprint {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->SUPER::perlprint($fh,$id,$level,@_); #Let Dotiac::DTL::Tag's take care of $self->{p};
		print $fh "\t" x $level,"print Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";
		return $self->{n}->perlprint($fh,$id+1,$level,@_);
	}
	sub perlstring { 
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->SUPER::perlstring($fh,$id,$level,@_); #Let Dotiac::DTL::Tag's take care of $self->{p};
		print $fh "\t" x $level,"\$r.=Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";
		return $self->{n}->perlprint($fh,$id+1,$level,@_);
	}
	sub perleval { #Still nothing to do here, we don't change anything.
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->{n}->perleval($fh,$id+1,$level,@_);
	}
	sub perlcount {
		my $self=shift;
		my $id=shift;
		$id=$self->{content}->perlcount($id+1,@_);
		return $self->{n}->perlcount($id+1);
	}
	
=head3 The {% double %}-Tag: Renders everything two times.

This shows nicely how to do endtags and controlstructures.

	var=>"Joe";

	{% double %}Hello, {{ var }} {% enddouble %} {# Hello, Joe Hello, Joe #}

	package Dotiac::DTL::Tag::double;
	use base qw/Dotiac::DTL::Tag/;
	use strict;
	use warnings;

	sub new {
		my $class=shift;
		my $self={p=>shift()};
		my $obj=shift;
		my $data=shift;
		my $pos=shift;
		my $found="";
		$self->{content}=$obj->parse($data,$pos,\$found,"enddouble");
		bless $self,$class;
		return $self;
	}
	sub next {
		my $self=shift;
		$self->{n}=shift;
	}
	sub print {
		my $self=shift;
		print $self->{p};
		$self->{content}->print(@_);
		$self->{content}->print(@_); #Just do it a second time.
		$self->{n}->print(@_);
	}
	sub string {
		my $self=shift;
		return $self->{p}.$self->{content}->string(@_).$self->{content}->string(@_).$self->{n}->string(@_);
		
	}
	sub eval {
		my $self=shift;
		$self->{content}->eval(@_); Don't forget to call eval on the content, it might contain blocks. Since it does no output and it is useless to set the blocks twice you need to call it only once.
		$self->{n}->eval(@_);
	}
	sub perl {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		$self->SUPER::perl($fh,$id,@_);
		$id = $self->{content}->perl($fh,$id+1,@_); #Don't forget $id here.
		return $self->{n}->perl($fh,$id+1,@_)
	}
	sub perlinit {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		$id = $self->{content}->perlinit($fh,$id+1,@_); #Don't forget $id here.
		return $self->{n}->perlinit($fh,$id+1,@_)
	}
	sub perlprint {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->SUPER::perlprint($fh,$id,$level,@_);
		# We can't just call $id = $self->{content}->perlprint($fh,$id+1,$level,@_); twice, it would blow up the code and destroy the ID scheme.
		print $fh "\t" x $level,"for my \$double (0 .. 1){\n"; #Don't need \$double$id since it is limited in scope. 
		$id = $self->{content}->perlprint($fh,$id+1,$level+1,@_); #$level+1 will indent this nicely
		print $fh "\t" x $level,"}\n";
		return $self->{n}->perlprint($fh,$id+1,$level,@_);
	}
	sub perlstring { #Same as perlprint, only calls perlstring() .
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$self->SUPER::perlstring($fh,$id,$level,@_);
		print $fh "\t" x $level,"for my \$double (0 .. 1){\n";
		$id = $self->{content}->perlstring($fh,$id+1,$level+1,@_);
		print $fh "\t" x $level,"}\n";
		return $self->{n}->perlstring($fh,$id+1,$level,@_);
	}
	sub perleval {
		my $self=shift;
		my $fh=shift;
		my $id=shift;
		my $level=shift;
		$id=$self->{content}->perleval($fh,$id+1,$level,@_);#Don't forget $id here.
		$self->{n}->perleval($fh,$id+1,$level,@_);
	}
	sub perlcount {
		my $self=shift;
		my $id=shift;
		$id=$self->{content}->perlcount($id+1,@_);#Don't forget $id here.
		return $self->{n}->perlcount($id+1);
	}
	


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

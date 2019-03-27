###############################################################################
#Comment.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Comment.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL::Compiled;
use strict;
use warnings;
our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $name=shift;
	my $self={};
	$self->{name}=$name;
	bless $self,$class;
	eval {
		#{
		no strict 'refs';
		#warn  Data::Dumper->Dump([${"$name"."::params"}]) if ${"$name"."::params"} and %{${"$name"."::params"}};
		%Dotiac::DTL::params=%{${"$name"."::params"}} if ${"$name"."::params"};
	};
	return $self;
}

sub print {
	my $self=shift;
	my $name=$self->{name};
	{
		no strict "refs";
		"${name}::print"->(@_);
	}
}

sub string {
	my $self=shift;
	my $name=$self->{name};
	{
		no strict "refs";
		"${name}::string"->(@_);
	}
}
sub perl {
	die "Can't perlify a compiled template";
}
sub eval {
	my $self=shift;
	my $name=$self->{name};
	{
		no strict "refs";
		"${name}::eval"->(@_);
	}
}
1;
__END__

=head1 NAME

Dotiac::DTL::Compiled - Stores a compiled Django template.

=head1 SYNOPSIS

Autocompiling:

	require Dotiac::DTL;
	$t=Dotiac::DTL->newandcompile("file.html");
	#file.html is now compiled to file.html.pm, $t is still the original version, unless it was already compiled.
	$t=Dotiac::DTL->new("file.html");
	#$t is now the compiled version.

Other compiled templates:

	package MyTemplate;
	sub print {
		my ($vars,$escape)=(shift(),shift());
		print "There are ".keys(%$vars)." parameters registered and x is $vars->{x}\n";
	}
	sub string {
		my ($vars,$escape)=(shift(),shift());
		return "There are ".keys(%$vars)." parameters registered and x is $vars->{x}\n";
	}
	sub eval {
		#nothing for now.
	}
	package main;
	require Dotiac::DTL;
	my $mytemplate=Dotiac::DTL->compiled("MyTemplate");
	# now you can use $mytemplate as a normal template.
	$mytemplate->print({x=>123});
	# This doesn't seem quite useful you could easily just write the code here, until you do this:
	my $templatedata="{% for x in array %}{% include template %}{% endfor %}";
	my $t = Dotiac::DTL->new(\$templatedata); #File templates work just as well.
	$t->print({array=>[1..100],template=>$mytemplate);
	# This will now include and print the above package a hundert times and 
	# will be a lot faster, depending on the contents of that for loop.

This also works with L<Dotiac::DTL::Reduced>;

=head1 DESCRIPTION

Compiled templates are just perl modules that act like templates, those are a lot faster than normal templates,
but more difficult to write.

=head2 Pros and cons

=head3 Pros

=over

=item Faster code execution.

=item Faster parsing, perl is a faster parser than anything written in perl.

=item Easier on the memory.

=item A lot less memory and time consuption by using Dotiac::DTL::Reduced and only compiled templates.

=item Autocompiler produces perl code out of templates. new() will automatically grab the compiled version if its ther, unless new("file.html",-1) is used.

=item Easy way to insert perl code in specific templates parts without having to write your own tags for it.

=item Autocompiler detects changed template files and recompiles. This can be prevented by using new("file.html",0)

=item Yes, you can include uncompiled templates in compiled ones (not using Dotiac::DTL::Reduced ) and vice versa

=item And, yes you also can extend uncompiled templates with compiled ones and compiled templates with uncompiled ones. This was quite a hassle actually.

=back

=head3 Cons

=over

=item To use the autocompiler the running script needs write access to the template directory

=item Writing your own tags gets a lot harder because of the autocompiler.

=item The autocompiler produces rather big files, some times 20x the size of the template it compiled. This lessens if less tags and more text are used.

=item There is now way back from autocompiled templates to normal ones. If you loose the source, it's gone.

=back

=head2 Removing cons

Some issues can be removed by compiling all the templates once and then just run the webserver with tight security, i.e. no write access.

This compiles all templates ending with .html in the current folder:

	require Dotiac::DTL;
	Dotiac::DTL->newandcompile($_) foreach (<*.html>);

=head2 Own Packages as templates

You can use other packages as templates with:

	Dotiac::DTL->compiled("packagename");

These packages must provide these three methods:

=head3 print($vars,$escape,@_)

This is called when print() is called somewhere above this template.

	sub print {
		my $vars=shift; #Reference to variable/parameter hash
		my $escape=shift; #Autoescape status: 0 off, 1 on
		print Dotiac::DTL::devar("somevar",$vars,$escape,@_) # Don't forget @_ here. for now it contains nothing, but it might in the future.
	}

=head3 string($vars,$escape,@_)

Similar to print(), is called when string() is called on the template and returns the data.

print() and string() should always produce the same data.

	sub string {
		my $vars=shift; #Reference to variable/parameter hash
		my $escape=shift; #Autoescape status: 0 off, 1 on
		print Dotiac::DTL::devar("somevar",$vars,$escape,@_) # Don't forget @_ here. for now it contains nothing, but it might in the future.
	}

=head3 eval($vars,$esacpe,@_)

This is only called when this template is included and the including template (or one including that or ...) has an {% extends %} tag.

This is only used to set blocks for extend.

	sub eval {
		my $sub = sub {
			return "Hello World"
		}
		my $subref=\$sub;
		unshift @{$Dotiac::DTL::blocks{"myblockname"}},$subref;
		unshift @{$Dotiac::DTL::blocks{"someotherblock"}},Dotiac::DTL->new("file.html")->{first};
	}

There should not be many occaisions where this is needed.

=head1 BUGS

There might be some bugs/problems in the autocompiled templates that won't appear in normal mode.

The autocompiled perl code is definitely not the most optimized and best solution.

The memory-saving way of print() will be less effective if extends is involved and templates are autocompiled.
However it is still faster than uncompiled templates but uses a bit more memory. I just have no idea how to fix
it properly without producing even more code.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

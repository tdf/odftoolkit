###############################################################################
#DTL.pm
#Last Change: 2008-01-19
#Copyright (c) 2006 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#DTL.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL;
use base qw/Dotiac::DTL::Core/; #This is only used to make Test::Pod::Coverage, since most functions in the Dotiac::DTL namespace are documented in that file.
require Dotiac::DTL::Tag;
require Digest::MD5;
use Carp qw/confess/;
use strict;
use warnings;
use Exporter;
BEGIN { push our @ISA, "Exporter" }
require File::Spec;
require File::Basename;

our @EXPORT=();
our @EXPORT_OK=qw/Context Template *TEMPLATE_DIRS/;
our $VERSION = 0.8;


sub Template {
    no warnings;
	my $file=shift;
	if (-e $file) {
	}
	elsif (-e "$file.html") {
		$file="$file.html" 
	}
	elsif (-e "$file.txt") {
		$file="$file.txt" ;
	}
	else {
		foreach my $dir (@Dotiac::DTL::TEMPLATE_DIRS) {
			$file=File::Spec->catfile($dir,"$file.html") and last if -e File::Spec->catfile($dir,"$file.html");
			$file=File::Spec->catfile($dir,"$file.txt") and last if -e File::Spec->catfile($dir,"$file.txt");
			$file=File::Spec->catfile($dir,$file) and last if -e File::Spec->catfile($dir,$file);
		}
	}
	return Dotiac::DTL->new($file,@_) if -e $file;
	return Dotiac::DTL->new(\$file,@_);
}


sub Context {
	return $_[0];
}

my %cache;
sub newandcompile {
	my $class=shift;
	return $class->new(@_,1);
}

{
	no warnings "redefine";
	sub new {
		%Dotiac::DTL::params=();
		my $class=shift;
		my $data=shift; 
		my $t="";
		my $filename="from cache";
		my $changetime=0;
		my $compile=shift; #1 compile, 0 no recompile, -1 skip compiled even if its there, undef=use compiled if there, recompile if needed.
		if (ref $data eq "SCALAR") {
			$t=$$data;
			$compile=0;
			$filename="form SCALARref";
			$Dotiac::DTL::currentdir=$Dotiac::DTL::CURRENTDIR;
		}
		elsif (not ref $data) {
			$t=$data;
			my @f = File::Basename::fileparse($data);
			$Dotiac::DTL::currentdir=$f[1];
			#warn "Cached:",Data::Dumper->Dump([\%cache]);
			if (-e "$data.pm" and (($compile and $compile > 0) or not defined $compile)) {
				if (-e $data) {
					if ((stat("$data.pm"))[9] > (stat("$data"))[9]) {
						
						eval {
							$cache{"$data.pm"}={
								template=>Dotiac::DTL::Compiled->new("Dotiac::DTL::Compiled::".require "$data.pm"),
								currentdir=>$Dotiac::DTL::currentdir,
								params=>{%Dotiac::DTL::params},
								parser=>$Dotiac::DTL::PARSER,
								changetime=>(stat("$data.pm"))[9]
							} if not $cache{"$data.pm"};# or (exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} > -M "$data.pm");
							$t="$data.pm";
							$compile=0;
							1;
						} or do {
							carp "Error while getting compiled template $data.pm:\n $@\n.";
							undef $@;
						};
					}
					else {
						delete $cache{"$data.pm"};
						delete $INC{"$data.pm"}; #Otherwise it won't work.
						$compile=1 if $compile or not defined $compile;
					}
				}
				else { # $data is not more here, but $data.pm is, use that one than.
					if ($cache{"$data.pm"} and exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} < (stat("$data.pm"))[9]) {
						carp "Foo";
						delete $cache{"$data.pm"};
						delete $INC{"$data.pm"};
					}
					eval {
						$cache{"$data.pm"}={
							template=>Dotiac::DTL->compiled("Dotiac::DTL::Compiled::".require "$data.pm"),
							currentdir=>$Dotiac::DTL::currentdir,
							params=>{%Dotiac::DTL::params},
							parser=>$Dotiac::DTL::PARSER,
							changetime=>(stat("$data.pm"))[9]
						} if not $cache{"$data.pm"};# or (exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} > -M "$data.pm");
						$t="$data.pm";
						$compile=0;
						1;
					} or do {
						croak "Error while getting compiled template $data.pm and $data is gone:\n $@\n.";
						undef $@;
					};	
				}
			}
			if ($cache{$t} and $t eq $data and exists $cache{$t}->{changetime} and $cache{$t}->{changetime} < (stat("$t"))[9]) {
				delete $cache{$t};
			}
			unless ($cache{$t})  {	
				open my $fh,"<",$data or croak "Can't open template $data: $!";
				binmode $fh;
				my $a=do {local $/,<$fh>};
				close $fh;
				$filename="\"$data\"";
				$changetime=(stat("$data"))[9];
				$data=\$a;
			}
		}
		else {
			die "Can't work with $data!";
		}
		unless ($cache{$t}) {
			eval "require $Dotiac::DTL::PARSER;" or croak "Can't load Parser '$Dotiac::DTL::PARSER': $@";
			my $parser=$Dotiac::DTL::PARSER->new();
			$cache{$t}={
				template=>Dotiac::DTL::Tag->new("include/extend cycle detected"), #This prevents cycled includes to screw around during parsing time.
				currentdir=>$Dotiac::DTL::currentdir,
				params=>{%Dotiac::DTL::params},
				parser=>$Dotiac::DTL::PARSER
			};
			my $pos=0;
			eval {
				$cache{$t}={
					template=>$parser->parse($data,\$pos),
					currentdir=>$Dotiac::DTL::currentdir,
					params=>{%Dotiac::DTL::params},
					parser=>$Dotiac::DTL::PARSER,
					changetime=>$changetime
				};
				1;
			} or do {
				croak "Error while getting template $filename:\n $@\n.";
				undef $@;
			};
		}
		if ($compile and $compile > 0) {
			if (open my $cp,">","$t.pm") {
				eval {
					require Data::Dumper;
					$Data::Dumper::Indent=2;
					$Data::Dumper::Useqq=1;
					my $template=$cache{$t}->{template};
					my $digest=Digest::MD5::md5_hex($t);
					print $cp "#Autogenerated\n";
					print $cp "package Dotiac::DTL::Compiled::$digest;\nuse strict;\nuse warnings;\nrequire Scalar::Util;\n#PARAMS USED:\nour "; 

					print $cp (Data::Dumper->Dump([$cache{$t}->{params}],["\$params"]));
					$template->perl($cp,0,$digest);
					print $cp "\n#INIT\n";
					$template->perlinit($cp,0,$digest);
					print $cp "\nsub string {\n	my \$vars=shift;\n	my \$escape=shift;\n	my \$r=\"\";\n";
					$template->perlstring($cp,0,1,$digest);
					print $cp "	return \$r;\n}\n";
					print $cp "sub print {\n	my \$vars=shift;\n	my \$escape=shift;\n";
					$template->perlprint($cp,0,1,$digest);
					print $cp "}\n";
					print $cp "sub eval {\n	my \$vars=shift;\n	my \$escape=shift;\n";
					$template->perleval($cp,0,1,$digest);
					print $cp "}\n";
					print $cp qq("$digest";);
					close $cp;
					1;
				} or do {
					croak "Error while compiling template $filename:\n $@\n.";
					undef $@;
				};
			}
			else {
				carp "Can't open output to $$data.pm while compiling: $!";
			}
		}
		Dotiac::DTL::Addon::restore();
		return "Dotiac::DTL::Template"->new($cache{$t}->{template},$cache{$t}->{currentdir},$cache{$t}->{parser},$cache{$t}->{params});
	}
}
1;

__END__
=head1 NAME

Dotiac::DTL - Run Django Templates in Perl

=head1 SYNOPSIS

Template File: (file.html):

	Hello, my name is {{ my_name }}

Perl skript:

	require Dotiac::DTL;
	my $t=Dotiac::DTL::new("file.html");
	$t->print({name=>"adrian"});

Or maybe you want a string returned;

	require Dotiac::DTL;
	my $t=Dotiac::DTL::new("file.html");
	$t->string({name=>"adrian"});

Use it like HTML::Template:

	require Dotiac::DTL;
	my $t=Dotiac::DTL::new("file.html");
	$t->param(name=>"adrian");
	print $t->output();

Use it like Django:

	use Dotiac::DTL qw/Template Context/;
	my $t = Template("file");
	my $c = Context({my_name=>"Adrian"});
	print $t->render($c);


=head1 DESCRIPTION

This template system implements (almost) the same template language as the templates in the Django project L<http://www.djangoproject.com/> only for Perl.

If you don't know what the django template language is see L<http://docs.djangoproject.com/en/dev/topics/templates/> for a very good introduction, which is also valid for this implementation.

This is not supported by L<http://www.djangoproject.com/>, so please don't send your questions there, drop me a mail instead.

But if you ever going to program webapplications in python, go check it out.

This is just a quick overview over the features, for detailed information and internals look at L<Dotiac::DTL::Core>.

=head2 Exported Functions

=head3 Template(FILE, COMPILE)

Creates a template from FILE. This function is for Django like syntax, use new(FILE, COMPILE) for better results and control.

=over

=item FILE

This can be a filename or a string containing the template data.

Template() will search the current directory and @Dotiac::DTL::TEMPLATE_DIRS (See Core.pm) for either FILE, FILE.html or FILE.txt and open the first file found.

If no file is found it treats FILE as template data and will parse the string.

=item COMPILE

Controls if and when the template should be compiled.

See new(FILE, COMPILE)

=back

Returns a L<Dotiac::DTL::Template> object.

=head3 Context(HASHREF)

Python's Django uses Context() to create a Context, Dotiac::DTL doesn't use this, it just uses a hash.

=over

=item HASHREF

A Hash of parameters.

=back

Returns the first Argument.

=head2 Class constructers

=head3 new(FILENAME, COMPILE)

Creates a template or loads it from the cache.

=over

=item FILENAME

The filename of the template to open or a scalarref to parse:

	$t=Dotiac::DTL->new("file.html");
	$file="Hello World";
	$t=Dotiac::DTL->new(\$file);

Templates from scalarrefs are never compiled.

=item COMPILE

Dotiac::DTL can translate (compile) text templates to perl code (as FILENAME+".pm") for faster parsing, execution and less memory consumption.

See L<Dotiac::DTL::Compiled> on information on the autocompiler.

The parameter "COMPILE" controls how the template is compiled:

=over

=item undef (default)

Will use a compiled template if it is there and older than the uncompiled version, otherwise the normal one.

Will recompile the template if it was outdated. (original version younger than compiled one)

=item 1 (newandcompile)

Will compile the template if it is not compiled already.

Will recompile the template if it was outdated. (original version younger than compiled one)

Returns the uncompiled version if it has been compiled by new() and the compiled version if it was already compiled.

	unlink "file.html.pm"
	my $t=Dotiac::DTL->new("file.html",1); #$t is the uncompiled version.
	$t=Dotiac::DTL->new("file.html",1); #$t is now the compiled version.

=item 0 (no recompile)

Will use a compiled template if it is there and older than the uncompiled version, otherwise the normal one.

Will not ever recompile the compiled version, if its outdated, its outdated.

=item -1 (no compiled)

Will never use the compiled version even if it is there.

=back

If you want to use only compiled templates, see L<Dotiac::DTL::Reduced>, which skips the parser to save memory.

=back

Returns a L<Dotiac::DTL::Template> object.

=head3 newandcompile(FILENAME)

Same as new (FILENAME,1), which means: Compiles the template if it is not already compiled and recompiles if the compiled one is older.

Returns a L<Dotiac::DTL::Template> object.

=cut

=head2 Methods

I<These work only on the returned Dotiac::DTL::Template object of new()>

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

Same thing as string(HASHREF) just for HTML::Template and PyDjango syntax.

=head3 print(HASHREF) 

You can think of these two being equal:

	print $t->string(HASHREF);
	$t->print(HASHREF);

But string() can cause a lot of memory to be used (on large templates), so print() will print to the default output handle as soon as it has some data, which uses a lot less memory.

=over

=item HASHREF

Parameters to give to the template. See Variables below.

=back

Returns nothing.

=head3 compiled(PACKAGENAME) 

Treats PACKAGENAME as a compiled template. See L<Dotiac::DTL::Compiled>.

This is useful to insert perl code into templates.

Returns a Dotiac::DTL object

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

=head1 DTL INTRO

=head2 Comments

Everything in {# and #} will be ignored by the parser. There is also a comment-tag.

	Hello World {# This is a default text, TODO enter more text #}
	{% comment %}
		This is also a comment.
	{% endcomment %}

See L<Dotiac::DTL:Comment> and L<Dotiac::DTL::Tag::comment>.

=head2 Variables 

Variables are either perl datastructures/objects made to look like python style objects (case sensitive), or in "" or '' encased strings.

	$foo=new foo;
	$template->print({hash=>{text=>Foo},scalar=>"Hello World",array=>[1,2,3],object=>$foo});

Template:

	{{ scalar }} <!-- Hello World -->
	{% for loop in array reversed%}
		{{ forloop.counter:}} {{loop}} <!-- 1:3 2:2 3:1 -->
	{% endfor %}
	First is {{ array.0 }};
	{{ hash.text|escape }} <!-- Foo -->
	{{ "10"|add:"10" }} <!-- 20  --> 
	{{ object.member }} <!-- either gets $foo->{member}/$foo->[member] or calls $foo->member() if $Dotiac::DTL::ALLOW_METHOD_CALLS is true(default) -->

Everywhere you can use a variable, you can also use a static string in single or double quotes. And everywhere you can use a string, you can also use a variable, this includes filters:

	{% with "HelloXXX, World"|cut:"X" as helloworld %}
		{{ helloworld|lower }} {# Prints hello, world #}
		{% with "l" as L %}
			{{ hellowordl|cut:L }} {# Prints heo, word #}
		{% endwith %}
	{% endwith %}

See L<Dotiac::DTL::Variable> for more details and L<Dotiac::DTL::Tag::with> for the {% with %} tag.

Variables will be escaped for use in HTML. Which means {{ "<" }} will turn to "&lt;" during output.

If you want to prevent this. Use either the global Autoescaping value L<Dotiac::DTL::Core>, the autoescape tag L<Dotiac::DTL::Tag::autoescape> or the safe filter L<Dotiac::DTL::Filter>

String literals (" ... text ... ") are not going to be escaped, because the Django doesn't do it as well. See L<http://docs.djangoproject.com/en/dev/topics/templates/#string-literals-and-automatic-escaping> 

=head3 Note

Don't ever use }} or %} in strings, it might confuse the parser. But you can use \}\} or %\} instead.

=head2 Making your objects work better in Dotiac::DTL

Python has some default representations of objects, that perl lacks. But you can provide one, two or all of these three methods to make your object work in Dotiac::DTL as python object would work in Django:

=head3 string()

When the object is rendered in the output without a call to the member variable, Dotiac::DTL tries to call its string() method without arguments.

	my $t="{{ Object }}"
	$template=Dotiac::DTL->new(\$t);
	$template->print({
		Object=>new foo
	}); #Will print "foo=<address>"

But this way:

	package foo;
	....
	sub string() {
		return "foo&bar"
	}
	package main;
	my $t="{{ Object }}"
	$template=Dotiac::DTL->new(\$t);
	$template->print({
		Object=>new foo
	}); #Now it will print "foo&amp;bar"

=head3 repr()

Like string(), but it should print out the objects complete data. B<Not used yet>.

=head3 count()

This is used in if and if(not)equal.

As default, objects are always true and counted as one. This is not good, better to implement your own count() method:

	my $t="{% if emptyobject %}true{% else %}false{% endif %}"
	$template=Dotiac::DTL->new(\$t);
	$template->print({
		emptyobject=>new foo
	}); #Will print "true"

with your own:

	package foo;
	....
	sub count() {
		return 0;
	}
	package main;
	my $t="{% if emptyobject %}true{% else %}false{% endif %}"
	$template=Dotiac::DTL->new(\$t);
	$template->print({
		emptyobject=>new foo
	}); #Now it will print "false"

=head2 Differences

There are some differences with the original template implementation of Django:

I wrote this using just the documentation, so it will differ a lot on undokumented features, If you are missing something or notice something not listed here, drop me a mail.

One mayor difference is: Python has a default string representation for objects (__str__()), Perl doesn't. So if you writing an object into the template it will appear as a perl pointer. For a solution to this see above.

The perl side interface is quite different from the Python one:
	
	#Python: (from http://docs.djangoproject.com/en/dev/ref/templates/api/)
	from django.template import Context, Template
	t = Template("My name is {{ my_name }}.")
	c = Context({"my_name": "Adrian"})
	t.render(c)
	
This was to un-Perl for me, so this follows the HTML::Template way:

	require Dotiac::DTL;
	my $text="My name is {{ my_name }}.";
	my $t=Dotiac::DTL->new("file.html");
	#or
	my $t=Dotiac::DTL->new(\$text);
	$t->string({my_name=>"Adrian"});
	#or 
	$t->print({my_name=>"Adrian"});
	
There is also a Django-like interface.

	use Dotiac::DTL qw/Template Context/;
	my $t = Template("file"); #This also looks in @Dotiac::DTL::TEMPLATE_DIRS for file, file.html and file.txt if nothing exists, it treats file as a string.
	my $c = Context({my_name=>"Adrian"}); #This just returns the first argument.
	$t->render($c); #this equals $t->string($c);
	#of course:
	$t->print($c); #works just as well
	

=head3 Tag: load

The tag {% load %} will work, but do something else internally.

Look at L<Dotiac::DTL::Addon> and  L<Dotiac::DTL::Tag::load> for information.

The greatest difference is: 

In Django the load doesn't affect included templates, in Dotiac::DTL load is global, this means you can do this:

load.html:

	{% load lib1 lib2 lib3 someohterlibrary %}

template.html:

	{% include "load.html" %} {{ text|lib1filter }}

=head3 Adding filters and Tags

If you want create addons with filters and tags, look at L<Dotiac::DTL::Addon>, but if you just want to add filters and Tags for one skript, this can be done easily: 


You can add costum tags by create a module named Dotiac::DTL::Tag::Yourtag and simply "require-ing" it.
See L<Dotiac::DTL::Tag> for details

You can simply add filters by adding them to the L<Dotiac::DTL::Filter> namespace.

B<All parameters to filters are> L<Dotiac::DTL::Value>B<-objects and the filter needs to return one of those as well>

	package Dotiac::DTL::Filter;
	
	sub myjoin {
		my $value=shift;
		my @param=shift;
		return Dotiac::DTL::Value->escape(join($value->repr(),map {$_->repr()} @param)); # {{ ", "|myjoin:"Foo","Bar","Baz" }} = Foo, Bar, Baz
	}
	
	package main; #Your script here

=head3 Tag: url

This one can't work without a complete django backend. If you have such a backend you will have to overwrite the Dotiac::DTL::Tag::url module.

However, it tries to do the right thing.

=head3 Parsing

The parser will ignore many mistakes and syntax errors since I build it for speed. It will only stop if it can't make out what to do next:

Too many end tags.

Unclosed {{ }}, {% %} or {# #}

Some tags will also die() if the syntax is wrong. 

=head3 string() or print()

The normal Django templates only support a method that converts them into a long string. I added a print() method which prints directly to the current output handle. 
This should be easier on the memory and might even a bit faster.

=head3 More filter arguments

I thought it might be useful to allow filters to have multiple arguments, this is only useful for your own filters and some of the inofficial extension in this module. Arguments can be seperated by either a comma "," or a semicolon ";" (In some tags commas are used to split arguments, its better to always use ";")

	{{ "Hello"|center:"20","-" }}
	{% for x in  "Hello"|center:"20";"-"|make_list %}



=head3 Other changes:

There are some additional features to some tags and filters ( {% else %} in {% ifchanged %}, padding chars in |center |left and |right )

=head2 Speed

I have tried to make this as fast as I could, there are however some minor problems:

Filter arguments will be reparsed every time a {{ variable|cut:" "|escape }} is called. (cut:" " and escape will be parsed again)

The whole filter thing will be parsed every time a variable in a Tag {% regroup var|dictsort:"gender" by gender as new %} is called. (var|dictsort:"gender" will be parsed again)

These cases shouldn't happen that often.

I might add caching to prevent this.


Using {% extend "filename" %} or {% include "filename" %} is faster than using {% extend variable %} or {% include variable %} because the parsing of the included template will happen during parsing time, not evaluation time.

{% include variable %} in a for-loop will be cached if the variable doesn't change during the loop. If it changes, prepare for a extrem slowness.

See Dotiac::DTL::Compile for a solution to included templates containing perl as a variable and other speed stuff

=head2 Extension to Dotiac::DTL (own Filters, own Tags)

I don't like the way Django handles extensions to the template language, so I wrote some other way:

=head3 Filters

Just extend the Dotiac::DTL::Filter package:

	require Dotiac::DTL;
	package Dotiac::DTL::Filter;
	sub times {
		my $value=shift; #The value on which the filter is applied
		return $value unless defined $value; #$value might be undef, beware.
		my $param1=shift; #Rest of the parameters are in @_;
		return $value x $param1 if Scalar::Util::looks_like_number($param1);
		return $value; #param1 might be string.
	}
	package main;
	my $text='{% filter times:"4" %} H{% endfilter %}ello, {{ myvar|times:"3" }}';
	my $t=Dotiac::DTL->new(\$text);
	$t->print({myvar=>"World"}); #prints ' H H H Hello, WorldWorldWorld';

=head3 Tags

You will have to create a module named like your tag in the Dotiac::DTL::Tag:: namespace:

	package Dotiac::DTL::Tag::mytag;
	
	sub new(CONTENT) { ... };
	sub print(VARS,ESCAPE,SOMEMORE) { ... }
	sub string(VARS,ESCAPE,SOMEMORE) { ... }
	sub eval(VARS,ESCAPE,SOMEMORE) { ... }
	#To make the template compile you also need this:
	sub perl(FH,ID,DIGEST,SOMEMORE) { ... }
	sub perlcount(FH,ID,DIGEST,SOMEMORE) { ... }
	sub perlinit(FH,ID,DIGEST,SOMEMORE) { ... }
	sub perlstring(FH,ID,LEVEL,DIGEST,SOMEMORE) { ... }
	sub perlprint(FH,ID,LEVEL,DIGEST,SOMEMORE) { ... }
	sub perleval(FH,ID,LEVEL,DIGEST,SOMEMORE) { ... }
	
	package main;
	#....

See L<Dotiac::DTL::Tag> for a tutorial and what methods must be provided.

This is a lot of work, if you just want to use some perl code in one or two places it's easier just to use your own compiled templates and include them. See L<Dotiac::DTL::Compiled>.

=head1 REFERENCE

See L<Dotiac::DTL::Tag> for the built-in tags (Same as Django 1.1 Tags).

See L<Dotiac::DTL::Filter> for the built-in filters (Same as Django 1.1 Filters).

=head1 BUGS

This library is not threadsafe at all.

Please post any bugs and undokumented differences to Django you might find in the sourceforge tracker of Dotiac DTL:

L<http://sourceforge.net/tracker/?group_id=249411&atid=1126348>

I did not include "django.contrib.humanize", "django.contrib.markup" and "django.contrib.webdesign" in the Core distribution, since they require some other modules (especially markup). I will release them as Addons in CPAN.

=head1 LICENSE

This module is published under the terms of the MIT license, which basically 
means "Do with it whatever you want". For more information, see the 
LICENSE file that should be enclosed with this distributions. A copy of
the license is (at the time of writing) also available at
L<http://www.opensource.org/licenses/mit-license.php>.

=head1 LEGAL

Dotiac::DTL was built according to the documentation on L<http://docs.djangoproject.com/en/dev/ref/templates/builtins/>.

=head1 SEE ALSO

Complete Dotiac::DTL namespace.

L<http://www.djangoproject.com/> for the template language this module implements

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

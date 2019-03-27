###############################################################################
#Reduced.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Reduced.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Reduced;
use base qw/Dotiac::DTL::Core/;
use Exporter;
require File::Spec;

our @EXPORT=();
our @EXPORT_OK=qw/Context Template/;
our $VERSION = 0.8;

sub Template {
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
1;

__END__

=head1 NAME

Dotiac::DTL::Reduced - Dotiac::DTL without the parser.

=head1 SYNOPSIS

	require Dotiac::DTL::Reduced;
	$t=Dotiac::DTL->new("compiled.html") #Works only with compiled templates
	$t->print();

=head1 DESCRIPTION

Dotiac::DTL::Reduced is a version of Dotiac::DTL that contains everything needed to run compiled templates. The other stuff, i.e. parser and Tags are not loaded, so it should save some memory.

I recon it makes almost no difference at all with mod_perl or FastCGI, but having all the tag modules parsed for nothing will impact normal CGI performance.

See L<Dotiac::DTL::Compiled> for pros and cons of compiled templates.

B<Note> This will only run compiled templates, if your compiled template includes a normal template, it will die.

It will also create a warning when the template is outdated and needs to be recompiled.

=head2 Compiling templates

Since Dotiac::DTL::Reduced will only work with compiled templates, you can use this litte script to compile all templates in the current folder:

	require Dotiac::DTL; #Not Reduced here, we need the parser for this.
	Dotiac::DTL->newandcompile($_) foreach (<*.html>); #You might have to change this to whatever file extension you are using.

But see L<Dotiac::DTL::Compiled> before you do this to read up on compiled template pros and cons.

You will also have to recompile everytime you change something. The above script will only compile the changed files in that case.

=head2 Changes from the normal Dotiac::DTL

Everything discriped in L<Dotiac::DTL> and L<Dotiac::DTL::Core> still applies here except:

=head3 new(FILENAME)

Creates a template from a compiled version or loads it from the cache.

=over

=item FILENAME

The filename of the compiled template to open. If you give it a scalarref it will die.

	require Dotiac::DTL::Reduced;
	$t=Dotiac::DTL->new("file.html"); #Will load "file.html.pm" or die.

=item COMPILE

The COMPILE parameter from L<Dotiac::DTL>->new() is ignored. It wouldn't work anyway

=back

Returns a L<Dotiac::DTL::Template> object.

=head2 And what about my own compiled templates?

This works like it should: (See L<Dotiac::DTL::Compiled>)

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


=head3 Template(FILE)

Creates a template from FILE. This function is for Django like syntax, use new(FILE) for better results and control.

=over

=item FILE

This can be a filename or a string containing the template data.

Template() will search the current directory and @Dotiac::DTL::TEMPLATE_DIRS (See Core.pm) for either FILE, FILE.html or FILE.txt and open the first file found.

If no file is found it treats FILE as template data and will parse the string.

=back

=head3 Context(HASHREF)

Python's Django uses Context() to create a Context, Dotiac::DTL doesn't use this, it just uses a hash.

=over

=item HASHREF

A Hash of parameters.

=back

Returns the first Argument.

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

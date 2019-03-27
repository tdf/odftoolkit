###############################################################################
#unparsed.pm
#Last Change: 2009-02-09
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.2
####################
#This file is an addon to the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#unparsed.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL::Addon::markup;
use strict;
use warnings;
#use Text::Markdown;
use File::Temp qw/ :POSIX tempfile /;
#use Text::Restructured;
use IO::Socket;
#If it is not already loaded.
require Dotiac::DTL::Filter;
require Dotiac::DTL::Value;



our $VERSION=0.2;

my $oldmarkdown;
my $oldtextile;
my $oldrest;

sub import {
	no warnings qw/redefine/;
	$oldrest = *{Dotiac::DTL::Filter::restructuredtext};
	$oldmarkdown = *{Dotiac::DTL::Filter::markdown};
	$oldtextile = *{Dotiac::DTL::Filter::textile};
	*{Dotiac::DTL::Filter::restructuredtext}=\&restructuredtext;
	*{Dotiac::DTL::Filter::markdown}=\&markdown;
	*{Dotiac::DTL::Filter::textile}=\&textile;

}
sub unimport {
	no warnings qw/redefine/;
	*{Dotiac::DTL::Filter::restructuredtext} = $oldrest;
	*{Dotiac::DTL::Filter::markdown} = $oldmarkdown;
	*{Dotiac::DTL::Filter::textile} = $oldtextile;
}

sub markdown {
        my $val=shift;
        my $sock = IO::Socket::UNIX->new(
            Peer    => $ENV{MARKDOWN_SOCKET},
            Type    => SOCK_STREAM,
            Timeout => 30,
        ) or die "Can't open markdown socket: $!";

        print $sock $val->repr();
        shutdown $sock, 1;
        my $html = "";
        1 while sysread $sock, $html, 65536, length $html;
        return Dotiac::DTL::Value->safe($html);
}
sub textile {
	my $val=shift;
        require Text::Textile;
	return Dotiac::DTL::Value->safe(Text::Textile->new(charset => "utf8", char_encoding => 0)->process($val->repr()));
}
sub restructuredtext {
	return eval {
		no warnings qw/redefine/;
		my $w=$^W;
		$^W=0;
		require Text::Restructured::Writer;
		require Text::Restructured::DOM;
		require File::Temp; #Has to be installed by Dotiac::DTL
		my $writer = new Text::Restructured::Writer('html',{w=>'html',d=>0,D=>{}});
		my $value=shift;
		$value=$value->repr;
		my $dom;
		if ($value =~ /^<document/) {
			$dom = Text::Restructured::DOM::Parse($value, {w=>'html',d=>0,D=>{}});
		}
		else {
			require Text::Restructured;
			my $rst_parser = new Text::Restructured({w=>'html',d=>0,D=>{}}, "1 release 1");
			$dom = $rst_parser->Parse($value, tmpnam());
		}
		my $x=$writer->ProcessDOM($dom);
		$^W=$w;
		$x=substr $x,index($x,"<body>")+6;
		$x=substr $x,0,index($x,"<div class=\"footer\"");
		return Dotiac::DTL::Value->safe($x);
	} || die "restructuredtext processing failed: $@\n";
}

1;

__END__

=head1 NAME

Dotiac::DTL::Addon::markup: Filters to work with common markup languages

=head1 SYNOPSIS

Load from a Dotiac::DTL-template:

	{% load markup %}

Load in Perl file for all templates:

	use Dotiac::DTL::Addon::markup;

Then it can be used:

	{{ var|markdown }}
	{{ text|textile }}
	{{ content|restructuredtext }}

=head1 INSTALLATION

via CPAN:

	perl -MCPAN -e "install Dotiac::DTL::Addon::markup"

or get it from L<https://sourceforge.net/project/showfiles.php?group_id=249411&package_id=306751>, extract it and then run in the extracted folder:

	perl Makefile.PL
	make test
	make install

=head1 DESCRIPTION

This is like Django.contrib.markup, (L<http://docs.djangoproject.com/en/dev/ref/contrib/#ref-contrib-marku>), but for Dotiac::DTL and Perl.

It converts some of the common markup languages to HTML.

=head2 Filters

=head3 textile

Converts textile syntax to HTML.

Gives the content to Text::Textile and returns the results.

It will always return a safe string.

	my $text = <<EOT;
	h1. Heading

	A _simple_ demonstration of Textile markup.

	* One
	* Two
	* Three

	"More information":http://www.textism.com/tools/textile is available.
	EOT

	text=>$text;

In the template:

	{{ text|textile }}

This will render to:

	<h1>Heading</h1>

	<p>A <em>simple</em> demonstration of Textile markup.</p>

	<ul>
		<li>One</li>
		<li>Two</li>
		<li>Three</li>
	</ul>

	<p><a href="http://www.textism.com/tools/textile">More information</a> is available.</p>

	
Example from L<Text::Textile>.

=head3 markdown

Converts markdown syntax to HTML.

Gives the content to Text::Markdown and returns the results.

It will always return a safe string.

	my $text = <<EOM;
	A First Level Header
	====================

	A Second Level Header
	---------------------

	Now is the time for all good men to come to
	the aid of their country. This is just a
	regular paragraph.

	The quick brown fox jumped over the lazy
	dog's back.

	### Header 3

	> This is a blockquote.
	> 
	> This is the second paragraph in the blockquote.
	>
	> ## This is an H2 in a blockquote
	EOM

	text=>$text;

In the template:

	{{ text|markdown }}

This will render to:

	<h1>A First Level Header</h1>

	<h2>A Second Level Header</h2>

	<p>Now is the time for all good men to come to
	the aid of their country. This is just a
	regular paragraph.</p>

	<p>The quick brown fox jumped over the lazy
	dog's back.</p>

	<h3>Header 3</h3>

	<blockquote>
	    <p>This is a blockquote.</p>

	    <p>This is the second paragraph in the blockquote.</p>

	    <h2>This is an H2 in a blockquote</h2>
	</blockquote>
	
Example from L<http://daringfireball.net/projects/markdown/basics>

=head3 restructuredtext

Converts ReST syntax to HTML.

Gives the content to Text::Restructured and returns the results.

It will always return a safe string.

	my $text = <<EOR;
	=====
	Title
	=====
	Subtitle
	-------- 

	Titles are underlined (or over-
	and underlined) with a printing
	nonalphanumeric 7-bit ASCII
	character.
	
	- This is item 1
	- This is item 2 
	
	EOR

	text=>$text;

In the template:

	{{ text|markdown }}

This will render to:

	<font size="+2"><strong>Title</strong></font>
        <p><font size="+1"><strong>Subtitle</strong></font>
        </p><p>Titles are underlined (or over-
          and underlined) with a printing
          nonalphanumeric 7-bit ASCII
          character.</p>
	

	<ul>
          <li>This is item 1
          </li><li>This is item 2
          </li></ul>
	
Example from L<http://docutils.sourceforge.net/docs/user/rst/quickref.html>

=head1 BUGS

Since Text::Restructured won't compile under Win32, this can't be tested. Either it works or it won't.

Please report any bugs or feature requests to L<https://sourceforge.net/tracker2/?group_id=249411&atid=1126445>

=head1 SEE ALSO

L<Dotiac::DTL>, L<Dotiac::DTL::Addon>, L<http://www.dotiac.com>, L<http://www.djangoproject.com>

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

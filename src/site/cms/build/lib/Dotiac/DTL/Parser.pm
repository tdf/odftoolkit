###############################################################################
#Parser.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Parser.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Parser;
require Dotiac::DTL::Tag;
require Dotiac::DTL::Variable;
require Dotiac::DTL::Comment;
require Dotiac::DTL::Tag::autoescape;
require Dotiac::DTL::Tag::block;
require Dotiac::DTL::Tag::extends;
require Dotiac::DTL::Tag::comment;
require Dotiac::DTL::Tag::cycle;
require Dotiac::DTL::Tag::debug;
require Dotiac::DTL::Tag::filter;
require Dotiac::DTL::Tag::firstof;
require Dotiac::DTL::Tag::for;
require Dotiac::DTL::Tag::if;
require Dotiac::DTL::Tag::ifequal;
require Dotiac::DTL::Tag::ifnotequal;
require Dotiac::DTL::Tag::ifchanged;
require Dotiac::DTL::Tag::include;
require Dotiac::DTL::Tag::load;
require Dotiac::DTL::Tag::now;
require Dotiac::DTL::Tag::regroup;
require Dotiac::DTL::Tag::spaceless;
require Dotiac::DTL::Tag::ssi;
require Dotiac::DTL::Tag::templatetag;
require Dotiac::DTL::Tag::url;
require Dotiac::DTL::Tag::widthratio;
require Dotiac::DTL::Tag::with;

our $VERSION = 0.8;

use strict;
use warnings;

sub new {
	my $class=shift;
	my $self={};
	bless $self,$class;
	return $self;
}

sub unparsed {
	my $self=shift;
	my $template=shift;
	my $pos=shift;
	my $start=$$pos;
	my @end = @_;
	my $found;
	my $starttag;
	$found=shift @end if @end;
	$starttag=shift @end if @end;
	my @starttag;
	@starttag = ($starttag) if $starttag and not ref $starttag;
	@starttag = @{$starttag} if $starttag and ref $starttag eq "ARRAY";
	my $text;
	local $_;
	while (1) {
		my $p = index($$template,"{",$$pos);
		if ($p >=0) {
			$$pos=$p+1;
			my $n = substr $$template,$$pos,1;
			if ($n eq "%") {
				my $text .= substr $$template,$start,$$pos-$start-1;
				my $npos = index($$template,"%}",++$$pos);
				die "Missing closing %} at char $$pos" if $npos < 0;
				my $cont=substr $$template,$$pos,$npos-$$pos;
				$$pos=$npos+2;
				my $c=$cont;
				$cont=~s/^\s+//;
				$cont=~s/\s+$//;
				my ($tagname,$param) = split /\s+/,$cont,2;
				$tagname=lc $tagname;
				$$found = $c and return $text if $found and grep {$_ eq $tagname} @end;
				$text .= "{\%$c\%}";
				$text .= $self->unparsed($template,$pos,@_) if $found and grep {$_ eq $tagname} @starttag;
				$text .="{\%$$found\%}";
				$$found="";				
			}
		}
		else {
			$$pos=length $$template;
			return $text
		}
	}
}


sub parse {
	my $self=shift;
	my $template=shift;
	my $pos=shift;
	my $start=$$pos;
	my @end = @_;
	my $found;
	$found=shift @end if @end;
	local $_;
	while ($Dotiac::DTL::PARSER eq __PACKAGE__) {
		my $p = index($$template,"{",$$pos);
		if ($p >=0) {
			$$pos=$p+1;
			my $n = substr $$template,$$pos,1;
			if ($n eq "%") {
				my $pre = substr $$template,$start,$$pos-$start-1;
				my $npos = index($$template,"%}",++$$pos);
				die "Missing closing %} at char $$pos" if $npos < 0;
				my $cont=substr $$template,$$pos,$npos-$$pos;
				$$pos=$npos+2;
				$cont=~s/^\s+//;
				$cont=~s/\s+$//;
				my ($tagname,$param) = split /\s+/,$cont,2;
				$tagname=lc $tagname;
				$$found = $tagname and return Dotiac::DTL::Tag->new($pre) if $found and grep {$_ eq $tagname} @end;
				my $r;
				eval {$r="Dotiac::DTL::Tag::$tagname"->new($pre,$param,$self,$template,$pos);};
				if ($@) {
					die "Error while loading Tag '$tagname' from Dotiac::DTL::Tag::$tagname. If this is an endtag (like endif) then your template is unbalanced\n$@";
				}
				#print "\n\nold: $npos, new $$pos, lenght=".length $$template and die if $tagname eq "extends";
				#warn $$pos," ",length $$template,"\n";
				if ($$pos >= length $$template) {
					$r->next(Dotiac::DTL::Tag->new(""));
				}
				else {
					$r->next($self->parse($template,$pos,@_));
				}
				return $r;
				
			}
			elsif ($n eq "{") {
				my $pre = substr $$template,$start,$$pos-$start-1;
				my $npos = index($$template,"}}",++$$pos);
				die "Missing closing }} at char $$pos" if $npos < 0;
				my $cont=substr $$template,$$pos,$npos-$$pos;
				$$pos=$npos+2;
				return Dotiac::DTL::Variable->new($pre,$cont,$self->parse($template,$pos,@_));
			}
			elsif ($n eq "#") {
				my $pre = substr $$template,$start,$$pos-$start-1;
				my $npos = index($$template,"#}",++$$pos);
				die "Missing closing #} at char $$pos" if $npos < 0;
				my $cont=substr $$template,$$pos,$npos-$$pos;
				$$pos=$npos+2;
				return Dotiac::DTL::Comment->new($pre,$cont,$self->parse($template,$pos,@_));
			}
		}
		else {
			$$pos=length $$template;
			return Dotiac::DTL::Tag->new(substr $$template,$start);
		}
	}
	my $parser=$Dotiac::DTL::PARSER->new();
	my @args=($template,$pos);
	push @args,$found if $found;
	push @args,@end if @end;
	return $parser->parse(@args);
}

1;
__END__
=head1 NAME

Dotiac::DTL::Parser - The default Django/Dotiac parser

=head1 SYNOPSIS

	require Dotiac::DTL;
	$t=Dotiac::DTL->new("file.html")
	$t->print();

=head2 Static methods

=head3 new(FILE) or new(FILE,COMPILE)

Creates a new empty Dotiac::DTL::Parser, used by Dotiac::DTL->new().

=head2 Methods

=head3 parse(TEMPLATEREF,POSITIONREF, [FOUNDREF, List,of,endtags,to,look,for])

Parses the string referenced in TEMPLATEREF starting at the position referenced in POSITIONREF.

Returns the parsed templatedata if either one of the endtags is found (and sets FOUNDREF to the endtag) or the end of the string is reached.

This is used by tags to look for their endtag. For example the ifequal tag:

	sub new {
		my $class=shift;
		my $self={p=>shift()}; #Text that came before the tag.
		my $data=shift; #Content of the tag other than the name.
		my $obj=shift;
		my $data=shift; #Templatedataref
		my $pos=shift;  #and positionref from the parse() calling this tags new().
		my $found=""; #Empty found
		$self->{true}=$obj->parse($data,$pos,\$found,"else","endifequal"); #Search for either "else" or "endifequal" and set it to $found.
		if ($found eq "else") {
			$self->{false}=$obj->parse($data,$pos,\$found,"endifequal"); #If "else" was found, search for "endifequal"
		}
		($self->{var1},$self->{var2},undef)=Dotiac::DTL::get_variables($data);
		bless $self,$class;
		return $self;
	}
	#....

=head4 Note

Dotiac::DTL::Reduced doesn't support this, so it doesn't have to load all the tags or the parser.

=head3 unparsed(TEMPLATEREF,POSITIONREF, [FOUNDREF, STARTTAG, List,of,endtags,to,look,for])

Parses the string referenced in TEMPLATEREF starting at the position referenced in POSITIONREF.

Returns the unparsed templatedata if either one of the endtags is found (and sets FOUNDREF to the endtag) or the end of the string is reached.

Skips STARTTAG occurences and searches for additional endtags

This is used by tags to look for their endtag. For example an unparsed tag:

	sub new {
		my $class=shift;
		my $self={p=>shift()}; #Text that came before the tag.
		my $data=shift; #Content of the tag other than the name.
		my $obj=shift;
		my $data=shift; #Templatedataref
		my $pos=shift;  #and positionref from the parse() calling this tags new().
		my $found=""; #Empty found
		$self->{content}=$obj->unparsed($data,$pos,\$found,"unparsed","endunparsed");
		bless $self,$class;
		return $self;
	}
	#....

=head4 Note

There is no internal tag for now that needs this. But you might find some addons.

I planned this for a addon like Calypso DTL's {% ajax %} tag, that throws the unparsed template at DojoxDTL to render it in the browser.

Dotiac::DTL::Reduced doesn't support this either.

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

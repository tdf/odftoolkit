###############################################################################
#Variable.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Variable.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Variable;
use strict;
use warnings;
use base qw/Dotiac::DTL::Tag/;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	my $name=shift();
	$name=~s/^\s+//;
	$name=~s/\s+$//;
#	$name=~s/"((?>(?:(?>[^"\\]+)|\\.)*))"/Dotiac::DTL::escap($1)/eg;
#	$name=~s/'((?>(?:(?>[^'\\]+)|\\.)*))'/Dotiac::DTL::escap($1)/eg;
	my @name=Dotiac::DTL::get_variables($name);
	my @data= split/\|/,$name[0];
	$self->{name}=shift(@data);
	$self->{filters}=[@data];
	$self->{n}=shift;
	return $self;
}
sub print {
	my $self=shift;
	my $param=shift;
	my $escape=shift;
	my $e=$escape;
	$e=0 if $self->{name} eq "block.super" and $param->{"block.super"};
	print $self->{p},Dotiac::DTL::apply_filters(Dotiac::DTL::devar_var($self->{name},$param,$e,@_),$param,$e,@{$self->{filters}})->string();
	$self->{n}->print($param,$escape,@_);
	return;
}
sub string {
	my $self=shift;
	my $param=shift;
	my $escape=shift;
	my $e=$escape;
	$e=0 if $self->{name} eq "block.super" and $param->{"block.super"};
	return $self->{p}.Dotiac::DTL::apply_filters(Dotiac::DTL::devar_var($self->{name},$param,$e,@_),$param,$e,@{$self->{filters}})->string().$self->{n}->string($param,$escape,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	#use Carp;
	#confess unless $id;
	return $self->{n}->perlinit($fh,$id+1,@_);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{p}],["\$text$id"]));
	print $fh "my ";
	my $x=$self->{name};

	$x=Dotiac::DTL::descap(substr($x,1,-1)) if substr($x,0,1) eq "`";
	print $fh (Data::Dumper->Dump([$x],["\$var$id"]));
	if (@{$self->{filters}}) {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{filters}],["\$filters$id"]));
	}
	return $self->{n}->perl($fh,$id+1,@_);
	
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	print $fh "\t" x $level,"print \$text$id, ";
	my $e="\$escape";
	if ($self->{name} eq "block.super") {
		print $fh "\t" x $level,"my \$e$id=\$escape;\n";
		print $fh "\t" x $level,"\$e$id=0 if \$vars->{\"block.super\"};\n";
		$e="\$e$id";
	}
	#print $fh "\t" x $level,"print ";
	print $fh "Dotiac::DTL::apply_filters(" if @{$self->{filters}};
	if (substr($self->{name},0,1) eq "`") {
		print $fh "Dotiac::DTL::Value->safe(\$var$id)";
	}
	else {
		print $fh "Dotiac::DTL::devar_var(\$var$id,\$vars,$e,\@_)" if @{$self->{filters}};
		print $fh "Dotiac::DTL::devar_raw(\$var$id,\$vars,$e,\@_)" unless @{$self->{filters}};
	}
	print $fh ",\$vars,$e,\@{\$filters$id})" if @{$self->{filters}};
	print $fh "->string();\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $e="\$escape";
	if ($self->{name} eq "block.super") {
		print $fh "\t" x $level,"my \$e$id=\$escape;\n";
		print $fh "\t" x $level,"\$e$id=0 if \$vars->{\"block.super\"};\n";
		$e="\$e$id";
	}
	print $fh "\t" x $level,"\$r.=\$text$id.";
	#print $fh "\t" x $level,"\$r.=";
		print $fh "Dotiac::DTL::apply_filters(" if @{$self->{filters}};
	if (substr($self->{name},0,1) eq "`") {
		print $fh "Dotiac::DTL::Value->safe(\$var$id)";
	}
	else {
		print $fh "Dotiac::DTL::devar_var(\$var$id,\$vars,$e,\@_)" if @{$self->{filters}};
		print $fh "Dotiac::DTL::devar_raw(\$var$id,\$vars,$e,\@_)" unless @{$self->{filters}};
	}
	print $fh ",\$vars,$e,\@{\$filters$id})" if @{$self->{filters}};
	print $fh "->string();\n";
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	return $self->{n}->perlcount($id+1);
}
sub eval {
	return;
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	return $self->{n}->perleval($fh,$id+1,$level,@_);
}

1;
__END__

=head1 NAME

Dotiac::DTL::Variable - Stores a Django template variable tag.


=head1 SYNOPSIS

Template file:

	Some text.... 
	{{ variable }}
	Some other Text. {{ variable|lower }} Some more text.
	Even more Text.{{ "Quoted text"|lower }} End of Text.

Perl file:

	use Dotiac::DTL;
	my $template=Dotiac::DTL->new("template.file");
	$template->print({variable=>"Dynamic Text"});

=head1 DESCRIPTION

Everything between the starting {{ and the next }} is treated a variable name.
Stuff that doesn't belong to the first variable is silently ignored:

	{{ variable variable2 }} is the same as {{ variable }}

{{ variable|lower }} is a varible with a filter, see L<Dotiac::DTL::Filter> for more details on filters.
Everywhere a variable is requested you can also use a string and/or filters as these are considered to be part
of the variable. Here shown with the tag cycle (L<Dotiac::DTL::Tag::cycle>):

	{% cycle variable othervariable, "string with spaces, and even colons" %}
	{% cycle variablewithfilter|lower variablewithmorefilters|lower|addslashes %}
	{% cycle "string with a filter"|lower|addslashes "string with a filter with arguments"|cut:", " %}

The variable can not contain spaces, colons or other special chars, unless those are in quotes (single or double)
They can never contain (for now) :  %} and }}, depending on wether they are in a tag or standalone.

The module itself has no real use, it's just used by the Dotiac::DTL 
parser to store free standing variables. Variables in specific tags are 
stored in those.

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

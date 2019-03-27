#include.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#include.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL::Tag::include;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	die "This include needs a filename or variable" unless $name;
	$name=(Dotiac::DTL::get_variables($name))[0];
	my $f=substr $name,0,1;
	my $e=substr $name,-1,1;
	if ($f eq "`" and $e eq "`") {
		$self->{load}=Dotiac::DTL::devar($name,{},0);
		my $tem = Dotiac::DTL->safenew($self->{load});
		$self->{content}=$tem->{first};
	}
	else {
		$self->{var}=$name;
	}
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	if ($self->{content}) {
		$self->{content}->print(@_);
	}
	else {
		my $tem = Dotiac::DTL::devar_raw($self->{var},@_);
		my $s=$tem->content();
		if ($tem->scalar()) {
			$tem = Dotiac::DTL->safenew($tem->repr);
		}
		elsif ($tem->object() and $tem->content->isa("Dotiac::DTL::Template")) {
			$tem=$tem->content;
		}
		else {
			die "Can't include \"$tem\"";
		}
		die "Cyclic include detected " if $Dotiac::DTL::included{$s}++;
		$tem->{first}->print(@_);
		$Dotiac::DTL::included{$s}=0;
	}
	$self->{n}->print(@_) if $self->{n};
}
sub string {
	my $self=shift;
	if ($self->{content}) {
		return $self->{p}.$self->{content}->string(@_).($self->{n}?$self->{n}->string(@_):"");
	}
	my $tem = Dotiac::DTL::devar_raw($self->{var},@_);
	my $s=$tem->content();
	if ($tem->scalar()) {
		$tem = Dotiac::DTL->safenew($tem->repr);
	}
	elsif ($tem->object() and $tem->content->isa("Dotiac::DTL::Template")) {
		$tem=$tem->content;
	}
	else {
		die "Can't include \"$tem\"";
	}
	die "Cyclic include detected " if $Dotiac::DTL::included{$s}++;
	my $r=$self->{p}.$tem->{first}->string(@_).($self->{n}?$self->{n}->string(@_):"");
	$Dotiac::DTL::included{$s}=0;
	return $r
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	if ($self->{load}) {
		print $fh "my \$template$id=Dotiac::DTL->safenew(\"".quotemeta($self->{load})."\");\n";
	}
	else {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{var}],["\$name$id"]));
	}
	return $self->{n}->perl($fh,$id+1,@_);
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	my $in="\t" x $level;
	if ($self->{content}) {
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$template$id}++;\n";
		print $fh "$in\$template$id"."->{first}->print(\$vars,\$escape,\@_);\n";
		print $fh $in,"\$Dotiac::DTL::included{\$template$id}=0;\n";
	}
	else {
		print $fh $in,"my \$template$id = Dotiac::DTL::devar_raw(\$name$id,\$vars,\$escape,\@_);\n";
		print $fh $in,"my \$s$id=\$template$id->content();\n";
		print $fh $in,"if (\$template$id->scalar()) {\n";
		print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id->repr());\n";
		print $fh $in,"} elsif (\$template$id->object() and \$template$id->content->isa(\"Dotiac::DTL::Template\")) {\n";
		print $fh $in,"\t\$template$id=\$template$id->content;\n";
		print $fh $in,"} else {\n";
		print $fh $in,"\tdie \"Can't include \\\"\$template$id\\\"\";\n";
		print $fh $in,"}\n";
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$s$id}++;\n";
		print $fh $in,"\$template$id"."->{first}->print(\$vars,\$escape,\@_);\n";
		print $fh $in,"\$Dotiac::DTL::included{\$s$id}=0;\n";
	}
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	my $in="\t" x $level;
	if ($self->{content}) {
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$template$id}++;\n";
		print $fh "$in\$r.=\$template$id"."->{first}->string(\$vars,\$escape,\@_);\n";
		print $fh $in,"\$Dotiac::DTL::included{\$template$id}=0;\n";
	}
	else {
		print $fh $in,"my \$template$id = Dotiac::DTL::devar_raw(\$name$id,\$vars,\$escape,\@_);\n";
		print $fh $in,"my \$s$id=\$template$id->content();\n";
		print $fh $in,"if (\$template$id->scalar()) {\n";
		print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id->repr());\n";
		print $fh $in,"} elsif (\$template$id->object() and \$template$id->content->isa(\"Dotiac::DTL::Template\")) {\n";
		print $fh $in,"\t\$template$id=\$template$id->content;\n";
		print $fh $in,"} else {\n";
		print $fh $in,"\tdie \"Can't include \\\"\$template$id\\\"\";\n";
		print $fh $in,"}\n";
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$s$id}++;\n";
		print $fh $in,"\$r.=\$template$id"."->{first}->string(\$vars,\$escape,\@_);\n";
		print $fh $in,"\$Dotiac::DTL::included{\$s$id}=0;\n";
	}
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
	my $level=shift;
	my $in="\t" x $level;
	if ($self->{content}) {
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$template$id}++;\n";
		print $fh "$in\$template$id"."->{first}->eval(\$vars,\$escape,\@_);\n";
		print $fh $in,"\$Dotiac::DTL::included{\$template$id}=0;\n";
	}
	else {
		print $fh $in,"my \$template$id = Dotiac::DTL::devar_raw(\$name$id,\$vars,\$escape,\@_);\n";
		print $fh $in,"my \$s$id=\$template$id->content();\n";
		print $fh $in,"if (\$template$id->scalar()) {\n";
		print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id->repr());\n";
		print $fh $in,"} elsif (\$template$id->object() and \$template$id->content->isa(\"Dotiac::DTL\")) {\n";
		print $fh $in,"\t\$template$id=\$template$id->content;\n";
		print $fh $in,"} else {\n";
		print $fh $in,"\tdie \"Can't include \\\"\$template$id\\\"\";\n";
		print $fh $in,"}\n";
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$s$id}++;\n";
		print $fh $in,"\$template$id"."->{first}->eval(\$vars,\$escape,\@_);\n";
		print $fh $in,"\$Dotiac::DTL::included{\$s$id}=0;\n";
	}
	return $self->{n}->perleval($fh,$id+1,$level,@_);
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
	if ($self->{content}) {
		$self->{content}->eval(@_) ;
	}
	else {
		my $tem = Dotiac::DTL::devar_raw($self->{var},@_);
		my $s=$tem->content();
		if ($tem->scalar()) {
			$tem = Dotiac::DTL->safenew($tem->repr);
		}
		elsif ($tem->object() and $tem->content->isa("Dotiac::DTL")) {
			$tem=$tem->content;
		}
		else {
			die "Can't include \"$tem\"";
		}
		die "Cyclic include detected " if $Dotiac::DTL::included{$s}++;
		$tem->{first}->eval(@_);
		$Dotiac::DTL::included{$s}=0;
	}
	$self->{n}->eval(@_);
}
1;
__END__

=head1 NAME

Dotiac::DTL::Tag::include - The {% include FILE %} tag

=head1 SYNOPSIS

Template file:

	{% include variable %} {# for example, monday.html, tuesday.html everyday another header #}
	<div id="content"> ... </div>
	{% include "footer.html" %}
	</body>
	</html>

Included template file (footer.html):

	<div id="footer">{{ Footertext }}</div>

=head1 DESCRIPTION

Loads another template and renders the content in at the point where the tag is standing. All variables are given to the included template as well, so they can be used in there.

The FILE parameter can be either a string: "file.html" or a variable. If it is a string, the template will be loaded and parsed during the parse time of the template, which is faster. A variable can be either a filename or a Dotiac::DTL object.

=head1 BUGS AND DIFFERENCES TO DJANGO

If you find any, please let me know

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

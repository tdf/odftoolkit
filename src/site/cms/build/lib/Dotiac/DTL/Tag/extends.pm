#extends.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#extends.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::extends;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;
require Scalar::Util;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	die "This extend needs a filename or variable" unless $name;
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
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{data}=$obj->parse($data,$pos,\$found,"endextends");
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	$self->{data}->eval(@_);
	#die Data::Dumper->Dump([$self]);
	if ($self->{content}) {
		$self->{content}->print(@_);
	}
	else {
		my $tem = Dotiac::DTL::devar_raw($self->{var},@_);
		if ($tem->scalar()) {
			$tem = Dotiac::DTL->safenew($tem->repr);
		}
		elsif ($tem->object() and $tem->content->isa("Dotiac::DTL::Template")) {
			$tem=$tem->content;
		}
		else {
			die "Can't extend with \"$tem\"";
		}
		$tem->{first}->print(@_)
	}
	$self->{n}->print(@_) if $self->{n};
}
sub string {
	my $self=shift;
	$self->{data}->eval(@_);
	if ($self->{content}) {
		return $self->{p}.$self->{content}->string(@_).($self->{n}?$self->{n}->string(@_):"");
	}
	my $tem = Dotiac::DTL::devar_raw($self->{var},@_);
	if ($tem->scalar()) {
		$tem = Dotiac::DTL->safenew($tem->repr);
	}
	elsif ($tem->object() and $tem->content->isa("Dotiac::DTL::Template")) {
		$tem=$tem->content;
	}
	else {
		die "Can't extend with \"$tem\"";
	}
	return $self->{p}.$tem->{first}->string(@_).($self->{n}?$self->{n}->string(@_):"");
	
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
	$id=$self->{data}->perl($fh,$id+1,@_);
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	my $in="\t" x $level;
	my $nid = $self->{data}->perleval($fh,$id+1,$level,@_);
	if ($self->{content}) {
		print $fh "$in\$template$id"."->{first}->print(\$vars,\$escape,\@_);\n";
	}
	else {
		print $fh $in,"my \$template$id = Dotiac::DTL::devar_raw(\$name$id,\$vars,\$escape,\@_);\n";
		print $fh $in,"if (\$template$id->scalar()) {\n";
		print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id->repr());\n";
		print $fh $in,"} elsif (\$template$id->object() and \$template$id->content->isa(\"Dotiac::DTL::Template\")) {\n";
		print $fh $in,"\t\$template$id=\$template$id->content;\n";
		print $fh $in,"} else {\n";
		print $fh $in,"\tdie \"Can't extend with \\\"\$template$id\\\"\";\n";
		print $fh $in,"}\n";
		print $fh $in,"\$template$id"."->{first}->print(\$vars,\$escape,\@_);\n";
	}
	return $self->{n}->perlprint($fh,$nid+1,$level,@_) if $self->{n};	
	return $nid;
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	my $in="\t" x $level;
	my $nid = $self->{data}->perleval($fh,$id+1,$level,@_);
	if ($self->{content}) {
		print $fh "$in\$r.=\$template$id"."->{first}->string(\$vars,\$escape,\@_);\n";
	}
	else {
		print $fh $in,"my \$template$id = Dotiac::DTL::devar_raw(\$name$id,\$vars,\$escape,\@_);\n";
		print $fh $in,"if (\$template$id->scalar()) {\n";
		print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id->repr());\n";
		print $fh $in,"} elsif (\$template$id->object() and \$template$id->content->isa(\"Dotiac::DTL::Template\")) {\n";
		print $fh $in,"\t\$template$id=\$template$id->content;\n";
		print $fh $in,"} else {\n";
		print $fh $in,"\tdie \"Can't extend with \\\"\$template$id\\\"\";\n";
		print $fh $in,"}\n";
		print $fh $in,"\$r.=\$template$id"."->{first}->string(\$vars,\$escape,\@_);\n";
	}
	return $self->{n}->perlstring($fh,$nid+1,$level,@_) if $self->{n};	
	return $nid;
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id = $self->{data}->perlcount($id+1);
	return $id unless $self->{n};
	return $self->{n}->perlcount($id+1);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{data}->perleval($fh,$id+1,@_);
	return $id unless $self->{n};
	return $self->{n}->perleval($fh,$id+1,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{data}->perlinit($fh,$id+1,@_);
	return $id unless $self->{n};
	return $self->{n}->perlinit($fh,$id+1,@_);
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
sub eval {
	my $self=shift;
	$self->{data}->eval(@_);
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::extends - The {% extends FILE %} tag

=head1 SYNOPSIS

Template file:

	{% extends "main.html" %}
	{% block title %}About us{% endblock %}
	{% block pagecontent %}<h1>About us</h1>Under construction{% endblock %}

Other template file:

	{% extends variable %}
	This text will never be printed, ever.
	{% block pagecontent %}<h1>Main Page</h1>Under construction{% endblock %}

=head1 DESCRIPTION

Loads another template and replaces its content with this.

The content will be ignored, unless {% block %} tags, which are evaluated. Those will replace the corresponding {% block %} tags in the included template. See Dotiac::DTL::Tag::block for details

The FILE parameter can be either a string: "file.html" or a variable. If it is a string, the template will be loaded and parsed during the parse time of the template, which is faster. A variable can be either a filename or a Dotiac::DTL object.

=head1 BUGS AND DIFFERENCES TO DJANGO

Django's {% extend %} works for the whole files and ends at the file end. In this Dotiac::DTL, this is also valid and works as you would expect:

Template file:

	<html><body>
	{% extends "sidebar.html" %}
		{% block sidebartext1 %}Great news{% endblock %}
		{% block sidebartext2 %}Dotiac::DTL finished{% endblock %}
	{% endextends %}
	<div id="page"> 
	Page content
	</div>
	{% extends "footer.html" %}
		{% block foottext %}Author: me{% endblock foo %}
	{% endextends %} 
	</body></html>

Most tags update blocks even if they shouldn't, this is why this won't work as you expect.

Django doesn't allow this anyway. This will always set the "content"-block to "No Text" no matter what var is.

	{% extends "foo.html" %}
	{% if var %}
		{% block content %}
			Text
		{% endblock content %}
	{% else %}
		{% block content %}
			No Text
		{% endblock content %}
	{% endif %}


=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

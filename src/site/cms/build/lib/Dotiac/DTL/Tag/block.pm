#block.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#block.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################


package Dotiac::DTL::Tag::block;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	die "This block needs a name" unless $name;
	$self->{name}=$name;
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{content}=$obj->parse($data,$pos,\$found,"endblock");
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $vars=shift;
	use Carp;
	#confess;
	if ($Dotiac::DTL::blocks{$self->{name}}) {
		#confess ref $Dotiac::DTL::blocks{$self->{name}}->[0];
		$Dotiac::DTL::blocks{$self->{name}}->[0]->print({%{$vars},"block.super",$self->{content}},@_) unless ref $Dotiac::DTL::blocks{$self->{name}}->[0] eq "REF" ;
		print ${$Dotiac::DTL::blocks{$self->{name}}->[0]}->({%{$vars},"block.super",$self->{content}},@_) if ref $Dotiac::DTL::blocks{$self->{name}}->[0] eq "REF" ;
	}
	else {
		$self->{content}->print($vars,@_);
	}
	$self->{n}->print($vars,@_);
}
sub string {
	my $self=shift;
	my $vars=shift;
	if ($Dotiac::DTL::blocks{$self->{name}}) {
		#use Carp;
		#confess "No Var" unless $vars;
		
		return $self->{p}.${$Dotiac::DTL::blocks{$self->{name}}->[0]}->({%{$vars},"block.super",$self->{content}},@_).$self->{n}->string($vars,@_) if ref $Dotiac::DTL::blocks{$self->{name}}->[0] eq "REF";
		return $self->{p}.$Dotiac::DTL::blocks{$self->{name}}->[0]->string({%{$vars},"block.super",$self->{content}},@_).$self->{n}->string($vars,@_);
	}
	return $self->{p}.$self->{content}->string($vars,@_).$self->{n}->string($vars,@_);
	
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{content}->perlcount($id+1,@_);
	return $self->{n}->perlcount($id+1);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my \$ssub$id = sub {return ''};\n";
	print $fh "my \$sub$id = \\\$ssub$id;\n";
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{name}],["\$name$id"]));
	$id=$self->{content}->perl($fh,$id+1,@_);
	return $self->{n}->perl($fh,$id+1,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	print $fh "\$ssub$id=sub {\n";
	print $fh "\tmy \$vars=shift();\n";
	print $fh "\tmy \$escape=shift();\n";
	print $fh "\tmy \$r=\"\";\n";
	my $nid=$self->{content}->perlstring($fh,$id+1,1,@_);
	print $fh "\treturn \$r;\n";
	print $fh "};\n";
	$id=$self->{content}->perlinit($fh,$id+1,@_);
	return $self->{n}->perlinit($fh,$id+1,@_);
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t" x $level,"if (\$Dotiac::DTL::blocks{\$name$id}) {\n";
	print $fh "\t" x $level,"\tprint \${\$Dotiac::DTL::blocks{\$name$id}->[0]}->({\%{\$vars},\"block.super\",\$ssub$id},\$escape,\@_) if ref \$Dotiac::DTL::blocks{\$name$id}->[0] eq 'REF';\n"; #Might a perlstring routine....
	print $fh "\t" x $level,"\t\$Dotiac::DTL::blocks{\$name$id}->[0]->print({\%{\$vars},\"block.super\",\$ssub$id},\$escape,\@_) unless ref \$Dotiac::DTL::blocks{\$name$id}->[0] eq 'REF';\n"; #Might a perlstring routine....
	print $fh "\t" x $level,"} else {\n";
	my $nid=$self->{content}->perlprint($fh,$id+1,$level+1,@_);
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perlprint($fh,$nid+1,$level,@_);

}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	use Carp; confess unless $level;	
	print $fh "\t" x $level,"if (\$Dotiac::DTL::blocks{\$name$id}) {\n";
	#print $fh "\t" x $level,"\t\$r.=\${\$Dotiac::DTL::blocks{\$name$id}}->({\%{\$vars},\"block.super\",\$ssub$id},\$escape,\@_);\n"; #Might a perlstring routine....
	print $fh "\t" x $level,"\t\$r.=\${\$Dotiac::DTL::blocks{\$name$id}->[0]}->({\%{\$vars},\"block.super\",\$ssub$id},\$escape,\@_) if ref \$Dotiac::DTL::blocks{\$name$id}->[0] eq 'REF';\n"; #Might a perlstring routine....
	print $fh "\t" x $level,"\t\$r.=\$Dotiac::DTL::blocks{\$name$id}->[0]->string({\%{\$vars},\"block.super\",\$ssub$id},\$escape,\@_) unless ref \$Dotiac::DTL::blocks{\$name$id}->[0] eq 'REF';\n"; #Might a perlstring routine....
	print $fh "\t" x $level,"} else {\n";
	my $nid=$self->{content}->perlstring($fh,$id+1,$level+1,@_);
	print $fh "\t" x $level,"}\n";
	return $self->{n}->perlstring($fh,$nid+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	print $fh "\t" x $level,"unshift \@{\$Dotiac::DTL::blocks{\$name$id}},\$sub$id;\n";
	$id=$self->{content}->perleval($fh,$id+1,$level,@_);
	return $self->{n}->perleval($fh,$id+1,$level);
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
sub eval {
	my $self=shift;
	unshift @{$Dotiac::DTL::blocks{$self->{name}}},$self->{content};
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::block - The {% block NAME %} tag

=head1 SYNOPSIS

Template file: (main.html)

	<html>
		<head>
			<title>{% block title %}Default title{% endblock title %}</title>
		</head>
		<body>
			<div class="main">{% block pagecontent %}
				This page has no content.
			{% endblock %}</div>
		</body>
	</html>

Other template file: (aboutus.html)

	{% extends "main.html" %}
	{% block title %}About us{% endblock %}
	{% block pagecontent %}<h1>About us</h1>Under construction{% endblock %}

Other template file: (aboutus2.html)

	{% extends "main.html" %}
	{% block pagecontent %}<h1>About us</h1>Under construction{% endblock %}


=head1 DESCRIPTION

The "block" tag defines a named block, which can be overwritten or overwrites it.

It is normaly used together with {% extends %}. It defines a block in one template and 
then overwrites the defined block from another template. This is called "template inheritance".
There are some great examples on the original Djagno homepage: L<http://docs.djangoproject.com/en/dev/topics/templates/#template-inheritance>

Everything from {% block NAME %} till {% endblock [NAME] %} is treated as a block with the name NAME.
In another template, which contains an {% extends "abovetemplate" %}, the block NAME can be overwritten.

The previous content of the block can be used in that block via the variable {{ block.super }}

If no new block with the same name is defined, the default text is used.

If no extend is used, the {% block %} tags will just return their content.

Of course all variables in a block will work just as they would outside, even if the block is defined in a different file alltogether.

The above examples will produce:

Rendering just "main.html", the block-tags will disappear:

	<html>
		<head>
			<title>Default title</title>
		</head>
		<body>
			<div class="main">
				This page has no content.
			</div>
		</body>
	</html>

Rendering "aboutus.html", all block-tags will be replaced:

	<html>
		<head>
			<title>About us</title>
		</head>
		<body>
			<div class="main"><h1>About us</h1>Under construction</div>
		</body>
	</html>

Rendering "aboutus2.html", one block-tags will be replaced, the other will be left as default:

	<html>
		<head>
			<title>Default title</title>
		</head>
		<body>
			<div class="main"><h1>About us</h1>Under construction</div>
		</body>
	</html>

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 BUGS AND DIFFERENCES TO DJANGO

If you find any, please report them.

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

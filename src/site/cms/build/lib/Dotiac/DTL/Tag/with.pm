#with.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#with.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::with;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;
our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	my %name=Dotiac::DTL::get_variables($name,"as");
	die "Can't use tag 'with' without a variable" unless $name{""} and @{$name{""}};
	die "Can't use tag 'with' without a new variable name (as)" unless $name{as} and @{$name{as}};
	$self->{var}=$name{""}->[0];
	$self->{as}=$name{as}->[0];
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{content}=$obj->parse($data,$pos,\$found,"endwith");
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $vars=shift;
	if (defined $self->{var} and $self->{as} ) {
		$self->{content}->print({%{$vars},$self->{as}=>Dotiac::DTL::devar($self->{var},$vars,@_)},@_);
	}
	else {
		$self->{content}->print($vars,@_);
	}
	$self->{n}->print($vars,@_);
}
sub string {
	my $self=shift;
	my $vars=shift;
	return $self->{p}.$self->{content}->string({%{$vars},$self->{as}=>Dotiac::DTL::devar($self->{var},$vars,@_)},@_).$self->{n}->string($vars,@_) if $self->{var} and $self->{as};
	return $self->{p}.$self->{content}->string($vars,@_).$self->{n}->string($vars,@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{var}],["\$var$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{as}],["\$as$id"]));
	$id = $self->{content}->perl($fh,$id+1,@_);
	return $self->{n}->perl($fh,$id+1,@_)
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id = $self->{content}->perlinit($fh,$id+1,@_);
	return $self->{n}->perlinit($fh,$id+1,@_)
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	if (defined $self->{var} and $self->{as} ) {
		print $fh "\t" x $level,"my \$vars$id=\$vars;\n";
		print $fh "\t" x $level,"{\n";
		$level++;
		print $fh "\t" x $level,"my \$vars={\%{\$vars$id}};\n";
		print $fh "\t" x $level,"\$vars->{\$as$id}=Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";

	}
	$id = $self->{content}->perlprint($fh,$id+1,$level,@_);
	if (defined $self->{var} and $self->{as}) {
		$level--;
		print $fh "\t" x $level,"}\n";
		
	}
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	if (defined $self->{var} and $self->{as} ) {
		print $fh "\t" x $level,"my \$vars$id=\$vars;\n";
		print $fh "\t" x $level,"{\n";
		$level++;
		print $fh "\t" x $level,"my \$vars={\%{\$vars$id}};\n";
		print $fh "\t" x $level,"\$vars->{\$as$id}=Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";

	}
	$id = $self->{content}->perlstring($fh,$id+1,$level,@_);
	if (defined $self->{var} and $self->{as}) {
		$level--;
		print $fh "\t" x $level,"}\n";
		
	}
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	if (defined $self->{var} and $self->{as} ) {
		print $fh "\t" x $level,"my \$vars$id=\$vars;\n";
		print $fh "\t" x $level,"{\n";
		$level++;
		print $fh "\t" x $level,"my \$vars={\%{\$vars$id}};\n";
		print $fh "\t" x $level,"\$vars->{\$as$id}=Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";

	}
	$id=$self->{content}->perleval($fh,$id+1,$level,@_);
	if (defined $self->{var} and $self->{as}) {
		$level--;
		print $fh "\t" x $level,"}\n";
		
	}
	$self->{n}->perleval($fh,$id+1,$level,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{content}->perlcount($id+1,@_);
	return $self->{n}->perlcount($id+1);
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
sub eval {
	my $self=shift;
	my $vars=shift;
	if (defined $self->{var} and $self->{as} ) {
		$self->{content}->eval({%{$vars},$self->{as}=>Dotiac::DTL::devar($self->{var},$vars,@_)},@_);
	}
	else {
		$self->{content}->eval($vars,@_);
	}
	$self->{n}->eval($vars,@_);
}

1;
__END__

=head1 NAME

Dotiac::DTL::Tag::with - The {% with VARIABLE as NEWVARIABLENAME %} tag

=head1 SYNOPSIS

Template file:

	{% with object.expensivemethod as newvar %}
		{{ newvar|upper }}
		<b>{{ newvar|slugify }}{{ newvar|pluralize:"es" }}</b>
	{% endwith %}

=head1 DESCRIPTION

Renames a VARIABLE to a NEW VARIABLE NAME for the block from {% with ... %} to {% endwith %}. This is quite useful, since VARIABLEs can contain slow lookups and/or method calls which then can be stored as in an easy NEW VARIABLE NAME for fast access. This way a slow method call is only performed once.

=head1 BUGS AND DIFFERENCES TO DJANGO

I don't know if Django allows this as well, but this implementation also allows filters as in any other variable, this can be used to save results of filters.

	{% with var|lower|striptags|slugify as postid %}
		<a href="post.pl?id={{ postid }}" title="{{ postid }}">Go to {{ postid }}</a>
	{% endwith %}

If you find anything else, please let me know.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

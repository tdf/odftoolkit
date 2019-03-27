#ifchanged.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#ifchanged.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::ifchanged;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{true}=$obj->parse($data,$pos,\$found,"else","endifchanged"); #Else might also be useful here, but not pyDjango style
	if ($found eq "else") {
		$self->{false}=$obj->parse($data,$pos,\$found,"endifchanged");
	}
	my $n=$name;
	$name=~s/^\s+// if $name;
	$name=~s/\s+$// if $name;
	$self->{var}=(Dotiac::DTL::get_variables($n))[0] if $name and $name=~/\w/;
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	$Dotiac::DTL::globals{ifchanged}->{$self} = "" unless $Dotiac::DTL::globals{ifchanged} and $Dotiac::DTL::globals{ifchanged}->{$self};
	print $self->{p};
	my $r = "";
	if ($self->{var}) {
		$r = Dotiac::DTL::devar($self->{var},@_);
	}
	else {
		$r = $self->{true}->string(@_);
	}
	if ($r ne $Dotiac::DTL::globals{ifchanged}->{$self}) {
		$Dotiac::DTL::globals{ifchanged}->{$self}=$r;
		if ($self->{var}) {
			$self->{true}->print(@_);
		}
		else {
			print $r;
		}
	}
	else {
		$self->{false}->print(@_) if $self->{false};
	}
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	$Dotiac::DTL::globals{ifchanged}->{$self} = "" unless $Dotiac::DTL::globals{ifchanged} and $Dotiac::DTL::globals{ifchanged}->{$self};
	print $self->{p};
	my $r = "";
	if ($self->{var}) {
		$r = Dotiac::DTL::devar($self->{var},@_);
	}
	else {
		$r = $self->{true}->string(@_);
	}
	if ($r ne $Dotiac::DTL::globals{ifchanged}->{$self}) {
		$Dotiac::DTL::globals{ifchanged}->{$self}=$r;
		if ($self->{var}) {
			return $self->{p}.$self->{true}->string(@_).$self->{n}->string(@_); 
		}
		else {
			return $self->{p}.$r.$self->{n}->string(@_);
		}
	}
	else {
		return $self->{p}.($self->{false}?$self->{false}->string(@_):"").$self->{n}->string(@_);
	}
	
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	if ($self->{var}) {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{var}],["\$var$id"]));
	}
	$id = $self->{true}->perl($fh,$id+1,@_);
	$id = $self->{false}->perl($fh,$id+1,@_) if $self->{false};
	return $self->{n}->perl($fh,$id+1,@_)
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id = $self->{true}->perlinit($fh,$id+1,@_);
	$id = $self->{false}->perlinit($fh,$id+1,@_) if $self->{false};
	return $self->{n}->perlinit($fh,$id+1,@_)
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	use Carp; confess unless $_[0];
	my $name="\"$_[0]-$id\"";
	my $nid=$id;
	if ($self->{var}) {
		print $fh "\t" x $level,"\$Dotiac::DTL::globals{ifchanged}->{$name} = \"\" unless \$Dotiac::DTL::globals{ifchanged} and \$Dotiac::DTL::globals{ifchanged}->{$name};\n";
		print $fh "\t" x $level,"my \$change$id = Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";
		print $fh "\t" x $level,"if (\$Dotiac::DTL::globals{ifchanged}->{$name} ne \$change$id) {\n";
		print $fh "\t" x $level,"\t\$Dotiac::DTL::globals{ifchanged}->{$name}=\$change$id;\n";
		$nid = $self->{true}->perlprint($fh,$id+1,$level+1,@_);
		if ($self->{false}) {
			print $fh "\t" x $level,"} else {\n";
			$nid = $self->{false}->perlprint($fh,$nid+1,$level+1,@_);
		}
		print $fh "\t" x $level,"}\n";
	}
	else {
		print $fh "\t" x $level,"\$Dotiac::DTL::globals{ifchanged}->{$name} = \"\" unless \$Dotiac::DTL::globals{ifchanged} and \$Dotiac::DTL::globals{ifchanged}->{$name};\n";
		print $fh "\t" x $level,"my \$change$id = \"\";\n";
		print $fh "\t" x $level,"{\n";
		print $fh "\t" x $level,"\tmy \$r=\"\";\n";
		$nid = $self->{true}->perlstring($fh,$id+1,$level+1,@_);
		print $fh "\t" x $level,"\$change$id = \$r;\n";
		print $fh "\t" x $level,"}\n";
		print $fh "\t" x $level,"if (\$Dotiac::DTL::globals{ifchanged}->{$name} ne \$change$id) {\n";
		print $fh "\t" x $level,"\t\$Dotiac::DTL::globals{ifchanged}->{$name}=\$change$id;\n";
		print $fh "\t" x $level,"\tprint \$change$id;\n";
		if ($self->{false}) {
			print $fh "\t" x $level,"} else {\n";
			$nid = $self->{false}->perlprint($fh,$nid+1,$level+1,@_);
		}
		print $fh "\t" x $level,"}\n";
	}
	return $self->{n}->perlprint($fh,$nid+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	my $name="\"$_[0]-$id\"";
	my $nid=$id;
	if ($self->{var}) {
		print $fh "\t" x $level,"\$Dotiac::DTL::globals{ifchanged}->{$name} = \"\" unless \$Dotiac::DTL::globals{ifchanged} and \$Dotiac::DTL::globals{ifchanged}->{$name};\n";
		print $fh "\t" x $level,"my \$change$id = Dotiac::DTL::devar(\$var$id,\$vars,\$escape,\@_);\n";
		print $fh "\t" x $level,"if (\$Dotiac::DTL::globals{ifchanged}->{$name} ne \$change$id) {\n";
		print $fh "\t" x $level,"\t\$Dotiac::DTL::globals{ifchanged}->{$name}=\$change$id;\n";
		$nid = $self->{true}->perlstring($fh,$id+1,$level+1,@_);
		if ($self->{false}) {
			print $fh "\t" x $level,"} else {\n";
			$nid = $self->{false}->perlstring($fh,$nid+1,$level+1,@_);
		}
		print $fh "\t" x $level,"}\n";
	}
	else {
		print $fh "\t" x $level,"\$Dotiac::DTL::globals{ifchanged}->{$name} = \"\" unless \$Dotiac::DTL::globals{ifchanged} and \$Dotiac::DTL::globals{ifchanged}->{$name};\n";
		print $fh "\t" x $level,"my \$change$id = \"\";\n";
		print $fh "\t" x $level,"{\n";
		print $fh "\t" x $level,"\tmy \$r=\"\";\n";
		$nid = $self->{true}->perlstring($fh,$id+1,$level+1,@_);
		print $fh "\t" x $level,"\t\$change$id=\$r;\n";
		print $fh "\t" x $level,"}\n";
		print $fh "\t" x $level,"if (\$Dotiac::DTL::globals{ifchanged}->{$name} ne \$change$id) {\n";
		print $fh "\t" x $level,"\t\$Dotiac::DTL::globals{ifchanged}->{$name}=\$change$id;\n";
		print $fh "\t" x $level,"\t\$r.=\$change$id;\n";
		if ($self->{false}) {
			print $fh "\t" x $level,"} else {\n";
			$nid = $self->{false}->perlstring($fh,$nid+1,$level+1,@_);
		}
		print $fh "\t" x $level,"}\n";
	}
	return $self->{n}->perlstring($fh,$nid+1,$level,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{true}->perleval($fh,$id+1,@_);
	$id=$self->{false}->perleval($fh,$id+1,@_) if $self->{false};
	$self->{n}->perleval($fh,$id+1,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{true}->perlcount($id+1,@_);
	$id=$self->{false}->perlcount($id+1,@_) if $self->{false};
	return $self->{n}->perlcount($id+1);
}
sub next {
	my $self=shift;
	$self->{n}=shift;
}
sub eval {
	my $self=shift;
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::ifchanged - The {% ifchanged [VARIABLE] %} tag

=head1 SYNOPSIS

Template file:

	{% for x in loop %}
		{% ifchanged %}
			Posted on {{ x.date }}
		{% endifchanged %}
		{% ifchanged x.poster %}
			Reply by {{ x.poster }} on {{ x.date }}
		{% endifchanged %}
	{% endfor %}

=head1 DESCRIPTION

Without VARIABLE, ifchanged only renders its content, if the content changed since the last iteration of a loop above.

With VARIABLE, ifchanged only renders its content, if VARIABLE has changed since the last iteration of a loop above.

=head2 Note

Every ifchanged stands on its own, even if they have the same variable or content to check.

	{% ifchanged x.post %}
		... {# This will be displayed #}
	{% endifchanged %}
	{% ifchanged x.post %}
		... {# This will also be displayed #}
	{% endifchanged %}

=head1 BUGS AND DIFFERENCES TO DJANGO

This implementation also supports the {% else %} tag in ifchanged, which is not included in Django, but there is a patch for that.

	{% for timepoint in timepoints %}
		{% ifchanged timepoint.day %}
			It's a new day.
		{% else %}
			It's still {{ timepoint.day }}
		{% endifchanged %}
	{% endfor %}

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

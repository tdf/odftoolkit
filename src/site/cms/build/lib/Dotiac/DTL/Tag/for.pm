#for.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#for.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::for;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;
require Scalar::Util;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	my %name = Dotiac::DTL::get_variables($name,"in","reversed");
	$self->{var}=[split /\s*,\s*/,join(" ",@{$name{""}})];
	if ($name{reversed}) {
		$self->{rev}=1;
	}
	$self->{source}=$name{in}->[0];
	die "Can't use \"for\" without \"in\"" unless $self->{source};
	die "Can't use \"for\" without a variablename" unless @{$self->{var}};
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{content}=$obj->parse($data,$pos,\$found,"endfor","empty");
	if ($found eq "empty") {
		$self->{empty}=$obj->parse($data,$pos,\$found,"endfor");
	}
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $var=Dotiac::DTL::devar_content($self->{source},@_);
	my $varname=$self->{var}->[0];
	my $vars=shift;
	my $parent = $vars->{forloop};
	my @vars=@{$self->{var}};
	my $ref=Scalar::Util::reftype($var);
	if ($ref and $ref eq "HASH") {
		if (%{$var}) {
			my @loop = sort keys %{$var};
			@loop=reverse @loop if $self->{rev};
			my $varname2="";
			$varname2=$vars[1] if $#vars;
			foreach my $v (0 .. $#loop) {
				my $fl = {counter=>$v+1,counter0=>$v,revcounter=>@loop-$v,revcounter0=>$#loop-$v,first=>($v==0),last=>($v==$#loop),parentloop=>$parent,key=>$loop[$v]};
				my $x= $var->{$loop[$v]};
				my %d;
				if ($#vars) {
					$d{$varname2}=$x;
					$d{$varname}=$loop[$v];
				}
				else {
					$d{$varname}=$x;
				}
				$self->{content}->print({%{$vars},forloop=>$fl,%d},@_);
			}
		}
		else {
			$self->{empty}->print($vars,@_) if $self->{empty};
		}
	}
	else {
		my @loop=();
		#@loop = ($var) unless $self->{empty};
		if ($ref and $ref eq "ARRAY" and @{$var}) {
			@loop=@{$var};
		}
		else {
			$self->{empty}->print($vars,@_) if $self->{empty};
		}
		@loop=reverse @loop if $self->{rev};
		foreach my $v (0 .. $#loop) {
			my $fl = {counter=>($v+1),counter0=>$v,revcounter=>@loop-$v,revcounter0=>$#loop-$v,first=>($v==0),last=>($v==$#loop),parentloop=>$parent};
			my $x= $loop[$v];
			my %d;
			if (ref $x and ref $x eq "ARRAY" and $#vars) {
				my @d=@{$x};
				$#d=$#vars;
				@d{@vars}=@d;
			}
			else {
				$d{$varname}=$x;
			}
			$self->{content}->print({%{$vars},forloop=>$fl,%d},@_);
		}

	}
	
	$self->{n}->print($vars,@_);
}
sub string {
	my $self=shift;
	my $r="";
	my $var=Dotiac::DTL::devar_content($self->{source},@_);
	my $varname=$self->{var}->[0];
	my $vars=shift;
	my @vars=@{$self->{var}};
	my $parent = $vars->{forloop};
	my $ref=Scalar::Util::reftype($var);
	if ($ref and $ref eq "HASH") {
		if (%{$var}) {
			my @loop = sort keys %{$var};
			@loop=reverse @loop if $self->{rev};
			my $varname2="";
			$varname2=$vars[1] if $#vars;
			foreach my $v (0 .. $#loop) {
				my $fl = {counter=>$v+1,counter0=>$v,revcounter=>@loop-$v,revcounter0=>$#loop-$v,first=>($v==0),last=>($v==$#loop),parentloop=>$parent,key=>$loop[$v]};
				my $x= $var->{$loop[$v]};
				my %d;
				if ($#vars) {
					$d{$varname2}=$x;
					$d{$varname}=$loop[$v];
				}
				else {
					$d{$varname}=$x;
				}
				$r.=$self->{content}->string({%{$vars},forloop=>$fl,%d},@_);
			}
		}
		else {
			$r=$self->{empty}->string($vars,@_) if $self->{empty};
		}
	}
	else {
		my @loop=();
		#@loop = ($var) unless $self->{empty};
		if ($ref and $ref eq "ARRAY" and @{$var}) {
			@loop=@{$var};
		}
		else {
			$r=$self->{empty}->string($vars,@_) if $self->{empty};
		}
		@loop=reverse @loop if $self->{rev};
		foreach my $v (0 .. $#loop) {
			my $fl = {counter=>$v+1,counter0=>$v,revcounter=>@loop-$v,revcounter0=>$#loop-$v,first=>($v==0),last=>($v==$#loop),parentloop=>$parent};
			my $x=$loop[$v];
			my %d;
			if (ref $x and ref $x eq "ARRAY" and $#vars) {
				my @d=@{$x};
				$#d=$#vars;
				@d{@vars}=@d;
			}
			else {
				$d{$varname}=$x;
			}
			$r.=$self->{content}->string({%{$vars},forloop=>$fl,%d},@_);
		}

	}
	return $self->{p}.$r.$self->{n}->string($vars,@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{var}->[0]],["\$var$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{source}],["\$source$id"]));
	if (@{$self->{var}} > 1) {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{var}],["\$vars$id"]));
	}
	#if ($self->{empty}) {
	#	print $fh "my ";
	#	print $fh (Data::Dumper->Dump([$self->{empty}],["\$empty$id"]));
	#}
	$id=$self->{content}->perl($fh,$id+1,@_);
	$id=$self->{empty}->perl($fh,$id+1,@_) if $self->{empty};
	return $self->{n}->perl($fh,$id+1,@_);


	
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	my $in="\t" x $level;
	print $fh $in,"my \$forvar$id = Dotiac::DTL::devar_content(\$source$id,\$vars,\$escape,\@_);\n";
	print $fh $in,"my \$fortype$id = 0;\n";
	#print $fh $in,"my \@forloop$id = (".($self->{empty}?"":"\$forvar$id").");\n";
	print $fh $in,"my \@forloop$id = ();\n";
	print $fh $in,"my \$parentloop$id = \$vars->{forloop};\n";
	print $fh $in,"my \$forvars$id = \$vars;\n";
	print $fh $in,"my \$ref$id = Scalar::Util::reftype(\$forvar$id);\n";
	print $fh $in,"if (\$ref$id and \$ref$id eq \"HASH\" ) {\n";
	print $fh $in,"\t\$fortype$id = 1;\n";
	print $fh $in,"\t\@forloop$id = sort keys \%{\$forvar$id};\n";
	print $fh $in,"} elsif (\$ref$id and \$ref$id eq \"ARRAY\" ) {\n";
	print $fh $in,"\t\@forloop$id = \@{\$forvar$id};\n";
	print $fh $in,"}\n";
	print $fh $in,"\@forloop$id = reverse \@forloop$id;\n" if $self->{rev};
	if ($self->{empty}) {
		print $fh $in,"if (\@forloop$id) {\n";
		print $fh $in,"\tforeach my \$loop (0 .. \$#forloop$id) {\n";
		$level++;
	}
	else {
		print $fh $in,"foreach my \$loop (0 .. \$#forloop$id) {\n";
	}
	my $in2="\t" x ($level+1);
	print $fh $in2,"my \$vars={\%{\$forvars$id}};\n";
	print $fh $in2,"\$vars->{forloop} = {counter=>\$loop+1,counter0=>\$loop,revcounter=>\@forloop$id-\$loop,revcounter0=>\$#forloop$id-\$loop,first=>(\$loop==0),last=>(\$loop==\$#forloop$id),parentloop=>\$parentloop$id};\n";
	if (@{$self->{var}} > 1) {
		print $fh $in2,"if (\$fortype$id) {\n";
		print $fh $in2,"\t\$vars->{forloop}->{key}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"\t\$vars->{\$var$id"."}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"\t\$vars->{\$vars$id"."->[1]}=\$forvar$id"."->{\$forloop$id"."[\$loop]};\n";
		print $fh $in2,"} else {\n";
		print $fh $in2,"\tmy \$x = \$forloop$id"."[\$loop];\n";
		print $fh $in2,"\tif (ref \$x and ref \$x eq \"ARRAY\") {\n";
		print $fh $in2,"\t\tmy \@d=\@{\$x};\n";
		print $fh $in2,"\t\t\$#d=\$#\$vars$id;\n";
		print $fh $in2,"\t\t\@\$vars{\@\$vars$id}=\@d;\n";
		print $fh $in2,"\t}\n";
		print $fh $in2,"}\n";
	}
	else {
		print $fh $in2,"if (\$fortype$id) {\n";
		print $fh $in2,"\t\$vars->{forloop}->{key}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"\t\$vars->{\$var$id}=\$forvar$id"."->{\$forloop$id"."[\$loop]};\n";
		print $fh $in2,"} else {\n";
		print $fh $in2,"\t\$vars->{\$var$id}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"}\n";
	}
	$id = $self->{content}->perlprint($fh,$id+1,$level+1,@_);
	if ($self->{empty}) {
		print $fh $in,"\t}\n";
		print $fh $in,"} else {\n";
		$id = $self->{empty}->perlprint($fh,$id+1,$level+1,@_);
		print $fh $in,"}\n";
		$level--;
	}
	else {
		print $fh $in,"}\n";
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
	print $fh $in,"my \$forvar$id = Dotiac::DTL::devar_content(\$source$id,\$vars,\$escape,\@_);\n";
	print $fh $in,"my \$fortype$id = 0;\n";
	#print $fh $in,"my \@forloop$id = (".($self->{empty}?"":"\$forvar$id").");\n";
	print $fh $in,"my \@forloop$id = ();\n";
	print $fh $in,"my \$parentloop$id = \$vars->{forloop};\n";
	print $fh $in,"my \$forvars$id = \$vars;\n";
	print $fh $in,"my \$ref$id = Scalar::Util::reftype(\$forvar$id);\n";
	print $fh $in,"if (\$ref$id and \$ref$id eq \"HASH\" ) {\n";
	print $fh $in,"\t\$fortype$id = 1;\n";
	print $fh $in,"\t\@forloop$id = sort keys \%{\$forvar$id};\n";
	print $fh $in,"} elsif (\$ref$id and \$ref$id eq \"ARRAY\" ) {\n";
	print $fh $in,"\t\@forloop$id = \@{\$forvar$id};\n";
	print $fh $in,"}\n";
	print $fh $in,"\@forloop$id = reverse \@forloop$id;\n" if $self->{rev};
	if ($self->{empty}) {
		print $fh $in,"if (\@forloop$id) {\n";
		print $fh $in,"\tforeach my \$loop (0 .. \$#forloop$id) {\n";
		$level++;
	}
	else {
		print $fh $in,"foreach my \$loop (0 .. \$#forloop$id) {\n";
	}
	my $in2="\t" x ($level+1);
	print $fh $in2,"my \$vars={\%{\$forvars$id}};\n";
	print $fh $in2,"\$vars->{forloop} = {counter=>\$loop+1,counter0=>\$loop,revcounter=>\@forloop$id-\$loop,revcounter0=>\$#forloop$id-\$loop,first=>(\$loop==0),last=>(\$loop==\$#forloop$id),parentloop=>\$parentloop$id};\n";
	if (@{$self->{var}} > 1) {
		print $fh $in2,"if (\$fortype$id) {\n";
		print $fh $in2,"\t\$vars->{forloop}->{key}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"\t\$vars->{\$var$id"."}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"\t\$vars->{\$vars$id"."->[1]}=\$forvar$id"."->{\$forloop$id"."[\$loop]};\n";
		print $fh $in2,"} else {\n";
		print $fh $in2,"\tmy \$x = \$forloop$id"."[\$loop];\n";
		print $fh $in2,"\tif (ref \$x and ref \$x eq \"ARRAY\") {\n";
		print $fh $in2,"\t\tmy \@d=\@{\$x};\n";
		print $fh $in2,"\t\t\$#d=\$#\$vars$id;\n";
		print $fh $in2,"\t\t\@\$vars{\@\$vars$id}=\@d;\n";
		print $fh $in2,"\t}\n";
		print $fh $in2,"}\n";
	}
	else {
		print $fh $in2,"if (\$fortype$id) {\n";
		print $fh $in2,"\t\$vars->{forloop}->{key}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"\t\$vars->{\$var$id}=\$forvar$id"."->{\$forloop$id"."[\$loop]};\n";
		print $fh $in2,"} else {\n";
		print $fh $in2,"\t\$vars->{\$var$id}=\$forloop$id"."[\$loop];\n";
		print $fh $in2,"}\n";
	}
	$id = $self->{content}->perlstring($fh,$id+1,$level+1,@_);
	if ($self->{empty}) {
		print $fh $in,"\t}\n";
		print $fh $in,"} else {\n";
		$id = $self->{empty}->perlstring($fh,$id+1,$level+1,@_);
		print $fh $in,"}\n";
		$level--;
	}
	else {
		print $fh $in,"}\n";
	}
	return $self->{n}->perlstring($fh,$id+1,$level,@_);
}

sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{content}->perleval($fh,$id+1,@_);
	$id=$self->{empty}->perleval($fh,$id+1,@_) if $self->{empty};
	$self->{n}->perleval($fh,$id+1,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id=$self->{content}->perlcount($id+1,@_);
	$id=$self->{empty}->perlcount($id+1,@_) if $self->{empty};
	return $self->{n}->perlcount($id+1);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{content}->perlinit($fh,$id+1,@_);
	$id=$self->{empty}->perlinit($fh,$id+1,@_) if $self->{empty};
	return $self->{n}->perlinit($fh,$id+1);
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

Dotiac::DTL::Tag::for - The {% for VARIABLE1[|VARIABLE2[|VARIABLE3[|...]]] in VARIABLE %} tag

=head1 SYNOPSIS

Template file:

	{% for x in arrayloop %}
		{{ forloop.counter }}: {{ x }}
	{% endfor %}
	{% for x in hashloop %}
		{{ forloop.revcounter }}: {{ x }}
	{% endfor %}
	{% for key,value in hashloop %}
		{{key}} is {{ value }}
	{% endfor %}
	{% for x,y in arrayofarrayloop %}
		X = {{ x }}, Y = {{ y }} 
	{% endfor %}
	{% for x in emptyloop %}
		{{ forloop.counter }}: {{ x }}
	{% empty %}
		The loop is empty
	{% endfor %}

Perl-file:

	$t=Dotiac::DTL->new("page.html");
	$t->print(
		{
			arrayloop=>[1 .. 10],
			hashloop=>{A=>1,B=>2,C=>3,D=>4},
			arrayofarrayloop=[[1,10],[2,20],[3,30]]
		});

=head1 DESCRIPTION

Iterates over a datastructure, assigns the variable to every element of an array or hash and runs the included templatecode with it.

If the loop is empty and an {% empty %} tag is given, it will run the templatecode from {% empty %} to {% endfor %}.

If given one variable to assign with a hash, it will set it to the value, if given two, it will assign the key to the first and the value to the second variable.

If given more than one variable and a array of arrays, it will assagin the variables to the hash content.

See also L<http://docs.djangoproject.com/en/dev/ref/templates/builtins/#for> for more details and examples.

=head2 the forloop variable.

Inside a loop, these variables are set:

=over

=item forloop.counter

The current iteration of the loop, starting with 1.

=item forloop.counter0

The current iteration of the loop, starting with 0.

=item forloop.revcounter

The remaining iterations, starting ending with 1.

=item forloop.revcounter0

The remaining iterations, starting ending with 0.

=item forloop.first

True if this iteration is the first one.

=item forloop.last

True if this iteration is the last one.

=item forloop.parentloop

In nested loops, this is the one above the current

=back

=head1 BUGS AND DIFFERENCES TO DJANGO

Also sets forloop.key if iterating over a hash.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut


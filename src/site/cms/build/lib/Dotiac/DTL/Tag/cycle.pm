#cycle.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#cycle.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################



package Dotiac::DTL::Tag::cycle;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	my $list;
	my %list = Dotiac::DTL::get_variables($name,"as"); 
	die "Can't create cycle without arguments" unless @{$list{""}} or @{$list{as}};
	#warn '"',@{$list[0]},"--",@{$list[1]},'"' if @list == 2;
	#warn '"',@{$list[0]},'"' if @list == 1;
	if ($list{""} and @{$list{""}} == 1 and $list{""}->[0] =~ /,/) {
		@{$list{""}}=map {Dotiac::DTL::escap($_)} split /,/,$list{""}->[0];
	}
	if ($list{as}) {
		$self->{name}=$list{as}->[0];
		if ($list{""} and @{$list{""}}) {
			$self->{cycle}=[@{$list{""}}];
		}
		else {
			$self->{res}=1;
		}
		
	}
	else {
		my @cycle = @{$list{""}};
		if ($#cycle) {
			$self->{cycle}=[@cycle];
		}
		else {
			$self->{name}=$cycle[0];
		}
	}
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	if ($self->{name}) {
		if ($self->{cycle}) {
			if ($Dotiac::DTL::cycle{$self->{name}}) {
				$Dotiac::DTL::cycle{$self->{name}}->[1]=scalar @{$self->{cycle}};
				$Dotiac::DTL::cycle{$self->{name}}->[2]=$self->{cycle};
			}
			else {
				$Dotiac::DTL::cycle{$self->{name}}=[0,scalar @{$self->{cycle}},$self->{cycle}];
			}
		}
		elsif ($self->{res}) {
			$Dotiac::DTL::cycle{$self->{name}}->[0]=0 if $Dotiac::DTL::cycle{$self->{name}};
		}
		print Dotiac::DTL::devar($Dotiac::DTL::cycle{$self->{name}}->[2]->[$Dotiac::DTL::cycle{$self->{name}}->[0]++ % $Dotiac::DTL::cycle{$self->{name}}->[1]],@_)  if $Dotiac::DTL::cycle{$self->{name}};
	}
	elsif ($self->{cycle} or $Dotiac::DTL::cycle{$self}) {
		my $l;
		unless ($Dotiac::DTL::cycle{$self}) {
			$Dotiac::DTL::cycle{$self}=[0,scalar @{$self->{cycle}},$self->{cycle}];
		}
		#TODO: Find out how Django deals with cycle in for in for.
		#if ($_[0]->{forloop}->{counter0}) {
		#	$l=$_[0]->{forloop}->{counter0};
		#}
		#else {
			$l = $Dotiac::DTL::cycle{$self}->[0]++;
		#}
		print Dotiac::DTL::devar($Dotiac::DTL::cycle{$self}->[2]->[$l % $Dotiac::DTL::cycle{$self}->[1]],@_);
	}	
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	my $r=undef;
	if ($self->{name}) {
		if ($self->{cycle}) {
			if ($Dotiac::DTL::cycle{$self->{name}}) {
				$Dotiac::DTL::cycle{$self->{name}}->[1]=scalar @{$self->{cycle}};
				$Dotiac::DTL::cycle{$self->{name}}->[2]=$self->{cycle};
			}
			else {
				$Dotiac::DTL::cycle{$self->{name}}=[0,scalar @{$self->{cycle}},$self->{cycle}];
			}
		}
		elsif ($self->{res}) {
			$Dotiac::DTL::cycle{$self->{name}}->[0]=0 if $Dotiac::DTL::cycle{$self->{name}};
		}
		$r=$Dotiac::DTL::cycle{$self->{name}}->[2]->[$Dotiac::DTL::cycle{$self->{name}}->[0]++ % $Dotiac::DTL::cycle{$self->{name}}->[1]] if $Dotiac::DTL::cycle{$self->{name}};
	}
	elsif ($self->{cycle} or $Dotiac::DTL::cycle{$self}) {
		my $l;
		unless ($Dotiac::DTL::cycle{$self}) {
			$Dotiac::DTL::cycle{$self}=[0,scalar @{$self->{cycle}},$self->{cycle}];
		}
		#if ($_[0]->{forloop}->{counter0}) {
		#	$l=$_[0]->{forloop}->{counter0};
		#}
		#else {
			$l = $Dotiac::DTL::cycle{$self}->[0]++;
		#}
		$r=$Dotiac::DTL::cycle{$self}->[2]->[$l % $Dotiac::DTL::cycle{$self}->[1]];
	}
	return $self->{p}.Dotiac::DTL::devar($r,@_).$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my " if $self->{name};
	print $fh (Data::Dumper->Dump([$self->{name}],["\$name$id"])) if $self->{name};
	print $fh "my " if $self->{cycle};
	print $fh (Data::Dumper->Dump([$self->{cycle}],["\$cycle$id"])) if $self->{cycle};
	return $self->{n}->perl($fh,$id+1,@_);
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $digest=shift;
	use Carp; confess unless $digest; #TODO;
	my $in="\t"x$level;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	if ($self->{name}) {
		if ($self->{cycle}) {
			print $fh $in,"if (\$Dotiac::DTL::cycle{\$name$id}) {\n";
			print $fh $in,"\t\$Dotiac::DTL::cycle{\$name$id}->[1]=scalar \@{\$cycle$id};\n";
			print $fh $in,"\t\$Dotiac::DTL::cycle{\$name$id}->[2]=\$cycle$id;\n";
			print $fh $in,"}\n";
			print $fh $in,"else {\n";
			print $fh $in,"\t\$Dotiac::DTL::cycle{\$name$id}=[0,scalar \@{\$cycle$id},\$cycle$id];\n";
			print $fh $in,"}\n";
		}
		elsif ($self->{res}) {
			print $fh "$in\$Dotiac::DTL::cycle{\$name$id}->[0]=0 if \$Dotiac::DTL::cycle{\$name$id};\n";
		}	
		print $fh $in,"print Dotiac::DTL::devar(\$Dotiac::DTL::cycle{\$name$id}->[2]->[\$Dotiac::DTL::cycle{\$name$id}->[0]++ % \$Dotiac::DTL::cycle{\$name$id}->[1]],\$vars,\$escape,\@_)  if \$Dotiac::DTL::cycle{\$name$id};\n";
	}
	else {
		if ($self->{cycle}) {
			print $fh $in,"\$Dotiac::DTL::cycle{'$digest-$id'}=[0,scalar \@{\$cycle$id},\$cycle$id] unless \$Dotiac::DTL::cycle{'$digest-$id'};\n";
			print $fh $in,"print Dotiac::DTL::devar(\$Dotiac::DTL::cycle{'$digest-$id'}->[2]->[\$Dotiac::DTL::cycle{'$digest-$id'}->[0]++ % \$Dotiac::DTL::cycle{'$digest-$id'}->[1]],\$vars,\$escape,\@_);\n";
		}
		else {
			print $fh $in,"print Dotiac::DTL::devar(\$Dotiac::DTL::cycle{'$digest-$id'}->[2]->[\$Dotiac::DTL::cycle{'$digest-$id'}->[0]++ % \$Dotiac::DTL::cycle{'$digest-$id'}->[1]],\$vars,\$escape,\@_) if \$Dotiac::DTL::cycle{'$digest-$id'};\n";
		}
	}
	return $self->{n}->perlprint($fh,$id+1,$level,$digest,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $digest=shift;
	use Carp; confess unless $digest; #TODO;
	my $in="\t"x$level;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	if ($self->{name}) {
		if ($self->{cycle}) {
			print $fh $in,"if (\$Dotiac::DTL::cycle{\$name$id}) {\n";
			print $fh $in,"\t\$Dotiac::DTL::cycle{\$name$id}->[1]=scalar \@{\$cycle$id};\n";
			print $fh $in,"\t\$Dotiac::DTL::cycle{\$name$id}->[2]=\$cycle$id;\n";
			print $fh $in,"}\n";
			print $fh $in,"else {\n";
			print $fh $in,"\t\$Dotiac::DTL::cycle{\$name$id}=[0,scalar \@{\$cycle$id},\$cycle$id];\n";
			print $fh $in,"}\n";
		}
		elsif ($self->{res}) {
			print $fh "$in\$Dotiac::DTL::cycle{\$name$id}->[0]=0 if \$Dotiac::DTL::cycle{\$name$id};\n";
		}
		print $fh $in,"\$r.=Dotiac::DTL::devar(\$Dotiac::DTL::cycle{\$name$id}->[2]->[\$Dotiac::DTL::cycle{\$name$id}->[0]++ % \$Dotiac::DTL::cycle{\$name$id}->[1]],\$vars,\$escape,\@_)  if \$Dotiac::DTL::cycle{\$name$id};\n";
	}
	else {
		if ($self->{cycle}) {
			print $fh $in,"\$Dotiac::DTL::cycle{'$digest-$id'}=[0,scalar \@{\$cycle$id},\$cycle$id] unless \$Dotiac::DTL::cycle{'$digest-$id'};\n";
			print $fh $in,"\$r.=Dotiac::DTL::devar(\$Dotiac::DTL::cycle{'$digest-$id'}->[2]->[\$Dotiac::DTL::cycle{'$digest-$id'}->[0]++ % \$Dotiac::DTL::cycle{'$digest-$id'}->[1]],\$vars,\$escape,\@_);\n";
		}
		else {
			print $fh $in,"\$r.=Dotiac::DTL::devar(\$Dotiac::DTL::cycle{'$digest-$id'}->[2]->[\$Dotiac::DTL::cycle{'$digest-$id'}->[0]++ % \$Dotiac::DTL::cycle{'$digest-$id'}->[1]],\$vars,\$escape,\@_) if \$Dotiac::DTL::cycle{'$digest-$id'};\n";
		}
	}
	return $self->{n}->perlstring($fh,$id+1,$level,$digest,@_);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->{n}->perleval($fh,$id+1,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->{n}->perlinit($fh,$id+1,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
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

Dotiac::DTL::Tag::cycle - The {% cycle CYCLENAME | VAR1 VAR2 VAR3 VAR4... [as CYCLENAME] %} tag

=head1 SYNOPSIS

In a loop:

	{% for x in loop  %}
		<tr class="{% cycle "row1" "row2" variable %}"><td>{{ forloop.counter }}:</td><td>{{ x }}</td></tr>
		{# First row is now class="row1" #}
		{# Second row class="row2" #}
		{# Third row=class="whatever is in {{ variable }}" #}
		{# fourth row is again class="row1" and so on.... #}
	{% endfor %}

With a name outside a loop

	<tr class="{% cycle "row1" "row2" variable as rows %}">...</tr> {# name this cycle with the name "rows" #}
	<tr class="{% cycle rows %}">...</tr> {# Same cycle again just the name is needed#}
	{% if extrarow %}
		<tr class="{% cycle rows %}">...</tr> {# The cycle will only step one forward if extrarow is true #}
	{% endif %}
	<tr class="{% cycle rows %}">...</tr> {# value now depends on extrarow, even though this is not in the if-block #}


=head1 DESCRIPTION

Cycles trough a list of variables on every call.

There are two types of cycles, the ones in a loop, which just need a list of variables and just cycle through them. And the other one
is outside of a loop. That one assigns a list of variables to a name and can be called again by that name.

=head1 IMPLEMENTATION-SPECIFIC

I don't know about Django, but this implementation also supports this:

	{% "A" "B" "X" "Y" as foo %}
	{% for x in loop  %}
		{% cycle foo %} -- {{ x }} -- {% cycle foo %} {# rows are now either A -- x --B or X-- x --Y #}
	{% endfor %}

You can also redefine the cycle in this implementation, but beware, the counter is NOT reseted, if you have ABCABC of the cycle "ABC" printed already and then set it to "1234" that will print "ABCABC341234":

	{% cycle "A" "B" "X" "Y" as foo %}
	{% for x in loop  %}
		
		{% ifequal x "4" %} {% cycle "ONE" "one" "TWO" "two" "THREE" "three" as foo %}{% else %}{% cycle foo %}{% endifequal %} -- {{ x }} -- {% cycle foo %} {# rows are now either "A -- x -- B" or "X -- x -- Y" until x is four and then either "ONE -- x -- one" or "TWO -- x -- two" ... #}
	{% endfor %}

Reset the cycle-counter in this implementation with an empty variable list:
The reset tag will generate no output.

	{% cycle "A" "B" "C" "D" as foo %}
	{% for x in loop  %} {# Lets say loop is [1,2,3,4,5,...] #}
		{% ifequal x "3" %}{% cycle as foo %}{% endifequal %}
		{% cycle foo %} {# This will now result in "A B A B C D A ..." as it gets reseted before the third time #}
	{% endfor %}

=head1 BUGS AND DIFFERENCES TO DJANGO

The implementation specific addons might disappear if Django changes the syntax of this tag

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

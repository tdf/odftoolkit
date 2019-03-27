#spaceless.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#spaceless.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::spaceless;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

#This is not optimized during print.
sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{content}=$obj->parse($data,$pos,\$found,"endspaceless");
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $r=$self->{content}->string(@_);
	$r=~s/>\s+</> </g;
	print $r;
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	my $r=$self->{content}->string(@_);
	$r=~s/>\s+</> </g;
	return $self->{p}.$r.$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	$id=$self->{content}->perl($fh,$id+1,@_);
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
	print $fh $in,"{\n";
	print $fh $in,"\tmy \$r=\"\";\n";
	my $nid = $self->{content}->perlstring($fh,$id+1,$level+1,@_);
	print $fh $in,"\t\$r=~s/>\\s+</> </g;\n";
	print $fh $in,"\tprint \$r;\n";
	print $fh $in,"}\n";
	return $self->{n}->perlprint($fh,$nid+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	my $in="\t" x $level;
	print $fh $in,"my \$v$id=\"\";\n";
	print $fh $in,"{\n";
	print $fh $in,"\tmy \$r=\"\";\n";
	my $nid = $self->{content}->perlstring($fh,$id+1,$level+1,@_);
	print $fh $in,"\t\$r=~s/>\\s+</> </g;\n";
	print $fh $in,"\t\$v$id=\$r;\n";
	print $fh $in,"}\n";
	print $fh $in,"\$r.=\$v$id;\n";
	return $self->{n}->perlstring($fh,$nid+1,$level,@_);
}
sub perlcount {
	my $self=shift;
	my $id=shift;
	$id = $self->{content}->perlcount($id+1);
	return $self->{n}->perlcount($id+1);
}
sub perleval {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{content}->perleval($fh,$id+1,@_);
	return $self->{n}->perleval($fh,$id+1,@_);
}
sub perlinit {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$id=$self->{content}->perlinit($fh,$id+1,@_);
	return $self->{n}->perlinit($fh,$id+1,@_);
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

Dotiac::DTL::Tag::spaceless - The {% spaceless %} tag

=head1 SYNOPSIS

Template file:

	{% spaceless lower %}
	<body>
		<p>
			<br>
		</p>
		Text
	</body>
	{% endspaceless %} {# = hello world ..#}

This will result in:

	<body> <p> <br> </p>
		Text
	</body>


=head1 DESCRIPTION

Reduces all the spaces between tags in the output to a single space. The spaces between tags and text or text and text are left as they are.

=head1 BUGS AND DIFFERENCES TO DJANGO

The tag has to gather all the data first, so it will use remove the memory benefits coming from using print(), but only for the content inside the block.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut


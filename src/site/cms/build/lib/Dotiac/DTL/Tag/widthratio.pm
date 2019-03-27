#widthratio.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#widthratio.pm is published under the terms of the MIT license, which 
#basically means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::widthratio;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;
require Scalar::Util;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	my $name=shift;
	my @arg=Dotiac::DTL::get_variables($name);
	die "Wrong format for widthratio THISVALUE MAXVALUE 1221 \"\"" unless @arg > 2 and Scalar::Util::looks_like_number($arg[2]);
	$self->{cur}=$arg[0];
	$self->{max}=$arg[1];
	$self->{width}=$arg[2];
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $cur = Dotiac::DTL::devar($self->{cur},@_);
	$cur=0 unless Scalar::Util::looks_like_number($cur);
	my $max = Dotiac::DTL::devar($self->{max},@_);
	$max=$cur+1 unless Scalar::Util::looks_like_number($max);
	print int($self->{width}*$cur/$max+0.5);
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	my $cur = Dotiac::DTL::devar($self->{cur},@_);
	$cur=0 unless Scalar::Util::looks_like_number($cur);
	my $max = Dotiac::DTL::devar($self->{max},@_);
	$max=$cur+1 unless Scalar::Util::looks_like_number($max);
	return $self->{p}.int($self->{width}*$cur/$max+0.5).$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{width}],["\$width$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{max}],["\$wmax$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{cur}],["\$wcur$id"]));
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t"x$level,"my \$cur$id = Dotiac::DTL::devar(\$wcur$id,\$vars,\$escape,\@_);\n";
	print $fh "\t"x$level,"\$cur$id=0 unless Scalar::Util::looks_like_number(\$cur$id);\n";
	print $fh "\t"x$level,"my \$max$id = Dotiac::DTL::devar(\$wmax$id,\$vars,\$escape,\@_);\n";
	print $fh "\t"x$level,"\$max$id=\$cur$id+1 unless Scalar::Util::looks_like_number(\$max$id);\n";
	print $fh "\t"x$level,"print int(\$width$id*\$cur$id/\$max$id+0.5);\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh "\t"x$level,"my \$cur$id = Dotiac::DTL::devar(\$wcur$id,\$vars,\$escape,\@_);\n";
	print $fh "\t"x$level,"\$cur$id=0 unless Scalar::Util::looks_like_number(\$cur$id);\n";
	print $fh "\t"x$level,"my \$max$id = Dotiac::DTL::devar(\$wmax$id,\$vars,\$escape,\@_);\n";
	print $fh "\t"x$level,"\$max$id=\$cur$id+1 unless Scalar::Util::looks_like_number(\$max$id);\n";
	print $fh "\t"x$level,"\$r.=int(\$width$id*\$cur$id/\$max$id+0.5);\n";
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
	return $self->{n}->perleval($fh,$id+1,@_);
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
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::widthratio - The {% widthratio CURRENTVALUE MAXVALUE CONSTANT %} tag

=head1 SYNOPSIS

Template file:

	<img src="bar.png" width="{% widthratio current max 160 %}">

=head1 DESCRIPTION

Calculates the ratio of CURRENTVALUE to MAXVALUE and applies this to a CONSTANT.

CURRENTVALUE and MAXVALUES are variables and CONSTANT is a constant number.

This is useful if you want to create a bar for the progress of a multi-page form: CURRENVALUE is the current page, MAXVALUE is the total number of pages a user has to fill out. CONSTANT is then the size of the bar at a 100%, for example 160 for a 160px bar.

=head1 BUGS AND DIFFERENCES TO DJANGO

If you find any, please let me know.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut


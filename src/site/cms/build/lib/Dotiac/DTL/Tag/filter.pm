#filter.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#filter.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::filter;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	die "This filter needs some filters" unless $name;
	my @name=Dotiac::DTL::get_variables($name);
	$self->{filters}=[split /\|/,$name[0]];
	my $obj=shift;
	my $data=shift;
	my $pos=shift;
	my $found="";
	$self->{content}=$obj->parse($data,$pos,\$found,"endfilter");
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	my $vars=shift;
	my $escape=shift;
	print Dotiac::DTL::apply_filters($self->{content}->string($vars,$escape,@_),$vars,0,@{$self->{filters}})->string();
	$self->{n}->print($vars,$escape,@_);
}
sub string {
	my $self=shift;
	my $vars=shift;
	my $escape=shift;
	return $self->{p}.Dotiac::DTL::apply_filters($self->{content}->string($vars,$escape,@_),$vars,0,@{$self->{filters}})->string().$self->{n}->string($vars,$escape,@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{filters}],["\$filters$id"]));
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
	print $fh $in,"\tprint Dotiac::DTL::apply_filters(\$r,\$vars,0,\@{\$filters$id},\@_)->string();\n";
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
	print $fh $in,"\t\$v$id=\$r;\n";
	print $fh $in,"}\n";
	print $fh $in,"\$r.=Dotiac::DTL::apply_filters(\$v$id,\$vars,0,\@{\$filters$id})->string();\n";
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

Dotiac::DTL::Tag::filter - The {% filter FILTER1[|FILTER2[|FILTER3[|...]]] %} tag

=head1 SYNOPSIS

Template file:

	{% filter lower %}
		HELLO WORLD {% include "other.html" %}
	{% endfilter %} {# = hello world ..#}
	{% filter striptags|cut:"x" %}
		<img src="dirty.png">xxxTheManxxx
	{% endfilter %} {# = TheMan #}

=head1 DESCRIPTION

Applies a filter to the output of everything between {% filter %} and {% endfilter %}.

See L<Dotiac::DTL::Filter> for a list of available filters.

=head1 BUGS AND DIFFERENCES TO DJANGO

The tag has to gather all the data first, so it will use remove the memory benefits coming from using print(), but only for the content inside the filter.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

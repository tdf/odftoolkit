#load.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#load.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::load;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $module=shift;
	$module=~s/[^\w\s]/_/g;
	my @modules=split /\s+/,$module;
	$self->{module}=[map {"Dotiac::DTL::Addon::$_"} @modules];
	foreach my $m (@{$self->{module}}) {
                ($m) = $m =~ /(.*)/;
		eval "require $m";
		unless ($Dotiac::DTL::Addon::Loaded{$m}++) {
			"$m"->import();
			push @Dotiac::DTL::Addon::Loaded,"$m";
		}
		die $@ if $@;
	}
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	foreach my $m (@{$self->{module}}) {
		unless ($Dotiac::DTL::Addon::Loaded{$m}++) {
			"$m"->import();
			push @Dotiac::DTL::Addon::Loaded,"$m";
		}
	}	
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	foreach my $m (@{$self->{module}}) {
		unless ($Dotiac::DTL::Addon::Loaded{$m}++) {
			"$m"->import();
			push @Dotiac::DTL::Addon::Loaded,"$m";
		}
	}
	return $self->{p}.$self->{n}->string(@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	my @mods=grep !$Dotiac::DTL::Addon::NOCOMPILE{$_},@{$self->{module}};
	#die Data::Dumper->Dump([\%Dotiac::DTL::Addon::NOCOMPILE,[@mods]]);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([\@mods],["\$module$id"]));
	foreach my $m (@mods) {
		print $fh "require $m;\n";
		print $fh "unless (\$Dotiac::DTL::Addon::Loaded{\"$m\"}++) {\n";
		print $fh "\t$m->import();\n";
		print $fh "\t\tpush \@Dotiac::DTL::Addon::Loaded,\"$m\";\n";
		print $fh "\t}\n";
	}
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $in="\t" x $level;
	print $fh $in,"foreach my \$m (\@\$module$id) {\n";
	print $fh $in,"\tunless (\$Dotiac::DTL::Addon::Loaded{\$m}++) {\n";
	print $fh $in,"\t\t\"\$m\"->import();\n";
	print $fh $in,"\t\t\tpush \@Dotiac::DTL::Addon::Loaded,\"\$m\";\n";
	print $fh $in,"\t}\n";
	print $fh $in,"}\n";
	$self->SUPER::perlprint($fh,$id,$level,@_);
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $in="\t" x $level;
	print $fh $in,"foreach my \$m (\@\$module$id) {\n";
	print $fh $in,"\tunless (\$Dotiac::DTL::Addon::Loaded{\$m}++) {\n";
	print $fh $in,"\t\t\"\$m\"->import();\n";
	print $fh $in,"\t\t\tpush \@Dotiac::DTL::Addon::Loaded,\"\$m\";\n";
	print $fh $in,"\t}\n";
	print $fh $in,"}\n";
	$self->SUPER::perlstring($fh,$id,$level,@_);
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
	print $fh $in,"foreach my \$m (\@\$module$id) {\n";
	print $fh $in,"\tunless (\$Dotiac::DTL::Addon::Loaded{\$m}++) {\n";
	print $fh $in,"\t\t\"\$m\"->import();\n";
	print $fh $in,"\t\t\tpush \@Dotiac::DTL::Addon::Loaded,\"\$m\";\n";
	print $fh $in,"\t}\n";
	print $fh $in,"}\n";
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
	foreach my $m (@{$self->{module}}) {
		unless ($Dotiac::DTL::Addon::Loaded{$m}) {
			"$m"->import();
			push @Dotiac::DTL::Addon::Loaded,"$m";
		}
	}
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::load - The {% load NAME %} tag

=head1 SYNOPSIS

Template file:

	{% load markup %}
	{{ post.text|markdown }}

=head1 DESCRIPTION

Loads a library with a specific NAME, which may contain additional filters, tags or custom locale stettings. See L<Dotiac::DTL::Addon> for details.

=head1 BUGS AND DIFFERENCES TO DJANGO

This can't work at all like Django's {% load %}, since that one requires python. This implementation requires "Dotiac::DTL::Addon::$NAME", with all non-word characters in $NAME replace with underlines "_". It then calls the import() method of that module. See also L<Dotiac::DTL::Addon> for that.

Example:

	{% load Foo.bar+this %}

tries to require Django/Template/Addon/Foo_bar_this.pm and calls Dotiac::DTL::Foo_bar_this->import().

After the rendering is completed, Dotiac::DTL::Foo_bar_this->unimport() is called before the next render process.

=head2 Warning

Dotiac::DTL keeps the loaded locales and loaded addons active even after an include.

common.html:

	{% load addon1 addon2 addon3 klingon_locale %}

page.html:

	{% include "common.html" %}
	{{ a|addon1 }} {# This won't work in Django #}

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

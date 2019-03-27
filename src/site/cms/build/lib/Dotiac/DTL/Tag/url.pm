#url.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#url.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::url;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

#Bugs: Url in var (as) must be marked safe manually
sub new {
	my $class=shift;
	my $self={p=>shift()};
	my %name=Dotiac::DTL::get_variables(shift(),"as");
	my $name=join(",",@{$name{""}});
	if ($name{"as"}) {
		$self->{var}=$name{"as"}->[0];
	}
	my @url=split /\,/,$name;
	my $var=0;
	my @path;
	my @query;
	foreach my $u (@url) {
		if ($u =~/\=/) {
			$var=1;
			push @query,[split /=/,$u,2];
		}
		elsif ($var) {
			push @query,$u;
		}
		else {
			push @path,$u;
		}
	}
	$self->{path}=[@path];
	$self->{query}=[@query];
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	my $vars=shift;
	my $escape=shift;
	print $self->{p};
	#print join("/",map {Dotiac::DTL::devar($_,@_)} @{$self->{path}}),(@{$self->{query}}?"?".join("&",map {join "=",map {Dotiac::DTL::devar($_,@_)} @{$_}} @{$self->{query}}:"");
	if ($self->{var}) {
		$vars->{$self->{var}} = join("/",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),":/")->repr()} @{$self->{path}}).(@{$self->{query}}?"?".join("&",map {join "=",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),"")->repr()} @{$_}} @{$self->{query}}):"");
	}
	else {
		print Dotiac::DTL::Escape(join("/",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),":/")->repr()} @{$self->{path}}).(@{$self->{query}}?"?".join("&",map {join "=",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),"")->repr()} @{$_}} @{$self->{query}}):""),$escape);
	}
	$self->{n}->print($vars,$escape,@_);
}
sub string {
	my $self=shift;
	my $vars=shift;
	my $escape=shift;
	if ($self->{var}) {
		$vars->{$self->{var}} = join("/",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),":/")->repr()} @{$self->{path}}).(@{$self->{query}}?"?".join("&",map {join "=",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),"")->repr()} @{$_}} @{$self->{query}}):"");
		return $self->{p}.$self->{n}->string($vars,$escape,@_);
	}
	return $self->{p}.Dotiac::DTL::Escape(join("/",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),":/")->repr()} @{$self->{path}}).(@{$self->{query}}?"?".join("&",map {join "=",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),"")->repr()} @{$_}} @{$self->{query}}):""),$escape).$self->{n}->string($vars,$escape,@_);
	
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{path}],["\$path$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{query}],["\$query$id"]));
	if ($self->{var}) {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{var}],["\$var$id"]));
	}
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	if ($self->{var}) {
		print $fh "\t" x $level,"\$vars->{\$var$id}=join(\"/\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,\$escape,\@_),\":/\")->repr()} \@{\$path$id}).(\@{\$query$id}?\"?\".join(\"&\",map {join \"=\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\"\")->repr()} \@{\$_}} \@{\$query$id}):\"\");\n";
	}
	else {
		print $fh "\t" x $level,"print Dotiac::DTL::Escape(join(\"/\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,\$escape,\@_),\":/\")->repr()} \@{\$path$id}).(\@{\$query$id}?\"?\".join(\"&\",map {join \"=\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\"\")->repr()} \@{\$_}} \@{\$query$id}):\"\"),\$escape);\n";
	}
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	if ($self->{var}) {
		print $fh "\t" x $level,"\$vars->{\$var$id}=join(\"/\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\":/\")->repr()} \@{\$path$id}).(\@{\$query$id}?\"?\".join(\"&\",map {join \"=\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\"\")->repr()} \@{\$_}} \@{\$query$id}):\"\");\n";
	}
	else {
		print $fh "\t" x $level,"\$r.=Dotiac::DTL::Escape(join(\"/\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\":/\")->repr()} \@{\$path$id}).(\@{\$query$id}?\"?\".join(\"&\",map {join \"=\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\"\")->repr()} \@{\$_}} \@{\$query$id}):\"\"),\$escape);\n";
	}
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
	if ($self->{var}) {
		print $fh "\t" x $level,"\$vars->{\$var$id}=join(\"/\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,\$escape,\@_),\":/\")->repr()} \@{\$path$id}).(\@{\$query$id}?\"?\".join(\"&\",map {join \"=\",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw(\$_,\$vars,0,\@_),\"\")->repr()} \@{\$_}} \@{\$query$id}):\"\");\n";
	}
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
	my $vars=shift;
	my $escape=shift;
	if ($self->{var}) {
		$vars->{$self->{var}} = join("/",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),":/")->repr()} @{$self->{path}}).(@{$self->{query}}?"?".join("&",map {join "=",map {Dotiac::DTL::Filter::urlencode(Dotiac::DTL::devar_raw($_,$vars,0,@_),"")->repr()} @{$_}} @{$self->{query}}):"");
	}
	$self->{n}->eval($vars,$escape,@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::url - The {% url PATH,[PATH,[...],[PARAMETER=VALUE,[PARAMETER=VALUE,[..]]] [as VAR] %} tag

=head1 SYNOPSIS

Template file:

	{% url "forum","thread",variable,"id"=post.id %} {# forum/thread/444/?id=556 #}
	{% url "","forum","thread",variable,"id"=post.id %} {# /forum/thread/444/?id=556 #}
	{% url "http://www.google.com","forum","thread",variable,"id"=post.id %} {# http://www.google.com/forum/thread/444/?id=556 #}
	{% url "http://www.google.com","forum","thread",variable,"id"=post.id as link_url %} {# <nothing> #}
	{{ link_url|upper|safe }} {# HTTP://WWW.GOOGLE.COM/FORUM/THREAD/444/?ID=556 #}

=head1 DESCRIPTION

Generates an url from a joined PATH and adds also PARAMETERs with VALUES for get-queries.

When provided with an "as" and a variable name, it will output nothing and save the url into a variable, which can be used for further processing.

the PATH, PARAMETERs and VALUES are automatically url-encoded.

=head1 BUGS AND DIFFERENCES TO DJANGO

The normal Django {% url %} tag gets as a first parameter the name of Django-view, since there is no Django backend in this implementation, this is not possible.

When writing the url into a variable, that variable has to be marked safe manually, using the safe Filter (See L<Dotiac::DTL::Filter>)

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

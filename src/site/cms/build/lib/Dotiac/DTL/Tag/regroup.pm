#regroup.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#regroup.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::regroup;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;
use Scalar::Util;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	my %name = Dotiac::DTL::get_variables(shift(),"as","by");
	die "Can't use regroup without a variable" unless $name{""} and $name{""}->[0];
	die "Can't use regroup without a property" unless $name{by} and $name{by}->[0];
	die "Can't use regroup without a new variable name" unless $name{as} and $name{as}->[0];
	$self->{source}=$name{""}->[0];
	$self->{property}=$name{by}->[0];
	$self->{var}=$name{as}->[0];
	return $self;
}
sub print {
	my $self=shift;
	my $var = Dotiac::DTL::devar_content($self->{source},@_);
	my $r = Scalar::Util::reftype($var);
	my @data=($var);
	@data=values %{$var} if $r eq "HASH";
	@data=@{$var} if $r eq "ARRAY";
	my %data;
	my @ret;
	my $vars=shift;
	my $escape=shift;
	foreach my $e (@data) {
		my $g="";
		if (substr($self->{property},0,1) eq "`") {
			$g=Dotiac::DTL::devar_var("foo.".Dotiac::DTL::devar($self->{property},$vars,0,@_),{foo=>$e},0,@_)->string();
		}
		else {
			$g=Dotiac::DTL::devar("foo.".$self->{property},{%{$vars},foo=>$e},0,@_);
		}
		next unless $g;
		my $list = $data{$g};
		unless ($list) {
			$list=[];
			push @ret,{grouper=>$g,list=>$list} ;
			$data{$g}=$list;
		}		
		push @$list,$e;
	}
	$vars->{$self->{var}}=\@ret;
	print $self->{p};
	$self->{n}->print($vars,$escape,@_);
}
sub string {
	my $self=shift;
	my $var = Dotiac::DTL::devar_content($self->{source},@_);
	my $r = Scalar::Util::reftype($var);
	my @data=($var);
	@data=values %{$var} if $r eq "HASH";
	@data=@{$var} if $r eq "ARRAY";
	my %data;
	my @ret;
	my $vars=shift;
	my $escape=shift;
	foreach my $e (@data) {
		my $g="";
		if (substr($self->{property},0,1) eq "`") {
			$g=Dotiac::DTL::devar_var("foo.".Dotiac::DTL::devar($self->{property},$vars,0,@_),{foo=>$e},0,@_)->string();
		}
		else {
			$g=Dotiac::DTL::devar("foo.".$self->{property},{%{$vars},foo=>$e},0,@_);
		}
		next unless $g;
		my $list = $data{$g};
		unless ($list) {
			$list=[];
			push @ret,{grouper=>$g,list=>$list} ;
			$data{$g}=$list;
		}		
		push @$list,$e;
	}
	$vars->{$self->{var}}=\@ret;
	return $self->{p}.$self->{n}->string($vars,$escape,@_);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{var}],["\$varname$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{property}],["\$property$id"]));
	print $fh "my ";
	print $fh (Data::Dumper->Dump([$self->{source}],["\$source$id"]));
	return $self->{n}->perl($fh,$id+1,@_) if $self->{n};	
	return $id;
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $in="\t" x $level;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh $in,"my \$var$id = Dotiac::DTL::devar_content(\$source$id,\$vars,\$escape,\@_);\n";
	print $fh $in,"my \$r$id = Scalar::Util::reftype(\$var$id);\n";
	print $fh $in,"my \@data$id=(\$var$id);\n";
	print $fh $in,"\@data$id=values \%{\$var$id} if \$r$id eq \"HASH\";\n";
	print $fh $in,"\@data$id=\@{\$var$id} if \$r$id eq \"ARRAY\";\n";
	print $fh $in,"my \%data$id;\n";
	print $fh $in,"my \@ret$id;\n";
	print $fh $in,"foreach my \$e (\@data$id) {\n";
	print $fh $in,"\tmy \$g=\"\";\n";
	print $fh $in,"\tif (substr(\$property$id,0,1) eq \"`\") {\n";
	print $fh $in,"\t\t\$g=Dotiac::DTL::devar_var(\"foo.\".Dotiac::DTL::devar(\$property$id,\$vars,0,\@_),{foo=>\$e},0,\@_)->string();\n";
	print $fh $in,"\t} else {\n";
	print $fh $in,"\t\t\$g=Dotiac::DTL::devar(\"foo.\".\$property$id,{\%{\$vars},foo=>\$e},0,\@_);\n";
	print $fh $in,"\t}\n";
	print $fh $in,"\tnext unless \$g;\n";
	print $fh $in,"\tmy \$list = \$data$id"."{\$g};\n";
	print $fh $in,"\tunless (\$list) {\n";
	print $fh $in,"\t\t\$list=[];\n";
	print $fh $in,"\t\tpush \@ret$id,{grouper=>\$g,list=>\$list} ;\n";
	print $fh $in,"\t\t\$data$id"."{\$g}=\$list;\n";
	print $fh $in,"\t}\n";
	print $fh $in,"\tpush \@\$list,\$e;\n";
	print $fh $in,"}\n";
	print $fh $in,"\$vars->{\$varname$id}=\\\@ret$id;\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	my $in="\t" x $level;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh $in,"my \$var$id = Dotiac::DTL::devar_content(\$source$id,\$vars,\$escape,\@_);\n";
	print $fh $in,"my \$r$id = Scalar::Util::reftype(\$var$id);\n";
	print $fh $in,"my \@data$id=(\$var$id);\n";
	print $fh $in,"\@data$id=values \%{\$var$id} if \$r$id eq \"HASH\";\n";
	print $fh $in,"\@data$id=\@{\$var$id} if \$r$id eq \"ARRAY\";\n";
	print $fh $in,"my \%data$id;\n";
	print $fh $in,"my \@ret$id;\n";
	print $fh $in,"foreach my \$e (\@data$id) {\n";
	print $fh $in,"\tmy \$g=\"\";\n";
	print $fh $in,"\tif (substr(\$property$id,0,1) eq \"`\") {\n";
	print $fh $in,"\t\t\$g=Dotiac::DTL::devar_var(\"foo.\".Dotiac::DTL::devar(\$property$id,\$vars,0,\@_),{foo=>\$e},0,\@_)->string();\n";
	print $fh $in,"\t} else {\n";
	print $fh $in,"\t\t\$g=Dotiac::DTL::devar(\"foo.\".\$property$id,{\%{\$vars},foo=>\$e},0,\@_);\n";
	print $fh $in,"\t}\n";
	print $fh $in,"\tnext unless \$g;\n";
	print $fh $in,"\tmy \$list = \$data$id"."{\$g};\n";
	print $fh $in,"\tunless (\$list) {\n";
	print $fh $in,"\t\t\$list=[];\n";
	print $fh $in,"\t\tpush \@ret$id,{grouper=>\$g,list=>\$list} ;\n";
	print $fh $in,"\t\t\$data$id"."{\$g}=\$list;\n";
	print $fh $in,"\t}\n";
	print $fh $in,"\tpush \@\$list,\$e;\n";
	print $fh $in,"}\n";
	print $fh $in,"\$vars->{\$varname$id}=\\\@ret$id;\n";
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
	print $fh $in,"my \$var$id = Dotiac::DTL::devar_content(\$source$id,\$vars,\$escape,\@_);\n";
	print $fh $in,"my \$r$id = Scalar::Util::reftype(\$var$id);\n";
	print $fh $in,"my \@data$id=(\$var$id);\n";
	print $fh $in,"\@data$id=values \%{\$var$id} if \$r$id eq \"HASH\";\n";
	print $fh $in,"\@data$id=\@{\$var$id} if \$r$id eq \"ARRAY\";\n";
	print $fh $in,"my \%data$id;\n";
	print $fh $in,"my \@ret$id;\n";
	print $fh $in,"foreach my \$e (\@data$id) {\n";
	print $fh $in,"\tmy \$g=\"\";\n";
	print $fh $in,"\tif (substr(\$property$id,0,1) eq \"`\") {\n";
	print $fh $in,"\t\t\$g=Dotiac::DTL::devar_var(\"foo.\".Dotiac::DTL::devar(\$property$id,\$vars,0,\@_),{foo=>\$e},0,\@_)->string();\n";
	print $fh $in,"\t} else {\n";
	print $fh $in,"\t\t\$g=Dotiac::DTL::devar(\"foo.\".\$property$id,{\%{\$vars},foo=>\$e},0,\@_);\n";
	print $fh $in,"\t}\n";
	print $fh $in,"\tnext unless \$g;\n";
	print $fh $in,"\tmy \$list = \$data$id"."{\$g};\n";
	print $fh $in,"\tunless (\$list) {\n";
	print $fh $in,"\t\t\$list=[];\n";
	print $fh $in,"\t\tpush \@ret$id,{grouper=>\$g,list=>\$list} ;\n";
	print $fh $in,"\t\t\$data$id"."{\$g}=\$list;\n";
	print $fh $in,"\t}\n";
	print $fh $in,"\tpush \@\$list,\$e;\n";
	print $fh $in,"}\n";
	print $fh $in,"\$vars->{\$varname$id}=\\\@ret$id;\n";
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
	my $var = Dotiac::DTL::devar_content($self->{source},@_);
	my $r = Scalar::Util::reftype($var);
	my @data=($var);
	@data=values %{$var} if $r eq "HASH";
	@data=@{$var} if $r eq "ARRAY";
	my %data;
	my @ret;
	my $vars=shift;
	my $escape=shift;
	foreach my $e (@data) {
		my $g="";
		if (substr($self->{property},0,1) eq "`") {
			$g=Dotiac::DTL::devar_var("foo.".Dotiac::DTL::devar($self->{property},$vars,0,@_),{foo=>$e},0,@_)->string();
		}
		else {
			$g=Dotiac::DTL::devar("foo.".$self->{property},{%{$vars},foo=>$e},0,@_);
		}
		next unless $g;
		my $list = $data{$g};
		unless ($list) {
			$list=[];
			push @ret,{grouper=>$g,list=>$list} ;
			$data{$g}=$list;
		}		
		push @$list,$e;
	}
	$vars->{$self->{var}}=\@ret;
	$self->{n}->eval($vars,$escape,@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::regroup - The {% regroup LIST by PROPERTY as NEWVARIABLE %} tag

=head1 SYNOPSIS

Template file:

	{% regroup loop by gender as grouped %}
	{% for group in grouped %}
		<h1>{{ group.grouper }}</h1>
		{% for entry in group.list %}
			<p>{{ entry }}</p>
		{% endfor %}
	{% endfor %}

=head1 DESCRIPTION

Regroups a LIST of objects, hashes or list by a common PROPERTY and saves it into a NEW VARIABLE.

The resulting NEW VARIABLE is a list containing hashes with a "grouper" string, containing the text which was grouped by and a "list" which contains all the objects with the same "grouper" string

This is best explained with an example. If you have this datastructure, each a blog post with a category:

	Posts=>[
		{title=>"I love food",text=>"I really do",category=>"My life"},
		{title=>"I love TV",text=>"Even more than food",category=>"My life"},
		{title=>"Simpsons",text=>"Awesome TV show",category=>"TV shows"},
		{title=>"ANTM",text=>"I love this one",category=>"TV shows"},
		{title=>"xkcd",text=>"The best webcomic",category=>"Webcomics"}
	]

Now you want to group it by that "category" in the template:

	{% regroup Posts by category as posts_grouped %}
	{% for cat in posts_grouped %}
		<h1>Category: {{ cat.grouper }}</h1>
		{% for entry in cat.list %}
			<h2>{{ entry.title }}</h2>
			{{ entry.text|linebreaks }}
		{% endfor %}
	{% endfor %}

This will result in this rendered template:

	<h1>Category: My life</h1>
		<h2>I love food</h2>
		<p>I really do</p>
		<h2>I love TV</h2>
		<p>Even more than food</p>
	<h1>Category: TV shows</h1>
		<h2>Simpsons</h2>
		<p>Awesome TV show</p>
		<h2>ANTM</h2>
		<p>I love this one</p>
	<h1>Category: Webcomics</h1>
		<h2>xkcd</h2>
		<p>The best webcomic</p>


Django has another fine example for this: L<http://docs.djangoproject.com/en/dev/ref/templates/builtins/#regroup>

=head1 BUGS AND DIFFERENCES TO DJANGO

Django's regroup tag needs the LIST to be sorted, this implementation doesn't need it.

I don't know about Django, but here PROPERY can also contain filters.

	{% regroup loop by content|length as grouped %}

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

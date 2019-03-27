#ssi.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#ssi.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::ssi;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	my $name=shift;
	die "This ssi needs a filename or variable" unless $name;
	die "\$Dotiac::DTL::ALLOWED_INCLUDE_ROOTS is not set, can't use this tag" unless $Dotiac::DTL::ALLOWED_INCLUDE_ROOTS;
	#my $parsed=$name=~s/\s+parsed$//;
	my @name=Dotiac::DTL::get_variables($name);
	$self->{parsed}=1 if $name[-1] and $name[-1] eq "parsed";
	my $f=substr $name[0],0,1;
	my $e=substr $name[0],-1,1;
	if ($f eq "`" and $e eq "`") {
		$self->{load}=Dotiac::DTL::devar($name[0],{},0);
		if ($self->{parsed}) {
			my $tem = Dotiac::DTL->safenew($self->{load});
			$self->{content}=$tem->{first};
		}
		else {
			open my $fh,"<",substr($name,1,-1) or die "Can't open ssi include $name";
			$self->{content}=Dotiac::DTL::Tag->new(do {local $/;<$fh>});
			close $fh;
		}
	}
	else {
		$self->{var}=$name[0];
	}
	bless $self,$class;
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	if ($self->{content}) {
		$self->{content}->print(@_);
	}
	else {
		my $tem = Dotiac::DTL::devar_repr($self->{var},@_);
		if ($self->{parsed}) {
			my $tem = Dotiac::DTL->safenew($tem);
			$tem->{first}->print(@_);
		}
		else {
			open my $fh,"<",$tem or die "Can't open ssi include from var :\"$tem\"";
			print do {local $/;<$fh>};
			close $fh;
		}
	}
	$self->{n}->print(@_) if $self->{n};
}
sub string {
	my $self=shift;
	if ($self->{content}) {
		return $self->{p}.$self->{content}->string(@_).($self->{n}?$self->{n}->string(@_):"");
	}
	my $tem = Dotiac::DTL::devar_repr($self->{var},@_);
	if ($self->{parsed}) {
		$tem = Dotiac::DTL->safenew($tem);
		return $self->{p}.$tem->{first}->string(@_).($self->{n}?$self->{n}->string(@_):"");
	}
	else {
		open my $fh,"<",$tem or die "Can't open ssi include from var :\"$tem\"";
		return $self->{p}.do {local $/;<$fh>}.($self->{n}?$self->{n}->string(@_):"");
		close $fh;
	}
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	if ($self->{content}) {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{load}],["\$name$id"]));
		if ($self->{parsed}) {
			print $fh "my \$template$id=Dotiac::DTL->safenew(\$name$id);\n";
		}
		else {
			print $fh "open my \$fh$id,'<',\$name$id or die \"Can't SSI \$name$id: \$!\";\n";
			print $fh "my \$template$id=do {local \$/;<\$fh$id>};\n";
			print $fh "close \$fh$id;\n";
		}
	}
	else {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{var}],["\$name$id"]));
	}
	return $self->{n}->perl($fh,$id+1,@_);
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	my $in="\t" x $level;
	if ($self->{content}) {
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$template$id}++;\n" if $self->{parsed};
		print $fh "$in\$template$id"."->{first}->print(\$vars,\$escape,\@_);\n" if $self->{parsed};
		print $fh $in,"print \$template$id;\n" unless $self->{parsed};
		print $fh $in,"\$Dotiac::DTL::included{\$template$id}=0;\n" if $self->{parsed};
	}
	else {
		if ($self->{parsed}) {
			print $fh $in,"my \$template$id = Dotiac::DTL::devar_repr(\$name$id,\$vars,\$escape,\@_);\n";
			print $fh $in,"my \$s$id=\$template$id;\n";
			print $fh $in,"if (not ref \$template$id) {\n";
			print $fh $in,"\t\$template$id = Dotiac::DTL->new(\$template$id);\n";
			print $fh $in,"} else {\n";
			print $fh $in,"\tdie \"Can't ssi with \\\"\$template$id\\\"\";\n";
			print $fh $in,"}\n";
			print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$s$id}++;\n";
			print $fh $in,"\$template$id"."->{first}->print(\$vars,\$escape,\@_);\n";
			print $fh $in,"\$Dotiac::DTL::included{\$s$id}=0;\n";
		}
		else {
			print $fh $in,"open my \$fh$id,'<',Dotiac::DTL::devar_repr(\$name$id,\$vars,\$escape,\@_) or die \"Can't SSI \$name$id: \$!\";\n";
			print $fh $in,"print do {local \$/;<\$fh$id>};\n";
			print $fh $in,"close \$fh$id;\n";
		}
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
	if ($self->{content}) {
		print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$template$id}++;\n" if $self->{parsed};
		print $fh "$in\$r.=\$template$id"."->{first}->string(\$vars,\$escape,\@_);\n" if $self->{parsed};
		print $fh "$in\$r.=\$template$id;\n" unless $self->{parsed};
		print $fh $in,"\$Dotiac::DTL::included{\$template$id}=0;\n" if $self->{parsed};
	}
	else {
		if ($self->{parsed}) {
			print $fh $in,"my \$template$id = Dotiac::DTL::devar_repr(\$name$id,\$vars,\$escape,\@_);\n";
			print $fh $in,"my \$s$id=\$template$id;\n";
			print $fh $in,"if (not ref \$template$id) {\n";
			print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id);\n";
			print $fh $in,"} else {\n";
			print $fh $in,"\tdie \"Can't ssi with \\\"\$template$id\\\"\";\n";
			print $fh $in,"}\n";
			print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$s$id}++;\n";
			print $fh $in,"\$r.=\$template$id"."->{first}->string(\$vars,\$escape,\@_);\n";
			print $fh $in,"\$Dotiac::DTL::included{\$s$id}=0;\n";
		}
		else {
			print $fh $in,"open my \$fh$id,'<',Dotiac::DTL::devar_repr(\$name$id,\$vars,\$escape,\@_) or die \"Can't SSI \$name$id: \$!\";\n";
			print $fh $in,"\$r.=do {local \$/;<\$fh$id>};\n";
			print $fh $in,"close \$fh$id;\n";
		}
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
	my $in="\t" x $level;
	if ($self->{parsed}) {
		if ($self->{content}) {
			print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$template$id}++;\n";
			print $fh "$in\$template$id"."->{first}->eval(\$vars,\$escape,\@_);\n";
			print $fh $in,"\$Dotiac::DTL::included{\$template$id}=0;\n";
		}
		else {
			print $fh $in,"my \$template$id = Dotiac::DTL::devar_repr(\$name$id,\$vars,\$escape,\@_);\n";
			print $fh $in,"my \$s$id=\$template$id;\n";
			print $fh $in,"if (not ref \$template$id) {\n";
			print $fh $in,"\t\$template$id = Dotiac::DTL->safenew(\$template$id);\n";
			print $fh $in,"} else {\n";
			print $fh $in,"\tdie \"Can't ssi with \\\"\$template$id\\\"\";\n";
			print $fh $in,"}\n";
			print $fh $in,"die \"Cyclic include detected \" if \$Dotiac::DTL::included{\$s$id}++;\n";
			print $fh $in,"\$template$id"."->{first}->eval(\$vars,\$escape,\@_);\n";
			print $fh $in,"\$Dotiac::DTL::included{\$s$id}=0;\n";
		}
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
	if ($self->{parsed}) {
		if ($self->{content}) {
			$self->{content}->eval(@_) ;
		}
		else {
			my $tem = Dotiac::DTL::devar($self->{var},@_);
			my $s=$tem;
			if (not ref $tem) {
				$tem = Dotiac::DTL->safenew($tem);
			}
			else {
				die "Can't ssi with \"$tem\"";
			}
			die "Cyclic include detected " if $Dotiac::DTL::included{$s}++;
			$tem->{first}->eval(@_);
			$Dotiac::DTL::included{$s}=0;
		}
	}
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::ssi - The {% ssi FILE [parsed] %} tag

=head1 SYNOPSIS

Template file:

	Some text {% ssi "/home/foo/web/index.html" %}
	Some other text {% ssi "/home/foo/web/djangoindex.html" parsed %}

=head1 DESCRIPTION

Similar to the {% include %} (L<Dotiac::DTL::Tag::include>) tag, but includes FILEs to be included from anywhere in the filesystem. So this tag will only work if $Dotiac::DTL::ALLOWED_INCLUDE_ROOTS is set to true (See L<Dotiac::DTL::Core>).

If there is a "parsed" at the end of the tag the file is treated as a template. (like {% include %}) If it isn't there, just the text is included, no matter what is in there.

=head1 BUGS AND DIFFERENCES TO DJANGO

The ssi-tag can't work with template objects, there is no need, use the include-tag for that.

If you find any, please let me know.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

###############################################################################
#Core.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Core.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Core;

our $VERSION = 0.8;

package Dotiac::DTL;
require Dotiac::DTL::Value;
require Dotiac::DTL::Template;
require Dotiac::DTL::Filter;
require Dotiac::DTL::Compiled;

use strict;
use warnings;
use Scalar::Util qw/reftype blessed/;
use Carp;
require File::Spec;
require File::Basename;

#These go into the context.
our $TEMPLATE_STRING_IF_INVALID=""; #If there was no parameter found
our $ALLOW_METHOD_CALLS=1;
our $ALLOWED_INCLUDE_ROOTS=0; #Allows the ssi tag
our $AUTOESCAPING=1; #Default auto escape or not
our $DATETIME_FORMAT='N j, Y, P';
our $DATE_FORMAT='N j, Y';
our $TIME_FORMAT='P';
our @TEMPLATE_DIRS=(); #Only used by Template();
our $Max_Depth=3;
our $CURRENTDIR="";
our $PARSER="Dotiac::DTL::Parser";

#This has to change someday. not global
our %blocks; #this needs to be global, sadly.
our %cycle; #Also needs to be global.
our %globals; #Well we already have other globals, this saves me the init() trough the whole tree/list.


our %included;
our %params;


# Template cache, needs to be global
my %cache;

sub new {
	my $class=shift;
	my $data=shift; 
	my $t="";
	%params=();
	if (ref $data eq "SCALAR") {
		die "Dotiac::DTL::Reduced can only work with compiled templates, use Dotiac::DTL for the full interface";
	}
	elsif (not ref $data) {
		$t=$data;
		my @f = File::Basename::fileparse($data);
		$Dotiac::DTL::currentdir=$f[1];
		if (-e "$data.pm") {
			if ($cache{"$data.pm"} and exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} < (stat("$data.pm"))[9]) {
				carp "Foo";
				delete $cache{"$data.pm"};
				delete $INC{"$data.pm"};
			}
			if (-e $data) {
				if ((stat("$data.pm"))[9] >= (stat("$data"))[9]) {
					eval {
						$cache{"$data.pm"}={
							template=>Dotiac::DTLCompiled->new("Dotiac::DTL::Compiled::".require "$data.pm"),
							currentdir=>$Dotiac::DTL::currentdir,
							params=>{%Dotiac::DTL::params},
							parser=>$Dotiac::DTL::PARSER,
							changetime=>(stat("$data.pm"))[9]
						} if not $cache{"$data.pm"};# or (exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} > -M "$data.pm"); #Can't do it, Require won't return the filename a second time, has to be solved differently by manually modifing %INC
						$t="$data.pm";
						1;
					} or do {
						croak "Error while getting compiled template $data.pm and can't use $data, because this is Reduced:\n $@\n.";
						undef $@;
					};	
				}
				else {
					carp "$data seem to outdate $data.pm, but Dotiac::DTL::Reduced can only work with compiled templates, use Dotiac::DTL to recompile";
					eval {
						$cache{"$data.pm"}={
							template=>Dotiac::DTLCompiled->new("Dotiac::DTL::Compiled::".require "$data.pm"),
							currentdir=>$Dotiac::DTL::currentdir,
							params=>{%Dotiac::DTL::params},
							parser=>$Dotiac::DTL::PARSER,
							changetime=>(stat("$data.pm"))[9]
						} if not $cache{"$data.pm"};# or (exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} > -M "$data.pm");
						$t="$data.pm";
						1;
					} or do {
						croak "Error while getting compiled template $data.pm and can't use $data, because this is Reduced:\n $@\n.";
						undef $@;
					};
				}
			}
			else {
				eval {
					$cache{"$data.pm"}={
						template=>Dotiac::DTL::Compiled->new("Dotiac::DTL::Compiled::".require "$data.pm"),
						currentdir=>$Dotiac::DTL::currentdir,
						params=>{%Dotiac::DTL::params},
						parser=>$Dotiac::DTL::PARSER,
						changetime=>(stat("$data.pm"))[9]
					} if not $cache{"$data.pm"};# or (exists $cache{"$data.pm"}->{changetime} and $cache{"$data.pm"}->{changetime} > -M "$data.pm");
					$t="$data.pm";
					1;
				} or do {
					croak "Error while getting compiled template $data.pm and $data is gone:\n $@\n.";
					undef $@;
				};	
			}
		}
		unless ($cache{$t})  {	
			croak "Dotiac::DTL::Reduced can only work with compiled templates, use Dotiac::DTL for the full interface";
		}
	}
	else {
		die "Can't work with $data!";
	}
	#$self->{data}=$data;
	Dotiac::DTL::Addon::restore();
	if ($cache{$t}) {
		return "Dotiac::DTL::Template"->new($cache{$t}->{template},$cache{$t}->{currentdir},$cache{$t}->{parser},$cache{$t}->{params});
	}
	else {
		croak "Dotiac::DTL::Reduced can only work with compiled templates, use Dotiac::DTL for the full interface";
	}
}

our $currentdir="";

sub safenew {
	my $class=shift;
	my $file=shift;
	unless ($ALLOWED_INCLUDE_ROOTS and int($ALLOWED_INCLUDE_ROOTS) > 2) {
		$file=~s/^[\\\/]//g;
		$file=~s/^\w+\://g; #Windows GRR
		1 while $file=~s/^\.\.[\\\/]//g;
	}
	unless ( -e $file or -e "$file.pm") {
		my $rfile=File::Spec->catfile(".",$currentdir,$file);
		foreach my $dir (@Dotiac::DTL::TEMPLATE_DIRS) {
			$rfile=File::Spec->catfile($dir,"$file.html") and last if -e File::Spec->catfile($dir,"$file.html");
			$rfile=File::Spec->catfile($dir,"$file.txt") and last if -e File::Spec->catfile($dir,"$file.txt");
			$rfile=File::Spec->catfile($dir,$file) and last if -e File::Spec->catfile($dir,$file);
		}
		return Dotiac::DTL->new($rfile) if -e $rfile or -e "$rfile.pm";
	}
	my $p=$Dotiac::DTL::PARSER;
	my $r=Dotiac::DTL->new($file);
	$Dotiac::DTL::PARSER=$p;
	return $r;
}

sub compiled {
	my $class=shift;
	my $name=shift;
	my $f;
	$Dotiac::DTL::currentdir=$Dotiac::DTL::CURRENTDIR;
	%params=();
	eval {
		$f=Dotiac::DTL::Compiled->new($name);
		1;
	} or do {
		croak "Error while getting compiled template from $name\n $@\n.";
		undef $@;
	};	
	undef $@;
	return "Dotiac::DTL::Template"->new($f,$Dotiac::DTL::CURRENTDIR);
}



sub urlencode {
	my $val=shift;
	$val = eval { pack("C*", unpack("U0C*", $val))} || pack("C*", unpack("C*", $val));
	$val=~s/([^a-zA-Z0-9_.~-])/uc sprintf("%%%02x",ord($1))/eg;
	return $val;
}

sub escap { #Escape is used too much these days.
	my $string=shift;
	$string=~s/\\n/\n/g;
	$string=~s/\\t/\t/g;
	$string=~s/\\r/\r/g;
	$string=~s/\\b/\b/g;
	$string=~s/\\f/\f/g;
	$string=~s/\\x([\dA-Fa-f]{2})/chr(hex($1))/eg;
	$string=~s/\\u([\dA-Fa-f]{4})/chr(hex($1))/eg;
	$string=~s/\\U([\dA-Fa-f]{8})/chr(hex($1))/eg;
	$string=~s/\\(["'{}])/$1/g;
	#$string=~s/\\([^\\])/die/eg;
	$string=~s/\\\\/\\/g; 
	#TODO more pyhton escape seq.
	$string=~s/([\|\s\,\"\'\`\%\:;=])/sprintf("%%%02X",ord($1))/eg;
	return "`$string`";
}

sub descap {
	my $string=shift;
	$string=~s/%([\da-fA-F]{2})/chr(hex($1))/eg;
	return $string;
}

sub get_variables {
	my $x=shift;
	$x="" if not defined $x or ref $x;
	while ($x=~m/[^\"\']*([\"\'])/g) {
		my $opos=pos($x);
		if ($1 eq '"') {
			$x=~m/((?>(?:(?>[^"\\]+)|\\.)*))"/g;
			die "Syntax error in $1..$1 of $x" unless pos($x);
			my $replace=escap($1);
			substr($x,$opos-1,pos($x)+1-$opos)=$replace;
			pos($x)=$opos+length($replace);
		}
		else {
			$x=~m/((?>(?:(?>[^'\\]+)|\\.)*))'/g;
			die "Syntax error in $1..$1 of $x" unless pos($x);
			my $replace=escap($1);
			substr($x,$opos-1,pos($x)+1-$opos)=$replace;
			pos($x)=$opos+length($replace);
		}
		die "Syntax error in $1..$1 of $x" unless pos($x);
	}
	#warn "var::$x";
	if (@_) {
		my %words;
		@words{@_}=(1) x scalar @_;
		my %ret;
		my $keywords = "(?:^|\\s+)".join ("(?:\\s+|\$)|(?:^|\\s+)",@_)."(?:\\s+|\$)";
		#print STDERR "@_: $keywords\n";
		my @l = split /($keywords)/,$x;
		#print STDERR join(", ",@l)."\n";
		unshift @l,"";
		while (defined(my $k=shift @l)) {
			$k=~s/^\s+//g;
			$k=~s/\s+$//g;
			if (@l) {
				my $next=$l[0];
				$next=~s/^\s+//g;
				$next=~s/\s+$//g;
				if ($words{$next}) {
					$ret{$k}=[];
				}
				else {
					my @a=split /\s+/,shift(@l);
					$ret{$k}=[@a];
					foreach my $a (@a) {
						$Dotiac::DTL::params{$a}++;
					}
				}
			}
			else {
				$ret{$k}=[];
			}	
		}
		return %ret;
	}
	my @a= split /\s+/,$x;
	foreach my $a (@a) {
		$Dotiac::DTL::params{$a}++;
	}
	return @a;
}

sub Escape {
	my $var=shift;
	return Dotiac::DTL::Value->escape($var)->string() if $_[0];
	return $var;
}

sub Conditional {
	my $var=shift;
	return "" unless $var;
	return $var unless ref $var;
	return $var->count() if Scalar::Util::blessed($var) and $var->can("count");
	return 1 if Scalar::Util::blessed($var);
	# return scalar @{$var} if ref $var eq "ARRAY";
	# return scalar keys %{$var} if ref $var eq "HASH";
	return 1;
}

sub apply_filters {
	my $value=shift; 
	my $vars=shift;
	my $escape=shift;
	#$escape=0 if $STRING_IS_LITERAL; #TODO
	#$VARIABLE_IS_SAFE=!$escape;
	unless (Scalar::Util::blessed($value) and $value->isa("Dotiac::DTL::Value")) {
		$value=Dotiac::DTL::Value->new($value,!$escape);
	}
	foreach my $f (@_) {
		my ($filter,$param)=split /:/,$f,2;
		$filter=lc $filter;
		eval {
			no strict "refs"; #I hate to do this, does anyone know a better one without eval?
			$value="Dotiac::DTL::Filter::$filter"->($value,defined $param?(map {devar_var($_,$vars,0)} split /[,;]/,$param):());
		};
		if ($@) {
			die "Filter '$filter' couldn't be found or an error occurred. The filter has to be in the Dotiac::DTL::Filter namespace\n$@";
		}
		die "Filter Error: $filter did not return a Dotiac::DTL::Value" unless Scalar::Util::blessed($value) and $value->isa("Dotiac::DTL::Value");
	}
	return $value;
}

sub devar {
	my $name=shift;
	return "" unless defined $name;
	my @data= split/\|/,$name;
	$name=shift @data;
	my $param=shift;
	my $escape=shift;
	my $var=devar_var($name,$param,$escape,@_);
	unless (@data) {
		return $var->string();
	}
	$var=apply_filters($var,$param,$escape,@data);
	return $var->string();

}

sub devar_nodefault {
	my $name=shift;
	return "" unless defined $name;
	my @data= split/\|/,$name;
	$name=shift @data;
	my $param=shift;
	my $escape=shift;
	my $var=devar_var($name,$param,$escape,@_);
	unless (@data) {
		return $var->stringnodefault();
	}
	$var=apply_filters($var,$param,$escape,@data);
	return $var->stringnodefault();

}

sub devar_raw {
	my $name=shift;
	return "" unless defined $name;
	my @data= split/\|/,$name;
	$name=shift @data;
	my $param=shift;
	my $escape=shift;
	my $var=devar_var($name,$param,$escape,@_);
	unless (@data) {
		return $var;
	}
	$var=apply_filters($var,$param,$escape,@data);
	return $var;

}

sub devar_content {
	my $name=shift;
	return "" unless defined $name;
	my @data= split/\|/,$name;
	$name=shift @data;
	my $param=shift;
	my $escape=shift;
	my $var=devar_var($name,$param,$escape,@_);
	unless (@data) {
		use Carp qw/confess/;
		confess unless ref $var;
		return $var->content();
	}
	$var=apply_filters($var,$param,$escape,@data);
	return $var->content();

}

sub devar_repr {
	my $name=shift;
	return "" unless defined $name;
	my @data= split/\|/,$name;
	$name=shift @data;
	my $param=shift;
	my $escape=shift;
	my $var=devar_var($name,$param,$escape,@_);
	unless (@data) {
		use Carp qw/confess/;
		confess unless ref $var;
		return $var->repr();
	}
	$var=apply_filters($var,$param,$escape,@data);
	return $var->repr();

}

sub devar_var {
	my $name=shift;
	my $n=$name;
	return Dotiac::DTL::Value->safe(undef) unless defined $name;
	my $param=shift;
	my $f=substr $name,0,1;
	my $l=substr $name,-1,1;
	my $escape=shift;
	#TODO
	use Carp;
	confess $param unless ref $param;
	confess $escape unless defined $escape;
	#confess @_ unless @_;
	#TODO
	return Dotiac::DTL::Value->safe(substr $name,1,-1) if $f eq "'" and $l eq "'" or $f eq '"' and $l eq '"';
	return Dotiac::DTL::Value->safe(descap(substr $name,1,-1)) if $f eq "`" and $l eq "`";
	if ($name eq "block.super" and $param->{"block.super"}) {
		return Dotiac::DTL::Value->safe($param->{"block.super"}->string($param,@_)) if Scalar::Util::blessed($param->{"block.super"});
		return Dotiac::DTL::Value->safe($param->{"block.super"}->($param,@_)) if ref $param->{"block.super"} eq "CODE";
	}
	return Dotiac::DTL::Value->new($param->{$name},!$escape) if exists $param->{$name};
	my @tree=split/\./,$name;
	$name=shift @tree;
	unless (exists $param->{$name}) {
		return Dotiac::DTL::Value->safe($n) if $n!~/[^\d\-\.\,\e]/;
		if ($cycle{$name} and $cycle{$name}->[1]) {
			return Dotiac::DTL::Value->safe("") if $included{"cycle_$name"}++;
			my $r=devar_raw($cycle{$name}->[2]->[$cycle{$name}->[0]-1 % $cycle{$name}->[1]],$param,$escape,@_);
			$included{"cycle_$name"}=0;
			return $r;
		}
		return Dotiac::DTL::Value->safe(undef) ;
	}
	$param=$param->{$name};
	while (defined(my $name = shift @tree)) {
		my $r = reftype $param;
		if ($r) {
			if ($r eq "HASH") {
				if (not exists $param->{$name}) {
					return Dotiac::DTL::Value->safe(undef) unless blessed $param;
				}
				else {
					$param=$param->{$name};
					next;
				}
			}
			elsif ($r eq "ARRAY") {
				if ($name=~m/\D/) {
					return Dotiac::DTL::Value->safe(undef) unless blessed $param;
				}
				else {
					if (not exists $param->[$name]) {
						return Dotiac::DTL::Value->safe(undef) unless blessed $param;
					}
					else {
						$param=$param->[$name];
						next;
					}
				}
			}
		}
		if (blessed $param) {
			return Dotiac::DTL::Value->safe(undef) unless $ALLOW_METHOD_CALLS; 
			if ($param->can($name)) {
				$param=$param->$name();
				next;
			}
			elsif ($param->can("__getitem__")) {
				my $x;
				eval {
					$x=$param->__getitem__($name);
					1;
				} or return Dotiac::DTL::Value->safe(undef);
				if (defined $x) {
					$param=$x;
					next;
				}
			}
			return Dotiac::DTL::Value->safe(undef);
		}
		return Dotiac::DTL::Value->safe($n) if $n!~/[^\d\-\.\,\e]/;
		return Dotiac::DTL::Value->safe(undef);
	}
	return Dotiac::DTL::Value->new($param,!$escape);
}

sub devar_var_default {
	my $var = devar_var(@_);
	return $var->string();
}

1;
__END__

=head1 NAME

Dotiac::DTL::Core - Common functions for Dotiac::DTL and Dotiac::DTL::Reduced

=head1 SYNOPSIS

	require Dotiac::DTL;
	$t=Dotiac::DTL->new("file.html")
	$t->print();

	require Dotiac::DTL::Reduced;
	$t=Dotiac::DTL->new("compiled.html") #Works only with compiled templates
	$t->print();

=head1 DESCRIPTION

This module includes the detailed documentation on the functions used by L<Dotiac::DTL> and L<Dotiac::DTL::Reduced>.

=head2 Settings

These are Settings that modify the behavior of Dotiac::DTL and should be set to your preferences.

=head3 $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID

Defines what value should be inserted if a variable is not found. Defaults to "";

	require Dotiac::DTL;
	my $data="Hello, {{ asfdsfsdfasdf }}"; #asfdsfsdfasdf won't be defined.
	$t=Dotiac::DTL->new(\$data);
	$t->print(); #prints "Hello, "
	$Dotiac::DTL::TEMPLATE_STRING_IF_INVALID="[[Not found]]";
	$t->print(); #prints "Hello, [[Not found]]";

=head3 $Dotiac::DTL::ALLOW_METHOD_CALLS

Setting this to "1" allows methods of objects in the given variables to be called (without parameters).

Setting this to "0" makes the template slightly safer if you can't trust your variables.

Defaults to "1".

=head3 $Dotiac::DTL::ALLOWED_INCLUDE_ROOTS

Setting this to "1" allows the unsafe ssi-tag to open any file on your filesystem.

Setting this to "2" also allows the tags include and extend to open files anywhere.

Defaults to "0", which allows only files to be opened in the current or subdirectories.

=cut

=head3 $Dotiac::DTL::AUTOESCAPING

Enables or disables HTML-autoescaping for all templates. Defaults to enabled(1)

	require Dotiac::DTL;
	my $data="Hello, {{ "<World>" }}";
	$t=Dotiac::DTL->new(\$data);
	$t->print(); #prints "Hello, &lt;World&gt;"
	$Dotiac::DTL::AUTOESCAPING=0;
	$t->print(); #prints "Hello, <World>";

You could also use the safe filter for a single variable L<Dotiac::DTL::Filter>, or the autoescape tag L<Dotiac::DTL::Tag::autoescape> for an area.

=head3 @Dotiac::DTL::TEMPLATE_DIRS

Defines a list of directories in which the exported Template() should look for the template. Defaults to an empty list.

=head3 $Dotiac::DTL::DATETIME_FORMAT

Default format for the {% now %}-tag if there is no format given. (Defaults to 'N j, Y, P')

=head3 $Dotiac::DTL::DATE_FORMAT

Default format for the date-filter if there is no format given. (Defaults to 'N j, Y')

=head3 $Dotiac::DTL::TIME_FORMAT

Default format for the time-filter if there is no format given. (Defaults to 'P')

=head3 $Dotiac::DTL::PARSER

Which parser Dotiac::DTL should use. Defaults to "Dotiac::DTL::Parser", the normal Dotiac parser.

You only should change this, if you know what you are doing.

=head2 $Dotiac::DTL::CURRENTDIR

This is used when called from a scalarref to define in what directory the template is (for include)

=head2 Static internal variables

These are only important to people writing there own Tags. See L<Dotiac::DTL::Tag> for details on that.

These variables are cleared every time print() or string() or similar on the main template is called.

=head2 $Dotiac::DTL::currentdir

This is used to save in what directory the template is (for include).

=head3 $Dotiac::DTL::Max_Depth

This is used for output (during the string()-call of L<Dotiac::DTL::Value>). This defines how deep datastructures are printed if rendered as a Variable. Defaults to 3, set this to 0 to make Arrays and Hashes print as a space.

=head3 %Dotiac::DTLs::blocks

Contains the content of the named blocks, either as a Dotiac::DTL::Tag like object or a reference to a code reference.

	sub eval() {
		$Dotiac::DTLs::blocks{"main"}=Dotiac::DTL->new("file.html")->{first};
		my $sub={
			return "Hello World";
		};
		$Dotiac::DTLs::blocks{"compiled"}=\$sub;
	}

=head3 %Dotiac::DTLs::cycle 

Used by the cycle tag to store state information, there should be no need to play around with this

=head3 %Dotiac::DTLs::globals

Storage space for other tags. You should never store any information that changes in the objects themselves, you should store it in here. The objects won't be resetted on every run.

	#In your tag, lets call it counter
	sub print {
		$Dotiac::DTLs::globals{counter}->{$self}=1 unless $Dotiac::DTLs::globals{counter}->{$self}=1;
		print $Dotiac::DTLs::globals{counter}->{$self}++;
		#or named counters:
		$Dotiac::DTLs::globals{counter}->{$self->{name}}=1 unless $Dotiac::DTLs::globals{counter}->{$self->{name}}=1;
		print $Dotiac::DTLs::globals{counter}->{$self->{name}}++;
		$self->{n}->print(@_);

	}
	#And for the compiled output
	sub perlprint {
		my ($fh,$id,$level,$digest)=(shift(),shift(),shift(),shift()); 
		#...
		my $name='"'.$digest."-".$id.'"'; #$digest will be unique in combination with $id.
		print $fh "\t" x $level,"\$Dotiac::DTLs::globals{counter}->{$name}=1 unless \$Dotiac::DTLs::globals{counter}->{$name}=1;\n";
		print $fh "\t" x $level,"print \$Dotiac::DTLs::globals{counter}->{$name}++;\n";
		# or named templates, which are even easier here: Just define \$name$id in perl()
		my ($fh,$id,$level,$digest)=(shift(),shift(),shift(),shift());
		print $fh "\t" x $level,"\$Dotiac::DTLs::globals{counter}->{\$name$id}=1 unless \$Dotiac::DTLs::globals{counter}->{\$name$id}=1;\n";
		print $fh "\t" x $level,"print \$Dotiac::DTLs::globals{counter}->{\$name$id}++;\n";
		#...
	}
	#Same for string() of course.

See L<Dotiac::DTL::Tag> for details on what those methods should do.

=head3 %Dotiac::DTLs::included 

Stores information on which template is already included to detect cyclic includes. Used by the tags ssi and include. There shouldn't also be any need to change this.

=head2 Public methods

=head3 new(FILE) or new(FILE,COMPILE)

This one differs if you are using L<Dotiac::DTL> or L<Dotiac::DTL::Reduced>, since Dotiac::DTL::Reduced will die on uncompiled Templates and can't compile them. See their documentation for details.

=head3 safenew(FILE) or safenew(FILE,COMPILE)

Same as new(), mostly used internally by include and extends. Accepts only relative pathes without leading ".." .

By setting $ALLOWED_INCLUDE_ROOTS to "2" this will be disabeled.

Also depends on if you are using L<Dotiac::DTL> or L<Dotiac::DTL::Reduced>.

=head3 compiled(PACKAGENAME) 

Treats PACKAGENAME as a compiled template. See L<Dotiac::DTL::Compiled>.

This is useful to insert perl code into templates.

Returns a Dotiac::DTL object

	package MyTemplate;
	sub print {
		my ($vars,$escape)=(shift(),shift());
		print "There are ".keys(%$vars)." parameters registered and x is $vars->{x}\n";
	}
	sub string {
		my ($vars,$escape)=(shift(),shift());
		return "There are ".keys(%$vars)." parameters registered and x is $vars->{x}\n";
	}
	sub eval {
		#nothing for now.
	}
	package main;
	require Dotiac::DTL;
	my $mytemplate=Dotiac::DTL->compiled("MyTemplate");
	# now you can use $mytemplate as a normal template.
	$mytemplate->print({x=>123});
	# This doesn't seem quite useful you could easily just write the code here, until you do this:
	my $templatedata="{% for x in array %}{% include template %}{% endfor %}";
	my $t = Dotiac::DTL->new(\$templatedata); #File templates work just as well.
	$t->print({array=>[1..100],template=>$mytemplate);
	# This will now include and print the above package a hundert times and 
	# will be a lot faster, depending on the contents of that for loop.

=head3 param(NAME, VALUE)

Works like HTML::Templates param() method, will set a param that will be used for output generation.

	my $t=Dotiac::DTL->new("file.html");
	$t->param(FOO=>"bar");
	$t->print();
	#Its the same as:
	my $t=Dotiac::DTL->new("file.html");
	$t->print({FOO=>"bar"});

=over

=item NAME

Name of the parameter.

=item VALUE

Value to set the parameter to.

=back

Returns the value of the param NAME if VALUE is skipped.

=head3 string(HASHREF)

Returns the templates output.

=over

=item HASHREF

Parameters to give to the template. See Variables below.

=back

=head3 output(HASHREF) and render(HASHREF)

Same as string(HASHREF) just for HTML::Template and Django syntax.

=head3 print(HASHREF) 

You can think of these two being equal:

	print $t->string(HASHREF);
	$t->print(HASHREF);

But string() can cause a lot of memory to be used (on large templates), so print() will print to the default output handle as soon as it has some data, which uses a lot less memory.

=over

=item HASHREF

Parameters to give to the template. See Variables below.

=back

Returns nothing.

=head2 Internal static functions

=head3 escap(STRING)

Escapes a quoted string so it won't contain any special chars the parser might use.

The are for now: Space, tab, newline (Seperates variables in most tags), "," (spereates variables in for and filter arguments), "|" (seperates Filters), ":" (seperates filter and its arguments) and of course "+'.

This is used by Dotiac::DTL::get_variables() and doesn't need to be called anywhere else.

String literals in this implementation can contain:

	\n 		#newline
	\t 		#tab
	\b 		#backspace
	\f 		#formfeed
	\r 		#carriage return
	\" 		# "
	\' 		# '
	\{ 		# {
	\} 		# }
	\\ 		# \\
	\xXX		# character with hexadecimal charcode (00 - FF) \x20 = " "
	\uXXXX		# Unicode 16Bit \u0020 = " "
	\UXXXXXXXX	# Unicode 32Bit \u00000020 = " "

=head3 descap(STRING)

inversion of escap().

Called by devar() and apply_filters(), no need to call it anywhere else.

=head3 get_variables(STRING, [SPLIT,BY])

Spilts a string, as given by the parser, into variables, which can be given to devar.

If there are additional arguments, it returns a two dimensional list splitted at the arguments.

Also escapes the quoted strings for safe processing using escap().

This is best explained with some examples:

	my @list = Dotiac::DTL::get_varibales("var1 var2|Filter \"String with spaces\""); #@list is then ("var1", "var2|Filter", "`String%20with%spaces`")
	my %list = Dotiac::DTL::get_varibales("foo|dictsort:'sort by some option' othervar by bar as newvar","as","by"); #%list is then (""=>["foo|dictsort:`sort%20by%20some%20option`","othervar"],as=>["bar"],by=>["newvar"])
	#note the first " by " in the string is not being used for splitting
	#And now without the arguments:
	my @list = Dotiac::DTL::get_varibales("foo|dictsort:'sort by some option' othervar by bar as newvar"); #@list is then ("foo|dictsort:`sort%20by%20some%20option`","othervar","by","bar","as","newvar")

=head3 Escape(STRING,ESCAPE)

This is used in compiled templates for variables, it just escapes if ESCAPE is set or doesn't otherwise.

There should be no need to use this anywhere else.

=head3 urlencode(STRING)

About the same as CGI::escape(STRING), but doesn't work on EBCDIC

=head3 Conditional(OBJECT)

Returns a true Value if the object is true or filled (for array and hashrefs) and "" otherwise.

This is needed for the if, ifequal and ifnot equal tags to skip on a lot defineds().

It will return the size of the referenced container of an arrayref or hashref.

It will also try to run the count() method of an object, if available.

=head3 apply_filters(VALUE,VARS,ESCAPE,[FILTER1,FILTER2....])

Applies FILTER1, FILTER2, ... on a variable and escapes it if needed.

VARS is a hashref which holds all the parameters (The same you would give to string() or print())

ESACPE gives the starting point for the auto-escape (1: autoescape, 0:no autoescape)

	print Dotiac::DTL::apply_filters("foo",{},0,"cut:`o`");# prints "f";
	print Dotiac::DTL::apply_filters("foo",{var=>"0"},0,"cut:var"); #Also prints "f";
	print Dotiac::DTL::apply_filters("<foo>",{},1); #Prints "&lt;foo&gt;";
	print Dotiac::DTL::apply_filters("<foo>",{},0); #Prints "<foo>";
	print Dotiac::DTL::apply_filters("<foo>",{},1,"safe"); #Prints "<foo>" even when autoescape is on
	print Dotiac::DTL::apply_filters("<foo>",{},0,"escape"); #Prints "&lt;foo&gt;" # even when autoescape is off

=head3 devar(VARIABLE,VARS,ESCAPE, ...)

Replaces a string (as returned from get_variables()) with the variable from the part of VARS it references.

VARS is a hashref as given to string() or print()

ESCAPE controls the autoescape behavior (0: off, 1:on)

Returns the resolved variable with all the filters applied, or $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID if the produkt is undef.

	print Dotiac::DTL::devar("FOO|lower",{FOO=>"Hello"},1); #prints "hello"
	print Dotiac::DTL::devar("FOO",{FOO=>"Hello"},1); #prints "Hello"
	print Dotiac::DTL::devar("`FOO`",{FOO=>"Hello"},1); #prints "FOO"
	print Dotiac::DTL::devar("`<FOO>`",{},1); #prints "&lt;FOO&gt;"
	print Dotiac::DTL::devar("`<FOO>`",{},0); #prints "<FOO>"
	print Dotiac::DTL::devar("`<FOO>|safe`",{},1); #prints "<FOO>"
	print Dotiac::DTL::devar("`<FOO>|escape`",{},0); #prints "&lt;FOO&gt;"

=head3 devar_nodefault(VARIABLE,VARS,ESCAPE, ...)

Same as devar() (see above), but returns undef if the generated variable is undef.

=head3 devar_raw(VARIABLE,VARS,ESCAPE, ...)

Same as devar() but returns the Dotiac::DTL::Value object.

=head3 devar_content(VARIABLE,VARS,ESCAPE, ...)

Same as devar() but returns always the content and does no escaping or pretty printing.

=head3 devar_repr(VARIABLE,VARS,ESCAPE, ...)

Same as devar() but returns always the a representation without escaping. Use this, if the variable is not for printing or is going to be escaped some other way

=head3 devar_var(RAWVARIABLE,VARS,ESCAPE, ...)

Similar as devar(). But it won't accept filters in RAWVARIABLE and returns a Dotiac::DTL::Value object.

This is used by the Variable construct to cache filters and calling devar_var() and apply_filters() for increased performance.

This will return undef similar to devar_nodefault().

=head3 devar_var_default(RAWVARIABLE,VARS,ESCAPE, ...)

Similar as devar_var().

This will return $Dotiac::DTL::TEMPLATE_STRING_IF_INVALID if the result of devar_var() is undef.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 BUGS

If you find a bug, please report it.

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

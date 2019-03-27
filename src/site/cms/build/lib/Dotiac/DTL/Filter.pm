###############################################################################
#Filter.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Filter.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Filter;
use strict;
use warnings;
require Scalar::Util;
our $VERSION = 0.8;

sub add {
	my $value=shift;
	my $add=shift;
	$value->set($value->repr+$add->repr) if $value->number and $add->number;
	$value->set($value->repr.$add->repr) unless $value->number and $add->number;
	return $value;

}

sub addslashes {
	my $value =shift;
	my $val=$value->repr();
	$val=~s/([\\'"])/\\$1/g;
	$value->set($val);
	return $value;
}
	
sub capfirst {
	my $value=shift;
	return $value->set(ucfirst $value->repr);
}

sub center {
	my $value=shift;
	my $length=shift;
	return $value unless $length->number;
	my $padding = shift;
	my $pad=" ";
	$pad=substr($padding->repr,0,1) if $padding;
	my $val=$value->repr;
	my $len=$length->repr;
	$len-=CORE::length $val;
	$val=($pad x int($len/2)).$val.($pad x int($len/2)).($len%2?$pad:"");
	$value->set($val);
	return $value;
}

sub cut {
	my $value=shift;
	my $val=$value->repr();
	my $t=shift;
	$t=$t->repr();
	$val=~s/\Q$t//g;
	$value->set($val);
	return $value;
}

#locale stuff
our @datemonths=qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );
our @datemonthl=qw( January February March April May Juni Juli August September October November December );
our @datemontha=qw( Jan. Feb. March April May Juni Juli Aug. Sep. Oct. Nov. Dec. );
our @weekdays=qw/Sun Mon Tue Wed Thu Fri Sat/;
our @weekdayl=qw/Sunday Monday Tuesday Wednesday Thursday Friday Saturday/;
our @timeampm=qw/a.m. p.m. AM PM/;
our @timespotnames=qw/midnight noon/;
our @datesuffixes=qw/th st nd rd/; #qw/Default day1 day2 day3 day4 day5...

sub date {
	my $value=shift;
	return $value unless $value->number() or $value->array();
	my $time=$value->repr();
	my $safe=0;
	my $string=shift;
	if (not defined $string or not $string->scalar()) {
		$string=$Dotiac::DTL::DATE_FORMAT;
		$safe=1;
	}
	else {
		$safe=$string->safe();
		$string=$string->repr;
	}
	my @t;
	if ($value->number()) {
		@t=localtime($time);
	}
	else {
		@t=@{$value->content};
	}
	my @s=split //,$string;
	my $res;
	while (my $s=shift(@s)) {
		if ($s eq '\\') {
			$res.=shift(@s);
		}
		elsif ($s eq "a") {
			if ($t[2] > 12 or ($t[2] == 12 and $t[1] > 0)) {
				$res.=$timeampm[0];
			}
			else {
				$res.=$timeampm[1];
			}
		}
		elsif ($s eq "A") {
			if ($t[2] > 12 or ($t[2] == 12 and $t[1] > 0)) {
				$res.=$timeampm[2];
			}
			else {
				$res.=$timeampm[3];
			}
		}
		elsif ($s eq "b") {
			$res.=lc($datemonths[$t[4]]);
		}
		elsif ($s eq "d") {
			$res.=sprintf("%02d",$t[3]);
		}
		elsif ($s eq "D") {
			$res.=$weekdays[$t[6]];
		}
		elsif ($s eq "f") {
			my $h=$t[2];
			$h=$h%12;
			$h=12 unless $h;
			$res.=$h;
			$res.=sprintf(":%02d",$t[1]) if ($t[1]);
		}
		elsif ($s eq "F") {
			$res.=$datemonthl[$t[4]];
		}
		elsif ($s eq "g") {
			my $h=$t[2];
			$h=$h%12;
			$h=12 unless $h;
			$res.=$h;
		}
		elsif ($s eq "G") {
			$res.=$t[2];
		}
		elsif ($s eq "h") {
			my $h=$t[2];
			$h=$h%12;
			$h=12 unless $h;
			$res.=sprintf("%02d",$h);
		}
		elsif ($s eq "H") {
			$res.=sprintf("%02d",$t[2]);
		}
		elsif ($s eq "i") {
			$res.=sprintf("%02d",$t[1]);
		}
		elsif ($s eq "j") {
			$res.=$t[3];
		}
		elsif ($s eq "l") {
			$res.=$weekdayl[$t[6]];
		}
		elsif ($s eq "L") {
			my $d=$t[5]+1900;
			$res.=(((not $d%4 and $d%100) or not $d%400)?"1":"0");
		}
		elsif ($s eq "m") {
			$res.=sprintf("%02d",$t[4]+1);
		}
		elsif ($s eq "M") {
			$res.=$datemonths[$t[4]];
		}
		elsif ($s eq "n") {
			$res.=$t[4]+1;
		}
		elsif ($s eq "N") {
			$res.=$datemontha[$t[4]];
		}
		elsif ($s eq "O") {
			my @tt=localtime(0);
			$tt[2]+=1 if $t[8];
			$res.=sprintf("%+05d",$tt[2]*100+$tt[1]);
		}
		elsif ($s eq "P") {
			if ($t[2] == 12 and $t[1] == 0) {
				$res.=$timespotnames[1];
			}
			elsif ($t[2] == 0 and $t[1] == 0) {
				$res.=$timespotnames[0];
			}
			else {
				my $h=$t[2];
				$h=$h%12;
				$h=12 unless $h;
				$res.=$h;
				$res.=sprintf(":%02d",$t[1]) if ($t[1]);
				if ($t[2] > 12 or ($t[2] == 12 and $t[1] > 0)) {
					$res.=" ".$timeampm[0];
				}
				else {
					$res.=" ".$timeampm[1];
				}
			}
			
		}
		elsif ($s eq "r") {
			$res.=$weekdays[$t[6]];
			$res.=", ";
			$res.=$t[4]+1;
			$res.=" ".$datemonths[$t[4]]." ".($t[5]+1900);
			$res.=sprintf(" %02d:%02d:%02d",$t[2],$t[1],$t[0]);
			my @tt=localtime(0);
			$tt[2]+=1 if $t[8];
			$res.=sprintf(" %+05d",$tt[2]*100+$tt[1]);
		}
		elsif ($s eq "s") {
			$res.=sprintf("%02d",$t[0]);
		}
		elsif ($s eq "S") {
			if ($datesuffixes[$t[3]]) {
				$res.=$datesuffixes[$t[3]];
			}
			else {
				$res.=$datesuffixes[0]
			}
		}
		elsif ($s eq "t") {
			if ($t[4] == 1 or $t[4]==3 or $t[4] == 5 or $t[4] == 7 or $t[4] == 8 or $t[4] == 10 or $t[4] == 12) {
				$res.="31";
			}
			elsif ($t[4] == 2) {
				my $d=$t[5]+1900;
				if ((not $d%4 and $d%100) or not $d%400) {
					$res.="29";
				}
				else {
					$res.="28";
				}
			}
			else {
				$res.="30";
			}
		}
		elsif ($s eq "T") {
			require POSIX;
			$res.=POSIX::strftime("%Z", @t);
		}
		elsif ($s eq "t") {
			$res.=$t[6];
		}
		elsif ($s eq "W") {
			require POSIX;
			$res.=POSIX::strftime("%W", @t);
		}
		elsif ($s eq "y") {
			$res.=sprintf("%02d",($t[5]%100));
		}
		elsif ($s eq "Y") {
			$res.=sprintf("%04d",$t[5]+1900);
		}
		elsif ($s eq "z") {
			$res.=$t[7];
		}
		elsif ($s eq "Z") {
			my @tt=localtime(0);
			$tt[2]+=1 if $t[8];
			$res.=$tt[2]*3600+$t[1]*60+$t[0];
		}
		elsif ($s eq "\n") {
			$res.="n";
		}
		elsif ($s eq "\t") {
			$res.="t";
		}
		elsif ($s eq "\f") {
			$res.="f";
		}
		elsif ($s eq "\b") {
			$res.="b";
		}
		elsif ($s eq "\r") {
			$res.="r";
		}
		else {
			$res.=$s;
		}
	}
	return Dotiac::DTL::Value->new($res,$safe);
}

sub default {
	my $val=shift;
	my $def=shift;
	return $def unless $val->true;
	return $val;
}

sub default_if_none {
	my $val=shift;
	my $def=shift;
	return $def unless $val->defined;
	return $val;
}

sub dictsort {
	my $value=shift;
	return $value unless $value->array();
	my $by=shift;
	unless ($by) {
		$value->set([sort { if (Scalar::Util::looks_like_number($a) and Scalar::Util::looks_like_number($b)) {
				$a <=> $b
			}
			else {
				$a cmp $b
			}
			} @{$value->content}]);
		return $value;
	}
	$by=$by->repr();
	$value->set([sort {
		my $aa = $a;
		if (ref $a) {
			$aa = $a->{$by} if Scalar::Util::reftype($a) eq "HASH" and exists $a->{$by};
			$aa = $a->[$by] if Scalar::Util::reftype($a) eq "ARRAY" and Scalar::Util::looks_like_number($by) and exists $a->[$by];
			$aa = $a->$by() if Scalar::Util::blessed($a) and $a->can($by);
		}
		my $bb = $b;
		if (ref $b) {
			$bb = $b->{$by} if Scalar::Util::reftype($b) eq "HASH" and $b->{$by};
			$bb = $b->[$by] if Scalar::Util::reftype($a) eq "ARRAY" and Scalar::Util::looks_like_number($by) and exists $b->[$by];
			$bb = $b->$by() if Scalar::Util::blessed($b) and $b->can($by);
		}
		if (Scalar::Util::looks_like_number($aa) and Scalar::Util::looks_like_number($bb)) {
			$aa <=> $bb
		}
		else {
			$aa cmp $bb
		}
	} @{$value->content}]);
	return $value;

}

sub dictsortreversed {
	my $value=shift;
	return $value unless $value->array();
	my $by=shift;
	unless ($by) {
		$value->set([reverse sort { if (Scalar::Util::looks_like_number($a) and Scalar::Util::looks_like_number($b)) {
				$a <=> $b
			}
			else {
				$a cmp $b
			}
			} @{$value->content}]);
		return $value;
	}
	$by=$by->repr();
	$value->set([reverse sort {
		my $aa = $a;
		if (ref $a) {
			$aa = $a->{$by} if Scalar::Util::reftype($a) eq "HASH" and exists $a->{$by};
			$aa = $a->[$by] if Scalar::Util::reftype($a) eq "ARRAY" and Scalar::Util::looks_like_number($by) and exists $a->[$by];
			$aa = $a->$by() if Scalar::Util::blessed($a) and $a->can($by);
		}
		my $bb = $b;
		if (ref $b) {
			$bb = $b->{$by} if Scalar::Util::reftype($b) eq "HASH" and $b->{$by};
			$bb = $b->[$by] if Scalar::Util::reftype($a) eq "ARRAY" and Scalar::Util::looks_like_number($by) and exists $b->[$by];
			$bb = $b->$by() if Scalar::Util::blessed($b) and $b->can($by);
		}
		if (Scalar::Util::looks_like_number($aa) and Scalar::Util::looks_like_number($bb)) {
			$aa <=> $bb
		}
		else {
			$aa cmp $bb
		}
	} @{$value->content}]);
	return $value;

}

sub divisibleby {
	my $value=shift;
	return Dotiac::DTL::Value->safe(0) unless $value->number;
	my $by=shift;
	return Dotiac::DTL::Value->safe(0) unless $by;
	return Dotiac::DTL::Value->safe(0) unless $by->number;
	my $res=!($value->content % $by->content);
	return Dotiac::DTL::Value->safe($res);
}

sub escape {
	my $value=shift;
	$value->escape(1);
	return $value;
}

#Not for JSON output of objects, I need to write an JSON-Addon for that.

my %jsescape = (
	"\n" => "\\n",
	"\r" => "\\r",
	"\t" => "\\t",
	"\f" => "\\f",
	"\b" => "\\b",
	'"' => "\\\"",
	"\\" => "\\\\",
	"'" => "\\'",
);

sub escapejs {
	my $value=shift;
	my $val=$value->repr();
	$val =~ s/([\n\r\t\f\b"'\\])/$jsescape{$1}/eg;
	#$val =~ s/([\x00-\x08\x0b\x0e-\x1f\x7f-\x{FFFF}])/'\\u' .sprintf("%04x",ord($1))/eg; #Won't work in Perl 5.6.0
	$val =~ s/([^\x09\x0a\x0c\x0d\x20-\x7e])/'\\u' .sprintf("%04x",ord($1))/eg;
	$value->set($val);
	return $value;
}

#Locale crap
our @filesizeformat=qw/bytes Kb Mb Gb Tb Eb Pb manybytes manybytes manybytes manybytes/;

our $floatformatlocale="";
#sub {
#	my $v=shift;
#	$v=s/\./,/g;
#	return $v;
#}

sub filesizeformat {
	my $val=shift;
	return $val unless $val->number();
	my $value=$val->content();
	my $i=0;
	while ($value >= 1024.0) {
		$value=$value/1024.0;
		$i++;
	}
	if ($value < 10) {
		$value=sprintf("%1.2f",$value);
	}
	else {
		$value=sprintf("%4.1f",$value);
	}
	$value=~s/0+$//g;
	$value=~s/\.$//g;
	$value=$floatformatlocale->($value) if $floatformatlocale;
	$val->set($value." ".$filesizeformat[$i]);
	return $val;
}

sub first {
	my $value=shift;
	if ($value->object) {
		if ($value->content->can("__getitem__")) {
			my $x = $value->content->__getitem__(0);
			if (defined $x) {
				$value->set($x);
				return $value;
			}
		}
	}
	if ($value->array) {
		$value->set($value->content->[0]);
	}
	elsif ($value->hash) {
		my @a=sort keys %{$value->content};
		$value->set($value->content->{$a[0]});
	}
	return $value;
}

sub fix_ampersands {
	my $value=shift;
	my $val=$value->repr();
	$val=~s/&/&amp;/g;
	$value->set($val);
	return $value;
}

sub floatformat {
	my $val=shift;
	return $val if not $val->number;
	my $value=$val->content;
	my $arg=shift;
	if ($arg and not $arg->number) {
		$val->set(int($value+0.5));
		return $val
	}
	if ($arg) {
		$arg=$arg->content;
	}
	else {
		$arg=-1;
	}
	my $skip=$arg=~s/^-//;
	$value=sprintf("%.".$arg."f",$value);
	unless ($skip) {
		$value=$floatformatlocale->($value) if $floatformatlocale;
		$val->set($value);
		return $val;
	}
	$value=~s/0+$//g;
	$value=~s/\.$//g;
	$value=$floatformatlocale->($value) if $floatformatlocale;
	$val->set($value);
	return $val;
}

my $escape=sub {
	my $val=shift;
	$val=~s/&/&amp;/g;
	$val=~s/</&lt;/g;
	$val=~s/>/&gt;/g;
	$val=~s/\"/&quot;/g;
	$val=~s/\'/&#39;/g;
	return $val;
};

sub force_escape {
	my $value=shift;
	$value->escape(1);
	return Dotiac::DTL::Value->safe($value->string());
}

sub get_digit {
	my $value=shift;
	return $value unless $value->number;
	my $val=$value->content;;
	my $pos = shift;
	return $val unless defined $pos and $pos->number;
	$pos=int $pos->content;
	return $value if $pos < 1;
	return Dotiac::DTL::Value->safe(0) if $pos > CORE::length($val);
	$value->set(substr $val,-$pos,1);
	return $value;
}

#Should only be used together with urlencode
sub iriencode {
	my $val=shift;
	my $value=$val->repr;
	#require Encode;
	#$value=Encode::encode_utf8($value) if Encode::is_utf8($value);
	$value = eval { pack("C*", unpack("U0C*", $value))} || pack("C*", unpack("C*", $value));
	$value=~s/([^a-zA-Z0-9\[\]\(\)\$\%\&\/:;#=,!\?\*_.~-])/uc sprintf("%%%02x",ord($1))/eg;	
	$val->set($value);
	return $val;
}

sub join {
	my $value=shift;
	my $j=shift;
	if ($j) {
		$j=$j->repr;
	}
	else {
		$j="";
	}
	if ($value->object) {
		if ($value->content->can("__len__") and $value->content->can("__getitem__")) { #No support for __iter__ right now.
			my @a;
			foreach my $i (0 .. $value->content->__len__()-1) {
				push @a,$value->content->__getitem__($i);
			}
			$value->set(CORE::join($j,@a));
			return $value
		}
		if ($value->content->can("count") and $value->content->can("__getitem__")) { #No support for __iter__ right now.
			my @a;
			foreach my $i (0 .. $value->content->count()-1) {
				push @a,$value->content->__getitem__($i);
			}
			$value->set(CORE::join($j,@a));
			return $value;
		}
	}
	$value->set(CORE::join($j,@{$value->content})) if $value->array;
	$value->set(CORE::join($j,values %{$value->content})) if $value->hash;
	return $value;
}

sub last {
	my $value=shift;
	if ($value->object) {
		if ($value->content->can("__len__") and $value->content->can("__getitem__")) {
			my $x = $value->content->__getitem__($value->content->__len__()-1);
			if (defined $x) {
				$value->set($x);
				return $value;
			}
		}
		if ($value->content->can("count") and $value->content->can("__getitem__")) {
			my $x = $value->content->__getitem__($value->content->count()-1);
			if (defined $x) {
				$value->set($x);
				return $value;
			}
		}
	}
	if ($value->array) {
		if (@{$value->content}) {
			$value->set($value->content->[-1]);
		}
		else {
			$value->set(undef);
		}
	}
	elsif ($value->hash) {
		my @a=sort keys %{$value->content};
		if (@a) {
			$value->set($value->content->{$a[-1]});
		}
		else {
			$value->set(undef);
		}
	}
	return $value;
}

sub length {
	my $value=shift;
	return Dotiac::DTL::Value->safe(0) if $value->undef;
	return Dotiac::DTL::Value->safe(CORE::length($value->content)) if $value->scalar;
	return Dotiac::DTL::Value->safe($value->content->count()) if $value->object and $value->content->can("count");
	return Dotiac::DTL::Value->safe($value->content->__len__()) if $value->object and $value->content->can("__len__");
	return Dotiac::DTL::Value->safe(scalar @{$value->content}) if $value->array;
	return Dotiac::DTL::Value->safe(scalar keys %{$value->content}) if $value->hash;
	return Dotiac::DTL::Value->safe(0);
}

#output will be 1 or 0, not True or False
sub length_is {
	my $value=shift;
	my $is=shift;
	if ($is->number) {
		$is=int($is->content());
	}
	else {
		$is=0;
	}
	$is = 0 unless defined $is and Scalar::Util::looks_like_number($is);
	return Dotiac::DTL::Value->safe(!$is) if $value->undef;
	return Dotiac::DTL::Value->safe(CORE::length($value->content) == $is) if $value->scalar();
	return Dotiac::DTL::Value->safe($value->content->count() == $is) if $value->object and $value->content->can("count");
	return Dotiac::DTL::Value->safe($value->content->__len__() == $is) if $value->object and $value->content->can("__len__");
	return Dotiac::DTL::Value->safe(@{$value->content} == $is) if $value->array;
	return Dotiac::DTL::Value->safe(keys %{$value->content} == $is) if $value->hash;
	return Dotiac::DTL::Value->safe(0)
}

sub linebreaks {
	my $value=shift;
	$value=$value->string();
	$value=~s/\n\s*\n/<\/p><p>/g;
	$value=~s/\n/<br \/>/g;
	return Dotiac::DTL::Value->safe("<p>".$value."</p>");
}

sub linebreaksbr {
	my $value=shift;
	$value=$value->string();
	$value=~s/\n/<br \/>/g;
	return Dotiac::DTL::Value->safe($value);
}

sub linenumbers {
	my $val=shift;
	my $value=$val->repr();
	return $val->set("1: $value") unless $value;
	my $count = ($value =~ tr/\n/\n/);
	$count=CORE::length $count;
	my $i=1;
	$value=~s/\n/sprintf("\n%0$count"."d: ",++$i)/eg;
	return $val->set(sprintf("%0$count"."d: ",1).$value);
}

sub ljust {
	my $value=shift;
	my $length=shift;
	return $value unless $length->number;
	my $padding = shift;
	my $pad=" ";
	$pad=substr($padding->repr,0,1) if $padding;
	my $val=$value->repr;
	my $len=$length->repr;
	$len-=CORE::length $val;
	$val=$val.($pad x int($len));
	$value->set($val);
	return $value;
}

sub lower {
	my $value=shift;
	return $value->set(lc $value->repr);
}

sub make_list {
	my $value=shift;
	my $val=$value->repr;
	my $by=shift;
	if ($by) {
		$by=quotemeta $by->repr;
		$value->set([split /$by/,$val]);
	}
	return $value->set([split //,$val]);
}

#No locale for now

sub phone2numeric {
	my $val=shift;
	my $value=$val->repr;
	$value=~y/AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpRrSsTtUuVvWwXxYy/222222333333444444555555666666777777888888999999/;
	return $val->set($value);
}

our $pluralizedefault = "s";

sub pluralize {
	my $value=shift;
	my $val=0;
	$val=CORE::length $value->content if $value->scalar;
	$val=$value->content if $value->number;
	$val=scalar keys %{$value->content} if $value->hash;
	$val=scalar @{$value->content} if $value->array;
	my $s = $pluralizedefault;
	if (@_) {
		$s=shift() if @_;
		$s=$s->repr();
	}
	my $p;
	my $o;
	if (@_) {
		$o=$s;
		$p=shift;
		$p=$p->repr();
	}
	else {
		($o,$p) = split /,/,$s,2;
	}
	unless ($p) {
		$p=$o;
		$o="";
	}
	return $value->set($val==1?$o:$p);
}


sub pprint {
	require Data::Dumper;
	return Dotiac::DTL::Value->new(Data::Dumper->Dump([@_]));
}

sub random {
	my $value=shift;
	if ($value->object) {
		if ($value->content->can("__len__") and $value->content->can("__getitem__")) {
			my $x = $value->content->__getitem__(int(rand($value->content->__len__())));
			if (defined $x) {
				return $value->set($x);
			}
		}
		if ($value->content->can("count") and $value->content->can("__getitem__")) {
			my $x = $value->content->__getitem__(int(rand($value->content->count())));
			if (defined $x) {
				return $value->set($x);
			}
		}
	}
	if ($value->array) {
		if (@{$value->content}) {
			return $value->set($value->content->[int(rand(scalar @{$value->content}))]);
		}
		else {
			return $value->set(undef);
		}
	}
	elsif ($value->hash) {
		my @a=sort keys %{$value->content};
		if (@a) {
			return $value->set($value->content->{$a[int(rand(scalar @a))]});
		}
		else {
			return $value->set(undef);
		}
	}
	return $value;
}

sub removetags {
	my $val=shift;
	my $value=$val->repr();
	my $tags=shift;
	$tags=$tags->repr;
	if ($tags) {
		my @t=split /\s+/,$tags;
		my $t=CORE::join("|",map {quotemeta $_} @t);
		$value=~s/<\/?(?:$t)(?:\/?>|\s[^>]+>)//g;
	}
	return $val->set($value);
}

sub rjust {
	my $value=shift;
	my $length=shift;
	return $value unless $length->number;
	my $padding = shift;
	my $pad=" ";
	$pad=substr($padding->repr,0,1) if $padding;
	my $val=$value->repr;
	my $len=$length->repr;
	$len-=CORE::length $val;
	$val=($pad x int($len)).$val;
	$value->set($val);
	return $value;
}

sub safe {
	my $value=shift;
	$value->safe(1);
	return $value;
}

sub slice {
	my $value=shift;
	return $value unless $value->hash or $value->array;
	my $slice=shift;
	return $value unless $slice;
	$slice=$slice->repr;
	my @slice=split /:/,$slice,2;
	
	my @value;
	@value=@{$value->content} if $value->array;
	@value=sort keys %{$value->content} if $value->hash;

	$slice[0] = int($slice[0] || 0) || 0;
	unless ($#slice) {
		return $value unless Scalar::Util::looks_like_number($slice[0]);
		return $value->set($value[int($slice[0])]) if $value->array;
		return $value->set($value->content->{$value[int($slice[0])]}) if $value->hash;
	}
	
	$slice[1] = int($slice[1] || 0) || 0;
	$slice[1]-=$slice[0] if ($slice[1] > 0);
	$slice[1]=scalar(@value)-$slice[0] unless $slice[1];
	return $value->set([splice(@value,$slice[0],$slice[1])]) if $value->array;
	return $value->set([map {$value->content->{$_}} splice(@value,$slice[0],$slice[1])]) if $value->hash;
}

sub slugify {
	my $value=shift;
	my $val=$value->repr();
	$val=lc($val);
	$val=~s/[^\w\s]//g;
	$val=~s/^\s+//g;
	$val=~s/\s+$//g;
	$val=~s/\s/-/g;
	$value->safe(1);
	return $value->set($val);
}



#This follows perls sprintf rules for now, which are about the same, but there is no "r"

sub stringformat {
	my $value=shift;
	my $format=shift;
	return $value unless $format;
	$format=$format->repr;
	my $val="";
	if ($format=~tr/r/s/) {
		$val=$value->pyrepr;
	}
	else {
		$val=$value->repr;
	}
	my $v;
	eval {
		local $SIG{__WARN__} = sub {};
		$v=sprintf("%$format",$val);
	};
	return $value->set($v) unless $@;
	undef $@;
	return $value;
}

sub striptags {
	my $value=shift;
	my $val=$value->repr;
	my $tags=shift;
	$val=~s/<[^>]+>//g;
	return $value->set($val);
}

sub time {
	my $value=shift;
	return $value unless $value->number() or $value->array();
	my $time=$value->repr();
	my $safe=0;
	my $string=shift;
	if (not defined $string or not $string->scalar()) {
		$string=$Dotiac::DTL::DATE_FORMAT;
		$safe=1;
	}
	else {
		$safe=$string->safe();
		$string=$string->repr;
	}
	my @t;
	if ($value->number()) {
		@t=localtime($time);
	}
	else {
		@t=@{$value->content};
	}
	my @s=split //,$string;
	my $res;
	while (my $s=shift(@s)) {
		if ($s eq '\\') {
			$res.=shift(@s);
		}
		elsif ($s eq "a") {
			if ($t[2] > 12 or ($t[2] == 12 and $t[1] > 0)) {
				$res.=$timeampm[0];
			}
			else {
				$res.=$timeampm[1];
			}
		}
		elsif ($s eq "A") {
			if ($t[2] > 12 or ($t[2] == 12 and $t[1] > 0)) {
				$res.=$timeampm[2];
			}
			else {
				$res.=$timeampm[3];
			}
		}
		elsif ($s eq "f") {
			my $h=$t[2];
			$h=$h%12;
			$h=12 unless $h;
			$res.=$h;
			$res.=sprintf(":%02d",$t[1]) if ($t[1]);
		}
		elsif ($s eq "g") {
			my $h=$t[2];
			$h=$h%12;
			$h=12 unless $h;
			$res.=$h;
		}
		elsif ($s eq "G") {
			$res.=$t[2];
		}
		elsif ($s eq "h") {
			my $h=$t[2];
			$h=$h%12;
			$h=12 unless $h;
			$res.=sprintf("%02d",$h);
		}
		elsif ($s eq "H") {
			$res.=sprintf("%02d",$t[2]);
		}
		elsif ($s eq "i") {
			$res.=sprintf("%02d",$t[1]);
		}
		elsif ($s eq "O") {
			my @tt=localtime(0);
			$tt[2]+=1 if $t[8];
			$res.=sprintf("%+05d",$tt[2]*100+$tt[1]);
		}
		elsif ($s eq "P") {
			if ($t[2] == 12 and $t[1] == 0) {
				$res.=$timespotnames[1];
			}
			elsif ($t[2] == 0 and $t[1] == 0) {
				$res.=$timespotnames[0];
			}
			else {
				my $h=$t[2];
				$h=$h%12;
				$h=12 unless $h;
				$res.=$h;
				$res.=sprintf(":%02d",$t[1]) if ($t[1]);
				if ($t[2] > 12 or ($t[2] == 12 and $t[1] > 0)) {
					$res.=" ".$timeampm[0];
				}
				else {
					$res.=" ".$timeampm[1];
				}
			}
			
		}
		elsif ($s eq "s") {
			$res.=sprintf("%02d",$t[0]);
		}
		elsif ($s eq "Z") {
			my @tt=localtime(0);
			$tt[2]+=1 if $t[8];
			$res.=$tt[2]*3600+$t[1]*60+$t[0];
		}
		elsif ($s eq "\n") {
			$res.="n";
		}
		elsif ($s eq "\t") {
			$res.="t";
		}
		elsif ($s eq "\f") {
			$res.="f";
		}
		elsif ($s eq "\b") {
			$res.="b";
		}
		elsif ($s eq "\r") {
			$res.="r";
		}
		else {
			$res.=$s;
		}
	}
	return Dotiac::DTL::Value->new($res,$safe);
}

our @timenames=qw/year years month month week weeks day days hour hours minute minutes/;

sub timesince {
	my $val=shift;
	return $val unless $val->number;
	$val=$val->content;
	my $comp=shift;
	if ($comp and $comp->number) {
		$comp=$comp->content;
	}
	else {
		$comp=CORE::time();
	}
	my $dist=$comp-$val;
	return Dotiac::DTL::Value->safe("0 $timenames[11]") if $dist < 60;
	my $mi=int($dist/60);
	my $h=int($mi/60);
	$mi=$mi%60;
	my $d=int($h/24);
	$h=$h%24;
	my $w=int($d/7);
	my $m=int($d/30);
	if ($m) {
		$d=$d%30;
	}
	else {
		$d=$d%7;
	}
	my $y=int($m/12);
	$m=$m%12;
	if (@_) { 
		my $r=($y?"$y ".($y==1?"$timenames[0] ":"$timenames[1] "):"").($m?"$m ".($m==1?"$timenames[2] ":"$timenames[3] "):($w?"$w ".($w==1?"$timenames[4] ":"$timenames[5] "):"")).($d?"$d ".($d==1?"$timenames[6] ":"$timenames[7] "):"").($h?"$h ".($h==1?"$timenames[8] ":"$timenames[9] "):"").($mi?"$mi ".($mi==1?"$timenames[10] ":"$timenames[11] "):"");
		$r=~s/\s$//;
		return Dotiac::DTL::Value->safe($r);
	}
	return Dotiac::DTL::Value->safe("$y ".($y==1?$timenames[0]:$timenames[1])) if ($y);
	return Dotiac::DTL::Value->safe("$m ".($m==1?$timenames[2]:$timenames[3])) if ($m);
	return Dotiac::DTL::Value->safe("$w ".($w==1?$timenames[4]:$timenames[5])) if ($w);
	return Dotiac::DTL::Value->safe("$d ".($d==1?$timenames[6]:$timenames[7])) if ($d);
	return Dotiac::DTL::Value->safe("$h ".($h==1?$timenames[8]:$timenames[9]).($mi?" $mi ".($mi==1?$timenames[10]:$timenames[11]):"")) if $h;
	return Dotiac::DTL::Value->safe("$mi ".($mi==1?$timenames[10]:$timenames[11])) if ($mi);
	
}

sub timeuntil {
	my $val=shift;
	return $val unless $val->number;
	$val=$val->content;
	my $comp=shift;
	if ($comp and $comp->number) {
		$comp=$comp->content;
	}
	else {
		$comp=CORE::time();
	}
	my $dist=$val-$comp;
	return Dotiac::DTL::Value->safe("0 $timenames[11]") if $dist < 60;
	my $mi=int($dist/60);
	my $h=int($mi/60);
	$mi=$mi%60;
	my $d=int($h/24);
	$h=$h%24;
	my $w=int($d/7);
	my $m=int($d/30);
	if ($m) {
		$d=$d%30;
	}
	else {
		$d=$d%7;
	}
	my $y=int($m/12);
	$m=$m%12;
	if (@_) {
		my $r=($y?"$y ".($y==1?"$timenames[0] ":"$timenames[1] "):"").($m?"$m ".($m==1?"$timenames[2] ":"$timenames[3] "):($w?"$w ".($w==1?"$timenames[4] ":"$timenames[5] "):"")).($d?"$d ".($d==1?"$timenames[6] ":"$timenames[7] "):"").($h?"$h ".($h==1?"$timenames[8] ":"$timenames[9] "):"").($mi?"$mi ".($mi==1?"$timenames[10] ":"$timenames[11] "):"");
		$r=~s/\s$//;
		return Dotiac::DTL::Value->safe($r);
	}
	return Dotiac::DTL::Value->safe("$y ".($y==1?$timenames[0]:$timenames[1])) if ($y);
	return Dotiac::DTL::Value->safe("$m ".($m==1?$timenames[2]:$timenames[3])) if ($m);
	return Dotiac::DTL::Value->safe("$w ".($w==1?$timenames[4]:$timenames[5])) if ($w);
	return Dotiac::DTL::Value->safe("$d ".($d==1?$timenames[6]:$timenames[7])) if ($d);
	return Dotiac::DTL::Value->safe("$h ".($h==1?$timenames[8]:$timenames[9]).($mi?" $mi ".($mi==1?$timenames[10]:$timenames[11]):"")) if $h;
	return Dotiac::DTL::Value->safe("$mi ".($mi==1?$timenames[10]:$timenames[11])) if ($mi);
	
}

sub title {
	my $val=shift;
	my $value=$val->repr();
	$value=~s/(\w+)/ucfirst($1)/eg;
	return $val->set($value);
}

sub truncatewords {
	my $value=shift;
	my $words=shift;
	return $value unless $words and $words->number;
	my @val = split /(\s+)/,$value->repr;
	$words=($words->content-1)*2;
	return $value if $words >= $#val;
	#$words=$#val if $words > $#val;
	return $value->set(CORE::join("",@val[0 .. $words],($val[$words]=~/\.\.\./?"":"...")));
}

my %singletags=qw/br 1 col 1 link 1 base 1 img 1 param 1 area 1 hr 1 input 1/;

sub truncatewords_html {
	my $val=shift;
	my $value=$val->string();
	my $words=shift;
	return $val unless $words and $words->number;
	my $len=CORE::length($value);
	$words=$words->content;
	my $ret="";
	my @tags;
	while ($words and (pos($value)||0) < $len) {
		my $pos=pos($value);
		if ($a=$value=~m/\G(\s*[^<\s]+\s*|\s+\Z)/g) {
			$ret.=$1;
			#warn "$1 $words";
			$words--;
			next;
		}
		else {
			pos($value)=$pos;
		}
		if ($a=$value=~m/\G(\s*)</g) {
                    my $space = $1;
			if ($a=$value=~m/\G([^>]+)>/g) {
				$ret.="$space<$1>";
				my $tag=lc($1);
				if ($tag eq "/") { #SGML: Close last tag </>, never seen it used in HTML, but whatever.
					shift @tags;
				}
				elsif ($tag=~s/^\///) {
					my @t=@tags;
					$tag=~m/^(\w+)/;
					$tag=$1;
					my $t=shift @t;
					$t=shift @t while (@t and $t ne $tag);
					if ($t eq $tag) {
						@tags=@t; #SGML: <p><b>bbb</p>, the </p> also closes </b>.
					}
					next;
				}
				elsif ($tag=~s/\/$//) { #XML: Singletag
					next;
				}
				else {
					$tag=~m/^(\w+)/;
					$tag=$1;
					unshift @tags,$tag unless $singletags{$tag}; #Some HTML-Tags shouldn't be closed, (why not, I wonder)
					next;
				}
			}
			else {
				return $val->set($ret); #Parsingerror.
			}
		}
		else {
			pos($value)=$pos;
		}
		
	}
	return $val if $words > 0; #Should be allright then.
	$ret=~s/\s+$//g;
	$ret.="..." unless $ret=~m/\.\.\.$/;
	foreach my $t (@tags) {
		$ret.="</$t>";
	}
	return $val->set($ret);
}


#TODO TODO TODO
# Split in subfuntion ziehe safe aus $value->safe();
#TODO TODO TODO
#
my $unordered_list;
$unordered_list = sub {
	my $e=shift;
	my $save=shift;
	my $level=shift;
	my $res="";
	return "" unless ref $e and ref $e eq "ARRAY";
	my @loop=@$e;
	while (@loop) {
		my $title=shift @loop;
		$title=$escape->($title) unless $save;
		$res.="\t"x($level)."<li>$title";
		if (ref $loop[0] and ref $loop[0] eq "ARRAY") {
			$res.="\n"."\t"x($level)."<ul>\n";
			$res.=$unordered_list->(shift(@loop),$save,$level+1);
			$res.="\t"x($level)."</ul>\n";
			$res.="\t"x($level);
		}
		
		$res.="</li>\n"
	}
	return $res;

};

sub unordered_list {
	my $value=shift;
	return "<li>".$value->string()."</li>\n" if $value->scalar;
	return $value unless $value->array;
	my @loop=@$value;
	if (@loop==2 and ref $loop[1] and Scalar::Util::reftype($loop[1]) eq "ARRAY" and (ref $loop[1]->[0] or not @{$loop[1]})) {
		#$ret.=unordered_list($loop[0],$save,$level);
		my $r=sub {
			my $d=shift;
			my $r=shift;
			return ($d->[0],[map {$r->($_,$r)} @{$d->[1]}]);
		};
		@loop=$r->($value,$r);
		#@loop=($loop[0],[map {@$_} @{$loop[1]}]);
	}
	my $ret=$unordered_list->($value->content(),$value->safe,0);
	return Dotiac::DTL::Value->safe($ret);
}


sub upper {
	my $value=shift;
	$value->set(uc $value->repr);
	return $value;
}

#This awesome Regex ripped of http://geekswithblogs.net/casualjim/archive/2005/12/01/61722.aspx

#Addition: parameters: Safechars. urlencode:"" encodes also slashes, needed if you are gonna built an url and urlencode:":/?=&" can be run over an http://foo/bar?foo=bar string
sub urlencode {
	my $val=shift;
	my $value=$val->repr;
	my $safe="/";
	if (@_) {
		$safe=shift;
		$safe=$safe->repr() if ref $safe; # For internal use
	}
	$safe="" unless $safe;
	$safe=quotemeta($safe);
	my $find=qr/([^\w$safe\.~-])/;
	$value=~s/$find/uc sprintf("%%%02x",ord($1))/eg;
	return $val->set($value);
}

sub urlize {
	my $value=shift;
	$value=$value->string();
	#$value=~s"(^|(?<!\w=)[^\"])((?#Protocol)(?:(?:ht|f)tp(?:s?)\:\/\/|~/|/)?(?#Username:Password)(?:\w+:\w+@)?(?#Subdomains)(?:(?:[-\w]+\.)+(?#TopLevel Domains)(?:com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))(?#Port)(?::[\d]{1,5})?(?#Directories)(?:(?:(?:/(?:[-\w~!\$+|.,=]|%[a-f\d]{2})+)+|/)+|\?|#)?(?#Query)(?:(?:\?(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)(?:(?:&amp;|&|;)(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)*)*(?#Anchor)(?:#(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)?)(?!\")"my $a=$2;($1||'').'<a href=\"'.($a=~/\w+:\/\//?$a:\"http://$a\").'\" rel=\"nofollow\" >'.$a.'</a>'"eg;
	$value=~s"((?#Protocol)(?:(?:ht|f)tp(?:s?)\:\/\/|~/|/)?(?#Username:Password)(?:\w+:\w+@)?(?#Subdomains)(?:(?:[-\w]+\.)+(?#TopLevel Domains)(?:com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))(?#Port)(?::[\d]{1,5})?(?#Directories)(?:(?:(?:/(?:[-\w~!\$+|.,=]|%[a-f\d]{2})+)+|/)+|\?|#)?(?#Query)(?:(?:\?(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)(?:(?:&amp;|&|;)(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)*)*(?#Anchor)(?:#(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)?)"my $a=$1;'<a href=\"'.($a=~/\w+:\/\//?$a:\"http://$a\").'\" rel=\"nofollow\" >'.$a.'</a>'"eg;
	return Dotiac::DTL::Value->safe($value);
}

sub urlizetrunc {
	my $value=shift;
	$value=$value->string();
	my $len=shift;
	if ($len and $len->number) {
		$len=int($len->content);
	}
	else {
		$len=0;
	}
	$len=15 unless $len;
	#$value=~s"(^|(?<!\w=)[^\"])((?#Protocol)(?:(?:ht|f)tp(?:s?)\:\/\/|~/|/)?(?#Username:Password)(?:\w+:\w+@)?(?#Subdomains)(?:(?:[-\w]+\.)+(?#TopLevel Domains)(?:com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))(?#Port)(?::[\d]{1,5})?(?#Directories)(?:(?:(?:/(?:[-\w~!\$+|.,=]|%[a-f\d]{2})+)+|/)+|\?|#)?(?#Query)(?:(?:\?(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)(?:(?:&amp;|&|;)(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)*)*(?#Anchor)(?:#(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)?)(?!\")"my $a=$2;($1||'').'<a href=\"'.($a=~/\w+:\/\//?$a:\"http://$a\").'\" rel=\"nofollow\">'.($len >= CORE::length($a)?$a:substr($a,0,$len).'...').'</a>'"eg;
	$value=~s"((?#Protocol)(?:(?:ht|f)tp(?:s?)\:\/\/|~/|/)?(?#Username:Password)(?:\w+:\w+@)?(?#Subdomains)(?:(?:[-\w]+\.)+(?#TopLevel Domains)(?:com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))(?#Port)(?::[\d]{1,5})?(?#Directories)(?:(?:(?:/(?:[-\w~!\$+|.,=]|%[a-f\d]{2})+)+|/)+|\?|#)?(?#Query)(?:(?:\?(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)(?:(?:&amp;|&|;)(?:[-\w~!\$+|.,*:]|%[a-f\d{2}])+=(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)*)*(?#Anchor)(?:#(?:[-\w~!\$+|.,*:=]|%[a-f\d]{2})*)?)"my $a=$1;'<a href=\"'.($a=~/\w+:\/\//?$a:\"http://$a\").'\" rel=\"nofollow\">'.($len >= CORE::length($a)?$a:substr($a,0,$len).'...').'</a>'"eg;
	return Dotiac::DTL::Value->safe($value);
}



sub wordcount {
	my $value=shift;
	$value=$value->repr;
	return Dotiac::DTL::Value->safe(scalar( ()=$value=~m/\S+/g));
}

sub wordwrap {
	my $val=shift;
	my @value = split /(\s+)/,$val->repr;
	my $len=shift;
	if ($len and $len->number) {
		$len=int($len->content);
	}
	else {
		$len=0;
	}
	$len=80 unless $len;
	my $line=shift @value;
	my $ret="";
	while (my $space=shift(@value)) {
		my $word=shift(@value);
		$word="" unless $word;
		if (CORE::length($line.$space.$word) > $len) {
			$ret.=$line."\n";
			$line=$word;
		}
		else {
			$line.=$space.$word;
		}
	}
	$ret.=$line;
	return $val->set($ret);
}



sub yesno {
	my $value=shift;
	my $yes=shift;
	if (@_) {
		my $no=shift;
		my $undef=shift;
		$yes=Dotiac::DTL::Value->safe("") unless $yes;
		$no=Dotiac::DTL::Value->safe("") unless $no;
		$undef=$no unless $undef;
		return $yes if $value->true;
		return $undef if $value->undef;
		return $no;
	}
	if ($yes) {
		$yes=$yes->string();
	}
	else {
		$yes="";
	}
	my ($y,$no,$undef) = split /,/,$yes,3;
	$no="" unless $no;
	$undef=$no unless $undef;
	return Dotiac::DTL::Value->safe($y) if $value->true;
	return Dotiac::DTL::Value->safe($undef) if $value->undef;
	return Dotiac::DTL::Value->safe($no);
}


=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=cut
1;

__END__

=head1 NAME

Dotiac::DTL::Filter - Filters for variables

=head1 SYNOPSIS

	{{ variable|add:10|upper }}
	{% cycle variable|add:10|upper variable2|add:10|upper %}
	{% include "foo.html"|cut:"o"|upper %}
	...

=head1 DESCRIPTION

Filters are small functions that are applied on variables. They can be stacked by using a pipe character ( | ), without space. And they have arguments, denoted by a ":".

Some filters don't require arguments, some have optional ones and other require then.

If a filter can't use an argument or variable, it will just return it unchanged.

In this implementation, you can also apply variables as arguments to any filter and even have multiple arguments for your own filters (seperated by a ","). However this may cause trouble in some tags (like {% url %}) and is not compatible to Django. Just know it's there if you need it.

Examples:

	{{ var|upper }}

Runs the "upper" filter on the content of the variable "var"

	{{ var|add:"10" }}

Adds a 10 to the content of the variable "var". the "10" is the argument (also called parameter) to the "add" filter.

	{{ var|upper|add:"foo" }}

This construct runs the "upper" filter on the content of the variable "var" and then adds the string "foo" to the result of that.

	{{ foo|add:bar }}

In this implementation: adds the content of the variable "bar" to content of the variable "foo".

B<These are the filters you can use>:

=head2 add :VALUE

Adds a VALUE (number or string) to the variable.

If both variable and the argument are both numbers, they are added together.

If one or both of them are not numbers, the arguments are concatenated together.

	{{ "10"|add:"10" }} {# 20 #}
	{{ "10"|add:"-5" }} {# 5 #}
	{{ "10"|add:"foo" }} {# 10foo #}
	{{ "foo"|add:"bar" }} {# foobar #}
	{{ "bar"|add:100 }} {# 100 #}
	{% filter add:100 %}1{{ "10"|add:"20"}}{% endfilter %} {# 230 #} {# 100+1.(10+20) #}

=head3 Bugs and Differences to Django

Django only supports numbers to be added (and substracted).

=head2 addslashes

Adds backslashes before any quotes in the variable. This is useful for CSV output

If you want some more 

	{{ 'Daimos "TheKing" Miller / Peter \'TheMan\' Miller'|addslashes }} {# Daimos \"TheKing\" Miller \\ Peter \'TheMan\' Miller #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 capfirst

Converts the first character of the value to an uppercase. Also see upper() and lower()

	{{"foo"|capfirst}} {# Foo #}
	{{"bar"|capfirst}} {# Bar #}
	{{"foo bar"|capfirst}} {# Foo bar #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 center :FIELDWIDTH

Centers a text in a field of FIELDWIDTH spaces.

This is not usefull for HTML (unless in <pre> like tags), but for email's from forms or other text files.

	{{ "Hello"|center:"20" }} {# "       Hello        " #}

B<The string is not truncated if it's larger than FIELDWIDTH>

=head3 Bugs and Differences to Django

Also supports a padding parameter, if you want something other than spaces:

	{{ "Hello":center:"20";"-" }} {# "-------Hello--------" #}

=head2 cut :STRING

Removes any occurences of a STRING from the value.

	{{ "Hello World"|cut:"el" }} {# Hlo World #}
	{{ "Hello World"|cut:"l" }} {# Heo Word #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 date :FORMAT

Formats a time, according to a FORMAT according to a FORMAT..

	{{ "20002312"|date:"jS F Y H:i" }} {# 20th August 1970 14:11 #}
	{{ post.time|date:"jS o\f F" }} {# It is the 4th of September #}

The retured value will be safe if the FORMAT string is safe.

=head3 Format options

You can combine as many of these as you like or need:

	{{ var|date:"d. b." }}

=over

=item "\"

Returns the next character, regardless if it is a format character or not.

	{{ var|date: "\H\e\l\l\o \W\o\r\l\d" }} {# =Hello World #}

This also means "\n" will in this case render an "n" and NOT a newline. Same for "\t","\f","\b","\r".

=item "a"

Returns whether it is AM or PM in Associated Press style: "a.m." or "p.m".

	{{ var|date: "a" }} {# a.m. on in the morning#}

B<This might change if a locale module is loaded.>

=item "A"

Returns AM or PM. 

	{{ var|date: "A" }} {# AM #}

B<This might change if a locale module is loaded.>

=item "b"

Returns the current month in 3 lowercase letters.

	{{ var|date: "b" }} {# dec #}

B<This might change if a locale module is loaded.>

=item "d"

Returns the day of the month with a leading zero.

	{{ var|date: "d" }} {# 01 #} to {# 31 #}

=item "D"

Returns the day of the week in 3 letters (2 letters on some locales)

	{{ var|date: "D" }} {# Sun #}

B<This might change if a locale module is loaded.>

=item "f"

Returns the time with hours and minutes, but minutes are left out if they are 0.

	{{ var|date: "f" }} o'clock {# 11:30 o'clock #} {# 3 o'clock #}

=item "F"

Returns the month in long form.

	{{ var|date: "F" }} {# December #}

B<This might change if a locale module is loaded.>

=item "g" 

Returns the hour in 12-hour format without leading zeros.

	{{ var|date: "g" }} {# 1 #} to {# 12 #}

=item "G" 

Returns the hour in 24-hour format without leading zeros.

	{{ var|date: "G" }} {# 0 #} to {# 24 #}

=item "h" 

Returns the hour in 12-hour format with a leading zero.

	{{ var|date: "h" }} {# 01 #} to {# 12 #}

=item "H" 

Returns the hour in 24-hour format with a leading zero.

	{{ var|date: "H" }} {# 00 #} to {# 24 #}

=item "i" 

Returns the minutes with a leading zero.

	{{ var|date: "i" }} {# 00 #} to {# 60 #}

=item "j"

Returns the day of the month without leading zeros.

	{{ var|date: "j" }} {# 1 #} to {# 31 #}

=item "l"

Returns the day of the week as a long text.

	{{ var|date: "l" }} {# Sunday #}

B<This might change if a locale module is loaded.>

=item "L"

Returns 1 or 0 whether it's a leap year.

	{{ var|date: "L" }} {# 1 #}

=item "m"

Returns the current month as a number with leading zeros.

	{{ var|date: "m" }} {# 01 #} to {# 12 #}

=item "M"

Returns the current month in 3 letters.

	{{ var|date: "M" }} {# Dec #}

B<This might change if a locale module is loaded.>

=item "n"

Returns the current month as a number without leading zeros.

	{{ var|date: "m" }} {# 1 #} to {# 12 #}

=item "M"

Returns the current in Associated Press style notation.

	{{ var|date: "M" }} {# Jan. #} {# March #} {# July #}

B<This might change if a locale module is loaded.>

=item "O"

Returns the difference to Greenwich time in hours.

	{{ var|date: "O" }} {# +0100 #}

=item "P"

Returns either the time in 12 hours and minutes if not zero with a.m. or p.m., midnight or noon.

	{{ var|date: "P" }} {# 1 p.m. #} {# 11:56 a.m. #} {# midnight #} {# noon #}

=item "r"

Returns an RFC 2822 formatted date.

	{{ var|date: "r" }} {# Sun, 28 Dec 2008 18:36:24 +0200' #}

B<This might change if a locale module is loaded.>

=item "s"

Returns the seconds with a leading zero.

	{{ var|date: "s" }} {# 00 #} to {# 59 #}

=item "S"

Returns the ordinal suffix for the day of the month.

	{{ var|date: "S" }} {# st #} {# nd #} {# rd #} {# th #}

Defaults to english, B<this may change if a locale module is loaded.>

=item "t"

Returns the number of days in the given month.

	{{ var|date: "t" }} {# 28 #} to  {# 31 #}

=item "T"

Returns the current timezone (needs the POSIX module)

	{{ var|date: "T" }} {# CET #} {# GMT #} {# EST #}...

=item "w"

Returns the day of week as a number from 0 (Sunday) to 6 (Saturday)

	{{ var|date: "w" }} {# 1 #} to {# 6 #}

=item "W"

Returns the ISO-8601 week number of year (uses the POSIX module), weeks start on monday.

	{{ var|date: "w" }} {# 1 #} to {# 53 #}

=item "y" 

Returns the year in two digits (with leading zeros) 

	{{ var|date: "y" }} {# 08 #}

=item "Y"

Returns the year in four (or more) digits (with leading zeros) 

	{{ var|date: "Y" }} {# 2008 #}

=item "z"

Returns the day of the year without leading zeros

	{{ var|date: "z" }} {# 0 #} to {# 365 #}

=item "Z"

Returns the difference of the current timezone to GMT in seconds.

	{{ var|date: "Z" }} {# -43200 #} to {# 43200 #}

=back

=head3 Bugs and Differences to Django

Since Perl has no default DateTime Object, this expects a normal unix timestamp ( result of the time() call in perl).

It also excepts the result of localtime as an array reference, this is useful for timestamps > 2038 on 32-Bit machines.

	var=>[36,31,21,2,0,109,5,1,0];
	
	{{ var|date:"jS F Y H:i" }} {# 2nd January 2009 21:31 #}

=head2 default :STRING

If the value is false (See L<Dotiac::DTL::Tag::if>) it will return the STRING, otherwise the value.

	{{ "Hello World"|cut:"el" }} {# Hlo World #}
	{{ "Hello World"|cut:"l" }} {# Heo Word #}

=head3 Bugs and Differences to Django

Perl considers other things false as Python.

=head2 default_if_none :STRING

If the value is not defined (not found or set to C<undef>) it will return the STRING, otherwise the value.

	{{ "Hello World"|cut:"el" }} {# Hlo World #}
	{{ "Hello World"|cut:"l" }} {# Heo Word #}

=head3 Bugs and Differences to Django

Perl considers other things false as Python.

=head2 dictsort :PROPERTY

Sorts an array of hashes, objects or arrays by a common PROPERTY. (See C<|dictsort> for reverse sort)

	Posts=>[
		{title=>"I love food",text=>"I really do",category=>"My life"},
		{title=>"I love TV",text=>"Even more than food",category=>"My life"},
		{title=>"Simpsons",text=>"Awesome TV show",category=>"TV shows"},
		{title=>"ANTM",text=>"I love this one",category=>"TV shows"},
		{title=>"xkcd",text=>"The best webcomic",category=>"Webcomics"}
	]

	{% for x in Posts|dictsort:"category" %}...
	{% endfor %}
	{% for x in Posts|dictsort:"title" %}...
	{% endfor %}

=head3 Bugs and Differences to Django

If PROPERTY is omitted, it just sorts by name, you can use this to sort an array of strings. 


	ListofWords=>["Foo","Bar","Baz"]

	{% for x in Posts|dictsort %}
		{{x}}
	{% endfor %}

=head2 dictsortreversed :PROPERTY

Sorts an array of hashes, objects or arrays by a common PROPERTY in reverse order. (See C<|dictsort> for normal order)

	Posts=>[
		{title=>"I love food",text=>"I really do",category=>"My life"},
		{title=>"I love TV",text=>"Even more than food",category=>"My life"},
		{title=>"Simpsons",text=>"Awesome TV show",category=>"TV shows"},
		{title=>"ANTM",text=>"I love this one",category=>"TV shows"},
		{title=>"xkcd",text=>"The best webcomic",category=>"Webcomics"}
	]

	{% for x in Posts|dictsort:"category" %}...
	{% endfor %}
	{% for x in Posts|dictsort:"title" %}...
	{% endfor %}

=head3 Bugs and Differences to Django

If PROPERTY is omitted, it just sorts by name, you can use this to sort an array of strings.

	ListofWords=>["Foo","Bar","Baz"]

	{% for x in Posts|dictsort %}
		{{x}}
	{% endfor %}

=head2 divisibleby :NUMBER

Returns 1 (true value) if the value is divisible by NUMBER.

	{{ "21"|divisibleby:"7" }} {# 1 #}
	{{ "45"|divisibleby:"8" }} {# 0 #}

=head3 Bugs and Differences to Django

Django's divisibleby returns a C<True> or C<False>. There is no binary type in perl, so it will return C<1> or C<0>

=head2 escape 

Marks a string as unsafe, i.e. in need of escaping for output.

C< < >, C<< > >>, C<'>, C<"> and C<&> are converted to C<&lt;>, C<&gt;>, C<&#39;>, C<&quot;> and C<&amp;> respectively.

B<Beware:> Escaping is done only once and only after all filters are applied. If you want to esacpe at this position in the filter pipeline use C<force_escape>.

	{{ "<>"|escape }} {# &lt;&gt; #}
	{{ "<>"|escape|cut:"&"|escape }} {# &lt;&gt; #} {# Escaping is done only once after all filter are applied #}
	{{ "<>"|force_escape|cut:"&"|escape }} {# lt;gt; #} {# This might be what you want. #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 escapejs 

Escapes a Javascript (JSON) String. This will not generate JSON Code out of datastructures, use L<Dotiac::DTL::Addon::JSON> for that.

	<script>var="{{ "\""|escapejs|safe }}"</script> {# <script>var="\""</script> #} {# you will have to mark it as safe if you are generating in script tags #}
	<body onload="alert('{{ "\""|escapejs|escape }}')"> {# <body onload="alert('\&quot;')" #} {# Better escape in event handlers#}

=head3 Bugs and Differences to Django

Might escape some more characters than original Django.

On perl 5.6.2 unicode output is not really supported, you will get for example:

	\u00e3\u0093\u00b4 instead of \u4532

=head2 filesizeformat

Returns a number of bytes in bytes, Kb, Mb, Gb or Tb ... This is used to display the size of files/traffic or anything else counted this way to be read by humans

	{{ "3939232"|filesizeformat }} {# 3.76 Mb #}
	{{ "5838388588776"|filesizeformat }} {# 5.31 Tb #}
	{{ "1012"|filesizeformat }} {# 1012 bytes #} {# < 1024 #}

This will divide by 1024, not 1000.

=head3 Bugs and Differences to Django

If you find any, pleas report them

=head2 first

Returns the first element of a list.

	var=>[1,2,3,4];

	{{ var|first }} {# 1 #}
	{{ "abc"|make_list|first }} {# a #}

=head3 Bugs and Differences to Django

Also returns the first value in a hash.

=head2 fix_ampersands

Replace C<&> with C<&amp;>. See C<escape> and C<force_escape> for a better solution. 

Doesn't mark the value safe.

	var=>"Tom & Jerry";

	{{ var|fix_ampersands }} {# Tom &amp;amp; Jerry #}
	{{ var|fix_ampersands|safe }} {# Tom &amp; Jerry #}

=head3 Bugs and Differences to Django

This is somewhat deprecated in Django and replaced by the autoescaping routines. Don't use this anymore.

=head2 floatformat :DIGITS

Formats a (float) value with variables with a number DIGITS after the dot. If DIGITS is negative, it will cut off trailing zeros (and the dot).

DIGITS defaults to -1

	{{ "1.001"|floatformat }} {# 1 #}
	{{ "1.001"|floatformat:"2" }} {# 1.00 #}

=head3 Bugs and Differences to Django

If you find any, please put them  in the tracker or drop me a mail.

=head2 force_escape 

Escapes the string at this point in the filter stack (not at the end like C<escape>) 

C< < >, C<< > >>, C<'>, C<"> and C<&> are converted to C<&lt;>, C<&gt;>, C<&#39;>, C<&quot;> and C<&amp;> respectively.

See also C<escape>

	{{ "<>"|force_escape|escape }} {# &amp;lt;&amp;gt; #}
	{{ "<>"|force_escape|safe }} {# &lt;&gt; #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 get_digit :NTH

Extracts the NTH digit (from the right) of an integer value.

Just returns the value if it was not an integer.

	{{ "4893"|get_digit:"3" }} {# 8 #}
	{{ "4893"|get_digit:"2" }} {# 9 #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 iriencode

Encodes Unicode characters according to rfc3987, all characters above 0x7f are encoded.

You won't need this filter if the output of the script is already Unicode.

This does not replace urlencode, but should be used in conjunction with it.

The result from iriencode of an iriencoded string will not change it anymore.

	{{ "http://www.google.com/?q=\u0334%20"|iriencode }} {# http://www.google.de/?q=%CC%B4%20 #} {# %20 stayed #}
	http://www.google.com/?q={{ var|urlencode|iriencode }}&hl=en {# The best way if var contains urlunsafe chars and unicode chars #}

=head3 Bugs and Differences to Django

This won't work on EBCDIC Systems for now, sadly.

If you find anything else, please report them.

=head2 join :STRING

Joins a list-value by a STRING.

	var=>["Foo","Bar","!"];

	{{ var|join:" : " }} {# Foo : Bar : ! #}
	{{ "4893"|make_list|join:"," }} {# 4,8,9,3 #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 last

Returns the last element of a list. (See also C<first>)

	var=>[1,2,3,4];

	{{ var|first }} {# 4 #}
	{{ "abc"|make_list|first }} {# c #}

=head3 Bugs and Differences to Django

Also returns the last value in a hash.

=head2 length

Returns the length of arrays, lists or strings.

The returned value is always marked safe (not that it matters for output)

	var=>[10,2,73,64];

	{{ var|length }} {# 4 #}
	{{ "abc"|length }} {# 3 #}

=head3 Bugs and Differences to Django

Tries to call count() on objects to get the length.

C<undef> (C<none> in python) will be counted as "".

=head2 length_is :LENGTH

Returns 1 if the length of arrays, lists or strings is equal to LENGTH, "" otherwise.

The returned value is always marked safe (not that it matters for output)

	var=>[10,2,73,64];

	{% if var|length_is:"4" %}1{% else %}0{% endif %} {# 1 #}
	{% if "abc"|length_is:"2" %}1{% else %}0{% endif %} {# 0 #}

=head3 Bugs and Differences to Django

Tries to call count() on objects to get the length.

Unknown datastructers (GLOBS, FILEHANDLES, SCALARREFS ... ) will never return true.

C<undef> (C<none> in python) will be counted as "".

=head2 linebreaks

Converts newlines in the value to paragraphs (<p>) and breaks <br>. The output will always be a paragraph.

Two linebreaks/newlines (\n\n) start a new paragraph, a single one gets converted into a <br /> tag.

This filter will apply escaping and return a safe string. Otherwise the <p> and <br /> tags are going to be messed up

	{{ "Hello\nWorld"|linebreaks }} {# <p>Hello<br />World</p> #}
	{{ "Hello\nWorld\n\n<b>Foo</b>"|escape|linebreaks|safe }} {# <p>Hello<br />World</p><p>&lt;b&gt;Foo&lt;/b&gt;</p>#}

=head3 Bugs and Differences to Django

This might mess up your HTML if the variable is marked safe, this will appear if you want the user to include HTML or something like BBCode.

You will have to use C<linebreaksbr> (See below) for that.

	{{ "<b>...\n\n..</b>"|safe|linebreaks }} {# <p><b>...</p><p>..</b></p> #} {# Invalid: You can see how the <b> tag is split up #}
	<p>{{ "<b>...\n\n..</b>"|safe|linebreaksbr }}</p> {# <p><b>...<br /><br />..</b></p> #} {# Valid! #} 

Many BBCode interpreters don't replace linebreaks by themselves. (In most forums for example you can as a user switch on "Post is HTML" "Post is BBCode" "Convert linebreaks")

=head2 linebreaksbr

Converts newlines into breaks <br>.

This filter will apply escaping and return a safe string. Otherwise the <br /> tags are going to be messed up

	{{ "Hello\nWorld"|linebreaksbr }} {# Hello<br />World #}
	{{ "Hello\nWorld\n\n<b>Foo</b>"|escape|linebreaksbr|safe }} {# Hello<br />World<br /><br />&lt;b&gt;Foo&lt;/b&gt;#}

=head3 Bugs and Differences to Django

If you find any, please report them

=head2 linenumbers

Writes a linenumber before each line.

	{{ "Hello\nWorld"|linenumbers }} 
	{# 1: Hello
	2: World #}
	{{ "Hello\nWorld\n\n<b>Foo</b>"|escape|linenumbers|safe }} 
	{# 1: Hello
	2: World
	3:
	4: <b>Foo</b> #}

=head3 Bugs and Differences to Django

If you find any, please report them

=head2 ljust :FIELDWIDTH

Leftjustifies a text in a field of FIELDWIDTH spaces.

This is not usefull for HTML (unless in <pre> like tags), but for email's from forms or other text files.

	{{ "Hello"|ljust:"20" }} {# "Hello               " #}

B<The string is not truncated if it's larger than FIELDWIDTH>

=head3 Bugs and Differences to Django

Also supports a padding parameter, if you want something other than spaces:

	{{ "Hello":ljust:"20";"-" }} {# "Hello---------------" #}

=head2 lower

Converts the value into lowercase.

	{{ "Hello, World"|lower }} {# hello, world #}

=head3 Bugs and Differences to Django

If you find any, please report them

=head2 make_list

Splits a value into a list of characters

	{% for x in "abc"|make_list %}{{ x }}{% if not forloop.last %},{% endif %}{% endfor %}{# a,b,c #}
	{{ "def"|make_list|join:"," }} {% d,e,f %}

=head3 Bugs and Differences to Django

If given a parameter it splits at the parameter:

	{{ "b,c,d"|make_list:","|join:" " }} {# b c d #}

=head2 phone2numeric

Converts a value into a phonenumber.

All this does is replace A-Y (without Q) with 2-9.

	{{ "800-FOOBAR"|phone2numeric }}{# 800-366227 #}
	{{ "Hello, World"|phone2numeric }} {% 43556, 96753 %}

=head3 Bugs and Differences to Django

This has no locale support for now, locales will have to redefine this one.

=head2 pluralize :STRING

Prints a different STRING if the value is not "1". This is very useful if you want to pluralize a value.

When STRING contains a comma ("y,ies") it will either take the first value on 1 and the other one in any other case.

The STRING defaults to "s".

	1 template{{ "1"|pluralize }}, 3 template{{ "3"|pluralize }} {# 1 template, 3 templates #}
	1 walrus{{ "1"|pluralize:"es" }}, 4 walrus{{ "4"|pluralize:"es" }} {# 1 walrus, 4 walruses #}
	1 berr{{ "1"|pluralize:"y,ies" }}, 6 berr{{ "6"|pluralize:"y,ies" }} {# 1 berry, 6 berries #}

=head3 Bugs and Differences to Django

Since Dotiac::DTL also supports multiple arguments to filters, you can also write this:

	1 cherr{{ "1"|pluralize:"y";"ies" }}, 6 cherr{{ "6"|pluralize:"y";"ies" }} {# 1 cherry, 6 cherries #}

This is useful if one of your STRINGs contains a comma.

	1 {{ "1"|pluralize:"";", and that's all" }}, 2 {{ "2"|pluralize:"";", and that's all" }} {# 1, 2, and that's all #}

=head2 pprint

For Debug

=head3 Bugs and Differences to Django

Uses Data::Dumper instead of pprint

=head2 random

Returns a random element of a list. (See also C<first> and C<last>)

	var=>[1,2,3,4];

	{{ var|first }} {# 3 #} {# or 1 or 2 or 4 #}
	{{ "abc"|make_list|first }} {# c #} {# or a or b #}

=head3 Bugs and Differences to Django

Also returns a random value of a hash.

=head2 removetags :TAGS

Removes HTML (XML) TAGS from the value. TAGS is a space seperated list of tags to be removed

	{{ "<p><b>Hello</b>World</p>"|removetags:"b" }} {# <p>HelloWorld</p> #}
	{{ "<p><b>H<u>el</u>lo</b><span class="w">World</span></p>"|removetags:"b span" }} {# <p>H<u>el</u>loWorld</p> #}

See C<striptags> if you want to strip all tags from the value.

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 rjust :FIELDWIDTH

Rightjustifies a text in a field of FIELDWIDTH spaces.

This is not usefull for HTML (unless in <pre> like tags), but for email's from forms or other text files.

	{{ "Hello"|rjust:"20" }} {# "               Hello" #}

B<The string is not truncated if it's larger than FIELDWIDTH>

=head3 Bugs and Differences to Django

Also supports a padding parameter, if you want something other than spaces:

	{{ "Hello":ljust:"20";"-" }} {# "---------------Hello" #}


=head2 safe 

Marks a string as safe, i.e. in no need of escaping for output.

Also see C<escape>.

	var="<>";

	{{ var|safe }} {# <> #}
	{{ var|escape|cut:"&"|safe }} {# <> #} {# Escaping is done only once after all filter are applied #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=cut

=head2 slice :POSITION

Extracts a sublist out of a list from a POSITION. POSITION is a string of two number seperated by a ":"

See also L<http://diveintopython.org/native_data_types/lists.html#odbchelper.list.slice>.

	var=[1,2,3,4];

	{{ var|slice:":2" }} {# [1, 2] #}
	{{ var|slice:"1:" }} {# [2, 3, 4] #}
	{{ var|slice:"1:2" }} {# [2] #}
	{{ var|slice:"-2:-1" }} {# [3] #}


=head3 Bugs and Differences to Django

Also allows you to get a single item:

	{{ var|slice:"3" }} {# 4 #} Same as: {{ var.3 }}

Also works on hashes. Then it slices the value list orderd by their keys.

=cut

=head2 slugify 

Converts the value to lowercase, removes all non word characters, removes trailing and leading whitespaces and replaces all other spaces with a "-".

The resulting value is marked safe.

This is useful if you want to generate a save ID for something like a name an user entered, while keeping the original meaning.

	{{ "Hello World"|slugify }} {# hello-world #}
	{{ "<b>Foo</b>"|slugify }} {# bfoob #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 stringformat :FORMAT 

FORMATs a value according to python's format rules. (str.format: L<http://docs.python.org/library/stdtypes.html#str.format>)

The leading % is dropped. ("%s" = "s"),

	{{ "Hello World"|stringformat:"s" }} {# Hello World #}
	{{ "3"|stringformat:"#+05b" }} {# 0b011 #}
	{{ "3"|stringformat:"#+02d" }} {# +03 #}

=head3 Bugs and Differences to Django

This uses perl's sprintf, which is about the same as python's format. See L<perlfunc/sprintf>

"r" is emulated

=head2 striptags

Removes all HTML (XML) tags from the value.

	{{ "<p><b>Hello</b>World</p>"|striptags }} {# HelloWorld #}
	{{ "<p><b>H<u>el</u>lo</b><span class="w">World</span></p>"|striptags }} {# HelloWorld #}

See C<removetags> if you want to remove only some tags from the value.

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 time :FORMAT

Formats a time, according to a FORMAT.

This only formats for time. See C<date> if you want to format date and time.

	{{ "20002312"|time:"H:i" }} {# 14:11 #}
	{{ post.time|time:"P" }} {# noon #}

The retured value will be safe if the FORMAT string is safe.

=head3 Format options

You can combine as many of these as you like or need:

	{{ var|time:"G:i A" }}

=over

=item "\"

Returns the next character, regardless if it is a format character or not.

	{{ var|time: "\H\e\l\l\o \W\o\r\l\d" %} {# =Hello World #}

This also means "\n" will in this case render an "n" and NOT a newline. Same for "\t","\f","\b","\r".

=item "a"

Returns whether it is AM or PM in Associated Press style: "a.m." or "p.m".

	{{ var|time: "a" }} {# a.m. on in the morning#}

B<This might change if a locale module is loaded.>

=item "A"

Returns AM or PM. 

	{{ var|time: "A" }} {# AM #}

B<This might change if a locale module is loaded.>

=item "f"

Returns the time with hours and minutes, but minutes are left out if they are 0.

	{{ var|time: "f" }} o'clock {# 11:30 o'clock #} {# 3 o'clock #}

=item "g" 

Returns the hour in 12-hour format without leading zeros.

	{{ var|time: "g" }} {# 1 #} to {# 12 #}

=item "G" 

Returns the hour in 24-hour format without leading zeros.

	{{ var|time: "G" }} {# 0 #} to {# 24 #}

=item "h" 

Returns the hour in 12-hour format with a leading zero.

	{{ var|time: "h" }} {# 01 #} to {# 12 #}

=item "H" 

Returns the hour in 24-hour format with a leading zero.

	{{ var|time: "H" }} {# 00 #} to {# 24 #}

=item "i" 

Returns the minutes with a leading zero.

	{{ var|time: "i" }} {# 00 #} to {# 60 #}

=item "O"

Returns the difference to Greenwich time in hours.

	{{ var|time: "O" }} {# +0100 #}

=item "P"

Returns either the time in 12 hours and minutes if not zero with a.m. or p.m., midnight or noon.

	{{ var|time: "P" }} {# 1 p.m. #} {# 11:56 a.m. #} {# midnight #} {# noon #}

=item "s"

Returns the seconds with a leading zero.

	{{ var|time: "s" }} {# 00 #} to {# 59 #}

=item "Z"

Returns the difference of the current timezone to GMT in seconds.

	{{ var|time: "Z" }} {# -43200 #} to {# 43200 #}

=back

=head3 Bugs and Differences to Django

Since Perl has no default DateTime Object, this expects a normal unix timestamp ( result of the time() call in perl).

It also excepts the result of localtime as an array reference, this is useful for timestamps > 2038 on 32-Bit machines.

	var=>[36,31,21,2,0,109,5,1,0];
	
	{{ var|time:"H:i" }} {# 21:31 #}

=head2 timesince :REFERNCETIME

Formats a time value and displays the time since REFERENCE TIME has passed.

REFERENCETIME is C<now> if it is omitted

If you have a past event and want to display the time since then you can use this filter.

For any time in the future it will return 0 Minutes

	{{ post.date|timesince }} {# 3 years #}
	{{ post.date|timesince:edit.date }} {# 3 minutes #} {# after the post #}

C<timesince> and C<timeuntil> only differ in the order of the arguments:

	{{ date1|timeuntil:date2 }} == {{ date2|timesince:date1 }}

The generated value is always marked as safe.

=head3 Bugs and Differences to Django

Like C<time> and C<date> it only accepts unix timestamps.

If given any additional parameter it will print out the full time, while without it will only print useful information

	{{ post.date|timesice:edit.date;"" }} {# 2 days 3 Minutes #}
	{{ post.date|timesice:"now";"" }} {# 3 years 2 days 2 seconds #} {# compare to current time #}

If you have just a elapsed time in seconds you can use this:

	{{ "0"|timesince:"60" }} {# 1 Minute #}

=head2 timeuntil :REFERNCETIME

Formats a time value and displays the time util REFERENCE TIME.

REFERENCETIME is C<now> if it is omitted.

If you have a funture event and want to display the time util then you can use this filter

For any time in the past it will return 0 Minutes

	{{ marriage.date|timeuntil }} {# 3 years #}
	{{ marriage.date|timeuntil:engagement.date }} {# 3 month #} {# after the engagement #}

C<timesince> and C<timeuntil> only differ in the order of the arguments:

	{{ date1|timesince:date2 }} == {{ date2|timeuntil:date1 }}

The generated value is always marked as safe.

=head3 Bugs and Differences to Django

Like C<time> and C<date> it only accepts unix timestamps.

If given any additional parameter it will print out the full time, while without it will only print useful information

	{{ post.date|timeuntil:edit.date;"" }} {# 3 month 3 Minutes #}
	{{ marriage.date|timeuntil:"now";"" }} {# 3 years 2 days 2 seconds #} {# compare to current time #}

If you have just a elapsed time in seconds you can use this:

	{{ "60"|timeuntil:"0" }} {# 1 Minute #}


=head2 title

Converts the value into titlecase.

	{{ "500 kilos of heroin found"|title }} {# 500 Kilos Of Heroin Found #}

=head3 Bugs and Differences to Django

If you find any, please report them

=head2 truncatewords :NUMBEROFWORDS

Cuts off the value after a specific NUMBER OF WORDS. Replaces the removed parts with "..."

	{{ "500 kilos of heroin found"|truncatewords:"3" }} {# 500 kilos of ... #}
	{{ "Today is monday"|truncatewords:"3" }} {# Today is monday #}

=head3 Bugs and Differences to Django

If you find any, please report them

=head2 truncatewords_html :NUMBEROFWORDS

Cuts off the value after a specific NUMBER OF WORDS. Replaces the removed parts with "..."

Same as C<truncatewords>, but also closes every HTML/XML Tag that's left open after the cutoff.

	{{ "<b>500 kilos <u>of</u> heroin found</b>"|truncatewords:"3" }} {# <b>500 kilos <u>of</u> ...</b> #}
	{{ "Today <u>is</u> monday"|truncatewords:"3" }} {# Today <u>is</u> monday #}

This one is much slower than truncatewords, so use this only when you have HTML tags in your value.

Returns a safe string and escapes an unsafe value.

=head3 Bugs and Differences to Django

If you have a six word string and a tag after the sixth word and you truncate to six words, it will still insert a "...".

=head2 unordered_list

Converts a list-value of list into a HTML-unordered list without the surrounding <ul> Tag.

	var=>[
		"Continents",
		[
			"North America",
			["USA","Kanada"],
			"South America",
			["Mexico"],
			"Europe"
			"Australia"
			"Asia"
		]
			
	]

	{{ var|unordered_list:"3" }} 
	{#
	<li>Continents
		<ul>
			<li>North America
				<ul>
					<li>USA</li>
					<li>Kanada</li>
				</ul>
			</li>
			<li>South America
				<ul>
					<li>Mexico</li>
				</ul>
			</li>
			<li>Europe</li>
			<li>Australia</li>
			<li>Asia</li>
		</ul>
	</li>
	#}

Also supports the old format.

The returned value is always escaped (if unsafe) and marked safe.

=head3 Bugs and Differences to Django

The old verbose format is supported, but I don't trust the implementation. (It works, I don't know why)

=head2 urlencode

Converts all characters except wordcharacters, minus, "~" and "/" to be used in an url

	http://www.google.com/?q={{ "Hello World"|urlencode }} {# http://www.google.com/?q=Hello%20World #}

=head3 Bugs and Differences to Django

If given an argument it allows for more characters to not be encoded.

	{{ "http://www.google.com/?q=Hello World"|urlencode }} {# http%3A//www.google.com/%3Fq%3DHello%20World #}
	{{ "http://www.google.com/?q=Hello World"|urlencode:":?=&" }} {# http://www.google.com/?q=Hello%20World #}

=head2 upper

Converts the value into uppercase. (Also see C<lower>)

	{{ "Hello, World"|upper }} {# HELLO, WORLD #}

=head3 Bugs and Differences to Django

If you find any, please report them

=head2 urlize

Converts all urls in the value.

	{{ "Go to www.dotiac.com and be happy"|urlize }} {# Go to <a href="www.dotiac.com" rel="nofollow">www.dotiac.com</a> and be happy #}

The value is escaped if needed and marked safe.

=head3 Bugs and Differences to Django

This uses a regular expression, so it might find different urls than Django.

This filter is not aware of <a href="..."></a> tags so it will convert the url in that. This should be fixed in the future.

=head2 urlizetrunc :LENGTH

Converts all urls in the value and truncates the output to LENGHT. LENGTH defaults to 15.

	{{ "Go to www.dotiac.com and be happy"|urlizetrunc:8 }} {# Go to <a href="www.dotiac.com" rel="nofollow">www.doti...</a> and be happy #}

The value is escaped if needed and marked safe.

=head3 Bugs and Differences to Django

This uses a regular expression, so it might find different urls than Django.

This filter is not aware of <a href="..."></a> tags so it will convert the url in that. This should be fixed in the future.

=head2 wordcount

Counts the number of words in the value

	{{ "Hello World"|wordcount }} {# 2 #}

The returned value is always safe.

=head3 Bugs and Differences to Django

If you find any, please report them.

=cut

=head2 wordwrap :AMOUNT_OF_CHARACTERS

Wraps the valuetext after a given AMOUNT OF CHARACTERS, but doesn't rip apart words.

AMOUNT_OF_CHARACTERS defaults to 80.

	{{ "This is some text without meaning"|wordwrap:7 }}
	{# This is
	some
	text
	without
	meaning #}

=head3 Bugs and Differences to Django

If you find any, please report them.

=head2 yesno :STRINGS

Returns a different STRING depending on the value. STRINGS is a comma seperated list of 2 or 3 strings.

The first string is the content returned if the value is true, the second is the content if it's false and the third is the content if the value is C<null> (C<undef> in perl).

If the thrid value is not given it defaults to the second one.

	true=>1,
	false=>0,
	null=>undef

	{{ true|yesno:"do it, don't do it" }} {# do it #}
	{{ false|yesno:"do it, don't do it" }} {# don't do it #}
	{{ null|yesno:"do it, don't do it" }} {# don't do it #}

	{{ true|yesno:"yes, no, don't care" }} {# yes #}
	{{ false|yesno:"yes, no, don't care" }} {# no #}
	{{ null|yesno:"yes, no, don't care" }} {# don't care #}

=head3 Bugs and Differences to Django

You can also give it three seperate arguments, this is quite useful for variables.

	{{ var|yesno:ontrue;onfalse;onnull }}

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

If you find any, please report them.

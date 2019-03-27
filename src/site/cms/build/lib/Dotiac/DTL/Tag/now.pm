#now.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#now.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Tag::now;
use base qw/Dotiac::DTL::Tag/;
use strict;
use warnings;

our $VERSION = 0.8;

sub new {
	my $class=shift;
	my $self={p=>shift()};
	bless $self,$class;
	$self->{format}=(Dotiac::DTL::get_variables(shift()))[0];
	return $self;
}
sub print {
	my $self=shift;
	print $self->{p};
	print scalar Dotiac::DTL::Filter::date(Dotiac::DTL::Value->safe(time),Dotiac::DTL::devar_raw($self->{format}||$Dotiac::DTL::DATETIME_FORMAT,@_))->string();
	$self->{n}->print(@_);
}
sub string {
	my $self=shift;
	return $self->{p}.Dotiac::DTL::Filter::date(Dotiac::DTL::Value->safe(time),Dotiac::DTL::devar_raw($self->{format}||$Dotiac::DTL::DATETIME_FORMAT,@_))->string().$self->{n}->string(@_);
}
sub perl {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	$self->SUPER::perl($fh,$id,@_);
	if ($self->{format}) {
		print $fh "my ";
		print $fh (Data::Dumper->Dump([$self->{format}],["\$format$id"]));
	}
	return $self->{n}->perl($fh,$id+1,@_);
}
sub perlprint {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlprint($fh,$id,$level,@_);
	print $fh "\t" x $level,"print scalar Dotiac::DTL::Filter::date(Dotiac::DTL::Value->safe(time),".($self->{format}?"Dotiac::DTL::devar_raw(\$format$id,\$vars,\$escape,\@_)":"\$Dotiac::DTL::DATETIME_FORMAT").")->string();\n";
	return $self->{n}->perlprint($fh,$id+1,$level,@_);
}
sub perlstring {
	my $self=shift;
	my $fh=shift;
	my $id=shift;
	my $level=shift;
	$self->SUPER::perlstring($fh,$id,$level,@_);
	print $fh "\t" x $level,"\$r.=Dotiac::DTL::Filter::date(Dotiac::DTL::Value->safe(time),".($self->{format}?"Dotiac::DTL::devar_raw(\$format$id,\$vars,\$escape,\@_)":"\$Dotiac::DTL::DATETIME_FORMAT").")->string();\n";
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
sub eval {
	my $self=shift;
	$self->{n}->eval(@_);
}
1;

__END__

=head1 NAME

Dotiac::DTL::Tag::now - The {% now FORMAT %} tag

=head1 SYNOPSIS

Template file:

	{% now "d. o\f F Y \a\t P %} {# 03. of May 1999 at 12:30 a.m. #}

=head1 DESCRIPTION

	Gives the current time and a specific FORMAT to the date filter (See L<Dotiac::DTL::Filter>). This will result in the current time being formatted according to the specified FORMAT.

=head2 Format options

You can combine as many of these as you like or need:

	{% now "d. b." %}

=over

=item "\"

Returns the next character, regardless if it is a format character or not.

	{% now "\H\e\l\l\o \W\o\r\l\d" %} {# =Hello World #}

This also means "\n" will in this case render an "n" and NOT a newline. Same for "\t","\f","\b","\r".

=item "a"

Returns whether it is AM or PM in Associated Press style: "a.m." or "p.m".

	{% now "a" %} {# a.m. on in the morning#}

B<This might change if a locale module is loaded.>

=item "A"

Returns AM or PM. 

	{% now "A" %} {# AM #}

B<This might change if a locale module is loaded.>

=item "b"

Returns the current month in 3 lowercase letters.

	{% now "b" %} {# dec #}

B<This might change if a locale module is loaded.>

=item "d"

Returns the day of the month with a leading zero.

	{% now "d" %} {# 01 #} to {# 31 #}

=item "D"

Returns the day of the week in 3 letters (2 letters on some locales)

	{% now "D" %} {# Sun #}

B<This might change if a locale module is loaded.>

=item "f"

Returns the time with hours and minutes, but minutes are left out if they are 0.

	{% now "f" %} o'clock {# 11:30 o'clock #} {# 3 o'clock #}

=item "F"

Returns the month in long form.

	{% now "F" %} {# December #}

B<This might change if a locale module is loaded.>

=item "g" 

Returns the hour in 12-hour format without leading zeros.

	{% now "g" %} {# 1 #} to {# 12 #}

=item "G" 

Returns the hour in 24-hour format without leading zeros.

	{% now "G" %} {# 0 #} to {# 24 #}

=item "h" 

Returns the hour in 12-hour format with a leading zero.

	{% now "h" %} {# 01 #} to {# 12 #}

=item "H" 

Returns the hour in 24-hour format with a leading zero.

	{% now "H" %} {# 00 #} to {# 24 #}

=item "i" 

Returns the minutes with a leading zero.

	{% now "i" %} {# 00 #} to {# 60 #}

=item "j"

Returns the day of the month without leading zeros.

	{% now "j" %} {# 1 #} to {# 31 #}

=item "l"

Returns the day of the week as a long text.

	{% now "l" %} {# Sunday #}

B<This might change if a locale module is loaded.>

=item "L"

Returns 1 or 0 whether it's a leap year.

	{% now "L" %} {# 1 #}

I<Not that needed with now, but with the date filter>

=item "m"

Returns the current month as a number with leading zeros.

	{% now "m" %} {# 01 #} to {# 12 #}

=item "M"

Returns the current month in 3 letters.

	{% now "M" %} {# Dec #}

B<This might change if a locale module is loaded.>

=item "n"

Returns the current month as a number without leading zeros.

	{% now "m" %} {# 1 #} to {# 12 #}

=item "M"

Returns the current in Associated Press style notation.

	{% now "M" %} {# Jan. #} {# March #} {# July #}

B<This might change if a locale module is loaded.>

=item "O"

Returns the difference to Greenwich time in hours.

	{% now "O" %} {# +0100 #}

=item "P"

Returns either the time in 12 hours and minutes if not zero with a.m. or p.m., midnight or noon.

	{% now "P" %} {# 1 p.m. #} {# 11:56 a.m. #} {# midnight #} {# noon #}

=item "r"

Returns an RFC 2822 formatted date.

	{% now "r" %} {# Sun, 28 Dec 2008 18:36:24 +0200' #}

B<This might change if a locale module is loaded.>

=item "s"

Returns the seconds with a leading zero.

	{% now "s" %} {# 00 #} to {# 59 #}

=item "S"

Returns the ordinal suffix for the day of the month.

	{% now "S" %} {# st #} {# nd #} {# rd #} {# th #}

Defaults to english, B<this may change if a locale module is loaded.>

=item "t"

Returns the number of days in the given month.

	{% now "t" %} {# 28 #} to  {# 31 #}

=item "T"

Returns the current timezone (needs the POSIX module)

	{% now "T" %} {# CET #} {# GMT #} {# EST #}...

=item "w"

Returns the day of week as a number from 0 (Sunday) to 6 (Saturday)

	{% now "w" %} {# 1 #} to {# 6 #}

=item "W"

Returns the ISO-8601 week number of year (uses the POSIX module), weeks start on monday.

	{% now "w" %} {# 1 #} to {# 53 #}

=item "y" 

Returns the year in two digits (with leading zeros) 

	{% now "y" %} {# 08 #}

=item "Y"

Returns the year in four (or more) digits (with leading zeros) 

	{% now "Y" %} {# 2008 #}

=item "z"

Returns the day of the year without leading zeros

	{% now "z" %} {# 0 #} to {# 365 #}

=item "Z"

Returns the difference of the current timezone to GMT in seconds.

	{% now "Z" %} {# -43200 #} to {# 43200 #}

=back

=head1 BUGS AND DIFFERENCES TO DJANGO

If you find any, please inform me about them.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut

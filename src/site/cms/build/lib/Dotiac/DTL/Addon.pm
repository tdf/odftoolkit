###############################################################################
#Addon.pm
#Last Change: 2009-01-19
#Copyright (c) 2009 Marc-Seabstian "Maluku" Lucksch
#Version 0.8
####################
#This file is part of the Dotiac::DTL project. 
#http://search.cpan.org/perldoc?Dotiac::DTL
#
#Addon.pm is published under the terms of the MIT license, which basically 
#means "Do with it whatever you want". For more information, see the 
#license.txt file that should be enclosed with libsofu distributions. A copy of
#the license is (at the time of writing) also available at
#http://www.opensource.org/licenses/mit-license.php .
###############################################################################

package Dotiac::DTL::Addon;
use strict;
use warnings;
our @Loaded=();
our %Loaded=();
our $VERSION = 0.8;

our %NOCOMPILE=(); #Modules that shouldn't be included into the compiled template, i.e. parser changing modules.

sub restore {
	while (my $mod=shift @Loaded) {
		eval {
			$mod->unimport();
		};
		if ($@) {
			warn $@;
			undef $@;
		}
	}
	%Loaded=();
	return;
}
1;

__END__

=head1 NAME

Dotiac::DTL::Addon - Manages Dotiac::DTL-addons.

=head1 DESCRIPTION

=head2 How to write your own addons

Addons are just perl modules living in the Dotiac::DTL::Addon namespace;

They can be called in two ways, as shown here with the example the Dotiac::DTL::Addon::foobar addon.

In a template file:

	{% load foobar %}

In a perl skript:

	use Dotiac::DTL::Addon::foobar; #not require, USE.

When loaded in a template file, the addon stays loaded until the next template calls string(), print() or ... . 

When used with "use Dotiac::DTL::Addon::..." the addon is loaded always, this may be sometimes what you disire, but not always:

Take a locale addon for example. One page might need the default english locale and another one will need the german one. If you load the german locale via C<use ...>, it will result in all pages having it.

=head2 How it works.

Both C<{% load x %}> and C<use Dotiac::DTL::Addon::x;> call the class-method  C<Dotiac::DTL::Addon::x->import()> when run. This import method makes all the neccessary adjustments to Dotiac::DTL.

When called by C<{% load x %}> the next call to string(),print() or similar will call the C<Dotiac::DTL::Addon::x->unimport()> method. That method must clean up all the changes done to Dotiac::DTL.

The unload order is the same as the load order, so the addons can just save the values they overwrite (if any).

If you are just adding a tag or a filter, you can just provide empty import() and unimport() methods, but see the warning below.

=head2 Example for a locale addon

This adds a german loacle as "german_locale":

	package Dotiac::DTL::Addon::german_locale;
	require Dotiac::DTL::Filter;
	use strict;
	use warnings;
	#Save the old values
	our @datemonths;
	our @datemonthl;
	our @datemontha;
	our @weekdays;
	our @weekdayl;
	our @timeampm;
	our @timespotnames;
	our @datesuffixes;
	our @filesizeformat;
	our $pluralizedefault;
	our $floatformatlocale;
	our @timenames;
	our $TIME_FORMAT;
	our $DATE_FORMAT;
	our $DATETIME_FORMAT;

	sub import {
		#Save values
		@datemonths=@Dotiac::DTL::Filter::datemonths;
		@datemonthl=@Dotiac::DTL::Filter::datemonthl;
		@datemontha=@Dotiac::DTL::Filter::datemontha;
		@weekdays=@Dotiac::DTL::Filter::weekdays;
		@weekdayl=@Dotiac::DTL::Filter::weekdayl;
		@timeampm=@Dotiac::DTL::Filter::timeampm;
		@timespotnames=@Dotiac::DTL::Filter::timespotnames;
		@datesuffixes=@Dotiac::DTL::Filter::datesuffixes;
		@filesizeformat=@Dotiac::DTL::Filter::filesizeformat;
		$pluralizedefault=$Dotiac::DTL::Filter::pluralizedefault;
		$floatformatlocale=$Dotiac::DTL::Filter::floatformatlocale;
		@timenames=@Dotiac::DTL::Filter::timenames;
		#Set new values
		@Dotiac::DTL::Filter::datemonths=qw( Jan Feb M&aum;r Apr Mai Jun Jul Aug Sep Okt Nov Dez );
		@Dotiac::DTL::Filter::datemonthl=qw( Januar Februar M&auml;rz April Mai Juni Juli August September Oktober November Dezember );
		@Dotiac::DTL::Filter::datemontha=qw( Jan. Feb. M&auml;rz April Mai Juni Juli Aug. Sep. Okt. Nov. Dez. );
		@Dotiac::DTL::Filter::weekdays=qw/So Mo Di Mi Do Fr Sa/;
		@Dotiac::DTL::Filter::weekdayl=qw/Sonntag Montag Dienstag Mittwock Donnerstag Freitag Samstag/;
		@Dotiac::DTL::Filter::timeampm=qw/a.m. p.m. AM PM/;
		@Dotiac::DTL::Filter::timespotnames=qw/Mitternacht Mittag/;
		@Dotiac::DTL::Filter::datesuffixes=qw/te/;
		@Dotiac::DTL::Filter::filesizeformat=qw/Bytes Kb Mb Gb Tb Eb Pb vielebytes vielebytes vielebytes vielebytes/;
		$Dotiac::DTL::Filter::floatformatlocale=sub {my $v=shift;$v=~s/\./,/g;return $v;};
		$Dotiac::DTL::Filter::pluralizedefault="n";
		@Dotiac::DTL::Filter::timenames=qw/Jahr Jahren Monat Monaten Woche Wochen Tag Tagen Stunde Stunden Minute Minuten/;
		#Time format
		$TIME_FORMAT=$Dotiac::DTL::TIME_FORMAT;
		$DATE_FORMAT=$Dotiac::DTL::DATE_FORMAT;
		$DATETIME_FORMAT=$Dotiac::DTL::DATETIME_FORMAT;
		$Dotiac::DTL::TIME_FORMAT="H:i";
		$Dotiac::DTL::DATE_FORMAT="d. M. Y";
		$Dotiac::DTL::DATETIME_FORMAT="d. M. Y, H:i";
	}
	sub unimport {
		#Restore values
		@Dotiac::DTL::Filter::datemonths=@datemonths;
		@Dotiac::DTL::Filter::datemonthl=@datemonthl;
		@Dotiac::DTL::Filter::datemontha=@datemontha;
		@Dotiac::DTL::Filter::weekdays=@weekdays;
		@Dotiac::DTL::Filter::weekdayl=@weekdayl;
		@Dotiac::DTL::Filter::timeampm=@timeampm;
		@Dotiac::DTL::Filter::timespotnames=@timespotnames;
		@Dotiac::DTL::Filter::datesuffixes=@datesuffixes;
		@Dotiac::DTL::Filter::filesizeformat=@filesizeformat;
		$Dotiac::DTL::Filter::pluralizedefault=$pluralizedefault;
		$Dotiac::DTL::Filter::floatformatlocale=$floatformatlocale;
		@Dotiac::DTL::Filter::timenames=@timenames;
		$Dotiac::DTL::TIME_FORMAT=$TIME_FORMAT;
		$Dotiac::DTL::DATE_FORMAT=$DATE_FORMAT;
		$Dotiac::DTL::DATETIME_FORMAT=$DATETIME_FORMAT;
	}
	1;


=head2 Example for a filter addon

	package Dotiac::DTL::Addon::myfilters;
	sub import {
		#No need to do anything here, filters can stay loaded all the time, but see the warning below.
	}
	sub unimport {
	}
	package Dotiac::DTL::Filters;
	#Not very useful filters, they should just serve as an example:
	# don't forget: Filters get and return Dotiac::DTL::Value objects.
	sub foobar {
		return Dotiac::DTL::Value->safe("Foobar")
	}
	sub helloworld {
		return Dotiac::DTL::Value->safe("Hello, World")
	}
	1;

=head2 Warning:

Using the above way to write a filter addon, will result in undefined behavior when two filters share the same name. This is because unimport() does nothing. A better way would be: Let import overwrite the function via modifying the symbol table and let unimport reverse it again.

=head2 Any tag addon

Tags can't share the same name, because they would be in the same file. (Unless you make it the wrong file, but that's not my problem then)

	package Dotiac::DTL::Addon::mytagcollection;
	require Dotiac::DTL::Tag::mytag1;
	require Dotiac::DTL::Tag::mytag2;
	#...
	sub import {
	}
	sub unimport {
	}
	1;

=head2 Internal stuff of Dotiac::DTL::Addons

These should not be used from anybody else than Dotiac::DTL::Core.

=head3 restore

Calls unimport on all methodes loaded by {% load %}

=head3 @Dotiac::DTL::Addons::loaded

Stores the loaded module names;

=head3 %Dotiac::DTL::Addons::NOCOMPILE 

Modules that shouldn't be included into compiled template (by {% load %}) should register themselves here.

These are mostly:

=over

=item Parser changing modules.

The Parser is not needed in compiled templates

=item Tag adding modules.

Since the Tags are compiled down to Perl-code, their source is not needed anymore

=back

B<Modules that add filters have to stay>

=head1 BUGS

If you find a bug, please report it.

=head1 SEE ALSO

L<http://www.djangoproject.com>, L<Dotiac::DTL>

=head1 LEGAL

Dotiac::DTL was built according to http://docs.djangoproject.com/en/dev/ref/templates/builtins/.

=head1 AUTHOR

Marc-Sebastian Lucksch

perl@marc-s.de

=cut




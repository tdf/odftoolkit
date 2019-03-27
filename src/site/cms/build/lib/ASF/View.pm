package ASF::View;

# abstract base class for default view methods
# see http://svn.apache.org/repos/asf/infrastructure/site/trunk/lib/view.pm for sample usage
# see http://svn.apache.org/repos/asf/thrift/cms-site/trunk/lib/view.pm for advanced usage
#
# newer features added in early 2014:
# * stacked wrapper views like 'offline' and 'memoize' that alter performance;
# * 'quick_deps' argument option to short-circuit full deps processing to either:
#   quick_deps => 1, the bare minimum, which just processes headers, should be tried first, or
#   quick_deps => 2, which keeps the content read off disk for processing too,
#   quick_deps => 3, which just takes the deps builds temporarily "offline", and hence is the
#                    most conservative of the three options;
# * loops in the %path::dependencies graph are supported with a 'quick_deps' = 1 or 2 setting.
# * 'snippet' wrapper view to preparse [snippet:arg1=val1:arg2=val2:...] template blocks;
# * new args like 'preprocess', 'deps' and 'conf' with custom behavior;
# * new wrapper views like 'reconstruct' and 'trim_local_links' that when combined with
#   'snippet', allow markdown files in source code repos to be imported to the website;
# * a more flexible 'sitemap' that takes a 'nest' argument to nest directory links into a tree

use strict;
use warnings;
use Dotiac::DTL qw/Template *TEMPLATE_DIRS/;
use Dotiac::DTL::Addon::markup;
use ASF::Util qw/read_text_file sort_tables parse_filename/;
use Data::Dumper ();

push our @TEMPLATE_DIRS, "templates";
our $VERSION = "1.16";

# This is most widely used view.  It takes a 'template' argument and a 'path' argument.
# Assuming the path ends in foo.mdtext, any files like foo.page/bar.mdtext will be parsed and
# passed to the template in the "bar" (hash) variable.
#
# Now supports templating within the markdown sources.
# Pass this a true 'preprocess' arg to enable template preprocessing of markdown sources...
# 'deps' arrayref and 'conf' arguments have special behavior (passed to foo.page/bar.mdtext)

sub single_narrative {
    my %args = @_;
    my $file = "content$args{path}";
    my $template = $args{template};

    read_text_file $file, \%args unless exists $args{content} and exists $args{headers};

    unless (exists $args{deps}) {
        view->can("fetch_deps")->($args{path} => \ my %deps, $args{quick_deps});
        my @d;
        while (my ($k, $v) = each %deps) {
            push @d, [$k, $v];
        }
        $args{deps} = [sort {$a->[0] cmp $b->[0]} @d] if @d;
    }

    $args{path} =~ s/\.\w+$/\.html/;
    $args{breadcrumbs} = view->can("breadcrumbs")->($args{path});

    my $page_path = $file;
    $page_path =~ s!\.[^./]+$!.page!;
    if (-d $page_path) {
        for my $f (grep -f, glob "$page_path/*.{mdtext,md}") {
            $f =~ m!/([^/]+)\.md(?:text)?$! or die "Bad filename: $f\n";
            $args{$1} = {};
            read_text_file $f, $args{$1};
            $args{$1}->{conf} = $args{conf} if exists $args{conf};
            $args{$1}->{deps} = $args{deps} if exists $args{deps};
            $args{$1}->{content} = sort_tables($args{preprocess}
                                        ? Template($args{$1}->{content})->render($args{$1})
                                        : $args{$1}->{content});
        }
    }
    $args{content} = sort_tables($args{preprocess}
                                     ? Template($args{content})->render(\%args)
                                     : $args{content});

    # the extra (3rd) return value is for sitemap support
    return Template($template)->render(\%args), html => \%args;
}

# Typical multi-narrative page view.  Has the same behavior as the above for foo.page/bar.mdtext
# files, parsing them into a bar variable for the template.
#
# Otherwise presumes the template is the path and any input content was generated in a wrapper.
# pass a true 'preprocess' arg for template preprocessing of content in foo.page/bar.mdtext files
# 'deps' arrayref and 'conf' args are passed along to foo.page/bar.mdtext files

sub news_page {
    my %args = @_;
    my $page_path = "content$args{path}";
    my $template = $args{content} // $page_path;
    $args{breadcrumbs} = view->can("breadcrumbs")->($args{path});

    unless (exists $args{deps}) {
        view->can("fetch_deps")->($args{path} => \ my %deps, $args{quick_deps});
        my @d;
        while (my ($k, $v) = each %deps) {
            push @d, [$k, $v];
        }
        $args{deps} = [sort {$a->[0] cmp $b->[0] } @d] if @d;
    }

    $page_path =~ s!\.[^./]+$!.page!;
    if (-d $page_path) {
        for my $f (grep -f, glob "$page_path/*.{mdtext,md}") {
            $f =~ m!/([^/]+)\.md(?:text)?$! or die "Bad filename: $f\n";
            $args{$1} = {};
            read_text_file $f, $args{$1};
            $args{$1}->{conf} = $args{conf} if exists $args{conf};
            $args{$1}->{deps} = $args{deps} if exists $args{deps};
            $args{$1}->{content} = sort_tables($args{preprocess}
                                         ? Template($args{$1}->{content})->render($args{$1})
                                         : $args{$1}->{content});
        }
    }

    # the extra (3rd) return value is for sitemap support
    return Template($template)->render(\%args), html => \%args;
}

# overridable internal sub for computing deps
# pass quick setting in 3rd argument to speed things up: 1 is faster than 2 or 3, but 3
# is guaranteed to work in 99.9% of all project builds.

sub fetch_deps {
    my ($path, $data, $quick) = @_;
    $quick //= 0;
    for (@{$path::dependencies{$path}}) {
        my $file = $_;
        my ($filename, $dirname) = parse_filename;
        for my $p (@path::patterns) {
            my ($re, $method, $args) = @$p;
            next unless $file =~ $re;
            if ($args->{headers}) {
                my $d = Data::Dumper->new([$args->{headers}], ['$args->{headers}']);
                $d->Deepcopy(1)->Purity(1);
                eval $d->Dump;
            }
            if ($quick == 1 or $quick == 2) {
                $file = "$filename" eq "index" ? $dirname : "$dirname$filename"; # no extension
                $data->{$file} = { path => $file, %$args };
                read_text_file "content/$_", $data->{$file}, $quick == 1;
            }
            else {
                local $ASF::Value::Offline = 1 if $quick == 3;
                my $s = view->can($method) or die "Can't locate method: $method\n";
                my (undef, $ext, $vars) = $s->(path => $file, %$args);
                $file = "$filename.$ext" eq "index.html" ? $dirname : "$dirname$filename.$ext";
                $data->{$file} = $vars;
            }
            last;
        }
        $data->{$file}->{headers}->{title} //= ucfirst $filename;
    }
}

# presumes the dependencies are all markdown files with subheadings of the form
## foo ## {#bar} or
## foo ## [#bar]
# useful for generating index.html pages as well given a suitably restricted set of dependencies
# pass a true 'nest' arg to nest links
# ditto for 'preprocess' arg to preprocess content with a Template() pass
# takes a 'deps' hashref to override deps fetching

sub sitemap {
    my %args = @_;
    my $template = "content$args{path}";
    $args{breadcrumbs} = view->can("breadcrumbs")->($args{path});
    view->can("fetch_deps")->($args{path} => $args{deps} = {}, $args{quick_deps})
        unless exists $args{deps};

    my $content = "";

    my $pre_title = $args{headers}->{title};
    if ($pre_title eq "Index" and $args{path} =~ m!/index\.html$!) {
	my ($filename, $dirname) = parse_filename($args{path});
	$args{headers}->{title} .= " of "
	    . File::Basename::basename($dirname) . "/";
    }

    for (sort keys %{$args{deps}}) {
        my $title = $args{deps}->{$_}->{headers}->{title};
        if ($title eq "Index" and m!/$!) {
            my ($filename, $dirname) = parse_filename;
            $title .= " of " . File::Basename::basename($dirname) . "/";
        }
        $content .= "- [$title]($_)\n";
        for my $hdr (grep /^#/, split /\s*\n/, $args{deps}->{$_}->{content} // "") {
            # this regexp supports 'elementid' and 'attr_list' markdown extensions
            $hdr =~ /^(#+)\s+([^#]+)?\s+\1?\s+[{\[](?:\:\s+)*(?:\.\S+\s+|\w+=\S+\s+)*#([^}\s]+)(?:\s+\.\S+|\s+\w+=\S+)*\s*[}\]]$/ or next;
            my $level = length $1;
            $level *= 4;
            $content .= " " x $level;
            $content .= "- [$2]($_#$3)\n";
        }
    }

    if ($args{nest}) {
        1 while $content =~ s{^(\s*-\s)                    # \1, prefix
                  (                                        # \2, link
                      \[ [^\]]+ \]
                      \(
                      (  [^\)]* / )                        # \3, (dir with trailing slash)
                      \)
                  )
                  (                                        # \4, subpaths
                      (?:\n\1\[ [^\]]+ \]\( \3 [^\#?] .*)+
                  )
             }{
                 my ($prefix, $link, $subpaths) = ($1, $2, $4);
                 $subpaths =~ s/\n/\n    /g;
                 "$prefix$link$subpaths"
             }xme;
    }

    $args{content} = $args{preprocess} ? Template($content)->render(\%args) : $content;
    # the extra (3rd) return value is for sitemap support
    return Template($template)->render(\%args), html => \%args;
}

# internal utility sub for the wrapper views that follow (not overrideable)

sub next_view {
    my $args = pop;
    $args->{view} = [@{$args->{view}}] if ref $args->{view}; # copy it since we're changing it
    return ref $args->{view} ? shift @{$args->{view}} : delete $args->{view};
}

# wrapper view for creating final content (eg sitemaps) that doesn't require being online
# to service relevant content generation in dependencies, etc.

sub offline {
    local $ASF::Value::Offline = 1;
    my %args = @_;
    my $view = next_view \%args;
    return view->can($view)->(%args);
}

# see top of www.apache.org site for how this works in practice (drops filename,
# just provides dirs).  overridable internal sub

sub breadcrumbs {
    my @path = split m!/!, shift;
    pop @path;
    my @rv;
    my $relpath = "";
    for (@path) {
        $relpath .= "$_/";
        $_ ||= "Home";
        push @rv, qq(<a href="$relpath">\u$_</a>);
    }
    return join "&nbsp;&raquo&nbsp;", @rv;
}

# Extensive use of the memoize() wrapper view probably necessitates adding
#
# our $runners = 1;
#
# in lib/path.pm to get the full benefit of the cache. That will ensure that site builds are
# processed by the same child perl process.  Use of this feature is a trial and error balancing
# process of performance behavior because by default 8 child 'runners' will process the site
# build in parallel, and reducing that number will tend to counteract the performance gains of
# caching built pages in sites with complex dependencies.

{
    my %cache;

    sub memoize {
        my %args = @_;
        my $view = next_view \%args;
        my $file = "content$args{path}";
        return @{$cache{$file}} if exists $cache{$file};

        return view->can($view)->(%args) if $ASF::Value::Offline; # don't cache offline pages

        $cache{$file} = [ view->can($view)->(%args) ];
        return @{$cache{$file}};
    }
}

# wrapper view for pulling snippets out of code repos; see thrift site sources for sample usage
# 'snippet_footer' and 'snippet_header' args are supported.

sub snippet {
    my %args = @_;
    my $file = "content$args{path}";
    read_text_file $file, \%args unless exists $args{headers} and exists $args{content};
    my $key = "snippetA";
    $args{content} =~ s{\[snippet:([^\]]+)\]} # format is [snippet:arg1=val1:arg2=val2:...]
                       {
                           my $argspec = $1;
                           my %a = (%args, map {split /=/, $_, 2} split /:/, $argspec);
                           require ASF::Value::Snippet; # see source for list of valid args
                           $args{$key} = ASF::Value::Snippet->new(%a);
                           my $filter = exists $a{lang} ?  "markdown" : "safe";
                           my $rv = "{{ $key.fetch|$filter }}";
                           if (defined(my $header = $args{snippet_header})) {
                               $header =~ s/\$snippet\b/$key/g;
                               $rv = "$header\n$rv";
                           }
                           if (defined(my $footer = $args{snippet_footer})) {
                               $footer =~ s/\$snippet\b/$key/g;
                               $rv .= "\n$footer";
                           }
                           ++$key;
                           $rv;
                       }ge;


    my $view = next_view \%args;
    return view->can($view)->(%args, preprocess => 1);
}

# wrapper view for rebuilding content and headers from content created in a prior wrapper
# will reread 'content' argument for any headers after a template pass (assuming 'preprocess'
# arg is set to enable that)

sub reconstruct {
    my %args = @_;
    die "Can't reconstruct from existing content" unless exists $args{content};
    read_text_file \( $args{preprocess}
                          ? Template($args{content})->render(\%args)
                          : $args{content},
                      %args );
    my $view = next_view \%args;
    delete $args{preprocess}; # avoid duplication of template processing
    view->can($view)->(%args);
}

# wrapper which drops file extensions from local links in markdown and html content.
# The reason this is a good thing is that all of the relevant httpd servers have MultiViews
# setup to dispatch to the correct file on the server's filesystem, so removing extensions
# (and trailing slashes) as a policy matter for links is wise.

sub trim_local_links {
    my %args = @_;
    my $view = next_view \%args;
    read_text_file "content$args{path}", \%args unless exists $args{content};

    no warnings 'uninitialized';
    $args{content} =~ s/                 # trim markdown links
                           \[
                           ( [^\]]+ )
                           \]
                           \(
                           ( (?!:http)[^\)#?]* ) (?:\.\w+|\/) ([#?][^\)#?]+)?
                           \)
                       /[$1]($2$3)/gx;

    $args{content} =~ s/                 # trim html links
                           href=(['"])
                           ( (?!:http)[^'"?#]* ) (?:\.\w+|\/) ([#?][^'"#?]+)?
                           \1
                       /href=$1$2$3$1/gx;

    return view->can($view)->(%args);
}

1;

=head1 LICENSE

           Licensed to the Apache Software Foundation (ASF) under one
           or more contributor license agreements.  See the NOTICE file
           distributed with this work for additional information
           regarding copyright ownership.  The ASF licenses this file
           to you under the Apache License, Version 2.0 (the
           "License"); you may not use this file except in compliance
           with the License.  You may obtain a copy of the License at

             http://www.apache.org/licenses/LICENSE-2.0

           Unless required by applicable law or agreed to in writing,
           software distributed under the License is distributed on an
           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
           KIND, either express or implied.  See the License for the
           specific language governing permissions and limitations
           under the License.

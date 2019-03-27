package ASF::CMS;
use strict;
use warnings;

#  This package will be the hub for the gui.
#
#  Basic Functionality:
#
#  * helper redirection code (fob off or reuse a wc and redirect to it)
#  * publishing sites via svnmucc
#
#  * working copy management (including anonymous wc's)
#    * fob off a new wc for a project
#    * directory navigation
#    * select edits, attachments, and file uploads
#    * commits, updates, diffs, etc. what about conflict resolution?
#
#  URL Layout:
#
#  / - list of projects
#  /redirect?uri=$blah - redirect to wc asset underlying the given url
#  /$proj/ - per-project options
#  /$proj/setup - initiate a new project
#  /$proj/teardown - nuke a project
#  /$proj/publish - url to publish a project's website (GET vs. POST)
#
#  /$proj/wc/$action/$wcname/$subpath - per-wc resource
#       $action:
#          browse - view content from a tree-walker perspective
#          edit - make content available for editing
#             * attach - manage attachments
#          add - add new files / dirs
#          move - move files / dirs
#          delete - delete files / dirs
#          diff
#          update
#          commit
#          static - static (default) file serving
#
#
#  Data Structures:
#
#   (on the surface it doesn't appear that we will need too many passed objects,
#    just need to collect+pass variables to the template in the main handler)
#
#   the url processing should be done in the main handler, so $action and
#   $wcname/$subpath will be object attributes

use Apache2::ServerRec;
use Apache2::ServerUtil;
use Apache2::RequestRec;
use Apache2::RequestIO;
use Apache2::Response;
use Apache2::Access;
use Apache2::SubRequest;
use Apache2::HookRun;
use Apache2::Log;
use Apache2::Const -compile => qw/:common :http :log :methods/;
use Apache2::URI;
use APR::Request::Apache2;
use APR::Table;
use APR::Const -compile => qw/SUCCESS/;
use SVN::Client; # swig impl. supports ithreads- probably fine given our use-case
use URI; # chiefly for variable annotation
use URI::Escape;
use File::Temp qw/tempdir tempfile/;
use File::Path qw/rmtree/;
use File::Basename;
use Cwd (); # we take a process file lock here to maintain thread safety, which should suffice
use Scalar::Util qw/blessed/; # core module *should* be thread-safe
use JSON::XS (); # not thread-safe (probably should avoid this module entirely and use the pure-perl version)
use LWP::UserAgent; # we take process locks here due to underlying SSLeay calls
use Dotiac::DTL qw/Template *TEMPLATE_DIRS/;
use Net::LDAP; # not thread-safe due to underlying SSLeay stuff (we take a process lock)
use ASF::CMS::Cookie;
use ASF::CMS::WC;
use ASF::Util qw/touch get_lock read_text_file normalize_svn_path parse_filename/;

my $svr_cfg = Apache2::ServerUtil->server->dir_config;

our $DOMAIN   = $svr_cfg->{DOMAIN};
our $BASE_DIR = $svr_cfg->{BASE_DIR};           # install path
our $STAGE_DIR= $svr_cfg->{STAGE_DIR};          # staged builds base path
our $SVN_URL  = $svr_cfg->{SVN_URL};
our $PROJ_PAT = qr/$svr_cfg->{PROJ_PAT}/;       # restrict proj names to lc
our $VERSION  = '1.12';                         # Public API version of the CMS
our $NO_COMMIT= $svr_cfg->{NO_COMMIT};          # trigger file globally disables commit

our $LDAP_SERVER= $svr_cfg->{LDAP_SERVER};      # LDAP config vars only used
our $LDAP_CAFILE= $svr_cfg->{LDAP_CAFILE};      # by ASF::CMS::WC::Mail- feel
our $LDAP_BASE= $svr_cfg->{LDAP_BASE};          # free to ignore these if you don't care about that.

our $URIc     = '^:/?=&;#A-Za-z0-9.~_-';        # complement of class of characters to uri_escape
                                                # note: we deftly avoid native treatment of utf8
my $REALM     = $svr_cfg->{REALM};              # server's auth realm
my $HOME      = $svr_cfg->{HOME};               # server's home env
my $LANG      = $svr_cfg->{LANG};               # utf8 locale
my $PATH      = $svr_cfg->{PATH};               # svn, tar, zfs, sudo, svnmucc
my $EXTERNALS = $svr_cfg->{EXTERNALS};          # filename of list of external dirs to preserve at publish time

(our $ZROOT, my $WC_MOUNT) = map +(split /[\t\n]/),
    run_shell_command(zfs => [qw/list -Ho/, "name,mountpoint"], "$BASE_DIR/wc")
    or die "Can't find zfs volume mounted on $BASE_DIR/wc\n";
die "$ZROOT not mounted on $BASE_DIR/wc\n" unless $WC_MOUNT eq "$BASE_DIR/wc";

# Needed for Dotiac::DTL::Addon::markup::markdown()
$ENV{MARKDOWN_SOCKET} = "$BASE_DIR/logs/markdown.socket";

$ENV{HOME} = $HOME;
$ENV{LANG} = $LANG;
$ENV{PATH} = $PATH;

sub setup;
sub teardown;
sub export;
sub import;
sub compat;
sub redirect;
sub create_working_copy;
sub publish;
sub lazy_publish;
sub home;
sub error;
sub bad_request;
sub forbidden;
sub conflict;
sub render;
sub get_svn_client;
sub get_file_info;
sub process_as_dir;
sub separate_content;
sub join_content;
sub run_shell_command;
sub negotiate_file;
sub send_options_or_not_allowed;
sub client_wants_json;
sub fixup_code;
sub breadcrumbs;
sub dev_list;

sub handler {
    my $r              = shift;
    my $uri            = $r->uri;
    my $request_method = $r->method;
    my $user           = $r->user || "";

    local $@; # we trap all of our own errors here

    my $status = eval {

        return bad_request $r, "Invalid HTTP method $request_method"
            if $r->method_number == Apache2::Const::M_INVALID;

        if ($uri =~ m!^/($PROJ_PAT)/wc/(\w+)(/\Q$user\E-.+)$!) {
            my $project = $1;
            my $action  = $2;
            my $wc_path = $3;
            my $apreq   = APR::Request::Apache2->handle($r);
            my $args    = $apreq->args;

            if ($args and $args->{action}) {
                no warnings 'uninitialized';
                my ($action, $path) = @$args{qw/action path/};

                $action =~ /^\w+$/
                    or return bad_request $r, "Invalid action: $action";

                my $loc = URI->new("/$project/wc/$action$wc_path$path")
                    or return bad_request $r, "Invalid path: $path";

                $r = $r->main unless $r->is_initial_req;
                $r->headers_out->set(Location => $r->construct_url($loc));
                return Apache2::Const::REDIRECT;
            }

            $wc_path =~ m!/\.svn/! and return Apache2::Const::FORBIDDEN;

            if ($r->filename =~ m!/$! and not $r->path_info
                    and $wc_path !~ m!/$!) {

                $r = $r->main unless $r->is_initial_req;
                $r->headers_out->set(Location =>
                                     $r->construct_url(URI->new("$uri/")));
                return Apache2::Const::REDIRECT;
            }

            my $wc = eval { ASF::CMS::WC->delegate(
                project => $project,
                action  => $action,
                wc_path => $wc_path,
            )};
            if ($@) {
                if ($@ =~ /^Can't locate /) {
                    return render $r, "error.html",
                            error => "Invalid action: $action",
                            title => "Not Found",
                              dir => 0,
                      http_status => Apache2::Const::HTTP_NOT_FOUND;
                }
                die "Internal error [load failed]:\n$@";
            }

            # avoid symbol autovivification inherent in method lookups via can()
            my $stash  = do {no strict 'refs'; *{ref($wc) . "::"}};
            my $symbol = $stash->{lc $request_method};

            $symbol and my $method = *{$symbol}{CODE}
                or return send_options_or_not_allowed $r,
                    map uc, grep $_ eq lc && *{$stash->{$_}}{CODE},
                        keys %$stash;

            return $method->($wc, $r);
        }
        elsif ($uri =~ m!^/($PROJ_PAT)/wc$!) {
            $r = $r->main unless $r->is_initial_req;
            $r->headers_out->set(Location => $r->construct_url("/$1/wc/"));
            return Apache2::Const::REDIRECT;
        }
        elsif ($uri =~ m!^/($PROJ_PAT)/wc/!) {
            return Apache2::Const::FORBIDDEN;
        }
        elsif ($uri =~ m!^/($PROJ_PAT)/$!) {
            my @methods = qw/GET HEAD/;

            return send_options_or_not_allowed $r, @methods
                unless grep $request_method eq $_, @methods;

            return render $r, "home.html", projects => [$1]
                if -d "$BASE_DIR/wc/$1";

            return Apache2::Const::DECLINED;
        }
        elsif ($uri =~ m!^/(redirect|import|export|compat)$!) {
            my @methods = qw/GET HEAD POST/;

            return send_options_or_not_allowed $r, @methods
                unless grep $request_method eq $_, @methods;

            return __PACKAGE__->can($1)->($r);
        }
        elsif ($uri =~ m!^/($PROJ_PAT)/(publish|lazy_publish|setup|teardown)$!) {
            my @methods = qw/GET HEAD POST/;

            return send_options_or_not_allowed $r, @methods
                unless grep $request_method eq $_, @methods;

            return __PACKAGE__->can($2)->($r, $1);
        }
        elsif ($uri eq "/") {
            my @methods = qw/GET HEAD/;

            return send_options_or_not_allowed $r, @methods
                unless grep $request_method eq $_, @methods;

            return home $r;
        }
        else {
            return Apache2::Const::DECLINED;
        }
    };
    if ($@) {
        my $error = $@;
        $status   = eval {error $r, $error};
        $status   = Apache2::Const::SERVER_ERROR if $@;
    }
    return $status;
}

sub setup {
    my ($r, $project) = @_;
    my $apreq         = APR::Request::Apache2->handle($r);
    my $uri           = $apreq->body("uri")
        or return render $r, "setup_get.html", project => $project;

    # If this is not https, WebGUI commits will fail.
    $uri =~ m!https://!
        or return bad_request $r, "URI must use https://";

    $project eq lc $project
        or return bad_request $r, "specified project must be all lower-case";

    my $wc_base = "$BASE_DIR/wc/$project";
    -d $wc_base and return conflict $r, "Project $project already exists!";

    eval { ASF::CMS::WC->delegate(action => $project) };
    $@ or return bad_request $r, "Project name cannot be an existing wc action";

    normalize_svn_path $uri;
    $uri =~ /(.*)/;
    $uri = URI->new(uri_escape $1, $URIc) or return bad_request $r, "Invalid uri param: $1";

    my $svn = get_svn_client $r;

    $svn->log_msg(sub { ${$_[0]} = "Setup $project for CMS use" });

    run_shell_command zfs => [qw/create/], "$ZROOT/$project";
    run_shell_command zfs => [qw/create/], "$ZROOT/$project/original";
    run_shell_command sudo => [qw/zfs mount -a/];

    eval {
        $svn->mkdir3("$SVN_URL/staging/$project/trunk", 1, undef);
    };
    eval {
        $svn->mkdir3("$SVN_URL/production/$project/content", 1, undef);
    };

    my $revision = $svn->checkout("$uri", "$wc_base/original", "HEAD", 1);
    if ($apreq->body("list")) {
        open my $fh, ">", "$wc_base/dev_list" or die "Can't open $wc_base/dev_list: $!\n";
        print $fh $apreq->body("list"), "\n";
    }
    return render $r, "setup_post.html",
          project => $project,
         revision => $revision;
}

sub teardown {
    my ($r, $project) = @_;
    my $apreq         = APR::Request::Apache2->handle($r);

    $apreq->body("submit")
        or return render $r, "teardown_get.html", project => $project;

    my $wc_base = "$BASE_DIR/wc/$project";
    my $zfs = run_shell_command sudo => [qw/zfs destroy -R/], "$ZROOT/$project";
    die $zfs if $?;
    rmtree $wc_base;

    return render $r, "teardown_post.html", project => $project;
}

sub export {
    my $r = shift;
    my %h;

    for (glob "$BASE_DIR/wc/*/original") {
        my ($url) = grep s/^URL:\s+//, run_shell_command svn => [qw/info/], $_;
        chomp $url;
        my ($project) = m!/($PROJ_PAT)/original$!
            or die "$_ contains bad project name: doesn't match /$PROJ_PAT/\n";
        my $list;
        if (-f "$BASE_DIR/wc/$project/dev_list") {
            $list = dev_list $_;
        }
        $h{$project} = { url => $url, $list ? (list => $list) : () };
    }

    $r->content_type("application/json");
    $r->main->content_type("application/json")
        unless $r->is_initial_req;
    my $lock = get_lock "$BASE_DIR/locks/json-xs-$$";
    $r->print(JSON::XS->new->utf8->pretty->encode(\%h));
    return Apache2::Const::OK;
}

sub import {
    my $r      = shift;
    return unless $r->isa("Apache2::RequestRec"); # import gets called on use()

    my $svn    = get_svn_client $r;
    my $apreq  = APR::Request::Apache2->handle($r);
    my $file   = $apreq->upload("file");
    my $uri    = $apreq->body  ("uri");

    return render $r, "import_get.html" unless $file or $uri;

    my $json;
    if ($file) {
        $file->upload_slurp($json);
    }
    else {
        $uri   = URI->new($uri) or return bad_request $r, "Bad uri param\n";
        my $ua = LWP::UserAgent->new;
        $ua->credentials($uri->host_port, $REALM, $r->user,
                         ($r->get_basic_auth_pw)[1]);
        my $lock = get_lock "$BASE_DIR/locks/ssleay-$$";
        my $response = $ua->get($uri);
        my $status   = $response->status_line;
        $response->is_success or die "Bad LWP response: $status\n";
        $response->header("Content-Type") eq "application/json"
            or die "Content-Type not application/json!\n";

        $json = $response->decoded_content . substr $uri, 0, 0; # taint this
    }

    my $lock     = get_lock "$BASE_DIR/locks/json-xs-$$";
    my $data     = JSON::XS->new->utf8->decode($json);
    undef $lock;

    my $imported = 0;
    my $failed   = 0;
    my $total    = keys %$data;

    $r->content_type('text/plain; charset="utf-8"');
    $r->main->content_type($r->content_type)
            unless $r->is_initial_req;

    while (my ($project, $hash) = each %$data) {
        $project =~ /^($PROJ_PAT)$/ or do {
            $r->print("[FAIL] $project doesn't match $PROJ_PAT\n");
            $r->rflush;
            ++$failed;
            next;
        };
        $project = $1; # not strictly necessary since keys are never tainted

        local $_ = "$BASE_DIR/wc/$project";

        if (-d) {
            $r->print("[SKIP] $project already exists, skipping.\n");
            $r->rflush;
            next;
        }

        run_shell_command zfs => [qw/create/], "$ZROOT/$project";
        run_shell_command zfs => [qw/create/], "$ZROOT/$project/original";
        run_shell_command sudo => [qw/zfs mount -a/];

        if ($hash->{list}) {
            open my $fh, ">", "$BASE_DIR/wc/$project/dev_list";
            print $fh $hash->{list}, "\n";
        }

        my $url = $hash->{url};
        $url =~ /(.*)/;
        $url = $1;

        eval { $svn->checkout($url, "$_/original", "HEAD", 1) };
        if ($@) {
            $r->print("[FAIL] $project original checkout failed, nuking: $@");
            run_shell_command sudo => [qw/zfs destroy -R/], "$ZROOT/$project";
            rmtree $_;
            $r->rflush;
            ++$failed;
            next;
        }
        $r->print("[OK] $project successfully imported.\n");
        $r->rflush;
        ++$imported;
    }
    $r->print("[NOTE] imported $imported projects ($failed failed)"
                  . " out of $total total.\n");
    return Apache2::Const::OK;
}

sub compat {
    my $r       = shift;
    my $apreq   = APR::Request::Apache2->handle($r);
    my $version = $apreq->param("version")
        or return bad_request $r, "No version param provided!";

    my ($server_major, $server_minor) = split /[.]/, $VERSION;
    my ($client_major, $client_minor) = split /[.]/, $version;

    my $compatible = $server_major == $client_major
                  && $server_minor >= $client_minor
                      or do {
                          $r->status(Apache2::Const::HTTP_CONFLICT);
                          $r->main->status(Apache2::Const::HTTP_CONFLICT)
                              unless $r->is_initial_req;
                      };

    my %h = (
       http_status => $r->is_initial_req ? $r->status : $r->main->status,
        version    => $VERSION,
        compatible => $compatible || 0,
    );
    $r->content_type("application/json");
    $r->main->content_type("application/json")
        unless $r->is_initial_req;
    my $lock = get_lock "$BASE_DIR/locks/json-xs-$$";
    $r->print(JSON::XS->new->utf8->pretty->encode(\%h));
    return Apache2::Const::OK;
}

sub redirect {
    my $r     = shift;
    my $apreq = APR::Request::Apache2->handle($r);
    my $user  = $r->user;

    my $original_uri = $apreq->param("uri")
        or return bad_request $r, "uri param not found";

    $original_uri =~ s!/[^/]*openoffice\.org/!/ooo-site.apache.org/!i;
    $original_uri =~ s/\Qopenejb.apache.org/tomee.apache.org/i;
    $original_uri =~ s/\Qlucenenet.apache.org/lucene.net.apache.org/i;

    $_ = URI->new($_) for $original_uri;
    $original_uri or return bad_request $r, "Invalid uri param";

    my ($project) = eval { lc($original_uri->host) =~
                               /^($PROJ_PAT)?(?:\.incubator)?(?:\.(?:staging|eu|us))?\.?\Q$DOMAIN\E$/ }
        or return bad_request $r, "Bad host in uri: $original_uri";

    $project = "www" if not defined $project;

    my $path = $original_uri->path || "/";
    Apache2::URI::unescape_url($path);

    # A podling that precedes podling.incubator.a.o days
    if ($project eq "incubator") {
        if ($path =~ m!^/($PROJ_PAT)/! and -d "$BASE_DIR/wc/$1") {
            $project = $1;
        }
    }

    if ($project eq "httpd") {
        if ($path =~ s!^/docs/([^/]+)!! and -d "$BASE_DIR/wc/httpd-docs-$1") {
            $project = "httpd-docs-$1";
        }
    }

    my $wc_base = "$BASE_DIR/wc/$project";
    my $wc_path;
    my ($new) = map /^([\w.-]+)$/ && $1, $apreq->args("new");

    die "CMS users can't clone themselves!\n"
        if $new and $new eq $user;

    -d $wc_base or return Apache2::Const::NOT_FOUND;

 LOOK_FOR_PATH:
    my $lock = get_lock "$BASE_DIR/locks/$project-wc-$user";

    unless ($new) {
        if (my %avail_wc = map {$_ => 1} glob "$wc_base/$user-*") {
            if (my $jar = $apreq->jar) {
                $jar->cookie_class("ASF::CMS::Cookie");

                for my $c (map $_->thaw, $jar->get("$project/wc")) {
                    if ($avail_wc{"$wc_base/$c->{path}"}) {
                        $wc_path = "$wc_base/$c->{path}";
                        touch $wc_path;
                        last;
                    }
                }
            }
            if (keys %avail_wc == 1 and not $wc_path) {
                $wc_path = each %avail_wc;
                touch $wc_path;
            }
        }
    }

    unless ($wc_path) {
        for (map /(.*)/ && $1, glob "$wc_base/$user-*") {
            my $basename = basename $_;
            run_shell_command sudo => [qw/zfs destroy -Rf/], "$ZROOT/$project/$basename";
            run_shell_command sudo => [qw/zfs destroy/], "$ZROOT/$project/original\@$basename";
            rmtree $_;
        }
        $wc_path = "$BASE_DIR/wc/$project/original";
    }

    $path .= "index" if $path eq "/" and -e "$wc_path/trunk/pom.xml"; # maven special case for /
    $path = "trunk/content$path";

    unless (-e "$wc_path/$path") {
        my ($filename, $dirname) = parse_filename "$wc_path/$path";
        my $glob_path = $dirname . $filename;
        my $ok = 0;
        for ($glob_path) {
            my @files = grep -f, glob "$_.*";
            if (@files > 1) {
                @files = negotiate_file $r, $_, "$wc_path/$path" if @files > 1;
            }
            elsif (@files == 0) {
                # maven /content/*/ support
                s!/content/!/content/*/!;
                $_ .= "index" if m!/$!;
                @files = grep -f, glob "$_.*" or last;
            }
            s!$wc_path/!! for $path = shift @files;
            $ok = 1;
        }
        if (not $ok) {
            if ($path =~ s!^trunk/content/($PROJ_PAT)/!/! and -d "$BASE_DIR/wc/$project-$1") {
                $wc_base = "$BASE_DIR/wc/$project-$1";
                $wc_path = undef;
                $project .= "-$1";
                goto LOOK_FOR_PATH;
            }
            $path = "trunk/";
        }
    }

    if ($wc_path eq "$BASE_DIR/wc/$project/original") {
        $wc_path = create_working_copy $project, $user, $new;
    }

    # ok we have a working copy to use, now we need to create
    # the location and set-cookie headers

    my ($wc_dir) = $wc_path =~ m!/wc/$project/(\Q$user\E-[^/]+)$!;
    my $new_cookie = ASF::CMS::Cookie->new($r,
              name => "$project/wc",
             value => { path => $wc_dir },
    );
    $new_cookie->bake($r);

    my $action = $apreq->param("action") || "browse";
    $r = $r->main unless $r->is_initial_req;
    $r->headers_out->set(
        "Location",
        $r->construct_url(URI->new(uri_escape "/$project/wc/$action/$wc_dir/$path", $URIc))
    );

    return Apache2::Const::REDIRECT;
}

sub create_working_copy {
    my ($project, $user, $clone) = @_;
    my $wc_base = "$BASE_DIR/wc/$project";
    -d "$wc_base/original" or die "Can't find $wc_base/original\n";
    my $lock;
    my $target;
    my $wc_dir;
    if ($clone and $clone ne "1") {
        $clone =~ m!^([\w.-]+)$! or die "Invalid clone name: $clone";
        $clone = $1;
        ($target) = grep {/(.*)/ && touch $1; s!\Q$BASE_DIR/wc/$project/!!}
            glob "$BASE_DIR/wc/$project/$clone-*"
                or die "Can't locate ${clone}'s tree to clone\n";
        $lock = get_lock "$BASE_DIR/locks/$project-wc-$clone";
        $wc_dir = tempdir "$user-$clone-XXXXXX", DIR => $wc_base;
    }
    else {
        $target = "original";
        $lock = get_lock "$BASE_DIR/locks/$project-wc-original";
        $wc_dir = tempdir "$user-XXXXXX", DIR => $wc_base;
    }
    my $wc_basename = basename $wc_dir;
    run_shell_command zfs => [qw/snapshot/], "$ZROOT/$project/$target\@$wc_basename";
    run_shell_command zfs => [qw/clone/], "$ZROOT/$project/$target\@$wc_basename", "$ZROOT/$project/$wc_basename";
    run_shell_command sudo => [qw/zfs mount/], "$ZROOT/$project/$wc_basename";
    touch $wc_dir;
    return $wc_dir;
}

sub publish {
    my ($r, $project) = @_;
    my $apreq         = APR::Request::Apache2->handle($r);
    my $args          = $apreq->args;
    my $body          = $apreq->body;
    my $jar           = $apreq->jar;
    my $path          = "$BASE_DIR/wc/$project";
    my $user          = $r->user;

    -d $path or return Apache2::Const::NOT_FOUND;

    my $key; # name is a relic from original implementation: aka staging tree's revision number
    if ($body) {
        ($key) = map defined && /^(\d+)$/ && $1, $body->{key};
        unless ($key) {
            return bad_request $r, "Invalid or missing key param"
                unless $jar;

            for ($jar->get("$project/publish")) {
                next unless /^(\d+)$/;
                $key = $1;
                last;
            }
            return bad_request $r,
                "Missing key param or valid '$project/publish' cookie"
                    unless $key;
        }
    }

    unless ($body and $key) {
        my $sdir = "$SVN_URL/staging/$project/trunk";
        my $pdir = "$SVN_URL/production/$project";
        ($key) = map /^Revision: (\d+)/, run_shell_command svn => [qw/info/], $sdir;
        my $diff;
        if ($args->{diff}) {
            $diff = run_shell_command svn => [qw/diff/], $pdir, "$sdir\@$key";
            fixup_code $pdir, !client_wants_json($r) && "diff", $diff;
        }
        # set a client-readable session cookie for the convenience of apps
        my $new_cookie = APR::Request::Cookie->new(
            ($r->is_initial_req ? $r->pool : $r->main->pool),
                  name => "$project/publish",
                 value => $key,
        );
        $new_cookie->ASF::CMS::Cookie::bake($r);

        my $referer = $r->headers_in->get("Referer") || "";
        undef $referer unless $referer =~ m!/$project/wc/\w+/\Q$user\E-.+!;
        if ($referer) {
            my $new_cookie = APR::Request::Cookie->new(
                ($r->is_initial_req ? $r->pool : $r->main->pool),
                    name => "$project/referer",
                    value => $referer,
            );
            $new_cookie->ASF::CMS::Cookie::bake($r);
        }
        else {
            $referer = $jar->get("$project/referer") if $jar;
        }

        my ($source_url) = map /^URL: (.*)/, run_shell_command svn => [qw/info/], "$path/original";
        my ($from_revision) = map /^(\d+)/, run_shell_command svn => [qw/propget --strict cms:source-revision/], "$sdir/content\@$key";
        my ($to_revision) = map /^(\d+)/, run_shell_command svn => [qw/propget --strict cms:source-revision/], "$pdir/content\@$key";
        $from_revision += 1;
        my (@authors) = map /[|] (.*?) [|]/, run_shell_command svn => [qw/log/,
                '-q', "-r", "$from_revision:$to_revision",
            ],
            $source_url
            unless $from_revision > $to_revision;
        return render $r, "publish_get.html",
              project => $project,
                 diff => $diff,
                error => $body && "Stale publication key detected,"
                                   . " please try again",
              message => $body && $body->{message},
                  key => $key,
              referer => $referer,
           source_url => $source_url,
              authors => (join ", ", @authors),
               nojson => [qw/authors/],
($body ? (http_status => Apache2::Const::HTTP_NOT_FOUND) : ());

    }

    die "Commits administratively disabled\n" if -f $NO_COMMIT;

    my $message = $body->{message};
    if ($message) {
        $message =~ s/\r//g;
        $message =~ /(.*)/s;
        $message = $1;
    }
    else {
        $message = "Publishing svnmucc operation to $project site by $user";
    }

    my @externals; # can't pull these from the filesystem since it's not stable
    my $ua = LWP::UserAgent->new;
    my $lock = get_lock "$BASE_DIR/locks/ssleay-$$";
    my $response = $ua->get("$SVN_URL/staging/$project/trunk/content/$EXTERNALS");
    undef $lock;

    if ($response->is_success) {
        @externals = map "production/$project/content/$_",
            grep /^\S/ && !/^#/, split /\n/, $response->decoded_content;
    }
    elsif ($response->status_line !~ /^404/) {
        die "Can't resolve $EXTERNALS: ", $response->status_line, "\n";
    }

    my ($lfh, $lfile) = tempfile "$user-log-XXXXXX", DIR => "$BASE_DIR/tmp";
    print $lfh $message;
    close $lfh;

    my @cgiops;
    run_shell_command svn => [qw/ls/], "$SVN_URL/production/$project/cgi-bin";
    push @cgiops, "rm", "production/$project/cgi-bin" unless $?;
    run_shell_command svn => [qw/ls/], "$SVN_URL/staging/$project/trunk/cgi-bin\@$key";
    push @cgiops, "cp", $key, "staging/$project/trunk/cgi-bin", "production/$project/cgi-bin" unless $?;

    my ($xfh, $xfile) = tempfile "$user-x-XXXXXX", DIR => "$BASE_DIR/tmp";
    print $xfh "$_\n" for
        "rm", "production/$project/content",
        "cp", $key, "staging/$project/trunk/content", "production/$project/content",
         @cgiops,
         map +("cp", "HEAD", ($_) x 2), @externals;
    close $xfh;

    my $svnmucc = run_shell_command svnmucc => [
        '--non-interactive', #'--no-auth-cache',
        '-F', $lfile,
        '-U', $SVN_URL,
        "-p", ($r->get_basic_auth_pw)[1],
        "-u", $user,
        "-X", $xfile,
    ];

    unlink $lfile, $xfile;
    die $svnmucc if $?;

    my ($revision) = ($svnmucc =~ /^r(\d+) committed/);

    # clear cookie cache
    for (qw/referer publish/) {
        my $new_cookie = APR::Request::Cookie->new(
            ($r->is_initial_req ? $r->pool : $r->main->pool),
                  name => "$project/$_",
                 value => 0,
               expires => "now",
        );
        $new_cookie->ASF::CMS::Cookie::bake($r);
    }

    return render $r, "publish_post.html",
          project => $project,
         revision => $revision,
          referer => $body->{referer};
}

sub lazy_publish {
    my ($r, $project) = @_;
    my $apreq         = APR::Request::Apache2->handle($r);
    my $body          = $apreq->body;
    my $jar           = $apreq->jar;
    my $path          = "$BASE_DIR/wc/$project";
    my $user          = $r->user;
    my $password      = ($r->get_basic_auth_pw)[1];

    -d $path or return Apache2::Const::NOT_FOUND;

    my @cgiops;
    run_shell_command svn => [qw/ls/], "$SVN_URL/production/$project/cgi-bin";
    push @cgiops, "rm", "production/$project/cgi-bin" unless $?;
    run_shell_command svn => [qw/ls/], "$SVN_URL/staging/$project/trunk/cgi-bin";
    push @cgiops, "cp", "HEAD", "staging/$project/trunk/cgi-bin", "production/$project/cgi-bin" unless $?;

    if ($body && $body->{submit}) {

        die "Commits administratively disabled\n" if -f $NO_COMMIT;

        my $svnmucc = run_shell_command svnmucc => [
            '--non-interactive', #'--no-auth-cache',
            '-U', $SVN_URL,
            '-u', $user,
            '-p', $password,
            '-m', "Lazy publish $project",
        ],
            "rm", "production/$project/content",
            "cp", "HEAD", "staging/$project/trunk/content", "production/$project/content",
            @cgiops;

        die $svnmucc if $?;
        my ($revision) = ($svnmucc =~ /^r(\d+) committed/);

        return render $r, "lazy_publish_post.html",
              project => $project,
             revision => $revision,
              referer => $body->{referer};
    }
    my $referer = $r->headers_in->get("Referer") || "";
    undef $referer unless $referer =~ m!/$project/wc/\w+/\Q$user\E-.+!;
    return render $r, "lazy_publish_get.html",
          project => $project,
          referer => $referer,
}

sub home {
    my $r        = shift;
    my @projects = sort grep s!\Q$BASE_DIR/wc/!!, glob "$BASE_DIR/wc/*";
    return render $r, "home.html", projects => \@projects;
}

sub error {
    my ($r, $error) = @_;
    if (blessed $error and $error->isa("APR::Request::Error")) {
        return bad_request $r, "Request parsing error: $error";
    }
    $r->log_rerror(Apache2::Log::LOG_MARK, Apache2::Const::LOG_ERR,
                   APR::Const::SUCCESS, $error);
    return render $r, "error.html",
            error => "$error",
              dir => 0,
            title => "Error",
      http_status => Apache2::Const::HTTP_INTERNAL_SERVER_ERROR;
}

sub bad_request {
    my ($r, $error) = @_;

    $r->log_rerror(Apache2::Log::LOG_MARK, Apache2::Const::LOG_WARNING,
                   APR::Const::SUCCESS, $error);
    no warnings 'uninitialized';
    $r->log_rerror(Apache2::Log::LOG_MARK,
                   Apache2::Const::LOG_WARNING,
                   APR::Const::SUCCESS,
                   "Cookie: " . $r->headers_in->get("Cookie"))
        if $error =~ /^Request parsing error:/;
    return render $r, "error.html",
            error => "$error",
              dir => 0,
            title => "Bad Request",
      http_status => Apache2::Const::HTTP_BAD_REQUEST;
}

sub forbidden {
    my ($r, $error) = @_;

    $r->log_rerror(Apache2::Log::LOG_MARK, Apache2::Const::LOG_WARNING,
                   APR::Const::SUCCESS, $error);
    return render $r, "error.html",
            error => "$error",
              dir => 0,
            title => "Forbidden",
      http_status => Apache2::Const::HTTP_FORBIDDEN;
}

sub conflict {
    my ($r, $error, $uri) = @_;

    $r->log_rerror(Apache2::Log::LOG_MARK, Apache2::Const::LOG_INFO,
                   APR::Const::SUCCESS, $error);
    if ($uri) {
        $uri = URI->new($uri);
        die "Bad uri arg\n" unless $uri;
        $r->err_headers_out->set(Location => $r->construct_url($uri));
        $r->main->err_headers_out->set(Location => $r->construct_url($uri))
            unless $r->is_initial_req;
    }
    no warnings 'uninitialized';
    return render $r, "error.html",
            error => "$error",
              dir => 0,
              uri => "$uri",
            title => "Conflict",
      http_status => Apache2::Const::HTTP_CONFLICT;
}

sub render {
    my ($r, $template, %args) = @_;
    $args{user}               = $r->user || "";

    if ($r->uri =~ m!^/($PROJ_PAT)/wc/(\w+)/(\Q$args{user}\E-.+)$!) {
        $args{project}         ||= $1;
        $args{action}          ||= $2;
        $args{breadcrumbs}     ||= breadcrumbs $3, $args{action};

        my $path                 = $r->filename;
        no warnings 'uninitialized';
        my ($file, $dir, $ext)   = parse_filename $path . $r->path_info;
        $ext = "mdtext" if $ext eq "md"; # kludge

        $args{is_dir}          ||= ($path . $r->path_info) =~ m!/$!;
        $args{dir} = process_as_dir($r, $path) if $args{is_dir} and not exists $args{dir};
        $args{ext}             ||= ".$ext" if defined $ext;
        $args{attachments_dir} ||= "$file.page/" unless $args{is_dir};
        $args{is_attachment}   ||= $dir =~ m!\.page/$!;
    }

    if ($args{http_status}) {
        $r->status($args{http_status});
        $r->main->status($args{http_status})
            unless $r->is_initial_req;
    }
    else {
        $args{http_status} = $r->is_initial_req ? $r->status : $r->main->status;
    }

    if (client_wants_json $r) {
        $r->content_type("application/json");
        $r->main->content_type("application/json")
            unless $r->is_initial_req;
        my $lock = get_lock "$BASE_DIR/locks/json-xs-$$";
        delete $args{$_} for @{$args{nojson}};
        delete $args{nojson};
        $r->print(JSON::XS->new->utf8->pretty->encode(\%args));
        return Apache2::Const::OK;
    }
    if (defined $args{diff} and length($args{diff}) > 1_000_000) {
        substr $args{diff}, 1_000_000, length($args{diff}), "... diff truncated due to excessive size!\n";
    }

    $r->content_type('text/html; charset="utf-8"');
    $r->main->content_type('text/html; charset="utf-8"')
        unless $r->is_initial_req;
    local our @TEMPLATE_DIRS = "$BASE_DIR/webgui/templates";
    $r->print(Template($template)->render(\%args));
    return Apache2::Const::OK;
}

sub get_svn_client {
    my $r = shift;

    my $authcb = sub {
        my $cred = shift;
        $cred->username($r->user);
        $cred->password(($r->get_basic_auth_pw)[1]);
        $cred->may_save(0);
    };

    return SVN::Client->new(
        pool => $r->pool,
        auth => [SVN::Client::get_ssl_server_trust_file_provider(),
                 SVN::Client::get_simple_prompt_provider($authcb, 1),
                ],
    );
}

my %shortname = (
    html   => "html+django",
  htaccess => "apache",
    rst    => "restructuredtext",
    css    => "css",
    js     => "javascript",
    py     => "python",
    pl     => "perl",
    pm     => "perl",
    java   => "java",
    rdf    => "xml",
    xml    => "xml",
    xsl    => "xslt",
    xslt   => "xslt",
);

sub get_file_info {
    my ($r, $file)   = @_;
    my $subr         = $r->lookup_file($file);
    my $content_type = $subr->content_type || "text/plain";

    $content_type =~ /^text/
        or return "", scalar $content_type =~ /^image/;

    read_text_file $file, \ my %h;
    my $title = $h{headers}->{title};

    unless (client_wants_json $r) {
        my (undef, undef, $ext) = parse_filename $file;
        $ext ||= "";
        fixup_code $file, $shortname{$ext} || "text", $h{content}
            unless $ext eq "mdtext" or $ext eq "md";
    }

    return $title, 0, \%h if $title;
    return $1, 0, \%h if $h{content} =~ m!{% block title %}(.*?){% endblock %}!s;
    return $1, 0, \%h if $h{content} =~ m!<title>(.*?)</title>!s;
    return "", 0, \%h;
}

sub process_as_dir {
    my ($r, $dir) = @_;
    $dir = dirname $dir unless $dir =~ m!/$!;
    normalize_svn_path $dir;
    my @st = run_shell_command svn => [qw/status --depth immediates/], $dir;
    chomp @st;
    my %status = map {reverse unpack "A8 A*"} @st;
    tr/ /_/ for values %status;

    my ($parent_status) = unpack "A8",
        run_shell_command svn => [qw/status --depth empty/], "$dir/..";
    $parent_status =~ tr/ /_/ if $parent_status;

    my ($title, $content);
    my ($index_file, @extras) = grep -f, glob "$dir/../index.*";
    if ($index_file) {
        $index_file = negotiate_file $r, "$dir/../index" if @extras;
        ($title, undef, my $data) = get_file_info $r, $index_file;
        $content = $data->{content};
    }

    my @dirent = {
        path         => "../",
        link_content => "Parent Directory",
        title        => $title,
        is_image     => 0,
        status       => $parent_status,
        file_content => $content,
    };
    # ignore dirs not under version control
    shift @dirent if $parent_status =~ /^svn:/;

    opendir my $dh, $dir or die "Can't opendir $dir: $!\n";
    # subdirectory-first processing
    no warnings 'uninitialized';
    for my $e (map $_->[0],
                sort {$b->[1] <=> $a->[1] || $a->[0] cmp $b->[0]}
                 map [$_, -d "$dir/$_"],
                  grep $_ ne "." && $_ ne ".." && $_ ne ".svn",
                   readdir $dh) {
        my $title    = "";
        my $is_image = 0;
        my $path     = "$dir/$e";
        my $data;
        if (-l $path) {
            next;
        }
        elsif (-d _) {
            if (my ($file, @extras) = grep -f, glob "$path/index.*") {
                $file = negotiate_file $r, "$path/index" if @extras;
                ($title, $is_image, $data) = get_file_info $r, $file;
            }
            $e .= "/";
        }
        elsif (-f _) {
            ($title, $is_image, $data) = get_file_info $r, $path;
        }
        else {
            # ignore other entities
            next;
        }
        my $uri = URI->new(uri_escape $e, $URIc);
        push @dirent, {
            path         => "$uri",
            link_content => $e,
            title        => $title,
            is_image     => $is_image,
            status       => $status{$path},
            file_content => $data->{content},
        };
    }
    return \@dirent;
}

sub separate_content {
    my $filename = shift;
    open my $fh, "<", $filename or die "Can't open $filename: $!\n";
    no warnings 'uninitialized';
    local $_ = <$fh>;
    my $BOM = "\xEF\xBB\xBF";
    s/^$BOM//;
    my ($headers, $content);
    if (/^---\s+$/) {
        $headers = $_;
        while (<$fh>) {
            $headers .= $_;
            last if /^---\s+$/;
        }

       $content = join "", <$fh>;
    }
    else {
        my $in_headers = /^[\w-]+:\s+/;
        do {
            if ($in_headers) {
                if (/^[\w-]+:\s+/) {
                    $headers .= $_;
                }
                elsif (/^\s+\S/) {
                    $headers .= $_;
                }
                elsif (/^$/) {
                    $in_headers = 0;
                }
                else {
                    $content .= $_;
                    $in_headers = 0;
                }
            }
            else {
                $content .= $_;
            }
        } while <$fh>;
    }

    return $headers, $content;
}

sub join_content {
    my ($headers, $content) = @_;
    return $content unless defined $headers and length $headers;
    return $headers . $content if $headers =~ /^---\s+$/m;
    return $headers . "\n" . $content;
}

sub run_shell_command {
    my ($cmd, $args, @filenames) = @_;
    $args = [ @$args ];
    local %ENV = (
        PATH => $PATH,
        HOME => $HOME,
        LANG => $LANG,
    );
    no warnings 'uninitialized';
    for (@filenames, @$args) {
        s/'/'\\''/g;
        "'$_'" =~ /^(.*)$/ and $_ = $1
            or die "Can't detaint '$_'\n";
    }
    my @rv = `$cmd @$args -- @filenames 2>&1`;
    return wantarray ? @rv : join "", @rv;
}

# For this to do anything useful MultiViews must be enabled on the
# $BASE_DIR/wc/ directory.  It is closely tied to the current
# calling invocation in the redirect sub, but basically assumes
# that the file args are non-existent wc paths, with the first
# filename being more generic than the second one.

sub negotiate_file {
    my ($r, $file1, $file2) = @_;
    # The reason we take an intermediate subreq here is to
    # avoid any funky lookup optimizations which would trigger
    # the subrequest's uri to be filled in with a reasonable guess,
    # which would trigger the ASF::CMS::MapToStorage handler to
    # perform the lookup instead of the default maptostorage handler.
    # We want that lookup to fail so mod_negotiation can kick in.
    # The funky lookup optimizations only happen when the subrequest
    # is an immediate directory entry of the parent request, which we can
    # avoid by doing a lookup of "/", which will resolve to
    # the docroot, not the base dir of the working copies.

    my $s = $r->lookup_uri("/");
    my $subr = $s->lookup_file($file1);
    return $subr->filename
        if $subr->status == Apache2::Const::HTTP_OK or not $file2;
    return $s->lookup_file($file2)->filename;
}

sub send_options_or_not_allowed {
    my $r = shift;
    $r->err_headers_out->set(Allow => join ",", OPTIONS => @_);
    $r->main->err_headers_out->set(Allow => join ",", OPTIONS => @_)
        unless $r->is_initial_req;

    $r->method_number == Apache2::Const::M_OPTIONS
        and return Apache2::Const::OK;

    return render $r, "error.html",
            error => $r->method . " method is not allowed for uri " . $r->uri,
              dir => 0,
            title => "Method Not Allowed",
      http_status => Apache2::Const::HTTP_METHOD_NOT_ALLOWED;
}

sub client_wants_json {
    my $r     = shift;
    my $apreq = APR::Request::Apache2->handle($r);

    return 1 if $apreq->args("as_json");

    no warnings 'uninitialized';
    my %accept;
    for (split /,\s*/, $r->headers_in->get("Accept")) {
        /([^;]+)(?:;\s*q=([\d.]+))?/ or next;
        $accept{$1} = $2 // 1;
    }
    for (sort {$accept{$b} <=> $accept{$a} || $a cmp $b} keys %accept) {
        return 1 if $_ eq "application/json" or $_ eq "application/*";
        return 0 if $_ eq "text/html"        or $_ eq "text/*";
    }
    return 0;
}

sub fixup_code {
    my $prefix = shift;
    my $type   = shift;

    $prefix =~ s!^($BASE_DIR/wc/$PROJ_PAT/[^/]+/).*$!$1!;

    for (@_) {
        s/\Q$prefix//g;
        s/^/    :::$type\n/,  s/\n/\n    /g
            if $type;
    }
}

sub breadcrumbs {
    my @path = split m!/!, shift, -1;
    my $tail = pop @path;
    my @rv;
    my $relpath = "../" x @path;
    my $ad; # attachment dir
    ++$ad and $relpath =~  s!\.\./$!! if length $tail and $path[-1] =~ /\.page$/;
    push @path, $tail if length $tail;
    my $action = shift;
    for (@path[0..$#path-1]) {
        $relpath =~  s!\.?\./$!!;
        $relpath ||= ++$ad == 3 ? "$_/" : './';
        push @rv, qq(<a href="$relpath?action=$action">$_</a>);
    }
    return join "&nbsp;&raquo&nbsp;", @rv, $path[-1];
}

sub dev_list {
    my $list = "";
    my $filename = shift;
    my ($url) = grep /^URL: (\S+)/, ASF::CMS::run_shell_command svn => [qw/info/], $filename;

    if ($filename =~ m!^(.*/wc/[^/]+)! and -f "$1/dev_list") {
        open my $fh, "<", "$1/dev_list" or die "Can't open $1/dev_list: $!";
        chomp($list = <$fh>);
        $list =~ s/(\@[^.]*?)\.incubator(.*)/$1$2/
            if $url !~ m!/repos/asf/incubator/!;
    }
    elsif ($url =~ m!/repos/asf/incubator/($ASF::CMS::PROJ_PAT)/!) {
        $list = "dev\@$1.incubator.apache.org";
    }
    elsif ($url =~ m!/repos/asf/($ASF::CMS::PROJ_PAT)/!) {
        $list = "dev\@$1.apache.org";
    }

    return $list;
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

=head1 NOTES

To hack this software to run in non-ASF installations you need to modify
the C<$BASE_DIR> and C<$SVN_URL>, as well as C<$HOME> and C<$PATH>
so shell commands will work via C<run_shell_command>.  You also need to
set C<$STAGE_DIR> to a sensible value (C<$STAGE_DIR/$project/trunk/content>
is the path to buildbot-generated staged sites).  C<$REALM> should match
the server config (so should C<$HOME> match the server envvar, with a cached
svn cert auth contained in C<$HOME/.subversion/auth/svn.ssl.server/...>).

Now this software has a hard dependency on zfs: note the C<$ZROOT> variable
will be set to point at the zfs dataset mounted on C<$BASE_DIR/wc>.

Currently there are hacks for the incubator that are based on the fact
that incubating podlings have "stuttering" urls: the project name and
the base path are the same.  It's just a couple of lines of code, and
if they cause you concern go ahead and remove them.

Otherwise study the layout for

  https://svn.apache.org/repos/infra/websites/{production,staging}
  +----------------- $SVN_URL ---------------+

because that is tied into how this package manages its projects. You need
to setup appropriate authorization rules for your users, but you don't
need to create any special accounts other than one for the buildbot user.
This webgui software will always use the given user's basic auth credentials
to commit to svn.  It presumes that the svn paths are all read-accessible
without providing any credentials.

C<$BASE_DIR> is expected to have an svn checkout of the cms/webgui/ in
the "webgui" dir and an svn checkout of the cms/build/ in the "build" dir.
IOW, C<$BASE_DIR/build/lib> and C<$BASE_DIR/webgui/lib> should both
be paths to perl modules.  There should also be "locks" and "wc" (and
optionally "tmp" if mod_apreq2 is configured to use it) subdirs that are
writable by the webserver's user.

The C</usr/local/cms/logs> directory is where markdownd.py normally expects
to find its socket.  If you install to a C<$BASE_DIR> other than
C</usr/local/cms>, you'll need to setup a C<$BASE_DIR/logs> dir and export
a custom MARKDOWN_SOCKET env var to the build scripts prior to executing them.

Security-wise because of the use of svnmucc it is necessary to run this service
on a restricted-access host because passwords will be exposed on svnmucc command
lines.

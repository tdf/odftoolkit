            Confluence Auto-Export to CMS Conversion Tools
            -----------------------------------------------

These tools exist to help you migrate a site from using confluence wiki
(cwiki) auto-export to the CMS.

The steps are:

1. Setup CMS site structure in SVN
 Details on this are available from http://www.staging.apache.org/dev/cms.html

2. Setup CMS view and path
 These will handle turning the markdown into nice HTML
 See https://svn.apache.org/repos/asf/comdev/site/trunk for an example

3. Convert your CWiki auto-export template
 You need to turn your CWiki auto-export template into two CMS templates.
 One template handles the main layout, and is available for use by DTL
  powered HTML pages
 The second allows markdown formatted text to be rendered

 Use <convert_export_template.pl> to handle this

 eg
   cd templates
   ~/apache/cms/conversion-utilities/cwiki/convert_export_template.pl /tmp/export.xml standard.html standard_markdown.html
   cd ..

4. Convert your CWiki pages to markdown
 You need to spider your CWiki site, and download the wiki pages in their raw
  CWiki markup. These pages then need to be converted into MarkDown syntax.

 The markup translation is done by <convert_cwiki_markup.pl>
 The spidering tool is <export_site.pl>

 export_site.pl will handle downloading all the pages, and running them
  through convert_cwiki_markup.pl for you. In theory it will do everything
  you need

5. Test the site generation
 Use build/build_site.pl to generate the HTML version of the site

6. Tweak the markdown pages as required

7. Delete the cwiki markup files
 The cwiki markup files will have been saved in the content directory for
  you to review when converting. When you're happy, these should be removed,
  and probably shouldn't ever be committed to svn

#! 
# coding:utf-8

'''

Add headerlink Extension for Python-Markdown
==========================================

This extension adds headerlink CSS to the output HTML in Python-Markdown.
This is intended for use with TocExtension(permalink=True) which generates the links

Simple Usage:

    >>> import markdown
    >>> markdown.markdown("Some text", ['addheaderlinkcss']) # doctest: +ELLIPSIS
    u'<style...h1:hover > .headerlink {\\n  display: inline;...</style>\\n<p>Some text</p>'

'''

import markdown
from markdown.util import etree
from markdown.util import isBlockLevel

# Global Vars
SECTIONLINK_PERMITTED_TAGS=set("h1 h2 h3 h4 h5 h6".split())
SECTIONLINK_CSS = r'''
/* The following code is added by mdx_addheaderlinkcss.py
   It was originally lifted from http://subversion.apache.org/style/site.css */
/*
 * Hide class="headerlink", except when an enclosing heading
 * has the :hover property.
 */
.headerlink {
  display: none;
}
'''

for tag in SECTIONLINK_PERMITTED_TAGS:
    SECTIONLINK_CSS += '''\
%s:hover > .headerlink {
  display: inline;
}
''' % tag

from markdown import Extension
from markdown.treeprocessors import Treeprocessor

class AddHeaderlinkCssTreeProcessor(Treeprocessor):
    def run(self, root):
        if isBlockLevel(root.tag) and root.tag not in ['code', 'pre']:
            child = etree.Element("style")
            for k,v in {
                          'type': 'text/css',
                       }.iteritems():
                child.set(k, v)
            # Note upstream doc bug: it's not called markdown.AtomicString().
            child.text = markdown.util.AtomicString(SECTIONLINK_CSS)
            root.insert(0, child)
            child.tail = root.text; root.text = None;

""" Add tableclass to Markdown. """
class AddHeaderlinkCssExtension(Extension):
    def extendMarkdown(self, md, md_globals):
        md.treeprocessors.add('addheaderlinkcss',
                              AddHeaderlinkCssTreeProcessor(md.parser),
                              '_end')

# https://pythonhosted.org/Markdown/extensions/api.html#makeextension says
# to use (**kwargs) only, but built-in extensions actually use (*args, **kwargs) 
def makeExtension(**kwargs):
    return AddHeaderlinkCssExtension(**kwargs)

if __name__ == "__main__":
    import doctest
    # Test does not work currently because processing is disabled
    doctest.testmod()

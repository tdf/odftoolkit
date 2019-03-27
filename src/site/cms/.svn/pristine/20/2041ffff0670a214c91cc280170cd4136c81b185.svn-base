#! 
# coding:utf-8

'''
id Extension for Python-Markdown
==========================================

This extension adds ids to block elements in Python-Markdown.

Simple Usage:

    >>> import markdown
    >>> from markdown.extensions.toc import TocExtension
    >>> def _strip_CSS(str):
    ...     return str.split("</style>\\n", 2)[1]
    >>> text = """
    ... [TOC]
    ...
    ... list: {#list.1} 
    ...
    ... 1. This is a test {#node1}
    ... 2. Other {#fun}{.node2 node3}
    ... # Download! # [#downloading]
    ... 
    ... More
    ... """
    >>> _strip_CSS(markdown.markdown(text, [TocExtension(permalink=True), 'elementid']))
    u'<div class="toc">\\n<ul>\\n<li><a href="#downloading">Download!</a></li>\\n</ul>\\n</div>\\n<p id="list.1">list:<a class="elementid-permalink" href="#list.1" title="Permanent link">&para;</a></p>\\n<ol>\\n<li id="node1">This is a test<a class="elementid-permalink" href="#node1" title="Permanent link">&para;</a></li>\\n<li class="node2 node3" id="fun">Other<a class="elementid-permalink" href="#fun" title="Permanent link">&para;</a></li>\\n</ol>\\n<h1 id="downloading">Download!<a class="headerlink" href="#downloading" title="Permanent link">&para;</a></h1>\\n<p>More</p>'
    >>> text2 = u"""Spain {#el1}
    ... :    Name of a country
    ...      in the South West of Europe
    ... 
    ... Espa\xf1a {#el2}
    ... :    Name of Spain
    ...      in Spanish (contains non-ascii)
    ... 
    ... End of definition list...
    ... # Hi there {#see toc permalinks this too}
    ... """
    >>> _strip_CSS(markdown.markdown(text2, [TocExtension(permalink=True),'elementid', 'def_list']))
    u'<dl>\\n<dt id="el1">Spain<a class="elementid-permalink" href="#el1" title="Permanent link">&para;</a></dt>\\n<dd>Name of a country\\n in the South West of Europe</dd>\\n<dt id="el2">Espa\\xf1a<a class="elementid-permalink" href="#el2" title="Permanent link">&para;</a></dt>\\n<dd>Name of Spain\\n in Spanish (contains non-ascii)</dd>\\n</dl>\\n<p>End of definition list...</p>\\n<h1 id="see toc permalinks this too">Hi there<a class="headerlink" href="#see toc permalinks this too" title="Permanent link">&para;</a></h1>'



Copyright 2010
* [Santiago Gala](http://memojo.com/~sgala/blog/)

'''

import markdown, re
from markdown.util import etree
import markdown.extensions
from markdown.util import isBlockLevel

# Global Vars
ID_RE = re.compile(r"""[ \t]*                    # optional whitespace
                       [#]{0,6}                  # end of heading
                       [ \t]*                    # optional whitespace
                       (?:[ \t]*[{\[][ \t]*(?P<type>[#.])(?P<id>[-._:a-zA-Z0-9 ]+)[}\]])
                       [ \t]*                    # optional whitespace
                       (\n|$)              #  ^^ group('id') = id attribute
                    """,
                    re.VERBOSE)

HEADER_TAGS=set("h1 h2 h3 h4 h5 h6".split())
CSS = r'''
/* The following code is added by mdx_elementid.py
   It was originally lifted from http://subversion.apache.org/style/site.css */
/*
 * Hide class="elementid-permalink", except when an enclosing heading
 * has the :hover property.
 */
.headerlink, .elementid-permalink {
  visibility: hidden;
}
'''
for tag in HEADER_TAGS:
    CSS += '%s:hover > .headerlink, ' % tag

class IdTreeProcessor(markdown.treeprocessors.Treeprocessor):
    """ Id Treeprocessor - parse text for id specs. """

    def __init__(self,md):
        self.css = CSS
        self.seen_block_tag = {}

    def _parseID(self, element):
        ''' recursively parse all {#idname}s at eol into ids '''
        if isBlockLevel(element.tag) and element.tag not in ['code', 'pre']:
            #print element
            while element.text and element.text.strip():
                m = ID_RE.search(element.text)
                if m:
                    if m.group('type') == '#':
                        element.set('id',m.group('id'))
                        element.text = element.text[:m.start()]

                        # TODO: should this be restricted to <h1>..<h4> only?
                        if element.tag not in HEADER_TAGS:
                            child = etree.Element("a")
                            for k,v in {
                                    'class': 'elementid-permalink',
                                    'href': '#'+m.group('id'),
                                    'title': 'Permanent link',
                            }.iteritems():
                                child.set(k, v)
                            # child.text = r" ¶" # U+00B6 PILCROW SIGN
                            child.text = "&para;"
                            # Actually append the child, and a space before it too.
                            element.append(child)
#                            if len(element):
#                                element.text += " "
#                            else:
#                                element[-1].tail += " "

                            if element.tag not in self.seen_block_tag:  
                                self.css += '%s:hover > .elementid-permalink, ' % element.tag
                                self.seen_block_tag[element.tag] = True

                    else:
                        element.set('class',m.group('id'))
                        element.text = element.text[:m.start()]

                else:
                    break
            for e in element:
                self._parseID(e)
        return element
        

    def run(self, root):
        '''
        Find and remove all id specs references from the text,
        and add them as the id attribute of the element.
        
        ROOT is div#section_content.
        '''
        if isBlockLevel(root.tag) and root.tag not in ['code', 'pre']:
            self._parseID(root)
            child = etree.Element("style")
            for k,v in {
                          'type': 'text/css',
                       }.iteritems():
                child.set(k, v)
            # Note upstream doc bug: it's not called markdown.AtomicString().
            self.css += 'dt:hover > .elementid-permalink { visibility: visible }'
            child.text = markdown.util.AtomicString(self.css)
            root.insert(0, child)
            self.css = CSS
            self.seen_block_tag = {}
            # child.tail = root.text; root.text = None;
        return root

class IdExtension(markdown.Extension):
    """ Id Extension for Python-Markdown. """

    def extendMarkdown(self, md, md_globals):
        """ Insert IdTreeProcessor in tree processors. It should be before toc. """
        idext = IdTreeProcessor(md)
        idext.config = self.config
        md.treeprocessors.add("elid", idext, "_begin")


def makeExtension(configs={}):
    return IdExtension(configs=configs)

"""
Version 2.6+ of Python markdown needs to use the following code instead
See https://pythonhosted.org/Markdown/release-2.6.html#the-configs-keyword-is-deprecated

def makeExtension(**kwargs):
    return IdExtension(**kwargs)

"""

if __name__ == "__main__":
    import doctest
    doctest.testmod()

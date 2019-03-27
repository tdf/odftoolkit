#!/usr/local/bin/python
#           Licensed to the Apache Software Foundation (ASF) under one
#           or more contributor license agreements.  See the NOTICE file
#           distributed with this work for additional information
#           regarding copyright ownership.  The ASF licenses this file
#           to you under the Apache License, Version 2.0 (the
#           "License"); you may not use this file except in compliance
#           with the License.  You may obtain a copy of the License at
#
#             http://www.apache.org/licenses/LICENSE-2.0
#
#           Unless required by applicable law or agreed to in writing,
#           software distributed under the License is distributed on an
#           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#           KIND, either express or implied.  See the License for the
#           specific language governing permissions and limitations
#           under the License.

'''
Simple test case:

    >>> import markdown
    >>> text = """
    ... BEGIN
    ...
    ... <table>
    ... </table>
    ...
    ... | a | b |
    ... |---|---|
    ... | c | d |
    ...
    ...  w | x
    ... ---|---
    ...  y | z
    ...
    ... END
    ... """
    >>> # test without addon
    >>> markdown.markdown(text, ['tables'])
    u'<p>BEGIN</p>\\n\
<table>\\n</table>\\n\\n\
<table>\\n<thead>\\n<tr>\\n<th>a</th>\\n<th>b</th>\\n</tr>\\n</thead>\\n<tbody>\\n<tr>\\n<td>c</td>\\n<td>d</td>\\n</tr>\\n</tbody>\\n</table>\\n\
<table>\\n<thead>\\n<tr>\\n<th>w</th>\\n<th>x</th>\\n</tr>\\n</thead>\\n<tbody>\\n<tr>\\n<td>y</td>\\n<td>z</td>\\n</tr>\\n</tbody>\\n</table>\\n\
<p>END</p>'
    >>> # test with addon, showing that class has only been added to generated tables
    >>> markdown.markdown(text, ['tables', 'addtableclass'])
    u'<p>BEGIN</p>\\n\
<table>\\n</table>\\n\\n\
<table class="table">\\n<thead>\\n<tr>\\n<th>a</th>\\n<th>b</th>\\n</tr>\\n</thead>\\n<tbody>\\n<tr>\\n<td>c</td>\\n<td>d</td>\\n</tr>\\n</tbody>\\n</table>\\n\
<table class="table">\\n<thead>\\n<tr>\\n<th>w</th>\\n<th>x</th>\\n</tr>\\n</thead>\\n<tbody>\\n<tr>\\n<td>y</td>\\n<td>z</td>\\n</tr>\\n</tbody>\\n</table>\\n\
<p>END</p>'
'''

"""

Python markdown module Tree PostProcessor extension
Adds <<class="table">> to any <table> tags that don't have a class

This assumes that all <table> tags in the tree created by Python markdown
need to have the class added.

The extension can be enabled by adding 'addtableclass' to the EXTENSIONS list 
in the file markdownd.py and restarting the daemon process

To test interactively, ensure the tables extension is specified:

$ python -m markdown -x tables -x addtableclass <inputfile>

"""

from markdown import Extension
from markdown.treeprocessors import Treeprocessor

class TableClassTreeProcessor(Treeprocessor):
    # Recursive scan of element tree
    def _scan(self, element):
        if element.tag == 'table':
            if element.get('class') == None:
                element.set('class','table')
        for e in element:
            self._scan(e)
        
    def run(self, root):
        self._scan(root)
        
""" Add tableclass to Markdown. """
class TableClassExtension(Extension):
    def extendMarkdown(self, md, md_globals):
        md.treeprocessors.add('addtableclass',
                              TableClassTreeProcessor(md.parser),
                              '_end')

# https://pythonhosted.org/Markdown/extensions/api.html#makeextension says
# to use (**kwargs) only, but built-in extensions actually use (*args, **kwargs) 
def makeExtension(**kwargs):
    return TableClassExtension(**kwargs)


if __name__ == "__main__":
    import doctest
    doctest.testmod()

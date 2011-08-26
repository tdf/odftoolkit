# /usr/bin/python

########################################################################
#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
#
# Copyright 2009 Sun Microsystems, Inc. All rights reserved.
#
# Use is subject to license terms.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy
# of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
# obtain a copy of the License at http://odftoolkit.org/docs/license.txt
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#
# See the License for the specific language governing permissions and
# limitations under the License.
#
########################################################################/

from datetime import datetime
from zipfile import ZipFile, ZipInfo, ZIP_DEFLATED, ZIP_STORED
from hierarchy import CHILD_ELEMENTS, CHILD_ATTRIBUTES
from xml.dom import minidom

class OdfDocument:

    # Constructor. Param: File-like input object or path to input file
    def __init__(self, path_or_file):
        self.mZip = ZipFile(path_or_file, 'r')
        self.mContentDom = None
        self.mStylesDom = None

    # Get DOM of content.xml
    def get_content_dom(self):
        if not self.mContentDom:
            contentString = self.mZip.read("content.xml")
            self.mContentDom = minidom.parseString(contentString)
        return self.mContentDom

    # Get document type depended content root (e.g. office:text node for .odt)
    def get_content_root(self):
        self.get_content_dom()
        document_content = self.mContentDom.firstChild
        if document_content:
            office_body = None
            for child in document_content.childNodes:
                if child.nodeName == "office:body":
                    office_body = child
                    break
            if office_body:
                return office_body.firstChild

    # Get DOM of styles.xml
    def get_styles_dom(self):
        if not self.mStylesDom:
            stylesString = self.mZip.read("styles.xml")
            self.mStylesDom = minidom.parseString(stylesString)
        return self.mStylesDom

    # Save Document. The input file must not be overwritten.
    def save(self, path_or_file):
        outZip = ZipFile(path_or_file, 'w')
        today = datetime.today()
        self.get_content_dom()
        self.get_styles_dom()
        for filename in self.mZip.namelist():
            transmit = ""
            info = ZipInfo(filename, (today.year, today.month, today.day, today.hour, today.minute, today.second))
            if filename == "content.xml":
                transmit = self.mContentDom.toxml().encode( 'utf-8')
            elif filename == "styles.xml":
                transmit = self.mStylesDom.toxml().encode( 'utf-8')
            else:
                transmit = self.mZip.read(filename)
            if filename == "mimetype":
                info.compress_type = ZIP_STORED
            else:
                info.compress_type = ZIP_DEFLATED
            outZip.writestr(info, transmit)
        outZip.close()

    # This method should be in Node itself
    def allowed_child_elements(self, odf_node):
        return CHILD_ELEMENTS[odf_node.nodeName]
        
    # This method should be in Node itself
    def allowed_attributes(self, odf_node):
        return CHILD_ATTRIBUTES[odf_node.nodeName]
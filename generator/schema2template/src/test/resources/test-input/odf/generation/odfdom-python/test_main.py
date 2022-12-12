########################################################################
#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
#
# Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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

import sys
import os.path
from odfdocument import OdfDocument

if __name__ == "__main__":
    mProjectBase = sys.path[0]
    textTemplate = os.path.join(mProjectBase, "OdfTextDocument.odt")
    odt = OdfDocument(textTemplate)
    dom = odt.get_content_dom()
    office_text = odt.get_content_root()

    if "text:p" in odt.allowed_child_elements(office_text):
        new_p = dom.createElement("text:p")
        office_text.appendChild(new_p)
        new_p.appendChild(dom.createTextNode("Text in a new paragraph."))

    outputPath = os.path.join(mProjectBase, "Output.odt")
    odt.save(outputPath)

    print "Saved output in file %s" % (outputPath)
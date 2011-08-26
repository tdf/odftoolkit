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
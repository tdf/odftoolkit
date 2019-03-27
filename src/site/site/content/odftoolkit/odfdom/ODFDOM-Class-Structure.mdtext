Title: Class Structure in ODFDOM   

The Java packages reflect the layers of the ODFDOM library.

Shared Functionality
--------------------

Shared by all layers are the basic XML classes as in the `org.odftoolkit.odfdom` package, like    

  - `OdfElement`: parent of all ODFDOM elements
  - `OdfAttribute`: parent of all ODFDOM attributes 
  - `OdfName`: the union of an ODF local name and an ODF namespace, represented by 
  - `OdfNamespace`: embracing all ODF namespaces (prefixes and URIs) defined in the specification.

Similar used through all layers are the ODF datatypes, to be found in

  - `org.odftoolkit.odfdom.type`: All datatypes defined in the ODF 1.2 specification, mostly overtaken from W3C Schema types, like AnyURI.
Helper functionality can be found at those types, like in case for AnyURI en/decode() methods.

The Package/Physical Layer
--------------------------

These classes are in the `org.odftoolkit.odfdom.pkg` package to access files from the ODF package.
The classes you will find here include:

  - `OdfPackage`: Allows you to access, insert, delete, load, and save individual entries in a packaged ODF document.
  - `OdfPackageStream`: Allows you to write to a member of the ODF document as an output stream.
  - `OdfXMLHelper`: Provides utility methods to parse the XML content of a package member and apply an XSLT transformation to it.

The ODF Typed DOM / XML Layer
-----------------------------

These classes are in general the generated DOM elements and attributes of ODF 1.2. 

  - `org.odftoolkit.odfdom.dom.element`: These classes give you access to the individual ODF elements. Its sub-packages are divided by namespace, so you would find the code for a `draw:ellipse` element in the `org.odftoolkit.odfdom.dom.element.draw.DrawEllipseElement` class.
  - `org.odftoolkit.odfdom.dom.attribute`: These classes give you access to the individual ODF attributes  (similar to the elements before). Again its sub-packages are divided by namespace, so you would find the code for a `xml:id` attribute in the `org.odftoolkit.odfdom.dom.attribute.xml.XmlId` class.

The third package `org.odftoolkit.odfdom.dom.style` will vanish in the future (mostly parts will be moved to the DOC layer in an upcoming release).

  - `org.odftoolkit.odfdom.dom.style`: These classes and the `.props` sub-packages give you access to ODF&rsquo;s style families and their properties.

If you are using an IDE such as NetBeans or Eclipse, you don't have to memorize this hierarchy; the IDE can generate the proper `import` statements for you. 

The code in these classes is generated directly from [the Relax NG][1] (RNG) schema for ODF.

The ODF Document / Convenience Functionality Layer
--------------------------------------------------

This is the layer that developers will use most often. A sub-level like `org.odftoolkit.odfdom.doc.text` inherits (currently) from the elements of the `org.odftoolkit.odfdom.dom.element.text` package. Aside of the namespace prefix an 'Odf' prefix is being used in the beginning. Furthermore is the namespace prefix neglected in case it already exists in the local name. The convenient class for the ODF element `table:table`, which is represented in the DOM layer by `org.odftoolkit.odfdom.table.TableTableElement`, is OdfTable and NOT OdfTableTable.
We encourage developers to create methods for these classes, based on actual use cases of the toolkit.

The entry point for the convenient layer should be in the future the document itself. Some example functions had been added to `OdfTextDocument` to show this, like `newParagraph()`, adding a paragraph (ie. `text:p` element) at the end of the document.


  [1]: http://www.relaxng.org/
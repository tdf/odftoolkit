**Getting Start Guide**
-------------------

This start guide shows how to use the Simple ODF API to create a text document with image, paragraph, list and table in OpenDocument format (ODF). In about 40 lines of code, you will be able to produce an OpenDocument Format text document named HelloWorld.odt that looks like this:

![demo file][1]

**Prerequisites**

Simple ODF is written in Java, so you will need to have Java 1.6 or above installed on your system. Make sure you download the SDK (software development kit), not just the runtime version of Java.

In this tutorial, you can build and run the application from the command line or IDE, such as Eclipse or Netbeans.
Anyway, all required jars, are stored within the "lib" directory of the demo bundle and needed to be added to the classpath.
A JAR with all dependencies had been included as download within [the resource file][2] of this demo.

NOTE: The latest version of this all-inclusive-JAR can be received after building the Simple ODF project. This will be JAR within the 'target' directory embracing all required JARs.


**Code**

We write all of the code needed to create the document in the HelloWorld class. For simplicity, the code is wrapped in main method directly, package declaration is omitted and hard code is also included in this program.


	import java.net.URI;

	import org.odftoolkit.simple.TextDocument;
	import org.odftoolkit.simple.table.Cell;
	import org.odftoolkit.simple.table.Table;
	import org.odftoolkit.simple.text.list.List;

	public class HelloWorld {
		public static void main(String[] args) {
			TextDocument outputOdt;
			try {
				outputOdt = TextDocument.newTextDocument();

				// add image
				outputOdt.newImage(new URI("odf-logo.png"));

				// add paragraph
				outputOdt.addParagraph("Hello World, Hello Simple ODF!");

				// add list
				outputOdt.addParagraph("The following is a list.");
				List list = outputOdt.addList();
				String[] items = {"item1", "item2", "item3"};
				list.addItems(items);

				// add table
				Table table = outputOdt.addTable(2, 2);
				Cell cell = table.getCellByPosition(0, 0);
				cell.setStringValue("Hello World!");

				outputOdt.save("HelloWorld.odt");
			} catch (Exception e) {
				System.err.println("ERROR: unable to create output file.");
			}
		}
	}

The TextDocument class has these convenient methods:

        // Creates a new text document that contains an empty paragraph.
    	TextDocument.newTextDocument();
    	// Puts the image at the given location at the end of the document.
    	TextDocument.newImage(URI location)
    	// Creates a new paragraph with the given content and appends it to the end of the document.
        TextDocument.addParagraph(String content)
    	// Creates a new, empty list and appends it to the end of the document you're building. You can add items for this list.
    	TextDocument.addList()
    	// Creates a new table with columnCount columns and rowCount rows. You can get the cell at given position and set its content.
    	TextDocument.addTable(int columnCount, int rowCount);
    	// Saves the document at the given path.
    	TextDocument.save(String path)

**Building and Running**

If you use an IDE, you can just run this class as a Java Application. If you use command line, you may compile the code and run it with the following commands:

    javac -cp lib/simple-odf-0.8.2-incubating-jar-with-dependencies.jar HelloWorld.java

		LINUX using ':' as classpath separator:
		java -cp .:lib/simple-odf-0.8.2-incubating-jar-with-dependencies.jar HelloWorld

		WINDOWS using ';' as classpath separator:
		java -cp .;lib/simple-odf-0.8.2-incubating-jar-with-dependencies.jar HelloWorld

**Getting the Files**

You can download the code and resources of this demo from [here][2].




  [1]: helloworld.png
  [2]: helloworld.zip

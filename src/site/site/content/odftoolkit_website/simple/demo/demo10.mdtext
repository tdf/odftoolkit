**[Demos][1] > Automatically Formatting a Document**  

**Overview**  

Simple API enhanced features to support document formatting in version 0.6.5. It supplies methods for manipulating headings, page breaks, hyperlinks, comments, font and alignment. This demo shows how to use these features to help document formatting.  

It's a common scenario that we need apply some  styles to plain text to improve its appearance. In this demo, a text document is created using plain text from a text file. While reading content, paragraphs whose  length  is less than 20 characters are changed to headings with a new font style. "Version" and "date" information is  set as right alignment and gray text. Each line which matches an URL  will be applied as a hyperlink. Each heading and its following paragraphs are considered as a chapter. There will be a page break after page line count larger than a predefined value. Then verify word spelling and add comments as tip to those which may have a spelling mistake. The last one is a security check. After these processes, the plain text has been changed into a formatted document.  

This picture shows part of the generated document. The new document looks orderly and beautiful than before.

![alt text][2]

**Code Introduction**

There code of this demo is very clear. Firstly, we open the data source and create a text document. Secondly, we create paragraph for each line. Thirdly, we set different style for different content. Then we do spell and security check with the help of Navigation API. Comments are given if suspicious words are found.  Finally, the new created document is saved.  

            BufferedReader reader = new BufferedReader(new InputStreamReader(this.class.getResourceAsStream("text.txt")));
    		String in = reader.readLine();
    		TextDocument doc = TextDocument.newTextDocument();
    		int lineCount = 0;
    		int pageLineCount = 0;
    		Paragraph refParagraph = null;
    		while (in != null) {
    			in = in.trim();
    			Paragraph paragraph = doc.addParagraph(in);
    			lineCount++;
    			pageLineCount++;
    			switch (lineCount) {
    			case 1:
    				paragraph
    						.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
    				paragraph.setFont(new Font("Arial", FontStyle.BOLD, 16));
    				paragraph.applyHeading();
    				break;
    			case 2:
    			case 3:
    				paragraph.setHorizontalAlignment(HorizontalAlignmentType.RIGHT);
    				paragraph.setFont(new Font("Tahoma", FontStyle.ITALIC, 10,
    						Color.GRAY));
    				break;
    			default:
    				if (in.startsWith("http://")) {
    					paragraph.applyHyperlink(new URI(in));
    				}
    				if (in.length() < 20) {
    					paragraph.applyHeading();
    					paragraph.setFont(new Font("Arial", FontStyle.BOLD, 12));
    					if (pageLineCount > 16) {
    						doc.addPageBreak(refParagraph);
    						pageLineCount = 0;
    					}
    				}
    			}
    			refParagraph = paragraph;
    			in = reader.readLine();
    
    		}
    		// spell check
    		TextNavigation navigation1 = new TextNavigation("lower-level", doc);
    		while (navigation1.hasNext()) {
    			TextSelection selection = (TextSelection) navigation1
    					.nextSelection();
    			selection.addComment(
    					"Please change 'lower-level' with 'lower level'.",
    					"SpellChecker");
    		}
    		// security check
    		TextNavigation navigation2 = new TextNavigation("confidential", doc);
    		if (navigation2.hasNext()) {
    			TextSelection selection = (TextSelection) navigation2
    					.nextSelection();
    			selection
    					.addComment(
    							"This is a confidential document, please don't redistribute.",
    							"SecurityChecker");
    		}
    		doc.save("format_text.odt");  

**Download**

Powered by the Simple Java API for ODF version [0.6.5][3].  
You can download the code of this demo from [here][4].


  [1]: index.html
  [2]: image/demo10.png
  [3]: ../downloads.html
  [4]: demo10.zip
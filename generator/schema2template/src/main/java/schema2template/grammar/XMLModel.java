/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>*********************************************************************
 */
package schema2template.grammar;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.writer.relaxng.RELAXNGWriter;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParserFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.SAXException;

/**
 * The most important model, the first access to the XML Schema information.
 *
 * <p>Provides all XML attribute and XML element definitions from the schema. All further
 * information can be accessed from those definitions (e.g. dependencies, constant values, data
 * types, etc.).
 */
public class XMLModel {

  PuzzlePieceSet mElements = new PuzzlePieceSet();
  PuzzlePieceSet mAttributes = new PuzzlePieceSet();
  private Map<String, SortedSet<PuzzlePiece>> elementNameToPuzzlePieces;
  private Map<String, SortedSet<PuzzlePiece>> attributeNameToPuzzlePieces;
  // Creates a Map Name->PuzzlePiece.
  // Ignoring the fact that there may be more than one PuzzlePiece per Name
  Map<String, PuzzlePiece> mNameElementMap;
  Map<String, PuzzlePiece> mNameAttributeMap;
  public Expression mRootExpression;
  private Grammar mGrammar;
  public String mLastSchemaFileName;
  private String mGrammarID;
  private String mGrammarVersion;

  /**
   * Constructs new model by the grammar and a label
   *
   * @param schemaFile grammar to read into MSV
   */
  public XMLModel(File schemaFile) {
    this(schemaFile, null, null);
  }

  /**
   * Constructs new model by the grammar and a label
   *
   * @param schemaFile grammar to read into MSV
   * @param grammarVersion numbered version used to establish timely order and create a label with
   *     the grammarID
   * @param grammarID identifier of the grammar (used in Velocity template and for output
   *     subdirectories - often concatenated with grammarVersion)
   */
  public XMLModel(File schemaFile, String grammarVersion, String grammarID) {
    mGrammarVersion = grammarVersion;
    mGrammarID = grammarID;
    mGrammar = loadSchema(schemaFile);
    mRootExpression = mGrammar.getTopLevel();
    String absolutePath = schemaFile.getAbsolutePath();
    mLastSchemaFileName =
        absolutePath.substring(
            absolutePath.lastIndexOf(File.separatorChar) + 1, absolutePath.length());

    PuzzlePiece.extractPuzzlePieces(mGrammar, mElements, mAttributes, mLastSchemaFileName);
    mElements.makeImmutable();
    mAttributes.makeImmutable();
    mNameElementMap = createMap(mElements);
    mNameAttributeMap = createMap(mAttributes);
  }

  /** @return a set of one or more elements, which might exist in the grammar for this qName */
  public SortedSet<PuzzlePiece> getElements(String qName) {
    if (this.elementNameToPuzzlePieces == null) {
      // create a map from the qName to the PuzzlePiece(s) - multiple if there are multiple
      // definitions for the qName in the grammar
      this.elementNameToPuzzlePieces =
          createMapQNameToPuzzlePiece(new TreeSet<PuzzlePiece>(this.getElements()));
    }
    return this.elementNameToPuzzlePieces.get(qName);
  }

  /** @return a set of one or more elements, which might exist in the grammar for this qName */
  public SortedSet<PuzzlePiece> getAttributes(String qName) {
    if (this.attributeNameToPuzzlePieces == null) {
      // create a map from the qName to the PuzzlePiece(s) - multiple if there are multiple
      // definitions for the qName in the grammar
      this.attributeNameToPuzzlePieces =
          createMapQNameToPuzzlePiece(new TreeSet<PuzzlePiece>(this.getElements()));
    }
    return this.attributeNameToPuzzlePieces.get(qName);
  }

  /**
   * Creates a Map, which maps the QName to PuzzlePiece(s), in case there are multiples the
   * SortedSet has more than one.
   *
   * @return one or multiple definitions within the set, in case of multiple definitions in grammar
   *     for the same QName.
   */
  private static Map<String, SortedSet<PuzzlePiece>> createMapQNameToPuzzlePiece(
      Set<PuzzlePiece> definitions) {
    Map<String, SortedSet<PuzzlePiece>> retval = new HashMap<String, SortedSet<PuzzlePiece>>();
    for (PuzzlePiece def : definitions) {
      SortedSet<PuzzlePiece> multiples = retval.get(def.getQName());
      if (multiples == null) {
        multiples = new TreeSet<PuzzlePiece>();
        retval.put(def.getQName(), multiples);
      }
      multiples.add(def);
    }
    return retval;
  }

  /** @return the MSV Grammar this model is based upon. */
  public Grammar getGrammar() {
    return mGrammar;
  }

  /** @return the version label identifying this version of this schema (XML grammar) */
  public String getGrammarVersion() {
    return mGrammarVersion;
  }

  /** @return the grammar ID identifying this schema (XML grammar) */
  public String getGrammarID() {
    return mGrammarID;
  }

  // Create Map Name->PuzzlePiece. Ignore the fact that there may be more than one PuzzlePiece per
  // Name
  private static Map<String, PuzzlePiece> createMap(Collection<PuzzlePiece> definitions) {
    Map<String, PuzzlePiece> retval = new HashMap<>();
    for (PuzzlePiece def : definitions) {
      retval.put(def.getQName(), def);
    }
    return retval;
  }

  /** Map Name to PuzzlePiece(s). */
  static Map<String, SortedSet<PuzzlePiece>> createDefinitionMap(
      Collection<PuzzlePiece> definitions) {
    Map<String, SortedSet<PuzzlePiece>> retval = new HashMap<>();
    for (PuzzlePiece def : definitions) {
      SortedSet<PuzzlePiece> multiples = retval.get(def.getQName());
      if (multiples == null) {
        multiples = new TreeSet<>();
        retval.put(def.getQName(), multiples);
      }
      multiples.add(def);
    }
    return retval;
  }

  /**
   * Load and parse a Schema from File.
   *
   * @param rngFile Schema file (RelaxNG or W3C schema)
   * @return MSV Expression Tree (more specific: The tree's MSV root expression)
   */
  public static Grammar loadSchema(File rngFile) {
    return loadSchema(rngFile.getAbsolutePath());
  }

  /**
   * Load and parse a Schema from File.
   *
   * @param rngFilePath Schema file (RelaxNG or W3C schema)
   * @return MSV Expression Tree (more specific: The tree's MSV root expression)
   */
  public static Grammar loadSchema(String rngFilePath) {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    // Parsing the Schema with MSV
    // 4-DEBUG: DebugController ignoreController = new DebugController(true, false);
    com.sun.msv.reader.util.IgnoreController ignoreController =
        new com.sun.msv.reader.util.IgnoreController();
    Grammar grammar = null;
    if (rngFilePath.endsWith(".rng")) {
      try {
        grammar = RELAXNGReader.parse(rngFilePath, factory, ignoreController);
      } catch (Exception ex) {
        Logger.getLogger(XMLModel.class.getName()).log(Level.SEVERE, null, ex);
      }
    } else if (rngFilePath.endsWith(".xsd")) {
      grammar = XMLSchemaReader.parse(rngFilePath, factory, ignoreController);
    } else {
      throw new RuntimeException("Reader not chosen for given schema suffix!");
    }
    if (grammar == null) {
      throw new RuntimeException("Schema could not be parsed.");
    }
    return grammar;
  }

  /** Writes a grammar to the specified output. */
  public static void writeGrammar(Grammar g, java.io.OutputStream out) throws SAXException {

    RELAXNGWriter writer = new RELAXNGWriter();
    // use XMLSerializer of Apache to serialize SAX event into plain text.
    // OutputFormat specifies "pretty printing".
    writer.setDocumentHandler(new XMLSerializer(out, new OutputFormat("xml", null, true)));
    // visit TREXGrammar and generate its XML representation.
    writer.write(g);
  }

  /**
   * Get all elements, sorted by ns:local name.
   *
   * @return Unmodifiable SortedSet of elements
   */
  public PuzzlePieceSet getElements() {
    return mElements;
  }

  /**
   * Get all attributes, sorted by ns:local name.
   *
   * @return Unmodifiable SortedSet of attributes
   */
  public PuzzlePieceSet getAttributes() {
    return mAttributes;
  }

  /**
   * Get element(s) by tag name. If there are multiple elements sharing the same tag name, a
   * PuzzlePieceSet is returned. If not, a single PuzzlePiece is returned.
   *
   * @param name
   * @return Element PuzzlePiece(s)
   */
  public PuzzleComponent getElement(String name) {
    PuzzlePiece element = mNameElementMap.get(name);
    if (element == null) {
      return null;
    }
    return element.withMultiples();
  }

  /**
   * Get element by tag name and hash code. The hash code distincts elements from sharing the same
   * tag name.
   *
   * @param name
   * @param hashCode
   * @return Element PuzzlePiece
   */
  public PuzzlePiece getElement(String name, int hashCode) {
    PuzzlePiece element = mNameElementMap.get(name);
    if (element == null) {
      return null;
    }
    for (PuzzlePiece def : element.withMultiples()) {
      if (def.hashCode() == hashCode) {
        return def;
      }
    }
    return null;
  }

  /**
   * Get attribute by tag name. If there are multiple attributes sharing the same tag name, a
   * PuzzlePieceSet is returned. If not, a single PuzzlePiece is returned.
   *
   * @param name
   * @return Attribute PuzzlePiece(s)
   */
  public PuzzleComponent getAttribute(String name) {
    PuzzlePiece attribute = mNameAttributeMap.get(name);
    if (attribute == null) {
      return null;
    }
    return attribute.withMultiples();
  }

  /**
   * Get attribute by tag name and hash code. The hash code distincts Attributes sharing the same
   * tag name.
   *
   * @param name
   * @param hashCode
   * @return Attribute PuzzlePiece
   */
  public PuzzlePiece getAttribute(String name, int hashCode) {
    PuzzlePiece attribute = mNameAttributeMap.get(name);
    if (attribute == null) {
      return null;
    }
    for (PuzzlePiece def : attribute.withMultiples()) {
      if (def.hashCode() == hashCode) {
        return def;
      }
    }
    return null;
  }

  /**
   * Convert a-few:words into AFewWords in CamelCase spelling
   *
   * @param raw input String
   * @return filtered output String
   */
  public static String camelCase(String raw) {
    StringTokenizer tok = new StringTokenizer(raw, "-:/ _.,");
    String retval = "";
    while (tok.hasMoreElements()) {
      String word = tok.nextToken();
      if (!word.equals("")) {
        retval += word.substring(0, 1).toUpperCase() + word.substring(1);
      }
    }
    return retval;
  }

  /**
   * Convert a-few:words into AFewWords in CamelCase spelling
   *
   * @param def input
   * @return filtered output String
   */
  public static String camelCase(PuzzleComponent def) {
    return camelCase(def.getQName());
  }

  /**
   * Convert a-few:words into aFewWords in spelling for java method names
   *
   * @param raw input String
   * @return filtered output String
   */
  public static String javaCase(String raw) {
    String retval = camelCase(raw);
    if (retval.length() > 0) {
      retval = retval.substring(0, 1).toLowerCase().concat(retval.substring(1));
    }
    return retval;
  }

  /**
   * Convert a-few:words into aFewWords in spelling for java method names
   *
   * @param def input
   * @return filtered output String
   */
  public static String javaCase(PuzzleComponent def) {
    return javaCase(def.getQName());
  }

  /**
   * Convert a-few:words into A_FEW_WORDS in spelling used for Java constants
   *
   * @param raw input String
   * @return filtered output String
   */
  public static String constantCase(String raw) {
    StringTokenizer tok = new StringTokenizer(raw, "-:/ _.,");
    String retval = "";
    String separator = "";
    while (tok.hasMoreElements()) {
      String word = tok.nextToken();
      if (!word.equals("")) {
        retval += separator + word.toUpperCase();
        separator = "_";
      }
    }
    return retval;
  }

  /**
   * Convert a-few:words into A_FEW_WORDS in spelling used for Java constants
   *
   * @param def input
   * @return filtered output String
   */
  public static String constantCase(PuzzleComponent def) {
    return constantCase(def.getQName());
  }

  /**
   * Assist method for camel-case adaptions or namespace extraction. Maybe not used anymore: Get
   * first word out of a String containing delimiters like "-:/ _.,"
   *
   * @param raw input String
   * @return filtered output String
   */
  public static String firstWord(String raw) {
    StringTokenizer tok = new StringTokenizer(raw, "-:/ _.,");
    if (tok.hasMoreElements()) {
      return tok.nextToken();
    }
    return null;
  }

  /**
   * Maybe not used anymore: Get first word out of a PuzzleComponent object containing delimiters
   * like "-:/ _.,"
   *
   * @param def input
   * @return first word
   */
  public static String firstWord(PuzzleComponent def) {
    return firstWord(def.getQName());
  }

  /**
   * Maybe not used anymore: Get last word out of a String containing delimiters like "-:/ _.,"
   *
   * @param raw input
   * @return last word
   */
  public static String lastWord(String raw) {
    StringTokenizer tok = new StringTokenizer(raw, "-:/ _.,");
    String retval = null;
    while (tok.hasMoreElements()) {
      retval = tok.nextToken();
    }
    return retval;
  }

  /**
   * Maybe not used anymore: Get last word out of a String containing delimiters like "-:/ _.,"
   *
   * @param def input
   * @return last word
   */
  public static String lastWord(PuzzleComponent def) {
    return lastWord(def.getQName());
  }

  /**
   * (Java) member variable may not start with a number, so escape it
   *
   * @param in raw input
   * @return filtered output, starting with a literal
   */
  public static String escapeKeyword(PuzzleComponent in) {
    return escapeKeyword(in.getQName());
  }

  /**
   * (Java) Keyword may not start with a number, so escape it
   *
   * @param in raw input
   * @return filtered output, starting with a literal
   */
  public static String escapeKeyword(String in) {
    if (in == null || in.length() == 0) {
      return in;
    }
    String out = in;
    // Do not start with number
    if (out.substring(0, 1).matches("^\\p{Digit}")) {
      out = "_" + out;
    }
    return out;
  }

  /**
   * Escape the quotation marks of String literals
   *
   * @param in raw input
   * @return filtered output, with escaped quotation marks
   */
  public static String escapeLiteral(PuzzleComponent in) {
    return escapeLiteral(in.getQName());
  }

  /**
   * Escape the quotation marks of String literals
   *
   * @param in raw input
   * @return filtered output, with escaped quotation marks
   */
  public static String escapeLiteral(String in) {
    if (in == null || in.length() == 0) {
      return in;
    }
    String out = in;
    // Escape '"'
    out = out.replaceAll("\"", "\\\\\"");
    return out;
  }

  /**
   * Extract namespace ns from ns:local name
   *
   * @param name in form ns:local
   * @return ns part from ns:local name
   */
  public static String extractNamespacePrefix(String name) {
    int pos = name.lastIndexOf(":");
    if (pos > 0 && pos < name.length() - 1) {
      return name.substring(0, pos);
    } else {
      return null;
    }
  }

  /**
   * Extract namespace ns from ns:local name
   *
   * @param def PuzzleComponent object
   * @return ns part from ns:local name
   */
  public static String extractNamespacePrefix(PuzzleComponent def) {
    return extractNamespacePrefix(def.getQName());
  }

  /**
   * Extract localname local from ns:local name
   *
   * @param name in form ns:local
   * @return local part from ns:local name
   */
  public static String extractLocalName(String name) {
    int pos = name.lastIndexOf(":");
    if (pos > 0 && pos < name.length() - 1) {
      return name.substring(pos + 1);
    } else if (pos < 0 && name.length() > 0) {
      // the name is a local name
      return name;
    } else {
      return null;
    }
  }

  /**
   * Extract localname local from ns:local name
   *
   * @param def PuzzleComponent object
   * @return local part from ns:local name
   */
  public static String extractLocalName(PuzzleComponent def) {
    return extractLocalName(def.getQName());
  }

  /**
   * Identifies heads of islands of grammars, which are multiple used and must be identified to
   * avoid infinite loops when traversing the grammar
   */
  public static Set<Expression> getHeadsOfIslands(Grammar grammar) {
    // collect all reachable ElementExps and ReferenceExps.
    final Set<Expression> nodes = new HashSet<Expression>();
    // ElementExps and ReferenceExps who are referenced more than once.
    final Set<Expression> heads = new HashSet<Expression>();

    grammar
        .getTopLevel()
        .visit(
            new ExpressionWalker() {
              // ExpressionWalker class traverses expressions in depth-first order.
              // So this invokation traverses the all reachable expressions from
              // the top level expression.

              // Whenever visiting elements and RefExps, they are memorized
              // to identify head of islands.
              public void onElement(ElementExp exp) {
                if (nodes.contains(exp)) {
                  heads.add(exp);
                  return; // prevent infinite recursion.
                }
                nodes.add(exp);
                super.onElement(exp);
              }

              public void onRef(ReferenceExp exp) {
                if (nodes.contains(exp)) {
                  heads.add(exp);
                  return; // prevent infinite recursion.
                }
                nodes.add(exp);
                super.onRef(exp);
              }
            });

    return heads;
  }
}

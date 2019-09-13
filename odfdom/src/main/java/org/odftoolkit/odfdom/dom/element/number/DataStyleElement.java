package org.odftoolkit.odfdom.dom.element.number;

import java.util.ArrayList;
import java.util.List;
import org.odftoolkit.odfdom.changes.MapHelper;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;

/**
 *
 * interface of styles that represent the different number formats that are referenced from styles/auto styles with
 * the data-style-name attribute
 *
 */
public abstract class DataStyleElement extends OdfElement{

    public enum TokenType{
        TOKEN_TEXT,
        TOKEN_NUMBER,
        TOKEN_COLOR,
        TOKEN_CURRENCY
    }
    public enum NumberFormatType{
        FORMAT_CURRENCY,
        FORMAT_TEXT,
        FORMAT_PERCENT,
        FORMAT_DATE,
        FORMAT_TIME,
        FORMAT_NUMBER
    }
    static public class StringToken{
        public TokenType type;
        public String text;
        StringToken(String tx, TokenType t){
            text = tx;
            type = t;
        }
    }
    DataStyleElement(OdfFileDom ownerDoc, OdfName elementName) {
        super(ownerDoc, elementName);
    }
    protected String getMapping(StyleMapElement mapElement){
      String mappedResult = "";
      String condition = mapElement.getStyleConditionAttribute();
      String applyStyleName = mapElement.getStyleApplyStyleNameAttribute();
      org.w3c.dom.Node parent = getParentNode();
      if(applyStyleName != null ) {
          DataStyleElement applyStyle = null;
          if( parent instanceof OdfOfficeStyles ) {
              applyStyle = ((OdfOfficeStyles) parent).getAllDataStyles().get(applyStyleName);
          } else {
              applyStyle = ((OdfOfficeAutomaticStyles) parent).getAllDataStyles().get(applyStyleName);
          }
          if(applyStyle != null) {
              String localFormat = applyStyle.getFormat(true);
              if(condition != null && condition.length() >= 9  && condition.startsWith("value()")) {
                  //always starts with "value()"
                  String operator2 = condition.substring(8, 9); // '=', "<>", '<', "<=", '>', ">="
                  int opLength = 1;
                  if(operator2.equals("=") || operator2.equals("<") || operator2.equals(">") ){
                      opLength = 2;
                  }
                  try{
                      double opValue = Double.parseDouble(condition.substring(7 + opLength));
                      if(opValue != 0.) {
                          mappedResult += '[';
                          mappedResult += condition.substring(7, 7 + opLength );
                          mappedResult += Double.toString(opValue);
                          mappedResult += ']';
                      }
                  } catch (NumberFormatException e) {

                  }
              }
              mappedResult += localFormat;
          }
      }
      return mappedResult;
    }
    /**
     * converts a color attribute to a color token of a number format string
     * @param e
     * @return the resulting color token
     */
    public String getColorFromElement(StyleTextPropertiesElement e) {
        //contains e.g. fo:color
        String ret = "";
        String color = e.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "color");
        if(color != null) {
            if(color.equalsIgnoreCase("#0000ff")) {
                ret = "[BLUE]";
            } else if(color.equalsIgnoreCase("#00FF00")) {
                ret = "[GREEN]";
            } else if(color.equalsIgnoreCase("#FF0000")) {
                ret = "[RED]";
            } else if(color.equalsIgnoreCase("#FFFFFF")) {
                ret = "[WHITE]";
            } else if(color.equalsIgnoreCase("#FF00FF")) {
                ret = "[MAGENTA]";
            } else if(color.equalsIgnoreCase("#FFFF00")) {
                ret = "[YELLOW]";
            } else if(color.equalsIgnoreCase("#00FFFF")) {
                ret = "[CYAN]";
            } else if(color.equalsIgnoreCase("#000000")) {
                ret = "[BLACK]";
            }
        }
        return ret;
    }
    /**
     * @param colorToken color name used in number format
     * @return resulting color value or empty string
     */
    static protected String getColorElement( String colorToken ) {
        String ret = "";
        if(colorToken.equalsIgnoreCase("RED")) {
            ret = "#ff0000";
        } else if(colorToken.equalsIgnoreCase("BLUE")) {
            ret = "#0000ff";
        } else if(colorToken.equalsIgnoreCase("GREEN")) {
            ret = "#00ff00";
        } else if(colorToken.equalsIgnoreCase("WHITE")) {
            ret = "#ffffff";
        } else if(colorToken.equalsIgnoreCase("MAGENTA")) {
            ret = "#ff00ff";
        } else if(colorToken.equalsIgnoreCase("YELLOW")) {
            ret = "#ffff00";
        } else if(colorToken.equalsIgnoreCase("CYAN")) {
            ret = "#00ffff";
        } else if(colorToken.equalsIgnoreCase("BLACK")) {
            ret = "#000000";
        }
        return ret;
    }
    /**
     * creates tokens from a number format
     * @param format
     * @return tokens to be converted to OdfElements
     *
     * TODO: at first only detecting currencies -
     *
     */
    static protected List<StringToken> tokenize(String format, NumberFormatType type){
        ArrayList<StringToken> tokens = new ArrayList<StringToken>();
        boolean hasNumber = false; // only one number token can be created, next one will be part of a text token
        String currentTextToken = "";
        for(int pos = 0; pos < format.length(); ++pos){
            char c = format.charAt(pos);
            if(c == '"'){
                //add all characters until the next quotation to the current text token
                currentTextToken += c;
                while(pos < format.length() - 1  ){
                    c = format.charAt(pos + 1);
                    if(c == '\\'){
                        currentTextToken += c;
                        if((pos > format.length() - 2))
                            break; //invalid!
                        currentTextToken += c;
                        currentTextToken += format.charAt(pos + 2);
                        ++pos;
                    } else if(c == '"' ){
                        currentTextToken += c;
                        ++pos;
                        break;
                    } else {
                        currentTextToken += c;
                    }
                    ++pos;
                }
            } else if(c == '['){
                if(!currentTextToken.isEmpty()){
                    tokens.add(new StringToken(currentTextToken, TokenType.TOKEN_TEXT));
                    currentTextToken = "";
                }
                int closePos = format.indexOf(']', pos);
                if(closePos < 0 || closePos < pos + 2){ // minimum two characters inside, closing bracket not quoted
                    break; // invalid
                }
                String bracketToken = format.substring(pos, closePos + 1);
                if(tokens.isEmpty() && !getColorElement(bracketToken.substring(1, bracketToken.length() - 1)).isEmpty()){
                    tokens.add(new StringToken(bracketToken, TokenType.TOKEN_COLOR));
                } else if(bracketToken.charAt(1) == '$'){
                    tokens.add(new StringToken(bracketToken, TokenType.TOKEN_CURRENCY));
                } else {
                    break; // invalid
                }
                pos = closePos;

            } else if(!hasNumber && (c == '#' || c == '0' || c == '.' )){
                if(!currentTextToken.isEmpty()){
                    tokens.add(new StringToken(currentTextToken, TokenType.TOKEN_TEXT));
                    currentTextToken = "";
                }
                final int numPos = pos;
                while(++pos < format.length()){
                    // TODO: hashes can only be at the beginning interrupted by comma
                    c = format.charAt(pos);
                    if((c != '#' && c != '.' && c != ',' && c != '0') || (pos == (format.length() - 1))) {
                        String numberToken = format.substring(numPos, pos + 1);
                        if(numberToken.charAt(numberToken.length() - 1) == '.'){
                            //add replacement chars
                            int spacePos = format.indexOf(' ', pos-1);
                            int bracketPos = format.indexOf('[', pos-1);
                            if(spacePos < 0) {
                                spacePos = bracketPos;
                            } else if(bracketPos < 0){
                                bracketPos = spacePos;
                            }
                            spacePos = Math.min(spacePos, bracketPos);
                            if(spacePos < 0){ // if not found add the rest of the format TODO: Are there other delimiters than space and bracket? ?
                                spacePos = format.length();
                            }
                            if(spacePos > 0){
                                numberToken += format.substring(pos, spacePos );
                                pos = spacePos;
                            }
                        }
                        tokens.add(new StringToken(numberToken, TokenType.TOKEN_NUMBER));
                        break;
                    }
                }
            } else if( c == '\\'){
                //add this and the next character to the text token
                currentTextToken += c;
                if(pos > format.length() - 2){
                    break;//invalid
                }
                currentTextToken += format.charAt(pos + 1);
                ++pos;
            } else {
                currentTextToken += c;
            }
        }
        if(!currentTextToken.isEmpty()){
            tokens.add(new StringToken(currentTextToken, TokenType.TOKEN_TEXT));
        }
        return tokens;
    }
    protected void emitTokens(List<StringToken> tokens, NumberFormatType type){

        for( StringToken token : tokens){
            switch(token.type) {
                case TOKEN_CURRENCY:
                {
                    if(NumberFormatType.FORMAT_CURRENCY == type){
                        emitCurrency(token.text);
                    } else {
                        emitText(token.text);
                    }
                }
                break;
                case TOKEN_TEXT:
                    emitText(token.text);
                break;
                case TOKEN_COLOR:
                {
                    emitColor(token.text);
                }
                break;
                case TOKEN_NUMBER:
                {
                	if(NumberFormatType.FORMAT_PERCENT == type && token.text.endsWith("%")) {
                		emitNumber(token.text.substring(0, token.text.length() - 1), true);
                		final NumberTextElement numberText = new NumberTextElement((OdfFileDom)this.getOwnerDocument());
                		numberText.setTextContent("%");
                		this.appendChild(numberText);
                	}
                	else if(NumberFormatType.FORMAT_CURRENCY == type) {
                		emitNumber(token.text, true);
                	}
                	else {
                		emitNumber(token.text, false);
                	}
                }
                break;
            }
        }
    }
    protected void emitCurrency(String currencyToken){
        String innerText = currencyToken.substring(1, currencyToken.length() - 1);
        String currencySymbol = "";
        String languageCode = "";
        if(innerText.startsWith("$") && innerText.length() > 1) {
            int dashPos = innerText.indexOf("-");
            currencySymbol = innerText.substring(1, dashPos);
            if(dashPos > 0){
                languageCode = innerText.substring(dashPos + 1);
            }
        }
        if(!currencySymbol.isEmpty()) {
            OdfFileDom dom = (OdfFileDom) this.getOwnerDocument();
             NumberCurrencySymbolElement cSymbol = new NumberCurrencySymbolElement(dom);
             String locale = MapHelper.getLocaleFromLangCode(languageCode);
             if(!locale.isEmpty()) {
                 int dashPos = locale.indexOf("-");
                 cSymbol.setAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "number:language", locale.substring(0, dashPos));
                 if(dashPos > 0) {
                     cSymbol.setAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "number:country", locale.substring(dashPos + 1));
                 }
             }
             cSymbol.setTextContent(currencySymbol);
             this.appendChild(cSymbol);
         }
    }
    protected void emitColor(String colorToken){
        String color = getColorElement(colorToken.substring(1, colorToken.length() - 1));
        if(!color.isEmpty()) {
            OdfFileDom dom = (OdfFileDom) this.getOwnerDocument();
            StyleTextPropertiesElement cProperties = new StyleTextPropertiesElement(dom);
            cProperties.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:color", color);
            this.appendChild(cProperties);
        }
    }
    protected void emitNumber(String numberToken, boolean forceCreateDecimalPlaces){
        OdfFileDom dom = (OdfFileDom) this.getOwnerDocument();
        NumberNumberElement number = new NumberNumberElement(dom);
        /* Process part before the decimal point (if any) */
        int nDigits = 0;
        char ch;
        int pos;
        for (pos = 0; pos < numberToken.length()
                && (ch = numberToken.charAt(pos)) != '.'; pos++) {
            if (ch == ',') {
                number.setNumberGroupingAttribute(true);
            } else if (ch == '0') {
                nDigits++;
            }
        }
        number.setNumberMinIntegerDigitsAttribute(nDigits);

        /* Number of decimal places is the length after the decimal */
        if (pos < numberToken.length()) {
            number.setNumberDecimalPlacesAttribute(numberToken.length() - (pos + 1));
            if(pos < numberToken.length() - 1 && numberToken.charAt(pos + 1) != '0'){
                number.setNumberDecimalReplacementAttribute(numberToken.substring(pos + 1));
            }
        }
        else if(forceCreateDecimalPlaces) {
            number.setNumberDecimalPlacesAttribute(0);
        }
        this.appendChild(number);
    }
    /**
     *  Place pending text into a &lt;number:text&gt; element.
     * @param textBuffer pending text
     */
    protected void emitText(String textBuffer) {
        NumberTextElement textElement;
        if (!textBuffer.equals("")) {
            textElement = new NumberTextElement((OdfFileDom) this.getOwnerDocument());
            textElement.setTextContent(textBuffer);
            this.appendChild(textElement);
        }
    }

    public String getNumberFormat() {
        String result = "";
        NumberNumberElement number = OdfElement.findFirstChildNode(NumberNumberElement.class, this);
        boolean isGroup = number.getNumberGroupingAttribute();
        int decimalPos = (number.getNumberDecimalPlacesAttribute() == null) ? 0
                : number.getNumberDecimalPlacesAttribute().intValue();
        int minInt = (number.getNumberMinIntegerDigitsAttribute() == null) ? 0
                : number.getNumberMinIntegerDigitsAttribute().intValue();
        String decimalReplacement = number.getNumberDecimalReplacementAttribute();
        int i;
        if( minInt == 0){
            result = "#";
        }
        for (i = 0; i < minInt; i++) {
            if (((i + 1) % 3) == 0 && isGroup) {
                result = ",0" + result;
            } else {
                result = "0" + result;
            }
        }
        while (isGroup && (result.indexOf(',') == -1)) {
            if (((i + 1) % 3) == 0 && isGroup) {
                result = "#,#" + result;
            } else {
                result = "#" + result;
            }
            i++;
        }

        if(decimalReplacement != null){
            result += '.' + decimalReplacement;
        } else if (decimalPos > 0) {
            result += ".";
            for (i = 0; i < decimalPos; i++) {
                result += "0";
            }
        }
        return result;
    }
    /**
     * Get the format string that represents this style.
     * @param caps use capitals
     * @return the format string
     */
    public abstract String getFormat(boolean caps);

    /**
     * Get the format string that represents this style.
     * Uses capitals by default
     * @return the format string
     */
    public String getFormat() {
        return getFormat(Boolean.FALSE);
    }

    /**
     * Get the format string that represents this style.
     * @param format the format string
     * @return the format string
     */
    public abstract void setFormat(String format);
}

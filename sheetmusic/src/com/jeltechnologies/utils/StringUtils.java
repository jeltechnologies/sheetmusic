package com.jeltechnologies.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

    private static final int ENDLESSLOOP_THRESHOLD = 100000;

    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzÃƒÂ¥ÃƒÂ¤ÃƒÂ¶ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789Ãƒâ€¦Ãƒâ€“Ãƒâ€ž<>/.,-+=()@!:;&'\"";

    public static final String NUMBERS = "0123456789";

    public static final String PUNCTUATION = ",.:;!?";

    public static final String SENTENCE_SPLITTER = ".!?";

    public static final String[] MONTHS_SWEDISH = new String[] { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };

    private static final String MANGLE_URL_TOKENS = "abcdefghijklmnopqrstuvwxyz0123456789.:/-_";
    private static final String MANGLE_TOKENS_URL = "e.56jdqu3_/:tar-bw0s9fkil1mpxz7ocvy8hn42g";

    public static final BigDecimal HUNDRED = new BigDecimal(100);
    public static MathContext PERCENTAGE_ROUNDING = new MathContext(3, RoundingMode.HALF_UP);

    private static final HashMap<Character, String> htmlEncodeChars = new HashMap<>();
    static {

	// Special characters for HTML
	htmlEncodeChars.put('\u0026', "&amp;");
	htmlEncodeChars.put('\u003C', "&lt;");
	htmlEncodeChars.put('\u003E', "&gt;");
	htmlEncodeChars.put('\u0022', "&quot;");

	htmlEncodeChars.put('\u0152', "&OElig;");
	htmlEncodeChars.put('\u0153', "&oelig;");
	htmlEncodeChars.put('\u0160', "&Scaron;");
	htmlEncodeChars.put('\u0161', "&scaron;");
	htmlEncodeChars.put('\u0178', "&Yuml;");
	htmlEncodeChars.put('\u02C6', "&circ;");
	htmlEncodeChars.put('\u02DC', "&tilde;");
	htmlEncodeChars.put('\u2002', "&ensp;");
	htmlEncodeChars.put('\u2003', "&emsp;");
	htmlEncodeChars.put('\u2009', "&thinsp;");
	htmlEncodeChars.put('\u200C', "&zwnj;");
	htmlEncodeChars.put('\u200D', "&zwj;");
	htmlEncodeChars.put('\u200E', "&lrm;");
	htmlEncodeChars.put('\u200F', "&rlm;");
	htmlEncodeChars.put('\u2013', "&ndash;");
	htmlEncodeChars.put('\u2014', "&mdash;");
	htmlEncodeChars.put('\u2018', "&lsquo;");
	htmlEncodeChars.put('\u2019', "&rsquo;");
	htmlEncodeChars.put('\u201A', "&sbquo;");
	htmlEncodeChars.put('\u201C', "&ldquo;");
	htmlEncodeChars.put('\u201D', "&rdquo;");
	htmlEncodeChars.put('\u201E', "&bdquo;");
	htmlEncodeChars.put('\u2020', "&dagger;");
	htmlEncodeChars.put('\u2021', "&Dagger;");
	htmlEncodeChars.put('\u2030', "&permil;");
	htmlEncodeChars.put('\u2039', "&lsaquo;");
	htmlEncodeChars.put('\u203A', "&rsaquo;");
	htmlEncodeChars.put('\u20AC', "&euro;");

	// Character entity references for ISO 8859-1 characters
	htmlEncodeChars.put('\u00A0', "&nbsp;");
	htmlEncodeChars.put('\u00A1', "&iexcl;");
	htmlEncodeChars.put('\u00A2', "&cent;");
	htmlEncodeChars.put('\u00A3', "&pound;");
	htmlEncodeChars.put('\u00A4', "&curren;");
	htmlEncodeChars.put('\u00A5', "&yen;");
	htmlEncodeChars.put('\u00A6', "&brvbar;");
	htmlEncodeChars.put('\u00A7', "&sect;");
	htmlEncodeChars.put('\u00A8', "&uml;");
	htmlEncodeChars.put('\u00A9', "&copy;");
	htmlEncodeChars.put('\u00AA', "&ordf;");
	htmlEncodeChars.put('\u00AB', "&laquo;");
	htmlEncodeChars.put('\u00AC', "&not;");
	htmlEncodeChars.put('\u00AD', "&shy;");
	htmlEncodeChars.put('\u00AE', "&reg;");
	htmlEncodeChars.put('\u00AF', "&macr;");
	htmlEncodeChars.put('\u00B0', "&deg;");
	htmlEncodeChars.put('\u00B1', "&plusmn;");
	htmlEncodeChars.put('\u00B2', "&sup2;");
	htmlEncodeChars.put('\u00B3', "&sup3;");
	htmlEncodeChars.put('\u00B4', "&acute;");
	htmlEncodeChars.put('\u00B5', "&micro;");
	htmlEncodeChars.put('\u00B6', "&para;");
	htmlEncodeChars.put('\u00B7', "&middot;");
	htmlEncodeChars.put('\u00B8', "&cedil;");
	htmlEncodeChars.put('\u00B9', "&sup1;");
	htmlEncodeChars.put('\u00BA', "&ordm;");
	htmlEncodeChars.put('\u00BB', "&raquo;");
	htmlEncodeChars.put('\u00BC', "&frac14;");
	htmlEncodeChars.put('\u00BD', "&frac12;");
	htmlEncodeChars.put('\u00BE', "&frac34;");
	htmlEncodeChars.put('\u00BF', "&iquest;");
	htmlEncodeChars.put('\u00C0', "&Agrave;");
	htmlEncodeChars.put('\u00C1', "&Aacute;");
	htmlEncodeChars.put('\u00C2', "&Acirc;");
	htmlEncodeChars.put('\u00C3', "&Atilde;");
	htmlEncodeChars.put('\u00C4', "&Auml;");
	htmlEncodeChars.put('\u00C5', "&Aring;");
	htmlEncodeChars.put('\u00C6', "&AElig;");
	htmlEncodeChars.put('\u00C7', "&Ccedil;");
	htmlEncodeChars.put('\u00C8', "&Egrave;");
	htmlEncodeChars.put('\u00C9', "&Eacute;");
	htmlEncodeChars.put('\u00CA', "&Ecirc;");
	htmlEncodeChars.put('\u00CB', "&Euml;");
	htmlEncodeChars.put('\u00CC', "&Igrave;");
	htmlEncodeChars.put('\u00CD', "&Iacute;");
	htmlEncodeChars.put('\u00CE', "&Icirc;");
	htmlEncodeChars.put('\u00CF', "&Iuml;");
	htmlEncodeChars.put('\u00D0', "&ETH;");
	htmlEncodeChars.put('\u00D1', "&Ntilde;");
	htmlEncodeChars.put('\u00D2', "&Ograve;");
	htmlEncodeChars.put('\u00D3', "&Oacute;");
	htmlEncodeChars.put('\u00D4', "&Ocirc;");
	htmlEncodeChars.put('\u00D5', "&Otilde;");
	htmlEncodeChars.put('\u00D6', "&Ouml;");
	htmlEncodeChars.put('\u00D7', "&times;");
	htmlEncodeChars.put('\u00D8', "&Oslash;");
	htmlEncodeChars.put('\u00D9', "&Ugrave;");
	htmlEncodeChars.put('\u00DA', "&Uacute;");
	htmlEncodeChars.put('\u00DB', "&Ucirc;");
	htmlEncodeChars.put('\u00DC', "&Uuml;");
	htmlEncodeChars.put('\u00DD', "&Yacute;");
	htmlEncodeChars.put('\u00DE', "&THORN;");
	htmlEncodeChars.put('\u00DF', "&szlig;");
	htmlEncodeChars.put('\u00E0', "&agrave;");
	htmlEncodeChars.put('\u00E1', "&aacute;");
	htmlEncodeChars.put('\u00E2', "&acirc;");
	htmlEncodeChars.put('\u00E3', "&atilde;");
	htmlEncodeChars.put('\u00E4', "&auml;");
	htmlEncodeChars.put('\u00E5', "&aring;");
	htmlEncodeChars.put('\u00E6', "&aelig;");
	htmlEncodeChars.put('\u00E7', "&ccedil;");
	htmlEncodeChars.put('\u00E8', "&egrave;");
	htmlEncodeChars.put('\u00E9', "&eacute;");
	htmlEncodeChars.put('\u00EA', "&ecirc;");
	htmlEncodeChars.put('\u00EB', "&euml;");
	htmlEncodeChars.put('\u00EC', "&igrave;");
	htmlEncodeChars.put('\u00ED', "&iacute;");
	htmlEncodeChars.put('\u00EE', "&icirc;");
	htmlEncodeChars.put('\u00EF', "&iuml;");
	htmlEncodeChars.put('\u00F0', "&eth;");
	htmlEncodeChars.put('\u00F1', "&ntilde;");
	htmlEncodeChars.put('\u00F2', "&ograve;");
	htmlEncodeChars.put('\u00F3', "&oacute;");
	htmlEncodeChars.put('\u00F4', "&ocirc;");
	htmlEncodeChars.put('\u00F5', "&otilde;");
	htmlEncodeChars.put('\u00F6', "&ouml;");
	htmlEncodeChars.put('\u00F7', "&divide;");
	htmlEncodeChars.put('\u00F8', "&oslash;");
	htmlEncodeChars.put('\u00F9', "&ugrave;");
	htmlEncodeChars.put('\u00FA', "&uacute;");
	htmlEncodeChars.put('\u00FB', "&ucirc;");
	htmlEncodeChars.put('\u00FC', "&uuml;");
	htmlEncodeChars.put('\u00FD', "&yacute;");
	htmlEncodeChars.put('\u00FE', "&thorn;");
	htmlEncodeChars.put('\u00FF', "&yuml;");

	// Mathematical, Greek and Symbolic characters for HTML
	htmlEncodeChars.put('\u0192', "&fnof;");
	htmlEncodeChars.put('\u0391', "&Alpha;");
	htmlEncodeChars.put('\u0392', "&Beta;");
	htmlEncodeChars.put('\u0393', "&Gamma;");
	htmlEncodeChars.put('\u0394', "&Delta;");
	htmlEncodeChars.put('\u0395', "&Epsilon;");
	htmlEncodeChars.put('\u0396', "&Zeta;");
	htmlEncodeChars.put('\u0397', "&Eta;");
	htmlEncodeChars.put('\u0398', "&Theta;");
	htmlEncodeChars.put('\u0399', "&Iota;");
	htmlEncodeChars.put('\u039A', "&Kappa;");
	htmlEncodeChars.put('\u039B', "&Lambda;");
	htmlEncodeChars.put('\u039C', "&Mu;");
	htmlEncodeChars.put('\u039D', "&Nu;");
	htmlEncodeChars.put('\u039E', "&Xi;");
	htmlEncodeChars.put('\u039F', "&Omicron;");
	htmlEncodeChars.put('\u03A0', "&Pi;");
	htmlEncodeChars.put('\u03A1', "&Rho;");
	htmlEncodeChars.put('\u03A3', "&Sigma;");
	htmlEncodeChars.put('\u03A4', "&Tau;");
	htmlEncodeChars.put('\u03A5', "&Upsilon;");
	htmlEncodeChars.put('\u03A6', "&Phi;");
	htmlEncodeChars.put('\u03A7', "&Chi;");
	htmlEncodeChars.put('\u03A8', "&Psi;");
	htmlEncodeChars.put('\u03A9', "&Omega;");
	htmlEncodeChars.put('\u03B1', "&alpha;");
	htmlEncodeChars.put('\u03B2', "&beta;");
	htmlEncodeChars.put('\u03B3', "&gamma;");
	htmlEncodeChars.put('\u03B4', "&delta;");
	htmlEncodeChars.put('\u03B5', "&epsilon;");
	htmlEncodeChars.put('\u03B6', "&zeta;");
	htmlEncodeChars.put('\u03B7', "&eta;");
	htmlEncodeChars.put('\u03B8', "&theta;");
	htmlEncodeChars.put('\u03B9', "&iota;");
	htmlEncodeChars.put('\u03BA', "&kappa;");
	htmlEncodeChars.put('\u03BB', "&lambda;");
	htmlEncodeChars.put('\u03BC', "&mu;");
	htmlEncodeChars.put('\u03BD', "&nu;");
	htmlEncodeChars.put('\u03BE', "&xi;");
	htmlEncodeChars.put('\u03BF', "&omicron;");
	htmlEncodeChars.put('\u03C0', "&pi;");
	htmlEncodeChars.put('\u03C1', "&rho;");
	htmlEncodeChars.put('\u03C2', "&sigmaf;");
	htmlEncodeChars.put('\u03C3', "&sigma;");
	htmlEncodeChars.put('\u03C4', "&tau;");
	htmlEncodeChars.put('\u03C5', "&upsilon;");
	htmlEncodeChars.put('\u03C6', "&phi;");
	htmlEncodeChars.put('\u03C7', "&chi;");
	htmlEncodeChars.put('\u03C8', "&psi;");
	htmlEncodeChars.put('\u03C9', "&omega;");
	htmlEncodeChars.put('\u03D1', "&thetasym;");
	htmlEncodeChars.put('\u03D2', "&upsih;");
	htmlEncodeChars.put('\u03D6', "&piv;");
	htmlEncodeChars.put('\u2022', "&bull;");
	htmlEncodeChars.put('\u2026', "&hellip;");
	htmlEncodeChars.put('\u2032', "&prime;");
	htmlEncodeChars.put('\u2033', "&Prime;");
	htmlEncodeChars.put('\u203E', "&oline;");
	htmlEncodeChars.put('\u2044', "&frasl;");
	htmlEncodeChars.put('\u2118', "&weierp;");
	htmlEncodeChars.put('\u2111', "&image;");
	htmlEncodeChars.put('\u211C', "&real;");
	htmlEncodeChars.put('\u2122', "&trade;");
	htmlEncodeChars.put('\u2135', "&alefsym;");
	htmlEncodeChars.put('\u2190', "&larr;");
	htmlEncodeChars.put('\u2191', "&uarr;");
	htmlEncodeChars.put('\u2192', "&rarr;");
	htmlEncodeChars.put('\u2193', "&darr;");
	htmlEncodeChars.put('\u2194', "&harr;");
	htmlEncodeChars.put('\u21B5', "&crarr;");
	htmlEncodeChars.put('\u21D0', "&lArr;");
	htmlEncodeChars.put('\u21D1', "&uArr;");
	htmlEncodeChars.put('\u21D2', "&rArr;");
	htmlEncodeChars.put('\u21D3', "&dArr;");
	htmlEncodeChars.put('\u21D4', "&hArr;");
	htmlEncodeChars.put('\u2200', "&forall;");
	htmlEncodeChars.put('\u2202', "&part;");
	htmlEncodeChars.put('\u2203', "&exist;");
	htmlEncodeChars.put('\u2205', "&empty;");
	htmlEncodeChars.put('\u2207', "&nabla;");
	htmlEncodeChars.put('\u2208', "&isin;");
	htmlEncodeChars.put('\u2209', "&notin;");
	htmlEncodeChars.put('\u220B', "&ni;");
	htmlEncodeChars.put('\u220F', "&prod;");
	htmlEncodeChars.put('\u2211', "&sum;");
	htmlEncodeChars.put('\u2212', "&minus;");
	htmlEncodeChars.put('\u2217', "&lowast;");
	htmlEncodeChars.put('\u221A', "&radic;");
	htmlEncodeChars.put('\u221D', "&prop;");
	htmlEncodeChars.put('\u221E', "&infin;");
	htmlEncodeChars.put('\u2220', "&ang;");
	htmlEncodeChars.put('\u2227', "&and;");
	htmlEncodeChars.put('\u2228', "&or;");
	htmlEncodeChars.put('\u2229', "&cap;");
	htmlEncodeChars.put('\u222A', "&cup;");
	htmlEncodeChars.put('\u222B', "&int;");
	htmlEncodeChars.put('\u2234', "&there4;");
	htmlEncodeChars.put('\u223C', "&sim;");
	htmlEncodeChars.put('\u2245', "&cong;");
	htmlEncodeChars.put('\u2248', "&asymp;");
	htmlEncodeChars.put('\u2260', "&ne;");
	htmlEncodeChars.put('\u2261', "&equiv;");
	htmlEncodeChars.put('\u2264', "&le;");
	htmlEncodeChars.put('\u2265', "&ge;");
	htmlEncodeChars.put('\u2282', "&sub;");
	htmlEncodeChars.put('\u2283', "&sup;");
	htmlEncodeChars.put('\u2284', "&nsub;");
	htmlEncodeChars.put('\u2286', "&sube;");
	htmlEncodeChars.put('\u2287', "&supe;");
	htmlEncodeChars.put('\u2295', "&oplus;");
	htmlEncodeChars.put('\u2297', "&otimes;");
	htmlEncodeChars.put('\u22A5', "&perp;");
	htmlEncodeChars.put('\u22C5', "&sdot;");
	htmlEncodeChars.put('\u2308', "&lceil;");
	htmlEncodeChars.put('\u2309', "&rceil;");
	htmlEncodeChars.put('\u230A', "&lfloor;");
	htmlEncodeChars.put('\u230B', "&rfloor;");
	htmlEncodeChars.put('\u2329', "&lang;");
	htmlEncodeChars.put('\u232A', "&rang;");
	htmlEncodeChars.put('\u25CA', "&loz;");
	htmlEncodeChars.put('\u2660', "&spades;");
	htmlEncodeChars.put('\u2663', "&clubs;");
	htmlEncodeChars.put('\u2665', "&hearts;");
	htmlEncodeChars.put('\u2666', "&diams;");
    }

    public static String encodeHtml(String source) {
	return encode(source, htmlEncodeChars);
    }

    private static String encode(String source, HashMap<Character, String> encodingTable) {
	if (null == source) {
	    return null;
	}

	if (null == encodingTable) {
	    return source;
	}

	StringBuilder encoded_string = null;
	char[] string_to_encode_array = source.toCharArray();
	int last_match = -1;
	int difference = 0;

	for (int i = 0; i < string_to_encode_array.length; i++) {
	    char char_to_encode = string_to_encode_array[i];

	    if (encodingTable.containsKey(char_to_encode)) {
		if (null == encoded_string) {
		    encoded_string = new StringBuilder(source.length());
		}
		difference = i - (last_match + 1);
		if (difference > 0) {
		    encoded_string.append(string_to_encode_array, last_match + 1, difference);
		}
		encoded_string.append(encodingTable.get(char_to_encode));
		last_match = i;
	    }
	}

	if (null == encoded_string) {
	    return source;
	} else {
	    difference = string_to_encode_array.length - (last_match + 1);
	    if (difference > 0) {
		encoded_string.append(string_to_encode_array, last_match + 1, difference);
	    }
	    return encoded_string.toString();
	}
    }

    public static String formatNumber(Object object) {
	DecimalFormat numberFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
	numberFormatter.setGroupingSize(3);
	return numberFormatter.format(object);
    }

    public static String cropAfter(Object lineObject, int afterPos, boolean addColumnHint) {
	String result;
	if (lineObject == null) {
	    result = "null";
	} else {
	    String line = lineObject.toString();
	    if (line.length() > afterPos) {
		result = line.substring(0, afterPos);
		if (addColumnHint) {
		    result = result + "...";
		}
	    } else {
		result = line;
	    }
	}
	return result;
    }

    public static String replaceEach(String value, char[] specialChars, String[] replacements) {
	StringBuilder resultBuilder = new StringBuilder();
	for (int charIndex = 0; charIndex < value.length(); charIndex++) {
	    char c = value.charAt(charIndex);
	    int replacementIndex = -1;

	    for (int specialCharIndex = 0; specialCharIndex < specialChars.length && replacementIndex == -1; specialCharIndex++) {
		if (specialChars[specialCharIndex] == c) {
		    replacementIndex = specialCharIndex;
		}
	    }

	    if (replacementIndex == -1) {
		resultBuilder.append(c);
	    } else {
		resultBuilder.append(replacements[replacementIndex]);
	    }
	}
	return resultBuilder.toString();
    }

    public static String stripControlChars(String html) {
	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < html.length(); i++) {
	    char c = html.charAt(i);
	    if (c >= ' ') {
		builder.append(c);
	    }
	}
	String strippedHtml = builder.toString();
	return strippedHtml;
    }

    public static String stripNumbers(String html) {
	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < html.length(); i++) {
	    char c = html.charAt(i);
	    if (c < '0' || c > '9') {
		builder.append(c);
	    }
	}
	String strippedHtml = builder.toString();
	return strippedHtml;
    }

    public static String stripNonAscii(String html) {
	StringBuilder builder = new StringBuilder();
	boolean lastTokenWasSpace = false;
	for (int i = 0; i < html.length(); i++) {
	    char c = html.charAt(i);
	    if (c > ' ' && c < 255) {
		builder.append(c);
		lastTokenWasSpace = false;
	    } else {
		if ((c == '\r') || (c == '\n') || (c == ' ') || (c == '\t')) {
		    if (!lastTokenWasSpace) {
			builder.append(' ');
		    }
		    lastTokenWasSpace = true;
		}
	    }
	}
	String strippedHtml = builder.toString();
	return strippedHtml;
    }

    public static String stripDoubleSpaces(String html) {
	StringBuilder builder = new StringBuilder();
	boolean lastCharWasSpace = false;
	for (int i = 0; i < html.length(); i++) {
	    char c = html.charAt(i);
	    if (c >= ' ') {
		boolean skip = (c == ' ' && lastCharWasSpace);
		if (!skip) {
		    builder.append(c);
		}
		lastCharWasSpace = c == ' ';
	    }
	}
	String strippedHtml = builder.toString();
	return strippedHtml;
    }

    public static String removeSHOUTING(String original) {
	// Make sure to remove all none ascii things
	StringBuilder allowedCharBuilder = new StringBuilder();
	for (int i = 0; i < original.length(); i++) {
	    char currentToken = original.charAt(i);
	    boolean allowed = ALLOWED_CHARS.indexOf(currentToken) > -1;
	    if (allowed) {
		allowedCharBuilder.append(currentToken);
	    } else {
		allowedCharBuilder.append(' ');
	    }
	}
	String allowedString = allowedCharBuilder.toString();

	// remove double spaces, !! and replace ! with .
	StringBuilder resultBuilder = new StringBuilder();
	char lastToken = ' ';
	for (int i = 0; i < allowedString.length(); i++) {
	    char currentToken = allowedString.charAt(i);
	    boolean skip = (lastToken == '!' && currentToken == '!') || (lastToken == ' ' && currentToken == ' ');
	    if (!skip) {
		if (currentToken == '!') {
		    resultBuilder.append(".");
		} else {
		    resultBuilder.append(currentToken);
		}
	    }
	    lastToken = currentToken;
	}
	String removedExcl = resultBuilder.toString();

	// remove CAPITALS
	String spaceTaggedOriginal = removedExcl.replaceAll(">", "> ");
	StringBuilder normalCaseBuilder = new StringBuilder();
	StringTokenizer tokenizer = new StringTokenizer(spaceTaggedOriginal);
	boolean first = true;
	while (tokenizer.hasMoreElements()) {
	    String currentWord = tokenizer.nextToken();
	    if (!currentWord.equals("")) {
		if (currentWord.length() > 1) {
		    char firstChar = currentWord.charAt(0);
		    char secondChar = currentWord.charAt(1);
		    boolean firstUpperCase = firstChar >= 'A' && firstChar <= 'Z';
		    boolean secondUpperCase = secondChar >= 'A' && secondChar <= 'Z';
		    if (firstUpperCase && secondUpperCase) {
			String sentence = normalCaseBuilder.toString();
			boolean newSentence = first || sentence.endsWith(".") || sentence.endsWith("!") || sentence.endsWith("<br/>");
			if (newSentence) {
			    currentWord = firstChar + currentWord.substring(1).toLowerCase();
			} else {
			    currentWord = currentWord.toLowerCase();
			}
		    }
		}
		if (first) {
		    first = false;
		} else {
		    normalCaseBuilder.append(" ");
		}
		normalCaseBuilder.append(currentWord);
	    }
	}
	// remove the spaces after tags again
	String result = normalCaseBuilder.toString().replaceAll("> ", ">");

	return result.toString();
    }

    public static String stripHtmlAndSpaces(String html) {
	String strippedHtml = stripNonAscii(html);
	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < strippedHtml.length(); i++) {
	    char c = strippedHtml.charAt(i);
	    if (c > ' ') {
		builder.append(c);
	    }
	}
	return builder.toString();
    }

    /**
     * In a string find text between two strings, the start and end string.
     * <p>
     * This method starts searching at start and then finds the end.
     * 
     * @param searchIn the text to search in
     * @param start    the start of the text to search between
     * @param end      the end of the text to search between
     * @return the text between start and end, without start and end itself
     */
    public static String findBetween(String searchIn, String start, String end) {
	String result = "";
	int startPos = searchIn.indexOf(start);
	if (startPos > -1) {
	    int endPos = searchIn.indexOf(end, startPos + start.length());
	    if (endPos > -1) {
		result = searchIn.substring(startPos + start.length(), endPos);
	    }
	}
	return result;
    }

    /**
     * In a string find text between two strings, the start and end string.
     * 
     * @param searchIn      the text to search in
     * @param start         the start of the text to search between
     * @param end           the end of the text to search between
     * @param searchAtStart start search at start (true) or at end (false)
     * @return the text between start and end, without start and end itself
     */
    public static String findBetween(String searchIn, String start, String end, boolean searchAtStart) {
	if (searchAtStart) {
	    return findBetween(searchIn, start, end);
	} else {
	    String result = "";
	    // search from end
	    int endPos = searchIn.indexOf(end);
	    if (endPos > -1) {
		int currentPos = endPos;
		boolean found = false;
		while (currentPos >= 0 && !found) {
		    String currentString = searchIn.substring(currentPos, endPos);
		    if (currentString.startsWith(start)) {
			found = true;
		    } else {
			currentPos--;
		    }
		}
		if (found) {
		    result = searchIn.substring(currentPos + start.length(), endPos);
		}
	    }
	    return result;
	}
    }

    /**
     * Find all texts between two strings, the start and end string.
     * 
     * @param searchIn the text to search in
     * @param start    the start of the text to search between
     * @param end      the end of the text to search between
     * @return a List of Strings containing all occurances between start and end, but without start and end itself.
     */
    public static List<String> findAllBetween(String searchIn, String start, String end, boolean includeStartAndEnd) {
	List<String> result = new ArrayList<String>();
	if (searchIn != null) {
	    int startPos = 0;
	    int counter = 0;
	    while (startPos > -1 && startPos < searchIn.length()) {
		startPos = searchIn.indexOf(start, startPos);
		if (startPos > -1) {
		    int endPos = searchIn.indexOf(end, startPos + start.length());
		    if (endPos > -1) {
			String between = searchIn.substring(startPos + start.length(), endPos);
			if (includeStartAndEnd) {
			    between = start + between + end;
			}
			result.add(between);
			startPos = endPos;
		    } else {
			startPos++; // to prevent endless loop
		    }
		}
		counter++;
		if (counter > ENDLESSLOOP_THRESHOLD) {
		    String errorMessage = "Endless loop found in findAllBetween counter: " + counter + ", searchIn: [" + searchIn + "], start: [" + start
			    + "], end: [" + end + "]";
		    LOGGER.error(errorMessage);
		    throw new RuntimeException(errorMessage);
		}
	    }
	}
	return result;
    }

    public static boolean isEmpty(String in) {
	boolean result = false;
	if (in == null) {
	    result = true;
	} else {
	    String trimmed = in.trim();
	    result = trimmed.length() == 0;
	}
	return result;
    }

    public static List<String> findAllBetween(String searchIn, String start, String end) {
	return findAllBetween(searchIn, start, end, false);
    }

    public static String stripAfter(String in, String after) {
	String result;
	int startStrip = in.indexOf(after);
	if (startStrip == 0) {
	    result = "";
	} else {
	    if (startStrip < 0) {
		result = in;
	    } else {
		result = in.substring(0, startStrip);
	    }
	}
	return result;
    }

    public static String stripBefore(String in, String before) {
	String result;
	int startStrip = in.indexOf(before);

	if (startStrip < 0) {
	    result = in;
	} else {
	    result = in.substring(startStrip + before.length());
	}
	return result;
    }

    public static boolean isEmptyTrimmed(String in) {
	boolean result;
	if (in == null) {
	    result = true;
	} else {
	    String trimmed = in.trim();
	    result = trimmed.isEmpty();
	}
	return result;
    }

    public static String stripBeforeLast(String in, String after) {
	String result;
	int lastPos = in.lastIndexOf(after);
	if (lastPos > -1 && lastPos < (in.length() - 1)) {
	    result = in.substring(lastPos + 1);
	} else {
	    result = "";
	}
	return result;
    }

    public static String stripAfterLast(String in, String after) {
	String result;
	int lastPos = in.lastIndexOf(after);
	if (lastPos > -1 && lastPos < (in.length() - 1)) {
	    result = in.substring(0, lastPos);
	} else {
	    result = "";
	}
	return result;
    }

    public static String findAfter(String in, String after) {
	String result;
	int startAfter = in.indexOf(after);
	if (startAfter > -1) {
	    result = in.substring(startAfter + after.length());
	} else {
	    result = "";
	}
	return result;
    }

    public static String findAfterLast(String in, String after) {
	String result;
	int startAfter = in.lastIndexOf(after);
	if (startAfter > -1) {
	    result = in.substring(startAfter + after.length());
	} else {
	    result = "";
	}
	return result;
    }

    public static String findAfterIfNotFoundReturnIn(String in, String after) {
	String result;
	int startAfter = in.indexOf(after);
	if (startAfter > -1) {
	    result = in.substring(startAfter + after.length());
	} else {
	    result = in;
	}
	return result;
    }

    public static String findAfterLastIfNotFoundReturnIn(String in, String after) {
	String result;
	int startAfter = in.lastIndexOf(after);
	if (startAfter > -1) {
	    result = in.substring(startAfter + after.length());
	} else {
	    result = in;
	}
	return result;
    }

    public static String stripAllOccurances(String in, String from, String to) {
	String result = in;
	int startPos = 0;
	int endPos = -1;
	while (startPos > -1) {
	    startPos = result.indexOf(from, startPos);
	    if (startPos > -1) {
		endPos = result.indexOf(to, startPos);
		if (endPos > -1) {
		    String newResult = result.substring(0, startPos) + result.substring(endPos + to.length());
		    result = newResult;
		    startPos++;
		} else {
		    startPos++;
		}
	    }
	}
	return result;
    }

    public static String stripAllOccurances(String in, String textToStrip) {
	String result;
	if (in == null) {
	    result = "";
	} else {
	    if (textToStrip == null || textToStrip.isEmpty()) {
		result = in;
	    } else {
		StringBuilder builder = new StringBuilder();
		int pos = 0;
		boolean found = true;
		int loopCounter = 0;
		while (found) {
		    int foundPos = in.indexOf(textToStrip, pos);
		    if (foundPos == -1) {
			found = false;
		    } else {
			builder.append(in.substring(pos, foundPos));
			pos = foundPos + textToStrip.length();
		    }
		    loopCounter++;
		    if (loopCounter > ENDLESSLOOP_THRESHOLD) {
			throw new RuntimeException("Internal error, endless loop detected, crash hard");
		    }
		}
		builder.append(in.substring(pos));
		result = builder.toString();
	    }
	}
	return result;
    }

    public static String stripChar(String in, char tokenToStrip) {
	StringBuilder builder = new StringBuilder();
	for (int pos = 0; pos < in.length(); pos++) {
	    char c = in.charAt(pos);
	    if (c != tokenToStrip) {
		builder.append(c);
	    }
	}
	return builder.toString();
    }
    
    public static String stripCharsNotIn(String in, String charsToRemove) {
	StringBuilder builder = new StringBuilder();
	for (int pos = 0; pos < in.length(); pos++) {
	    char c = in.charAt(pos);
	    if (charsToRemove.indexOf(c) > -1) {
		builder.append(c);
	    }
	}
	return builder.toString();
    }

    public static List<String> splitIntoSentences(String in) {
	List<String> sentences = new ArrayList<String>();
	if (in != null) {
	    StringBuilder builder = new StringBuilder();
	    int pos = 0;
	    int length = in.length();
	    int lengthMinOne = length - 1;
	    while (pos < length) {
		char c = in.charAt(pos);
		builder.append(c);
		if (SENTENCE_SPLITTER.indexOf(c) > -1) {
		    if (pos < lengthMinOne) {
			char nextChar = in.charAt(pos + 1);
			if (NUMBERS.indexOf(nextChar) == -1) {
			    String sentence = builder.toString().trim();
			    if (sentence.length() > 1) {
				sentences.add(sentence);
			    }
			    builder = new StringBuilder();
			}
		    }
		}
		pos++;
	    }
	    String sentence = builder.toString().trim();
	    if (sentence.length() > 1) {
		sentences.add(sentence);
	    }
	}
	return sentences;
    }

    public static String fixPunctuation(String in) {
	StringBuilder builder = new StringBuilder();
	int pos = 0;
	int length = in.length();
	int lengthMinOne = length - 1;
	while (pos < length) {
	    char c = in.charAt(pos);
	    builder.append(c);
	    if (PUNCTUATION.indexOf(c) > -1) {
		if (pos < lengthMinOne) {
		    char nextChar = in.charAt(pos + 1);
		    if (NUMBERS.indexOf(nextChar) == -1) {
			builder.append(" ");
		    }
		}
	    }
	    pos++;
	}
	String withoutDoubleSpaces = stripDoubleSpaces(builder.toString());
	return withoutDoubleSpaces;
    }

    public static String fixFirstCharUpperCase(String in) {
	StringBuilder builder = new StringBuilder();
	if (in != null) {
	    boolean nextTokenMustBeUpper = true;
	    for (int pos = 0; pos < in.length(); pos++) {
		char c = in.charAt(pos);
		String s = String.valueOf(c);
		if (nextTokenMustBeUpper) {
		    builder.append(s.toUpperCase());
		} else {
		    builder.append(s.toLowerCase());
		}
		nextTokenMustBeUpper = (c == ' ') || (c == '-');
	    }
	}
	return builder.toString().trim();
    }

    public static String stripTagsFromParagraph(String in) {
	StringBuilder builder = new StringBuilder();
	boolean inTag = false;
	int pos = 0;
	while (pos < in.length()) {
	    char c = in.charAt(pos);
	    boolean mustBeAdded;
	    if (inTag) {
		if (c == '>') {
		    inTag = false;
		}
		mustBeAdded = false;
	    } else {
		if (c == '<') {
		    inTag = true;
		    mustBeAdded = false;
		} else {
		    mustBeAdded = true;
		}
	    }
	    if (mustBeAdded) {
		builder.append(c);
	    }
	    pos++;
	}

	return builder.toString();
    }

    public static String stripSpaces(String in) {
	StringBuilder builder = new StringBuilder();
	int pos = 0;
	while (pos < in.length()) {
	    char c = in.charAt(pos);
	    if (c != ' ') {
		builder.append(c);
	    }
	    pos++;
	}
	return builder.toString();
    }

    public static String decodeURL(String in) {
	String result;
	try {
	    result = URLDecoder.decode(in, "UTF-8");
	} catch (Exception exception) {
	    result = in;
	    LOGGER.debug("Ignoring URL decode for [" + "]", exception);
	}
	return result;
    }

    public static String encodeURL(String in) {
	String result;
	try {
	    result = URLEncoder.encode(in, "UTF-8");
	} catch (Exception exception) {
	    result = in;
	    LOGGER.debug("Ignoring URL encode for [" + "]", exception);
	}
	return result;
    }

    public static String replaceAll(String in, char searchFor, char replaceBy) {
	return replaceAll(in, searchFor, String.valueOf(replaceBy));
    }

    public static String replaceAll(String in, char searchFor, String replaceBy) {
	StringBuilder builder = new StringBuilder();
	if (in != null) {
	    int pos = 0;
	    while (pos < in.length()) {
		char c = in.charAt(pos);
		if (c == searchFor) {
		    builder.append(replaceBy);
		} else {
		    builder.append(c);
		}
		pos++;
	    }
	}
	return builder.toString();
    }

//    public static String replaceAllOld(String in, String searchFor, String replaceBy) {
//	String current = in;
//	int currentPos = 0;
//	int startPos = current.indexOf(searchFor);
//	int counter = 0;
//	while (startPos >= 0) {
//	    startPos = current.indexOf(searchFor, currentPos);
//	    if (startPos >= 0) {
//		int endPos = startPos + searchFor.length();
//		if (endPos >= current.length()) {
//		    endPos = current.length();
//		}
//		current = current.substring(0, startPos) + replaceBy + current.substring(endPos);
//		currentPos = endPos;
//	    }
//	    counter++;
//	    if (counter > ENDLESSLOOP_THRESHOLD) {
//		String errorMessage = "Endless loop found in replaceAll counter: " + counter + ", in: [" + in + "], searchFor: [" + searchFor + "], replaceBy: [" + replaceBy + "]";
//		LOGGER.error(errorMessage);
//		throw new RuntimeException(errorMessage);
//	    }
//	}
//	return current;
//    }

    public static String replaceAll(String in, String searchFor, String replaceBy) {
	String result = in.replaceAll(searchFor, replaceBy);
	return result;
    }

    public static String addEscapes(String value) {
	StringBuilder result = new StringBuilder();
	for (int i = 0; i < value.length(); i++) {
	    char c = value.charAt(i);
	    String r;
	    switch (c) {
		case '\b':
		    r = "\\b";
		    break;
		case '\f':
		    r = "\\f";
		    break;
		case '\n':
		    r = "\\n";
		    break;
		case '\r':
		    r = "\\r";
		    break;
		case '\t':
		    r = "\\t";
		    break;
		case '"':
		    r = "\\\"";
		    break;
		case '\\':
		    r = "\\\\";
		    break;
		default:
		    r = String.valueOf(c);
	    }
	    result.append(r);
	}
	return result.toString();
    }

    /**
     * Take a string and add all numbers. Ignore everything after , or . just like the (int) in Java.
     * 
     * @param in
     * @return
     */
    public static String stripToIntegerString(String in) {
	if (in == null) {
	    return "";
	} else {
	    StringBuilder builder = new StringBuilder();
	    boolean fractionFound = false;
	    for (int pos = 0; !fractionFound && pos < in.length(); pos++) {
		char c = in.charAt(pos);
		if (c == '.' || c == ',') {
		    fractionFound = true;
		} else {
		    if (NUMBERS.indexOf(c) > -1) {
			builder.append(c);
		    }
		}
	    }
	    return builder.toString();
	}
    }

    /**
     * Take a string and add all numbers. Ignore everything after , or . just like the (int) in Java.
     * 
     * @param in
     * @return
     */
    public static int stripToInteger(String in) {
	String integerString = stripToIntegerString(in);
	return Integer.parseInt(integerString);
    }

    public static String upperFirstChar(String in) {
	String result;
	if (in == null || in.isEmpty()) {
	    result = "";
	} else {
	    String first = in.substring(0, 1);
	    StringBuilder resultBuilder = new StringBuilder();
	    resultBuilder.append(first.toUpperCase());
	    if (in.length() > 1) {
		resultBuilder.append(in.substring(1).toLowerCase());
	    }
	    result = resultBuilder.toString();
	}
	return result;
    }

    public static String mangleURL(String url) {
	StringBuilder builder = new StringBuilder();
	String urlNoExtension = stripAfterLast(url, ".");
	String extension = "." + stripBeforeLast(url, ".");

	for (int pos = 0; pos < urlNoExtension.length(); pos++) {
	    char c = urlNoExtension.charAt(pos);
	    int tokenPos = MANGLE_URL_TOKENS.indexOf(c);
	    if (tokenPos < 0) {
		throw new RuntimeException("Token not found: '" + c + "'");
	    }
	    builder.append(MANGLE_TOKENS_URL.charAt(tokenPos));
	}
	builder.append(extension);
	String encoded = builder.toString();
	return encoded;
    }

    public static String demangleURL(String mangledURL) {
	String decoded = mangledURL;
	String urlNoExtension = stripAfterLast(decoded, ".");
	String extension = "." + stripBeforeLast(decoded, ".");

	StringBuilder builder = new StringBuilder();
	for (int pos = 0; pos < urlNoExtension.length(); pos++) {
	    char c = urlNoExtension.charAt(pos);
	    int urlPos = MANGLE_TOKENS_URL.indexOf(c);
	    if (urlPos < 0) {
		throw new RuntimeException("Token not found: '" + c + "'");
	    }
	    builder.append(MANGLE_URL_TOKENS.charAt(urlPos));
	}
	builder.append(extension);
	return builder.toString();
    }

    public static String toMilliseconds(long startTimeNano, long endTimeNano) {
	long elapsedTime = endTimeNano - startTimeNano;
	long milliseconds = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
	return milliseconds + " ms";
    }

    public static String getNameWithPercentage(String name, int value, int total) {
	return getNameWithPercentage(name, value, new BigDecimal(total));
    }

    public static String getNameWithPercentage(String name, int value, BigDecimal total) {
	StringBuilder builder = new StringBuilder(name);
	if (value != 0) {
	    BigDecimal percentage = new BigDecimal(value).divide(total, PERCENTAGE_ROUNDING).multiply(HUNDRED);
	    builder.append(" (").append(new DecimalFormat("#.##").format(percentage)).append("%)");
	} else {
	    builder.append("0 (0%)");
	}
	return builder.toString();
    }

    public static int toInt(String s, int fallback) {
	int i;
	try {
	    i = Integer.valueOf(s);
	} catch (NumberFormatException nfe) {
	    i = fallback;
	}
	return i;
    }

    public static List<String> splitToLines(String in) {
	List<String> lines = new ArrayList<String>();
	if (in != null) {
	    StringBuilder builder = new StringBuilder();
	    for (int pos = 0; pos < in.length(); pos++) {
		char c = in.charAt(pos);
		if (c == '\n') {
		    lines.add(builder.toString());
		    builder = new StringBuilder();
		} else {
		    if (c != '\r') {
			builder.append(c);
		    }
		}
	    }
	    String lastLine = builder.toString();
	    if (!lastLine.isEmpty()) {
		lines.add(lastLine);
	    }
	}
	return lines;
    }

    public static String getValueEmptyIfNull(String in) {
	String result;
	if (in == null) {
	    result = "";
	} else {
	    result = in;
	}
	return result;
    }

    public static String dup(int length, char c) {
	StringBuilder b = new StringBuilder();
	for (int i = 0; i < length; i++) {
	    b.append(c);
	}
	return b.toString();
    }

    public static String dup(int length, String s) {
	StringBuilder b = new StringBuilder();
	for (int i = 0; i < length; i++) {
	    b.append(s);
	}
	return b.toString();
    }

    public static List<String> split(String in, char devider) {
	List<String> lines = new ArrayList<String>();
	if (in != null) {
	    StringBuilder builder = new StringBuilder();
	    for (int pos = 0; pos < in.length(); pos++) {
		char c = in.charAt(pos);
		if (c == devider) {
		    if (builder.length() > 0) {
			lines.add(builder.toString());
			builder = new StringBuilder();
		    }
		} else {
		    builder.append(c);
		}
	    }
	    String lastLine = builder.toString();
	    if (!lastLine.isEmpty()) {
		lines.add(lastLine);
	    }
	}
	return lines;
    }

    public static boolean containsValue(String s) {
	return s != null && !s.trim().equals("");
    }
}

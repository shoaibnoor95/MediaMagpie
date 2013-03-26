package de.wehner.mediamagpie.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final String[] LABELS = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z" };

    public static List<String> getParametersAsStringList(Map<String, String[]> parameterMap) {
        return Arrays.asList(getParametersAsStrings(parameterMap));
    }

    public static String[] getParametersAsStrings(Map<String, String[]> parameterMap) {
        Set<String> keySet = parameterMap.keySet();
        String[] parameters = new String[keySet.size()];
        int i = 0;
        for (String key : keySet) {
            StringBuilder builder = new StringBuilder();
            builder.append(key);
            builder.append(" : ");
            String[] values = parameterMap.get(key);
            builder.append(Arrays.asList(values));
            parameters[i] = builder.toString();
            i++;
        }
        return parameters;
    }

    public static String getStackTraceAsString(Throwable exception) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(baos));
        return new String(baos.toByteArray());
    }

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;

    public static String formatBytesToHumanReadableRepresentation(final long value) {
        final long[] dividers = new long[] { T, G, M, K, 1 };
        final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
        if (value < 1)
            throw new IllegalArgumentException("Invalid file size: " + value);
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    public static String formatTimeDuration(long timeDuration) {
        StringBuilder builder = new StringBuilder();
        long hours = timeDuration / (60 * 60 * 1000);
        long rem = (timeDuration % (60 * 60 * 1000));
        long minutes = rem / (60 * 1000);
        rem = rem % (60 * 1000);
        long seconds = rem / 1000;

        if (hours != 0) {
            builder.append(hours);
            builder.append(" hrs, ");
        }
        if (minutes != 0) {
            builder.append(minutes);
            builder.append(" mins, ");
        }
        // return "0sec if no difference
        builder.append(seconds);
        builder.append(" sec");
        return builder.toString();
    }

    public static String interpolateString(String value, Map<Object, Object> properties) {
        String result = value;
        String nestedPattern = "\\$\\{[\\S]+?\\}";

        Pattern pattern = Pattern.compile(nestedPattern);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int i = 0; i <= groupCount; i++) {
                String toReplace = matcher.group();
                String key = toReplace.substring(2, toReplace.length() - 1);
                Object property = properties.get(key);
                result = replaceString(result, toReplace, "" + property);
            }
        }
        return result;
    }

    private static String replaceString(String source, String pattern, String replace) {
        int start = 0;
        int end = 0;
        StringBuffer result = new StringBuffer();

        while ((end = source.indexOf(pattern, start)) >= 0) {
            result.append(source.substring(start, end));
            result.append(replace);
            start = end + pattern.length();
        }
        result.append(source.substring(start));
        return result.toString();
    }

    public static String getDefaultColumnLabel(int columnIndex) {
        String label = "";
        if (columnIndex < 26) {
            label = LABELS[columnIndex];
        } else {
            String firstChar = LABELS[(int) (Math.floor((double) columnIndex / 26) - 1)];
            String secondChar = LABELS[columnIndex % 26];
            label = firstChar + secondChar;
        }
        return label;
    }

    public static String[] getDefaultColumnLabels(int number) {
        String[] labels = new String[number];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = getDefaultColumnLabel(i);
        }
        return labels;
    }

    public static String join(List<? extends Object> objects, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (Object object : objects) {
            if (builder.length() != 0) {
                builder.append(delimiter);
            }
            builder.append(object);
        }
        return builder.toString();
    }

    /**
     * Converters a string into a list of strings, were the break criteria is the comma or new line character. The incoming string will be
     * cut into parts, each part will be trimmed by the <code>String.trim()</code> method and each element that is not empty it will be
     * pushed into the return array.
     * 
     * <pre>
     * Input:      Output:
     * null         -> {}
     * ""           -> {}
     * " ,"         -> {}
     * "a,b"        -> {"a", "b"}
     * "a\n\rb"     -> {"a", "b"}
     * "a,\nb,c"    -> {"a", "b", "c"}
     * " a,   b,  " -> {"a", "b"}
     * </pre>
     * 
     * @param separateStr
     *            The comma separated string that must be split
     * @return A String collection contains non empty Strings
     */
    public static Collection<String> createListFromCommaAndLineFeedSeparatedString(String separateStr) {
        if (separateStr == null || separateStr.trim().length() == 0) {
            return new ArrayList<String>(0);
        }

        // splitt input string into parts
        String[] strArray = separateStr.split("[,\n]");
        Collection<String> ret = new ArrayList<String>(strArray.length);
        for (String element : strArray) {
            String pureElement = element.trim();

            if (pureElement.length() > 0) {
                ret.add(pureElement);
            }
        }

        return ret;
    }

    /**
     * Split a string and remove empty lines.
     * 
     * @param string
     * @param regex
     * @return
     */
    public static String[] split(String string, String regex) {
        List<String> splitList = new ArrayList<String>();
        String[] splits = string.split(regex);
        for (String split : splits) {
            if (split.length() > 0) {
                splitList.add(split);
            }
        }
        return splitList.toArray(new String[splitList.size()]);
    }

    // rwe: see Strings.emptyToNull() in guava lib
    // public static String emptyToNull(String string) {
    // if (isEmpty(string)) {
    // return null;
    // }
    // return string;
    // }
    //
    public static boolean containsIgnoreCase(Collection<String> collection, String test) {
        for (String s : collection) {
            if (s.equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves a sequence of control character as given from a web ui imput field into a String object. The normal 1:1 copy is sometimes
     * not usable, because quote characters will be interpreted as normal characters. E.g. the user types the string <code>'\t'</code> into
     * an input field which is stored in spring's command objects as a two character string <code>'\'</code> and <code>'t'</code>.<br/>
     * For example the method maps:
     * 
     * <pre>
     * string in UI's input field   - chars in cmd obj. -> return string
     * -----------------------------------------------------------------
     * "\t"                         - '\','t'          -> "\t"
     * "\n"                         - '\','n'          -> "\n"
     * "\r"                         - '\','r'          -> "\r"
     * ""\t""                       - '"','\','t','"'  -> "\t"
     * " "                          - ' '              -> " "
     * ";"                          - ';'              -> ";"
     * "\n\r"                       - '\','n','\','r'  -> "\n\r"
     * </pre>
     * 
     * @param uiPattern
     *            The string from springs command object
     * @return the resolved String.
     */
    public static String resolveControlCharsFromWebUi(String uiPattern) {
        if (uiPattern == null || uiPattern.length() <= 1) {
            return uiPattern;
        }
        int length = uiPattern.length();

        // remove optional '"' from beginning and end
        if (uiPattern.charAt(0) == '"' && uiPattern.charAt(length - 1) == '"') {
            uiPattern = uiPattern.substring(1, length - 1);
        }
        StringBuilder resolved = new StringBuilder(uiPattern.length());
        boolean quoted = false;
        for (int i = 0; i < uiPattern.length(); i++) {
            char c = uiPattern.charAt(i);
            if (quoted) {
                switch (c) {
                case 't':
                    resolved.append("\t");
                    break;
                case 'n':
                    resolved.append("\n");
                    break;
                case 'r':
                    resolved.append("\r");
                    break;
                default:
                    resolved.append('\\').append(c);
                }
                quoted = false;
            } else {
                if (c == '\\') {
                    // user typed the quote character
                    quoted = true;
                } else {
                    resolved.append(c);
                }
            }
        }

        if (resolved.length() == 0 && quoted == true) {
            return "\\";
        }

        return resolved.toString();
    }

    /**
     * This is the inverse method to {@link #resolveControlCharsFromWebUi(String)} and maps some control chars into separate chars that can
     * be displayed in html input fields.<br/>
     * For example the method maps:
     * 
     * <pre>
     * parameter   - return chars
     * ---------------------------
     * "\t"     -> '\','t'
     * "\n"     -> '\','n'
     * "\r"     -> '\','r'
     * " "      -> ' '
     * ";"      -> ';'
     * "\n\r"   -> '\','n','\','r'
     * "abc\t"  -> 'a','b','c','\','r'
     * </pre>
     * 
     * @param controlPattern
     *            The control characters as stored in entities.
     * @return The string representation that is able to display in html input fields.
     */
    public static String resolveControlCharsToWebUi(String controlPattern) {
        if (controlPattern == null) {
            return null;
        }

        StringBuilder resolved = new StringBuilder(controlPattern.length() * 2);
        for (int i = 0; i < controlPattern.length(); i++) {
            char c = controlPattern.charAt(i);
            switch (c) {
            case '\t':
                resolved.append("\\t");
                break;
            case '\n':
                resolved.append("\\n");
                break;
            case '\r':
                resolved.append("\\r");
                break;
            default:
                resolved.append(c);
            }
        }
        return resolved.toString();
    }
}

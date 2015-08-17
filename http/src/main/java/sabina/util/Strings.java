/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.util;

import static java.lang.System.getProperty;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Things.stringOf;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Several string utilities not included in the JDK. Some are in other libraries like Guava or
 * Commons Lang.
 *
 * <p>I implement them because the previously mentioned libraries just duplicate some functions
 * with modern JREs.
 *
 * @author jam
 */
public final class Strings {
    /** Runtime specific end of line. */
    public  static final String EOL = getProperty ("line.separator");

    /** Variable prefix for string filtering. */
    private static final String VARIABLE_PREFIX = "${";
    /** Variable sufix for string filtering. */
    private static final String VARIABLE_SUFFIX = "}";

    /**
     * Calls {@link #filter(String, Entry[])} converting the map in entries.
     *
     * @see #filter(String, Entry[])
     */
    public static String filter (final String text, final Map<?, ?> parameters) {
        checkArgument (parameters != null);
        Set<? extends Entry<?, ?>> entries = parameters.entrySet ();
        return filter (text, entries.toArray (new Entry[entries.size ()]));
    }

    /**
     * Filters a text substituting each key by its value. The keys format is:
     * <code>${key}</code> and all occurrences are replaced by the supplied value.
     *
     * <p>If a variable does not have a parameter, it is left as it is.
     *
     * @param text The text to filter. Can not be 'null'.
     * @param parameters The map with the list of key/value tuples. Can not be 'null'.
     * @return The filtered text or the same string if no values are passed or found in the text.
     */
    public static String filter (final String text, final Entry<?, ?>... parameters) {
        checkArgument (text != null);
        checkArgument (parameters != null);

        String result = text;

        for (Entry<?, ?> parameter : parameters) {
            Object k = parameter.getKey ();
            Object v = parameter.getValue ();
            checkArgument (k != null);
            checkArgument (v != null, "'%s' value is 'null'", k);

            String key = stringOf (k);
            checkArgument (!isEmpty (key), "key with '%s' value is empty", v);
            String value = stringOf (v);

            result = result.replace (VARIABLE_PREFIX + key + VARIABLE_SUFFIX, value);
        }

        return result;
    }

    /**
     * Utility method to check if a string has a value.
     *
     * @param text String to check.
     * @return True if the string is 'null' or empty.
     */
    public static boolean isEmpty (String text) {
        return text == null || text.isEmpty ();
    }

    /**
     * Only a wrapper to avoid having to catch the checked exception.
     *
     * @see String#String(byte[], String)
     */
    public static String encode (final byte[] data, final String encoding) {
        try {
            return new String (data, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Only a wrapper to avoid having to catch the checked exception.
     *
     * @see String#getBytes(String)
     */
    public static byte[] decode (final String text, final String encoding) {
        try {
            return text.getBytes (encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Repeat a given text a certain number of times.
     *
     * @param text String to repeat. It can't be 'null'.
     * @param times Number of times to repeat the text. Must be greater than 0.
     * @return The passed text repeated the given times.
     */
    public static String repeat (String text, int times) {
        checkArgument (text != null);
        checkArgument (times >= 0);

        StringBuilder buffer = new StringBuilder (text.length () * times);

        for (int ii = 0; ii < times; ii++)
            buffer.append (text);

        return buffer.toString ();
    }

    /**
     * Indents every line with the given padding the number of times specified.
     *
     * @param text Text to indent. Can't be 'null'.
     * @param padding String to repeat at the beginning of each line. Can't be 'null'.
     * @param times Number of times to repeat the padding text. Must be greater than 0.
     * @return Text with every line indented with the given padding the number of times specified.
     */
    public static String indent (final String text, final String padding, final int times) {
        checkArgument (text != null);

        String[] lines = text.split (EOL, -1);
        String appendString = repeat (padding, times);
        StringBuilder buffer = new StringBuilder ();

        for (int ii = 0; ii < lines.length - 1; ii++)
            buffer.append (appendString).append (lines[ii]).append (EOL);

        return buffer.append (appendString).append (lines[lines.length - 1]).toString ();
    }

    static void _create () { new Strings (); }

    private Strings () {
        throw new IllegalStateException ();
    }
}

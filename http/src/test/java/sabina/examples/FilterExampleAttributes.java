/*
 * Copyright © 2011 Per Wendel. All rights reserved.
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

package sabina.examples;

import static java.lang.String.format;
import static java.lang.System.out;

import sabina.Application;

/**
 * Example showing the use of attributes.
 *
 * @author Per Wendel
 */
final class FilterExampleAttributes extends Application {
    FilterExampleAttributes () {
        get ("/hi", it -> {
            it.attribute ("foo", "bar");
            return "hi";
        });

        after ("/hi", it -> {
            for (String attr : it.attributes ())
                out.println ("attr: " + attr);
        });

        after ("/hi", it -> {
            Object foo = it.attribute ("foo");
            it.body (format ("<%s>%s</%s>", "foo", foo, "foo"));
        });

        start ();
    }

    public static void main (String... args) {
        new FilterExampleAttributes ();
    }
}

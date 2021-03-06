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

import sabina.Application;

/**
 * Example showing the use of wildcards in paths. The filter will match any path starting with
 * '/protected/'. And the remaining path will be available through the request parameter.
 *
 * @author Per Wendel
 */
final class FilterExampleWildcard extends Application {
    FilterExampleWildcard () {
        before ("/protected/*", it -> it.halt (401, "Go Away!"));
        start ();
    }

    public static void main (String[] args) {
        new FilterExampleWildcard ();
    }
}

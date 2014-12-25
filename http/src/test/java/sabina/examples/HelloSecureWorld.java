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

import static sabina.Server.*;

import sabina.Server;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
class HelloSecureWorld {
    public static void main (String[] args) {
        Server server = server (
            get ("/hello", it -> "Hello Secure World!")
        );

        server.setSecure (args[0], args[1], null, null);
        server.startUp ();
    }
}
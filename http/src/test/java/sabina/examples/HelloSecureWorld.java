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
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
final class HelloSecureWorld extends Application {
    HelloSecureWorld (String keystorePath, String keystorePassword) {
        get ("/hello", it -> "Hello Secure World!");

        secure (keystorePath, keystorePassword);
        start ();
    }

    public static void main (String[] args) {
        new HelloSecureWorld (args[0], args[1]);
    }
}

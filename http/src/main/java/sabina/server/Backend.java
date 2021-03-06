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

package sabina.server;

/**
 * @author Per Wendel
 */
public interface Backend {
    /**
     * Ignites the Sabina server, listening on the specified port, running SSL secured with the
     * specified keystore and truststore. If truststore is null, keystore is reused.
     *
     * @param host The address to listen on.
     * @param port The port.
     * @param keystoreFile The keystore file location as string.
     * @param keystorePassword The password for the keystore.
     * @param truststoreFile Truststore file location as string, leave null to reuse keystore.
     * @param truststorePassword The trust store password.
     * @param staticFilesRoute The route to static files in classPath.
     * @param externalFilesLocation The route to static files external to classPath.
     */
    void startUp (
        String host, int port,
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword,
        String staticFilesRoute, String externalFilesLocation);

    /**
     * Stops the Sabina server.
     */
    void shutDown ();
}

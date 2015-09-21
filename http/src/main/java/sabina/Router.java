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

package sabina;

import static sabina.HttpMethod.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import sabina.route.RouteMatcher;

/**
 * Trait to support router methods in classes with access to a RouteMatcher.
 *
 * @author jamming
 */
public interface Router {
    /** This is just a "type alias". */
    interface Handler extends Function<Request, Object> {}
    /** This is just a "type alias". */
    interface VoidHandler extends Consumer<Request> {}

    RouteMatcher getMatcher ();

    /**
     * Parses, validates and adds a route
     */
    default void addRoute (Route action) { getMatcher ().processRoute (action); }

    default void add (HttpMethod m, Handler h) {
        addRoute (new Route (m, h));
    }

    default void add (HttpMethod m, String p, Handler h) {
        addRoute (new Route (m, p, h));
    }

    default void add (HttpMethod m, String p, String ct, Handler h) {
        addRoute (new Route (m, p, ct, h));
    }

    default Handler wrap (VoidHandler h) {
        return request -> {
            h.accept (request);
            return "";
        };
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing.
     *
     * @param exception the exception class.
     * @param h The handler.
     * @param <T> Exception type.
     */
    default <T extends Exception> void exception (Class<T> exception, BiConsumer<T, Request> h) {
        Fault<?> wrapper = new Fault<> (exception, h);
        getMatcher ().processFault (wrapper);
    }

    /*
     * Filters
     */
    default void after (VoidHandler h) { add (AFTER, wrap (h)); }
    default void before (VoidHandler h) { add (BEFORE, wrap (h)); }
    default void after (String p, VoidHandler h) { add (AFTER, p, wrap (h)); }
    default void before (String p, VoidHandler h) { add (BEFORE, p, wrap (h)); }
    default void after (String p, String ct, VoidHandler h) { add (AFTER, p, ct, wrap (h)); }
    default void before (String p, String ct, VoidHandler h) { add (BEFORE, p, ct, wrap (h)); }

    /*
     * Routes
     */
    default void delete (String p, Handler h) { add (DELETE, p, h); }
    default void delete (String p, VoidHandler h) { delete (p, wrap (h)); }
    default void get (String p, Handler h) { add (GET, p, h); }
    default void get (String p, VoidHandler h) { get (p, wrap (h)); }
    default void head (String p, Handler h) { add (HEAD, p, h); }
    default void head (String p, VoidHandler h) { head (p, wrap (h)); }
    default void options (String p, Handler h) { add (OPTIONS, p, h); }
    default void options (String p, VoidHandler h) { options (p, wrap (h)); }
    default void patch (String p, Handler h) { add (PATCH, p, h); }
    default void patch (String p, VoidHandler h) { patch (p, wrap (h)); }
    default void post (String p, Handler h) { add (POST, p, h); }
    default void post (String p, VoidHandler h) { post (p, wrap (h)); }
    default void put (String p, Handler h) { add (PUT, p, h); }
    default void put (String p, VoidHandler h) { put (p, wrap (h)); }
    default void trace (String p, Handler h) { add (TRACE, p, h); }
    default void trace (String p, VoidHandler h) { trace (p, wrap (h)); }
    default void delete (String p, String ct, Handler h) { add (DELETE, p, ct, h); }
    default void delete (String p, String ct, VoidHandler h) { delete (p, ct, wrap (h)); }
    default void get (String p, String ct, Handler h) { add (GET, p, ct, h); }
    default void get (String p, String ct, VoidHandler h) { get (p, ct, wrap (h)); }
    default void head (String p, String ct, Handler h) { add (HEAD, p, ct, h); }
    default void head (String p, String ct, VoidHandler h) { head (p, ct, wrap (h)); }
    default void options (String p, String ct, Handler h) { add (OPTIONS, p, ct, h); }
    default void options (String p, String ct, VoidHandler h) { options (p, ct, wrap (h)); }
    default void patch (String p, String ct, Handler h) { add (PATCH, p, ct, h); }
    default void patch (String p, String ct, VoidHandler h) { patch (p, ct, wrap (h)); }
    default void post (String p, String ct, Handler h) { add (POST, p, ct, h); }
    default void post (String p, String ct, VoidHandler h) { post (p, ct, wrap (h)); }
    default void put (String p, String ct, Handler h) { add (PUT, p, ct, h); }
    default void put (String p, String ct, VoidHandler h) { put (p, ct, wrap (h)); }
    default void trace (String p, String ct, Handler h) { add (TRACE, p, ct, h); }
    default void trace (String p, String ct, VoidHandler h) { trace (p, ct, wrap (h)); }
}

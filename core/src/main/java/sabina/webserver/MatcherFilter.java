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

package sabina.webserver;

import static java.lang.System.currentTimeMillis;
import static java.util.logging.Logger.getLogger;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static sabina.HttpMethod.after;
import static sabina.HttpMethod.before;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sabina.*;
import sabina.route.RouteMatch;
import sabina.route.RouteMatcher;
import sabina.route.RouteMatcherFactory;

/**
 * Filter for matching of filters and routes.
 *
 * @author Per Wendel
 */
class MatcherFilter implements Filter {
    private static final Logger LOG = getLogger (MatcherFilter.class.getName ());

    private static final String
        ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept",
        INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>",
        NOT_FOUND =
            "<html><body>" +
                "<h2>404 Not found</h2>The requested route [%s] has not been mapped in Spark" +
                "</body></html>";

    public final RouteMatcher routeMatcher;
    public final boolean isServletContext, hasOtherHandlers;

    boolean handled;

    /**
     * TODO Needed by Undertow to instantiate the filter
     */
    public MatcherFilter () {
        super ();
        routeMatcher = null;
        isServletContext = false;
        hasOtherHandlers = false;
    }

    /**
     * Constructor.
     *
     * @param routeMatcher The route matcher
     * @param isServletContext If true, chain.doFilter will be invoked if request is not
     * consumed by Spark.
     * @param hasOtherHandlers If true, do nothing if request is not consumed by Spark in
     * order
     * to let others handlers process the request.
     */
    public MatcherFilter (
        RouteMatcher routeMatcher, boolean isServletContext, boolean hasOtherHandlers) {

        this.routeMatcher = routeMatcher;
        this.isServletContext = isServletContext;
        this.hasOtherHandlers = hasOtherHandlers;
    }

    public MatcherFilter (boolean isServletContext, boolean hasOtherHandlers) {
        this (RouteMatcherFactory.get (), isServletContext, hasOtherHandlers);
    }

    @Override public void doFilter (
        ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {

        long t = currentTimeMillis ();

        HttpServletRequest httpReq = (HttpServletRequest)servletRequest;
        HttpServletResponse httpRes = (HttpServletResponse)servletResponse;

        String uri = httpReq.getRequestURI ();
        String httpMethodStr = httpReq.getMethod ().toLowerCase ();
        String acceptType = httpReq.getHeader (ACCEPT_TYPE_REQUEST_MIME_HEADER);
        String bodyContent = null;

        RequestWrapper req = new RequestWrapper ();
        ResponseWrapper res = new ResponseWrapper ();

        try {
            bodyContent =
                onFilter (before, httpReq, httpRes, uri, acceptType, bodyContent, req, res);

            HttpMethod httpMethod = HttpMethod.valueOf (httpMethodStr);

            RouteMatch match =
                routeMatcher.findTargetForRequestedRoute (httpMethod, uri, acceptType);

            Object target = null;
            if (match != null) {
                target = match.getTarget ();
            }
            else if (httpMethod == HttpMethod.head && bodyContent == null) {
                // See if get is mapped to provide default head mapping
                RouteMatch requestedRouteTarget =
                    routeMatcher.findTargetForRequestedRoute (HttpMethod.get, uri, acceptType);
                bodyContent = requestedRouteTarget != null? "" : null;
            }

            if (target != null) {
                bodyContent =
                    handleTargetRoute (httpReq, httpRes, bodyContent, req, res, match, target);
            }

            bodyContent =
                onFilter (after, httpReq, httpRes, uri, acceptType, bodyContent, req, res);
        }
        catch (HaltException hEx) {
            LOG.fine ("halt performed");
            httpRes.setStatus (hEx.statusCode);
            String haltBody = hEx.body;
            bodyContent = (haltBody != null)? haltBody : "";
        }

        // If redirected and content is null set to empty string to not throw NotConsumedException
        if (bodyContent == null && res.isRedirected())
            bodyContent = "";

        boolean consumed = bodyContent != null;

        if (!consumed && hasOtherHandlers) {
//            throw new NotConsumedException ();
            handled = false;
			if (SparkServerFactory.IMPL == 1)
				httpRes.setStatus (SC_NOT_FOUND); // TODO Only for Undertow
            return;
        }

        if (!consumed && !isServletContext) {
            httpRes.setStatus (HttpServletResponse.SC_NOT_FOUND);
            bodyContent = String.format (NOT_FOUND, uri);
            consumed = true;
        }

        // Write body content
        if (consumed && !httpRes.isCommitted ()) {
            if (httpRes.getContentType () == null) {
                httpRes.setContentType ("text/html; charset=utf-8");
            }
            httpRes.getOutputStream ().write (bodyContent.getBytes ("utf-8"));
        }
        else if (chain != null) {
            // TODO 'SessionExample' triggers an error here!
            chain.doFilter (httpReq, httpRes);
        }

        // TODO this is an instance variable take care of multi-threading!
        handled = true;

        // TODO Merge logs and take care of method flow to log always
        LOG.fine ("httpMethod:" + httpMethodStr + ", uri: " + uri);
        LOG.fine ("Time for request: " + (currentTimeMillis () - t));
    }

    private String handleTargetRoute (
        HttpServletRequest aHttpReq, HttpServletResponse aHttpRes, String aBodyContent,
        RequestWrapper aReq, ResponseWrapper aRes, RouteMatch aMatch, Object aTarget) {

        try {
            String result = null;
            if (aTarget instanceof Route) {
                Route route = ((Route)aTarget);
                Request request = Request.create (aMatch, aHttpReq);
                Response response = Response.create (aHttpRes);

                aReq.setDelegate (request);
                aRes.setDelegate (response);

                Object element = route.handle (aReq, aRes);
                result = element != null? element.toString () : null;
            }
            if (result != null) {
                aBodyContent = result;
            }
        }
        catch (HaltException hEx) {
            throw hEx;
        }
        catch (Exception e) {
            LOG.severe (e.getMessage ());
            aHttpRes.setStatus (SC_INTERNAL_SERVER_ERROR);
            aBodyContent = INTERNAL_ERROR;
        }

        return aBodyContent;
    }

    /*
     * After and before are the same method except for HttpMethod.after|before
     */
    private String onFilter (
        HttpMethod method, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
        String uri, String acceptType, String bodyContent, RequestWrapper req,
        ResponseWrapper res) {

        List<RouteMatch> matchSet =
            routeMatcher.findTargetsForRequestedRoute (method, uri, acceptType);

        for (RouteMatch filterMatch : matchSet) {
            Object filterTarget = filterMatch.getTarget ();
            if (filterTarget instanceof sabina.Filter) {
                Request request = Request.create (filterMatch, httpRequest);
                Response response = Response.create (httpResponse);

                req.setDelegate (request);
                res.setDelegate (response);

                sabina.Filter filter = (sabina.Filter)filterTarget;
                filter.handle (req, res);

                String bodyAfterFilter = response.body ();
                if (bodyAfterFilter != null) {
                    bodyContent = bodyAfterFilter;
                }
            }
        }
        return bodyContent;
    }

    @Override public void init (FilterConfig filterConfig) {
        // Not used
    }

    @Override public void destroy () {
        // Not used
    }
}

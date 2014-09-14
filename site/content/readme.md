title=Readme
date=2013-09-24
type=page
status=published
~~~~~~


Getting started
---------------

Add the Sabina maven dependency

```xml
<dependency>
  <groupId>com.sabina</groupId>
  <artifactId>sabina-core</artifactId>
  <version>2.0.0</version>
</dependency>
```

Start coding:

```java
import static sabina.Sabina.*;
    
public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", it -> "Hello World");
    }
}
```


Ignite and view at
------------------

    http://localhost:4567/hello


Routes
------

The main building block of a Sabina application is a set of routes. A route is made up of three
simple pieces:

A **verb** (`get`, `post`, `put`, `delete`, `head`, `trace`, `connect`, `options`)
A **path** (`/hello`, `/users/:name`)
A **callback** (`request -> { }`)

NOTE! Routes are matched in the order they are defined. The first route that matches the
request is invoked.

```java
get ("/", it -> {
    // Show something ...
});
    
post ("/", it -> {
    // Create something ...
});
    
put ("/", it -> {
    // Update something ...
});
  
delete ("/", it -> {
    // Annihilate something ...
});
    
options ("/", it -> {
    // Appease something ...
});
```

Route patterns may include named parameters, accessible via the params method on the request
object:

```java
/*
 * Matches "GET /hello/foo" and "GET /hello/bar"
 * it.request.params(":name") is 'foo' or 'bar'
 */
get ("/hello/:name", it -> {
    return "Hello: " + it.request.params(":name");
});
```

Route patterns may also include splat (or wildcard) parameters, accessible via the splat method
on the request object:

```java
/*
 * Matches "GET /say/hello/to/world"
 * it.request.splat()[0] is 'hello' and request.splat()[1] 'world'
 */
get ("/say/*/to/*", it -> {
    return "Nbr of splat parameters: " + it.request.splat().length;
});
```


Request
-------

In the handle method request information and functionality is provided by the request
parameter:

```java
request.body();                // Request body sent by the client
request.cookies();             // Request cookies sent by the client
request.contentLength();       // Length of request body
request.contentType();         // Content type of request.body
request.headers();             // The HTTP header list
request.headers("BAR");        // Value of BAR header
request.attributes();          // The attributes list
request.attribute("foo");      // Value of foo attribute
request.attribute("A", "V");   // Sets value of attribute A to V
request.host();                // "example.com"
request.ip();                  // Client IP address
request.pathInfo();            // The path info
request.params("foo");         // Value of foo path parameter
request.params();              // Map with all parameters
request.port();                // The server port
request.queryMap();            // The query map
request.queryMap("foo");       // Query map for a certain parameter
request.queryParams("FOO");    // Value of FOO query param
request.queryParams();         // The query param list
request.raw();                 // Raw request handed in by Jetty
request.requestMethod();       // The HTTP method (GET, ..etc)
request.scheme();              // "http"
request.session();             // Session management
request.splat();               // Splat (*) parameters
request.url();                 // "http://example.com/foo"
request.userAgent();           // User agent
```


Query maps
----------

Query maps allows you to group parameters to a map by their prefix. This allows you to group
two parameters like `user[name]` and `user[age]` to a `user` map.

```java
request.queryMap ().get ("user", "name").value ();
request.queryMap ().get ("user").get ("name").value ();
request.queryMap ("user").get ("age").integerValue ();
request.queryMap ("user").toMap ();
```


Response
--------

In the handle method response information and functionality is provided by the response
parameter:

```java
response.body ("Hello");           // Sets content to Hello
response.header ("FOO", "bar");    // Sets header FOO with value bar
response.raw ();                   // Raw response handed in by Jetty
response.redirect ("/example");    // Browser redirect to /example
response.status (401);             // Set status code to 401
response.type ("text/xml");        // Set content type to text/xml
```


Stopping the server
-------------------

By calling the `stop ()` method the server is stopped and all routes are cleared.


Cookies
-------

Handling cookies can be done via sabina request and response objects.

```java
request.cookies ();                            // Get map of all request cookies
request.cookie ("foo");                        // Access request cookie by name
  
response.cookie ("foo", "bar");                // Set cookie with a value
response.cookie ("foo", "bar", 3600);          // Set cookie with a max-age
response.cookie ("foo", "bar", 3600, true);    // Secure cookie
response.removeCookie ("foo");                 // Remove cookie
```


Session management
------------------

Every request has access to the session created on the server side, provided with the following
methods:

```java
request.session (true);                             // Create (first time) and return session
request.session ().attribute ("user");              // Get session attribute 'user'
request.session ().attribute ("user", "foo");       // Set session attribute 'user'
request.session ().removeAttribute ("user", "foo"); // Remove session attribute 'user'
request.session ().attributes ();                   // Get all session attributes
request.session ().id ();                           // Get session id
request.session ().isNew ();                        // Check is session is new
request.session ().raw ();                          // Return servlet object
```


Halting
-------

To immediately stop a request within a filter or route use:

```java
halt ();
```

You can also specify the status when halting:

```java
halt (401);
```

Or the body:

```java
halt ("This is the body");
```

Or both:

```java
halt (401, "Go Away!");
```


Filters
-------

Before filters are evaluated before each request and can read the request and read/modify the
response.

To stop execution, use halt:

```java
before (it -> {
    boolean authenticated;
    // Check if authenticated
    if (!authenticated) {
        halt (401, "You are not welcome here");
    }
});
```

After filters are evaluated after each request and can read the request and read/modify the
response:

```java
after (it -> {
    response.header("foo", "set by after filter");
});
```

Filters optionally take a pattern, causing them to be evaluated only if the request path
matches that pattern:

```java
before ("/protected/*", it -> {
    // Check if authenticated
    halt (401, "Go Away!");
});
```


Browser Redirect
----------------

You can trigger a browser redirect with the redirect helper method:

```java
response.redirect ("/bar");
```

You can also trigger a browser redirect with specific http 3XX status code:

```java
response.redirect ("/bar", 301); // moved permanently
```


Exception Mapping
-----------------

To handle exceptions of a configured type for all routes and filters:

```java
get ("/throwexception", it -> {
    throw new NotFoundException ();
});

exception (NotFoundException.class, (e, request, response) -> {
    response.status (404);
    response.body ("Resource not found");
});
```


Static files
------------

Assign a folder in the classpath serving static files with the staticFileLocation method.

```java
staticFileLocation ("/public"); // Static resources
```

Note that the public directory name is not included in the URL. A file `/public/css/style.css`
is made available as `http://<host>:<port>/css/style.css`

You can also assign an external folder (not in the classpath) serving static files with the
`externalStaticFileLocation` method.

```java
externalStaticFileLocation ("/var/www/public"); // Static files
```


ResponseTransformer
-------------------

Mapped routes that transforms the output from the handle method. This is done by extending the
ResponseTransformer and pass this to the mapping method.

Example Of a route transforming output to JSON using Gson:

```java
import com.google.gson.Gson;
  
public class JsonTransformer implements ResponseTransformer {
    private Gson gson = new Gson ();
      
    @Override public String render (Object model) {
        return gson.toJson (model);
    }
}
```

and how it is used (MyMessage is a bean with one member 'message'):

```java
get ("/hello", "application/json", it -> {
    return new MyMessage ("Hello World");
}, new JsonTransformer ());
```


Views / Templates - TemplateEngine
----------------------------------

A TemplateViewRoute is built up by a path (for url-matching) and the template engine holding
the implementation of the 'render' method.

Instead of returning the result of calling toString() as body the TemplateViewRoute returns
the result of calling render method.

The primary purpose of this kind of Route is to provide a way to create generic and reusable
components for rendering output using a Template Engine.

Available TemplateEngine implementations:


Mustache
--------

Renders objects to HTML using the Mustache template engine.

Maven dependency:

```xml
<dependency>
  <groupId>com.sabina</groupId>
  <artifactId>sabina-template-mustache</artifactId>
  <version>1.0.0</version>
</dependency></pre>
```

Source: [sabina-template-mustache][msrc]
Code example: [sabina-template-mustache example][mex]

[msrc]: https://github.com/perwendel/sabina-template-engines/tree/master/sabina-template-mustache
[mex]: https://github.com/perwendel/sabina-template-engines/blob/master/sabina-template-mustache/src/test/java/sabina/template/mustache/MustacheTemplateExample.java


Port
----

By default, Sabina runs on port 4567. If you want to set another port use setPort.
This has to be done before using routes and filters:

```java
setPort (9090); // Sabina will run on port 9090
```


Embedded Web Server
-------------------

Standalone Sabina runs on an embedded Jetty web server.


Running Sabina on a Web Server
-----------------------------

To run Sabina on a web server instead of standalone first of all an implementation of the
interface `sabina.servlet.Sabina` is needed. In the `init()` method the routes should
be initialized. In your `web.xml` the following filter needs to be configured:

```xml
<filter>
  <filter-name>SabinaFilter</filter-name>
  <filter-class>sabina.servlet.SabinaFilter</filter-class>
  <init-param>
    <param-name>applicationClass</param-name>
    <param-value>com.company.YourApplication</param-value>
  </init-param>
</filter>
 
<filter-mapping>
  <filter-name>SabinaFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```


Javadoc
-------

Javadoc is available at [there4.co/sabina](https://there4.co/sabina)


Examples
--------

Examples can be found on the project's page at [Github](http://github.com/jamming/sabina)

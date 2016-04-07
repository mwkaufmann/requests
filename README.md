[![](https://travis-ci.org/caoqianli/requests.svg)](https://travis-ci.org/caoqianli/requests) 
![License](https://img.shields.io/badge/licence-Simplified%20BSD-blue.svg?style=flat)

Requests is a http request lib for java, using HttpClient as backend and with fluent api.
From version 3.0, requests required java8.

-	[Maven Setting](#maven-setting)
-	[Requests](#requests)
	-	[Simple http request](#simple-http-request)
	-	[Charset](#charset)
	-	[Passing Parameters](#passing-parameters)
	-	[Custom Headers](#custom-headers)
	-	[Cookies](#cookies)
	-	[Request with data](#request-with-data)
	-	[Basic Auth](#basic-auth)
	-	[Client Settings](#client-settings)
-	[Client](#client)
	-	[Redirection](#redirection)
	-	[Timeout](#timeout)
	-	[Response compress](#response-compress)
	-	[Https Verification](#https-verification)
	-	[Proxy](#proxy)
-	[Session](#session)
-	[Exceptions](#exceptions)

#Maven Setting

Requests is now in maven central repo.

```xml
<dependency>
    <groupId>net.dongliu</groupId>
    <artifactId>requests</artifactId>
    <version>3.0.5</version>
</dependency>
```

#Requests

A Requests class is provided to make plain, simple http requests ##Simple http request Simple example that do http get request:

```java
String url = ...;
String resp = Requests.get(url).send().readToText();
```

Post and other method:

```java
resp = Requests.post(url).send().readToText();
resp = Requests.head(url).send().readToText();
...
```

The response object have several common http response fields can be used:

```java
RawResponse resp = Requests.get(url).send();
int statusCode = resp.getStatus();
List<Header> headers = resp.getHeaders();
List<Cookie> cookies = resp.getCookies();
String body = resp.readToText();
```
Make sure call readToText or other methods to consume resp, or call close method to close resp.

The readToText() method here trans http response body as String, more other methods provided:

```java
// get response as string, use encoding get from response header
String resp = Requests.get(url).send().readToText();
// get response as bytes
byte[] resp1 = Requests.get(url).send().readToBytes();
// save response as file
boolean result = Requests.get(url).send().writeTo("/path/to/save/file");
```

or you can custom http response processor your self:

```java
String resp = Requests.get(url).send().process(new ResponseHandler<String>() {...});
```

##Charset 

Requests default use UTF-8 to encode parameters, post forms or request string body, you can set other charset by:
```
String resp = Requests.get(url).requestCharset(StandardCharsets.ISO_8859_1).send().readToText();
```
When read response to text-based result, use charset get from http response header, or UTF-8 if not found.
Can force use other charset by:
```
String resp = Requests.get(url).send().responseCharset(StandardCharsets.ISO_8859_1).readToText();
```

##Passing Parameters 

Pass parameters in urls using param or params method:
```java
// set params by map
Map<String, Object> params = new HashMap<>();
params.put("k1", "v1");
params.put("k2", "v2");
String resp = Requests.get(url).params(params).send().readToText();
// set multi params
String resp = Requests.get(url).params(Entry.of("k1", "v1"), Entry.of("k2", "v2"))
        .send().readToText();
```

If you want to send post form-encoded paramters, use form()/forms() methods ##Custom Headers Http request headers can be set by header or headers method:

```java
// set headers by map
Map<String, Object> headers = new HashMap<>();
headers.put("k1", "v1");
headers.put("k2", "v2");
String resp = Requests.get(url).headers(headers).send().readToText();
// set multi headers
String resp = Requests.get(url).headers(Entry.of("k1", "v1"), Entry.of("k2", "v2"))
        .send().readToText();
```

##Cookies 

Cookies can be add by:
```java
Map<String, Object> cookies = new HashMap<>();
cookies.put("k1", "v1");
cookies.put("k2", "v2");
// set cookies by map
String resp = Requests.get(url).cookies(cookies).send().readToText();
// set cookies
String resp = Requests.get(url).cookies(Entry.of("k1", "v1"), Entry.of("k2", "v2"))
        .send().readToText();
```

##Request with data 

Http Post, Put, Patch method can send request body. Take Post for example:
```java
// set post form data
String resp = Requests.post(url).forms(Entry.of("k1", "v1"), Entry.of("k2", "v2"))
        .send().readToText();
// set post form data by map
Map<String, Object> formData = new HashMap<>();
formData.put("k1", "v1");
formData.put("k2", "v2");
String resp = Requests.post(url).forms(formData).send().readToText();
// send byte array data as body
byte[] data = ...;
resp = Requests.post(url).body(data).send().readToText();
// send string data as body
String str = ...;
resp = Requests.post(url).body(str).send().readToText();
// send data from inputStream
InputStream in = ...
resp = Requests.post(url).body(in).send().readToText();
```

One more complicate situation is multiPart post request, this can be done via multiPart method:

```java
// send form-encoded data
InputStream in = ...;
byte[] bytes = ...;
String resp = Requests.post(url)
        .multiParts(Part.filePart("file1", new File(...)), Part.filePart("file2", new File("...")))
        .send().readToText();
```

##Basic Auth 

Set http basic auth param by auth method:
```java
String resp = Requests.get(url).basicAuth("user", "passwd").send().readToText();
```

##Client Settings


Requests create a Single Client object for each request, and close it when request finished. You can specify custom settings for this client:
```java
String response = Requests.get("https://127.0.0.1:8443/otn/")
        .timeout(3_000)
        .compress(false)
        .allowRedirects(false)
        .userAgent("Custom user agent")
        .verify(false).send().readToText();
assertEquals(200, response.getStatusCode());
```

See [Client section](#client) to get more client settings and what them means.

#Client

Use Client to reuse http connections, and custom connection properties. Client has similar method as Requests class.

There are two kinds of client, single and pooled Client is not thread-safe and only process one http request at a time, can be use in single thread context;Pooled client is thread-safe can be used across multi thread.

Note: you need to close client when no longer used.

```java
// create pooled client
try(Client client = Client.pooled()
       .maxPerRoute(20) // max connection per site
       .maxTotal(100)   // max connectoin
       .build()) {
    String resp1 = client.get(url1).send().readToText();
    String resp2 = client.get(url2).send().readToText();
}
// create single client
try(Client client = Client.single().build()) {
   // ...
}
```

##Redirection 

Requests and Client will handle 30x http redirect automatically, you can disable it by:

```java
try (Client client = Client.single().allowRedirects(false).build()) {
    String resp = client.get(url).send().readToText();
}
```

##Timeout

There are two timeout parameters you can set, connect timeout, and socket timeout. The timeout value default to 10_1000 milliseconds.

```java
// both connec timeout, and socket timeout
Client client = Client.single().timeout(30_000).build();
// set connect timeout and socket timeout separately
Client client = Client.single().socketTimeout(20_000).connectTimeout(30_000).build();
```

You may not need to know, but Requests also use connect timeout as the timeout value get connection from connection pool if connection pool is used. ##Response compress Requests send Accept-Encoding: gzip, deflate, and handle gzipped response in default. You can disable this by:

```java
Client client = Client.single().compress(false).build();
```

##Https Verification 

Some https sites do not have trusted http certificate, Exception will be throwed when request. You can disable https certificate verify by:

```java
Client client = Client.single().verify(false).build();
```

##Proxy Set 

Proxy by proxy method:

```java
Client client = Client.single()
        .proxy(Proxy.httpProxy("127.0.0.1", 8080))
        .build();
```

The proxy can be created by:

```java
//http proxy
Proxy.httpProxy("127.0.0.1", 8080)
//https proxy
Proxy.httpsProxy("127.0.0.1", 8080)
//socket proxy
Proxy.socketProxy("127.0.0.1", 5678)
//with auth
Proxy.httpProxy("127.0.0.1", 8080, userName, password)
```

#Session

Session maintains cookies, basic authes and maybe other http context for you, useful when need login or other situations. Session have the same usage as Requests and Client.

```java
Session session = client.session();
String resp1 = session.get(url1).send().readToText();
String resp2 = session.get(url2).send().readToText();
```

Session do not need to be closed.If the client which this session obtained from is closed, session can no longer be used. 

#Exceptions 

Requests wrapped all checked exceptions into corresponding unchecked exceptions:
```
 IOException -> UncheckedIOException
 URISyntaxException -> UncheckedURISyntaxException
```
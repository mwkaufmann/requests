#Requests

Requests is a http request lib for java inspired by The Python [requests](http://docs.python-requests.org/) Module.

The Httpclient lib is greate, but has too complex API, which confuse beginners. Requests build simple and flexible api, both for common and advanced Usage.

#User Guide
##Get Requests
Requests is now in maven central repo.
```xml
<dependency>
    <groupId>net.dongliu</groupId>
    <artifactId>requests</artifactId>
    <version>1.9.0</version>
</dependency>
```
##Make request
Simple example that do http get request:
```java
String url = ...;
Response<String> resp = Requests.get(url).text();
```
Post and other method:
```java
resp = Requests.post(url).text();
resp = Requests.head(url).text();
...
```
##Response Content
The response object have common http response field to be used:
```java
Response<String> resp = Requests.get(url).text();
int statusCode = resp.getStatusCode();
Headers headers = resp.getHeaders();
Cookies cookies = resp.getCookies();
String body = resp.getBody();
```
The text() method here trans http response body as String, there are other method to process http response:
```java
// get response as string, and use provided encoding
Response<String> resp = Requests.get(url).text();
// get response as bytes
Response<byte[]> resp1 = Requests.get(url).bytes();
// save response as file 
Response<File> resp2 = Requests.get(url).file("/path/to/save/file");
```
or you can custom http response processor your self:
```java
Response<String> resp = Requests.get(url).handler(new ResponseHandler<String>() {...});
```
##Charset
Set charset used to encode parameters, post forms, request/response string body:
```
Response<String> resp = Requests.get(url).charset(StandardCharsets.UTF_8).text();
```
Default charset is utf-8.
##Passing Parameters
Pass parameters in urls using param or params method:
```java
Map<String, Object> params = new HashMap<>();
params.put("k1", "v1");
params.put("k2", "v2");
Response<String> resp = Requests.get(url)
        // add one param
        .param("key1", "value1")
        // add multi params by map
        .params(params)
        // add multi params
        .params(new Parameter(...), new Parameter(...))
        .text();
```
##Custom Headers
Http request headers can be set by header or headers method:
```java
Map<String, Object> headers = new HashMap<>();
headers.put("k1", "v1");
headers.put("k2", "v2");
Response<String> resp = Requests.get(url)
        // add one header
        .header("key1", "value1")
        // add multi headers by map
        .headers(headers)
        // add multi headers
        .headers(new Header(...), new Header(...))
        .text();
```
##Cookies
Cookies can be add by::
```java
Map<String, Object> cookies = new HashMap<>();
cookies.put("k1", "v1");
cookies.put("k2", "v2");
Response<String> resp = Requests.get(url)
        // add one cookie
        .cookie("key1", "value1")
        // add multi cookies by map
        .cookies(cookies)
        // add multi headers
        .cookies(new Cookie(...), new Cookie(...))
        .text();
```
##UserAgent
A convenient method to set userAgent:
```java
Response<String> resp = Requests.get(url).userAgent(userAgent).text();
```
##Request with data
Http Post, Put, Patch method can send request bodys. Take Post for example:
```java
Map<String, Object> formData = new HashMap<>();
formData.put("k1", "v1");
formData.put("k2", "v2");
// send www-form-encoded data. x-www-form-urlencoded header will be added automatically
Response<String> resp = Requests.post(url).forms(formData).text();
// send byte array data as body
byte[] data = ...;
resp = Requests.post(url).data(data).text();
// send string data as body
String str = ...;
resp = Requests.post(url).data(str).text();
// send data from inputStream
InputStream in = ...
resp = Requests.post(url).data(in).text();
```
One more complicate situation is multiPart post request, this can be done via multiPart method:
```java
// send form-encoded data
InputStream in = ...;
byte[] bytes = ...;
Response<String> resp = Requests.post(url)
        .multiPart("test", "value")
        .multiPart("byFile", new File("/path/to/file"))
        .multiPart("byStream", mimeType, in)
        .multiPart("byBytes", mimeType, bytes)
        .text();
```
###Redirection
Requests will handle 301/302 http redirect, you can get redirect history:
```java
Response<String> resp = Requests.get(url).text();
List<URI> history = resp.getHistory();
```
Or you can disable automatic redirect and handle it yourself:
```java
Response<String> resp = Requests.get(url).allowRedirects(false).text();
```
## Timeout
There are two timeout parameters you can set, connect timeout, and socket timeout. The timeout value default to 10_1000 milliseconds.
```java
// both connec timeout, and socket timeout
Response<String> resp = Requests.get(url).timeout(30_000).text();
// set connect timeout and socket timeout
resp = Requests.get(url).timeout(30_000, 30_000).text();
```
You may not need to know, but Requests also use connect timeout as the timeout value get connection from connection pool if connection pool is used.
##Gzip
Requests send Accept-Encoding: gzip, deflate, and handle gzipped response in default. You can disable this by:
```java
Response<String> resp = Requests.get(url).gzip(false).text();
```
##Https Verification
Some https sites do not have trusted http certificate, Exception will be throwed when request. You can disable https certificate verify by:
```java
Response<String> resp = Requests.get(url).verify(false).text();
```
##Basic Auth
Set http basic auth param by auth method:
```java
Response<String> resp = Requests.get(url).auth("user", "passwd").verify(false).text();
```
##Proxy
Set proxy by proxy method:
```java
Response<String> resp = Requests.get("http://www.baidu.com/")
        .proxy(Proxy.httpProxy("127.0.0.1", 8080))
        .text();
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
##Exceptions
Requests wrapped checked exceptions into runtime exception, RuntimeIOException, InvalidUrlException, IllegalEncodingException. Catch this if you mind.
## Session
Session keep cookies and basic auto cache and other http context for you, useful when need login or other situations.Session have exactly the same usage as Requests.
```java
Session session = Requests.session();
Response<String> resp1 = session.get(url1).text();
Response<String> resp2 = session.get(url2).text();
```
##Connection Pool
Request(and Session) can share one connection pool to reuse http connections.
```java
ConnectionPool connectionPool = ConnectionPool.custom().verify(false)
       .maxPerRoute(20)
       .maxTotal(100)
       //.proxy(...)
       .build();
Response<String> resp1 = Requests.get(url1).connectionPool(connectionPool).text();
Response<String> resp2 = Requests.get(url2).connectionPool(connectionPool).text();
connectionPool.close();
```
Note:
* you need to close connection pool manually when do not need it any more.
* if connection pool is used, you should set verify and proxy use ConnectionPoolBuilder, the connection pool's (verify, proxy) settings will override requests' settings.

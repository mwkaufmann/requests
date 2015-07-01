package com.duxiu.modules.utils;

import java.io.IOException;

import net.dongliu.requests.PooledClient;
import net.dongliu.requests.RequestBuilder;
import net.dongliu.requests.Session;
import net.dongliu.requests.exception.RequestException;

/**
 * golbal init Requests, web site to use
 */
public class Requests {
	private static final PooledClient POOLED_CLIENT = customPooledClient();
	
	private static PooledClient customPooledClient() {
		return PooledClient.custom().maxTotal(200).maxPerRoute(20).allowPostRedirects(true).build();
	}
	
	/**
	 * get method
	 */
	public static RequestBuilder get(String url) throws RequestException {
		return POOLED_CLIENT.get(url);
	}
	
	/**
	 * head method
	 */
	public static RequestBuilder head(String url) throws RequestException {
		return POOLED_CLIENT.head(url);
	}
	
	/**
	 * get url, and return content
	 */
	public static RequestBuilder post(String url) throws RequestException {
		return POOLED_CLIENT.post(url);
	}
	
	/**
	 * put method
	 */
	public static RequestBuilder put(String url) throws RequestException {
		return POOLED_CLIENT.put(url);
	}
	
	/**
	 * delete method
	 */
	public static RequestBuilder delete(String url) throws RequestException {
		return POOLED_CLIENT.delete(url);
	}
	
	/**
	 * options method
	 */
	public static RequestBuilder options(String url) throws RequestException {
		return POOLED_CLIENT.options(url);
	}
	
	/**
	 * patch method
	 */
	public static RequestBuilder patch(String url) throws RequestException {
		return POOLED_CLIENT.patch(url);
	}
	
	/**
	 * trace method
	 */
	public static RequestBuilder trace(String url) throws RequestException {
		return POOLED_CLIENT.trace(url);
	}
	
	/**
	 * create a session. session can do request as Requests do, and keep cookies to maintain a http session
	 */
	public static Session session() {
		return POOLED_CLIENT.session();
	}
	
	public static void close() throws IOException {
		POOLED_CLIENT.close();
	}
}

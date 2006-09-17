package org.red5.server.net.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

public class RTMPTServletTest {
	/*
	 * @Test public void testHandleBadRequest() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testReturnMessageByteHttpServletResponse() { fail("Not
	 * yet implemented"); }
	 * 
	 * @Test public void testReturnMessageStringHttpServletResponse() {
	 * fail("Not yet implemented"); }
	 * 
	 * @Test public void
	 * testReturnMessageRTMPTConnectionByteBufferHttpServletResponse() {
	 * fail("Not yet implemented"); }
	 * 
	 * @Test public void testGetClientId() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testGetClient() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testSkipData() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testReturnPendingMessages() { fail("Not yet
	 * implemented"); }
	 * 
	 * @Test public void testHandleOpen() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testHandleClose() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testHandleSend() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testHandleIdle() { fail("Not yet implemented"); }
	 */

	@Test
	public void testServiceHttpServletRequestHttpServletResponse()
			throws Exception {

		final String[] requests = new String[] { "/open", "/close", "/send",
				"/idle" };

		MyRequest req = new MyRequest();
		req.setServletPath(requests[3]);

		MyResponse resp = new MyResponse();

		long start = System.nanoTime();
		RTMPTServlet s1 = new RTMPTServlet();
		for (int i = 0; i < 10000; i++) {
			try {
				// try if/else if instead of switch
				s1.service(req, resp);
			} catch (Exception ex) {
			}
		}
		System.out.println("Method 1: " + (System.nanoTime() - start) + " ns");

		start = System.nanoTime();
		RTMPTServlet s2 = new RTMPTServlet();
		for (int i = 0; i < 10000; i++) {
			try {
				// try switch instead of if/else if
				s2.service(req, resp);
			} catch (Exception ex) {
			}
		}
		System.out.println("Method 2: " + (System.nanoTime() - start) + " ns");

	}

	class MyRequest implements HttpServletRequest {

		private String path;

		public String getAuthType() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContextPath() {
			// TODO Auto-generated method stub
			return null;
		}

		public Cookie[] getCookies() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getDateHeader(String arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getHeader(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getHeaders(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getIntHeader(String arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getMethod() {
			return "POST";
		}

		public String getPathInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getPathTranslated() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getQueryString() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRemoteUser() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRequestedSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRequestURI() {
			// TODO Auto-generated method stub
			return null;
		}

		public StringBuffer getRequestURL() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setServletPath(String path) {
			this.path = path;
		}

		public String getServletPath() {
			return path;
		}

		public HttpSession getSession() {
			// TODO Auto-generated method stub
			return null;
		}

		public HttpSession getSession(boolean arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Principal getUserPrincipal() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isRequestedSessionIdFromCookie() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRequestedSessionIdFromUrl() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRequestedSessionIdFromURL() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRequestedSessionIdValid() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isUserInRole(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getAttribute(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getAttributeNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getContentLength() {
			return 1;
		}

		public String getContentType() {
			return "application/x-fcs";
		}

		public ServletInputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getLocalAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getLocales() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getLocalName() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getLocalPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getParameter(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Map getParameterMap() {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getParameterNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getParameterValues(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getProtocol() {
			// TODO Auto-generated method stub
			return null;
		}

		public BufferedReader getReader() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRealPath(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRemoteAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRemoteHost() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getRemotePort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public RequestDispatcher getRequestDispatcher(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getScheme() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getServerName() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getServerPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeAttribute(String arg0) {
			// TODO Auto-generated method stub

		}

		public void setAttribute(String arg0, Object arg1) {
			// TODO Auto-generated method stub

		}

		public void setCharacterEncoding(String arg0)
				throws UnsupportedEncodingException {
			// TODO Auto-generated method stub

		}

	}

	class MyResponse implements HttpServletResponse {

		public void addCookie(Cookie arg0) {
			// TODO Auto-generated method stub

		}

		public void addDateHeader(String arg0, long arg1) {
			// TODO Auto-generated method stub

		}

		public void addHeader(String arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		public void addIntHeader(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public boolean containsHeader(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public String encodeRedirectUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeRedirectURL(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeURL(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public void sendError(int arg0, String arg1) throws IOException {
			// TODO Auto-generated method stub

		}

		public void sendError(int arg0) throws IOException {
			// TODO Auto-generated method stub

		}

		public void sendRedirect(String arg0) throws IOException {
			// TODO Auto-generated method stub

		}

		public void setDateHeader(String arg0, long arg1) {
			// TODO Auto-generated method stub

		}

		public void setHeader(String arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		public void setIntHeader(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void setStatus(int arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		public void setStatus(int arg0) {
			// TODO Auto-generated method stub

		}

		public void flushBuffer() throws IOException {
			// TODO Auto-generated method stub

		}

		public int getBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		public ServletOutputStream getOutputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public PrintWriter getWriter() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isCommitted() {
			// TODO Auto-generated method stub
			return false;
		}

		public void reset() {
			// TODO Auto-generated method stub

		}

		public void resetBuffer() {
			// TODO Auto-generated method stub

		}

		public void setBufferSize(int arg0) {
			// TODO Auto-generated method stub

		}

		public void setCharacterEncoding(String arg0) {
			// TODO Auto-generated method stub

		}

		public void setContentLength(int arg0) {
			// TODO Auto-generated method stub

		}

		public void setContentType(String arg0) {
			// TODO Auto-generated method stub

		}

		public void setLocale(Locale arg0) {
			// TODO Auto-generated method stub

		}

	}

}

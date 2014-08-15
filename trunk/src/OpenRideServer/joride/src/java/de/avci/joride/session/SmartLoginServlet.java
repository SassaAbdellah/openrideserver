package de.avci.joride.session;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.avci.joride.utils.PropertiesLoader;

public class SmartLoginServlet extends HttpServlet {

	/** Standard j_username parameter for logging in
	 */
	public static final String j_username="j_username";
	
	/** Standard j_password parameter for logging in
	 */
	public static final String j_password="j_password";
	
	/** Initial servlet configuration
	 * 
	 */
	
	private ServletConfig servletConfig;
	
	@Override
	
	public void init(ServletConfig arg){
			this.servletConfig=arg;
	}

	
	
	public ServletConfig getServletConfig() {
		return servletConfig;
	}

	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		// delegate to processRequest
		this.processRequest(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		// delegate to processRequest
		this.processRequest(request, response);
	}
	
	
	
	
	
	/** Do stuff with request and response
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response){
		
		
			
		String username=request.getParameter(j_username);
		String password=request.getParameter(j_password);
		
		//
		boolean login_success=false;
		
		try{
			request.login(username, password);
			login_success=true;
		} catch (ServletException servletExc){
			
			// TODO: do something more decent here
			
			
		}
		
		try {
			
			
			String baseURL=PropertiesLoader.getNavigationProperties().getProperty("urlBase");
			
			if(login_success){
				//
				// programmatic login successful, 
				//
				//
				response.sendRedirect(baseURL);
			} else {
				//
				// login failed, send response to noauthURL
				//
				String noauthURL=this.getInitParameter("noauthPath");
				response.sendRedirect(baseURL+noauthURL);
			}
		} catch (IOException exc) {
			// TODO: do something more decent here
			throw new Error("Error while sending redirect", exc);
		}
		
	}
	
	
	
	

}

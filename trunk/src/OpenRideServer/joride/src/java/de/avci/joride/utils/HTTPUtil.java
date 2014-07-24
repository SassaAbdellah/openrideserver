/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.avci.joride.utils;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Access to the HTTPServletRequest and some functions therein
 *
 * Sometimes it's a good Idea to access the HTTPRequest (for Example when it
 * comes to retrieving sensitive User data based on the AuthPrincipal)
 *
 *
 *
 * @author jochen
 */
public class HTTPUtil {
	
	/** Parameter for accepted languages from http request.
	 * 
	 */
	public static final String acceptLanguageParam="Accept-Language";
	

    transient Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    /**
     * @return the HTTPServletRequest, or null if it can not be accessed
     */
    public HttpServletRequest getHTTPServletRequest() {

        Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request);
        } else {

            return null;
        }
    }
    
    
    /** Get the set of supported locales
     * 
     * @return set of (JSF) supported locales
     */
    public Set <Locale> getSupportedLocales(){
    	
    	HashSet <Locale> res=new HashSet<Locale>();
    	Iterator<Locale> i= FacesContext.getCurrentInstance().getApplication().getSupportedLocales();

    	while (i.hasNext()){res.add(i.next());}
    	
    	return res;
    }
    
    
    
   
    /** Ectract best accepted language from Browser request.
     * 
     * @param request
     */
    
    public  Locale detectBestLocale() {
    	
    	
    	Set <Locale> supportedLocals=this.getSupportedLocales();
      
    	HttpServletRequest request=this.getHTTPServletRequest();
    	if(request==null){
    		return null;
    	}
        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
          
        	Locale locale=locales.nextElement();
            if (supportedLocals.contains(locale)) {
                return locale;
            }
        }
        
        return null;
    }
    
    
   
  
    

    /**
     * @return the HTTPServletRequest, or null if it can not be accessed
     */
    public HttpServletResponse getHTTPServletResponse() {

        Object response = FacesContext.getCurrentInstance().getExternalContext().getResponse();
        if (response instanceof HttpServletResponse) {
            return ((HttpServletResponse) response);
        } else {

            return null;
        }
    }

    /**
     * Get Access to the HTTPAuth Principal's name. This allows to savely
     * restrict access to data to these belonging to the authenticated user.
     *
     * @return the HTTPPrincipal's Name , or null if this cannot be retrieved.
     *
     *
     */
    public String getUserPrincipalName() {

        HttpServletRequest request = this.getHTTPServletRequest();
        if (request == null) {
            log.log(Level.WARNING, "HTTPRequest is null, cannot determine AuthUser");
            return null;
        }

        if (this.getHTTPServletRequest().getUserPrincipal() == null) {
            log.log(Level.WARNING, "UserPrincipal is null, cannot determine AuthUser");
            return null;
        }

        return this.getHTTPServletRequest().getUserPrincipal().getName();
    }

    /**
     * Returns the value for the given Parameter as an Array of Strings
     *
     * @param paramName
     * @return
     */
    public String[] getParamValueAsArray(String paramName) {
        return getHTTPServletRequest().getParameterMap().get(paramName);
    }

    /**
     * Returns the HTTP Request parameter's value as a single String (as opposed
     * to an Array of Strings). Return null, if the HTTPRequest did not feature
     * such a parameter.
     *
     * @param paramName the httprequest parameter to be evaluated
     * @return
     */
    public String getParameterSingleValue(String paramName) {

        String[] ar = this.getParamValueAsArray(paramName);

        if (ar == null) {
            return null;
        }
        if (ar.length == 0) {
            return null;
        }

        String res = "";

        for (int i = 0; i < ar.length; i++) {
            res += ar[i];
        }

        return res;
    }
} // class
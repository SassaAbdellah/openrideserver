package de.avci.joride.session;

import de.avci.joride.constants.JoRideConstants;
import de.avci.joride.utils.HTTPUtil;
import de.avci.joride.utils.PropertiesLoader;
import java.io.IOException;

import java.io.Serializable;
import java.security.Principal;
import java.util.TimeZone;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 * A simplistic bean allowing access to HTTPAuthData
 *
 *
 */
@Named("HTTPUser")
@SessionScoped
public class HTTPUser implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2141634023951952843L;

    public String getCurrentUser() {

        Principal principal = getUserPrincipal();
        if (principal == null) {
            return null;
        }

        return principal.getName();
    }

    public Principal getUserPrincipal() {

        if (getFacesContext() != null) {
            return getFacesContext().getExternalContext().getUserPrincipal();
        }

        return null;
    }

    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    
    
    /** 
     * 
     * @return  true, if httprequest has a non-null user principal, else false
     */
    public boolean isLoggedIn(){
        return getUserPrincipal()!=null;
    }
    
    public String getLoginLabel() {
        if (getUserPrincipal() == null) {
            return (new PropertiesLoader()).getMessagesProps().getProperty("login");
        }
        return null;
    }
    
    
    /**  URLBase is the base of the webapplication,
     *   i,e "/joride" for joride webapp,
     *   "/joride-public/" for joride-public webapp, etc
     *   It is defined in navigation.properties file.
     * 
     * 
     * @return 
     */
    public String getURLBase(){
    
           PropertiesLoader pl=new PropertiesLoader();
           return pl.getNavigationProps().getProperty("urlBase");
    }
    
    
    /**  LoginURL is the URL where Users should be sent to log in.
     * 
     *   It is defined in navigation.properties file.
     * 
     * 
     * @return 
     */
    
    public String getLoginURL() {
     
            PropertiesLoader pl=new PropertiesLoader();
         
            String urlLogin=pl.getNavigationProps().getProperty("urlLogin");
            return getURLBase()+urlLogin;
            
    }
    

   
     public String getLogoutLabel() {
        if (getUserPrincipal() != null) {
            return (new PropertiesLoader()).getMessagesProps().getProperty("logout");
        }
        return null;
    }
    
    
    public String getLogoutURL() {
        
            PropertiesLoader pl=new PropertiesLoader();
            String urlLogout=pl.getNavigationProps().getProperty("urlLogout");
            
            return urlLogout;
    } 
     
     
    
    
      public String getLoggedOutURL() {
        
            PropertiesLoader pl=new PropertiesLoader();
            
            System.err.println("TODO: navigation props: "+pl.getNavigationProps());
            
            String urlLoggedOut=pl.getNavigationProps().getProperty("urlLoggedOut");
            return urlLoggedOut;
    } 
     
      
      
    
      /** Invalidate Session, then forward to "loggedOutURL"
       * 
       * @return 
       */
    public String logOut(){
    
        HTTPUtil hpu=new HTTPUtil();
        HttpSession session=hpu.getHTTPServletRequest().getSession();
        session.invalidate();
        try { hpu.getHTTPServletResponse().sendRedirect(getLoggedOutURL());
        } catch (IOException exc) {
            throw new Error("Unexpected Error", exc);
        }
        
        return "logout";
       
    }
    
    
        /** URL from which the OpenLayers library should be included
     *  this is now configurable via the urlOpenLayers property
     *  in navigation.properties
     *  
     */
    public String getOpenLayersURL() {


        PropertiesLoader pl = new PropertiesLoader();

        System.err.println("TODO: navigation props: " + pl.getNavigationProps());

        String urlLoggedOut = pl.getNavigationProps().getProperty("urlOpenLayers");
        return urlLoggedOut;
    }
    
     
    /** Flag signifying wether or not maps should be shown.
     *  I.e: Maps are cool and should always be shown, unless there
     *  is not enough bandwidth.
     *  
     *  Since such conditions can be expected to last for
     *  
     * 
     */
    private boolean showMap=false;

	public boolean isShowMap() {
		return showMap;
	}

	public void setShowMap(boolean showMap) {
		this.showMap = showMap;
	}
    
	
	public void toggleMapVisibility(ActionEvent evt){
		this.setShowMap(!this.isShowMap());
		
		System.err.println("TODO: toggled showMapsProperty, current property is "+this.isShowMap());
	}
    
    /** return inversion of isShowMap
     */
    public boolean isHideMap(){
    	return !this.isShowMap();
    }
    
    /** Return the timezone for this session
     * 
     */
    public TimeZone getTimeZone(){
    	return JoRideConstants.getTimeZone();
    }
    
    
} // class

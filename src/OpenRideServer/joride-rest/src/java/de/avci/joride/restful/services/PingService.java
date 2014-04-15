package de.avci.joride.restful.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;



/**
 * Simple Update Service providing information about updated requests and offers
 * to the Webclient.
 *
 *
 * @deprecated -- note, that this is currently unused, but may be reactivated
 * when RESTFUL interface will be enabled
 *
 *
 *
 *
 * @author jochen
 */
@Path("ping")
@Produces("text/json")
public class PingService {

    @GET
    @Path("text")
    
    /** Returning simple JSON text to show that Rest Interface is there.
     *
     *
     */
    public String ping(@Context HttpServletRequest request) {

       

        // For simplicity, we build the JSON Response "byHand" here //
        String result = "{\"PingResponse\": \" hello from : "+this.getClass()+"\"}";

        return result;

    }

   


    /**
     * Root webservices must have a default constructor!
     *
     */
    public PingService() {
    }
}

/*
 OpenRide -- Car Sharing 2.0
 Copyright (C) 2010  Fraunhofer Institute for Open Communication Systems (FOKUS)

 Fraunhofer FOKUS
 Kaiserin-Augusta-Allee 31
 10589 Berlin
 Tel: +49 30 3463-7000
 info@fokus.fraunhofer.de

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License Version 3 as
 published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhg.fokus.openride.matching;

/**
 * Maybe this class should also be removed.
 *
 * @author fvi
 */
public class Constants {
    /*
     * SPATIAL REFERENCE ID'S TO USE IN DATABASE AND FOR COORDINATES
     * 
     * 
     * 
     * Made hardwired SRIDs private, obviously, SRID_DB and SRID_LATLON can be removed now.
     * 
	 * 
	 */

    private static final int SRID_DB = 3068;
    private static final int SRID_LATLON = 4326;
    
    
    
    
    /*
     *  Routing algoritm, (not specific to route enging)
     * 
     */
    
    public static final double ROUTER_NEAREST_NEIGHBOR_THRESHOLD = 2000d;
    public static final boolean ROUTE_FASTEST_PATH_DEFAULT = true;
    /*
     * MATCHING ALGORITHM
     */
    public static final double MATCHING_DEFAULT_D = 2000;
    public static final double MATCHING_DEFAULT_MAX_DETOUR_SECONDS = 600;
    public static final double MATCHING_MAX_ROUTE_POINT_DISTANCE = 2000;
   
   
}
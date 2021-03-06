/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.avci.joride.jbeans.driverundertakesride;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import de.avci.joride.jbeans.customerprofile.JCustomerEntityService;
import de.avci.joride.jbeans.matching.JMatchingEntity;
import de.avci.joride.jbeans.riderundertakesride.JRiderUndertakesRideEntity;
import de.avci.joride.jbeans.riderundertakesride.JRiderUndertakesRideEntityService;
import de.avci.openrideshare.errorhandling.OpenRideShareException;
import de.fhg.fokus.openride.customerprofile.CustomerEntity;
import de.fhg.fokus.openride.matching.MatchEntity;
import de.fhg.fokus.openride.rides.driver.DriverUndertakesRideControllerLocal;
import de.fhg.fokus.openride.rides.driver.DriverUndertakesRideEntity;
import de.fhg.fokus.openride.rides.driver.RoutePointEntity;
import de.fhg.fokus.openride.rides.driver.WaypointEntity;
import de.fhg.fokus.openride.rides.rider.RiderUndertakesRideEntity;
import de.fhg.fokus.openride.routing.Coordinate;
import de.fhg.fokus.openride.routing.Route;
import de.fhg.fokus.openride.routing.RoutePoint;
import de.fhg.fokus.openride.routing.RouterBeanLocal;
import de.avci.openrideshare.errorhandling.ErrorCodes;

/**
 * Service
 *
 * @author jochen
 */
public class JDriverUndertakesRideEntityService {

	Logger logger = Logger.getLogger("" + this.getClass());

	/**
	 * Lookup DriverUndertakesRideControllerLocal Bean that controls my offers.
	 *
	 * @return
	 */
	protected DriverUndertakesRideControllerLocal lookupDriverUndertakesRideControllerBeanLocal() {
		try {
			javax.naming.Context c = new InitialContext();
			return (DriverUndertakesRideControllerLocal) c
					.lookup("java:global/OpenRideServer/OpenRideServer-ejb/DriverUndertakesRideControllerBean!de.fhg.fokus.openride.rides.driver.DriverUndertakesRideControllerLocal");
		} catch (NamingException ne) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"exception caught", ne);
			throw new RuntimeException(ne);
		}

	}

	/**
	 * Lookup RouterBean to find Route for new offer
	 *
	 * @return
	 */
	protected RouterBeanLocal lookupRouterBeanLocal() {
		try {
			javax.naming.Context c = new InitialContext();
			return (RouterBeanLocal) c
					.lookup("java:global/OpenRideServer/OpenRideServer-ejb/RouterBean!de.fhg.fokus.openride.routing.RouterBeanLocal");
		} catch (NamingException ne) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"exception caught", ne);
			throw new RuntimeException(ne);
		}

	}

	/**
	 * Get a customerEntity from the current request
	 *
	 * @return
	 */
	public CustomerEntity getCustomerEntity() {
		return (new JCustomerEntityService()).getCustomerEntitySafely();
	}

	/**
	 * Get a customerEntity from the current request
	 *
	 * @return
	 */
	public CustomerEntity getCustomerEntity(HttpServletRequest request) {
		return (new JCustomerEntityService())
				.getCustomerEntityFromRequest(request);
	}

	/**
	 * Get a list of Rides the current User has offered. Current user/customer
	 * is determined from HTTPRequest's AuthPrincipal.
	 *
	 * @return
	 */
	public List<DriverUndertakesRideEntity> getActiveDrivesForDriver() {

		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		return durcl.getActiveDrives(ce.getCustNickname());

	}

	/**
	 * Get a list of Rides the current User has offered. Current user/customer
	 * is determined from HTTPRequest's AuthPrincipal.
	 *
	 * @return
	 */
	public List<DriverUndertakesRideEntity> getActiveDrivesForDriver(
			HttpServletRequest request) {

		CustomerEntity ce = this.getCustomerEntity(request);
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		return durcl.getActiveDrives(ce.getCustNickname());

	}

	/**
	 * Get a list of open active Rides the current user has offered. Current
	 * user/customer is determined from HTTPRequest's AuthPrincipal.
	 *
	 * @return
	 */
	public List<DriverUndertakesRideEntity> getOpenDrivesForDriver() {

		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		return durcl.getActiveOpenDrives(ce.getCustNickname());
	}

	/**
	 * Get a list all Drives of this driver. Current user/customer is determined
	 * from HTTPRequest's AuthPrincipal.
	 *
	 * @return
	 */
	public List<DriverUndertakesRideEntity> getDrivesForDriver() {

		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		if (ce == null) {
			throw new Error("Cannot determine Drives, customerEntity is null");
		}

		if (ce.getCustNickname() == null) {
			throw new Error("Cannot determine Drives, customerNickname is null");
		}

		return durcl.getDrivesForDriver(ce.getCustNickname());

	}

	/**
	 * Get a list all Drives of this driver. Current user/customer is determined
	 * from HTTPRequest's AuthPrincipal.
	 *
	 * @return
	 */
	public List<JDriverUndertakesRideEntity> getDrivesInInterval(
			Date startDate, Date endDate) {

		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		if (ce == null) {
			throw new Error("Cannot determine Drives, customerEntity is null");
		}

		if (ce.getCustId() == null) {
			throw new Error("Cannot determine Drives, customerId is null");
		}

		List<DriverUndertakesRideEntity> preres = durcl.getDrivesInInterval(ce,
				startDate, endDate);

		// cast results from DriverUndertakesRideEntity to
		// JDriverUndertakesRideEntity

		List<JDriverUndertakesRideEntity> res = new LinkedList<JDriverUndertakesRideEntity>();
		Iterator<DriverUndertakesRideEntity> it = preres.iterator();

		while (it.hasNext()) {
			JDriverUndertakesRideEntity jdure = new JDriverUndertakesRideEntity();
			jdure.updateFromDriverUndertakesRideEntity(it.next());
			res.add(jdure);
		}

		return res;
	} // getDrivesInInterval

	/**
	 * Safely get the Drive with given ID.
	 *
	 * Current user/customer is determined from HTTPRequest's AuthPrincipal.
	 *
	 * @return
	 */
	public DriverUndertakesRideEntity getDriveByIdSafely(int id) {

		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		if (ce == null) {
			throw new Error("Cannot determine Drives, customerEntity is null");
		}

		if (ce.getCustNickname() == null) {
			throw new Error("Cannot determine Drives, customerNickname is null");
		}

		DriverUndertakesRideEntity dure = durcl.getDriveByDriveId(id);

		if (dure.getCustId().getCustId() != ce.getCustId()) {
			throw new Error(
					"Cannot retrieve Drive with given ID, object does not belong to user");
		}

		return dure;

	} // getDriveByIdSafely(int id)

	/**
	 * Safely update JDriverUndertakesRideEntity from database
	 *
	 *
	 *
	 * @param rideId
	 *            ride Id from the DriverUndertakesRideEntity providing the
	 *            data. If this is null, simply no update will be done.
	 *
	 * @param jdure
	 *            JDriverUndertakesRideEntity to be updated with data from
	 *            database
	 */
	public void updateJDriverUndertakesRideEntityByIDSafely(Integer rideId,
			JDriverUndertakesRideEntity jdure) {

		if (rideId == null) {
			// nothing to do in that case!
			return;
		}

		DriverUndertakesRideEntity dure = this.getDriveByIdSafely(rideId);
		// update
		jdure.updateFromDriverUndertakesRideEntity(dure);
	}

	/**
	 * Get RoutePoints for Drive with DriveId
	 *
	 * @param driveId
	 * @return
	 */
	public JRoutePointsEntity getRoutePointsForDrive(int driveId) {

		//
		// , if drive does really belong to the calling user
		//
		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		if (ce == null) {
			throw new Error("Cannot determine Drives, customerEntity is null");
		}

		if (ce.getCustNickname() == null) {
			throw new Error("Cannot determine Drives, customerNickname is null");
		}

		DriverUndertakesRideEntity dure = durcl.getDriveByDriveId(driveId);

		if (dure.getCustId().getCustId() != ce.getCustId()) {
			throw new Error(
					"Cannot retrieve Drive with given ID, object does not belong to user");
		}

		//
		// done with ing for user
		//

		List<RoutePointEntity> routePoints = durcl.getRoutePoints(driveId);

		JRoutePointsEntity res = new JRoutePointsEntity();
		res.setRoutePoints(routePoints);

		return res;

	}

	/**
	 * getRequiredRoutePoints for drive with given driveID
	 *
	 * @param rideID
	 * @return
	 */
	JRoutePointsEntity getRequiredRoutePointsForDrive(int driveId) {

		//
		// , if drive does really belong to the calling user
		//
		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		if (ce == null) {
			throw new Error("Cannot determine Drives, customerEntity is null");
		}

		if (ce.getCustNickname() == null) {
			throw new Error("Cannot determine Drives, customerNickname is null");
		}

		DriverUndertakesRideEntity dure = durcl.getDriveByDriveId(driveId);

		if (dure.getCustId().getCustId() != ce.getCustId()) {
			throw new Error(
					"Cannot retrieve Drive with given ID, object does not belong to user");
		}

		//
		// done with ing for user
		//

		List<RoutePointEntity> routePoints = durcl
				.getRequiredRoutePoints(driveId);

		JRoutePointsEntity res = new JRoutePointsEntity();
		res.setRoutePoints(routePoints);
		return res;

	}

	/**
	 * getWaypoints for given drive in a JWaypointsContainer.
	 *
	 * @param rideID
	 * @return
	 */
	JWaypointsEntity getWaypointsForDrive(int driveId) {

		//
		// , if drive does really belong to the calling user
		//
		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		if (ce == null) {
			throw new Error("Cannot determine Drives, customerEntity is null");
		}

		if (ce.getCustNickname() == null) {
			throw new Error("Cannot determine Drives, customerNickname is null");
		}

		DriverUndertakesRideEntity dure = durcl.getDriveByDriveId(driveId);

		if (dure.getCustId().getCustId() != ce.getCustId()) {
			throw new Error(
					"Cannot retrieve Drive with given ID, object does not belong to user");
		}

		// done with ing for user

		List<WaypointEntity> waypoints = durcl.getWaypoints(dure);
		JWaypointsEntity res = new JWaypointsEntity();

		for (WaypointEntity w : waypoints) {
			if (w != null) {
				res.add(new JWaypointEntity(w));
			}
		}

		Collections.sort(res);
		return res;
	}

	public JRoutePointsEntity findRoute(DriverUndertakesRideEntity dure) {

		//
		// , if drive does really belong to the calling user
		//

		CustomerEntity ce = this.getCustomerEntity();
		RouterBeanLocal rbl = this.lookupRouterBeanLocal();

		if (ce == null) {
			throw new Error("Cannot find route, customerEntity is null");
		}

		Coordinate startC = new Coordinate(dure.getRideStartpt().getY(), dure
				.getRideStartpt().getX());

		Coordinate endC = new Coordinate(dure.getRideEndpt().getY(), dure
				.getRideEndpt().getX());

		Double threshold = 1d;

		Route route = rbl.findRoute(startC, endC, new java.sql.Timestamp(dure
				.getRideStarttime().getTime()), true, threshold);

		RoutePoint[] routePoints = route.getRoutePoints();

		List<RoutePointEntity> routePointsEntities = new LinkedList<RoutePointEntity>();

		for (int i = 0; i < routePoints.length; i++) {

			RoutePointEntity rpe = new RoutePointEntity();
			rpe.setLatitude(routePoints[i].getCoordinate().getLatititude());
			rpe.setLongitude(routePoints[i].getCoordinate().getLongitude());
			routePointsEntities.add(rpe);
		}

		JRoutePointsEntity res = new JRoutePointsEntity();
		res.setRoutePoints(routePointsEntities);
		return res;
	}

	
	/**
	 * Add a new Drive (rsp: driverUndertakesRideEntity) safely to the
	 * persistence layer. As usually, safely here means that the calling user is
	 * determined from http request and will get set as driver from serverside
	 *
	 * Note that the Route for this ride will not be transferred as a parameter,
	 * but will be autocreated. TODO: dealing with the route as depicted above
	 * might not be what we want in the long run, so this should be revisited.
	 *
	 *
	 *
	 *
	 *
	 *
	 * @param jdure
	 * @return
	 */
	public int addDriveSafely(JDriverUndertakesRideEntity jdure) {

		//  integrity of Offer
		      
		try { this.lookupDriverUndertakesRideControllerBeanLocal().checkDriverUndertakesRideEntity(jdure);
		} catch (OpenRideShareException exc) {
			// set error status on argument and return
			jdure.setErrorCode(exc.getErrorCode());
			return JDriverUndertakesRideEntity.UNINITIALIZED;
		}
		
	
		//
		// if drive does really belong to the calling user
		//

		CustomerEntity ce = this.getCustomerEntity();
		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		
		
		
		// startpoint coordinates 0/0 probably means uninitialized rather then pole
		if(jdure.getRideStartpt().getX()==0d && jdure.getRideStartpt().getY()==0d){
			
			jdure.setErrorCode(ErrorCodes.CreateOfferFailure_RideStartPointNull_Str);
			logger.log(Level.SEVERE, "Startpoint not initialized while adding offer");
			return DriverUndertakesRideEntity.UNINITIALIZED;
		}
		
		// startpoint coordinates 0/0 probably means uninitialized rather then pole
		// TODO: produce a more decent error state
		if(jdure.getRideEndpt().getX()==0d && jdure.getRideEndpt().getY()==0d){
			
			jdure.setErrorCode(ErrorCodes.CreateOfferFailure_RideEndpointNull_Str);
			logger.log(Level.SEVERE, "Endpoint not initialized while adding offer");
			return DriverUndertakesRideEntity.UNINITIALIZED;
		}
		

		try {
			
			return durcl.addRide(
			// Customer ID
					ce.getCustId(),
					// Point ridestartPt
					jdure.getRideStartpt(),
					// Point rideendPt
					jdure.getRideEndpt(),
					// Point[] intermediatePoints
					jdure.getIntermediatePoints(),
					// waypointds
					jdure.getWaypoints(),
					// java.sql.Date ridestartTime
					new java.sql.Date(jdure.getRideStarttime().getTime()),
					// String rideComment
					jdure.getRideComment(),
					// Integer acceptableDetourInMin
					jdure.getRideAcceptableDetourInMin(),
					// Integer acceptableDetourKm
					jdure.getRideAcceptableDetourInM(),
					// Integer acceptableDetourPercent,
					jdure.getRideAcceptableDetourInPercent(),
					// int offeredSeatsNo,
					jdure.getRideOfferedseatsNo(),
					// String startptAddress,
					jdure.getStartptAddress(),
					// String endptAddress
					jdure.getEndptAddress());
				
		} catch (OpenRideShareException exc) {

			jdure.setErrorCode(exc.getErrorCode());
			logger.log(Level.SEVERE, "ORS Exception while adding offer : "+exc.getMessage(),exc);
			return JDriverUndertakesRideEntity.UNINITIALIZED;	
			
		} catch(Exception exc){
			
			jdure.setErrorCode(ErrorCodes.UnknownError_Str);
			logger.log(Level.SEVERE, "Unexpected Exception while adding offer       : "+exc.getMessage(), exc);
			return JRiderUndertakesRideEntity.UNINITIALIZED;
		}
			
	}
	
	

	/**
	 * True, if drive has been updated since last driver access, else false.
	 * Small wrapper to "isDriveUpdated" in DriverUndertakesRideControllerLocal
	 *
	 * @param rideId
	 * @return
	 */
	public boolean isDriveUpdated(Integer rideId) {

		return lookupDriverUndertakesRideControllerBeanLocal().isDriveUpdated(
				rideId);
	}

	/**
	 * Get Drives for calling driver that are scheduled to start after a given
	 * point in time. The driver is safely determined from the remoteUser of the
	 * calling HTTPRequest.
	 *
	 *
	 * @param rideStarttime
	 *            times starting after starttime will be returned.
	 *
	 * @return list of all rides the caller has offered after startime, order
	 *         descending by starttime
	 */
	public List<JDriverUndertakesRideEntity> getDrivesAfterTimeSafely(
			Date rideStarttime) {

		CustomerEntity ce = this.getCustomerEntity();
		if (ce == null) {
			throw new Error("Cannot list ride offers, driver Id is null !");
		}

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		List<DriverUndertakesRideEntity> preres = durcl.getDrivesAfterTime(ce,
				rideStarttime);

		// cast results from DriverUndertakesRideEntity to
		// JDriverUndertakesRideEntity

		List<JDriverUndertakesRideEntity> res = new LinkedList<JDriverUndertakesRideEntity>();
		Iterator<DriverUndertakesRideEntity> it = preres.iterator();

		while (it.hasNext()) {

			JDriverUndertakesRideEntity jdure = new JDriverUndertakesRideEntity();
			jdure.updateFromDriverUndertakesRideEntity(it.next());
			res.add(jdure);
		}

		return res;
	}

	/**
	 * Invalidate/cancel/countermand the ride with given Rideid. The identity is
	 * ed from http request. Invalidation is
	 *
	 *
	 * @param rideId
	 *            id of the drive to be invalidated.
	 *
	 * @return true if ride was invalidated, else false
	 */
	public boolean invalidateOfferSavely(int rideId) {

		CustomerEntity ce = this.getCustomerEntity();

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		DriverUndertakesRideEntity due = durcl.getDriveByDriveId(rideId);

		// Sanity , caller of this method must be owner of this offer

		if (ce.getCustId() != due.getCustId().getCustId()) {
			throw new Error(
					"Attempt to invalidate Offer that is not owned by User");
		}

		return durcl.invalidateRide(rideId);
	}

	/**
	 * Add a waypoint to Drive *safely*. Safely means that it gets ed
	 * wether the caller is owner
	 *
	 *
	 */
	public void addWaypointToDriveSafely(JWaypointEntity waypoint) {

		CustomerEntity ce = this.getCustomerEntity();

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		DriverUndertakesRideEntity due = waypoint.getRideId();

		// Sanity , caller of this method must be owner of this offer

		if (ce.getCustId() != due.getCustId().getCustId()) {
			throw new Error("Attempt to change Offer that is not owned by User");
		}

		// call DriverUndertakesRideControllerLocal to do the real job
		durcl.addWaypoint(due.getRideId(), waypoint.extractWaypoint(),
				waypoint.getPosition());
	}

	/**
	 * remove a waypoint from Drive *safely*. Safely means that it gets ed
	 * wether the caller is entitled to do this operation or not.
	 *
	 *
	 *
	 * @param rideId
	 * @param routeIdx
	 */
	public void removeWaypointFromDriveSafely(int rideId, int routeIdx) {

		CustomerEntity ce = this.getCustomerEntity();

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		DriverUndertakesRideEntity due = durcl.getDriveByDriveId(rideId);

		// Sanity , caller of this method must be owner of this offer

		if (ce.getCustId() != due.getCustId().getCustId()) {
			throw new Error("Attempt to change Offer that is not owned by User");
		}

		// call DriverUndertakesRideControllerLocal to do the real job
		durcl.removeWaypoint(rideId, routeIdx);

	}

	/**
	 * fetch a list of mutually accepted pickups for Drive given by
	 * riderundertakesrideentity
	 *
	 *
	 * @param rideId
	 *            rideId of the DriverUndertakesRideEntity to be ed
	 * @return
	 */
	public List<JMatchingEntity> getAcceptedMatches(Integer rideId) {

		CustomerEntity ce = this.getCustomerEntity();

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		DriverUndertakesRideEntity due = durcl.getDriveByDriveId(rideId);

		// Sanity , caller of this method must be owner of this offer

		if (ce.getCustId() != due.getCustId().getCustId()) {
			throw new Error("Attempt to set matches, but caller is not driver!");
		}

		// call DriverUndertakesRideControllerLocal to do the real job
		ArrayList<JMatchingEntity> result = new ArrayList<JMatchingEntity>();

		for (MatchEntity me : durcl.getAcceptedMatches(rideId)) {
			result.add(new JMatchingEntity(me));
		}

		return result;
	}

	/**
	 * true, if some accepted Matches exist for the ride given by rideId
	 * 
	 * @param rideId
	 *            of given DriverUndertakesRideEntity .
	 * @return
	 */
	public boolean getAcceptedMatchesExist(int rideId) {
		return this.getAcceptedMatches(rideId).size() > 0;
	}

	/**
	 * Count the number of accepted rides for given drive.
	 * 
	 * @param rideId
	 *            of given DriverUndertakesRideEntity .
	 * @return number of accepted rides for this drive.
	 */
	public Integer getAcceptedMatchesCount(int rideId) {
		return this.getAcceptedMatches(rideId).size();
	}

	/**
	 * Re-count the filter for future matches
	 * 
	 * @param aThis
	 */
	void updateJFilteredDriveList(JDriveFilteredLists myList) {
		List<JDriverUndertakesRideEntity> futureDrives = this
				.getDrivesAfterTimeSafely(new Date(System.currentTimeMillis()));
		myList.setAllDrives(futureDrives);
	}

	/**
	 * Return number of offers that the caller currently can issue.
	 * 
	 * @return
	 */
	public Integer getNoLeftOffers() {

		CustomerEntity ce = this.getCustomerEntity();

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();

		return durcl.noOfLeftOffers(ce.getCustId());
	}

	/**
	 * Return true, if caller currently can issue another offer.
	 * 
	 * @return
	 */
	public Boolean canAddOffer() {

		CustomerEntity ce = this.getCustomerEntity();

		DriverUndertakesRideControllerLocal durcl = this
				.lookupDriverUndertakesRideControllerBeanLocal();
		return durcl.canAddOffer(ce.getCustId());
	}

} // class

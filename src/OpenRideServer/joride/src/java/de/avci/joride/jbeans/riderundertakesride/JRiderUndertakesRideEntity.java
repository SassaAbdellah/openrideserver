package de.avci.joride.jbeans.riderundertakesride;

import de.fhg.fokus.openride.matching.RideNegotiationConstants;
import de.avci.joride.constants.JoRideConstants;
import de.avci.joride.jbeans.auxiliary.RideSearchParamsBean;
import de.avci.joride.jbeans.customerprofile.JCustomerEntity;
import de.avci.joride.jbeans.customerprofile.JCustomerEntityService;
import de.avci.joride.jbeans.matching.JMatchingEntity;
import de.avci.joride.jbeans.matching.JMatchingEntityService;
import de.avci.joride.jbeans.matching.JMatchingSorter4Driver;
import de.avci.joride.jbeans.matching.JMatchingSorter4Rider;
import de.avci.joride.utils.CRUDConstants;
import de.avci.joride.utils.HTTPUtil;
import de.avci.joride.utils.PropertiesLoader;
import de.avci.joride.utils.WebflowPoint;
import de.fhg.fokus.openride.customerprofile.CustomerEntity;
import de.fhg.fokus.openride.matching.MatchEntity;
import de.fhg.fokus.openride.matching.MatchingStatistics;
import de.fhg.fokus.openride.rides.rider.RiderUndertakesRideEntity;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import org.postgis.Point;
import org.primefaces.event.FlowEvent;

/**
 * Wrapper to make RideUndertakesRideEntity availlable as a JSFBean
 * 
 * 
 * This also governs the wizard view when creating a new request.
 * 
 * 
 * 
 * @author jochen
 * 
 * 
 * 
 */
@Named
@SessionScoped
public class JRiderUndertakesRideEntity extends RiderUndertakesRideEntity
		implements Serializable {

	private PropertiesLoader propertiesLoader = new PropertiesLoader();
	private Logger log = Logger.getLogger("" + this.getClass());

	/**
	 * Matches come in form of MatchEntity. To turn the matches into JSF
	 * compliant, JMatchingEntinties we use this property (to be lazily
	 * instantiated)
	 */
	private List<JMatchingEntity> jMatches = null;

	/**
	 * A date format for formatting start and end date. Created via lazy
	 * instantiation.
	 * 
	 * @deprecated should be done centrally in utils* class
	 * 
	 */
	protected DateFormat dateTimeFormat = (new JoRideConstants())
			.createDateTimeFormat();

	protected DateFormat getDateTimeFormat() {
		return dateTimeFormat;
	}

	/**
	 * Return a nicely formatted version of the startDate
	 * 
	 * @return
	 */
	public String getStartDateFormatted() {
		return getDateTimeFormat().format(this.getStarttimeEarliest());
	}

	/**
	 * if comment property is null, replace it with an empty string rather than
	 * null to avoid Nullpointertrouble in backend. Also, remove leading and
	 * trailing blanks
	 */
	protected void cleanseComment() {

		if (this.getComment() == null) {
			this.setComment("");
		}

		this.setComment(this.getComment().trim());
	}

	/**
	 * if price property is null, replace it with "Double.NaN" to avoid
	 * Nullpointertrouble in backend. Also, remove leading and trailing blanks
	 */
	protected void cleansePrice() {

		if (this.getPrice() == null) {
			this.setPrice(Double.NaN);
		}

		this.setComment(this.getComment().trim());
	}

	/**
	 * Update from a given RiderUndertakesRideEntity object.
	 * 
	 * @param rure
	 *            RiderUndertakesRideEntity to update from
	 */
	public void updateFromRiderUndertakesRideEntity(
			RiderUndertakesRideEntity rure) {

		if (rure == null) {

			log.log(Level.WARNING,
					"refusing to update JRiderUndertakesRideEntity, argument is null ");
			return;
		}

		// private Integer riderrouteId;
		this.setRiderrouteId(rure.getRiderrouteId());
		// private Integer givenrating;
		this.setGivenrating(rure.getGivenrating());
		// private String givenratingComment;
		this.setGivenratingComment(rure.getGivenratingComment());
		// private Date givenratingDate;
		this.setGivenratingDate(rure.getGivenratingDate());
		// private Integer receivedrating;
		this.setReceivedrating(rure.getReceivedrating());
		// private String receivedratingComment;
		this.setReceivedratingComment(rure.getReceivedratingComment());
		// private Date receivedratingDate;
		this.setReceivedratingDate(rure.getReceivedratingDate());
		// private Date starttimeEarliest;
		this.setStarttimeEarliest(rure.getStarttimeEarliest());
		// private Date starttimeLatest;
		this.setStarttimeLatest(rure.getStarttimeLatest());
		// private Date timestampbooked
		this.setTimestampbooked(rure.getTimestampbooked());
		// private Date timestamprealized;
		this.setTimestamprealized(rure.getTimestamprealized());
		// private Point startpt;
		this.setStartpt(rure.getStartpt());
		// private String startptAddress;
		this.setStartptAddress(rure.getStartptAddress());
		// private Point endpt;
		this.setEndpt(rure.getEndpt());
		// private String endptAddress;
		this.setEndptAddress(rure.getEndptAddress());
		// private Double price;
		this.setPrice(rure.getPrice());
		// private Integer noPassengers;
		this.setNoPassengers(rure.getNoPassengers());
		// private CustomerEntity custId;
		this.setCustId(rure.getCustId());
		// private DriverUndertakesRideEntity rideId;
		this.setRideId(rure.getRideId());
		// private String comment;
		this.setComment(rure.getComment());
		// matchings
		this.setMatchings(rure.getMatchings());
		// enforce recalculatine matches next time they are needed
		this.jMatches = null;

	} // public void updateFromRiderUndertakesRideEntit

	/**
	 * Lists *all* rides this customer has ever requested
	 * 
	 * @return
	 */
	public List<JRiderUndertakesRideEntity> getRidesForRider() {

		return (new JRiderUndertakesRideEntityService()).getRidesForRider();
	}

	/**
	 * Lists *all* **active** **open** rides for this customer. I.e: Rides which
	 * have lastStartTime still in the future, and are not booked.
	 * 
	 * @return list of all Active OpenRides
	 */
	public List<JRiderUndertakesRideEntity> getActiveOpenRidesForRider() {

		List<RiderUndertakesRideEntity> activeOpenRides = (new JRiderUndertakesRideEntityService())
				.getActiveOpenRides();

		List<JRiderUndertakesRideEntity> res = new LinkedList<JRiderUndertakesRideEntity>();

		Iterator<RiderUndertakesRideEntity> it = activeOpenRides.iterator();

		while (it.hasNext()) {
			JRiderUndertakesRideEntity jrue = new JRiderUndertakesRideEntity();
			jrue.updateFromRiderUndertakesRideEntity(it.next());
			res.add(jrue);
		}

		return res;
	}

	/**
	 * Update *this* with the Data read in from database for given id, or just
	 * do nothing if ID is null.
	 * 
	 * @param id
	 *            rideId of the DriverUndertakeRide Entity to update from.
	 */
	public void updateFromRiderRouteId(Integer myRiderRouteId) {

		JRiderUndertakesRideEntityService service = new JRiderUndertakesRideEntityService();
		service.updateJRiderUndertakesRideEntityByRiderRouteIDSavely(
				myRiderRouteId, this);

	}

	/**
	 * See, if the CRUDConstants().getParamNameCrudId() parameter is present in
	 * HTTPRequest. If the ID parameter is != null, then update data from
	 * RriverUndertakesRideEntity in database with riderRouteId given by id
	 * parameter. If parameter's value is not null, then leave **this**
	 * untouched
	 * 
	 */
	public void update() {

		String idStr = (new HTTPUtil())
				.getParameterSingleValue(new CRUDConstants()
						.getParamNameCrudId());

		int id = 0;

		try {
			id = new Integer(idStr).intValue();
			this.updateFromRiderRouteId(id);
		} catch (java.lang.NumberFormatException exc) {
			log.log(Level.WARNING,
					"ID Parameter does not contain Numeric Value, value was : "
							+ idStr);
		}

	}

	/**
	 * Initialize a *new* RiderUntertakesRideEntity, which is not yet in the
	 * database
	 * 
	 */
	public void initializeNewRide() {

		this.setRiderrouteId(null);

		// naturally, we cannot start earlier then now
		if (this.getStarttimeEarliest() == null) {
			this.setStarttimeEarliest(new Date(System.currentTimeMillis()));
		}

		// two hours from now seems to be a good default
		if (this.getStarttimeLatest() == null) {
			this.setStarttimeLatest(new Date(System.currentTimeMillis()
					+ (1000 * 60 * 60 * 2)));
		}

		this.setNoPassengers(1);
	}

	/**
	 * Value for point.target parameters. If "Startpoint" ist set, then
	 * smartUpdate will set the startpoint
	 */
	private static final String paramValueTargetStartpoint = "STARTPOINT";

	/**
	 * Trivial Accessor making paramValueStartpoint accessible with JSF Methods
	 * 
	 * @return
	 */
	public String getParamValueTargetStartpoint() {
		return paramValueTargetStartpoint;
	}

	/**
	 * Value for point.target parameters. If "Endpoint" ist set, then
	 * smartUpdate will set the startpoint
	 */
	private static final String paramValueTargetEndpoint = "ENDPOINT";

	/**
	 * Trivial Accessor making paramValueStartpoint accessible with JSF Methods
	 * 
	 * @return
	 */
	public String getParamValueTargetEndpoint() {
		return paramValueTargetEndpoint;
	}

	/**
	 * Return the Longitude of the rideStartpt , or null if the rideStartpt is
	 * null;
	 */
	public double getLongitudeStart() {

		if (this.getStartpt() == null) {
			return new Double(0);
		}
		return new Double(getStartpt().getX());
	}

	/**
	 * Return the Latitude of the rideStartpt , or null if the rideStartpt is
	 * null;
	 */
	public double getLatitudeStart() {

		if (this.getStartpt() == null) {
			return new Double(0);
		}
		return new Double(getStartpt().getY());
	}

	/**
	 * Return the Longitude of the rideEndpt , or null if the rideEndpt is null;
	 */
	public double getLongitudeEnd() {

		if (this.getEndpt() == null) {
			return new Double(0);
		}
		return new Double(getEndpt().getX());
	}

	/**
	 * Return the Latitude of the rideStartpt , or null if the rideStartpt is
	 * null;
	 */
	public double getLatitudeEnd() {

		if (this.getEndpt() == null) {
			return new Double(0);
		}
		return new Double(getEndpt().getY());
	}

	/**
	 * set the latitude of the rideStartpt
	 */
	public void setLongitudeStart(double arg) {

		if (this.getStartpt() == null) {
			this.setStartpt(new Point(arg, 0));
		}

		this.getStartpt().setX(arg);
	}

	/**
	 * set the latitude of the rideStartpt
	 */
	public void setLatitudeStart(double arg) {

		if (this.getStartpt() == null) {
			this.setStartpt(new Point(0, arg));
		}

		this.getStartpt().setY(arg);
	}

	/**
	 * set the latitude of the rideStartpt
	 */
	public void setLongitudeEnd(double arg) {

		if (this.getEndpt() == null) {
			this.setEndpt(new Point(arg, 0));
		}

		this.getEndpt().setX(arg);
	}

	/**
	 * set the latitude of the rideStartpt
	 */
	public void setLatitudeEnd(double arg) {

		if (this.getEndpt() == null) {
			this.setEndpt(new Point(0, arg));
		}

		this.getEndpt().setY(arg);
	}

	/**
	 * Update bean, thereby evaluating the HTTPRequest and update startpoint or
	 * endpoint data depending on params present in HTTPRequest
	 */
	public void smartUpdate() {

		WebflowPoint webflowPoint = new WebflowPoint();
		webflowPoint.smartUpdate();

		//
		// see, if we should update the startpoints
		//

		if (paramValueTargetStartpoint.equals(webflowPoint.getTarget())) {

			if (
					(webflowPoint.getParamAddress() != null) 
					&&
					(isNullOrEmpty(this.getStartptAddress()))
			  ) {
				this.setStartptAddress(webflowPoint.getAddress());
			}

			// Set Start/End Latitude depending on target param
			if (webflowPoint.getLat() != null) {
				this.setLatitudeStart(webflowPoint.getLat());
			}

			// Set Start/End Longitude depending on target param
			if (webflowPoint.getLon() != null) {
				this.setLongitudeStart(webflowPoint.getLon());
			}

		} // if(paramValueTargetStartpoint.equals(webflowPoint.getTarget()))

		//
		// see, if we should update the endpoints
		//

		if (paramValueTargetEndpoint.equals(webflowPoint.getTarget())) {

			if (
				(webflowPoint.getParamAddress() != null)
				&&
				(isNullOrEmpty(this.getEndptAddress()))
			) {
				this.setEndptAddress(webflowPoint.getAddress());
			}

			// Set Start/End Latitude depending on target param
			if (webflowPoint.getLat() != null) {
				this.setLatitudeEnd(webflowPoint.getLat());
			}

			// Set Start/End Longitude depending on target param
			if (webflowPoint.getLon() != null) {
				this.setLongitudeEnd(webflowPoint.getLon());
			}

		} // if(paramValueTargetEndpoint.equals(webflowPoint.getTarget()))

	} // smartUpdate

	/**
	 * Create a new RiderUndertakesRideEntity and save it to the Database.
	 * Return the Id of the newly created database.
	 * 
	 * @return id of the newly create DriverUndertakesRideEntity
	 */
	public int addToDB() {

		if (this.getRiderrouteId() != null) {
			throw new Error(
					"Cannot add Ride to Database, riderrouteid already exists");
		}

		JRiderUndertakesRideEntityService jrures = new JRiderUndertakesRideEntityService();

		int my_id = jrures.addRideRequest(this);

		this.setRiderrouteId(new Integer(my_id));

		return this.getRiderrouteId();

	} // addToDB

	public void doCrudAction(ActionEvent evt) {

		HTTPUtil hru = new HTTPUtil();

		log.log(Level.FINE, "doCrudAction Event : " + evt.toString());

		String action = hru.getParameterSingleValue((new CRUDConstants())
				.getParamNameCrudAction());
		log.log(Level.FINE, "Param Action : " + action);

		String id = hru.getParameterSingleValue((new CRUDConstants())
				.getParamNameCrudId());
		log.log(Level.FINE, "Param ID     : " + id);

		// Deleting is not yet implemented,
		//
		// if (CRUDConstants.PARAM_VALUE_CRUD_DELETE.equals(action)) {
		// this.delete(new Integer(id).intValue());
		// }

		if (CRUDConstants.PARAM_VALUE_CRUD_CREATE.equals(action)) {
			this.addToDB();
		}

	} // doCrudAction()

	/**
	 * Accessor with lazy instantiation
	 * 
	 * @return
	 */
	public List<JMatchingEntity> getJMatches() {

		if (this.jMatches == null) {
			this.jMatches = new LinkedList<JMatchingEntity>();
			for (MatchEntity m : this.getMatchings()) {
				this.jMatches.add(new JMatchingEntity(m));
			}
		}
		Collections.sort(this.jMatches, new JMatchingSorter4Rider());
		return this.jMatches;
	}


	/**
	 * Returns true, if this ride has been updated
	 * 
	 * @return Returns the Number of OpenMatches for this RideRequest
	 */
	public boolean getRideUpdated() {
		return (new JRiderUndertakesRideEntityService()).isRideUpdated(this
				.getRiderrouteId());
	}

	/**
	 * Short message to be displayed if ride has an update
	 */
	public String getUpdatedShortcut() {

		
		if (this.getRideUpdated()) {
			Locale locale=new HTTPUtil().detectBestLocale();
			return " "
					+ PropertiesLoader.getMessageProperties(locale).getProperty("updatedRideShort");
		}

		return "  ";
	}

	/**
	 * Returns the Number of OpenMatches for this RideRequest
	 * 
	 * @return Returns the Number of OpenMatches for this RideRequest
	 */
	public int getNoMatches() {
		if (this.getMatchings() == null) {
			return 0;
		}
		return this.getMatchings().size();
	}

	/**
	 * @return Returns true, if the number of Matches is > 0, else false
	 * 
	 */
	public boolean getHasMatches() {

		if (this.getMatchings() == null) {
			return false;
		}
		return this.getMatchings().size() > 0;
	}

	/**
	 * Invalidate/countermand rideRequest with given riderroute id.
	 * 
	 * 
	 * Returns "rider" to move back to "rider" page if removal was successful,
	 * else returns null. May frequently fail, if there are open Matches for
	 * this ride.
	 * 
	 */
	public void invalidate(ActionEvent evt) {

		Integer idToRemove = this.getRiderrouteId();
		boolean result = false;

		try {
			result = new JRiderUndertakesRideEntityService()
					.removeRideSafely(this);
		} catch (Exception exc) {
			// TODO: add a message why this failed
			log.log(Level.SEVERE, "invalidating ride " + this.getCustId()
					+ " failed with unknown exception", exc);
		}

		PropertiesLoader loader = new PropertiesLoader();

		if (result) {
			log.info("invalidating ride request with id " + idToRemove);
		} else {
			// TODO: add a JSF message why this failed
			log.log(Level.SEVERE, "removing ride request " + idToRemove
					+ " failed");
		}

	} // remove ride

	/**
	 * Determines the caller from http-request, and if caller is stakeholder
	 * (either rider or driver) then returns true
	 * 
	 * @return true, if caller is identical to rider or diver, and if there is
	 *         an associated ride, else false
	 */
	public boolean isRateable() {

		if (!this.getHasDriverUndertakesRideEntity()) {
			return false; // nothing to be rated
		}

		JRiderUndertakesRideEntityService jrureService = new JRiderUndertakesRideEntityService();

		Integer id = this.getRiderrouteId();

		if (id == null) {
			log.log(Level.SEVERE,
					"Cannot determine rateable status, id is null");
			return false;
		}

		if (jrureService.callerIsRider(id)) {
			return true;
		}

		if (jrureService.callerIsDriver(id)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if links for rider ratings should be presented
	 * 
	 * @return true, if caller is rider and ride is rateable
	 * 
	 */
	public boolean isRiderRateable() {
		return (this.isRateable() && this.isCallerIsRider());
	}

	/**
	 * Checks if links for driver ratings should be presented
	 * 
	 * @return true, if caller is rider and ride is rateable
	 * 
	 */
	public boolean isDriverRateable() {
		return (this.isRateable() && this.isCallerIsDriver());
	}

	/**
	 * Checks, if the HTTP Caller is identical to the driver, and if so returns
	 * true -- else false
	 * 
	 */
	public boolean isCallerIsDriver() {

		JRiderUndertakesRideEntityService jrureService = new JRiderUndertakesRideEntityService();
		Integer id = this.getRiderrouteId();

		if (id == null) {
			log.log(Level.SEVERE,
					"Cannot determine driver caller status, id is null");
			return false;
		}

		return (jrureService.callerIsDriver(id));
	}

	/**
	 * Checks, if the HTTP Caller is identical to the rider, and if so returns
	 * true -- else false
	 * 
	 */
	public boolean isCallerIsRider() {

		JRiderUndertakesRideEntityService jrureService = new JRiderUndertakesRideEntityService();
		Integer id = this.getRiderrouteId();

		if (id == null) {
			log.log(Level.SEVERE,
					"Cannot determine rider caller status, id is null");
			return false;
		}

		return (jrureService.callerIsRider(id));
	}
	
	
	/**
	 * 
	 * @return ! isNotCallerIsRider()
	 */
	public boolean isNotCallerIsRider() {
		return ! isCallerIsRider();
	}
	/**
	 * Determines the caller from http-request, and if caller is identical to
	 * rider, then allow for rider cancel
	 * 
	 * @return true, if caller is identical to rider, else false
	 */
	public boolean isRiderCancellable() {

		CustomerEntity caller = (new JCustomerEntityService())
				.getCustomerEntitySafely();

		if (this.getCustId() == null) {
			return false;
		}
		if (caller == null) {
			return false;
		}

		if (caller.getCustId() == this.getCustId().getCustId()) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if this ride has already been rated by the rider
	 * (receivedrating != null)
	 * 
	 * @return true, if receivedrating is >= 0 , else false
	 */
	public boolean isRiderRated() {

		if (this.getGivenrating() == null) {
			return false;
		}
		if (this.getGivenrating() >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if this ride has already been rated by the driver
	 * (receivedrating != null)
	 * 
	 * @return true, if receivedrating is >=0 , else false
	 */
	public boolean isDriverRated() {

		if (this.getReceivedrating() == null) {
			return false;
		}
		if (this.getReceivedrating() >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Update Rider's rating and comment for this ride
	 * 
	 */
	public void doSetGivenRating(ActionEvent evt) {

		new JRiderUndertakesRideEntityService().setGivenRatingSavely(this);
	}

	/**
	 * Update Driver's rating and comment for this ride
	 * 
	 */
	public void doSetReceivedRating(ActionEvent evt) {

		new JRiderUndertakesRideEntityService().setReceivedRatingSavely(this);
	}

	/**
	 * Mnemonic Value for
	 * 
	 * @see RideSearchParamsBean searchType Property that will make
	 *      getRideReport return a list of all rides in given Timespan
	 * 
	 */
	protected static final String RIDEREPORT_ALL_RIDES_FOR_RIDER = "All_RIDES_FOR_RIDER";

	/**
	 * Make RIDEREPORT_ALL_RIDES_FOR_RIDER mnemonic available to outside in JSF
	 * Bean Fashion.
	 * 
	 * @return RIDEREPORT_ALL_RIDES_FOR_RIDER
	 */
	public String getParamValueRidereportAllRidesForRider() {
		return RIDEREPORT_ALL_RIDES_FOR_RIDER;
	}

	/**
	 * Mnemonic Value for
	 * 
	 * @see RideSearchParamsBean searchType Property that will make
	 *      getRideReport return a list of all **realized** rides in given
	 *      Timespan
	 * 
	 */
	protected static final String RIDEREPORT_REALIZED_RIDES_FOR_RIDER = "REALIZED_RIDES_FOR_RIDER";

	/**
	 * Make RIDEREPORT_REALIZED_RIDES_FOR_RIDER mnemonic available to outside in
	 * JSF Bean Fashion.
	 * 
	 * @return RIDEREPORT_REALIZED_RIDES_FOR_RIDER
	 */
	public String getParamValueRidereportRealizedRidesForRider() {
		return RIDEREPORT_REALIZED_RIDES_FOR_RIDER;
	}

	/**
	 * Mnemonic Value for
	 * 
	 * @see RideSearchParamsBean searchType Property that will make
	 *      getRideReport return a list of all **unrated** rides in given
	 *      Timespan
	 * 
	 */
	protected static final String RIDEREPORT_UNRATED_RIDES_FOR_RIDER = "UNRATED_RIDES_FOR_RIDER";

	
	/**
	 * Mnemonic Value for
	 * 
	 * @see RideSearchParamsBean searchType Property that will make
	 *      getRideReport return a list of all **unrated** rides for driver in given
	 *      Timespan
	 * 
	 */
	protected static final String RIDEREPORT_UNRATED_RIDES_FOR_DRIVER = "UNRATED_RIDES_FOR_DRIVER";

	
	
	/**
	 * Make RIDEREPORT_UNRATED_RIDES_FOR_RIDER mnemonic available to outside in
	 * JSF Bean Fashion.
	 * 
	 * @return RIDEREPORT_UNRATED_RIDES_FOR_RIDER
	 */
	public String getParamValueRidereportUnratedRidesForRider() {
		return RIDEREPORT_UNRATED_RIDES_FOR_RIDER;
	}
	
	/**
	 * Make RIDEREPORT_UNRATED_RIDES_FOR_DRIVER mnemonic available to outside in
	 * JSF Bean Fashion.
	 * 
	 * @return RIDEREPORT_UNRATED_RIDES_FOR_DRIVER
	 */
	public String getParamValueRidereportUnratedRidesForDriver() {
		return RIDEREPORT_UNRATED_RIDES_FOR_DRIVER;
	} 
	
	

	/**
	 * Mnemonic Value for
	 * 
	 * @see RideSearchParamsBean searchType Property that will make
	 *      getRideReport return a list of all rides in given Timespan where
	 *      caller acted as driver
	 * 
	 */
	protected static final String RIDEREPORT_ALL_RIDES_FOR_DRIVER = "All_RIDES_FOR_DRIVER";

	/**
	 * Make RIDEREPORT_ALL_RIDES_FOR_DRIVER mnemonic available to outside in JSF
	 * Bean Fashion.
	 * 
	 * @return RIDEREPORT_ALL_RIDES_FOR_RIDER
	 */
	public String getParamValueRidereportAllRidesForDriver() {
		return RIDEREPORT_ALL_RIDES_FOR_DRIVER;
	}

	/**
	 * Determines the current Instance of RideSearchParamBean, and returns a
	 * list of JRiderUndertakesRideEntity Objects.
	 * 
	 * 
	 * The list of rides actually returned will be based on the value of the
	 * RideSearchParamBean's searchType property.
	 * 
	 * I.e, if this property equals:
	 * 
	 * <ul>
	 * 
	 * <li>{
	 * 
	 * @see RIDEREPORT_ALL_RIDES_FOR_RIDER returns a list of all rides for this
	 *      rider in given timespan </li>
	 * 
	 *      <li>{
	 * @see RIDEREPORT_REALIZED_RIDES_FOR_RIDER returns a list of all *realized*
	 *      rides for this rider in given timespan </li>
	 * 
	 *      <li>{
	 * @see RIDEREPORT_UNRATED_RIDES_FOR_RIDER returns a list of all unrated
	 *      rides for this rider in given timespan </li>
	 * 
	 * 
	 *      <li>{
	 * @see RIDEREPORT_ALL_RIDES_FOR_DRIVER returns a list of all rides for this
	 *      given timespan, where caller acted as a driver. </li>
	 * 
	 *      </ul>
	 * 
	 * 
	 * 
	 * @return List of Entities. See listing above.
	 * 
	 */
	public List<JRiderUndertakesRideEntity> getRideReport() {

		RideSearchParamsBean rspb0 = new RideSearchParamsBean();
		String beanName = rspb0.getBeanNameRidesearchparam();
		RideSearchParamsBean rspb = new RideSearchParamsBean()
				.retrieveCurrentTimeInterval(beanName);

		if (rspb == null) {
			log.log(Level.FINE, this.getClass()
					+ "RideSearchParamsBean is null, returning empty list");
			return new LinkedList<JRiderUndertakesRideEntity>();
		}

		String reportType = rspb.getSearchType();

		// see if we want to see **all** rides
		if (this.getParamValueRidereportAllRidesForRider().equals(reportType)) {
			return (new JRiderUndertakesRideEntityService())
					.getRidesForRiderInInterval();
		}

		// see if we want to see **realized** rides only
		if (this.getParamValueRidereportRealizedRidesForRider().equals(
				reportType)) {
			return (new JRiderUndertakesRideEntityService())
					.getRealizedRidesForRiderInInterval();
		}

		// see if we want to see **unrated** rides for rider only
		if (this.getParamValueRidereportUnratedRidesForRider().equals(
				reportType)) {
			return (new JRiderUndertakesRideEntityService())
					.getUnratedRidesForRiderInInterval();
		}

		// see if we want to see **unrated** rides for driver only
				if (this.getParamValueRidereportUnratedRidesForDriver().equals(
						reportType)) {
					return (new JRiderUndertakesRideEntityService())
							.getUnratedRidesForDriverInInterval();
				}
		
		
		// see if we want to see **drivers** rides only
		if (this.getParamValueRidereportAllRidesForDriver().equals(reportType)) {
			return (new JRiderUndertakesRideEntityService())
					.getRidesForDriverInInterval();
		}

		// if the parameter is not supported, then throw a new Error
		throw new Error("Parameter " + reportType
				+ " is  not supported in getRideReport()");

	}

	/**
	 * Length to which the lengty addresses should be shortened when displayed
	 * in title tags
	 * 
	 * TODO: make this configurable.
	 * 
	 */
	final int ShortStringLength = 45;

	public String getEndptAddressShort() {

		String endpointAddress = this.getEndptAddress();

		if (endpointAddress == null) {
			return "";
		}

		if (endpointAddress.length() <= ShortStringLength) {
			return endpointAddress;
		}

		return ((endpointAddress.substring(0, (ShortStringLength - 1))) + "...");

	}

	/**
	 * Returns a list of future riders, i.e: list of rides for calling rider,
	 * that have latestStartTime >= now.
	 * 
	 * @return list of future rides for calling rider.
	 */
	public List<JRiderUndertakesRideEntity> getFutureRidesForRider() {

		return (new JRiderUndertakesRideEntityService()
				.getFutureRidesForRider());

	}

	/**
	 * Overwrite RiderUndertakesRideEntity() to start out with negative ratings,
	 * signifying that this ride has not been rated yet
	 * 
	 */
	public JRiderUndertakesRideEntity() {

		super();

		this.setGivenrating(-1);
		this.setReceivedrating(-1);
	}

	/**
	 * 
	 * @return MatchingStatitstics Object for this ride
	 */
	@Override
	public MatchingStatistics getMatchingStatistics() {

		Integer riderrouteId = this.getRiderrouteId();
		return new JMatchingEntityService()
				.getMatchingStatisticsForRide(riderrouteId);
	}

	/**
	 * @return nicely formatted version of startTime earliest
	 */
	public String getStarttimeEarliestFormatted() {
		return this.getDateTimeFormat().format(this.getStarttimeEarliest());
	}

	/**
	 * @return nicely formatted version of startTime latest
	 */
	public String getStarttimeLatestFormatted() {
		return this.getDateTimeFormat().format(this.getStarttimeLatest());
	}

	/**
	 * TODO: move this to superclass
	 * 
	 * 
	 * Calculate the state of negotians for this drive. This is done by
	 * evaluating the matches
	 * 
	 * 
	 * @return calculated State, see above
	 * 
	 * 
	 */
	public RideNegotiationConstants getMatchingState() {

		MatchingStatistics stats = this.getMatchingStatistics();

		if (stats == null) {
			return RideNegotiationConstants.STATE_UNCLEAR;
		}

		if (stats.getNumberOfMatches() == 0) {
			return RideNegotiationConstants.STATE_NEW;
		}

		if (stats.getAcceptedBoth() > 0) {
			return RideNegotiationConstants.STATE_CONFIRMED_BOTH;
		}

		if (stats.getAcceptedDriver() > 0) {
			return RideNegotiationConstants.STATE_DRIVER_ACCEPTED;
		}

		if (stats.getAcceptedRider() > 0) {
			return RideNegotiationConstants.STATE_RIDER_REQUESTED;
		}

		return RideNegotiationConstants.STATE_UNCLEAR;
	}

	/**
	 * Determines wether a match must be countermanded before this ride can be
	 * invalidated
	 * 
	 * @return true, if the ride has to be countermanded before it can be
	 *         invalidated, else false
	 */
	public boolean isCountermandingRequired() {

		RideNegotiationConstants state = this.getMatchingState();
		if (state.equals(RideNegotiationConstants.STATE_CONFIRMED_BOTH)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines wether a match must be countermanded before this ride can be
	 * invalidated
	 * 
	 * @return false, if the ride has to be countermanded before it can be
	 *         invalidated, else true
	 */
	public boolean isCountermandingNotRequired() {

		return !this.isCountermandingRequired();
	}
	
	@Override
	
	/** return superclasses' Comment if != null,
	 *  or something like "-- --"
	 *  if there is no comment.
	 * 
	 */
	public String getComment(){
		
		String noCommentExists="-- --";
		
		String rideComment=super.getComment();
		
		if(rideComment==null || rideComment.trim().equals("")){
			return noCommentExists;
		}
		
		return rideComment;
	}
	
	
	
	/** Override superclass' trivial Setter to impose upper and lower Limits
	 *  on latest starttime.
	 * 
	 */	
	public void setStarttimeLatestOnCreation(Date arg){
		
		CustomerEntity cust=new JCustomerEntityService().getCustomerEntitySafely();
		
		long argtime=arg.getTime();	
		long upperHorizon=cust.getPlanningHorizonForOfferTS().getTime();
		long now=System.currentTimeMillis();
		
		
		long res1=Math.min(argtime, upperHorizon);
		long res2=Math.max(argtime, now);
		
		super.setStarttimeLatest(new Date(Math.max(res1, res2)));
	}
	
	public Date getStarttimeLatestOnCreation(){
		return this.getStarttimeLatest();
	}
	
	
	/** If on creation of a new Request some error was encountered, then 
	 *  then the ErrorCode should be set.
	 *  This is especially needed to inform the user wether or not
	 *  a request has been successfully created at the end to a workflow.
	 *   
	 */
	private String errorCode=null;
	
	public String getErrorCode(){
		return this.errorCode;
	}	

	public void setErrorCode(String arg){
		this.errorCode=arg;
	}
	
	/** 
	 * @return true, if this offer has an error code, else false
	 */
	public boolean getHasErrorCode(){	
		return this.getErrorCode()!=null;
	}
	
	/** 
	 * @return true, if this offer has no error code, else false
	 */
	public boolean getHasNoErrorCode(){	
		return this.getErrorCode()==null;
	}
	
	/** Retrieve a localized Version of the error code
	 */
	public String getLocalizedErrorCode(){
		
		if(errorCode==null){return null;}
		
		Locale locale=new HTTPUtil().detectBestLocale();
		Properties errorProperties=de.avci.openrideshare.utils.PropertiesLoader.getErrorMessageProperties(locale);		
		return errorProperties.getProperty(errorCode);
	}
	

	
	
	
	
	
	
	
	/** Govern wizard steps when creating the request.
	 * 
	 */
	public String handleFlow(FlowEvent evt){
		
		String currentStep = evt.getOldStep();
		String nextStep    = evt.getNewStep();
		
	
		//when going forward from location step,
		// check if locations have been initialized
		if( ("locationTab".equals(currentStep)) 
				&&("timesTab".equals(nextStep) || "miscTab".equals(nextStep))){
			
			if(!this.isInitializedPoint(this.getStartpt())){ return currentStep;}
			if(!this.isInitializedPoint(this.getEndpt()))  { return currentStep;}
		}	
		

		//when going forward from times step,
		// check if locations have been initialized
		if( ("timesTab".equals(currentStep)) &&("miscTab".equals(nextStep))){
			// uninitialized data
			if(this.getStarttimeEarliest()==null){ return currentStep; }
			if(this.getStarttimeLatest()==null)  { return currentStep; }
			//
			// align earliest and latest starttime
			//
			long now=new Date().getTime();
			//
			if(this.getStarttimeEarliest().getTime()< now) { 
				this.setStarttimeEarliest(new Date(now));
			}
			//
			if(this.getStarttimeLatest().getTime()< now) { 
				this.setStarttimeLatest(new Date(now));
			}	
		}
	
		// all tests passed, return next step
		return nextStep;
	}

	
	/** Check if point (startpoint / endpoint is initialized)
	 * 
	 * @param endpt
	 * @return
	 */
	private boolean isInitializedPoint(Point pt) {
		
		if(pt==null){return false;}
		if(pt.getX()==0 && pt.getX()==0){return false;}
	
		return true;
	}
	
	
	/** ordinary method to test wether given String is null or empty
	 * 
	 * @param arg   string to be tested
	 * @return true, if argument is null or consists of whitespaces only
	 */
	private boolean isNullOrEmpty(String arg){
		
		if(arg==null){return true;}	
		if("".equals(arg.trim())){ return true;}
		return false;
	}
	

} // class


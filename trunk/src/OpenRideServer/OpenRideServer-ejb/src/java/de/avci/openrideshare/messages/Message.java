package de.avci.openrideshare.messages;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.fhg.fokus.openride.customerprofile.CustomerEntity;

/**
 * Internal message for use in OpenRideshare. This is intended to be sent from
 * one user to another user, (or, less frequently from system to user)
 * 
 * 
 * @param args
 */

@Entity
@Table(name = "message")


@NamedQueries({
    @NamedQuery(name = "Message.findAll", query = "SELECT c FROM Message c"),
})
public class Message {

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "message_id")
	private Integer messageId;

	/**
	 * Delivery Type to signify no delivery. This is just here to ensure that
	 * messages can be savely initialized without beeing sent away at random.
	 * 
	 * Such messages simply stay in the users' queue
	 */
	private static final long DELIVERY_TYPE_NONE = 1;

	/**
	 * Delivery Type to signify that this message should be sent to customer via
	 * email
	 */
	private static final long DELIVERY_TYPE_EMAIL = 2;

	/**
	 * Delivery Type to signify that this message should be sent to customer via
	 * sms
	 */
	private static final long DELIVERY_TYPE_SMS = 4;

	/**
	 * Sender of this internal message. Sender may also be null, in that case,
	 * the message is sent to the system
	 */

	@JoinColumn(name = "sender_id", referencedColumnName = "cust_id", insertable = true, updatable = true)
	@ManyToOne(optional = false)
	private CustomerEntity sender;

	/**
	 * The recipient. This is always
	 * 
	 */
	@JoinColumn(name = "recipient_id", referencedColumnName = "cust_id", insertable = true, updatable = true)
	@ManyToOne(optional = false)
	private CustomerEntity recipient;

	/**
	 * When the message was created
	 */

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStampCreated = null;

	/**
	 * Timestamp containing the time when the receiver has seen the message.
	 * 
	 */

	@Column(name = "received")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStampReceived = null;

	/**
	 * Payload, message that is going to be sent
	 */

	@Column(name = "message")
	private String message;

	/**
	 * Delivery Type. If this message is delivered using more than one delivery
	 * type, deliver types are concatenated using bitwise or.
	 * 
	 */

	@Column(name = "deliverytype")
	private Long deliveryType = DELIVERY_TYPE_NONE;

	public CustomerEntity getSender() {
		return sender;
	}

	public void setSender(CustomerEntity sender) {
		this.sender = sender;
	}

	public CustomerEntity getRecipient() {
		return recipient;
	}

	public void setRecipient(CustomerEntity recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(Long deliveryType) {
		this.deliveryType = deliveryType;
	}

	public boolean isDeliveryEmail() {
		return (DELIVERY_TYPE_EMAIL & this.getDeliveryType()) > 0;
	}

	public boolean isDeliverySMS() {
		return (DELIVERY_TYPE_SMS & this.getDeliveryType()) > 0;
	}

	/**
	 * Ad delivery Type email, so this message will be delivered by email.
	 */
	public void addDeliveryTypeEmail() {
		this.setDeliveryType(deliveryType | DELIVERY_TYPE_EMAIL);
	}

	/**
	 * Ad delivery Type sms, so this message will be delivered by sms also.
	 */
	public void addDeliveryTypeSMS() {
		this.setDeliveryType(deliveryType | DELIVERY_TYPE_SMS);
	}

}

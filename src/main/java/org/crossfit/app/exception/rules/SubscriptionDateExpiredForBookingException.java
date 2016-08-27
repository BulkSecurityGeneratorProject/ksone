package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Subscription;

public class SubscriptionDateExpiredForBookingException extends
		SubscriptionException {

	private final Booking booking;
	
	public SubscriptionDateExpiredForBookingException(Subscription subscription, Booking booking) {
		super(subscription);
		this.booking = booking;
	}

}
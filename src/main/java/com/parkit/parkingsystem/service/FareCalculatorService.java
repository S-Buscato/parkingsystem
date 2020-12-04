package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		int inHour = ticket.getInTime().getHours();
		int outHour = ticket.getOutTime().getHours();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		int duration = outHour - inHour;
		double price = 0;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			price = duration * Fare.CAR_RATE_PER_HOUR;
			if (ticket.isRegularCustomer()) {
				price = price * Fare.DISCOUNT_FOR_REGULAR_CUSTOMER;
				System.out.println("DISCOUNT !!!!!!!!!!" + (1 - Fare.DISCOUNT_FOR_REGULAR_CUSTOMER)* 100 + "%");
			}
			ticket.setPrice(price);
			break;
		}
		case BIKE: {
			price = duration * Fare.BIKE_RATE_PER_HOUR;
			if (ticket.isRegularCustomer()) {
				price = price * Fare.DISCOUNT_FOR_REGULAR_CUSTOMER;
				System.out.println("DISCOUNT !!!!!!!!!!" + (1 - Fare.DISCOUNT_FOR_REGULAR_CUSTOMER)* 100 + "%");
			}
			ticket.setPrice(price);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}
package com.parkit.parkingsystem.service;

import java.sql.Date;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		Long inHour = ticket.getInTime().getTime();
		Long outHour = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		long duration = (outHour - inHour)/1000 / 60 / 60;
		System.out.println("duration : " +  duration);
		double price = 0;
		int free30Min = 30;
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			price = duration > free30Min ? duration * Fare.CAR_RATE_PER_HOUR / 60: 0;
			if (ticket.isRegularCustomer()) {
				price = price * Fare.DISCOUNT_FOR_REGULAR_CUSTOMER;
				System.out.println("DISCOUNT !!!!!!!!!!" + (1 - Fare.DISCOUNT_FOR_REGULAR_CUSTOMER)* 100 + "%");
			}
			ticket.setPrice(price);
			break;
		}
		case BIKE: {
			price = duration > free30Min ? duration * Fare.BIKE_RATE_PER_HOUR / 60 : 0;
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
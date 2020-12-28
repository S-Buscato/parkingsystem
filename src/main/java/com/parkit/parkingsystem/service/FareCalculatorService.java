package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	/**
	 * Return a number rounded to 2 digits after the decimal point
	 * 
	 * @param double price
	 */
	public double toRoundPrice(double price) {
		return Math.round(price * 100.0) / 100.0;
	}

	/**
	 * Return the price for a vehicle type
	 * 
	 * @param String vehicle, double duration, boolean isRegularCustomer
	 */
	public double calculForVehiculeType(String vehicleType, double duration, boolean isRegularCustomer) {
		double price = 0.00;
		double rate = 0.00;
		rate = vehicleType.equals("car") ? Fare.CAR_RATE_PER_HOUR : Fare.BIKE_RATE_PER_HOUR;
		price = duration > Fare.FREE_TIME_30MIN ? duration * rate / 60 : 0.00; 
		if (isRegularCustomer) {
			price = (price * Fare.DISCOUNT_FOR_REGULAR_CUSTOMER);
			System.out.println("DISCOUNT !!!!!!!!!!" + (1 - Fare.DISCOUNT_FOR_REGULAR_CUSTOMER) * 100 + "%");
		}
		return toRoundPrice(price);
	} 

	/**
	 * Calcul the fare of the parking and set the price in the ticket
	 * 
	 * @param Ticket ticket
	 */
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		Long inHour = ticket.getInTime().getTime();
		Long outHour = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = (outHour - inHour) / 1000 / 60; // millisecondes to minutes;
		
		System.out.println("duration : " + duration + " minutes" + " - soit en h : " + duration / 60);

		double price = 0.00;
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			price = calculForVehiculeType("car", duration, ticket.isRegularCustomer());
			ticket.setPrice(price);
			break;
		}
		case BIKE: {
			price = calculForVehiculeType("bike", duration, ticket.isRegularCustomer());
			ticket.setPrice(price);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}
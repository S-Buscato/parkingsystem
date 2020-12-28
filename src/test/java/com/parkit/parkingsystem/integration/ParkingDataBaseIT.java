package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
		assertEquals(1, ticketDAO.getTicket("ABCDEF").getParkingSpot().getId());
	}

	@Test
	public void testParkingLotExit() throws Exception {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
		Thread.sleep(1000);

		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		assertTrue(ticket.getOutTime() != null);
		assertEquals(0.0, ticket.getPrice());
	}

	@Test
	public void TestParkingABike() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		assertEquals(5, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
		assertEquals(4, ticketDAO.getTicket("ABCDEF").getParkingSpot().getId());
	}

	@Test
	public void TestParkingExitABike() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Thread.sleep(1000);// wait 1 sec before the exiting

		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		assertTrue(ticket.getOutTime() != null);
		assertEquals(0.0, ticket.getPrice());
	}

	@Test
	public void TestParkingInAndOut2TimesToHaveADiscount() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// first time
		parkingService.processIncomingVehicle();

		Thread.sleep(1000);// wait 1 sec before the exiting

		parkingService.processExitingVehicle();

		// second time
		Thread.sleep(1000);// wait 1 sec before the exiting
		parkingService.processIncomingVehicle();

		Ticket ticketIn = ticketDAO.getTicket("ABCDEF");

		assertTrue(ticketIn.isRegularCustomer());
	}

	@Test
	public void TestParkingIn2TimesAndExitAll() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		Thread.sleep(500);// wait 1 sec before the exiting

		parkingService.processIncomingVehicle();
		Thread.sleep(500);// wait 1 sec before the exiting

		parkingService.processExitingVehicle();
		Thread.sleep(500);// wait 1 sec before the exiting
		parkingService.processExitingVehicle();

		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		assertTrue(ticket.getOutTime() != null);
	}

	@Test
	public void TestParkingACarInAFullParking() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		parkingService.processIncomingVehicle();
		parkingService.processIncomingVehicle();

		assertEquals(0, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
	}

}

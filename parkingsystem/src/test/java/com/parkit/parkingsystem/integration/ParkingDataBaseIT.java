package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import junit.framework.Assert;
//import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
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
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){  // Integration Test # 1 present in the testParkingACar( ) method
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        // add code for Parking table is updated with availability
        Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Checking if the ticket ABCDEF enter correctly in the DB

        Assert.assertNotNull(ticket);
        }

    @Test
    public void testParkingLotExit(){  // Integration Test # 2 present in the testParkingLotExit( )  method
        //testParkingACar(); never add method test inside another method test
        // need to use a code to enter a vehicle below 
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        try {
           Thread.sleep(5000);
           } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
    
        Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Checking if the ticket ABCDEF out correctly in the DB with fare calculated

        Assert.assertNotNull(ticket.getPrice());
        Assert.assertNotNull(ticket.getOutTime());
    }
/* redone the test below change the name and go over all the test to change it
   // change the name to Integration Test #3 : testParkingLotExitRecurringUser( ) 
    //Integration Test # 3 present in the testParkingLotExitRecurringUser( ) method
    @Test
    public void testRecurentUser(){
        testParkingLotExit(); // never call test inside a test
        testParkingACar();

        Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Checking if the user is recurrent (=1) on the DB
        ticket.setRecurentUser(1); // getNBticket will say if recurent  because price modify of 5% discount

        Assert.assertEquals(ticket.isRecurentUser(),1);

      }
}
*/
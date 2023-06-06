package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;  // ADD THIS IMPORT  FOR CLASS class Fare  , for test integration #3 For session mentor tuesday 6 juin 2023

import com.parkit.parkingsystem.constants.ParkingType;
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


/*   (for next session mentor 6 june 2023)
Integration Test # 1 - test is ok
*/
    @Test // test is ok 
    public void testParkingACar(){  // Integration Test # 1 present in the testParkingACar( ) method
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int slotAvant = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); //to save the slot before
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        // add code for Parking table is updated with availability
        
        //for session mentor tuesday 3à may put code below  line 66 in comment
        //Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Checking if the ticket ABCDEF enter correctly in the DB

        int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slotAvant, slotApres);   //check that parking table is updated with availability
        assertNotNull( ticketDAO.getTicket("ABCDEF")); //check that a ticket is actualy saved in DB 

         //for session mentor tuesday 30 may put code below  line 73 in comment
        //Assert.assertNotNull(ticket);
    }


/*  (for next session mentor tuesday 6 june 2023)
Integration Test # 2 - modified - in terminal test is ok 
*/
    @Test
    public void testParkingLotExit(){  // Integration Test # 2 present in the testParkingLotExit( )  method
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();  //the vehicle is entering     +add this code line for session mentor tuesday 30 may  // need to use a code to enter a vehicle below 
        try {   ////pause added between incoming/exiting otherwise base returns ticket null sometimes
           Thread.sleep(5000);
           } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
    
        Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Checking if the ticket ABCDEF out correctly in the DB with fare calculated

        //Ticket ticket = ticketDAO.getTicketWithOutTime("ABCDEF");   Check if it is good code just add for session tuesday 30 may
        Assert.assertNotNull(ticket.getPrice());      // check that the generated price is entered in the database
        Assert.assertNotNull(ticket.getOutTime());  //check that the exit time is entered correctly in the database
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
/*  (for next session mentor tuesday 6 june 2023)
Integration Test # 3 - REWROTE code bellow but still NOT working
*/
   
   /*   PUT ALL IN COMMENT THE TEST 3 BC WAS NOT WORKING AND START FROM SCRATCH FOR SESSION TUESDAY 6 JUNE 
    @Test
    public void testParkingLotExitRecurringUser(){ //  remise de 5%  
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    int slotAvant = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);  //save the value of the next available slot
    parkingService.processIncomingVehicle();  //the vehicle is entering
    int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);  // save the value of the next available slot
    assertNotEquals(slotAvant, slotApres);  // we check that the 2 slot values ​​are different (so the vehicle has been parked)
    parkingService.processIncomingVehicle();  // we try again to make the vehicle enter
    int slotApres2 = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);  //save the value of the next available slot
    assertEquals(slotApres, slotApres2); //this time we make sure that the values ​​are the same (so that the vehicle could not be parked twice in a row without leaving)
   */

    // FOR SESSION MENTOR TUESDAY 6 JUNE TRY A NEW CODE BELOW
    // Integration Test # 3 - REWROTE code bellow for 2nd time is not working ([ERROR]   ParkingDataBaseIT.testParkingLotExitRecurringUser:147 expected: <2> but was: <3>)
   /* @Test
    public void testParkingLotExitRecurringUser() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int slotBefore = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        parkingService.processIncomingVehicle();
        int slotAfter = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slotBefore, slotAfter);
        parkingService.processIncomingVehicle();
        int slotAfter2 = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(slotAfter, slotAfter2);

        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Assuming "ABCDEF" is the registration number of the recurring user
        assertNotNull(ticket.getPrice()); // Check that the fare is populated correctly
        assertNotNull(ticket.getOutTime()); // Check that the exit time is populated correctly

    }
    */

    // FOR SESSION MENTOR TUESDAY 6 JUNE TRY A NEW CODE BELOW
    // Integration Test # 3 - REWROTE code again - in terminal test is ok 
   @Test
    public void testParkingLotExitRecurringUser() {
    // Create a recurring user by parking a car twice
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    Ticket ticketDayOne  = ticketDAO.getTicket("ABCDEF");
    ticketDayOne.setInTime(new Date(System.currentTimeMillis() - (3 * 60 * 60 * 1000)));
    ticketDAO.updateTicket(ticketDayOne);

    parkingService.processExitingVehicle();
    ticketDayOne = ticketDAO.getTicket("ABCDEF");

    parkingService.processIncomingVehicle();
    Ticket ticketDayTwo = ticketDAO.getTicket("ABCDEF");
    ticketDayTwo.setInTime(new Date(System.currentTimeMillis() - (3 * 60 * 60 * 1000)));
    ticketDAO.updateTicket(ticketDayTwo);

    parkingService.processExitingVehicle();
    ticketDayTwo = ticketDAO.getTicket("ABCDEF");

    assertEquals(ticketDayOne.getPrice()*0.95 , ticketDayTwo.getPrice());
    
    }



}

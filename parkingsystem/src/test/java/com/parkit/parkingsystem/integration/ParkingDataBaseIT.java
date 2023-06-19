package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import junit.framework.Assert;

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


/*   
Integration Test # 1 
*/
    @Test 
    public void testParkingACar(){  // Integration Test # 1 present in the testParkingACar( ) method
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int slotAvant = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); //to save the slot before
        parkingService.processIncomingVehicle();
     
        
        

        int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slotAvant, slotApres);   //check that parking table is updated with availability
        assertNotNull( ticketDAO.getTicket("ABCDEF")); //check that a ticket is actualy saved in DB 

        
    }


/*  
Integration Test # 2 
*/
    @Test
    public void testParkingLotExit(){  // Integration Test # 2 present in the testParkingLotExit( )  method
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();  //the vehicle is entering     
        try {   // pause added between incoming/exiting otherwise base returns ticket null sometimes
           Thread.sleep(5000);
           } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
        parkingService.processExitingVehicle();
      
            Ticket ticket = ticketDAO.getTicket("ABCDEF"); // Checking if the ticket ABCDEF out correctly in the DB with fare calculated

        Assert.assertNotNull(ticket.getPrice());      // check that the generated price is entered in the database
        Assert.assertNotNull(ticket.getOutTime());  //check that the exit time is entered correctly in the database
    }




    
    // Integration Test # 3
   @Test
    public void testParkingLotExitRecurringUser() {
    // Create a recurring user by parking a car twice
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    Ticket ticketDayOne  = ticketDAO.getTicket("ABCDEF");
    ticketDayOne.setInTime(new Date(System.currentTimeMillis() - (3 * 60 * 60 * 1000)));  // Set the inTime for the ticket for DayOne (the first day , vehicle  is coming)
    ticketDAO.updateTicket(ticketDayOne);

    parkingService.processExitingVehicle();
    ticketDayOne = ticketDAO.getTicket("ABCDEF");

    parkingService.processIncomingVehicle();
    Ticket ticketDayTwo = ticketDAO.getTicket("ABCDEF");
    ticketDayTwo.setInTime(new Date(System.currentTimeMillis() - (3 * 60 * 60 * 1000)));   // Set the inTime for the ticket for second day (the second day , vehicle  is coming)
    ticketDAO.updateTicket(ticketDayTwo);

    parkingService.processExitingVehicle();
    ticketDayTwo = ticketDAO.getTicket("ABCDEF");
 
    assertEquals(ticketDayOne.getPrice() * 0.95, ticketDayTwo.getPrice(), 0.5);
        /*
        ticketDayOne.getPrice() * 0.95 represents the expected value, which is the price of the first ticket multiplied by 0.95 (applying a 5% discount).
        ticketDayTwo.getPrice() represents the actual value, which is the price of the ticket for day two.
        0.5 is the delta value. It specifies the maximum allowed difference between the expected and actual values for the test to pass. In this case, the test will pass as long as the difference between the expected and actual values is within 0.5.
        */

    }



}

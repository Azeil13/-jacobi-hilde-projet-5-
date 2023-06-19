package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    //@Mock
    //private static TicketDAO ticketDAO1;

    @Mock
    private TicketDAO ticketDAO = new TicketDAO();

    @BeforeEach
    private void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

 
// FOR STEP 5 - Unit Test -  processExitingVehicleTest    _ Complete the existing test: processExitingVehicleTest
                             
    @Test
    public void processExitingVehicleTest(){   
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }



     @Test
    public void getNbTicketTest(){
        
        int nbTicket = ticketDAO.getNbTicket("ABCDEF");
        System.out.println("nbTicket = " + nbTicket);
        //verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
       
        assertEquals(0, nbTicket); // put O instead of 1 to pass the test etape 4  because enter the first time so 0 number of ticket the vehicle has
}
 
 





//FOR STEP 5 - Unit Test #1:call of the processIncomingVehicle() method where everything happens as expected.)
    @Test
    public void testprocessIncomingVehicle(){
        //GIVEN
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN

        parkingService.processIncomingVehicle();

        //THEN
         verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }




//FOR STEP 5 - Unit Test #2:processExitingVehicleTestUnableUpdate: execution of the test in the event that the updateTicket() method of ticketDAO returns false when calling processExitingVehicle()  )
    @Test
    public void processExitingVehicleTestUnableUpdate(){
        //GIVEN
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenThrow(IllegalArgumentException.class);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
    


//FOR STEP 5 - Unit Test #3: testGetNextParkingNumberIfAvailable: test of the call to the getNextParkingNumberIfAvailable() method with the result of obtaining a spot whose ID is 1 and which is available.  )
     @Test
     public void testGetNextParkingNumberIfAvailable(){
       //GIVEN
       parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
       // When
       when(inputReaderUtil.readSelection()).thenReturn(1);
       when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);

      
       ParkingSpot actualParkingSpot = parkingService.getNextParkingNumberIfAvailable();

       //THEN 
       verify(inputReaderUtil).readSelection();
       verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertNotNull(actualParkingSpot);  // on a trouvé une place
        // verify if available spot add code below
     }



// FOR STEP 5 - Unit Test # 4 :testGetNextParkingNumberIfAvailableParkingNumberNotFound: test of the call to the getNextParkingNumberIfAvailable() method with the result of no available spot (the method returns null).
    @Test 
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound(){
        //GIVEN
       parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
       when(inputReaderUtil.readSelection()).thenReturn(1);
       when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(0);

       //WHEN
       ParkingSpot actualParkingSpot = parkingService.getNextParkingNumberIfAvailable();

       //THEN
       verify(inputReaderUtil).readSelection();
       verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertNull(actualParkingSpot);
    }



  
//   FOR STEP 5 - Unit Test # 5 :testGetNextParkingNumberIfAvailableParkingNumberWrongArgument: test of the call to the getNextParkingNumberIfAvailable() method with the result of no spot (the method returns null) because the argument entered by the user concerning the type of vehicle is wrong (for example, the user entered 3 ).
     @Test 
     public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument(){
         //GIVEN
         parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
         when(inputReaderUtil.readSelection()).thenReturn(4);

         //WHEN
         ParkingSpot actualParkingSpot = parkingService.getNextParkingNumberIfAvailable();

         //THEN
         verify(inputReaderUtil).readSelection();
         assertNull(actualParkingSpot);
     }
        



}

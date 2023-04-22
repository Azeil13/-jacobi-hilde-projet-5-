package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
/**
* class ParkingService that manages parking services.
*
* @author Hilde Jacobi
*/

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

/**
* method processIncomingVehicle() that manages the arrival of a vehicle.
* When the user enters, the system asks for the type of vehicle (car or motorcycle) 
* and the license plate number, then lets the user enter if a space is available. 
* It also tells the user where to park.
* 
* @author Hilde Jacobi
*/
    public void processIncomingVehicle() {
        try{
            System.out.println("processIncomingVehicle");
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                String vehicleRegNumber = getVehichleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                
                //For ression mentor friday 31 march 2023 -using method getNbTicket Modify processIncomingVehicle method of the ParkingService class to display the welcome message.
                int nbTicket = ticketDAO.getNbTicket(vehicleRegNumber);
                       if(nbTicket>0) {
                               System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
                           }

                Ticket oldTicket = ticketDAO.getTicket(vehicleRegNumber);
                        if (oldTicket != null) {
                                ticket.setRecurentUser(1);
                                System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
                        } else {
                                ticket.setRecurentUser(0);
                        }
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime);
            }
        }catch(Exception e){
            logger.error("Unable to process incoming vehicle",e);
        }
    }

    private String getVehichleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        System.out.println("enter method");
        ParkingSpot parkingSpot = null;
        try{
            System.out.println("enter try");
            ParkingType parkingType = getVehichleType();
            System.out.println("parking type");
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            System.out.println("parking number");
            if(parkingNumber > 0){
                System.out.println("parking number 0");
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                System.out.println("else");
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        }catch(IllegalArgumentException ie){
            logger.error("Error parsing user input for type of vehicle", ie);
        }catch(Exception e){
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType(){
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }



/**
* method processExitingVehicle() that manages the exit of a vehicle.
* When the vehicle leaves the parking, the user again indicates his license plate number.
* The system then calculates and displays the price according 
* to the duration of parking lot and type of vehicle, then returns to the home menu.
* 
* @author Hilde Jacobi
*/
    public void processExitingVehicle() {
       System.out.println("begin processExitingVehicle method");
        //For ression mentor friday 31 march 2023 - using method getNbTicket Modify processExitingVehicle method of the ParkingService class 
        try{
            String vehicleRegNumber = getVehichleRegNumber();
            System.out.println("vehiculeRegNumber = " + vehicleRegNumber);
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            System.out.println("Ticket = In Time : " + ticket.getInTime());
            int nbTicket = ticketDAO.getNbTicket(vehicleRegNumber);
            System.out.println("nbTicket = " + nbTicket);
            
            // We check if the user is recurrent
            if (nbTicket > 1) { // user is recurrent
                ticket.setRecurentUser(1);
                System.out.println("is recurrent");
            } else  { // user is not recurrent
                ticket.setRecurentUser(0);
                System.out.println("is not recurrent");
            }
            
            Date outTime = new Date();
            ticket.setOutTime(outTime);
            System.out.println("Ticket = Out Time : " + ticket.getOutTime());
            fareCalculatorService.calculateFare(ticket);
            if(ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" +ticket.getVehicleRegNumber() + " is:" + outTime);
            }else{
                System.out.println("Unable to update ticket information. Error occurred");
            }
        }catch(Exception e){
            logger.error("Unable to process exiting vehicle",e);
        }


    }
}

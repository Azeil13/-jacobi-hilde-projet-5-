package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
* class FareCalculatorService is the calculation of the ticket price
*
* @author Hilde Jacobi
*/

public class FareCalculatorService {
    public void calculateFare(Ticket ticket, boolean discount){
        
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();  // Data type long + method getTime() is in Milliseconde [ms]              n Java, the statement long inHour = ticket.getInTime().getTime();  is used to assign the time in milliseconds of the "in time" of a ticket to a variable named inHour. 
        long outHour = ticket.getOutTime().getTime(); // Data type long + method getTime() is in Milliseconde [ms] 

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = (outHour - inHour)/(1000.0*60*60); // Converting the duration time in hour                      calculates the duration between two time points in hours and assigns it to the variable "duration."         (1000.0*60*60)" is a conversion factor that represents the number of milliseconds in an hour. 
        if (duration <= 0.5) {      // Free fare if user out before 30 min
            ticket.setPrice(0);     // In that case, the code sets the price of the ticket object to 0, indicating that the fare for the parking is free. The setPrice(0) method sets the fare value to 0.
            return;    // The return statement ensures that the method exits at this point, as there is no need to continue calculating the fare or performing any further operations.
        }


        switch (ticket.getParkingSpot().getParkingType()){
           
            case CAR: {                   
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);     // Normal fare
                break;
            }

            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);      // Normal fare
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }

        
        if (discount) {        // 5% discount if boolean discount True
            ticket.setPrice(ticket.getPrice()*0.95);
        }
    }

    public void calculateFare(Ticket ticket){ 
        this.calculateFare(ticket,false );  // boolean discount False
    }
}
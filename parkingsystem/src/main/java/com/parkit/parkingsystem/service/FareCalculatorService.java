package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
// TEST COMMIT 
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = (outHour - inHour)/ 3600000 ;
        // IN 1 hour = 3600 *1000 milisecond = 3 600 000 Milliseconde
        //1 Heure [h] = 3 600 000 Milliseconde [ms] 
        // Duration in hour = Duration in milisecond   divide by  3 600 000 Milliseconde [ms] 
        // DURATION inHOUR =  long inHour / 3 600 000
        // DURATION out HOUR =  long outHour / 3 600 000
        // => long duration IN HOUR = (outHour - inHour) / 3 600 000
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
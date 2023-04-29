package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;

    // ------------------------------------------------------------------------------------


    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.

        Reservation reservation = new Reservation();

        if(userRepository3.findById(userId) == null) {
            throw new Exception("Cannot make reservation");
        }
        User user = userRepository3.findById(userId).get();


        if(parkingLotRepository3.findById(parkingLotId) == null) {
            throw new Exception("Cannot make reservation");
        }
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        List<Spot> spotList = parkingLot.getSpotList();
        Spot currSpot = null;
        int minPrice = Integer.MAX_VALUE;

        for(Spot spot : spotList)
        {
            if(spot.getOccupied() == false)
            {
                if(spot.getSpotType().equals(SpotType.TWO_WHEELER))
                {
                    if(numberOfWheels <= 2)
                    {
                        if(minPrice > spot.getPricePerHour())
                        {
                            minPrice = spot.getPricePerHour();
                            currSpot = spot;
                        }
                    }
                }
                else if(spot.getSpotType().equals(SpotType.FOUR_WHEELER))
                {
                    if(numberOfWheels <= 4)
                    {
                        if(minPrice > spot.getPricePerHour())
                        {
                            minPrice = spot.getPricePerHour();
                            currSpot = spot;
                        }
                    }
                }
                else
                {
                    if(minPrice > spot.getPricePerHour())
                    {
                        minPrice = spot.getPricePerHour();
                        currSpot = spot;
                    }
                }
            }
        }

        if(currSpot == null)
            throw new Exception("Cannot make reservation");

        // Spot
        currSpot.setOccupied(true);
        List<Reservation> reservationList = currSpot.getReservationList();
        reservationList.add(reservation);
        currSpot.setReservationList(reservationList);

        spotRepository3.save(currSpot);

        // User
        List<Reservation> reservations = user.getReservationList();
        reservations.add(reservation);
        user.setReservationList(reservations);

        userRepository3.save(user);

        // reservation
        reservation.setUser(user);
        reservation.setSpot(currSpot);
        reservation.setNumberOfHours(timeInHours);


        return reservation;
    }
}

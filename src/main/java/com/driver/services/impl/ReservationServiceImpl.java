package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        ParkingLot parkingLot;
        try{
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        User user;
        try{
            user = userRepository3.findById(userId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        List<Spot> spotList = parkingLot.getSpotList();
//        Spot currSpot = null;
        List<Spot> spotListFollowCondition = new ArrayList<>();

        for(Spot st : spotList)
        {
            if(st.getOccupied() == false)
            {
                int capacity ;

                if(st.getSpotType().equals(SpotType.TWO_WHEELER))
                   capacity = 2;
                else if(st.getSpotType().equals(SpotType.FOUR_WHEELER))
                    capacity = 4;
                else
                    capacity = Integer.MAX_VALUE;

                if(capacity >= numberOfWheels)
                    spotListFollowCondition.add(st);
            }
        }

        if(spotListFollowCondition == null)
            throw new Exception("Cannot make reservation");

        Spot reverseSpot = null;
        int minimumPrice = Integer.MAX_VALUE;

        for(Spot st : spotListFollowCondition){
            int price = st.getPricePerHour() * timeInHours;

            if(price < minimumPrice) {
                minimumPrice = price;
                reverseSpot = st;
            }
        }

        reverseSpot.setOccupied(true);

        // Spot
//        currSpot.setOccupied(true);
//        List<Reservation> reservationList = currSpot.getReservationList();
//        reservationList.add(reservation);
//        currSpot.setReservationList(reservationList);
//
        spotRepository3.save(reverseSpot);

        // reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSpot(reverseSpot);
        reservation.setNumberOfHours(timeInHours);
        reservation.setPayment(null);

        // User
        user.getReservationList().add(reservation);
        userRepository3.save(user);

        return reservation;
    }
}

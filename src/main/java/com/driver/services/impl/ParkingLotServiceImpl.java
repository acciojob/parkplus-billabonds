package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    @Autowired
    ParkingLotRepository parkingLotRepository1;

    @Autowired
    SpotRepository spotRepository1;



    // -------------------------------------------------------------------------------


    @Override                                                                               // 1st API - done
    public ParkingLot addParkingLot(String name, String address) {

        //add a new parking lot to the database

        ParkingLot parkingLot = new ParkingLot();

        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLot.setSpotList(new ArrayList<>());

        return parkingLotRepository1.save(parkingLot);
    }

    @Override                                                                               // 2nd API - done
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {

        // create a new spot in the parkingLot with given id
        // the spot type should be the next biggest type in case the number of wheels are not 2 or 4, for 4+ wheels, it is others

        Spot spot = new Spot();

        // spotType
        if(numberOfWheels <= 2)
            spot.setSpotType(SpotType.TWO_WHEELER);
        else if(numberOfWheels <= 4)
            spot.setSpotType(SpotType.FOUR_WHEELER);
        else
            spot.setSpotType(SpotType.OTHERS);


        spot.setPricePerHour(pricePerHour);
        spot.setOccupied(false);

        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        spot.setParkingLot(parkingLot);

        List<Spot> spotList = parkingLot.getSpotList();
        spotList.add(spot);
        parkingLot.setSpotList(spotList);

        spot.setReservationList(new ArrayList<>());

        spotRepository1.save(spot);
        parkingLotRepository1.save(parkingLot);

        return spot;
    }

    @Override                                                                               // 3rd API - done
    public void deleteSpot(int spotId) {

        //delete a spot from given parking lot
        spotRepository1.deleteById(spotId);

    }

    @Override                                                                               // 4th API - working
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {

        // Update the details of a spot

        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        List<Spot> spotList = parkingLot.getSpotList();

        List<Spot> newSpotList = new ArrayList<>();
        Spot changeSpot = null;

        for(Spot spot: spotList)
        {
            if(spot.getId() == spotId) {
                spot.setPricePerHour(pricePerHour);
                spot = spotRepository1.save(spot);
            }
            newSpotList.add(spot);
        }

        changeSpot.setParkingLot(parkingLot);

        parkingLot.setSpotList(newSpotList);
        parkingLotRepository1.save(parkingLot);

        return changeSpot;
    }

    @Override                                                                               // 5th API - done
    public void deleteParkingLot(int parkingLotId) {

        //delete a parkingLot
        parkingLotRepository1.deleteById(parkingLotId);

    }
}

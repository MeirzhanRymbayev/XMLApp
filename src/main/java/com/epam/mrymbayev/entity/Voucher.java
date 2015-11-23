package com.epam.mrymbayev.entity;

import com.epam.mrymbayev.annotation.IsSimleType;

public class Voucher {
    @IsSimleType(value = true)
    private String id;
    @IsSimleType(value = true)
    private String typeOfTour;
    @IsSimleType(value = true)
    private String country;
    @IsSimleType(value = true)
    private String dayAndNightQuantity;
    @IsSimleType(value = true)
    private String transport;
    private Hotel hotel;
    private Cost cost;

    public Voucher() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTypeOfTour(String typeOfTour) {
        this.typeOfTour = typeOfTour;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDayAndNightQuantity(String dayAndNightQuantity) {
        this.dayAndNightQuantity = dayAndNightQuantity;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}

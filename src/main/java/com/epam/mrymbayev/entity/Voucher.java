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
    private String daysAndNightQuantity;
    @IsSimleType(value = true)
    private String transport;
    private Hotel hotelCharacteristics;
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

    public void setDaysAndNightQuantity(String daysAndNightQuantity) {
        this.daysAndNightQuantity = daysAndNightQuantity;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}

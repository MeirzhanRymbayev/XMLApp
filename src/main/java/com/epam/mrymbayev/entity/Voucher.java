package com.epam.mrymbayev.entity;

import com.epam.mrymbayev.annotation.IsSimleType;

public class Voucher {
    @IsSimleType(value = true)
    private String id;
    @IsSimleType(value = true)
    private String typeTour;
    @IsSimleType(value = true)
    private String country;
    @IsSimleType(value = true)
    private String daysAndNights;
    @IsSimleType(value = true)
    private String transport;
    private Hotel hotelCharacteristics;
    private Cost cost;

    public Voucher() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTypeTour(String typeTour) {
        this.typeTour = typeTour;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDaysAndNights(String daysAndNights) {
        this.daysAndNights = daysAndNights;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}

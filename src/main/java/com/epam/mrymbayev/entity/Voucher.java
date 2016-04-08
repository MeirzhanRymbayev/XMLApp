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

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id='" + id + '\'' +
                ", typeOfTour='" + typeOfTour + '\'' +
                ", country='" + country + '\'' +
                ", dayAndNightQuantity='" + dayAndNightQuantity + '\'' +
                ", transport='" + transport + '\'' +
                ", hotel=" + hotel +
                ", cost=" + cost +
                '}';
    }
}

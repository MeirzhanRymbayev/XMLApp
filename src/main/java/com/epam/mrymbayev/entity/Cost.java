package com.epam.mrymbayev.entity;

import com.epam.mrymbayev.annotation.IsSimleType;

public class Cost {
    @IsSimleType(value = true)
    private int price;
    private String currency;

    public void setPrice(int cost) {
        this.price = cost;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Cost{" +
                "price=" + price +
                ", currency=" + currency +
                '}';
    }
}




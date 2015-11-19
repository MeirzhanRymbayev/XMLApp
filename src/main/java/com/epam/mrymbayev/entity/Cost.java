package com.epam.mrymbayev.entity;

import com.epam.mrymbayev.annotation.IsSimleType;

public class Cost {
    @IsSimleType(value = true)
    private int cost;
    private Currency currency;

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

enum Currency {
    USD, EUR, KZT
}


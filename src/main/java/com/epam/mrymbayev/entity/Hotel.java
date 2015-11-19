package com.epam.mrymbayev.entity;

import com.epam.mrymbayev.annotation.IsSimleType;

public class Hotel {
    @IsSimleType(value = true)
    private int stars;
    @IsSimleType(value = true)
    private String food;
    @IsSimleType(value = true)
    private int room;
    @IsSimleType(value = true)
    private boolean tv;
    @IsSimleType(value = true)
    private boolean airConditioning;

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public void setTv(boolean tv) {
        this.tv = tv;
    }

    public void setAirConditioning(boolean airConditioning) {
        this.airConditioning = airConditioning;
    }
}

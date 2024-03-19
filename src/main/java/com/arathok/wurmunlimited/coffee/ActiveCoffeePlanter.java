package com.arathok.wurmunlimited.coffee;

public class ActiveCoffeePlanter {
    public  long itemId;
    public  long nextTendAt;
    public  int numberTended;
    public  boolean hasBean;

    public ActiveCoffeePlanter(long itemId,long nextTendAt, int numberTended, boolean hasBean)
    {
        this.itemId = itemId;
        this.nextTendAt = nextTendAt;
        this.numberTended = numberTended;
        this.hasBean = hasBean;
    }
}

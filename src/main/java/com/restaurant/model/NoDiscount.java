package com.restaurant.model;
import java.util.List;
public class NoDiscount implements DiscountStrategy{
    @Override
    public String getName(){
        return "No discount";
    }
    @Override
    public double calculateDiscount(List<Meal>meals){
        return 0;
    }
}

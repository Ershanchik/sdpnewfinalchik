package com.restaurant.discount;
import com.restaurant.model.DiscountStrategy;
import com.restaurant.model.Meal;
import java.util.List;
public class StudentDiscount implements DiscountStrategy{
    private static final double RATE=0.10;
    @Override
    public String getName(){
        return String.format("Student discount %.0f%%",RATE*100);
    }
    @Override
    public double calculateDiscount(List<Meal>meals){
        double subtotal=0;
        for(Meal meal : meals){
            subtotal+=meal.getPrice();
        }
        return subtotal*RATE;
    }
}

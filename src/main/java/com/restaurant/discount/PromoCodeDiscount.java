package com.restaurant.discount;
import com.restaurant.model.DiscountStrategy;
import com.restaurant.model.Meal;
import java.util.List;
public class PromoCodeDiscount implements DiscountStrategy{
    private static final String CODE="WELCOME";
    private static final double MIN_SUBTOTAL=3000;
    private static final double AMOUNT=500;
    @Override
    public String getName(){
        return String.format("Promo code %s: %.0f off orders from %.0f",CODE,AMOUNT,MIN_SUBTOTAL);
    }
    @Override
    public double calculateDiscount(List<Meal>meals){
        double subtotal=0;
        for(Meal meal : meals){
            subtotal+=meal.getPrice();
        }
        return subtotal>=MIN_SUBTOTAL ? AMOUNT : 0;
    }
}

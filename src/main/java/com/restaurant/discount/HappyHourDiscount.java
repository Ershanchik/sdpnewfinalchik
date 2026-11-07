package com.restaurant.discount;
import com.restaurant.model.DiscountStrategy;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import java.util.List;
public class HappyHourDiscount implements DiscountStrategy{
    @Override
    public String getName(){
        return "Happy hour: drinks half price";
    }
    @Override
    public double calculateDiscount(List<Meal>meals){
        double discount=0;
        for(Meal meal : meals){
            if(meal.getName().equals(MealType.DRINK.getTitle())){
                discount+=meal.getPrice() / 2;
            }
        }
        return discount;
    }
}

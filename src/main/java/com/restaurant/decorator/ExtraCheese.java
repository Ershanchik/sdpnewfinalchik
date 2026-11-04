package com.restaurant.decorator;
import com.restaurant.model.Meal;
public class ExtraCheese extends MealDecorator {
    public static final double PRICE=400;
    public ExtraCheese(Meal meal){
        super(meal);
    }
    @Override
    public String getDescription(){
        return meal.getDescription()+" + extra cheese";
    }
    @Override
    public double getPrice(){
        return meal.getPrice()+PRICE;
    }
}

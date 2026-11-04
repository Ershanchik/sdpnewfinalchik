package com.restaurant.decorator;
import com.restaurant.model.Meal;
public class Bacon extends MealDecorator {
    public static final double PRICE=600;
    public Bacon(Meal meal){
        super(meal);
    }
    @Override
    public String getDescription(){
        return meal.getDescription()+" + bacon";
    }
    @Override
    public double getPrice(){
        return meal.getPrice()+PRICE;
    }
}

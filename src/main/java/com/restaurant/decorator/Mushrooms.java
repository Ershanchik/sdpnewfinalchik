package com.restaurant.decorator;
import com.restaurant.model.Meal;
public class Mushrooms extends MealDecorator {
    public static final double PRICE=300;
    public Mushrooms(Meal meal){
        super(meal);
    }
    @Override
    public String getDescription(){
        return meal.getDescription()+" + mushrooms";
    }
    @Override
    public double getPrice(){
        return meal.getPrice()+PRICE;
    }
}

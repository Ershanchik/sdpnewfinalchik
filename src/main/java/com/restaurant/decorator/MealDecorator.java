package com.restaurant.decorator;
import com.restaurant.model.Meal;
import java.util.Objects;
public abstract class MealDecorator implements Meal {
    protected final Meal meal;
    protected MealDecorator(Meal meal){
        this.meal=Objects.requireNonNull(meal,"meal must not be null");
    }
    @Override
    public String getName(){
        return meal.getName();
    }
}

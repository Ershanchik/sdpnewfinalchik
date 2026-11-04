package com.restaurant.model;
public enum MealType {
    PIZZA("Pizza",true),
    BURGER("Burger",true),
    PASTA("Pasta",true),
    DRINK("Drink",false);
    private final String title;
    private final boolean allowsToppings;
    MealType(String title,boolean allowsToppings){
        this.title=title;
        this.allowsToppings=allowsToppings;
    }
    public String getTitle(){
        return title;
    }
    public boolean allowsToppings(){
        return allowsToppings;
    }
}

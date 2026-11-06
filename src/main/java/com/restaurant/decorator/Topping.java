
package com.restaurant.decorator;
import com.restaurant.model.Meal;
import java.util.function.UnaryOperator;
public enum Topping{
    EXTRA_CHEESE("Extra cheese",ExtraCheese.PRICE,ExtraCheese::new),
    BACON("Bacon",Bacon.PRICE,Bacon::new),
    MUSHROOMS("Mushrooms",Mushrooms.PRICE,Mushrooms::new);
    private final String title;
    private final double price;
    private final UnaryOperator<Meal> wrapper;
    Topping(String title,double price,UnaryOperator<Meal> wrapper){
        this.title=title;
        this.price=price;
        this.wrapper=wrapper;
    }
    public String getTitle(){
        return title;
    }
    public double getPrice(){
        return price;
    }
    public Meal applyTo(Meal meal){
        return wrapper.apply(meal);
    }
}
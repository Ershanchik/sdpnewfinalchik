package com.restaurant.builder;
import com.restaurant.model.DiscountStrategy;
import com.restaurant.model.Meal;
import com.restaurant.model.NoDiscount;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class OrderBuilder {
    private final int id;
    private final List<Meal> meals=new ArrayList<>();
    private OrderType type=OrderType.DINE_IN;
    private int tableNumber;
    private String comment="";
    private DiscountStrategy discount=new NoDiscount();
    public OrderBuilder(int id){
        this.id=id;
    }
    public OrderBuilder type(OrderType type){
        this.type=Objects.requireNonNull(type,"type must not be null");
        return this;
    }
    public OrderBuilder table(int tableNumber){
        this.tableNumber=tableNumber;
        return this;
    }
    public OrderBuilder comment(String comment){
        this.comment=comment==null?"":comment.trim();
        return this;
    }
    public OrderBuilder discount(DiscountStrategy discount){
        this.discount=Objects.requireNonNull(discount,"discount must not be null");
        return this;
    }
    public OrderBuilder addMeal(Meal meal){
        meals.add(Objects.requireNonNull(meal,"meal must not be null"));
        return this;
    }
    public Order build(){
        if(meals.isEmpty()){
            throw new IllegalStateException("Order must contain at least one meal");
        }
        if(type==OrderType.DINE_IN&&tableNumber<=0){
            throw new IllegalStateException("Dine-in order needs a table number");
        }
        int table=type==OrderType.DINE_IN?tableNumber:0;
        Order order=new Order(id,type,table,comment);
        order.setDiscount(discount);
        for(Meal meal:meals){
            order.addMeal(meal);
        }
        return order;
    }
}

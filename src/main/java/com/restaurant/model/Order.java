
package com.restaurant.model;
import java.util.ArrayList;
import java.util.List;
public class Order {
    private final int id;
    private final OrderType type;
    private final int tableNumber;
    private final String comment;
    private final List<Meal> meals=new ArrayList<>();
    private OrderStatus status=OrderStatus.NEW;
    public Order(int id){
        this(id,OrderType.TAKEAWAY,0,"");
    }
    public Order(int id,OrderType type,int tableNumber,String comment){
        this.id=id;
        this.type=type;
        this.tableNumber=tableNumber;
        this.comment=comment;
    }
    public int getId(){
        return id;
    }
    public OrderType getType(){
        return type;
    }
    public int getTableNumber(){
        return tableNumber;
    }
    public String getComment(){
        return comment;
    }
    public void addMeal(Meal meal){
        meals.add(meal);
    }
    public List<Meal> getMeals(){
        return List.copyOf(meals);
    }
    public double getTotal(){
        double total=0;
        for(Meal meal:meals){
            total+=meal.getPrice();
        }
        return total;
    }
    public OrderStatus getStatus(){
        return status;
    }
    public void setStatus(OrderStatus status){
        this.status=status;
    }
}

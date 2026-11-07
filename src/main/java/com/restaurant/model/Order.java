package com.restaurant.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class Order {
    private final int id;
    private final OrderType type;
    private final int tableNumber;
    private final String comment;
    private final List<Meal> meals=new ArrayList<>();
    private OrderStatus status=OrderStatus.NEW;
    private final List<OrderObserver> observers=new ArrayList<>();
    private DiscountStrategy discount=new NoDiscount();
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
    public double getSubtotal(){
        double subtotal=0;
        for(Meal meal : meals){
            subtotal+=meal.getPrice();
        }
        return subtotal;
    }
    public double getDiscountAmount(){
        // a discount can never make the order cost less than zero
        return Math.min(discount.calculateDiscount(getMeals()),getSubtotal());
    }
    public double getTotal(){
        return getSubtotal() - getDiscountAmount();
    }
    public DiscountStrategy getDiscount(){
        return discount;
    }
    public void setDiscount(DiscountStrategy discount){
        this.discount=Objects.requireNonNull(discount,"discount must not be null");
    }
    public OrderStatus getStatus(){
        return status;
    }
    public void setStatus(OrderStatus status){
        if(status==this.status){
            return;
        }
        OrderStatus oldStatus=this.status;
        this.status=status;
        // iterate over a copy so an observer may unsubscribe while being notified
        for(OrderObserver observer : List.copyOf(observers)){
            observer.onStatusChanged(this,oldStatus);
        }
    }
    public void advanceStatus(){
        setStatus(status.next());
    }
    public void addObserver(OrderObserver observer){
        observers.add(Objects.requireNonNull(observer,"observer must not be null"));
    }
    public void removeObserver(OrderObserver observer){
        observers.remove(observer);
    }
}

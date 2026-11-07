package com.restaurant.facade;
import com.restaurant.builder.OrderBuilder;
import com.restaurant.decorator.Topping;
import com.restaurant.discount.DiscountOption;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import com.restaurant.observer.CustomerNotifier;
import com.restaurant.observer.KitchenDisplay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
public class RestaurantFacade {
    private final MealFactory factory=new MealFactory();
    private final Map<Integer,TrackedOrder> orders=new HashMap<>();
    private final Consumer<String> liveOutput;
    private int nextOrderId=1;
    private record TrackedOrder(Order order,List<String> notifications){}
    public RestaurantFacade(){
        this(message->{});
    }
    public RestaurantFacade(Consumer<String> liveOutput){
        this.liveOutput=liveOutput;
    }
    public Meal createMeal(MealType type){
        return createMeal(type,List.of());
    }
    public Meal createMeal(MealType type,List<Topping> toppings){
        if(!toppings.isEmpty()&&!type.allowsToppings()){
            throw new IllegalArgumentException(type.getTitle()+" cannot have toppings");
        }
        Meal meal=factory.createMeal(type);
        for(Topping topping:toppings){
            meal=topping.applyTo(meal);
        }
        return meal;
    }
    public synchronized Order placeOrder(OrderType type,int tableNumber,String comment,DiscountOption discount,List<Meal> meals){
        OrderBuilder builder=new OrderBuilder(nextOrderId)
                .type(type)
                .table(tableNumber)
                .comment(comment)
                .discount(discount.createStrategy());
        for(Meal meal:meals){
            builder.addMeal(meal);
        }
        Order order=builder.build();
        nextOrderId++;
        List<String> notifications=new ArrayList<>();
        Consumer<String> output=message->{
            notifications.add(message);
            liveOutput.accept(message);
        };
        order.addObserver(new KitchenDisplay(output));
        order.addObserver(new CustomerNotifier(output));
        orders.put(order.getId(),new TrackedOrder(order,notifications));
        return order;
    }
    public synchronized Optional<Order> findOrder(int id){
        TrackedOrder tracked=orders.get(id);
        return tracked==null?Optional.empty():Optional.of(tracked.order());
    }
    public synchronized List<String> getNotifications(int id){
        TrackedOrder tracked=orders.get(id);
        return tracked==null?List.of():List.copyOf(tracked.notifications());
    }

    public synchronized Order advanceOrder(int id){
        TrackedOrder tracked=orders.get(id);
        if(tracked==null){
            throw new IllegalArgumentException("Order not found: "+id);
        }
        tracked.order().advanceStatus();
        return tracked.order();
    }
}

package com.restaurant.observer;
import com.restaurant.model.Order;
import com.restaurant.model.OrderObserver;
import com.restaurant.model.OrderStatus;
import java.util.function.Consumer;
public class CustomerNotifier implements OrderObserver{
    private final Consumer<String> output;
    public CustomerNotifier(Consumer<String> output){
        this.output=output;
    }
    @Override
    public void onStatusChanged(Order order,OrderStatus oldStatus){
        String message=switch(order.getStatus()){
            case COOKING -> String.format("Customer: your order #%d is being prepared",order.getId());
            case READY -> String.format("Customer: your order #%d is ready",order.getId());
            case DELIVERED -> String.format("Customer: order #%d delivered, enjoy your meal",order.getId());
            default -> String.format("Customer: order #%d is now %s",order.getId(),order.getStatus());
        };
        output.accept(message);
    }
}

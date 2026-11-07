package com.restaurant.observer;
import com.restaurant.model.Order;
import com.restaurant.model.OrderObserver;
import com.restaurant.model.OrderStatus;
import java.util.function.Consumer;
public class KitchenDisplay implements OrderObserver{
    private final Consumer<String> output;
    public KitchenDisplay(Consumer<String> output){
        this.output=output;
    }
    @Override
    public void onStatusChanged(Order order,OrderStatus oldStatus){
        String message=String.format("Kitchen: order #%d %s -> %s",order.getId(),oldStatus,order.getStatus());
        // the cook needs the customer's note exactly when the cooking starts
        if(order.getStatus()==OrderStatus.COOKING&&!order.getComment().isEmpty()){
            message += " (note: "+order.getComment()+")";
        }
        output.accept(message);
    }
}

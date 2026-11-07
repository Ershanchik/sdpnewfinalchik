package com.restaurant.model;
public interface OrderObserver{
    void onStatusChanged(Order order,OrderStatus oldStatus);
}

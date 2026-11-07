package com.restaurant.model;
import java.util.List;
public interface DiscountStrategy{
    String getName();
    double calculateDiscount(List<Meal>meals);
}

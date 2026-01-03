package com.stockmarket.logic;

import com.stockmarket.domain.Order;

import java.util.Comparator;

public class SellOrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
        if(o1.getPrice() < o2.getPrice())
            return -1;
        if(o1.getPrice() > o2.getPrice())
            return 1;
        return 0;
    }
}

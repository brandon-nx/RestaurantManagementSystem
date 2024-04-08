package com.restaurantmanagementsystem.pos.extra;

public interface IItemOperations {
    void addItem(Object item);
    void removeItem(Object item);
    void updateItem(Object oldItem, Object newItem);
}

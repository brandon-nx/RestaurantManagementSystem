package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerControllerTest {

    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        customerController = new CustomerController();

        OrderItem item1 = new OrderItem("1", "Item1", 2, 5.0);
        OrderItem item2 = new OrderItem("2", "Item2", 1, 10.0);
        customerController.orderItems.add(item1);
        customerController.orderItems.add(item2);
    }

    @Test
    void calculateSubtotalTest() {
        double expected = 20.0;
        double actual = customerController.calculateSubtotal();
        assertEquals(expected, actual, "Subtotal should be correctly calculated.");
    }

    @Test
    void calculateServiceChargeTest() {
        double subtotal = customerController.calculateSubtotal();
        double expectedServiceCharge = subtotal * 0.10;
        double actualServiceCharge = customerController.calculateServiceCharge(subtotal);
        assertEquals(expectedServiceCharge, actualServiceCharge, "Service charge should be correctly calculated.");
    }

    @Test
    void calculateTaxTest() {
        double subtotal = customerController.calculateSubtotal();
        double expectedTax = subtotal * 0.06;
        double actualTax = customerController.calculateTax(subtotal);
        assertEquals(expectedTax, actualTax, "Tax should be correctly calculated.");
    }

    @Test
    void calculateTotalTest() {
        double subtotal = customerController.calculateSubtotal();
        double serviceCharge = customerController.calculateServiceCharge(subtotal);
        double tax = customerController.calculateTax(subtotal);
        double expectedTotal = subtotal + serviceCharge + tax;
        double actualTotal = customerController.calculateTotal(subtotal, serviceCharge, tax);
        assertEquals(expectedTotal, actualTotal, "Total should be correctly calculated.");
    }
}
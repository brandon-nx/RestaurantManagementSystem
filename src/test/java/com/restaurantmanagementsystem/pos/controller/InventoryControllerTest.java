package com.restaurantmanagementsystem.pos.controller;

import org.junit.Assert;
import org.junit.Test;

public class InventoryControllerTest {

    @Test
    public void testIsValidNumberWithValidNumber() {
        InventoryController controller = new InventoryController();
        boolean result = controller.isValidNumber("123", "stock");
        Assert.assertTrue("Expected to be a valid number", result);
    }
}

package com.techelevator.view;

import org.junit.Assert;
import org.junit.Test;

public class ItemTest {
    @Test
    public void create_and_display_new_item() {
        Item item = new Item("D5|Snickers|2.05|Candy");
        item.separateText();

        String name = item.getName();
        Assert.assertEquals("Snickers", name);
    }

    @Test
    public void create_and_display_null_item() {
        Item item = new Item("");
        item.separateText();

        String name = item.getName();
        Assert.assertEquals("null", name);
    }

    @Test
    public void create_and_display_invalid_price() {
        Item item = new Item("E7|Jolly Rancher|tree-fiddy|Gum");
        item.separateText();

        double price = item.getPrice();
        Assert.assertEquals(0.0, price, 0.01);
    }
}

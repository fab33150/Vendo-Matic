package com.techelevator.view;

import java.util.regex.Pattern;

//The item object class holds the code, name, price, type, and how many of each item are remaining within the machine
public class Item {
    private String code;
    private String name;
    private double price;
    private String type;
    private String fullText;
    private int remaining;

    public Item(String fullText) {
        this.fullText = fullText;
    }

    public String getFullText(){
        return fullText;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    //The separate text method takes each string from the vending machine.csv, and parses the data
    public void separateText(){

        //run a for loop to check to see if the text is legitimate
        int count = 0;
        for (int i = 0; i < fullText.length(); i++)
            //count the number of pipes in the string
            if (fullText.charAt(i) == '|')
                count++;

            //if the text is legit
        if(count==3) {
            //The string is split by the "|"
            String[] itemArray = fullText.split(Pattern.quote("|"));

            //the code is the first part,
            code = itemArray[0];
            //then the name,
            name = itemArray[1];
            //then the price,
            String priceString = itemArray[2];
            try{
                price = Double.parseDouble(priceString);
            }catch(NumberFormatException e){
                price = 0;
            }
            //and lastly the type of item
            type = itemArray[3];
        }
        //if the number of pipes do not equal 3
        else {
            //then display that the text is invalid
            code = "null";
            name = "null";
            price = 0;
            type = "null";
        }
    }
}

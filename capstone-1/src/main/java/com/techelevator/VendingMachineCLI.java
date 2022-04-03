package com.techelevator;

import com.techelevator.view.Item;
import com.techelevator.view.Menu;

import java.io.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/*
The Vending Machine CLI program will ask the user to select from a number of options, accept money, display items,
complete transactions and dispense change. It will also keep track of the number of items within the machine and log
every transaction into a text file.
*/
public class VendingMachineCLI {

	//The Main Menu options are instantiated and then put into an array.
	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT, MAIN_MENU_OPTION_SALES_REPORT };

	//The Purchase Menu options are instantiated and then put into an array.
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] PURCHASE_MENU_OPTIONS = {PURCHASE_MENU_OPTION_FEED_MONEY,PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION };

	//The balance will keep track of how much money the user has put into the machine.
	private double balance = 0;

	//The menu list will keep track of all the items in the vending machine, along with their names, prices,
	//item code, type of snack and how many are remaining inside the machine.
	private List<Item> itemList = new ArrayList<>();

	//DateFormat will take the current time and date and display it into a readable format for the user.
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

	//The path names for the sales report and transaction log are instantiated here.
	private String logPathName = "C:\\Users\\mecha\\Desktop\\meritamerica\\Repos\\capstone-1\\src\\main\\java\\com\\techelevator\\log.txt";
	private String salePathName = "C:\\Users\\mecha\\Desktop\\meritamerica\\Repos\\capstone-1\\src\\main\\java\\com\\techelevator\\salesreport.txt";
	private String tempPathName = "C:\\Users\\mecha\\Desktop\\meritamerica\\Repos\\capstone-1\\src\\main\\java\\com\\techelevator\\temp.txt";

	//Menus and Items are declared here.
	private Menu menu;
	private Item selectedItem;

	//The numberformat will display the balance variable as a dollar and cents format.
	private NumberFormat formatter = NumberFormat.getCurrencyInstance();

	//This constructor will take the menu as a parameter so that this program can access its methods.
	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	//The run method will go through all the menu options in the main menu and route the user to the correct methods.
	public void run() {
		while (true) {
			//Display the choices
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			//if user selects dispay items
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				//run a for loop through all the items in the item list
				for(Item item : itemList){
					//format and display according to code, name, price, and how many remaining
					System.out.println(item.getCode() + " : " + item.getName() + " " +
							formatter.format(item.getPrice()) + ", " + item.getRemaining() + " remaining");
					//if the item is sold out, display it in here
					if(item.getRemaining() == 0)
						System.out.println("^ SOLD OUT!");
				}
				//if user selects purchase,
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				//then go to purchase method
				purchase();
			}
			//if user decides to exit
			else if(choice.equals(MAIN_MENU_OPTION_EXIT)){
				//thank user and exit
				System.out.println("Thank you for using the Vendo-Matic 800");
				System.exit(800);
			}
			//if user selects secret menu option
			else if(choice.equals(MAIN_MENU_OPTION_SALES_REPORT)){
				//go to the sales report method
				salesReport();
				//and exit
				System.out.println("Thank you for using the Vendo-Matic 800");
				System.out.println("(Sales Report has been appended)");
				System.exit(801);
			}
		}
	}

	//The stockitems method is called at the beginning of the program. It runs through the list of items in the
	//vendingmachine.csv, puts 5 of each one into the machine.
	public void stockItems() {
		//open the csv here
		String path = "vendingmachine.csv";
		File file = new File(path);

		try (Scanner input = new Scanner(file)) {
			//runs through each string
			while(input.hasNext()) {
				//instantiates a new item for every string
				Item item = new Item(input.nextLine());
				//run through the separate text method to split and parse the data
				item.separateText();
				//put five of each item into the machine
				item.setRemaining(5);
				//and add the item into the item list
				itemList.add(item);
			}
		}
		catch (FileNotFoundException e){
			System.out.println("File not found: " + file.getAbsolutePath());
		}
	}

	//The purchase method simply displays the purchase menu to the user
	public void purchase() {
		while(true) {
			String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);

			//if Feed Money is chosen,
			if(choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
				//then run the feedmoney()
				feedMoney();
			}
			//if select product is chosen,
			else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
				//run selectProduct()
				selectProduct();
			}
			//if finish transaction is chosen,
			else if(choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)){
				//run finishTransaction()
				finishTransaction();
			}
			//Finally, the menu will display the current balance within the machine
			System.out.println("\nCurrent Money Provided: " + formatter.format(balance));
		}

	}

	//The FeedMoney() allows the user to add money into the machine, increasing its balance to be available for
	//transaction
	public void feedMoney() {
		//The user will be prompted to insert a valid dollar amount.
		System.out.print("Please enter valid dollar amount: (1, 2, 5, 10, 20) or enter 0 to exit: ");
		Scanner input = new Scanner(System.in);
		String amount = input.nextLine();
		//open the log file to log this transaction
		try (PrintWriter dataOutput = new PrintWriter(new FileOutputStream(logPathName, true))) {
			//the money variable is the amount that the user entered
			int money = Integer.valueOf(amount);
			//The user may enter as many dollar amounts as they would like until they enter the amount 0
			while (money != 0) {
				//as long as the amount is a valid dollar amount,
				if (money == 1 || money == 2 || money == 5 || money == 10 || money == 20) {
					//add the money to the balance
					balance += money;
					//display that the balance has been added
					System.out.println(money + " added to balance.");
					System.out.println("Current balance: " + formatter.format(balance));
					//and then log this transaction to the log file
					dataOutput.println(">" + dateFormat.format(new Date()) + " FEED MONEY " + formatter.format(money) + " " + formatter.format(balance));
					//if the money is not a valid dollar amount
				} else {
					//the user will be prompted to re enter
					System.out.print("Please enter valid dollar amount: (1, 2, 5, 10, 20) or enter 0 to exit: ");
				}
				amount = input.nextLine();
				money = Integer.valueOf(amount);
			}
			//this will catch in case the amount entered is not an Integer
		} catch (NumberFormatException e) {
			System.out.println("Please enter valid dollar amount");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	//SelectProduct() method will display the list of items once more, allow the user to enter a code for the snack
	//that they would like to buy, dispense the snack and display the balance after the transaction
	public void selectProduct(){
		//same as display items
		for(Item item : itemList){
			System.out.println(item.getCode() + " : " + item.getName() + " " +
					formatter.format(item.getPrice()) + ", " + item.getRemaining() + " remaining");
			if(item.getRemaining() == 0)
				System.out.println("^ SOLD OUT!");
		}
		Scanner input = new Scanner(System.in);
		//this boolean will be true when the code that the user puts in finds a corresponding item
		boolean found = false;
		//prompt the user to enter a code
		System.out.print("\nPlease enter a valid two digit code: ");
		String code = input.nextLine();
		//put the code to uppercase for easier location
		code = code.toUpperCase();
		//run throuch every item in the item list
		for(Item item : itemList){
			//compare the code entered to the code of each item in the list
			if(item.getCode().equals(code)) {
				//if found, then the selected item becomes the item that the user selected
				found = true;
				selectedItem = item;
				//and then stop the loop
				break;
			}
		}
		//if the item is not found,
		if(!found)
			//then the code is invalid. the user is returned to the purchase menu
			System.out.println("Code is invalid.");
		//if the item is found, but the number remaining is zero,
		else if(selectedItem.getRemaining()==0)
			//then the item is out of stock, and the user cannot purchase it
			System.out.println("Item is out of stock.");
		//if the found item is not out of stock, but the balance is lower than the price of the item,
		else if(selectedItem.getPrice() > balance)
			//then the user has insufficient funds and cannot purchase the item.
			System.out.println("Insufficient funds.");
		//if all conditions are met, then the snack will be dispensed
		else{
			//the balance is put into a temp variable for the log
			double previousBalance = balance;
			//the price of the item is deducted from the current balance
			balance -= selectedItem.getPrice();
			//the selected item is removed from the machine, and the number remaining goes down one
			selectedItem.setRemaining(selectedItem.getRemaining()-1);
			//And then we say yum
			String yum = ", Yum!";
			//a switch-case is used to separate the different exclamations depending on the type of snack dispensed.
			switch(selectedItem.getType()){
				//chips say crunch
				case "Chip" : yum = "Crunch Crunch" + yum;
				break;
				//candy says munch
				case "Candy" : yum = "Munch Munch" + yum;
				break;
				//drinks say glug
				case "Drink" : yum = "Glug Glug" + yum;
				break;
				//gum says chew
				case "Gum" : yum = "Chew Chew" + yum;
				break;
				//if the type is invalid, then display as such
				default: yum = "invalid type";
				break;
			}
			//display the exclamation to the user
			yum = "Now dispensing " + selectedItem.getName() + ", costing "
					+ formatter.format(selectedItem.getPrice()) + "\n" + yum;
			System.out.println(yum);
			//write this transaction ito the log
			try (PrintWriter dataOutput = new PrintWriter(new FileOutputStream(logPathName, true))) {
				dataOutput.println(">" + dateFormat.format(new Date()) + " " + selectedItem.getName() + " " + selectedItem.getCode() + " " + formatter.format(previousBalance) + " " + formatter.format(balance));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	//the finish transaction method will return any change to the user and then return them user to the main menu.
	public void finishTransaction(){
		//display that the user is leaving the purchase menu
		System.out.println("Exiting Purchase Menu");
		//the balance is put into a temporary variable for the log
		double previousBalance = balance;
		//if there is still a balance in the machine,
		if(balance > 0){
			//dispense change by quarters, nickles, and dimes
			System.out.println("Here's your change: " + formatter.format(balance));
			int quarters = 0;
			//for every 25 cents, dispense a quarter
			//the while parameter is formatted as such to remove floating points
			while(balance > 0.24){
				quarters++;
				balance-=0.25;
			}
			//for every 10 cents remaining, dispense a dime
			int dimes = 0;
			while (balance > 0.09){
				dimes++;
				balance-=0.10;
			}
			//for every 5 cents remaining, dispense a nickel
			int nickels = 0;
			while(balance > 0.04){
				nickels++;
				balance-=0.05;
			}
			//display the coins dispensed
			System.out.println(quarters + " quarters, " + dimes + " dimes, " + nickels + " nickels");
			//return the balance to 0
			balance = 0;
			//log the transaction to the log
			try (PrintWriter dataOutput = new PrintWriter(new FileOutputStream(logPathName, true))) {
				dataOutput.println(">" + dateFormat.format(new Date()) + " GIVE CHANGE " + formatter.format(previousBalance) + " " + formatter.format(balance));
				dataOutput.println(">```");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		run();
	}

	//The sales report method will accumulate all the transactions that took place during this run of the program,
	//then read from the salesreport.txt, append the totals and write back into it.
	public void salesReport(){File file = new File(salePathName);
		//List of sales string is the total amount of strings currently in the sales report.
		List<String> listOfSales = new ArrayList<>();
		//total sales is declared
		double totalSales = 0;
		//the last line of the file is declared here
		String totalSalesFullString = "";
		//open the file
		try (Scanner input = new Scanner(file)) {
			//only the first 16 lines go into the string list
			for(int i=0;i<16;i++) {
				//go through each line
				String sale = input.nextLine();

				//with sales array, split by the pipe delimiter
				String[] salesArray = sale.split(Pattern.quote("|"));
				//instantiate the name and total units sold here
				String name = salesArray[0];
				String unitsSoldString = salesArray[1];
				int totalUnitsSold = Integer.parseInt(unitsSoldString);

				//Here, we get the corresponding item in the list of items
				Item currentItem = itemList.get(i);
				//snackRemaining is how many of this snack have been sold on this run
				int snackRemaining = 5-currentItem.getRemaining();
				//snack sales is the total price of this type of item that have been sold
				double snackSales = currentItem.getPrice() * snackRemaining;
				//gross remaining becomes the total number of this current snack that have been sold since the
				//machine started
				totalUnitsSold += snackRemaining;
				//the snack sales is added to the total amount of sales of all snacks
				totalSales += snackSales;

				//Here, the new string is appended to include the total number of snacks that have been sold
				sale = name + "|" + totalUnitsSold;
				//The string list adds the new sale for the item
				listOfSales.add(sale);
			}
			//skip the blank line
			input.nextLine();
			//This string displays how much money has been made since the beginning of the program
			totalSalesFullString = input.nextLine();
			//The substring takes the double that is the price
			String totalSalesFull = totalSalesFullString.substring(totalSalesFullString.indexOf("$")+1);
			//and parses it
			double currentTotalSales = Double.parseDouble(totalSalesFull);
			//we add the total sales made in this run to the total gross sales of all transactions
			totalSales += currentTotalSales;

			//and then append the new line here
			totalSalesFullString = "TOTAL SALES: " + formatter.format(totalSales);
		}
		catch (FileNotFoundException e){
			System.out.println("File not found: " + file.getAbsolutePath());
		}

		//write the appended lines back into the file
		try (PrintWriter dataOutput = new PrintWriter(tempPathName)) {
			for(String saleName : listOfSales)
				//one at a time
				dataOutput.println(saleName);
			dataOutput.println("\n"+totalSalesFullString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//we have to write into a temp file and then move the file into the previous location.
		new File(salePathName).delete();
		File sReport = new File(tempPathName);
		sReport.renameTo(new File(salePathName));

	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.stockItems();
		cli.run();
	}
}

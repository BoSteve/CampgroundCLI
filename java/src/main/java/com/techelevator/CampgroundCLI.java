package com.techelevator;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.view.Menu;

public class CampgroundCLI {

	private Menu menu;

	private static final String MAIN_MENU_PARK_1 = "Acadia";
	private static final String MAIN_MENU_PARK_2 = "Arches";
	private static final String MAIN_MENU_PARK_3 = "Cuyahoga National Valley Park";
	private static final String MAIN_MENU_OPTION_EXIT = "EXIT";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_PARK_1, MAIN_MENU_PARK_2, MAIN_MENU_PARK_3,
			MAIN_MENU_OPTION_EXIT};

	private static final String[] PARK_INFO = { "View Camp Grounds", "Search for Reservation", "Back" };

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.run();
	}

	public CampgroundCLI(DataSource datasource) {
		// create your DAOs here
		
	}

	public void run() {
		while (true) {
			System.out.println("MAIN MENU");
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(MAIN_MENU_PARK_1)) {
				parkInfoScreen();
			} else if (choice.equals(MAIN_MENU_PARK_2)) {
				parkInfoScreen();
			} else if (choice.equals(MAIN_MENU_PARK_3)) {
				parkInfoScreen();
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				System.out.println("Get outta here!");
				System.exit(1);
			}
		}
	}

	private void viewParksInterface() {
		String purchaseMenuOption = "";
		while (!purchaseMenuOption.equals("Back")) {
			purchaseMenuOption = (String) menu.getChoiceFromOptions(PARK_INFO);
			if (purchaseMenuOption.equals("Acadia")) {
				parkInfoScreen();
			} else if (purchaseMenuOption.equals("Arches")) {
				parkInfoScreen();
			} else if (purchaseMenuOption.equals("Cuyahoga National Valley Park")) {
				parkInfoScreen();
			}
		}
	}

	private void parkInfoScreen() {
		String purchaseMenuOption = "";
		while (!purchaseMenuOption.equals("Back")) {
			purchaseMenuOption = (String) menu.getChoiceFromOptions(PARK_INFO);
			if (purchaseMenuOption.equals("View Camp Grounds")) {
				parkInfoScreen();
			} else if (purchaseMenuOption.equals("Search For Reservation")) {
				parkInfoScreen();
			}
		}
	}

}

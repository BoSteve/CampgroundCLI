package com.techelevator.CLI;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.parks.model.CampgroundDAO;
import com.techelevator.parks.model.ParkDAO;
import com.techelevator.parks.model.Reservation;
import com.techelevator.parks.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.parks.model.jdbc.JDBCParkDAO;
import com.techelevator.parks.model.jdbc.JDBCReseravtionDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {

 private static final String EXIT = "EXIT";
 private static final String RETURN = "Return to Previous Screen";

 // Main Menu
 private static final String MAIN_MENU_PARK_1 = "Acadia";
 private static final String MAIN_MENU_PARK_2 = "Arches";
 private static final String MAIN_MENU_PARK_3 = "Cuyahoga National Valley Park";
 private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_PARK_1, MAIN_MENU_PARK_2, MAIN_MENU_PARK_3, EXIT };

 private static final String PARK_INFO_VIEW_CAMPS = "View Camp Grounds";
 private static final String PARK_INFO_SEARCH_RESERVES = "Search for Reservations";
 private static final String[] PARK_INFO_OPTIONS = { PARK_INFO_VIEW_CAMPS, PARK_INFO_SEARCH_RESERVES, RETURN, EXIT };

 private static final String PARK_CAMPS_AVAILABLE = "Available Reservations";
 private static final String[] PARK_CAMPS_OPTIONS = { PARK_CAMPS_AVAILABLE, RETURN, EXIT };
 
 private static final String RESERVE_AGAIN = "Reserve Another Site";
 private static final String[] RESERVATION_COMPLETED_OPTIONS = { RESERVE_AGAIN, EXIT };
 
 private Menu menu;
 private CampgroundDAO campgroundDAO;
 private ParkDAO parkDAO;
 private Reservation reservationDAO;
BasicDataSource dataSource = new BasicDataSource();
 
 public static void main(String[] args) {
	 	CampgroundCLI application = new CampgroundCLI(dataSource);
	 	application.run();
	 }

 public CampgroundCLI(DataSource datasource) {
 	this.menu = new Menu(System.in, System.out);
 	dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
 	dataSource.setUsername("postgres");
 	dataSource.setPassword("postgres1");
 	// create your DAOs here
 	campgroundDAO = new JDBCCampgroundDAO();
 	reservationDAO = new JDBCParkDAO(dataSource);
 	parkDAO = new JDBCReseravtionDAO();

 }
 


 private void run() {
 	System.out.println("Main Menu");
 	System.out.println("------");
 	System.out.println("SELECT A PARK");
 	while (true) {
 		String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
 		if (choice.equals(MAIN_MENU_PARK_1)) {
 			displayParkInfo();
 		} else if (choice.equals(MAIN_MENU_PARK_2)) {
 			displayParkInfo();
 		} else if (choice.equals(MAIN_MENU_PARK_3)) {
 			displayParkInfo();
 		} else if (choice.equals(EXIT)) {
 			System.exit(0);
 		}
 	}
 }

 private void displayParkInfo() {
 	String choice = (String) menu.getChoiceFromOptions(PARK_INFO_OPTIONS);
 	if (choice.equals(PARK_INFO_VIEW_CAMPS)) {
 		displayParkCamps();
 	} else if (choice.equals(PARK_INFO_SEARCH_RESERVES)) {
 		displayParkCampsReservation();
 	} else if (choice.equals(RETURN)) {
 		run();
 	}
 	else if (choice.equals(EXIT)) {
 		System.exit(0);
 	}
 }

 private void displayParkCamps() {
 	String choice = (String) menu.getChoiceFromOptions(PARK_CAMPS_OPTIONS);
 	if (choice.equals(PARK_CAMPS_AVAILABLE)) {
 		displayParkCampsReservation();
 	} else if (choice.equals(RETURN)) {
 		displayParkInfo();
 	}
 	else if (choice.equals(EXIT)) {
 		System.exit(0);
 	}
 }

 private void displayParkCampsReservation() {
 	System.out.println("Which campground?");
 	System.out.println("Arrival?");
 	System.out.println("Departure?");
 	//DISPLAY OPTIONS HERE. 
 	System.out.println("Select site to reserve");
 	System.out.println("Name for reservation?");
 	System.out.println("RESERVATION ID");
 	
 	String choice = (String) menu.getChoiceFromOptions(RESERVATION_COMPLETED_OPTIONS);
 	if (choice.equals(RESERVE_AGAIN)) {
 		run();
 	}
 	else if (choice.equals(EXIT)) {
 		System.exit(0);
 	}
 }
}
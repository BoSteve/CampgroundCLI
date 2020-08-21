package com.techelevator.CLI;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.parks.model.Campground;
import com.techelevator.parks.model.CampgroundDAO;
import com.techelevator.parks.model.Park;
import com.techelevator.parks.model.ParkDAO;
import com.techelevator.parks.model.ReservationDAO;
import com.techelevator.parks.model.Site;
import com.techelevator.parks.model.SiteDAO;
import com.techelevator.parks.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.parks.model.jdbc.JDBCParkDAO;
import com.techelevator.parks.model.jdbc.JDBCReseravtionDAO;
import com.techelevator.parks.model.jdbc.JDBCSiteDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {

	private static final String EXIT = "EXIT";
	private static final String RETURN = "Return to Previous Screen";

	private Menu menu;
	private CampgroundDAO campgroundDAO;
	private ParkDAO parkDAO;
	private ReservationDAO reservationDAO;
	private SiteDAO siteDAO;
	private int parkSelected = 0;

	static BasicDataSource dataSource = new BasicDataSource();
	private String [] allParks;
	private JDBCParkDAO jdbcParkDAO = new JDBCParkDAO(dataSource);

	// Main Menu
	// set an array with these values in it.
	private static String[] MAIN_MENU_OPTIONS;

	private static final String PARK_INFO_VIEW_CAMPS = "View Camp Grounds";
	private static final String PARK_INFO_SEARCH_RESERVES = "Search for Reservations";
	private static final String[] PARK_INFO_OPTIONS = { PARK_INFO_VIEW_CAMPS, PARK_INFO_SEARCH_RESERVES, RETURN, EXIT };

	private static final String PARK_CAMPS_AVAILABLE = "Available Reservations";
	private static final String[] PARK_CAMPS_OPTIONS = { PARK_CAMPS_AVAILABLE, RETURN, EXIT };

	private static final String RESERVE_AGAIN = "Reserve Another Site";
	private static final String[] RESERVATION_COMPLETED_OPTIONS = { RESERVE_AGAIN, EXIT };

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
		campgroundDAO = new JDBCCampgroundDAO(datasource);
		parkDAO = new JDBCParkDAO(datasource);
		reservationDAO = new JDBCReseravtionDAO(datasource);
		siteDAO = new JDBCSiteDAO(datasource);
		// make a seperate setter later
		allParksSetter();
		System.out.println(siteDAO.getSiteInfoByCampName("Blackwoods").get(4).getSiteId());

	}

	private void allParksSetter() {
		this.allParks = new String[jdbcParkDAO.getNameByParkId().size()+1];
		jdbcParkDAO.getNameByParkId().toArray(allParks);
		allParks[jdbcParkDAO.getNameByParkId().size()] = "EXIT";
		System.out.println(allParks[0]);
		System.out.println(allParks[3]);
		this.MAIN_MENU_OPTIONS = allParks;
	}

	private void run() {
		System.out.println("Main Menu");
		System.out.println("------");
		System.out.println("SELECT A PARK");
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(allParks[0])) {
				parkSelected = 1;
				handleGetAllParksByName(allParks[0]);
				displayParkInfo();
			} else if (choice.equals(allParks[1])) {
				parkSelected = 2;
				handleGetAllParksByName(allParks[1]);
				displayParkInfo();
			} else if (choice.equals(allParks[2])) {
				handleGetAllParksByName(allParks[2]);
				parkSelected = 3;
				displayParkInfo();
			} else if (choice.equals(EXIT)) {
				System.out.println("Have a nice trip!");
				System.exit(0);
			}
		}
	}

	// Change this to get by name
	private void handleGetAllParksByName(String parkName) {
		Park allParks = parkDAO.getParkName(parkName);
		listParkInfo(allParks);
	}

	private void listParkInfo(Park parks) {
		System.out.println();
		System.out.println(parks.getParkName() + ", " + parks.getParkLocation() + "\nEST. " + parks.getEstablishedYear()
				+ "\nAREA (sq/km): " + parks.getArea() + "\nAnnual Visitors: " + parks.getAnnualVisitors()
				+ "\nDESCRIPTION:\n " + parks.getDescription());
	}

	// Park Info //

	private void displayParkInfo() {
		String choice = (String) menu.getChoiceFromOptions(PARK_INFO_OPTIONS);
		if (choice.equals(PARK_INFO_VIEW_CAMPS)) {
			handleGetAllCamps();
			displayParkCamps();
		} else if (choice.equals(PARK_INFO_SEARCH_RESERVES)) {
			displayParkCampsReservation();
		} else if (choice.equals(RETURN)) {
			run();
		} else if (choice.equals(EXIT)) {
			System.exit(0);
		}
	}

	// Change this to get by name //
	private void handleGetAllCamps() {
		List<Campground> allCampgrounds = campgroundDAO.getAllCampgroundsByParkId(parkSelected);
		listAllCamps(allCampgrounds);
	}

	private void listAllCamps(List<Campground> Campgrounds) {
		System.out.println();
		if (Campgrounds.size() > 0) {
			for (Campground Campground : Campgrounds) {
				System.out.println(
						"Name: " + Campground.getNameOfCampground() + "\n" + "Open Months: " + Campground.getOpenMonth()
								+ " until " + Campground.getCloseMonth() + "\n$" + Campground.getDailyFee() + "/Day\n");
			}
		} else {
			System.out.println("\n*** No results ***");
		}
	}

	// Park Campgrounds //

	private void displayParkCamps() {
		String choice = (String) menu.getChoiceFromOptions(PARK_CAMPS_OPTIONS);
		handleGetAllCamps();
		if (choice.equals(PARK_CAMPS_AVAILABLE)) {
			displayParkCampsReservation();
		} else if (choice.equals(RETURN)) {
			displayParkInfo();
		} else if (choice.equals(EXIT)) {
			System.exit(0);
		}
	}

	private String arrivalSelect = "";
	private String departureSelect = "";

	private void displayParkCampsReservation() {
		System.out.print("Enter campground number>>>");
		Scanner scan = new Scanner(System.in);
		String campgroundSelect = scan.nextLine();
//		System.out.println(campgroundDAO.getAllCampgroundsByParkId(parkSelected).get(Integer.parseInt(campgroundSelect)-1).getNameOfCampground());
		
		String camp = campgroundDAO.getAllCampgroundsByParkId(parkSelected).get(Integer.parseInt(campgroundSelect)-1).getNameOfCampground();
		System.out.println("name " + camp);
		System.out.println(siteDAO.getSiteInfoByCampName(camp).get(0).getCampgroundId());
		try {
			List<Site> sites = siteDAO.getSiteInfoByCampName(campgroundSelect);
				for (Site Campground : sites) {
					System.out.println(siteDAO.getSiteInfoByCampName("Blackwoods"));
				}
		} catch (Exception e) {
			System.out.println("INVALED INPUT!!! RETURNING TO RESERVATION SCREEN");
			displayParkCamps();
		}
		
		// Scans userinput, selects campground by number,
		// retrieve site_info by campground_id
		System.out.println("Enter Arrival date: MM/DD/YYYY");
		String arrivalSelect = scan.nextLine();
		//plug arrival date into reservation
		System.out.println("Departure: __/__/__?");
		String departureSelect = scan.nextLine();
		//plug departure date into reservation
		
		// DISPLAY OPTIONS HERE.
		System.out.println("Select site to reserve");
		String siteReserved = scan.nextLine();
		//plug siteReserved into site_id
		System.out.println("Name for reservation?");
		String nameOfReservation = scan.nextLine();
		//plug nameOfReservation into reservation_name
		System.out.println("RESERVATION ID");
		//return the reservation_id

		String choice = (String) menu.getChoiceFromOptions(RESERVATION_COMPLETED_OPTIONS);
		if (choice.equals(RESERVE_AGAIN)) {
			run();
		} else if (choice.equals(EXIT)) {
			System.exit(0);
		}
	}

}
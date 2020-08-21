package com.techelevator.CLI;

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

	private Menu menu;
	private JDBCParkDAO jdbcParkDAO = new JDBCParkDAO(dataSource);
	static BasicDataSource dataSource = new BasicDataSource();

	private CampgroundDAO campgroundDAO;
	private ParkDAO parkDAO;
	private ReservationDAO reservationDAO;
	private SiteDAO siteDAO;
	
	private int parkSelected = 0;
	private String[] allParks;
	
	private static String[] MAIN_MENU_OPTIONS;
	private static final String RETURN = "Go back";
	private static final String EXIT = "Exit";
	private static final String PARK_INFO_VIEW_CAMPS = "View Park Campgrounds";
	private static final String[] PARK_INFO_OPTIONS = { PARK_INFO_VIEW_CAMPS, RETURN, EXIT };
	private static final String RESERVE_AGAIN = "Reserve Another Site";
	private static final String[] RESERVATION_COMPLETED_OPTIONS = { RESERVE_AGAIN, EXIT };

	public static void main(String[] args) {
		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.mainMenu();
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
		allParksSetter();
	}

	private void allParksSetter() {
		this.allParks = new String[jdbcParkDAO.getNameByParkId().size() + 1];
		jdbcParkDAO.getNameByParkId().toArray(allParks);
		allParks[jdbcParkDAO.getNameByParkId().size()] = "Exit";
		this.MAIN_MENU_OPTIONS = allParks;
	}

	private void mainMenu() {
		System.out.println("Main Menu");
		System.out.println("------");
		System.out.println("SELECT A PARK");
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(allParks[0])) {
				parkSelected = 1;
				handleGetAllParksByName(allParks[0]);
				displayParkInfoMenu();
			} else if (choice.equals(allParks[1])) {
				parkSelected = 2;
				handleGetAllParksByName(allParks[1]);
				displayParkInfoMenu();
			} else if (choice.equals(allParks[2])) {
				handleGetAllParksByName(allParks[2]);
				parkSelected = 3;
				displayParkInfoMenu();
			} else if (choice.equals(EXIT)) {
				System.out.println("Have a nice trip!");
				System.exit(0);
			}
		}
	}

	private void handleGetAllParksByName(String parkName) {
		Park allParks = parkDAO.getParkName(parkName);
		listParkInfo(allParks);
	}
	private void listParkInfo(Park parks) {
		System.out.println("\n" + parks.getParkName() + ", " + parks.getParkLocation() + "\nEST.\t\t" + parks.getEstablishedYear()
				+ "\nAREA:\t\t" + parks.getArea() + "sqr kilometers\n" + "Visitors/yr:\t" + parks.getAnnualVisitors()
				+ "\n\nDESCRIPTION:\n" + parks.getDescription());
		//make the description visually appealing
	}

	private void displayParkInfoMenu() {
		String choice = (String) menu.getChoiceFromOptions(PARK_INFO_OPTIONS);
		if (choice.equals(PARK_INFO_VIEW_CAMPS)) {
			handleGetAllCamps();
			displayParkCampsReservationMenu();
		} else if (choice.equals(RETURN)) {
			mainMenu();
		} else if (choice.equals(EXIT)) {
			System.out.println("Have a nice trip!");
			System.exit(0);
		}
	}

	// Change this to get by name //
	private void handleGetAllCamps() {
		List<Campground> allCampgrounds = campgroundDAO.getAllCampgroundsByParkId(parkSelected);
		listAllCamps(allCampgrounds);
	}

	private void listAllCamps(List<Campground> Campgrounds) {
		int counter =0;
		if (Campgrounds.size() > 0) {
			for (Campground Campground : Campgrounds) {
				System.out.println("Campground:" + ++counter);
				System.out.println(
						"Name: " + Campground.getNameOfCampground() + "\n" + "Open Months: " + Campground.getOpenMonth()
								+ " until " + Campground.getCloseMonth() + "\n$" + Campground.getDailyFee() + "/Day\n");
			}
		} else {
			System.out.println("\n*** No results ***");
		}
	}

	// Park Campgrounds //

	private void displayParkCampsReservationMenu() {
		System.out.print("Select campground number >>>");
		Scanner scan = new Scanner(System.in);
		String campgroundSelect = scan.nextLine();
		String camp = campgroundDAO.getAllCampgroundsByParkId(parkSelected).get(Integer.parseInt(campgroundSelect) - 1)
				.getNameOfCampground().toString();
		System.out.println("Campgrounds: " + camp);		
		handleGetAllSites(camp);
		handleGetAllSitesEmpty(camp);
		
		//put this whole thing in a try-catch
		System.out.print("\nEnter Arrival date: YYYY/MM/DD >>>");
		String arrivalSelect = scan.nextLine();
		System.out.println("Selected Arrival: "+ reservationDAO.stringToDateToSQL(arrivalSelect));
		System.out.print("\nEnter Departure date: YYYY/MM/DD >>>");
		String departureSelect = scan.nextLine();
		System.out.println("Selected Departure: "+ reservationDAO.stringToDateToSQL(departureSelect));
		
		listAllSites(siteDAO.dateToSet(reservationDAO.stringToDateToSQL(arrivalSelect), 
				reservationDAO.stringToDateToSQL(departureSelect), 
				campgroundDAO.getCampgroundIdByName(camp)));
		// plug departure date into reservation

		System.out.println("\nSelect site to reserve by site ID >>>");
		int siteReserved = scan.nextInt();
		System.out.println("Name for reservation?");
		String nameOfReservation = scan.nextLine();
		// plug nameOfReservation into reservation_name
		System.out.println("RESERVATION ID");
		// return the reservation_id

		String choice = (String) menu.getChoiceFromOptions(RESERVATION_COMPLETED_OPTIONS);
		if (choice.equals(RESERVE_AGAIN)) {
			mainMenu();
		} else if (choice.equals(EXIT)) {
			System.exit(0);
		}
	}

	private void handleGetAllSites(String input) {
		System.out.println("\nSites by Popularity");
		List<Site> allSites = siteDAO.getSiteInfoByCampName(input);
		listAllSites(allSites);
	}
	
	private void handleGetAllSitesEmpty(String input) {
		List<Site> allSites = siteDAO.getSiteInfoByCampNameEmpty(input);
		listAllSites(allSites);
	}
	
	
	private void listNonOverlapSites (List<Site> inputSite) {
		String result = "";
		for (Site sites : inputSite) {				
			result = "Site Id. " + sites.getSiteId()+" Site No. " + sites.getSiteNumber() + " Max Occup. " + sites.getMaxOccupancy()
			+ " Accessible? " + sites.isItAccessible() + " Max RV Length " + sites.getMaxRvLength()
			+ " Utilities? " + sites.isUtilities();
			System.out.println(result);
		}
	}

	private String listAllSites(List<Site> inputSite) {
		String result ="";
		if (inputSite.size() > 0) {
			for (Site sites : inputSite) {				
				result = "Site Id. " + sites.getSiteId()+" Site No. " + sites.getSiteNumber() + " Max Occup. " + sites.getMaxOccupancy()
				+ " Accessible? " + sites.isItAccessible() + " Max RV Length " + sites.getMaxRvLength()
				+ " Utilities? " + sites.isUtilities();
				System.out.println(result);
			}
		} else {
			System.out.println("ERROR");
		}
		return result;
	}
}
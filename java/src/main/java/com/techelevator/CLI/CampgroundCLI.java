package com.techelevator.CLI;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public static void main(String[] args) {
		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.mainMenu();
	}

	public CampgroundCLI(DataSource datasource) {
		this.menu = new Menu(System.in, System.out);
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		campgroundDAO = new JDBCCampgroundDAO(datasource);
		parkDAO = new JDBCParkDAO(datasource);
		reservationDAO = new JDBCReseravtionDAO(datasource);
		siteDAO = new JDBCSiteDAO(datasource);
		// sets up the parks in main menu
		setMainMenuParks();
	}

	private static String[] MAIN_MENU_OPTIONS;
	private static final String RETURN = "Go back";
	private static final String EXIT = "Exit";
	private static final String PARK_INFO_VIEW_CAMPS = "View Park Campgrounds";
	private static final String[] PARK_INFO_OPTIONS = { PARK_INFO_VIEW_CAMPS, RETURN, EXIT };
	private static final String RESERVE_AGAIN = "Reserve Another Site";
	private static final String[] RESERVATION_COMPLETED_OPTIONS = { RESERVE_AGAIN, EXIT };

	// records user's park choice from array of parks
	private int parkSelected = 0;
	private String[] allParks;

	// IDs of all sites within date
	List<Long> sitesNarrowedByDate = new ArrayList<Long>();

	// 1
	private void mainMenu() {
		System.out.println("WELCOME TO CAMPSITE SELECTOR");
		System.out.println("\n--SELECT A PARK--");
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(allParks[0])) {
				handlePrintAllParksByName(allParks[0]);
				parkSelected = 1;
			} else if (choice.equals(allParks[1])) {
				handlePrintAllParksByName(allParks[1]);
				parkSelected = 2;
			} else if (choice.equals(allParks[2])) {
				handlePrintAllParksByName(allParks[2]);
				parkSelected = 3;
			} else if (choice.equals(EXIT)) {
				exile();
			}
			displayParkInfoMenu();
		}
	}

	// 2
	private void displayParkInfoMenu() {
		String choice = (String) menu.getChoiceFromOptions(PARK_INFO_OPTIONS);
		if (choice.equals(PARK_INFO_VIEW_CAMPS)) {
			handlePrintAllCamps();
			displayParkCampsReservationMenu();
		} else if (choice.equals(RETURN)) {
			mainMenu();
		} else if (choice.equals(EXIT)) {
			exile();
		}
	}

	// 3

	// records user's inputs, passes into encapsulated methods
	private String camp = null;
	private String arrivalSelect = null;
	private String departureSelect = null;
	private String campgroundSelect = null;
	private String siteReserved = null;

	private void displayParkCampsReservationMenu() {

		System.out.print("\nSelect campground number >>> ");
		Scanner scan = new Scanner(System.in);
		inputCampground(scan);

		inputArrivalDate(scan);
		inputDepartureDate(scan);

		printAllSitesGivenDate(camp, arrivalSelect, departureSelect);

		validSite(scan);
		System.out.println("Site " + siteReserved + " is available!" );
		String nameOfReservation = makeReservation(scan);
		confirmReservation(nameOfReservation);

		String choice = (String) menu.getChoiceFromOptions(RESERVATION_COMPLETED_OPTIONS);
		if (choice.equals(RESERVE_AGAIN)) {
			mainMenu();
			wipeSitesNarrowed();
		} else if (choice.equals(EXIT)) {
			exile();
		}
	}

	//

	//

	//

	//

	//

	public void confirmReservation(String nameOfReservation) {
		int totalDays = reservationDAO.stringToDateToSQL(departureSelect)
				.compareTo(reservationDAO.stringToDateToSQL(arrivalSelect));
		BigDecimal bDTotalDays = new BigDecimal(totalDays);
		System.out.println("\n--CONFIRMATION--");
		System.out.println("Reservation ID: " + reservationDAO.getReservationId(Long.parseLong(siteReserved),
				reservationDAO.stringToDateToSQL(arrivalSelect), reservationDAO.stringToDateToSQL(departureSelect),
				nameOfReservation));
		System.out.println("Site " + siteReserved + " reserved for " + nameOfReservation);
		System.out.println("From: " + arrivalSelect + " Until: " + departureSelect + "\n");
		System.out.println("Days booked: " + bDTotalDays);
		System.out.println("Cost/Day: " + campgroundDAO.getCampgroundCostByName(camp));
		System.out.println("Subtotal: $" + campgroundDAO.getCampgroundCostByName(camp).multiply(bDTotalDays));
	}

	public String makeReservation(Scanner scan) {
		System.out.println("Name for reservation >>> ");
		String nameOfReservation = scan.nextLine();
		System.out.println("Reservation for: " + nameOfReservation);
		try {
			reservationDAO.createReservation(Long.parseLong(siteReserved),
					reservationDAO.stringToDateToSQL(arrivalSelect), reservationDAO.stringToDateToSQL(departureSelect),
					nameOfReservation);
		} catch (Exception e) {
			System.out.println("Reservation failed");
		}
		return nameOfReservation;
	}

	public void validSite(Scanner scan) {
		System.out.println("\nSelect site to reserve by Site ID >>> ");
		Long upper = sitesNarrowedByDate.get(sitesNarrowedByDate.size() - 1);
		Long lower = sitesNarrowedByDate.get(0);
		try {
			siteReserved = scan.nextLine();

			if (!(Long.parseLong(siteReserved) >= lower && Long.parseLong(siteReserved) <= upper)) {
				System.out.println("Selected campground is not available");
				validSite(scan);
			}
		} catch (Exception e) {
			System.out.println("Invalid format entered");
			validSite(scan);
		}
	}

	public void inputArrivalDate(Scanner scan) {
		try {
			System.out.print("\nEnter Arrival date: YYYY/MM/DD >>> ");
			arrivalSelect = scan.nextLine();
			System.out.println("Selected Arrival: " + reservationDAO.stringToDateToSQL(arrivalSelect));
		} catch (Exception e) {
			System.out.println("Invalid Date Entered");
			inputArrivalDate(scan);
		}
	}

	public void inputDepartureDate(Scanner scan) {
		try {
			System.out.print("\nEnter Departure date: YYYY/MM/DD >>> ");
			departureSelect = scan.nextLine();
			System.out.println("Selected Departure: " + reservationDAO.stringToDateToSQL(departureSelect)
					+ "\n\n--Sites available between--\n" + arrivalSelect + " - " + departureSelect + "\n");
		} catch (Exception e) {
			System.out.println("Invalid Date");
			inputDepartureDate(scan);
		}
	}

	public void inputCampground(Scanner scan) {
		try {
			campgroundSelect = scan.nextLine();
			camp = displayCurrentParkCamp(campgroundSelect);
		} catch (Exception e) {
			System.out.println("Invalid campsite number");
			displayParkCampsReservationMenu();
		}
	}

	Map<String, String> monthMap = new HashMap<String, String>() {
		{
			put("01", "Jan");
			put("02", "Feb");
			put("03", "Mar");
			put("04", "Apr");
			put("05", "May");
			put("06", "Jun");
			put("07", "Jul");
			put("08", "Aug");
			put("09", "Sep");
			put("10", "Oct");
			put("11", "Nov");
			put("12", "Dec");
		}
	};

	private String printMonthName(String monthnum) {
		String result = monthMap.get(monthnum);
		return result;
	}

	private String printCommaFormat(Long lng) {
		DecimalFormat df = new DecimalFormat("#,###");
		String result = df.format(lng);
		return result;
	}

	private String printYearMonthName(LocalDate date) {
		String[] splitted = date.toString().split("-");
		String year = splitted[0];
		String month = splitted[1];
		String day = splitted[2];
		String result = monthMap.get(month) + " " + day + " " + year;
		return result;
	}

	public void printAllSitesGivenDate(String camp, String arrivalSelect, String departureSelect) {
		List<Site> sitesFromDateId = siteDAO.sitesByDate(reservationDAO.stringToDateToSQL(arrivalSelect),
				reservationDAO.stringToDateToSQL(departureSelect), campgroundDAO.getCampgroundIdByName(camp));
		Long id;
		for (Site holder : sitesFromDateId) {
			id = holder.getSiteId();
			sitesNarrowedByDate.add(id);
		}

		List<Site> sorted = siteDAO.sortSitesByReservations(sitesNarrowedByDate);
		handlePrintSitesPopular(sorted);
	}

	private String displayCurrentParkCamp(String campgroundSelect) {
		String camp = campgroundDAO.getAllCampgroundsByParkId(parkSelected).get(Integer.parseInt(campgroundSelect) - 1)
				.getNameOfCampground();
		System.out.println("\n--Information Selected--\nPark: " + allParks[parkSelected - 1]);
		System.out.println("Camp: " + camp);
		return camp;
	}

	// prints all sites w/o popularity
	private void handlePrintSitesNoPop(String input) {
		String result = "";
		// acquire sites by date restriction
		List<Site> allSites = siteDAO.sitesByDate(reservationDAO.stringToDateToSQL(arrivalSelect),
				reservationDAO.stringToDateToSQL(departureSelect), campgroundDAO.getCampgroundIdByName(camp));

		System.out.println("Sites within date (not sorted)");
		System.out.println("ID.\tNo.\tMax Occup.\tMax RV length\tAccessible\tUtilities");
		try {
			for (int i = 0; i < 5; i++) {
				result = allSites.get(i).getSiteId() + "\t" + allSites.get(i).getSiteNumber() + "\t"
						+ allSites.get(i).getMaxOccupancy() + "\t\t" + allSites.get(i).getMaxRvLength() + "\t\t"
						+ allSites.get(i).isItAccessible() + "\t\t" + allSites.get(i).isUtilities();
				System.out.println(result);
			}
		} catch (Exception e) {
		}
	}

	// prints all sites by popularity
	private String handlePrintSitesPopular(List<Site> inputSite) {
		String result = "";
		if (inputSite.size() >= 5) {
			System.out.println("Top 5 campsites!");
			System.out.println("ID.\tNo.\tMax Occup.\tMax RV length\tAccessible\tUtilities");
			for (Site sites : inputSite) {
				result = sites.getSiteId() + "\t" + sites.getSiteNumber() + "\t" + sites.getMaxOccupancy() + "\t\t"
						+ sites.getMaxRvLength() + "\t\t" + sites.isItAccessible() + "\t\t" + sites.isUtilities();
				System.out.println(result);
			}
		} else {
			handlePrintSitesNoPop(camp);
		}
		return result;
	}

	private void setMainMenuParks() {
		this.allParks = new String[jdbcParkDAO.getNameByParkId().size() + 1];
		jdbcParkDAO.getNameByParkId().toArray(allParks);
		allParks[jdbcParkDAO.getNameByParkId().size()] = "Exit";
		this.MAIN_MENU_OPTIONS = allParks;
	}

	private void printParkInfo(Park parks) {
		System.out.println("\n" + parks.getParkName() + "\nLocation:\t" + parks.getParkLocation() + "\nEST.\t\t"
				+ printYearMonthName(parks.getEstablishedYear()) + "\nAREA:\t\t" + printCommaFormat(parks.getArea())
				+ " sq km \n" + "Visitors/yr:\t" + printCommaFormat(parks.getAnnualVisitors()) + "\n\nDESCRIPTION:\n"
				+ parks.getDescription());
	}

	private void handlePrintAllParksByName(String parkName) {
		System.out.println();
		System.out.println("--Park Information--");
		Park allParks = parkDAO.getParkName(parkName);
		printParkInfo(allParks);
	}

	private void handlePrintAllCamps() {
		List<Campground> allCampgrounds = campgroundDAO.getAllCampgroundsByParkId(parkSelected);
		printAllCamps(allCampgrounds);
	}

	private void printAllCamps(List<Campground> Campgrounds) {
		int counter = 0;
		System.out.println("\n--Campground Info--\n\nPark: " + allParks[parkSelected - 1]);
		System.out.println("ID\tCost\t\tOpen Months\tName");
		if (Campgrounds.size() > 0) {
			for (Campground Campground : Campgrounds) {
				System.out.println(++counter + "\t$" + Campground.getDailyFee() + "/Day\t"
						+ printMonthName(Campground.getOpenMonth()) + " - " + printMonthName(Campground.getCloseMonth())
						+ "\t" + Campground.getNameOfCampground());
			}
		} else {
			System.out.println("\n*** No results ***");
		}
	}

	public void wipeSitesNarrowed() {
		sitesNarrowedByDate.removeAll(sitesNarrowedByDate);
	}

	private void exile() {
		System.out.println("Happy Camping!");
		System.exit(0);
	}

}
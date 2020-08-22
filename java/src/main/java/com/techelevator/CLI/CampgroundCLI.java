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

	private static String[] MAIN_MENU_OPTIONS;
	private static final String RETURN = "Go back";
	private static final String EXIT = "Exit";
	private static final String PARK_INFO_VIEW_CAMPS = "View Park Campgrounds";
	private static final String[] PARK_INFO_OPTIONS = { PARK_INFO_VIEW_CAMPS, RETURN, EXIT };
	private static final String RESERVE_AGAIN = "Reserve Another Site";
	private static final String[] RESERVATION_COMPLETED_OPTIONS = { RESERVE_AGAIN, EXIT };

	// records user's park choice, creates list of parks
	private int parkSelected = 0;
	private String[] allParks;
	List<Long> sitesNarrowed = new ArrayList<Long>();

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
		allParksSetter();
	}

	// 1
	private void mainMenu() {
		System.out.println("WELCOME TO CAMPSITE SELECTOR");
		System.out.println("\n--SELECT A PARK--");
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(allParks[0])) {
				handleGetAllParksByName(allParks[0]);
				parkSelected = 1;
			} else if (choice.equals(allParks[1])) {
				handleGetAllParksByName(allParks[1]);
				parkSelected = 2;
			} else if (choice.equals(allParks[2])) {
				handleGetAllParksByName(allParks[2]);
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
			handleGetAllCamps();
			displayParkCampsReservationMenu();
		} else if (choice.equals(RETURN)) {
			mainMenu();
		} else if (choice.equals(EXIT)) {
			exile();
		}
	}

	private String camp = null;
	private String arrivalSelect = null;
	private String departureSelect = null;
	private String campgroundSelect = null;
	private String siteReserved = null;

	// 3
	private void displayParkCampsReservationMenu() {

		System.out.print("\nSelect campground number >>> ");
		Scanner scan = new Scanner(System.in);

		inputCampground(scan);
		inputArrivalDate(scan);
		inputDepartureDate(scan);

		printAllSitesGivenDate(camp, arrivalSelect, departureSelect);

		validSite(scan); 
		System.out.println("Selected site: " + siteReserved);
		
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
		siteReserved = scan.nextLine();
		
		Long upper = sitesNarrowed.get(sitesNarrowed.size()-1);
		Long lower = sitesNarrowed.get(0);
		if (!(Long.parseLong(siteReserved) >= lower && Long.parseLong(siteReserved) <= upper)) {
			System.out.println("Invalid Campsite selected");
			validSite(scan);
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
			camp = listSitesByPopularity(campgroundSelect);
		} catch (Exception e) {
			System.out.println("Invalid campsite number");
			displayParkCampsReservationMenu();
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
		List<Site> sitesFromDateId = siteDAO.dateToSet(reservationDAO.stringToDateToSQL(arrivalSelect),
				reservationDAO.stringToDateToSQL(departureSelect), campgroundDAO.getCampgroundIdByName(camp));
		listAllSites(sitesFromDateId);
		Long id;
		for (Site holder : sitesFromDateId) {
			id = holder.getSiteId();
			sitesNarrowed.add(id);
		}
	}

	private String listSitesByPopularity(String campgroundSelect) {
		String camp = campgroundDAO.getAllCampgroundsByParkId(parkSelected).get(Integer.parseInt(campgroundSelect) - 1)
				.getNameOfCampground();

		System.out.println("\n--Site Information--\nPark: " + allParks[parkSelected - 1]);
		System.out.println("Camp: " + camp + "\n");
		int size = campgroundDAO.getAllCampgroundsByParkId(parkSelected).size();

		List<Site> reservationChecker = siteDAO.getSiteInfoByCampName(camp);
		if (reservationChecker.size() >= 5) {
			handleGetAllSites(camp);
		} else
			handleGetAllSitesEmpty(camp);
		return camp;
	}

	private void handleGetAllSites(String input) {
		System.out.println("Top 5 Most Popular Sites:");
		List<Site> allSites = siteDAO.getSiteInfoByCampName(input);
		listAllSites(allSites);
	}

	private void handleGetAllSitesEmpty(String input) {
		List<Site> allSites = siteDAO.getSiteInfoByCampNameEmpty(input);
		listAllSites(allSites);
	}

	private String listAllSites(List<Site> inputSite) {
		String result = "";
		System.out.println("ID.\tNo.\tMax Occup.\tMax RV length\tAccessible\tUtilities");
		if (inputSite.size() > 0) {
			for (Site sites : inputSite) {
				result = sites.getSiteId() + "\t" + sites.getSiteNumber() + "\t" + sites.getMaxOccupancy() + "\t\t"
						+ sites.getMaxRvLength() + "\t\t" + sites.isItAccessible() + "\t\t" + sites.isUtilities();
				System.out.println(result);
			}
		} else {
			System.out.println("listAllSites Error");
		}
		return result;
	}

	private void allParksSetter() {
		this.allParks = new String[jdbcParkDAO.getNameByParkId().size() + 1];
		jdbcParkDAO.getNameByParkId().toArray(allParks);
		allParks[jdbcParkDAO.getNameByParkId().size()] = "Exit";
		this.MAIN_MENU_OPTIONS = allParks;
	}
	
	private void listParkInfo(Park parks) {
		System.out.println("\n" + parks.getParkName() + "\nLocation:\t" + parks.getParkLocation() + "\nEST.\t\t"
				+ printYearMonthName(parks.getEstablishedYear()) + "\nAREA:\t\t" + printCommaFormat(parks.getArea())
				+ " sq km \n" + "Visitors/yr:\t" + printCommaFormat(parks.getAnnualVisitors()) + "\n\nDESCRIPTION:\n"
				+ parks.getDescription());
	}
	
	private void handleGetAllParksByName(String parkName) {
		System.out.println();
		System.out.println("--Park Information--");
		Park allParks = parkDAO.getParkName(parkName);
		listParkInfo(allParks);
	}

	private void handleGetAllCamps() {
		List<Campground> allCampgrounds = campgroundDAO.getAllCampgroundsByParkId(parkSelected);
		listAllCamps(allCampgrounds);
	}

	private void listAllCamps(List<Campground> Campgrounds) {
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
		sitesNarrowed.removeAll(sitesNarrowed);
	}

	private void exile() {
		System.out.println("Happy Camping!");
		System.exit(0);
	}

}
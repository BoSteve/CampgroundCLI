package com.techelevator.CLI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

	public CampgroundCLI(DataSource datasource) {
		this.menu = new Menu(System.in, System.out);
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		reservationDAO = new JDBCReseravtionDAO(datasource);
		campgroundDAO = new JDBCCampgroundDAO(datasource);
		parkDAO = new JDBCParkDAO(datasource);
		siteDAO = new JDBCSiteDAO(datasource);
		// sets up the parks in main menu
		setMainMenuParks();
	}

	public static void main(String[] args) {
		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.mainMenu();
	}

	private static String[] MAIN_MENU_OPTIONS;
	private static final String RETURN = "Go back";
	private static final String EXIT = "Exit";
	private static final String PARK_INFO_VIEW_CAMPS = "View Campgrounds";
	private static final String[] PARK_INFO_OPTIONS = { PARK_INFO_VIEW_CAMPS, RETURN, EXIT };
	private static final String RESERVE_AGAIN = "Reserve Another Site";
	private static final String[] RESERVATION_COMPLETED_OPTIONS = { RESERVE_AGAIN, EXIT };

	// records user's park choice from array of parks
	private int parkSelected = 0;
	private String[] allParks;

	// 1
	private void mainMenu() {
		happyCamper();
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
			displayParkCampsReservationMenu();
		} else if (choice.equals(RETURN)) {
			mainMenu();
		} else if (choice.equals(EXIT)) {
			exile();
		}
	}

	// 3
	Scanner scan = new Scanner(System.in);
	// camp is the name given campgroundSelect
	private String camp;
	private String campgroundSelect;
	private String arrivalSelect;
	private String departureSelect;
	private String siteReserved;
	private String nameOfReservation;

	private void displayParkCampsReservationMenu() {
		handlePrintAllCamps();
		
		inputCampground();
		inputArrivalDate();
		inputDepartureDate();
		
		inverseDateCheck(departureSelect, arrivalSelect);
		printAllSitesGivenDate(camp, arrivalSelect, departureSelect);
		validateSites(topFive);

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

	//	NOTE: In hindsight, we should have separated DAO methods from functional methods

	//

	//

	@SuppressWarnings("static-access")
	private void setMainMenuParks() {
		this.allParks = new String[jdbcParkDAO.getNameByParkId().size() + 1];
		jdbcParkDAO.getNameByParkId().toArray(allParks);
		allParks[jdbcParkDAO.getNameByParkId().size()] = "Exit";
		this.MAIN_MENU_OPTIONS = allParks;
	}

// !!!Park Methods

	public void printParkInfo(Park parks) {
		System.out.println("\n" + parks.getParkName() + "\nLocation:\t" + parks.getParkLocation() + "\nEST.\t\t"
				+ printYearMonthName(parks.getEstablishedYear()) + "\nAREA:\t\t" + printCommaFormat(parks.getArea())
				+ " sq km \n" + "Visitors/yr:\t" + printCommaFormat(parks.getAnnualVisitors()) + "\n\nDESCRIPTION:\n"
				+ paragrapher(parks.getDescription()));
	}

	public void handlePrintAllParksByName(String parkName) {
		System.out.println();
		System.out.println("--Park Information--");
		Park parkByName = parkDAO.getParkByName(parkName);
		printParkInfo(parkByName);
	}

	public String displayCurrentParkCamp(String campgroundSelect) {
		String camp = campgroundDAO.getAllCampgroundsByParkId(parkSelected).get(Integer.parseInt(campgroundSelect) - 1)
				.getNameOfCampground();
		System.out.println("\n--Information Selected--\nPark: " + allParks[parkSelected - 1]);
		System.out.println("Camp: " + camp);
		return camp;
	}

// !!!Camp Methods

	public void inputCampground() {
		System.out.print("\nRent campground by ID >>> ");
		try {
			campgroundSelect = scan.nextLine();
			camp = displayCurrentParkCamp(campgroundSelect);
		} catch (Exception e) {
			System.out.println("Invalid campsite number");
			inputCampground();
		}
	}

	private void handlePrintAllCamps() {
		List<Campground> allCampgrounds = campgroundDAO.getAllCampgroundsByParkId(parkSelected);
		printAllCamps(allCampgrounds);
	}

	private void printAllCamps(List<Campground> Campgrounds) {
		int counter = 0;
		System.out.println("\n--Select Campground--\n\nPark: " + allParks[parkSelected - 1]);
		System.out.println("ID\tCost\t\tOpen Months\tName");
		if (Campgrounds.size() > 0) {
			for (Campground Campground : Campgrounds) {
				System.out.println(++counter + "\t$" + Campground.getDailyFee() + "/Day\t"
						+ printMonthName(Campground.getOpenMonth()) + " - " + printMonthName(Campground.getCloseMonth())
						+ "\t" + Campground.getNameOfCampground());
			}
		} else {
			System.out.println("\n*** No Campgrounds Found ***");
		}
	}

// !!!Sites Methods

	private Map<Long, Integer> siteMapIDxPopularity = new HashMap<Long, Integer>();

	private List<Long> siteIDsByDate = new ArrayList<Long>();
	private List<Long> siteIDByDateAndPopularity = new ArrayList<Long>();

	private List<Site> sitesFromDateId = new ArrayList<Site>();
	private List<Site> sitesByDateAndPopularity = new ArrayList<Site>();
	private List<Site> topFive = new ArrayList<Site>();

	private void printAllSitesGivenDate(String camp, String arrivalSelect, String departureSelect) {

		sitesFromDateId = siteDAO.sitesByDate(stringDateToLocalDate(arrivalSelect), stringDateToLocalDate(departureSelect),
				campgroundDAO.getCampgroundIdByName(camp));
		Long id;
		for (Site holder : sitesFromDateId) {
			id = holder.getSiteId();
			siteIDsByDate.add(id);
		}

		System.out.println("\n--Sites available between--\n" + arrivalSelect + " - " + departureSelect + "\n");
		// sorting a map
		siteMapIDxPopularity = sortByValue(siteDAO.sortSitesByReservations(siteIDsByDate));
		// @TE please explain Entry and entrySet
		for (Entry<Long, Integer> en : siteMapIDxPopularity.entrySet()) {
			siteIDByDateAndPopularity.add(en.getKey());
		}

		// takes ID, returns site to be printed
		sitesByDateAndPopularity = siteDAO.getSitesById(siteIDByDateAndPopularity);

		// hardcoded way of always displaying 5 sites.
		if (sitesByDateAndPopularity.size() == 0) {
			printSiteInfo(sitesFromDateId, 0, 4);
		} else if (sitesByDateAndPopularity.size() > 0 && sitesByDateAndPopularity.size() < 5) {
			printSiteInfoRent(sitesByDateAndPopularity);
			printSiteInfo(sitesFromDateId, 1, (5 - sitesByDateAndPopularity.size()));
		} else {
			printSiteInfoRent(sitesByDateAndPopularity);
		}
	}

	public boolean validateSites(List<Site> siteList) {
		List<String> top5IDs = new ArrayList<String>();
		try {
			for (int i = 0; i < 5; i++) {
				top5IDs.add(siteList.get(i).getSiteId().toString());
			}
		} catch (Exception e) {
			if (top5IDs.size() == 0) {
				System.out.println("\n--No sites were available!--");
				displayParkCampsReservationMenu();
			}
		}

		System.out.println("\nSelect site to reserve by Site ID >>> ");
		siteReserved = scan.nextLine();

		if (top5IDs.contains(siteReserved)) {
			System.out.println("Site " + siteReserved + " is available!");
			nameOfReservation = makeReservation();
			return true;
		} else {
			System.out.println("Site is not available");
			validateSites(siteList);
			return false;
		}
	}

	private void printSiteInfoRent(List<Site> inputSite) {
		String result = "";
		try {
			System.out.println("--Top 5 Popular campsites!--");
			System.out.println("ID.\tRentals\t\tMax Occup.\tMax RV length\tAccessible\tUtilities");
			for (int i = 0; i < 5; i++) {
				Site sites = inputSite.get(i);
				topFive.add(sites);
				int rese = siteMapIDxPopularity.get(inputSite.get(i).getSiteId());
				result = sites.getSiteId() + "\t" + rese + "\t\t" + sites.getMaxOccupancy() + "\t\t"
						+ sites.getMaxRvLength() + "\t\t" + sites.isItAccessible() + "\t\t" + sites.isUtilities();
				System.out.println(result);
			}
		} catch (Exception e) {
		}
	}

	// when reservation count does not exist
	private void printSiteInfo(List<Site> inputSite, int start, int cap) {
		String result = "";
		try {
			System.out.println("--Road Less Travelled Campsites--");
			System.out.println("ID.\tRentals\t\tMax Occup.\tMax RV length\tAccessible\tUtilities");
			for (int i = start; i <= cap; i++) {
				Site sites = inputSite.get(i);
				topFive.add(sites);
				result = sites.getSiteId() + "\t?\t\t" + sites.getMaxOccupancy() + "\t\t" + sites.getMaxRvLength()
						+ "\t\t" + sites.isItAccessible() + "\t\t" + sites.isUtilities();
				System.out.println(result);
			}
		} catch (Exception e) {
		}
	}

// !!!Reservation Methods

	private String makeReservation() {
		System.out.println("Name for reservation >>> ");
		String nameOfReservation = scan.nextLine();
		System.out.println("Reservation for: " + nameOfReservation);
		try {
			reservationDAO.createReservation(Long.parseLong(siteReserved), stringDateToLocalDate(arrivalSelect),
					stringDateToLocalDate(departureSelect), nameOfReservation);
		} catch (Exception e) {
			System.out.println("Reservation failed");
		}
		return nameOfReservation;
	}

	private void confirmReservation(String nameOfReservation) {
		LocalDate departTest = stringDateToLocalDate(departureSelect);
		LocalDate arriveTest = stringDateToLocalDate(arrivalSelect);
		int totalDays = (int) ChronoUnit.DAYS.between(arriveTest, departTest);

		BigDecimal bDTotalDays = new BigDecimal(totalDays);
		System.out.println("\n--CONFIRMATION--");
		System.out.println("Reservation ID: " + reservationDAO.getReservationId(Long.parseLong(siteReserved),
				stringDateToLocalDate(arrivalSelect), stringDateToLocalDate(departureSelect), nameOfReservation));
		System.out.println("Site " + siteReserved + " reserved for " + nameOfReservation);
		System.out.println("From: " + arrivalSelect + " Until: " + departureSelect + "\n");
		System.out.println("Days booked: " + bDTotalDays.toString());
		System.out.println("Cost/Day: $" + String.format("%,.2f",
				campgroundDAO.getCampgroundCostByName(camp).setScale(2, RoundingMode.HALF_UP)));
		System.out.println("Subtotal: $"
				+ String.format("%,.2f", campgroundDAO.getCampgroundCostByName(camp).multiply(bDTotalDays)));
	}

	private void inputArrivalDate() {
		try {
			System.out.print("\nEnter Arrival date (YYYY/MM/DD) >>> ");
			arrivalSelect = scan.nextLine();
			System.out.println("Selected Arrival: " + printYearMonthName(stringDateToLocalDate(arrivalSelect)));
		} catch (Exception e) {
			System.out.println("Invalid Date Entered");
			inputArrivalDate();
		}
	}

	private void inputDepartureDate() {
		try {
			System.out.print("\nEnter Departure date (YYYY/MM/DD) >>> ");
			departureSelect = scan.nextLine();
			System.out.println("Selected Departure: " + printYearMonthName(stringDateToLocalDate(departureSelect)));
		} catch (Exception e) {
			System.out.println("Invalid Date");
			inputDepartureDate();
		}
	}

	public boolean inverseDateCheck(String dep, String arr) {
		LocalDate departTest = stringDateToLocalDate(dep);
		LocalDate arriveTest = stringDateToLocalDate(arr);
		int lengthOfStay = (int) ChronoUnit.DAYS.between(arriveTest, departTest);
		if (lengthOfStay <= 0) {
			System.out.println("\n--Please put dates in order--");
			inputArrivalDate();
			inputDepartureDate();
			inverseDateCheck(dep, arr);
			return false;
		}
		return true;
	}

// Format Methods

	public Map<Long, Integer> sortByValue(Map<Long, Integer> hm) {
		List<Map.Entry<Long, Integer>> list = new LinkedList<Map.Entry<Long, Integer>>(hm.entrySet());

		// Sort the list
		// @TE know how to use but not what it does
		Collections.sort(list, new Comparator<Map.Entry<Long, Integer>>() {
			public int compare(Map.Entry<Long, Integer> o1, Map.Entry<Long, Integer> o2) {
				// order is determined here
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to map
		HashMap<Long, Integer> temp = new LinkedHashMap<Long, Integer>();
		for (Map.Entry<Long, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public String paragrapher(String longText) {
		StringBuilder result = new StringBuilder();
		String[] arr = longText.split("\\ ");
		int counter = 0;
		for (String holder : arr) {
			counter++;
			if (counter % 9 == 0) {
				result.append(holder + "\n");
			} else {
				result.append(holder + " ");
			}
		}
		return result.toString();
	}

	@SuppressWarnings("serial")
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

	public String printMonthName(String monthnum) {
		String result = monthMap.get(monthnum);
		return result;
	}

	public String printCommaFormat(Long lng) {
		DecimalFormat df = new DecimalFormat("#,###");
		String result = df.format(lng);
		return result;
	}

	public String printYearMonthName(LocalDate date) {
		String[] splitted = date.toString().split("-");
		String year = splitted[0];
		String month = splitted[1];
		String day = splitted[2];
		String result = monthMap.get(month) + " " + day + " " + year;
		return result;
	}

	public LocalDate stringDateToLocalDate(String userInput) {
		String[] arr = userInput.split("\\/|//-");
		int year = Integer.parseInt(arr[0]);
		int month = Integer.parseInt(arr[1]);
		int day = Integer.parseInt(arr[2]);

		LocalDate result = LocalDate.of(year, month, day);

		return result;
	}

	public void wipeSitesNarrowed() {
		siteIDsByDate.removeAll(siteIDsByDate);
	}

	public void happyCamper() {
		System.out.println("   _____                      _             \r\n"
				+ "  / ____|                    (_)            \r\n"
				+ " | |     __ _ _ __ ___  _ __  _ _ __   __ _ \r\n"
				+ " | |    / _` | '_ ` _ \\| '_ \\| | '_ \\ / _` |\r\n"
				+ " | |___| (_| | | | | | | |_) | | | | | (_| |\r\n"
				+ "  \\_____\\__,_|_| |_| |_| .__/|_|_| |_|\\__, |\r\n"
				+ "                       | |             __/ |\r\n" + "                       |_|            |___/ ");

		System.out.println("        ______\r\n" + "       /     /\\\r\n" + "      /     /  \\\r\n"
				+ "     /_____/----\\_    (  \r\n" + "    \"     \"          ).  \r\n"
				+ "   _ ___          o (:') o   \r\n" + "  (@))_))        o ~/~~\\~ o   \r\n"
				+ "                  o  o  o");
		System.out.println("\n--SELECT A PARK--");
	}

	private void exile() {
		System.out.println("\n\tHappy Camping!\n");
		System.out.println("           (                 ,&&&.\r\n" + "            )                .,.&&\r\n"
				+ "           (  (              \\=__/\r\n" + "               )             ,'-'.\r\n"
				+ "         (    (  ,,      _.__|/ /|\r\n" + "         ) /-((-||------((_|___/ |\r\n"
				+ "        (  // | (`'      ((  `'--|\r\n" + "      _ -.;_/ \\\\--._      \\\\ \\-._/.\r\n"
				+ "     (_;-// | \\ \\-'.\\    <_,\\_\\`--'|\r\n" + "     ( `.__ _  ___,')      <_,-'__,'\r\n"
				+ "      `'(_ )_)(_)_)'");
		System.exit(0);
	}

}
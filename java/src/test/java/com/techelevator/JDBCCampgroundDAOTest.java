package com.techelevator;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.parks.model.Campground;
import com.techelevator.parks.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.parks.model.jdbc.JDBCParkDAO;
import com.techelevator.parks.model.Park;


public class JDBCCampgroundDAOTest {
	
	private static SingleConnectionDataSource dataSource;
//	private static final long Campground_TEST_ID = 500;

	private JDBCCampgroundDAO dao;
	private JDBCParkDAO parkDao;
	private long parkId;
	private static final long Park_id = 923;
	private static final String Camp_name = "Campville";

	/* Before any tests are run, this method initializes the datasource for testing. */
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/* The following line disables autocommit for connections
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
		dataSource.setAutoCommit(false);
	}

	/* After all tests have finished running, this method will close the DataSource */
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}
	
	@Before
	public void setup() {
		System.out.println("Starting test");
		String sqlInsertCampground = "INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) VALUES (1, 'Camptown', '08', '10', 20.00)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertCampground);
		//lines above add new campground

		String sqlInsertPark = "INSERT INTO park (name, location, establish_date, area, visitors, description) VALUES ('Parky Park Park','Washington' , '1976-07-04', '60000', '10000000', 'Best park in the world, thats all that needs to be said')";
		jdbcTemplate.update(sqlInsertPark);
		parkId = jdbcTemplate.queryForObject("INSERT INTO park (name, location, establish_date, area, visitors, description) VALUES ('Parkyville','New York' , '1926-12-04', '40000', '124925241', 'Worst park in the world, thats all that needs to be said') RETURNING park_id",Long.class);
		dao = new JDBCCampgroundDAO(dataSource);
		parkDao = new JDBCParkDAO(dataSource);
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	
	@Test
	public void test_to_get_all_campgrounds() {
		Campground testCampground = new Campground();
		
		List<Campground> campgroundList = dao.getAllCampgrounds();
		campgroundList.add(testCampground);
		boolean actualResult = campgroundList.contains(testCampground);
		
		assertEquals(true, actualResult);
		
	}

	
	@Test
	public void test_get_all_camps_by_park_id() {
		Campground newCamp = new Campground();
		newCamp.setId(Park_id);
		newCamp.setNameOfCampground(Camp_name);
		
List<Campground> campgroundList = dao.getAllCampgroundsByParkId((int) Park_id);
campgroundList.add(newCamp);
Campground newTestCamp = campgroundList.get(0);

	assertEquals("Campville", newTestCamp.getNameOfCampground());
		
	}
	@Test
	public void get_camp_id_by_name() {
		
	
	Campground newCamp = new Campground();
		newCamp.setId((long) 890);
		newCamp.setNameOfCampground(Camp_name);
		List<Campground> campingList = dao.getAllCampgrounds();
		campingList.add(newCamp);
				
//		assertEquals(newCamp.getNameOfCampground(), "Campville");
		

	}
	
	@Test
	public void get_campground_by_name() {
		Campground myTestCamp = new Campground();
		myTestCamp.setNameOfCampground(Camp_name);
		
		assertEquals("Campville", myTestCamp.getNameOfCampground());
		
		
	


	
	}
	@Test
	public void _get_campground_cost_by_name() {
		
		assertEquals(true, false);
		
	}
	
	
	/* This method provides access to the DataSource for subclasses so that
	 * they can instantiate a DAO for testing */
	protected DataSource getDataSource() {
		return dataSource;
	}
}
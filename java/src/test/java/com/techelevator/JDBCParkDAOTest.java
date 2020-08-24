package com.techelevator;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.parks.model.Campground;
import com.techelevator.parks.model.Park;
import com.techelevator.parks.model.jdbc.JDBCParkDAO;

public class JDBCParkDAOTest {
	private static SingleConnectionDataSource dataSource;

	private JDBCParkDAO dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/*
		 * The following line disables autocommit for connections returned by this
		 * DataSource. This allows us to rollback any changes after each test
		 */
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("Starting test");
		String sqlInsertCampground = "INSERT INTO park (park_id, name, location, establish_date, area, visitors, description) VALUES (8, 'Parky Park Park','Washington' , '1976-07-04', '60000', '10000000', 'Best park in the world, thats all that needs to be said') ";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertCampground);
		dao = new JDBCParkDAO(dataSource);
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();

	}

	@Test
	public void get_all_parks() {
		Park testPark = new Park();

		List<Park> parkList = dao.getAllParks();
		parkList.add(testPark);
		boolean actualResult = parkList.contains(testPark);

		assertEquals(true, actualResult);
	}

	@Test
	public void get_park_id() {
//JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Park testIdPark = new Park();

		testIdPark.setId((long) 100);

		Park idPark = dao.getParkId(testIdPark.getId());
		List<Park> testDeptIdList = dao.getAllParks();
		testDeptIdList.add(idPark);
		boolean actualResult = testDeptIdList.contains(idPark);

		assertEquals(true, actualResult);
	}

	
	@Test 
	public void get_park__by_name() {
		String testParkName = "Parktucky";
		
		Park testPark = new Park();
		
		testPark.setParkName(testParkName);
		
		List<Park> parkList = dao.getAllParks();
		parkList.add(testPark);
		boolean actualResult = parkList.contains(testPark);
		
		assertEquals(true, actualResult);
		
	}
	
	
	@Test
	public void park_name_by_id() {
		String parkTest = "Parkville";
		Park testIdPark = new Park();
		
		testIdPark.setId((long) 100);
		testIdPark.setParkName(parkTest);
		
		List<Park> parkIdList = dao.getAllParks();
		
		parkIdList.add(testIdPark);
		Long expectedResult = (long) 100;
		String expectedResult2 = parkTest;
		assertEquals(expectedResult, parkIdList.get(4).getId());
		assertEquals(expectedResult2, parkIdList.get(4).getParkName());

	}

}

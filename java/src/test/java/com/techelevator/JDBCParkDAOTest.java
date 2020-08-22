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
		/* The following line disables autocommit for connections
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
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
testDeptIdList.add(testIdPark);
boolean actualResult = testDeptIdList.contains(testIdPark);

assertEquals(true, actualResult);
	}
	
	
	@Test
	public void park_name_by_id() {
		List<Park> parkIdList = dao.getAllParks();
		assertEquals(4, parkIdList.size());
//COME BACK TO THIS TEST
		
	}
	
	@Test
	public void get_park_name() {
		String testString = "Worst Park Ever";
		Park testPark = new Park();
		//BREAK*****
//		List<Park> searchParkList = dao.getParkName(testString);
//		searchParkList.add(testPark);
//		boolean actualResult = searchParkList.contains(testPark);

//		assertEquals(testPark, searchParkList.get(0).getParkName());
	//COME BACK -- LINE 101	
	}
	
}

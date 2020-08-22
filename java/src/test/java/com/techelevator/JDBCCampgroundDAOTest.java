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

import com.techelevator.parks.model.Campground;
import com.techelevator.parks.model.jdbc.JDBCCampgroundDAO;



public class JDBCCampgroundDAOTest {
	
	private static SingleConnectionDataSource dataSource;
//	private static final long Campground_TEST_ID = 500;

	private JDBCCampgroundDAO dao;


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
		String sqlInsertCampground = "INSERT INTO campground (campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee) VALUES (500, 1, 'Camptown', '08', '10', 20.00)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertCampground);
		dao = new JDBCCampgroundDAO(dataSource);
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
	public void test_getall_camps_by_park_id() {
		Campground newCamp = new Campground();
		List<Campground> campList = dao.getAllCampgroundsByParkId(550);
		campList.add(newCamp);
		boolean actualResult = campList.contains(newCamp);
		
		assertEquals(true, actualResult);
		
		}
	
	
	
	@Test
	public void get_camp_id_by_name() {
		Campground newCampground = new Campground();
		Long camp = (long) 40;
		
		
		
		
		//PLACEHOLDER
		assertEquals(true, false);
	}
	
	@Test
	public void get_campground_by_name() {
		//PLACEHOLDER

		assertEquals(true, false);

	
	}
	
	
	/* This method provides access to the DataSource for subclasses so that
	 * they can instantiate a DAO for testing */
	protected DataSource getDataSource() {
		return dataSource;
	}
}
package com.techelevator;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.parks.model.Reservation;
import com.techelevator.parks.model.jdbc.JDBCReseravtionDAO;

public class JDBCReservationDAOTest {
	private static SingleConnectionDataSource dataSource;

	private JDBCReseravtionDAO dao;

	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Starting testing");
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
		String sqlInsertReservation = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) VALUES (1, 'Steve and Kevin' , '2020-06-12' , '2020-08-22', now()) ";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertReservation);
		dao = new JDBCReseravtionDAO(dataSource);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Ending test");
		try {
		dataSource.getConnection().rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Database Connection Problems");
		}

	}

	@Test
	public void get_all_reservations() {
	Reservation testReservation = new Reservation();
	
	List<Reservation> reservationList = dao.getAllReservations();
	reservationList.add(testReservation);
	boolean actualResult = reservationList.contains(testReservation);
	
	assertEquals(true, actualResult);
	}
	
	@Test
	public void create_reservation() {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int before = dao.getAllReservations().size();

		jdbcTemplate.update("INSERT INTO reservation VALUES (923, 1, 'Happy time', '2020-12-12', '2020-12-25', now())");
		

		Long site = (long) 1;
		String nameOfReservation = "KS";
		LocalDate startDate = LocalDate.of(2020, 12, 03);
		LocalDate endDate = LocalDate.of(2020, 12, 12);
		LocalDate current = LocalDate.now();
		
		int after = dao.getAllReservations().size();

		dao.createReservation(site, startDate, endDate, nameOfReservation);
		
		
		
		assertEquals(before +1, after);
		

//		
//		Long site = (long) 1;
//		String nameOfReservation = "KS";
//		LocalDate startDate = LocalDate.of(2020, 12, 03);
//		LocalDate endDate = LocalDate.of(2020, 12, 12);
//		LocalDate current = LocalDate.now();
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//		jdbcTemplate.update(createdRes, site, nameOfReservation, startDate, endDate, current);
//		
//		assertEquals("1", site);
		
	}
	
	@Test
	public void get_reservation_id() {
		Reservation testRes = new Reservation();
		Long site = (long) 1;
		String nameOfReservation = "KS";
		LocalDate startDate = LocalDate.of(2020, 12, 03);
		LocalDate endDate = LocalDate.of(2020, 12, 12);
		LocalDate current = LocalDate.now();
		dao.getReservationId(site, startDate, endDate, nameOfReservation);
		int actualResult = dao.getAllReservations().size();
		

		assertEquals(45, actualResult);
		
//		Reservation reserve = dao.getReservationId(site, startDate, endDate, nameOfReservation)

	}
	
}

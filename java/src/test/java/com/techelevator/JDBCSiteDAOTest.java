package com.techelevator;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.parks.model.Site;
import com.techelevator.parks.model.jdbc.JDBCSiteDAO;


public class JDBCSiteDAOTest {

		private static SingleConnectionDataSource dataSource;

		private JDBCSiteDAO dao;

		

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
			String sqlInsertSite = "INSERT INTO site (site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length) VALUES (700, 3, 40, 8 , true , 20) ";
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sqlInsertSite);
			dao = new JDBCSiteDAO(dataSource);
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
			public void sites_by_date() {
			
			Site newSite = new Site();
			
			assertEquals(true, false);
			
			}
		
			@Test
			public void sorted_sites_by_reservations() {
				
				assertEquals(true, false);
				
			}
				
			@Test
			public void get_sites_by_id() {
			
				assertEquals(true, false);
}
}


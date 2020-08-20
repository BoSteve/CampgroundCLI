package com.techelevator.parks.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.parks.model.Site;
import com.techelevator.parks.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {
	
private JdbcTemplate jdbcTemplate;
	public JDBCSiteDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public List<Site> getAvailableSites(long Id, LocalDate startDate, LocalDate endDate) {
	ArrayList<Site> availableSites = new ArrayList<Site>();
	String sqlSiteString = "SELECT * FROM site s JOIN campground c ON s.campground_id = c.campground_id GROUP BY )";
//		String 
		
		
		return null;
	}

}

package com.techelevator.parks.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.parks.model.Site;
import com.techelevator.parks.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {
	
private JdbcTemplate jdbcTemplate;
	public JDBCSiteDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	public List<Site> getSiteInfoByCampName(String campName) {
		List<Site> result = new ArrayList<Site>();
		String sql = "SELECT campground.name, site_number, max_occupancy, accessible, max_rv_length, utilities from site\r\n" + 
				"JOIN campground ON campground.campground_id = site.campground_id\r\n" + 
				"WHERE campground.name = ? ";
		SqlRowSet sqlrowset = jdbcTemplate.queryForRowSet(sql, campName);
		while(sqlrowset.next()) {
			
		}
		
		return result;
	}

}

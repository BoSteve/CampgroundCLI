package com.techelevator.parks.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.techelevator.parks.model.Site;
import com.techelevator.parks.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {
	
private JdbcTemplate jdbcTemplate;
private NamedParameterJdbcTemplate jdbcSpecial;

	public JDBCSiteDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcSpecial = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public List<Site> dateToSet(LocalDate arrival, LocalDate departure, Long id) {
		List<Site> results = new ArrayList<Site>();
		
		Set <LocalDate> dates = new HashSet<LocalDate>();
		dates.add(arrival);
		dates.add(departure);
		
		Set <Long> ids = new HashSet<Long>();
		ids.add(id);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("dates", dates);
		parameters.addValue("ids", ids);
		
		String sql = "SELECT * FROM site where campground_id = ( :ids ) AND site_id " +
		"NOT IN (SELECT site_id FROM reservation WHERE (from_date, to_date) OVERLAPS ( :dates ))"
		+ "LIMIT 10";
		
		SqlRowSet rowset = jdbcSpecial.queryForRowSet(sql, parameters);
		
		while (rowset.next()) {
			Site newSite = rowFromSite(rowset);
			results.add(newSite);
		}
		return results;
	}
	
	
	@Override
	public List<Site> getSiteInfoByCampNameEmpty(String campName) {
		List<Site> result = new ArrayList<Site>();
		String sql = "Select  " + 
				"site_id, " + 
				"site.site_number, " + 
				"max_occupancy, " + 
				"accessible, " + 
				"max_rv_length, " + 
				"utilities " + 
				"from site " + 
				"JOIN campground ON campground.campground_id = site.campground_id " + 
				"WHERE campground.name = ? " + 
				"GROUP BY site_id " +
				"LIMIT 5";
		SqlRowSet sqlrowset = jdbcTemplate.queryForRowSet(sql, campName);
		while(sqlrowset.next()) {	
			Site holder = rowFromSite(sqlrowset);
			result.add(holder);
		}
		return result;
	}
	
	private Site rowFromSite(SqlRowSet sqlPark) {
		Site newSite = new Site();
		newSite.setSiteId(sqlPark.getLong("site_id"));
		newSite.setSiteNumber(sqlPark.getInt("site_number"));
		newSite.setMaxOccupancy(sqlPark.getInt("max_occupancy"));
		newSite.setItAccessible(sqlPark.getBoolean("accessible"));
		newSite.setMaxRvLength(sqlPark.getInt("max_rv_length"));
		newSite.setUtilities(sqlPark.getBoolean("utilities"));

		return newSite;
	}
	
	public List<Site> getSiteInfoByCampName(String campName) {
		List<Site> result = new ArrayList<Site>();
		String sql = "SELECT site.site_id, " + 
				"site.campground_id, " +
				"site.max_occupancy, " + 
				"site.site_number, " + 
				"site.accessible, " + 
				"site.max_rv_length, " + 
				"site.utilities, " + 
				"COUNT(reservation_id) as reservation_count FROM reservation  " + 
				"JOIN site ON reservation.site_id = site.site_id " + 
				"JOIN campground ON campground.campground_id = site.campground_id " + 
				"WHERE campground.name = ? " + 
				"GROUP BY reservation.site_id, site.site_id, campground.name " + 
				"ORDER BY reservation_count desc, site_id asc " + 
				"LIMIT 5";
		SqlRowSet sqlrowset = jdbcTemplate.queryForRowSet(sql, campName);
		while(sqlrowset.next()) {	
			Site holder = rowFromSiteSpecial(sqlrowset);
			result.add(holder);
		}
		return result;
	}
	
	private Site rowFromSiteSpecial(SqlRowSet sqlPark) {
		Site newSite = new Site();
		newSite.setSiteId(sqlPark.getLong("site_id"));
		newSite.setSiteNumber(sqlPark.getInt("site_number"));
		newSite.setCampgroundId(sqlPark.getInt("campground_id"));
		newSite.setMaxOccupancy(sqlPark.getInt("max_occupancy"));
		newSite.setItAccessible(sqlPark.getBoolean("accessible"));
		newSite.setMaxRvLength(sqlPark.getInt("max_rv_length"));
		newSite.setUtilities(sqlPark.getBoolean("utilities"));
		return newSite;
	}

}

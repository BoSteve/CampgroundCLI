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
	public List<Site> sitesByDate(LocalDate arrival, LocalDate departure, Long id) {
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
		"NOT IN (SELECT site_id FROM reservation WHERE (from_date, to_date) OVERLAPS ( :dates ))";
		
		SqlRowSet rowset = jdbcSpecial.queryForRowSet(sql, parameters);
		
		while (rowset.next()) {
			Site newSite = rowFromSite(rowset);
			results.add(newSite);
		}
		return results;
	}
	
	@Override
	public List<Site> sortSitesByReservations (List <Long> input) {
		List<Site> results = new ArrayList <Site>();
		
		Long upper = input.get(input.size()-1);
		Long lower = input.get(0);
		
		String sql = 
				"SELECT " + 
				"site.site_id, " + 
				"site.site_number, " + 
				"max_occupancy, " + 
				"accessible, " + 
				"max_rv_length, " + 
				"utilities,  " + 
				"COUNT(reservation_id) as reservation_count from site " + 
				"JOIN reservation ON reservation.site_id = site.site_id " + 
				"where site.site_id >= ? AND site.site_id <= ? " +  
				"GROUP BY site.site_id " + 
				"ORDER BY reservation_count desc " + 
				"limit 5";
		SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, lower, upper);
		while (rowset.next()) {
			Site newSite = rowFromSite(rowset);
			results.add(newSite);
		}
		return results;
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
}

package com.techelevator.parks.model.jdbc;

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

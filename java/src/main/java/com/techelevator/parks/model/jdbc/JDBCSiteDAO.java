package com.techelevator.parks.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.parks.model.Park;
import com.techelevator.parks.model.Site;
import com.techelevator.parks.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {
	
private JdbcTemplate jdbcTemplate;
	public JDBCSiteDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	public List<Site> getSiteInfoByCampName(String campName) {
		List<Site> result = new ArrayList<Site>();
		String sql = "SELECT site.site_id, COUNT(reservation_id) as reservation_count FROM reservation  " + 
				"JOIN site ON reservation.site_id = site.site_id " + 
				"JOIN campground ON campground.campground_id = site.campground_id " + 
				"WHERE campground.name = 'Blackwoods'" + 
				"GROUP BY reservation.site_id, site.site_id, campground.name " + 
				"ORDER BY reservation_count desc, site_id asc " + 
				"LIMIT 5";
		SqlRowSet sqlrowset = jdbcTemplate.queryForRowSet(sql);
		while(sqlrowset.next()) {	
			Site holder = rowFromSiteSpecial(sqlrowset);
			result.add(holder);
		}
		return result;
	}
	
	private Site rowFromSiteSpecial(SqlRowSet sqlPark) {
		Site newSite = new Site();
		newSite.setSiteId(sqlPark.getLong("site_id"));
		return newSite;
	}
}

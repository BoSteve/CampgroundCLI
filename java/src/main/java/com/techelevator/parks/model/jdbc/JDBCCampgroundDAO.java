package com.techelevator.parks.model.jdbc;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.techelevator.parks.model.Campground;
import com.techelevator.parks.model.CampgroundDAO;

public class JDBCCampgroundDAO implements CampgroundDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCCampgroundDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Campground> getAllCampgrounds() {
		ArrayList<Campground> campgroundList = new ArrayList<Campground>();
		String sqlGetCampground = "SELECT * FROM campground";
		SqlRowSet sqlCamp = jdbcTemplate.queryForRowSet(sqlGetCampground);
		while (sqlCamp.next()) {
			Campground newCamp = rowFromCampground(sqlCamp);
			campgroundList.add(newCamp);
		}
		return campgroundList;
	}

	@Override
	public List<Campground> getAllCampgroundsByParkId(int parkId) {
		List<Campground> campIdList = new ArrayList<Campground>();
		String sqlCampId = "SELECT * FROM campground WHERE park_id = ?";
		SqlRowSet sqlCampIdRow = jdbcTemplate.queryForRowSet(sqlCampId, parkId);
		while (sqlCampIdRow.next()) {
			Campground newCamp = rowFromCampground(sqlCampIdRow);
			campIdList.add(newCamp);
		}
		return campIdList;

	}

	private Campground rowFromCampground(SqlRowSet sqlCamp) {
		Campground newCampGround = new Campground();
		newCampGround.setId(sqlCamp.getLong("campground_id"));
		newCampGround.setNameOfCampground(sqlCamp.getString("name"));
		newCampGround.setOpenMonth(sqlCamp.getString("open_from_mm"));
		newCampGround.setCloseMonth(sqlCamp.getString("open_to_mm"));
		newCampGround.setDailyFee(sqlCamp.getBigDecimal("daily_fee"));

		return newCampGround;
	}
}

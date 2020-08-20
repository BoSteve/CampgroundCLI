package com.techelevator.parks.model.jdbc;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.parks.model.Campground;
import com.techelevator.parks.model.CampgroundDAO;

public class JDBCCampgroundDAO implements CampgroundDAO {

	private JdbcTemplate jdbcTemplate;

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
	public Campground getCampgroundById(Long id) {
		ArrayList<Campground> campIdList = new ArrayList<Campground>();
		String sqlCampId = "SELECT * FROM campground WHERE park_id = ?";
		SqlRowSet sqlCampIdRow = jdbcTemplate.queryForRowSet(sqlCampId, id);
		while (sqlCampIdRow.next()) {
			Campground newCamp = rowFromCampground(sqlCampIdRow);
			campIdList.add(newCamp);
		}
		return null;

	}

	private Campground rowFromCampground(SqlRowSet sqlCamp) {
		Campground newCampGround = new Campground();
		newCampGround.setId(sqlCamp.getLong("id"));
		newCampGround.setNameOfCampground(sqlCamp.getString("nameOfCampGround"));
		newCampGround.setOpenMonth(sqlCamp.getString("openMonth"));
		newCampGround.setCloseMonth(sqlCamp.getString("closeMonth"));
		newCampGround.setDailyFee(sqlCamp.getBigDecimal("dailyFee"));

		return newCampGround;
	}
}

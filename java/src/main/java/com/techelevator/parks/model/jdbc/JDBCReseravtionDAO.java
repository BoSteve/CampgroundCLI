package com.techelevator.parks.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.techelevator.parks.model.Reservation;
import com.techelevator.parks.model.ReservationDAO;

public class JDBCReseravtionDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCReseravtionDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Reservation> getAllReservations() {
		ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
		String sqlReservationString = "SELECT * FROM reservation";
		SqlRowSet sqlReservation = jdbcTemplate.queryForRowSet(sqlReservationString);
		while (sqlReservation.next()) {
			Reservation newReservation = rowFromReservation(sqlReservation);
			reservationList.add(newReservation);
		}
		return reservationList; 
	}

	@Override
	public void createReservation(Long site, LocalDate startDate, LocalDate endDate, String nameOfReservation) {
		String createdRes = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) "
				+ "VALUES (?, ?, ?, ?, ?)";
		LocalDate current = LocalDate.now();
		jdbcTemplate.update(createdRes, site, nameOfReservation, startDate, endDate, current);
	}
	
	@Override
	public String getReservationId (Long site, LocalDate startDate, LocalDate endDate, String nameOfReservation) {
		String result = null;
		String sql = "SELECT *  " + 
				"FROM reservation " + 
				"where  " + 
				"name = ? AND " + 
				"site_id = ? AND " + 
				"from_date = ? AND " + 
				"to_date = ? AND " + 
				"create_date = ? ";
		LocalDate current = LocalDate.now();
		SqlRowSet sqlrowset = jdbcTemplate.queryForRowSet(sql, nameOfReservation, site, startDate, endDate,current);
		while (sqlrowset.next()) {
			Reservation holder = rowFromReservation(sqlrowset);
			result = holder.getConfirmationId().toString();
		}
		return result;
	
<<<<<<< HEAD
	}
	
=======
>>>>>>> ba6095ab38e0c30a5fdb51bb073fa1c54f7c6d8f
	private Reservation rowFromReservation(SqlRowSet sqlReservation) {
		Reservation reservationRow = new Reservation();
		reservationRow.setNameOfReservation(sqlReservation.getString("name"));
		reservationRow.setStartDate(sqlReservation.getDate("from_date").toLocalDate());
		reservationRow.setEndDate(sqlReservation.getDate("to_date").toLocalDate());
		reservationRow.setConfirmationId(sqlReservation.getLong("reservation_id"));

		return reservationRow;
	}
}

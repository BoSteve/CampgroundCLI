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
	
	public JDBCReseravtionDAO (DataSource dataSource) {
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
		


	private Reservation rowFromReservation(SqlRowSet sqlReservation) {
		Reservation reservationRow = new Reservation();
		reservationRow.setNameOfReservation(sqlReservation.getString("name"));
		reservationRow.setStartDate(sqlReservation.getDate("from_date").toLocalDate());
		reservationRow.setEndDate(sqlReservation.getDate("to_date").toLocalDate());
		reservationRow.setConfirmationId(sqlReservation.getLong("reservation_id"));
		
		
		return reservationRow;
	}

	@Override
	public void createReservation(Long confirmationId, LocalDate startDate, LocalDate endDate,
			String nameOfReservation) {

		String createdRes = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(createdRes, confirmationId, startDate, endDate, nameOfReservation);
	}

	
	
}

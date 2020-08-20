import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
		reservationRow.setNameOfReservation(sqlReservation.getString("nameOfReservation"));
		reservationRow.setStartDate(sqlReservation.getDate("startDate").toLocalDate());
		reservationRow.setEndDate(sqlReservation.getDate("endDate").toLocalDate());
		reservationRow.setConfirmationId(sqlReservation.getLong("confirmationId"));
		
		
		return reservationRow;
	}

	@Override
	public void createReservation(Long confirmationId, LocalDate startDate, LocalDate endDate,
			String nameOfReservation) {

		String createdRes = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(createdRes, confirmationId, startDate, endDate, nameOfReservation);
	}

	
	
}

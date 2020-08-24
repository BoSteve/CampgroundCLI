package com.techelevator.parks.model;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {

	
	public List<Reservation> getAllReservations();
	
	public void createReservation(Long confirmationId, LocalDate startDate, LocalDate endDate, String nameOfReservation);

	String getReservationId(Long site, LocalDate startDate, LocalDate endDate, String nameOfReservation);
}

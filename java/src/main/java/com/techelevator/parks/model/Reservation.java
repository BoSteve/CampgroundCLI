package com.techelevator.parks.model;
import java.time.LocalDate;

public class Reservation {

	
	private String nameOfReservation;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long confirmationId;
	
	
	public String getNameOfReservation() {
		return nameOfReservation;
	}
	public void setNameOfReservation(String nameOfReservation) {
		this.nameOfReservation = nameOfReservation;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public Long getConfirmationId() {
		return confirmationId;
	}
	public void setConfirmationId(Long confirmationId) {
		this.confirmationId = confirmationId;
	}
	
}

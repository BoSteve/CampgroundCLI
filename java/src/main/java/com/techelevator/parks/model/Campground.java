package com.techelevator.parks.model;
import java.math.BigDecimal;

public class Campground {

	private Long parkId;
	private Long id;
	private String nameOfCampground;
	private String openMonth;
	private String closeMonth;
	private BigDecimal dailyFee;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNameOfCampground() {
		return nameOfCampground;
	}
	public void setNameOfCampground(String nameOfCampground) {
		this.nameOfCampground = nameOfCampground;
	}
	public String getOpenMonth() {
		return openMonth;
	}
	public void setOpenMonth(String openMonth) {
		this.openMonth = openMonth;
	}
	public String getCloseMonth() {
		return closeMonth;
	}
	public void setCloseMonth(String closeMonth) {
		this.closeMonth = closeMonth;
	}
	public BigDecimal getDailyFee() {
		return dailyFee;
	}
	public void setDailyFee(BigDecimal dailyFee) {
		this.dailyFee = dailyFee;
	}
	public Long getParkId() {
		return parkId;
	}
	public void setParkId(Long parkId) {
		this.parkId = parkId;
	}
}

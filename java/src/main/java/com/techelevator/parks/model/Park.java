package com.techelevator.parks.model;
import java.time.LocalDate;

public class Park {

	
	private Long id;
	private String parkName;
	private String parkLocation;
	private LocalDate establishedYear;
	private Long area;
	private Long annualVisitors;
	private String description;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getParkName() {
		return parkName;
	}
	public void setParkName(String parkName) {
		this.parkName = parkName;
	}
	public String getParkLocation() {
		return parkLocation;
	}
	public void setParkLocation(String parkLocation) {
		this.parkLocation = parkLocation;
	}
	public LocalDate getEstablishedYear() {
		return establishedYear;
	}
	public void setEstablishedYear(LocalDate establishedYear) {
		this.establishedYear = establishedYear;
	}
	public Long getArea() {
		return area;
	}
	public void setArea(Long area) {
		this.area = area;
	}
	public Long getAnnualVisitors() {
		return annualVisitors;
	}
	public void setAnnualVisitors(Long annualVisitors) {
		this.annualVisitors = annualVisitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}

package com.techelevator.parks.model;
import java.math.BigDecimal;
import java.util.List;

public interface CampgroundDAO {

	public List<Campground> getAllCampgrounds();
	
	public List<Campground> getAllCampgroundsByParkId(int parkId);

	Long getCampgroundIdByName(String campName);

	BigDecimal getCampgroundCostByName(String campName);

}

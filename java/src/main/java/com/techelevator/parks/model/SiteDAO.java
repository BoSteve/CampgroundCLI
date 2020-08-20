package com.techelevator.parks.model;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

	
public List <Site> getAvailableSites(long Id, LocalDate startDate, LocalDate endDate);

}

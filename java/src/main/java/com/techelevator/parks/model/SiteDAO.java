package com.techelevator.parks.model;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

public List<Site> getSiteInfoByCampName(String campName);

public List<Site> getSiteInfoByCampNameEmpty(String campName);

List<Site> dateToSet(LocalDate arrival, LocalDate departure, Long id);
}

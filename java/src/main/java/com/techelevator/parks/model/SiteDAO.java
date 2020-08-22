package com.techelevator.parks.model;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

List<Site> sitesByDate(LocalDate arrival, LocalDate departure, Long id);

List<Site> sortSitesByReservations(List<Long> input);
}

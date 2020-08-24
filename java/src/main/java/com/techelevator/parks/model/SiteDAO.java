spackage com.techelevator.parks.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SiteDAO {

List<Site> sitesByDate(LocalDate arrival, LocalDate departure, Long id);

Map<Long, Integer> sortSitesByReservations(List<Long> input);

List<Site> getSitesById(List<Long> in);
}

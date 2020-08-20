import java.util.List;

public interface CampgroundDAO {

	public List<Campground> getAllCampgrounds();
	
	public Campground getCampgroundById(Long id);
}

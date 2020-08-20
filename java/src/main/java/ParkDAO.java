import java.util.List;

public interface ParkDAO {

	
	public List<Park> getAllParks();
//	public Park getParkName(String parkName);

//************  BONUS ****** COME BACK TO THIS, MAYBE?	 **********************************
//	public void savePark(Park updateParks);
	public Park getParkId(Long id);
}

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {

	
	public List<Reservation> getAllReservations();
	
	public void createReservation(Long confirmationId, LocalDate startDate, LocalDate endDate, String nameOfReservation);
}

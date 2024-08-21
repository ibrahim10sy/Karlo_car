package projet.karlo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import projet.karlo.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,String>{

    // JPQL query to get total amount grouped by month and year
    @Query("SELECT FUNCTION('DATE_FORMAT', r.dateDebut, '%Y-%m') AS monthYear, SUM(r.montant) FROM Reservation r GROUP BY FUNCTION('DATE_FORMAT', r.dateDebut, '%Y-%m')")
    List<Object[]> findTotalAmountByMonth();
//     @Query("SELECT FUNCTION('DATE_FORMAT', r.dateDebut, '%Y-%m') AS monthYear, SUM(r.montant) FROM Reservation r GROUP BY FUNCTION('DATE_FORMAT', r.dateDebut, '%Y-%m')")
// List<Object[]> findTotalAmountByMonth();


    List<Reservation> findByDateDebut(String date);
    List<Reservation> findByDateFin(String date);
    List<Reservation> findByNomClient(String nom);

      @Query("SELECT SUM(r.montant) FROM Reservation r")
      Long calculateTotalReservation();


    Reservation findByIdReservation(String idReservation);
}

package projet.karlo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import projet.karlo.model.Vente;

public interface VenteRepository extends JpaRepository<Vente,String> {
    
    List<Vente> findByNomClient(String nom);

    @Query("SELECT SUM(v.montant) FROM Vente v")
    Long calculateTotalSales();

    @Query("SELECT FUNCTION('DATE_FORMAT', v.dateAjout, '%Y-%m') AS monthYear, SUM(v.montant) FROM Vente v GROUP BY FUNCTION('DATE_FORMAT', v.dateAjout, '%Y-%m')")
    List<Object[]> findTotalSalesByMonth();
    

}

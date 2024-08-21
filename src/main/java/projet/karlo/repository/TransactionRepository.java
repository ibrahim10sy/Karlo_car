package projet.karlo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import projet.karlo.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    List<Transaction> findByTypeTransaction_Libelle(String libelle);
    List<Transaction> findByDateTransaction(String date);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.typeTransaction.libelle = 'depot'")
    Long calculateTotalAmountForDepot();
    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.typeTransaction.libelle = 'retrait'")
    Long calculateTotalAmountForRetrait();
}

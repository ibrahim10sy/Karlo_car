package projet.karlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projet.karlo.model.Marque;

@Repository
public interface MarqueRepository extends JpaRepository<Marque,String>{
    
    Marque findByNomMarque(String nom);

    Marque findByIdMarque(String idMarque);
}

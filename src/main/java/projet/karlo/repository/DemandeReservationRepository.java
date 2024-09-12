package projet.karlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.karlo.model.DemandeReservation;

public interface DemandeReservationRepository extends JpaRepository<DemandeReservation, String> {

    DemandeReservation findByIdDemandeReservation(String idDemandeReservation);
    
}

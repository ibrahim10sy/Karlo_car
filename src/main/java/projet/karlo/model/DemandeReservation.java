package projet.karlo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class DemandeReservation {

    @Id
    private String idDemandeReservation;

    private String dateDajout;

    @Column(nullable = true)
    private String email;

    private Boolean isReserved;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String telephone;

    @ManyToOne
    private VoitureLouer voitureLouer;

    
}

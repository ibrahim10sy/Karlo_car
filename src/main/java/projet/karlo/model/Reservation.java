package projet.karlo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Reservation {

    @Id
    private String idReservation;

    @Column(nullable = false)
    private String dateDebut;
  
    @Column(nullable = false)
    private String dateAjout;
  
    @Column(nullable = false)
    private String dateModif;

    @Column(nullable = false)
    private String dateFin;

    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private int montant;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "pieces", joinColumns = @JoinColumn(name = "id_reservation"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();

    @ManyToOne
    VoitureLouer voitureLouer;
}

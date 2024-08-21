package projet.karlo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;


@Entity
@Data
public class VoitureVendre {
    
    @Id
    private String idVoiture;

    @Column(nullable = false)
    private String matricule;

    @Column(nullable = false)
    private String modele;

    @Column(nullable = false)
    private String annee;

    @Column(nullable = false)
    private String typeBoite;

    @Column(nullable = false)
    private String dateAjout;

    @Column(nullable = true)
    private String dateModif;

    @Column(nullable = false)
    private int nbreView;

    @Column(nullable = false)
    private int nbPortiere;

    private Boolean isVendu;

    @Column(nullable = false)
    private int prixProprietaire;

    @Column(nullable = false)
    private int prixAugmente;

    @ElementCollection
    @CollectionTable(name = "voiture_vendre_images", joinColumns = @JoinColumn(name = "id_voiture"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();

    @ManyToOne
    @ToString.Exclude
    Marque marque;

    @ManyToOne
    @ToString.Exclude
    TypeVoiture typeVoiture;

    @ManyToOne
    @ToString.Exclude
    User user;

    @ManyToOne
    @ToString.Exclude
    TypeReservoir typeReservoir;

}

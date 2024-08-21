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
import lombok.ToString;

@Entity
@Data
public class Vente {

    @Id
    private String idVente;
  
    @Column(nullable = true)
    private String dateAjout;
  
    @Column(nullable = true)
    private String dateModif;


    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private int montant;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "pieces_vente", joinColumns = @JoinColumn(name = "id_vente"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();

    @ManyToOne

    @ToString.Exclude
    VoitureVendre voitureVendre;
    
}

package projet.karlo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class Marque {
    
    @Id
    private String idMarque;

    @Column(nullable = false)
    private String nomMarque;

    @Column(nullable = true)
    private String logo;
    
    @OneToMany(mappedBy = "marque")
    @JsonIgnore
    @ToString.Exclude
    private List<VoitureLouer> voitureLouers;


    @OneToMany
    (mappedBy = "marque")
    @JsonIgnore
    @ToString.Exclude
    private List<VoitureVendre> voitureVendre;
}

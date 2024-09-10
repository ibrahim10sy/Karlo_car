package projet.karlo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class User {
    
    @Id
    private String idUser;

    @Column(nullable = false)
    private String nomUser;

    @Column(nullable = true)
    private String email;

    private Boolean isConnected;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String dateAjout;

    private Boolean statut = false;

    @OneToMany
    (mappedBy = "user")
    @JsonIgnore
    @ToString.Exclude
    private List<VoitureLouer> voitureLouers;

    @OneToMany
    (mappedBy = "user")
    @JsonIgnore
     @ToString.Exclude
    private List<VoitureVendre> voitureVendre;

    @ManyToOne
    Role role;

    @OneToMany
    (mappedBy = "user")
    @JsonIgnore
    private List<Transaction> transactions;

    
    @OneToMany
    (mappedBy = "user")
    @JsonIgnore
     @ToString.Exclude
    private List<Historique> historique;
    
    @OneToMany
    (mappedBy = "user")
    @JsonIgnore
     @ToString.Exclude
    private List<Vente> vente;

 
}

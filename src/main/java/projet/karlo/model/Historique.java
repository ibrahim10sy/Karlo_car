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
public class Historique {
    @Id
    private String idHistorique;

    @Column(nullable = false)
    private String dateHistorique;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @ToString.Exclude
    User user;
}

package projet.karlo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Contact {
    
    @Id
    private String  idContact;

    @Column(nullable = true)
    private String nomComplet;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String telephone;
  
    @Column(nullable = false)
    private String dateAjout;

    @Column(nullable = true)
    private String message;
}

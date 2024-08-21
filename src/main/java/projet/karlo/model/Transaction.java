package projet.karlo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Transaction {

    @Id
    private String idTransaction;

    @Column(nullable = false)
    private String dateTransaction;

    @Column(nullable = true)
    private String dateModif;

    @Column(nullable = false)
    private int montant;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    User user;

    @ManyToOne
    TypeTransaction typeTransaction;
}

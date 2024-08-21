package projet.karlo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import projet.karlo.model.VoitureLouer;
import projet.karlo.model.VoitureVendre;

@Repository
public interface VoitureVendreRepository extends JpaRepository<VoitureVendre,String>{
    long count(); // Méthode pour compter le nombre total des voitures à vendre


    List<VoitureVendre> findByAnnee(String annee);
    List<VoitureVendre> findByTypeBoite(String typeBoite);
    // List<VoitureVendre> findByNbreView();
    List<VoitureVendre> findByTypeReservoir_idTypeReservoir(String id);
    List<VoitureVendre> findByTypeVoiture_idTypeVoiture(String id);
    // List<VoitureVendre> findByNbreView();

    List<VoitureVendre> findByTypeReservoir_NomTypeReservoir(String nom);
    List<VoitureVendre> findByTypeVoiture_NomTypeVoiture(String nom);
    List<VoitureVendre> findByUser_idUser(String id);
    List<VoitureVendre> findByMarque_NomMarque(String nom);

     // Pour récupérer toutes les voitures triées par nombre de vues en ordre décroissant
    List<VoitureVendre> findAllByOrderByNbreViewDesc();

     // Pour récupérer toutes les voitures triées par prix augmenté en ordre décroissant
    List<VoitureVendre> findAllByOrderByPrixAugmenteDesc();

     // Pour récupérer toutes les voitures triées par prix augmenté en ordre croissant
    List<VoitureVendre> findAllByOrderByPrixAugmenteAsc();


    @Query("SELECT v FROM VoitureLouer v " +
    "WHERE (:nomMarque IS NULL OR v.marque.nomMarque = :nomMarque) " +
    "AND (:nomTypeVoiture IS NULL OR v.typeVoiture.nomTypeVoiture = :nomTypeVoiture) " +
    "AND (:nomTypeReservoir IS NULL OR v.typeReservoir.nomTypeReservoir = :nomTypeReservoir) " +
    "AND (:prixAugmente IS NULL OR v.prixAugmente = :prixAugmente)")
List<VoitureVendre> searchVoitures(@Param("nomMarque") String nomMarque,
                               @Param("nomTypeVoiture") String nomTypeVoiture,
                               @Param("nomTypeReservoir") String nomTypeReservoir,
                               @Param("prixAugmente") Integer prixAugmente);


    List<VoitureVendre> findAllByUserIdUser(String idUser);


}

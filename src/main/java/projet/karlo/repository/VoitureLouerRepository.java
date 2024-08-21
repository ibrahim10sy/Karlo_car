package projet.karlo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import projet.karlo.model.VoitureLouer;
import projet.karlo.model.VoitureVendre;

@Repository
public interface VoitureLouerRepository extends JpaRepository<VoitureLouer,String>{
    long count(); // Méthode pour compter le nombre total des voitures à louer


    List<VoitureLouer> findByAnnee(String annee);
    List<VoitureLouer> findByTypeBoite(String typeBoite);
    // List<VoitureLouer> findByNbreView();
    List<VoitureLouer> findByTypeReservoir_idTypeReservoir(String id);
    List<VoitureLouer> findByTypeVoiture_idTypeVoiture(String id);
    // List<VoitureLouer> findByNbreView();

    List<VoitureLouer> findByTypeReservoir_NomTypeReservoir(String nom);
    List<VoitureLouer> findByTypeVoiture_NomTypeVoiture(String nom);
    List<VoitureLouer> findByUser_idUser(String id);
    List<VoitureLouer> findByMarque_NomMarque(String id);

     // Pour récupérer toutes les voitures triées par nombre de vues en ordre décroissant
     List<VoitureLouer> findAllByOrderByNbreViewDesc();

     // Pour récupérer toutes les voitures triées par prix augmenté en ordre décroissant
    List<VoitureLouer> findAllByOrderByPrixAugmenteDesc();

     // Pour récupérer toutes les voitures triées par prix augmenté en ordre croissant
    List<VoitureLouer> findAllByOrderByPrixAugmenteAsc();


    // @Query("SELECT v FROM VoitureLouer v " +
    //        "WHERE (:nomMarque IS NULL OR v.marque.nomMarque = :nomMarque) " +
    //        "AND (:nomTypeVoiture IS NULL OR v.typeVoiture.nomTypeVoiture = :nomTypeVoiture) " +
    //        "AND (:nomTypeReservoir IS NULL OR v.typeReservoir.nomTypeReservoir = :nomTypeReservoir)")
    // List<VoitureLouer> searchVoitures(@Param("nomMarque") String nomMarque,
    //                                   @Param("nomTypeVoiture") String nomTypeVoiture,
    //                                   @Param("nomTypeReservoir") String nomTypeReservoir);

    @Query("SELECT v FROM VoitureLouer v " +
           "WHERE (:nomMarque IS NULL OR v.marque.nomMarque = :nomMarque) " +
           "AND (:nomTypeVoiture IS NULL OR v.typeVoiture.nomTypeVoiture = :nomTypeVoiture) " +
           "AND (:nomTypeReservoir IS NULL OR v.typeReservoir.nomTypeReservoir = :nomTypeReservoir) " +
           "AND (:prixAugmente IS NULL OR v.prixAugmente = :prixAugmente)")
    List<VoitureLouer> searchVoitures(@Param("nomMarque") String nomMarque,
                                      @Param("nomTypeVoiture") String nomTypeVoiture,
                                      @Param("nomTypeReservoir") String nomTypeReservoir,
                                      @Param("prixAugmente") Integer prixAugmente);


    List<VoitureLouer> findAllByUserIdUser(String idUser);

}

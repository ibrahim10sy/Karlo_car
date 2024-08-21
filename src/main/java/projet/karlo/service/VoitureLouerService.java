package projet.karlo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import projet.karlo.model.Marque;
import projet.karlo.model.TypeReservoir;
import projet.karlo.model.TypeVoiture;
import projet.karlo.model.User;
import projet.karlo.model.VoitureLouer;

import projet.karlo.model.VoitureVendre;
import projet.karlo.model.VoitureLouer;
import projet.karlo.repository.MarqueRepository;
import projet.karlo.repository.TypeReservoirRepository;
import projet.karlo.repository.TypeVoitureRepository;
import projet.karlo.repository.UserRepository;
import projet.karlo.repository.VoitureLouerRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class VoitureLouerService {
    
    @Autowired
    IdGenerator idGenerator ;
    @Autowired
    VoitureLouerRepository voitureLouerRepository;
    @Autowired
    UserRepository userRepository ;
    @Autowired
    TypeReservoirRepository typeReservoirRepository;
    @Autowired
    TypeVoitureRepository typeVoitureRepository;
    @Autowired
    MarqueRepository marqueRepository;
    @Autowired
    HistoriqueService historiqueService;
    @Autowired
    FileUpload fileUploade;


    public VoitureLouer createVoiture(VoitureLouer vLouer, List<MultipartFile> imageFiles) throws Exception {
        // Vérification de l'existence des entités associées
        User user  = userRepository.findById(vLouer.getUser().getIdUser()).orElseThrow();

        TypeVoiture type = typeVoitureRepository.findById(vLouer.getTypeVoiture().getIdTypeVoiture())
                .orElseThrow(() -> new EntityNotFoundException("Type de voiture non trouvé"));
        TypeReservoir typeRe = typeReservoirRepository.findById(vLouer.getTypeReservoir().getIdTypeReservoir())
                .orElseThrow(() -> new EntityNotFoundException("Type de réservoir non trouvé"));
        Marque marque = marqueRepository.findById(vLouer.getMarque().getIdMarque())
                .orElseThrow(() -> new EntityNotFoundException("Marque de voiture non trouvée"));
    
        // Génération de l'ID et mise à jour de la date
        String idcodes = idGenerator.genererCode();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        vLouer.setIdVoiture(idcodes);
        vLouer.setDateAjout(now.format(formatter));
    
        // Traitement des fichiers d'images
        if (imageFiles != null && !imageFiles.isEmpty()) {
              String imageLocation = "/karlo"; 
            // String imageLocation = "C:\\Users\\bane8\\Documents\\Spring Boot App\\Karlo_car\\images";
            Path imageRootLocation = Paths.get(imageLocation);
            if (!Files.exists(imageRootLocation)) {
                Files.createDirectories(imageRootLocation);
            }
    
            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                    Path imagePath = imageRootLocation.resolve(imageName);
                    try {
                        Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                        String onlineImagePath =fileUploade.uploadImageToFTP(imagePath, imageName);
                        imagePaths.add(imageName);
                    } catch (IOException e) {
                        throw new IOException("Erreur lors de la sauvegarde de l'image : " + imageFile.getOriginalFilename(), e);
                    }
                }
            }
            vLouer.setImages(imagePaths);
        }

        // Création de l'historique
        historiqueService.createHistorique("Ajout de voiture de location : " + vLouer.getModele() + " matricule : " + vLouer.getMatricule());

        return voitureLouerRepository.save(vLouer);
    }

    public List<VoitureLouer> searchVoitures(String nomMarque, String nomTypeVoiture, String nomTypeReservoir, int prix) {
        return voitureLouerRepository.searchVoitures(nomMarque, nomTypeVoiture, nomTypeReservoir, prix);
    }




    public VoitureLouer updateVoiture(VoitureLouer vlouer, String id, List<MultipartFile> imageFiles) throws Exception {
        VoitureLouer v = voitureLouerRepository.findById(id).orElseThrow(() -> new IllegalStateException("Voiture non trouvée"));
    
        // Mettre à jour les informations de la voiture
        v.setModele(vlouer.getModele());
        v.setMatricule(vlouer.getMatricule());
        v.setAnnee(vlouer.getAnnee());
        v.setTypeBoite(vlouer.getTypeBoite());
        v.setNbPortiere(vlouer.getNbPortiere());
        v.setPrixProprietaire(vlouer.getPrixProprietaire());
        v.setPrixAugmente(vlouer.getPrixAugmente());
        v.setIsChauffeur(vlouer.getIsChauffeur());
        v.setIsDisponible(true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        v.setDateModif(now.format(formatter));
    
        if (vlouer.getTypeVoiture() != null) {
            v.setTypeVoiture(vlouer.getTypeVoiture());
        }
    
        if (vlouer.getTypeReservoir() != null) {
            v.setTypeReservoir(vlouer.getTypeReservoir());
        }
    
        if (vlouer.getMarque() != null) {
            v.setMarque(vlouer.getMarque());
        }
    
        // Traitement des fichiers d'images
        if (imageFiles != null && !imageFiles.isEmpty()) {
              String imageLocation = "/karlo"; 
            Path imageRootLocation = Paths.get(imageLocation);
            if (!Files.exists(imageRootLocation)) {
                Files.createDirectories(imageRootLocation);
            }
    
            // Supprimer les anciennes images
            for (String oldImagePath : v.getImages()) {
                Path oldImageFile = imageRootLocation.resolve(oldImagePath.substring("/karlo/".length()));
                if (Files.exists(oldImageFile)) {
                    Files.delete(oldImageFile);
                }
            }
    
            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                    Path imagePath = imageRootLocation.resolve(imageName);
                    try {
                        Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                        String onlineImagePath =fileUploade.uploadImageToFTP(imagePath, imageName);
                        imagePaths.add(imageName);
                    } catch (IOException e) {
                        throw new IOException("Erreur lors de la sauvegarde de l'image : " + imageFile.getOriginalFilename(), e);
                    }
                }
            }
            v.setImages(imagePaths);
        }
    
        historiqueService.createHistorique("Modification de voiture de location : " + v.getModele() + " matricule : " + v.getMatricule());
    
        return voitureLouerRepository.save(v);
    }
    // public VoitureLouer updateVoiture(VoitureLouer vlouer, String id, List<MultipartFile> imageFiles) throws Exception {
    //     VoitureLouer v = voitureLouerRepository.findById(id).orElseThrow(() -> new IllegalStateException("Voiture non trouvée"));

    //     v.setModele(vlouer.getModele());
    //     v.setMatricule(vlouer.getMatricule());
    //     v.setAnnee(vlouer.getAnnee());
    //     v.setTypeBoite(vlouer.getTypeBoite());
    //     v.setNbPortiere(vlouer.getNbPortiere());
    //     v.setPrixProprietaire(vlouer.getPrixProprietaire());
    //     v.setPrixAugmente(vlouer.getPrixAugmente());
    //     v.setIsChauffeur(vlouer.getIsChauffeur());
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //     LocalDateTime now = LocalDateTime.now();
    //     v.setDateModif(now.format(formatter));
    
    //     if (vlouer.getTypeVoiture() != null) {
    //         v.setTypeVoiture(vlouer.getTypeVoiture());
    //     }
    
    //     if (vlouer.getTypeReservoir() != null) {
    //         v.setTypeReservoir(vlouer.getTypeReservoir());
    //     }
    
    //     if (vlouer.getMarque() != null) {
    //         v.setMarque(vlouer.getMarque());
    //     }
    
    //      // Traitement des fichiers d'images
    //      if (imageFiles != null && !imageFiles.isEmpty()) {
    //           String imageLocation = "/karlo"; 
    //         Path imageRootLocation = Paths.get(imageLocation);
    //         if (!Files.exists(imageRootLocation)) {
    //             Files.createDirectories(imageRootLocation);
    //         }
    
    //         List<String> imagePaths = new ArrayList<>();
    //         for (MultipartFile imageFile : imageFiles) {
    //             if (!imageFile.isEmpty()) {
    //                 String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
    //                 Path imagePath = imageRootLocation.resolve(imageName);
    //                 try {
    //                     Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
    //                     imagePaths.add("/karlo/" + imageName);
    //                 } catch (IOException e) {
    //                     throw new IOException("Erreur lors de la sauvegarde de l'image : " + imageFile.getOriginalFilename(), e);
    //                 }
    //             }
    //         }
    //         v.setImages(imagePaths);
    //     }

    //     historiqueService.createHistorique("Modification  de voiture de location : " + v.getModele() + "matricule : " + v.getMatricule());

    //     return voitureLouerRepository.save(v);
    // }
    


    public List<VoitureLouer> getAllVoiture(){
        List<VoitureLouer> voitureList = voitureLouerRepository.findAll();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureLouerByUser(String idUser){
        List<VoitureLouer> voitureList = voitureLouerRepository.findAllByUserIdUser(idUser);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    // public List<VoitureLouer> getAllVoitureByNombreView(){
    //     List<VoitureLouer> voitureList = voitureLouerRepository.findAll();

    //     if (voitureList.isEmpty())
    //         throw new EntityNotFoundException("Aucune voiture trouvée");

    //     voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

    //     return voitureList;
    // }

    public VoitureLouer active(String id) throws Exception{
        VoitureLouer v = voitureLouerRepository.findById(id).orElseThrow(null);

        try {
            v.setIsDisponible(true);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'activation de la voiture: " + e.getMessage());
        }
        historiqueService.createHistorique("Mis à jour du statut à disponible de la voiture" + v.getMatricule() + " model " + v.getModele());
        return voitureLouerRepository.save(v);
    }

    public VoitureLouer desactive(String id) throws Exception{
        VoitureLouer v = voitureLouerRepository.findById(id).orElseThrow(null);

        try {
            v.setIsDisponible(false);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la desactivation du User : " + e.getMessage());
        }
        historiqueService.createHistorique("Mis à jour du statut à non disponible de la voiture" + v.getMatricule() + " model " + v.getModele());
        return voitureLouerRepository.save(v);
    }

      public List<VoitureLouer> getAllVoitureByMarque(String nom){
        List<VoitureLouer> voitureList = voitureLouerRepository.findByMarque_NomMarque(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureByTypeVoiture(String nom){
        List<VoitureLouer> voitureList = voitureLouerRepository.findByTypeVoiture_NomTypeVoiture(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureByTypeReservoir(String nom){
        List<VoitureLouer> voitureList = voitureLouerRepository.findByTypeReservoir_NomTypeReservoir(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureByTypeBoite(String nom){
        List<VoitureLouer> voitureList = voitureLouerRepository.findByTypeBoite(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureByAnnee(String annee){
        List<VoitureLouer> voitureList = voitureLouerRepository.findByAnnee(annee);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureByNbreViews(){
        List<VoitureLouer> voitureList = voitureLouerRepository.findAllByOrderByNbreViewDesc();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }


    public List<VoitureLouer> getAllVoitureByPrixAugmenter(){
        List<VoitureLouer> voitureList = voitureLouerRepository.findAllByOrderByPrixAugmenteDesc();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureLouer> getAllVoitureByPrixAugmenterMoinsChere(){
        List<VoitureLouer> voitureList = voitureLouerRepository.findAllByOrderByPrixAugmenteAsc();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureLouer::getDateAjout).reversed());

        return voitureList;
    }

    public String deleteVoiture(String id){
        VoitureLouer v = voitureLouerRepository.findById(id).orElseThrow(()-> new IllegalStateException("Voiture non trouvée"));
        historiqueService.createHistorique("Suppression de la  voiture de location : " + v.getModele() + "matricule : " + v.getMatricule());

        voitureLouerRepository.delete(v);
        return "Supprimé avec succès";
    }

    //  public VoitureLouer updateNbViev(String id) throws Exception {
    //     Optional<VoitureLouer> voitureOpt = voitureLouerRepository.findById(id);
    //     int count = voitureOpt.get().getNbreView();

    //     if (voitureOpt.isPresent()) {
    //         VoitureLouer VoitureLouer = voitureOpt.get();
    //         VoitureLouer.setNbreView(count);
     public VoitureLouer updateNbViev(String id) throws Exception {
        Optional<VoitureLouer> voitureOpt = voitureLouerRepository.findById(id);
        
        if (voitureOpt.isPresent()) {
            VoitureLouer VoitureLouer = voitureOpt.get();
            int count = VoitureLouer.getNbreView() + 1;
            VoitureLouer.setNbreView(count);

            return voitureLouerRepository.save(VoitureLouer);
        } else {
            throw new Exception("Une erreur s'est produite");
        }
    }
}

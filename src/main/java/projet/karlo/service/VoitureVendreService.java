package projet.karlo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import projet.karlo.model.Marque;
import projet.karlo.model.TypeReservoir;
import projet.karlo.model.TypeVoiture;
import projet.karlo.model.User;
import projet.karlo.model.VoitureLouer;
import projet.karlo.model.VoitureVendre;
import projet.karlo.repository.MarqueRepository;
import projet.karlo.repository.TypeReservoirRepository;
import projet.karlo.repository.TypeVoitureRepository;
import projet.karlo.repository.UserRepository;
import projet.karlo.repository.VoitureVendreRepository;

@Service
public class VoitureVendreService {
    @Autowired
    IdGenerator idGenerator ;
    @Autowired
    VoitureVendreRepository voitureVendreRepository;
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


    public VoitureVendre createVoiture(VoitureVendre vVendre,List<MultipartFile> imageFiles) throws Exception{
        User user  = userRepository.findByIdUser(vVendre.getUser().getIdUser());

        TypeVoiture type = typeVoitureRepository.findById(vVendre.getTypeVoiture().getIdTypeVoiture()).orElseThrow();

        TypeReservoir typeRe = typeReservoirRepository.findById(vVendre.getTypeReservoir().getIdTypeReservoir()).orElseThrow();

        Marque marque = marqueRepository.findById(vVendre.getMarque().getIdMarque()).orElseThrow();

        if(marque == null)
            throw new IllegalStateException("Aucune marque de voiture trouvée");
    
        if(user == null)
            throw new IllegalStateException("Aucun utilisateur trouvée");

        if(type == null)
            throw new IllegalStateException("Aucun type de voiture trouvée");

        if(typeRe == null)
            throw new IllegalStateException("Aucun type de reservoir trouvé trouvée");
     
    // Traitement des fichiers d'images
    if (imageFiles != null && !imageFiles.isEmpty()) {
          String imageLocation = "/karlo"; 
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
        vVendre.setImages(imagePaths);
    }

        vVendre.setIsVendu(false);

       // Génération de l'ID et mise à jour de la date
        String idcodes = idGenerator.genererCode();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        vVendre.setIdVoiture(idcodes);
        vVendre.setDateAjout(now.format(formatter));

    historiqueService.createHistorique("Ajout de voiture de location : " + vVendre.getModele() + "matricule : " + vVendre.getMatricule());

            return voitureVendreRepository.save(vVendre);
    }

    public VoitureVendre updateVoiture(VoitureVendre vVendre, String id,  List<MultipartFile> imageFiles) throws Exception{
        VoitureVendre v = voitureVendreRepository.findById(id).orElseThrow(()-> new IllegalStateException("Voiture non trouvé"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        v.setModele(vVendre.getModele());
        v.setMatricule(vVendre.getMatricule());
        v.setAnnee(vVendre.getAnnee());
        v.setTypeBoite(vVendre.getTypeBoite());
        v.setNbPortiere(vVendre.getNbPortiere());
        v.setPrixProprietaire(vVendre.getPrixProprietaire());
        v.setPrixAugmente(vVendre.getPrixAugmente());
        v.setDateModif(now.format(formatter));

        if(vVendre.getTypeVoiture() != null){
            v.setTypeVoiture(vVendre.getTypeVoiture());
        }

        if(vVendre.getTypeReservoir() != null){
            v.setTypeReservoir(vVendre.getTypeReservoir());
        }

        if(vVendre.getMarque() != null){
            v.setMarque(vVendre.getMarque());
        }

       // Traitement des fichiers d'images
         if (imageFiles != null && !imageFiles.isEmpty()) {
              String imageLocation = "/karlo"; 
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
            v.setImages(imagePaths);
        }

        historiqueService.createHistorique("Modification  de voiture de location : " + v.getModele() + "matricule : " + v.getMatricule());

        return voitureVendreRepository.save(v);
    }


    public VoitureVendre active(String id) throws Exception{
        VoitureVendre v = voitureVendreRepository.findById(id).orElseThrow(null);

        try {
            v.setIsVendu(true);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'activation de la voiture: " + e.getMessage());
        }
        historiqueService.createHistorique("Mis à jour du statut à vendu de la voiture" + v.getMatricule() + " model " + v.getModele());
        return voitureVendreRepository.save(v);
    }

    public VoitureVendre desactive(String id) throws Exception{
        VoitureVendre v = voitureVendreRepository.findById(id).orElseThrow(null);

        try {
            v.setIsVendu(false);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la desactivation du User : " + e.getMessage());
        }
        historiqueService.createHistorique("Mis à jour du statut à non vendu de la voiture" + v.getMatricule() + " model " + v.getModele());
        return voitureVendreRepository.save(v);
    }


    public List<VoitureVendre> searchVoitures(String nomMarque, String nomTypeVoiture, String nomTypeReservoir, int prix) {
        return voitureVendreRepository.searchVoitures(nomMarque, nomTypeVoiture, nomTypeReservoir, prix);
    }


    public List<VoitureVendre> getAllVoiture(){
        List<VoitureVendre> voitureList = voitureVendreRepository.findAll();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureVendreByUser(String idUser){
        List<VoitureVendre> voitureList = voitureVendreRepository.findAllByUserIdUser(idUser);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByMarque(String nom){
        List<VoitureVendre> voitureList = voitureVendreRepository.findByMarque_NomMarque(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByTypeVoiture(String nom){
        List<VoitureVendre> voitureList = voitureVendreRepository.findByTypeVoiture_NomTypeVoiture(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByTypeReservoir(String nom){
        List<VoitureVendre> voitureList = voitureVendreRepository.findByTypeReservoir_NomTypeReservoir(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByTypeBoite(String nom){
        List<VoitureVendre> voitureList = voitureVendreRepository.findByTypeBoite(nom);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByAnnee(String annee){
        List<VoitureVendre> voitureList = voitureVendreRepository.findByAnnee(annee);

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByNbreViews(){
        List<VoitureVendre> voitureList = voitureVendreRepository.findAllByOrderByNbreViewDesc();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }


    public List<VoitureVendre> getAllVoitureByPrixAugmenter(){
        List<VoitureVendre> voitureList = voitureVendreRepository.findAllByOrderByPrixAugmenteDesc();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }

    public List<VoitureVendre> getAllVoitureByPrixAugmenterMoinsChere(){
        List<VoitureVendre> voitureList = voitureVendreRepository.findAllByOrderByPrixAugmenteAsc();

        if (voitureList.isEmpty())
            throw new EntityNotFoundException("Aucune voiture trouvée");

        voitureList.sort(Comparator.comparing(VoitureVendre::getDateAjout).reversed());

        return voitureList;
    }


    public String deleteVoiture(String id){
        VoitureVendre v = voitureVendreRepository.findById(id).orElseThrow(()-> new IllegalStateException("Voiture non trouvée"));
        historiqueService.createHistorique("Suppression de la  voiture  : " + v.getModele() + "matricule : " + v.getMatricule());

        voitureVendreRepository.delete(v);
        return "Supprimé avec succès";
    }


    public VoitureVendre updateNbViev(String id) throws Exception {
        Optional<VoitureVendre> voitureOpt = voitureVendreRepository.findById(id);
        
        if (voitureOpt.isPresent()) {
            VoitureVendre voitureVendre = voitureOpt.get();
            int count = voitureVendre.getNbreView() +1;
            voitureVendre.setNbreView(count);

            return voitureVendreRepository.save(voitureVendre);
        } else {
            throw new Exception("Une erreur s'est produite");
        }
    }
}

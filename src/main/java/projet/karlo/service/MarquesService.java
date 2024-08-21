package projet.karlo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import projet.karlo.model.Marque;
import projet.karlo.repository.MarqueRepository;

@Service
public class MarquesService {

    @Autowired
    MarqueRepository marqueRepository;
    @Autowired
    IdGenerator idGenerator;
    @Autowired
    FileUpload fileUploade;

    public Marque createMarque(Marque marque, MultipartFile logoFile) throws Exception {
        Marque m = marqueRepository.findByNomMarque(marque.getNomMarque());
    
        if (m != null)
            throw new IllegalStateException("Cette marque existe déjà");
    
        if (logoFile != null) {
              String imageLocation = "/karlo"; 
            try {
                Path imageRootLocation = Paths.get(imageLocation);
                if (!Files.exists(imageRootLocation)) {
                    Files.createDirectories(imageRootLocation);
                }
    
                String imageName = UUID.randomUUID().toString() + "_" + logoFile.getOriginalFilename();
                Path imagePath = imageRootLocation.resolve(imageName);
                Files.copy(logoFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                String onlineImagePath =fileUploade.uploadImageToFTP(imagePath, imageName);
                marque.setLogo(imageName); // Utiliser le nom de fichier généré
    
            } catch (IOException e) {
                throw new Exception("Erreur lors du traitement du fichier image : " + e.getMessage());
            }
        }
        String idcodes = idGenerator.genererCode();
        marque.setIdMarque(idcodes);
    
        return marqueRepository.save(marque);
    }
    

    public Marque updateMarque(Marque marque, String id, MultipartFile logoFile) throws Exception {
        Marque m = marqueRepository.findById(id).orElseThrow(()->new IllegalStateException("Marque non trouvé"));

        m.setNomMarque(marque.getNomMarque());

        if (logoFile != null) {
              String imageLocation = "/karlo"; 
            try {
                Path imageRootLocation = Paths.get(imageLocation);
                if (!Files.exists(imageRootLocation)) {
                    Files.createDirectories(imageRootLocation);
                }

                String imageName = UUID.randomUUID().toString() + "_" + logoFile.getOriginalFilename();
                Path imagePath = imageRootLocation.resolve(imageName);
                Files.copy(logoFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                // String onlineImagePath =fileUploade.uploadImageToFTP(imagePath, imageName);
                String onlineImagePath =fileUploade.uploadImageToFTP(imagePath, imageName);
                m.setLogo(imageName);

            } catch (IOException e) {
                throw new  Exception("Erreur lors du traitement du fichier image : " + e.getMessage());
            }
        }
        
        return marqueRepository.save(m);
    }

    public List<Marque> getAllMarque() {
        List<Marque> marque = marqueRepository.findAll();

        if (marque.isEmpty())
            throw new EntityNotFoundException("Aucune marque trouvée");

            marque.sort(Comparator.comparing(Marque::getNomMarque));
        return marque;
    }

    public String deleteMarque(String idMarque){
        Marque marque = marqueRepository.findById(idMarque).orElseThrow(() -> new IllegalStateException("Marque non trouvé") );
        marqueRepository.delete(marque);
        return "Marque supprimée avec succès";
    }
}

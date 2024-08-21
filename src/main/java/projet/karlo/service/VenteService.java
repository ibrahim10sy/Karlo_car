package projet.karlo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.antlr.v4.runtime.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.YearMonth;
import java.util.stream.Collectors;


import jakarta.persistence.EntityNotFoundException;
import projet.karlo.model.Vente;
import projet.karlo.model.VoitureVendre;
import projet.karlo.repository.VenteRepository;
import projet.karlo.repository.VoitureVendreRepository;

@Service
public class VenteService {

    @Autowired
    IdGenerator idGenerator ;
    @Autowired
    VoitureVendreRepository vRepository;
    @Autowired
    VenteRepository venteRepository;
    @Autowired
    VoitureVendreRepository voitureVendreRepository;
    @Autowired
    HistoriqueService historiqueService;
    @Autowired
    FileUpload fileUploade;


     public Vente createVente (Vente vente, List<MultipartFile> imageFiles) throws Exception {
        VoitureVendre vVendre = vRepository.findById( vente.getVoitureVendre().getIdVoiture()).orElseThrow();
    if (vVendre != null) {
        vVendre.setIsVendu(true); // Mettre le statut à true
        // Vous devez sauvegarder la voiture aussi si elle est modifiée
        voitureVendreRepository.save(vVendre);
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
        vente.setImages(imagePaths);
    }

        String idcodes = idGenerator.genererCode();
        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        vente.setIdVente(idcodes);
        vente.setDateAjout(formattedDateTime);
        historiqueService.createHistorique("Vente de voiture " + vVendre.getModele() + " matricule " + vVendre.getMatricule());

        return venteRepository.save(vente);
    }

    public Vente updateVente(Vente vente, String id , List<MultipartFile> imageFiles ) throws Exception{
        Vente vExistant = venteRepository.findById(id).orElseThrow();


        vExistant.setNomClient(vente.getNomClient());
        vExistant.setTelephone(vente.getTelephone());
        vExistant.setMontant(vente.getMontant());
        vExistant.setDescription(vente.getDescription());
         

        if(vente.getVoitureVendre() != null){
            vExistant.setVoitureVendre(vente.getVoitureVendre());
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
        vExistant.setImages(imagePaths);
    }


        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        vExistant.setDateModif(formattedDateTime);
        historiqueService.createHistorique("Modification vente de voiture " + vExistant.getVoitureVendre().getModele() + " matricule " + vExistant.getVoitureVendre().getMatricule());

        return venteRepository.save(vExistant);
    }


    public Map<String, Long> getTotalSalesByMonth() {
    List<Object[]> results = venteRepository.findTotalSalesByMonth();
    Map<String, Long> totalSalesByMonth = new HashMap<>();

    // Convert the results to a map
    for (Object[] result : results) {
        String monthYear = (String) result[0];
        Long totalSales = ((Number) result[1]).longValue();
        totalSalesByMonth.put(monthYear, totalSales);
    }

    // Generate a list of all months of the current year
    YearMonth now = YearMonth.now();
    for (int month = 1; month <= 12; month++) {
        String monthYear = now.withMonth(month).toString();
        monthYear = monthYear.substring(0, 7); // Extract 'YYYY-MM'
        totalSalesByMonth.putIfAbsent(monthYear, 0L);
    }

    return totalSalesByMonth;
    }


    public Long getTotalSales() {
        return venteRepository.calculateTotalSales();
    }


    public List<Vente> getAllVente() {

        List<Vente> vente = venteRepository.findAll();

        if (vente.isEmpty()){
            throw new EntityNotFoundException("Aucune vente de voiture trouvée");
        }
            vente.sort(Comparator.comparing(Vente::getDateAjout).reversed());
        return vente;
    }

    public List<Vente> getAllVenteByClient(String nomClient) {
        List<Vente> vente = venteRepository.findByNomClient(nomClient);

        if (vente.isEmpty()){

            throw new EntityNotFoundException("Aucune vente trouvée pour le client " + nomClient);
        }

            vente.sort(Comparator.comparing(Vente::getDateAjout).reversed());
        return vente;
    }



    public String deleteVente(String id){
        Vente vente = venteRepository.findById(id).orElseThrow(() -> new IllegalStateException("vente non trouvé") );

        if(vente == null){

            throw new IllegalStateException("Vente not found");
        }

            venteRepository.delete(vente);
        return "Vente supprimé avec succèss";
    }
    
}

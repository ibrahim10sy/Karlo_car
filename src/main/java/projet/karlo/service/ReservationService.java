package projet.karlo.service;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.time.YearMonth;

import java.nio.file.Files;

import projet.karlo.model.Reservation;
import projet.karlo.model.Role;
import projet.karlo.model.VoitureLouer;
import projet.karlo.repository.ReservationRepository;
import projet.karlo.repository.VoitureLouerRepository;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository rRepository;
    @Autowired
    IdGenerator idGenerator ;
    @Autowired
    VoitureLouerRepository vRepository;
    @Autowired
    VoitureLouerRepository voitureLouerRepository;
    @Autowired
    HistoriqueService historiqueService;
    @Autowired
    FileUpload fileUploade;


    public Reservation createReservation (Reservation reservation, List<MultipartFile> imageFiles) throws Exception {
        VoitureLouer vlouer = vRepository.findById( reservation.getVoitureLouer().getIdVoiture()).orElseThrow();
        if (vlouer != null) {
            vlouer.setIsDisponible(false); // Mettre le statut à false non dispo
            // Vous devez sauvegarder la voiture aussi si elle est modifiée
            voitureLouerRepository.save(vlouer);
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
        reservation.setImages(imagePaths);
    }

        String idcodes = idGenerator.genererCode();
        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        reservation.setIdReservation(idcodes);
        reservation.setDateAjout(formattedDateTime);
        historiqueService.createHistorique("Réservation de voiture " + vlouer.getModele() + " matricule " + vlouer.getMatricule());

        return rRepository.save(reservation);
    }

    public Reservation updateReservation(Reservation reservation, String id , List<MultipartFile> imageFiles ) throws Exception{
        Reservation res = rRepository.findById(id).orElseThrow();

        res.setDateDebut(reservation.getDateDebut());
        res.setDateFin(reservation.getDateFin());
        res.setNomClient(reservation.getNomClient());
        res.setTelephone(reservation.getTelephone());
        res.setMontant(reservation.getMontant());
        res.setDescription(reservation.getDescription());

        if(reservation.getVoitureLouer() != null){
            res.setVoitureLouer(reservation.getVoitureLouer());
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
        res.setImages(imagePaths);
    }


        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        res.setDateModif(formattedDateTime);
        historiqueService.createHistorique("Modification réservation de voiture " + res.getVoitureLouer().getModele() + " matricule " + res.getVoitureLouer().getMatricule());

        return rRepository.save(res);
    }

    public Map<String, Long> getTotalAmountByMonth() {
        List<Object[]> results = rRepository.findTotalAmountByMonth();
        Map<String, Long> totalByMonth = new HashMap<>();
        
        // Convert the results to a map
        for (Object[] result : results) {
            String monthYear = (String) result[0];
            Long totalAmount = ((Number) result[1]).longValue();
            totalByMonth.put(monthYear, totalAmount);
        }

        // Generate a list of all months of the current year
        YearMonth now = YearMonth.now();
        for (int month = 1; month <= 12; month++) {
            String monthYear = now.withMonth(month).toString();
            monthYear = monthYear.substring(0, 7); // Extract 'YYYY-MM'
            totalByMonth.putIfAbsent(monthYear, 0L);
        }

        return totalByMonth;
    }


    public Long getTotalReservation() {
        return rRepository.calculateTotalReservation();
    }


    public List<Reservation> getAllReservation() {

        List<Reservation> res = rRepository.findAll();

        if (res.isEmpty()){
            throw new EntityNotFoundException("Aucune reservation trouvée");
        }
            res.sort(Comparator.comparing(Reservation::getDateAjout).reversed());
        return res;
    }

    public List<Reservation> getAllReservationByClient(String nomClient) {
        List<Reservation> reservation = rRepository.findByNomClient(nomClient);

        if (reservation.isEmpty()){

            throw new EntityNotFoundException("Aucune reservation trouvée");
        }

            reservation.sort(Comparator.comparing(Reservation::getDateAjout).reversed());
        return reservation;
    }

    public List<Reservation> getAllReservationByDateDebut(String date) {
        List<Reservation> reservation = rRepository.findByDateDebut(date);

        if (reservation.isEmpty()){

            throw new EntityNotFoundException("Aucune reservation trouvée");
        }

            reservation.sort(Comparator.comparing(Reservation::getDateAjout).reversed());
        return reservation;
    }

    public List<Reservation> getAllReservationByDateFin(String date) {
        List<Reservation> reservation = rRepository.findByDateFin(date);

        if (reservation.isEmpty()){

            throw new EntityNotFoundException("Aucune reservation trouvée");
        }

            reservation.sort(Comparator.comparing(Reservation::getDateAjout).reversed());
        return reservation;
    }

    public String deleteRes(String id){
        Reservation reservation = rRepository.findById(id).orElseThrow(() -> new IllegalStateException("reservation non trouvé") );

        if(reservation == null){

            throw new IllegalStateException("Reservation not found");
        }

            rRepository.delete(reservation);
        return "Reservation supprimé avec succèss";
    }

}

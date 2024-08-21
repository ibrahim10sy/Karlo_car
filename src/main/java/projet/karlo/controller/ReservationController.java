package projet.karlo.controller;

import java.util.List;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.*;

import projet.karlo.model.Marque;
import projet.karlo.model.Reservation;
import projet.karlo.model.VoitureLouer;
import projet.karlo.repository.ReservationRepository;
import projet.karlo.service.FileUpload;
import projet.karlo.service.ReservationService;



@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    ReservationService reservationService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    FileUpload fileUploade;


    @PostMapping("/addReservation")
    @Operation(summary = "Ajout d'une reservation")
    public ResponseEntity<Reservation> createReser(
            @Valid @RequestParam("reservation") String reservationString,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
            throws Exception {
        Reservation reservation = new Reservation();
        try {
            reservation = new JsonMapper().readValue(reservationString, Reservation.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }
    
        Reservation savedreservation = reservationService.createReservation(reservation, imageFiles);
        System.out.println(" controller :" + savedreservation);
    
        return new ResponseEntity<>(savedreservation, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Modification d'une reservation")
    public ResponseEntity<Reservation> updateRes(
            @Valid @RequestParam("reservation") String reservationString,
            @PathVariable String id,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
            throws Exception {
        Reservation reservation = new Reservation();
        try {
            reservation = new JsonMapper().readValue(reservationString, Reservation.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }
    
        Reservation savedreservation = reservationService.updateReservation(reservation, id, imageFiles);
        System.out.println(" controller :" + savedreservation);
    
        return new ResponseEntity<>(savedreservation, HttpStatus.OK);
    }


      @GetMapping("/{idReservation}/image")
            public ResponseEntity<byte[]> getImage(@PathVariable String idReservation) {
                try {
                    // Récupérer le nom de l'image associée a la reservation
                    Reservation reservation = reservationRepository.findByIdReservation(idReservation);
                    if (reservation == null || reservation.getImages().isEmpty()) {
                        return ResponseEntity.notFound().build();
                    }
            
                    List<String> imageName = reservation.getImages();
            
                    // Récupérer l'image à partir du serveur FTP
                    byte[] imageBytes = fileUploade.getImagesByName(imageName);
            
                    // Détecter le type de contenu de l'image en fonction de son extension
                MediaType contentType = detectContentType(imageName);
            
                // Retourner l'image avec le type de contenu approprié
                return ResponseEntity.ok()
                        .contentType(contentType)
                        .body(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            }
            
            private MediaType detectContentType(List<String> imageName) {
                for (String image : imageName) {
                    String[] parts = image.split("\\.");
                    if (parts.length > 1) {
                        String extension = parts[parts.length - 1].toLowerCase();
                        switch (extension) {
                            case "jpg":
                            case "jpeg":
                                return MediaType.IMAGE_JPEG;
                            case "png":
                                return MediaType.IMAGE_PNG;
                            case "gif":
                                return MediaType.IMAGE_GIF;
                            // Ajoutez d'autres cas pour les types de contenu supplémentaires si nécessaire
                            default:
                                break;
                        }
                    }
                }
                // Par défaut, retourner MediaType.APPLICATION_OCTET_STREAM
                return MediaType.APPLICATION_OCTET_STREAM;
            }



     @GetMapping("/totalReservationParMoi")
     public java.util.Map<String, Long> getTotalAmountByMonth() {
        return reservationService.getTotalAmountByMonth();
    }


    @GetMapping("/totalVoitureLouer")
    public Long getTotalSommeLocation() {
        return reservationService.getTotalReservation();
    }


     @GetMapping("/getAllReservation")
     @Operation(summary="Liste de tout les reservations")
      public ResponseEntity<List<Reservation>> getAllReservation(){
                return new ResponseEntity<>(reservationService.getAllReservation(),HttpStatus.OK);
    }

     @GetMapping("/getAllReservationByClient/{nomClient}")
     @Operation(summary="Liste de tout les reservations")
      public ResponseEntity<List<Reservation>> getAllReservationByClt(@PathVariable String nom){
                return new ResponseEntity<>(reservationService.getAllReservationByClient(nom),HttpStatus.OK);
    }

    @GetMapping("/getAllReservationVyDateDebut/{date}")
    @Operation(summary="Liste de tout les reservations")
     public ResponseEntity<List<Reservation>> getAllReservationByDateDebut(@PathVariable String date){
               return new ResponseEntity<>(reservationService.getAllReservationByDateDebut(date),HttpStatus.OK);
   }

    @GetMapping("/getAllReservationVyDateFin/{date}")
    @Operation(summary="Liste de tout les reservations")
     public ResponseEntity<List<Reservation>> getAllReservationByDateFin(@PathVariable String date){
               return new ResponseEntity<>(reservationService.getAllReservationByDateFin(date),HttpStatus.OK);
   }


   @DeleteMapping("/delete/{id}")
   @Operation(summary="Suppression d'une reservation de voiture")
    public ResponseEntity<Void> deleteRes(@PathVariable("id") String id) {
        reservationService.deleteRes(id);
    return  new ResponseEntity<>(HttpStatus.OK); 
    }
    
}

package projet.karlo.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.Operation;
import projet.karlo.model.DemandeReservation;
import projet.karlo.model.Transaction;
import projet.karlo.service.DemandeReservationService;

@RestController
@RequestMapping("/demandeReservation")
public class DemandeReservationController {

    @Autowired
    private DemandeReservationService demandeReservationService;

    @PostMapping("/AddDemandeReservation")
    @Operation(summary="Demande de reservation")
    public ResponseEntity<DemandeReservation> createDemandeReservation(@RequestBody DemandeReservation demandeReservation) {
        return new ResponseEntity<>(demandeReservationService.createDemandeReservation(demandeReservation) , HttpStatus.OK);
    }

    @GetMapping("/getAllDemandeReservation")
    @Operation(summary="Liste de toutes les demandes de reservations")
    public ResponseEntity<List<DemandeReservation>> getAllDemandesReservations(){
        return new ResponseEntity<>(demandeReservationService.getAllDemandesReservations(), HttpStatus.OK);
    }



    @PutMapping("/valider")
    @Operation(summary="Valider une demande de reservation")
    public ResponseEntity<String> validerDemandeReservation(@RequestBody DemandeReservation demandeReservation) {
        demandeReservationService.validerDemandeReservation(demandeReservation);
        return  new ResponseEntity<>("Demande de deservation faite avec succès", HttpStatus.OK); 
    }

    @PutMapping("/annuler")
    @Operation(summary="Annuler une demande de reservation")
    public ResponseEntity<String> annulerDemandeReservation(@RequestBody DemandeReservation demandeReservation) {
        demandeReservationService.annulerDemandeReservation(demandeReservation);
        return  new ResponseEntity<>("Demande de reservation annuler avec succès",HttpStatus.OK); 
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary="Supprimé de Transaction")
    public ResponseEntity<Void> deleteDemandeReservation(@PathVariable("id") String id) {
        demandeReservationService.deleteDemandeReservation(id);
        return  new ResponseEntity<>(HttpStatus.OK); 
    }
    
}

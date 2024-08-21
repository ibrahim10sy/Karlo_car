package projet.karlo.controller;

import java.util.List;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import projet.karlo.model.Reservation;
import projet.karlo.model.Vente;
import projet.karlo.service.VenteService;

@RestController
@RequestMapping("/vente")
public class VenteController {

    @Autowired
    VenteService venteService;

     @PostMapping("/addVente")
    @Operation(summary = "Ajout d'une vente")
    public ResponseEntity<Vente> createVente(
            @Valid @RequestParam("vente") String venteString,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
            throws Exception {
        Vente vente = new Vente();
        try {
            vente = new JsonMapper().readValue(venteString, Vente.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }
    
        Vente savedVente = venteService.createVente(vente, imageFiles);
        System.out.println(" controller :" + savedVente);
    
        return new ResponseEntity<>(savedVente, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Modification d'une vente")
    public ResponseEntity<Vente> updateVente(
            @Valid @RequestParam("vente") String venteString,
            @PathVariable String id,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
            throws Exception {
        Vente vente = new Vente();
        try {
            vente = new JsonMapper().readValue(venteString, Vente.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }
    
        Vente savedVente = venteService.updateVente(vente, id, imageFiles);
        System.out.println(" controller :" + savedVente);
    
        return new ResponseEntity<>(savedVente, HttpStatus.OK);
    }

     @GetMapping("/getAllVente")
     @Operation(summary="Liste de tout les ventes")
      public ResponseEntity<List<Vente>> getAllVente(){
                return new ResponseEntity<>(venteService.getAllVente(),HttpStatus.OK);
    }

     @GetMapping("/getAllVenteByClient/{nomClient}")
     @Operation(summary="Liste de tout les ventes par client")
      public ResponseEntity<List<Vente>> getAllVenteByClt(@PathVariable("nomClient") String nomClient){
                return new ResponseEntity<>(venteService.getAllVenteByClient(nomClient),HttpStatus.OK);
    }

     @GetMapping("/totalVenteParMoi")
    public java.util.Map<String, Long> getTotalSalesByMonth() {
        return venteService.getTotalSalesByMonth();
    }


    @GetMapping("/totalVoitureVendu")
    public Long getTotalSales() {
        return venteService.getTotalSales();
    }
   
        @DeleteMapping("/delete/{id}")
        @Operation(summary="Suppression d'une vente de voiture")
   public ResponseEntity<Void> deleteVente(@PathVariable("id") String id) {
       venteService.deleteVente(id);
       return  new ResponseEntity<>(HttpStatus.OK); 
   }
    
}

package projet.karlo.controller;

import java.util.List;

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
import projet.karlo.model.User;
import projet.karlo.model.VoitureLouer;
import projet.karlo.service.VoitureLouerService;

@RestController
@RequestMapping("/voitureLouer")
public class VoitureLouerController {

    @Autowired
    VoitureLouerService voitureServices;

    @PostMapping("/addVoiture")
    @Operation(summary = "création d'une voiture à vendre")
    public ResponseEntity<VoitureLouer> createVoiture(
            @Valid @RequestParam("voiture") String voitureString,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
            throws Exception {
        VoitureLouer voiture = new VoitureLouer();
        try {
            voiture = new JsonMapper().readValue(voitureString, VoitureLouer.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }
    
        VoitureLouer savedVoiture = voitureServices.createVoiture(voiture, imageFiles);
        System.out.println("Vendre controller :" + savedVoiture);
    
        return new ResponseEntity<>(savedVoiture, HttpStatus.CREATED);
    }
    
    @PutMapping("/update/{id}")
    @Operation(summary = "modification d'une voiture à vendre")
    public ResponseEntity<VoitureLouer> updateVoiture(
            @Valid @RequestParam("voiture") String voitureString,
            @PathVariable String id,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
            throws Exception {
        VoitureLouer voiture = new VoitureLouer();
        try {
            voiture = new JsonMapper().readValue(voitureString, VoitureLouer.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }

        VoitureLouer savedVoiture = voitureServices.updateVoiture(voiture, id, imageFiles);
        System.out.println("Vendre controller :" + savedVoiture);

        return new ResponseEntity<>(savedVoiture, HttpStatus.OK);
    }


       @PutMapping("/activer/{id}")
    @Operation(summary="Activation d'une voiture à louer mettre son statut à disponible")
    public ResponseEntity<VoitureLouer> activeVoitureLouer(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(voitureServices.active(id), HttpStatus.OK);
    }

    @PutMapping("/desactiver/{id}")
    @Operation(summary="Desactivation d'une voiture à louer mettre son statut à non disponible")
    public ResponseEntity<VoitureLouer> desactiveVoitureLouer(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(voitureServices.desactive(id), HttpStatus.OK);
    }

            @PutMapping("/updateView/{id}")
            @Operation(summary = "Update view")
            public ResponseEntity<VoitureLouer> updateViews(@PathVariable String id) throws Exception{
                return new ResponseEntity<>(voitureServices.updateNbViev(id), HttpStatus.OK);
            }

            @GetMapping("/getAllVoiture")
            @Operation(summary="Liste de toutes les Voitures")
            public ResponseEntity<List<VoitureLouer>> getAllVoitures(){
                return new ResponseEntity<>(voitureServices.getAllVoiture(),HttpStatus.OK);
            }

            @GetMapping("/getAllByMarque/{nom}")
            @Operation(summary="Liste de toutes les Voitures par marque")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByMarque(@PathVariable String nom){
                return new ResponseEntity<>(voitureServices.getAllVoitureByMarque(nom),HttpStatus.OK);
            }

            @GetMapping("/getAllByTypeBoite/{nom}")
            @Operation(summary="Liste de toutes les Voitures par type boite")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByTypeBoite(@PathVariable String nom){
                return new ResponseEntity<>(voitureServices.getAllVoitureByTypeBoite(nom),HttpStatus.OK);
            }

            @GetMapping("/getAllVoitureLouerByUser/{idUser}")
            @Operation(summary="Liste de toutes les voitures à louer par utilisateur")
            public ResponseEntity<List<VoitureLouer>> getAllVoitureLouerByUser(@PathVariable String idUser){
                return new ResponseEntity<>(voitureServices.getAllVoitureLouerByUser(idUser),HttpStatus.OK);
            }


            @GetMapping("/searchVoituresLouer")
            public List<VoitureLouer> searchVoitures(
                    @RequestParam(required = false) String nomMarque,
                    @RequestParam(required = false) String nomTypeVoiture,
                    @RequestParam(required = false) String nomTypeReservoir,
                    @RequestParam(required = false) Integer prix
                    ) {
                return voitureServices.searchVoitures(nomMarque, nomTypeVoiture, nomTypeReservoir, prix);
            }


            @GetMapping("/getAllByTypeReservoir/{nom}")
            @Operation(summary="Liste de toutes les Voitures par type reservoir")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByTypeReservoir(@PathVariable String nom){
                return new ResponseEntity<>(voitureServices.getAllVoitureByTypeReservoir(nom),HttpStatus.OK);
            }

            @GetMapping("/getAllByTypeVoiture/{nom}")
            @Operation(summary="Liste de toutes les Voitures par type voiture")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByTypeVoiture(@PathVariable String nom){
                return new ResponseEntity<>(voitureServices.getAllVoitureByTypeVoiture(nom),HttpStatus.OK);
            }

            @GetMapping("/getAllByAnnee/{annee}")
            @Operation(summary="Liste de toutes les Voitures par année")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByAnne(@PathVariable String annee){
                return new ResponseEntity<>(voitureServices.getAllVoitureByAnnee(annee),HttpStatus.OK);
            }

            @GetMapping("/getAllByNbView")
            @Operation(summary="Liste de toutes les Voitures populaires")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresBYvIew(){
                return new ResponseEntity<>(voitureServices.getAllVoitureByNbreViews(),HttpStatus.OK);
            }

            @GetMapping("/getAllByPrixChere")
            @Operation(summary="Liste de toutes les Voitures les plus chères")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByCheres(){
                return new ResponseEntity<>(voitureServices.getAllVoitureByPrixAugmenter(),HttpStatus.OK);
            }

            @GetMapping("/getAllByPrixMoinsChere")
            @Operation(summary="Liste de toutes les Voitures les plus moins chères")
            public ResponseEntity<List<VoitureLouer>> getAllVoituresByMoinsCheres(){
                return new ResponseEntity<>(voitureServices.getAllVoitureByPrixAugmenterMoinsChere(),HttpStatus.OK);
            }


            @DeleteMapping("/delete/{id}")
            @Operation(summary="Supprimé de voiture")
            public ResponseEntity<Void> deleteVoitures(@PathVariable("id") String id) {
                voitureServices.deleteVoiture(id);
                return  new ResponseEntity<>(HttpStatus.OK);
            }


}

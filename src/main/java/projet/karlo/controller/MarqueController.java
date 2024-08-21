package projet.karlo.controller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import projet.karlo.model.Marque;
import projet.karlo.repository.MarqueRepository;
import projet.karlo.service.FileUpload;
import projet.karlo.service.MarquesService;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/marque")
public class MarqueController {

    @Autowired
    MarquesService marqueService;
    @Autowired
    MarqueRepository marqueRepository;
    @Autowired
    FileUpload fileUploade;

    @PostMapping("/addMarque")
    @Operation(summary = "création d'une marque de voiture")
    public ResponseEntity<Marque> createMarque(
            @Valid @RequestParam("marque") String MarqueString,
            @RequestParam(value = "image", required = false) MultipartFile imageFile)
            throws Exception {
                Marque marque = new Marque();
                try {
                    marque = new JsonMapper().readValue(MarqueString, Marque.class);
                } catch (JsonProcessingException e) {
                    throw new Exception(e.getMessage());
                }
        
                Marque savedMarque = marqueService.createMarque(marque, imageFile);
                System.out.println("Marque controller :" + savedMarque);

                return new ResponseEntity<>(savedMarque, HttpStatus.CREATED);
            }

    @PutMapping("/update/{id}")
    @Operation(summary = "Modification")
    public ResponseEntity<Marque> updateMarque(
            @Valid @RequestParam("marque") String MarqueString,
            @PathVariable String id,
            @RequestParam(value = "image", required = false) MultipartFile imageFile)
            throws Exception {
                Marque marque = new Marque();
                try {
                    marque = new JsonMapper().readValue(MarqueString, Marque.class);
                } catch (JsonProcessingException e) {
                    throw new Exception(e.getMessage());
                }
                Marque savedMarque = marqueService.updateMarque(marque, id, imageFile);

                return new ResponseEntity<>(savedMarque, HttpStatus.CREATED);
            }

    @GetMapping("/getAllMarque")
    @Operation(summary="Liste de tout les voiture")
    public ResponseEntity<List<Marque>> getAll(){
        return new ResponseEntity<>(marqueService.getAllMarque(), HttpStatus.OK);
    }


          @GetMapping("/{idMarque}/image")
            public ResponseEntity<byte[]> getImage(@PathVariable String idMarque) {
                try {
                    // Récupérer le nom de l'image associée a la marque
                    Marque marque = marqueRepository.findByIdMarque(idMarque);
                    if (marque == null || marque.getLogo() == null) {
                        return ResponseEntity.notFound().build();
                    }
            
                    String imageName = marque.getLogo();
            
                    // Récupérer l'image à partir du serveur FTP
                    byte[] imageBytes = fileUploade.getImageByName(imageName);
            
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
            
            private MediaType detectContentType(String imageName) {
                String[] parts = imageName.split("\\.");
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
                // Par défaut, retourner MediaType.APPLICATION_OCTET_STREAM
                return MediaType.APPLICATION_OCTET_STREAM;
            }


    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMarques(@PathVariable("id") String id) {
        marqueService.deleteMarque(id);
        return  new ResponseEntity<>(HttpStatus.OK); 
    }
}

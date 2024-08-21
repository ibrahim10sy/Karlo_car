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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import projet.karlo.model.TypeVoiture;
import projet.karlo.service.TypeVoitureService;

@RestController
@RequestMapping("/typeVoiture")
public class TypeVoitureController {
    
    @Autowired
    TypeVoitureService typeVoitureService;
    
    @PostMapping("/addTypeVoiture")
    @Operation(summary="Création d'un type de voiture")
    public ResponseEntity<TypeVoiture> createTypes(@RequestBody TypeVoiture typeVoiture) {
        return new ResponseEntity<>(typeVoitureService.createTypeVoiture(typeVoiture) , HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary="Modification")
    public ResponseEntity<TypeVoiture> updateTypeVoiture(@PathVariable String id, @RequestBody TypeVoiture typeVoiture) {
        return new ResponseEntity<>(typeVoitureService.updateType(typeVoiture, id), HttpStatus.OK);
    }

    @GetMapping("/getAllTypeVoiture")
    @Operation(summary="Liste de tout les types voiture")
    public ResponseEntity<List<TypeVoiture>> getAll(){
        return new ResponseEntity<>(typeVoitureService.getVoiture(), HttpStatus.OK);
    }

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTypeVoitures(@PathVariable("id") String id) {
        typeVoitureService.deleteType(id);
        return  new ResponseEntity<>(HttpStatus.OK);
    }
}

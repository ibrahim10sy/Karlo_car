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
import projet.karlo.model.Alerte;
import projet.karlo.model.Contact;
import projet.karlo.service.ContactService;
import projet.karlo.service.EmailService;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    ContactService contactService;

    @Autowired
    EmailService emailService;

    @PostMapping("/addContact")
    @Operation(summary="Création de contact")
    public ResponseEntity<Contact> createContact(@RequestBody Contact Contact) {
        System.out.println(Contact.toString());
        return new ResponseEntity<>(contactService.createContact(Contact) , HttpStatus.CREATED);
    }

    @Operation(summary = "Envoyer un mail")
@PostMapping("/sendMail")
public ResponseEntity<String> sendMail(@RequestBody Alerte alerte) {
    String status = emailService.sendSimpleMail(alerte);
    
    if (status.contains("Erreur")) {
        // Retourner un statut 500 en cas d'échec
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
    }

    return ResponseEntity.ok(status);  // Retourne 200 en cas de succès
}


    @PutMapping("/update/{id}")
    @Operation(summary="Modification")
    public ResponseEntity<Contact> updateContact(@PathVariable String id, @RequestBody Contact Contact) {
        return new ResponseEntity<>(contactService.updateContact(Contact, id), HttpStatus.OK);
    }

    @GetMapping("/getAllContact")
    @Operation(summary="Liste de tout les contacts")
    public ResponseEntity<List<Contact>> getAll(){
        return new ResponseEntity<>(contactService.getContacts(), HttpStatus.OK);
    }

    
    @DeleteMapping("/delete/{id}")
    @Operation(summary="Supprimé de Contact")
    public ResponseEntity<Void> deleteContacts(@PathVariable("id") String id) {
        contactService.deleteContact(id);
        return  new ResponseEntity<>(HttpStatus.OK); 
    }
}

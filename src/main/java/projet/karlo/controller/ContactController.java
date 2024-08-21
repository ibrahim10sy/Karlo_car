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
import projet.karlo.model.Contact;
import projet.karlo.service.ContactService;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    ContactService contactService;

    @PostMapping("/addContact")
    @Operation(summary="Création de contact")
    public ResponseEntity<Contact> createContact(@RequestBody Contact Contact) {
        System.out.println(Contact.toString());
        return new ResponseEntity<>(contactService.createContact(Contact) , HttpStatus.CREATED);
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

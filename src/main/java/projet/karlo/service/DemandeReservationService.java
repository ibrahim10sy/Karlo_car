package projet.karlo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import projet.karlo.model.Alerte;
import projet.karlo.model.DemandeReservation;
import projet.karlo.model.Transaction;
import projet.karlo.model.TypeTransaction;
import projet.karlo.model.User;
import projet.karlo.repository.DemandeReservationRepository;

@Service
public class DemandeReservationService {

    @Autowired
    private DemandeReservationRepository demandeReservationRepository;

    @Autowired
    HistoriqueService historiqueService;
    @Autowired
    IdGenerator idGenerator;
    @Autowired
    EmailService emailService;


     public DemandeReservation createDemandeReservation(DemandeReservation demandeReservation) {
        DemandeReservation demandeReservationExistant = demandeReservationRepository.findByIdDemandeReservation(demandeReservation.getIdDemandeReservation());
        if(demandeReservationExistant != null && demandeReservationExistant.getIsReserved()== true)
        throw  new IllegalStateException("La voiture " + demandeReservationExistant.getVoitureLouer().getMatricule() + " matricule " + demandeReservationExistant.getVoitureLouer().getMatricule() + " a déjà été reservé" );

        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        demandeReservation.setIdDemandeReservation(idGenerator.genererCode());
        demandeReservation.setIsReserved(false);
        String formattedDateTime = now.format(formatter);
        demandeReservation.setDateDajout(formattedDateTime);
        Alerte al = new Alerte("Une demande de reservation a été faite par le numéro " + demandeReservation.getTelephone(), "karlocarml@gmail.com");
        al.setId(idGenerator.genererCode());
        al.setDateAjout(formattedDateTime);
        emailService.sendSimpleMail(al);
        historiqueService.createHistoriques("Demande de reservation de la voiture" + demandeReservation.getVoitureLouer().getModele() + "matricule " + demandeReservation.getVoitureLouer().getMatricule());
        
        return demandeReservationRepository.save(demandeReservation);
     
    }

     public String validerDemandeReservation(DemandeReservation demandeReservations) {
        DemandeReservation demandeReservation = demandeReservationRepository.findByIdDemandeReservation(demandeReservations.getIdDemandeReservation());
         
         String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        if(demandeReservation != null && demandeReservation.getIsReserved()== true)
        throw  new IllegalStateException("La voiture " + demandeReservation.getVoitureLouer().getMatricule() + " matricule " + demandeReservation.getVoitureLouer().getMatricule() + " a déjà été reservé" );
        demandeReservation.setIsReserved(true);
        Alerte al = new Alerte("Une demande de reservation a été faite par le numéro " + demandeReservation.getTelephone(), "karlocarml@gmail.com");
        al.setId(idGenerator.genererCode());
        al.setDateAjout(formattedDateTime);
        emailService.sendSimpleMail(al);
        historiqueService.createHistoriques("Validation de la demande de reservation de la voiture" + demandeReservation.getVoitureLouer().getModele() + "matricule " + demandeReservation.getVoitureLouer().getMatricule());
        
         demandeReservationRepository.save(demandeReservation);
         return "Demande de reservation validée";
     
    }

     public String annulerDemandeReservation(DemandeReservation demandeReservations) {
        DemandeReservation demandeReservation = demandeReservationRepository.findByIdDemandeReservation(demandeReservations.getIdDemandeReservation());
         
         String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
       
        demandeReservation.setIsReserved(true);
        Alerte al = new Alerte("Votre demande de reservation   " + " pour la voiture " + demandeReservation.getVoitureLouer().getModele() + "matricule " + demandeReservation.getVoitureLouer().getMatricule() + " faite le "+ demandeReservation.getDateDajout() + " a été annulé", demandeReservation.getEmail());
        al.setId(idGenerator.genererCode());
        al.setDateAjout(formattedDateTime);
        demandeReservation.setIsReserved(false);
        emailService.sendSimpleMail(al);
        historiqueService.createHistoriques("Annulation de la demande de reservation de la voiture" + demandeReservation.getVoitureLouer().getModele() + "matricule " + demandeReservation.getVoitureLouer().getMatricule());
        
         demandeReservationRepository.save(demandeReservation);
         return "Demande de reservation annulée";
     
    }

    public List<DemandeReservation> getAllDemandesReservations() {
        List<DemandeReservation> demandeReservations = demandeReservationRepository.findAll();

        if(demandeReservations.isEmpty())
            throw new IllegalStateException("Aucune demande de reservation trouvée");
        
            demandeReservations.sort(Comparator.comparing(DemandeReservation::getDateDajout).reversed());
            return demandeReservations;
    }
    
    


    public String deleteDemandeReservation(String id){
        DemandeReservation d = demandeReservationRepository.findById(id).orElseThrow();
        historiqueService.createHistoriques("Suppression de la demande de reservation de la voiture" + d.getVoitureLouer().getMatricule() + "matricule " + d.getVoitureLouer().getModele() + " demande par " + d.getTelephone());
        demandeReservationRepository.delete(d);
        return "Supprimé avec succèss"; 
    }
}

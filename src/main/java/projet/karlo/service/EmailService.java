package projet.karlo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import projet.karlo.model.Alerte;





@Service
public class EmailService {



     @Autowired private JavaMailSender javaMailSender;
     

    @Value("contact@karlocar.com") private String sender;
  

    
    public String sendSimpleMail(Alerte alerte) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(alerte.getEmail());
            mailMessage.setText(alerte.getMessage());
            mailMessage.setSubject(alerte.getSujet());

            javaMailSender.send(mailMessage);
            return "Email envoyé avec succès...";
        } catch (Exception e) {
            e.printStackTrace();  // Log de l'exception pour le débogage
            return "Erreur lors de l'envoi de l'email: " + e.getMessage();
        }
    }
    
    
}

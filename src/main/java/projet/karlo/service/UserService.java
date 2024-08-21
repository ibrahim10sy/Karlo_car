package projet.karlo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import projet.karlo.exception.NoContentException;
import projet.karlo.model.Alerte;
import projet.karlo.model.Role;
import projet.karlo.model.User;
import projet.karlo.repository.RoleRepository;
import projet.karlo.repository.UserRepository;

@Service
public class UserService {
      
    @Autowired
    UserRepository userRepository;
    @Autowired
    IdGenerator idGenerator ;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    HistoriqueService historiqueService;
    @Autowired
    RoleRepository roleRepository;


    @Autowired
    EmailService emailService;

    public User createUser(User user){
 
        User u = userRepository.findByEmail(user.getEmail());

        String passWordHasher = passwordEncoder.encode(user.getPassword());
        user.setPassword(passWordHasher);

        if(u != null)
            throw new IllegalStateException("Cet email existe déjà");
        
        String idcodes = idGenerator.genererCode();
        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        user.setIdUser(idcodes);
        user.setStatut(true);
        user.setDateAjout(formattedDateTime);
        user.setIsConnected(false);
        historiqueService.createHistorique("Ajout de l'utilisateur" + user.getNomUser() + " rôle " + user.getRole().getLibelle());
        return userRepository.save(user);
    }


    public User updateUser(User user, String id) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User non trouvé") );
        String passWordHasher = passwordEncoder.encode(user.getPassword());
        u.setPassword(passWordHasher);
        u.setNomUser(user.getNomUser());
        u.setAdresse(user.getAdresse());
        u.setEmail(user.getEmail());
        u.setTelephone(user.getTelephone());
        if(user.getRole() != null){
            u.setRole(user.getRole());
        }
        historiqueService.createHistorique("Modification de l'utilisateur" + u.getNomUser() + ", rôle " + u.getRole().getLibelle());
        return userRepository.save(u);
    }

    public List<User> getAllUser() {
        List<User> users = userRepository.findAll();
    
        if (users.isEmpty())
            throw new EntityNotFoundException("Aucune utilisateur trouvée");
    
        users.sort(Comparator.comparing(User::getDateAjout).reversed());
        
        return users;
    }
    

    public User updatePassWord(String id, String newPassWord) throws Exception {
        Optional<User> userOpt = userRepository.findById(id);
    
        if (userOpt.isPresent()) {
            User user = userOpt.get();
    
            // Hacher le nouveau mot de passe
            String hashedPassword = passwordEncoder.encode(newPassWord);
            user.setPassword(hashedPassword);
            return userRepository.save(user);
        } else {
            throw new Exception("User non trouvé avec l'ID : " + id);
        }
    }

    public String deleteUser(String idUser){
        User user = userRepository.findById(idUser).orElseThrow(() -> new IllegalStateException("User non trouvé") );

        if(user == null)
            throw new IllegalStateException("User not found");
            historiqueService.createHistorique("Suppression de l'utilisateur" + user.getNomUser() + ", rôle " + user.getRole().getLibelle());
        userRepository.delete(user);
        return "Utilisateur supprimé avec succèss";
    }


       public User connexionUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new EntityNotFoundException("Email ou mot de passe incorrect");
        }
    
        if (!user.getStatut()) {
            throw new IllegalArgumentException("Connexion échouée : votre compte est désactivé.");
        }
    
        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
    
        // Vérifiez si l'utilisateur est déjà connecté
        if (user.getIsConnected()) {
            // Envoyer une alerte de sécurité
            Alerte al = new Alerte(user.getEmail(), "Bonjour " +  user.getNomUser().toUpperCase() +" un utilisateur essaie de se connecter avec vos identifiants. Veuillez noter qu'on ne peut pas se connecter avec un compte sur 2 appareils différents. Nous vous conseillons de changer votre mot de passe dès que possible.", "Alerte de securité");
           emailService.sendSimpleMail(al);
            // Échouer la connexion
            throw new IllegalArgumentException("Connexion échouée : un appareil est déjà connecté avec ce compte.");
        }
        // if (user != null && user.getRole().getLibelle().toLowerCase().equals("user")) {
        //     // Échouer la connexion
        //     throw new IllegalArgumentException("Connexion échouée : juste les administrateurs sont autorisés à se connecter à ce panel.");
        // }
        
    
        // Mettre à jour l'état de connexion de l'utilisateur
        user.setIsConnected(true);
        userRepository.save(user);
    
        return user;
    }

      //Se connecter
    public User connexion(String email, String password){
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new EntityNotFoundException("Email ou mot de passe incorrect");
        }
        if(user.getStatut()==false){
            throw new NoContentException("Connexion échoué car votre compte  est desactivé ");
        }
        historiqueService.createHistorique("Authentification de  " + user.getNomUser() + ", rôle " + user.getRole().getLibelle());
        return user;
        }


        public boolean authenticate(String email, String password) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return passwordEncoder.matches(password, user.getPassword());
            }
            return false;
        }
    
        public User findByEmail(String email) {
            return userRepository.findByEmail(email);
        }


        public void logoutUser(String idUser) {
            User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            if (!user.getIsConnected()) {
                throw new IllegalStateException("L'utilisateur n'est pas actuellement connecté.");
            }
            user.setIsConnected(false);
            historiqueService.createHistorique("Déconnexion de  " + user.getNomUser() + ", rôle " + user.getRole().getLibelle());
            userRepository.save(user);
        }



        public User active(String id) throws Exception{
            User user = userRepository.findById(id).orElseThrow(null);
    
            try {
                user.setStatut(true);
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'activation du User: " + e.getMessage());
            }
            historiqueService.createHistorique("Activation de l'utilisateur" + user.getNomUser() + " rôle " + user.getRole().getLibelle());
            return userRepository.save(user);
        }
    
        public User desactive(String id) throws Exception{
            User user = userRepository.findById(id).orElseThrow(null);
    
            try {
                user.setStatut(false);
            } catch (Exception e) {
                throw new Exception("Erreur lors de la desactivation du User : " + e.getMessage());
            }
            historiqueService.createHistorique("Désactivation de l'utilisateur" + user.getNomUser() + " rôle " + user.getRole().getLibelle());
            return userRepository.save(user);
        }
    
}

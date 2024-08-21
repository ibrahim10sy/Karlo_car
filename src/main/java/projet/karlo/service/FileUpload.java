package projet.karlo.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.ByteArrayOutputStream;

// import org.apache.commons.net.ftp.FTP;
// import org.apache.commons.net.ftp.FTPClient;


@Service
public class FileUpload {

      private static final String FTP_SERVER = "185.194.216.57";
    private static final int FTP_PORT = 22; // Mise à jour si nécessaire
    private static final String FTP_USER = "karloftp";
    private static final String FTP_PASSWORD = "Coolschool2021";
    int retryCount = 3; 
    // private static final String FTP_IMAGES_DIRECTORY = "/images";
    
    @Async
    public String uploadImageToFTP(Path imagePath, String imageName) throws Exception {
        FTPClient ftpClient = new FTPClient();
        while (retryCount > 0) {
            try {
                ftpClient.connect(FTP_SERVER, FTP_PORT);
                ftpClient.login(FTP_USER, FTP_PASSWORD);
                ftpClient.enterLocalPassiveMode();
        
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
                try (InputStream inputStream = Files.newInputStream(imagePath)) {
                    String remoteFilePath = "upload/images/" + imageName; // Chemin d'acc                                                         ès complet sur le serveur FTP
                    boolean uploadResult = ftpClient.storeFile(remoteFilePath, inputStream);
                    if (uploadResult) {
                        return "ftp://" + FTP_USER + "@" + FTP_SERVER + remoteFilePath; // Retourne le lien complet de l'image en ligne
                    } else {
                        throw new Exception("Erreur lors du chargement de l'image sur le serveur FTP.");
                    }
                }
            } catch (IOException e) {
                throw new Exception("Erreur lors de la connexion au serveur FTP : " + e.getMessage());
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } 
                }
                throw new Exception("Échec du téléchargement du fichier après plusieurs tentatives.");
    }
      
    // Méthode pour récupérer une image à partir de son nom
      public byte[] getImageByName(String imageName) throws IOException {
        // Chemin où les images sont stockées sur le serveur FTP
        String imagePath = "upload/images/";
    
        // Télécharger l'image à partir du serveur FTP en utilisant son nom
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(FTP_SERVER, FTP_PORT);
                ftpClient.login(FTP_USER, FTP_PASSWORD);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    
                // Chemin d'accès complet de l'image sur le serveur FTP
                String remoteFilePath = imagePath + imageName;
    
                // Télécharger l'image depuis le serveur FTP
                if (ftpClient.retrieveFile(remoteFilePath, outputStream)) {
                    return outputStream.toByteArray(); // Retourner le tableau d'octets de l'image
                } else {
                    throw new IOException("Erreur lors du téléchargement de l'image depuis le serveur FTP.");
                }
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Méthode pour récupérer une image à partir de son nom
    public byte[] getImagesByName(List<String> imageName) throws IOException {
        // Chemin où les images sont stockées sur le serveur FTP
        String imagePath = "upload/images/";
    
        // Télécharger l'image à partir du serveur FTP en utilisant son nom
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(FTP_SERVER, FTP_PORT);
                ftpClient.login(FTP_USER, FTP_PASSWORD);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    
                // Chemin d'accès complet de l'image sur le serveur FTP
                String remoteFilePath = imagePath + imageName;
    
                // Télécharger l'image depuis le serveur FTP
                if (ftpClient.retrieveFile(remoteFilePath, outputStream)) {
                    return outputStream.toByteArray(); // Retourner le tableau d'octets de l'image
                } else {
                    throw new IOException("Erreur lors du téléchargement des images depuis le serveur FTP.");
                }
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}

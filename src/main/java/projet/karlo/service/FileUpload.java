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
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

// import org.apache.commons.net.ftp.FTP;
// import org.apache.commons.net.ftp.FTPClient;


@Service
public class FileUpload {


    int retryCount = 3; 
   
    private static final String SFTP_SERVER = "185.194.216.57";
    private static final int SFTP_PORT = 22;
    private static final String SFTP_USER = "karloftp";
    private static final String SFTP_PASSWORD = "Coolschool2021";

    // public String uploadImageToFTP(Path imagePath, String imageName) throws Exception {
    //     JSch jsch = new JSch();
    //     Session session = null;
    //     ChannelSftp channelSftp = null;

    //     try {
    //         // Initialisation de la session SFTP
    //         session = jsch.getSession(SFTP_USER, SFTP_SERVER, SFTP_PORT);
    //         session.setPassword(SFTP_PASSWORD);
    //         session.setConfig("StrictHostKeyChecking", "no");
    //         session.connect();

    //         channelSftp = (ChannelSftp) session.openChannel("sftp");
    //         channelSftp.connect();

    //         // Vérification et création du répertoire distant
    //         String remoteDir = "/home/karloftp/ftp/upload/images/";
    //         try {
    //             channelSftp.cd(remoteDir); // Changer de répertoire
    //         } catch (SftpException e) {
    //             channelSftp.mkdir(remoteDir); // Créer le répertoire s'il n'existe pas
    //             channelSftp.cd(remoteDir);
    //         }

    //         // Téléchargement du fichier
    //         try (InputStream inputStream = Files.newInputStream(imagePath)) {
    //             String remoteFilePath = remoteDir + imageName;
    //             channelSftp.put(inputStream, remoteFilePath);
    //             return "sftp://" + SFTP_USER + "@" + SFTP_SERVER + "/" + remoteFilePath;
    //         }
    //     } catch (Exception e) {
    //         throw new Exception("Erreur lors du téléchargement de l'image via SFTP : " + e.getMessage());
    //     } finally {
    //         if (channelSftp != null) {
    //             channelSftp.disconnect();
    //         }
    //         if (session != null) {
    //             session.disconnect();
    //         }
    //     }
    // }
    public String uploadImageToFTP(Path imagePath, String imageName) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
    
        if (imageName == null || imageName.isEmpty()) {
            throw new IllegalArgumentException("Le nom du fichier image ne peut pas être vide.");
        }
    
        try {
            // Initialisation de la session SFTP
            session = jsch.getSession(SFTP_USER, SFTP_SERVER, SFTP_PORT);
            session.setPassword(SFTP_PASSWORD);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
    
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
    
            // Vérification et création du répertoire distant
            String remoteDir = "/home/karloftp/ftp/upload/images/";
            try {
                channelSftp.cd(remoteDir);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    String[] dirs = remoteDir.split("/");
                    StringBuilder path = new StringBuilder();
                    for (String dir : dirs) {
                        if (dir.isEmpty()) continue;
                        path.append("/").append(dir);
                        try {
                            channelSftp.cd(path.toString());
                        } catch (SftpException ex) {
                            if (ex.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                                channelSftp.mkdir(path.toString());
                            } else {
                                throw new Exception("Erreur lors du changement ou de la création du répertoire distant : " + ex.getMessage(), ex);
                            }
                        }
                    }
                } else {
                    throw new Exception("Erreur lors du changement ou de la création du répertoire distant : " + e.getMessage(), e);
                }
            }
    
            // Téléchargement du fichier
            try (InputStream inputStream = Files.newInputStream(imagePath)) {
                String remoteFilePath = remoteDir + imageName;
                channelSftp.put(inputStream, remoteFilePath);
                return "sftp://" + SFTP_USER + "@" + SFTP_SERVER + "/" + remoteFilePath;
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors du téléchargement de l'image via SFTP : " + e.getMessage(), e);
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    

    // Méthode pour récupérer une image à partir de son nom    
    public byte[] getImagesByNames(List<String> imageNames) throws IOException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
        try {
            session = jsch.getSession(SFTP_USER, SFTP_SERVER, SFTP_PORT);
            session.setPassword(SFTP_PASSWORD);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
    
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
    
            // Parcourir chaque image et l'ajouter à l'outputStream
            for (String imageName : imageNames) {
                String imagePath = "/home/karloftp/ftp/upload/images/" + imageName;
                try (ByteArrayOutputStream tempStream = new ByteArrayOutputStream()) {
                    channelSftp.get(imagePath, tempStream);
                    outputStream.write(tempStream.toByteArray());
                } catch (Exception e) {
                    throw new IOException("Erreur lors du téléchargement de l'image '" + imageName + "' depuis le serveur SFTP : " + e.getMessage(), e);
                }
            }
    
            return outputStream.toByteArray();
    
        } catch (Exception e) {
            throw new IOException("Erreur lors de la connexion ou du téléchargement des images depuis le serveur SFTP : " + e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public byte[] getImageByName(String imageName) throws IOException {
        String imagePath = "/home/karloftp/ftp/upload/images/" + imageName;
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            session = jsch.getSession(SFTP_USER, SFTP_SERVER, SFTP_PORT);
            session.setPassword(SFTP_PASSWORD);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // Télécharger l'image depuis le serveur SFTP
            channelSftp.get(imagePath, outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new IOException("Erreur lors du téléchargement de l'image depuis le serveur SFTP : " + e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

}
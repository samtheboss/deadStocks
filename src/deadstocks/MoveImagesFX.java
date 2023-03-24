/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class MoveImagesFX extends Application {

    private TextField sourceFolderField;
    private TextField destinationFolderField;
    private ProgressBar progressBar;
    private Label progressLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        sourceFolderField = new TextField();
        destinationFolderField = new TextField();
        Button moveButton = new Button("Move Images");
        progressBar = new ProgressBar();
        progressLabel = new Label();

        HBox sourceBox = new HBox(new Label("Source Folder: "), sourceFolderField);
        sourceBox.setSpacing(10);
        sourceBox.setAlignment(Pos.CENTER_LEFT);

        HBox destinationBox = new HBox(new Label("Destination Folder: "), destinationFolderField);
        destinationBox.setSpacing(10);
        destinationBox.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(sourceBox, destinationBox, moveButton, progressBar, progressLabel);
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);

        moveButton.setOnAction(event -> getImaList(primaryStage));

        primaryStage.setScene(new Scene(root, 500, 250));
        primaryStage.setTitle("Move Images");
        primaryStage.show();
    }

    private void moveImages() {
        try {
            Connection conn = new dbConnection().connection();
            String imagename = "select image_name from item_images";

            PreparedStatement pst = conn.prepareStatement(imagename);
            ResultSet rs = pst.executeQuery();
            List<String> imageFiles = new ArrayList<>();

            while (rs.next()) {
                //   System.out.println("uuu");
                imageFiles.add(rs.getString("image_name"));

            }
            imageFiles.size();

            String sourceFolder = sourceFolderField.getText();
            String destinationFolder = destinationFolderField.getText();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int i = 0;
                    int total = imageFiles.size();
                    for (String imageFile : imageFiles) {
                        Path sourcePath = Paths.get(sourceFolder, imageFile);
                        Path destinationPath = Paths.get(destinationFolder, imageFile);
                        try {

                            //  sourceFolder =sourceFolder.replace("\", "/");
                            Files.move(sourcePath, destinationPath);
                            i++;
                            System.out.println(sourceFolder);
                            updateProgress(i, total);
                            updateMessage(String.format("Moved %d of %d images", i, total));
                        } catch (IOException e) {
                            System.out.println(e.getLocalizedMessage());
                            // updateMessage("Error moving image: " + e.getMessage());

                            // break;
                        }
                    }

                    return null;
                }
            };

            progressBar.progressProperty().bind(task.progressProperty());
            progressLabel.textProperty().bind(task.messageProperty());

            new Thread(task).start();
        } catch (SQLException ex) {
            Logger.getLogger(MoveImagesFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String> getImages() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        }
        File folder = fileChooser.getSelectedFile();
        File[] files = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        System.out.println(fileNames);
        return fileNames;
    }

    List<String> getImaList(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory == null) {
            
        }
        File[] files = selectedDirectory.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        System.out.println(fileNames);
        return fileNames;
    }
}

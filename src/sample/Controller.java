package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Controller {
    static String path;

    @FXML
    private VBox mainContainer;
    private List<Button> chooseMovies;

    public void openSpDialogue(ActionEvent a){
        Stage selectPathStage = new Stage();
        selectPathStage.initModality(Modality.APPLICATION_MODAL);
        selectPathStage.setTitle("Select Path");
        selectPathStage.setMinWidth(400);
        selectPathStage.setMinHeight(60);

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);

        TextField pathTextField = new TextField();
        Button button = new Button("Select a directory");

        button.setOnAction(event -> { File dir = directoryChooser.showDialog(selectPathStage);

            boolean update = false;

            if (dir != null) {
                pathTextField.setText(dir.getAbsolutePath());
                path = pathTextField.getText();
                update = true;
            } else {
                pathTextField.setText(null);
            }
            selectPathStage.close();

            if(update) {
                getMovies();
            }
        });

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(pathTextField, button);

        Scene scene = new Scene(root, 400, 60);

        selectPathStage.setTitle("Choose Directory");
        selectPathStage.setScene(scene);
        selectPathStage.show();
    }

    public void getMovies() {
        mainContainer.getChildren().clear();

        Path chooseFromPath = Paths.get(path);
        chooseMovies = new ArrayList<>();
        try (Stream<Path> subPaths = Files.walk(chooseFromPath)) {
            subPaths.forEach(a -> {
                if(a.toString().contains(".mp4") || a.toString().contains(".avi") ||
                   a.toString().contains(".flv") || a.toString().contains(".mkv") ||
                   a.toString().contains(".webm") || a.toString().contains(".wav")) {

                    String string = a.toString();
                    String[] parts = string.split(Pattern.quote("\\"));

                    Button button = new Button(parts[parts.length - 1]);
                    button.setStyle("-fx-background-color: #C468F4; ");

                    Label temp = new Label();

                    button.setOnAction(event -> {
                        try {
                            mediaPlayer mPObject = new mediaPlayer();
                            mPObject.path = a.toString();
                            BorderPane bP = FXMLLoader.load(getClass().getResource("mediaPlayer.fxml"));
                            mainContainer.getChildren().setAll(bP);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    chooseMovies.add(button);
                    mainContainer.getChildren().addAll(button, temp);
                }
            });
        } catch (IOException E) {}
    }

    private static void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public void openFbDialogue(ActionEvent a) throws IOException {
        Stage fbStage = new Stage();
        fbStage.setTitle("Send your feedback to any of the following e-mails!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(getClass().getResource("feedbackLinks.fxml").openStream());
        fbStage.setScene(new Scene(root,450,174));
        fbStage.showAndWait();
    }

    public void openAbtDialogue(ActionEvent a) throws IOException {
        Stage abtStage = new Stage();
        abtStage.setTitle("About ILAP Player");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(getClass().getResource("aboutProject.fxml").openStream());
        abtStage.setScene(new Scene(root, 700, 300));
        abtStage.showAndWait();
    }

    public void exitPlayer(ActionEvent a) throws Exception {
        System.exit(0);
    }
}
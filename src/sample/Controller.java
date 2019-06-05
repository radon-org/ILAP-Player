package sample;

import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Controller {
    @FXML
    private VBox mainContainer;
    private List<Button> chooseMovies;
    static String path;
    private DirectMediaPlayerComponent mp;

    /*
    On Action for Set Path
     */
    public void openSpDialogue(ActionEvent a){
        Stage selectPathStage = new Stage();
        selectPathStage.initModality(Modality.APPLICATION_MODAL);

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);

        TextField pathTextField = new TextField();

        /*
        Opens JavaFX Directory Chooser
         */
        File dir = directoryChooser.showDialog(selectPathStage);

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
    }

    public void getMovies() {
        mainContainer.getChildren().clear();

        /*
        Traverse all the files in the given path and initialize an ArrayList to store the path of all those files
         */
        Path chooseFromPath = Paths.get(path);
        chooseMovies = new ArrayList<>();

        try (Stream<Path> subPaths = Files.walk(chooseFromPath)) {
            subPaths.forEach(a -> {

                /*
                Check for the files with the given extensions only
                 */
                if(a.toString().contains(".mp4") || a.toString().contains(".avi") ||
                   a.toString().contains(".flv") || a.toString().contains(".mkv") ||
                   a.toString().contains(".webm") || a.toString().contains(".wav")) {

                    /*
                    Separate the file name from the path name using split method
                     */
                    String string = a.toString();
                    String[] parts = string.split(Pattern.quote("\\"));

                    Button button = new Button(parts[parts.length - 1]);
                    button.setStyle("-fx-background-color: #C468F4; ");

                    Label temp = new Label();

                    button.setOnAction(event -> {
                        try {
                            /*
                            Load VLC
                             */
                            NativeLibrary.addSearchPath("libvlc", "lib");
                            final Canvas canvas = new Canvas(1377, 768);
                            BorderPane borderPane = new BorderPane();
                            borderPane.setCenter(canvas);

                            final PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
                            final WritablePixelFormat<ByteBuffer> byteBgraInstance = PixelFormat.getByteBgraInstance();

                            mp = new DirectMediaPlayerComponent("RV32", 1377, 768, 1377*4) {
                                @Override
                                public void display(Memory nativeBuffer) {
                                    ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                                    pixelWriter.setPixels(0, 0, 1377, 768, byteBgraInstance, byteBuffer, 1377*4);
                                }
                            };

                            mp.getMediaPlayer().playMedia(a.toString());

                            Button playButton = new Button("Play");
                            Button pauseButton = new Button("Pause");
                            Button skipButton = new Button("Skip 30");
                            Button backButton = new Button("Back 30");

                            /*
                            On Action for playback buttons
                             */
                            playButton.setOnAction(event1 -> {
                                mp.getMediaPlayer().play();
                            });

                            pauseButton.setOnAction(event1 -> {
                                mp.getMediaPlayer().pause();
                            });

                            skipButton.setOnAction(event1 -> {
                                mp.getMediaPlayer().skip(30000);
                            });

                            backButton.setOnAction(event1 -> {
                                mp.getMediaPlayer().skip(-30000);
                            });

                            HBox playbackButtons = new HBox();
                            playbackButtons.setAlignment(Pos.CENTER);
                            playbackButtons.getChildren().addAll(backButton, playButton, pauseButton, skipButton);

                            mainContainer.getChildren().setAll(borderPane, playbackButtons);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    /*
                    Add movies, followed by labels in succession
                     */
                    chooseMovies.add(button);
                    mainContainer.getChildren().addAll(button, temp);
                }
            });
        } catch (IOException E) {}
    }

    /*
    Set initial directory for directory chooser
     */
    private static void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    /*
    On Action for Feedback
     */
    public void openFbDialogue(ActionEvent a) throws IOException {
        Stage fbStage = new Stage();
        fbStage.setTitle("Send your feedback to any of the following e-mails!");

        /*
        Load feedbackLinks.fxml
         */
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(getClass().getResource("feedbackLinks.fxml").openStream());
        fbStage.setScene(new Scene(root,450,174));
        fbStage.showAndWait();
    }

    /*
    On Action for About
     */
    public void openAbtDialogue(ActionEvent a) throws IOException {
        Stage abtStage = new Stage();
        abtStage.setTitle("About ILAP Player");

        /*
        Load aboutProject.fxml
         */
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(getClass().getResource("aboutProject.fxml").openStream());
        abtStage.setScene(new Scene(root, 700, 300));
        abtStage.showAndWait();
    }

    /*
    On Action for Exit
     */
    public void exitPlayer(ActionEvent a) throws Exception {
        System.exit(0);
    }
}
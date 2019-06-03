package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class mediaPlayer implements Initializable{
    @FXML
    private MediaView player;
    private MediaPlayer mP;
    private Media mE;
    static String path;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mE = new Media(new File(path).toURI().toString());
        mP = new MediaPlayer(mE);
        player.setMediaPlayer(mP);
        mP.setAutoPlay(true);
        DoubleProperty width = player.fitWidthProperty();
        DoubleProperty height = player.fitHeightProperty();
        width.bind(Bindings.selectDouble(player.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(player.sceneProperty(), "height"));
    }

    public void play(ActionEvent A) {
        mP.play();
    }
    public void pause(ActionEvent A) {
        mP.pause();
    }
    public void fast(ActionEvent A) {
        mP.setRate(2);
    }
    public void slow(ActionEvent A) {
        mP.setRate(0.5);
    }
    public void reload(ActionEvent A) {
        mP.seek(mP.getStartTime());
        mP.play();
    }
}

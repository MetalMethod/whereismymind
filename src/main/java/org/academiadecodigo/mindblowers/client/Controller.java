package org.academiadecodigo.mindblowers.client;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.academiadecodigo.mindblowers.constants.Constants;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Controller implements Initializable {

    @FXML
    private Button btn1;
    private Session session;


    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //TODO remove
        //  btn1.setLayoutX(Math.random() * 780);
        // btn1.setLayoutY(Math.random() * 555);
        btn1.setStyle("-fx-background-radius: 5em;");

        // Button fading
        fading(btn1);
    }

    @FXML
    void onMouseClick(MouseEvent event) {
        btn1.setLayoutX(Math.random() * Constants.MAX_BUTTON_X);
        btn1.setLayoutY(Math.random() * Constants.MAX_BUTTON_Y);
    }

    private void fading(Button btn) {
        FadeTransition fadeIn = createFadeIn(btn1);
        FadeTransition fadeOut = createFadeOut(btn1);

        SequentialTransition fade = new SequentialTransition(
                btn1,
                fadeIn,
                fadeOut
        );
        fade.play();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        addListener();
    }

    private void addListener() {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                try {
                    Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    session = new Session(socket);
                } catch (IOException e) {
                    //TODO notify user
                    e.printStackTrace();
                }
            }

        });
    }

    private FadeTransition createFadeIn(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        return fade;
    }

    private FadeTransition createFadeOut(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);

        return fade;
    }
}

package org.academiadecodigo.mindblowers.server;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.academiadecodigo.mindblowers.constants.Constants;
import org.academiadecodigo.mindblowers.constants.Messages;

import java.io.*;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Game implements Runnable {

    private Socket[] players;
    private BufferedReader[] bufferedReaders;
    private PrintWriter[] printerWriters;
    private boolean isGameOver;
    private int connectedPlayers;
    private String teamName;
    private int score;
    private ObjectProperty<Integer> pressed = new SimpleObjectProperty<>();
    private ChangeListener<Integer> pressedListener = new ChangeListener<Integer>() {
        @Override
        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            if (observable.getValue().equals(2)) {
                write(Messages.EGO, Messages.PLAYERS_READY);
                write(Messages.ALTEREGO, Messages.PLAYERS_READY);
            }
        }
    };

    public Game(Socket[] players) {
        this.players = players;
        bufferedReaders = new BufferedReader[2];
        printerWriters = new PrintWriter[2];
        isGameOver = false;

        try {

            populateArrays();

        } catch (IOException e) {
            //TODO create exception
            e.printStackTrace();
        }

    }

    private void populateArrays() throws IOException {
        bufferedReaders[0] = new BufferedReader(new InputStreamReader(players[0].getInputStream()));
        bufferedReaders[1] = new BufferedReader(new InputStreamReader(players[1].getInputStream()));

        printerWriters[0] = new PrintWriter(players[0].getOutputStream(), true);
        printerWriters[1] = new PrintWriter(players[1].getOutputStream(), true);

    }

    @Override
    public void run() {
        new Thread(new PlayerHandler(this, bufferedReaders[0], Messages.EGO)).start();
        new Thread(new PlayerHandler(this, bufferedReaders[1], Messages.ALTEREGO)).start();

        write(Messages.EGO, Messages.EGO);
        write(Messages.ALTEREGO, Messages.ALTEREGO);
        write(Messages.EGO, Messages.GAME_START);
        write(Messages.ALTEREGO, Messages.GAME_START);
        pressed.addListener(pressedListener);


        generateBubbles();
    }

    public void generateBubbles() {

        for (int i = 0; i < Constants.MAX_BUBBLES; i++) {

            int x = (int) (Math.random() * Constants.MAX_BUTTON_X);
            int y = (int) (Math.random() * Constants.MAX_BUTTON_Y);

            String message = Messages.NEW_BUBBLE + " " + Messages.EGO + " " + x + " " + y;

            synchronized (PrintWriter.class) {
                write(Messages.EGO, message);
                write(Messages.ALTEREGO, message);

            }
        }

        for (int i = 0; i < Constants.MAX_BUBBLES; i++) {

            int x = (int) (Math.random() * Constants.MAX_BUTTON_X);
            int y = (int) (Math.random() * Constants.MAX_BUTTON_Y);

            String message = Messages.NEW_BUBBLE + " " + Messages.ALTEREGO + " " + x + " " + y;

            synchronized (PrintWriter.class) {
                write(Messages.EGO, message);
                write(Messages.ALTEREGO, message);
            }
        }
    }

    public void write(String playerName, String message) {

        if (playerName.equals("ego")) {
            printerWriters[1].println(message);
            return;
        }

        printerWriters[0].println(message);

    }

    public void addPlayer() {
        connectedPlayers++;
        pressed.setValue(connectedPlayers);
    }

    public void addScore(String playerType, String nickname, int score) {
        if (teamName == null) {
            teamName = nickname;
            this.score = score;
            return;
        }
        teamName += " & " + nickname;
        this.score += score;
        new JdbcService().addScore(teamName, this.score);
    }
}

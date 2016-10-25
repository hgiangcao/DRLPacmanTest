/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RunGameByCNN;

import engine.pacman.controllers.HumanController;
import engine.pacman.game.Constants;
import engine.pacman.game.Constants.MOVE;
import engine.pacman.game.Game;
import engine.pacman.game.GameView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;

/**
 *
 * @author giang-rocker
 */
public class RunGameByCNN {

    public static void main(String[] args) throws IOException {
        int numOfGame = 0;
        int maxScore = 0;
        int maxTime = 0;
        ServerSocket s = new ServerSocket(22009);//22009
        Socket ss;
      //  FormRunCNN formRunCNN = new FormRunCNN();
     //   formRunCNN.setVisible(true);
        Game game = new Game(0);
        MOVE nextMove = MOVE.NEUTRAL;
        String stringMove = "";
        boolean visual = true;
        boolean wrongMove = false;

        float percentTranning = 0;

        PrintWriter pw;
        BufferedReader inFromPython;
        BufferedReader outToPython;
        try {
            ss = s.accept();
            pw = new PrintWriter(ss.getOutputStream(), true);

            System.out.println("Client python connected.. Waiting for Command");

        } finally {
        }

        while (true) {
            wrongMove = false;
            outToPython = new BufferedReader(new InputStreamReader(System.in));
            inFromPython = new BufferedReader(new InputStreamReader(ss.getInputStream()));
            int timeStep = 0;
            String command = inFromPython.readLine();

            if (command.indexOf("TRANNING") != -1) {
                percentTranning = Float.parseFloat(command.substring(("TRANNING").length()));
            //    formRunCNN.setValue(stringMove, game.getTotalTime(), game.getScore(), numOfGame, maxScore, maxTime, true, percentTranning);
                continue;
            } else if (command.equals("START_GAME")) {
                if (game.getScore() > maxScore) {
                    maxScore = game.getScore();
                }
                if (game.getTotalTime() > maxTime) {
                    maxTime = game.getTotalTime();
                }
                numOfGame++;
                game = new Game(0);

            } else {

                // if not start game, it should be a move for current Game
                String moveString = command;
                // convert to int
                int ret = Integer.parseInt(command);

                // convert to MOVE
                switch (ret) {
                    case 0:
                        nextMove = MOVE.DOWN;
                        stringMove = "DOWN";
                        break;
                    case 1:
                        nextMove = MOVE.LEFT;
                        stringMove = "LEFT";
                        break;
                    case 2:
                        nextMove = MOVE.UP;
                        stringMove = "UP";
                        break;
                    case 3:
                        nextMove = MOVE.RIGHT;
                        stringMove = "RIGHT";
                        break;
                    default:
                        nextMove = MOVE.NEUTRAL;
                        stringMove = "NEUTRAL";
                        break;
                }

                // advance
                // wrong move
                if (game.getNeighbour(game.getPacmanCurrentNodeIndex(), nextMove) == -1) {
                    pw.println("WRONG");
                    wrongMove = true;
                } else {
                    // simulate ghost move
                    Game simulatedGame = game.copy(false);
                    SimulateGhostMove ghostsMove = new SimulateGhostMove();
                    EnumMap<Constants.GHOST, MOVE> listGhostMove = new EnumMap<>(Constants.GHOST.class);
                    listGhostMove = ghostsMove.getMove(simulatedGame);
                    timeStep++;
                    game.advanceGame(nextMove, listGhostMove);
                }

            }

            // write game state to python
            if (command.indexOf("TRANNING") == -1) {
                if (game.gameOver()) {
                    pw.println("GAME_OVER" + game.getScore());

                } else if (!wrongMove) {

                    pw.println(game.getGameState());
                }
            }

         //   formRunCNN.setValue(stringMove, game.getTotalTime(), game.getScore(), numOfGame, maxScore, maxTime, game.gameOver(), percentTranning);

        }

    }

}

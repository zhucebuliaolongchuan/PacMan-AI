package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.util.EnumMap;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by chuanlong on 10/20/16.
 */

public class bfsPacMan extends Controller<MOVE> {
    public MOVE getMove(Game game, long timeDue) {
        int bestScore = game.getScore();
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
     //   Random rnd = new Random();
        MOVE bestMove = MOVE.NEUTRAL;
        //get possible moves that let pacman get away from the ghost
        for(Constants.GHOST ghost : Constants.GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
                bestMove = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost), Constants.DM.PATH);
            else
                bestMove = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost), Constants.DM.PATH);
       // MOVE bestMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
        for (int i = 0; i < possibleMoves.length; i++) {
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            Game copyGame = game.copy();
            MOVE pacmanMove = possibleMoves[i];
            int currentLife = copyGame.getPacmanNumberOfLivesRemaining();
            copyGame.advanceGame(pacmanMove, ghostMoves);
            int score = bfsPacMan(copyGame, 1000, currentLife);
            if (score > bestScore) {
                bestScore = score;
                bestMove = pacmanMove;
            }
        }
        return bestMove;

    }

    // bfsPacMan using Recursion
//    public int bfsPacMan(Game game, int depthLimited){
//        int bestScore = game.getScore();
//        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
//        for(int i = 0; i < possibleMoves.length; i++){
//            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
//            Game copyGame = game.copy();
//            MOVE pacmanMove = possibleMoves[i];
//            copyGame.advanceGame(pacmanMove, ghostMoves);
////            copyGame.updatePacMan(pacmanMove);
////            copyGame.updateGame();
//            int score = copyGame.getScore();
//            if(score > bestScore) bestScore = score;
//            if(depthLimited == 0 || copyGame.gameOver()){
//                if(depthLimited == 0) return bestScore;
//                if(copyGame.gameOver()) continue;
//            }
//            if(i == possibleMoves.length - 1){
//                depthLimited -= 1;
//                score = bfsPacMan(copyGame, depthLimited--);
//                if(score > bestScore) bestScore = score;
//            }
//        }
//        return bestScore;
//    }
// bfsPacMan using queue
    public int bfsPacMan(Game game, int depthLimited, int currentLife) {
        int remainingLives = game.getPacmanNumberOfLivesRemaining();
        if (game.gameOver() || remainingLives < currentLife || game.wasPacManEaten()) return 0;
        int bestScore = game.getScore();
        Queue<Game> queue = new ArrayBlockingQueue<Game>(1000);
        queue.add(game);
        while (queue.isEmpty() == false && depthLimited > 0) {
            Game openNode = queue.poll();
            MOVE[] possibleMoves = openNode.getPossibleMoves(openNode.getPacmanCurrentNodeIndex(), openNode.getPacmanLastMoveMade());
            for (int i = 0; i < possibleMoves.length; i++) {
                EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
                Game copyGame = openNode.copy();
                remainingLives = copyGame.getPacmanNumberOfLivesRemaining();
                MOVE pacmanMove = possibleMoves[i];
                copyGame.advanceGame(pacmanMove, ghostMoves);
//                queue.add(copyGame);
//                int score = copyGame.getScore();
//                if (score > bestScore) bestScore = score;
                if (copyGame.gameOver() || copyGame.getPacmanNumberOfLivesRemaining() < remainingLives || copyGame.wasPacManEaten()) {
                    break;
                } else {
                    queue.add(copyGame);
                    int score = copyGame.getScore();
                    if (score > bestScore) bestScore = score;
                }
            }
            depthLimited -= 1;
        }
        return bestScore;
    }
}

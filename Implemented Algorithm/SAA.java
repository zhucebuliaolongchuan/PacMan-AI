package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;
/**
 * Created by chuanlong on 10/21/16.
 */
/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class SAA extends Controller<MOVE> {

    public MOVE getMove(Game game, long timeDue) {
        int bestScore = game.getScore();
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        Random rnd = new Random();
        MOVE bestMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
        for (int i = 0; i < possibleMoves.length; i++) {
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            Game copyGame = game.copy();
            MOVE pacmanMove = possibleMoves[i];
            copyGame.advanceGame(pacmanMove, ghostMoves);
            int score = SimulatedAnnealing(copyGame);
            if (score > bestScore) {
                bestScore = score;
                bestMove = pacmanMove;
            }
        }
        return bestMove;
    }

    public int SimulatedAnnealing(Game game){
        int bestScore = game.getScore();
        EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
        Stack<Game> stack = new Stack<Game>();
        stack.push(game);
        while(stack.isEmpty() == false){
            Game current = stack.pop();
            MOVE[] possibleMoves = current.getPossibleMoves(current.getPacmanCurrentNodeIndex(), current.getPacmanLastMoveMade());
            for(int i = 0; i < possibleMoves.length; i++){
                MOVE pacmanMove = possibleMoves[i];
                Game neighbor = current.copy();
                neighbor.advanceGame(pacmanMove, ghostMoves);
                stack.push(neighbor);
                int score = neighbor.getScore();
                int increment = score - bestScore;
                int currentTime = neighbor.getTotalTime();
                double exponent = Math.abs(increment) / currentTime;
                if(neighbor.gameOver() == false){
                    if(increment > 0){
                        bestScore = score;
                        continue;
                    }else if(Math.pow(2.7182, exponent) > 1){      //transfer to the other node with possibility
                        continue;
                    }else{
                        stack.pop();
                    }
                }else{
                    // you have to consider that even though the pacman will be eaten but with a higher score
                    stack.pop();
                }
            }
        }
        return bestScore;
    }
}
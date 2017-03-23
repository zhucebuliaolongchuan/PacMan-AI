package pacman.entries.pacman;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by chuanlong on 10/21/16.
 */
/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class HillClimbingPacMan extends Controller<Constants.MOVE> {
    public MOVE getMove(Game game, long timeDue) {
        int bestScore = game.getScore();
        Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        Random rnd = new Random();
        MOVE bestMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
        for (int i = 0; i < possibleMoves.length; i++) {
            int currentlife = game.getPacmanNumberOfLivesRemaining();
            EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            Game copyGame = game.copy();
            Constants.MOVE pacmanMove = possibleMoves[i];
            copyGame.advanceGame(pacmanMove, ghostMoves);
            int score = HillClimbingPacMan(copyGame, currentlife);
            if (score > bestScore) {
                bestScore = score;
                bestMove = pacmanMove;
            }
        }
        return bestMove;
    }
    //Hill Climbing using stack
    public int HillClimbingPacMan(Game game, int currentlife) {
        if(currentlife > game.getPacmanNumberOfLivesRemaining() || game.wasPacManEaten() || game.gameOver())
            return 0;
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
                if(neighbor.gameOver() == false){
                    int score = neighbor.getScore();
                    if(score <= bestScore){
                        stack.pop();
                        break;
                    }else{
                        bestScore = score;
                    }
                }else{
                    stack.pop();
                    }
                }
        }
        return bestScore;
    }
}
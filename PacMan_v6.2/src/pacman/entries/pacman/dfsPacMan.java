package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.util.EnumMap;
import java.util.Random;
import java.util.ArrayList;

/**
 * Created by chuanlong on 10/20/16.
 */
public class dfsPacMan  extends Controller<MOVE>
{
    public MOVE getMove(Game game, long timeDue)
    {

        int bestScore = Integer.MIN_VALUE;
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(),game.getPacmanLastMoveMade());
        Random rnd = new Random();
        MOVE bestMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
        for(int i = 0; i < possibleMoves.length; i++){
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            Game copyGame = game.copy();
            MOVE pacmanMove = possibleMoves[i];
            copyGame.advanceGame(pacmanMove, ghostMoves);
            int score = dfsPacMan(copyGame);
            if(score > bestScore) {
                bestScore = score;
                bestMove = pacmanMove;
            }
        }
        return bestMove;

    }

    public int depthLimited = 100;
    public int dfsPacMan(Game game){
        int bestScore = Integer.MIN_VALUE;
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        for(int i = 0; i < possibleMoves.length; i++){
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            MOVE pacmanMove = possibleMoves[i];
            Game copyGame = game.copy();
            copyGame.advanceGame(pacmanMove, ghostMoves);
            if(depthLimited == 0 || copyGame.gameOver()){
                if(copyGame.gameOver()){
                    break;
                }
                if(depthLimited == 0){
                    depthLimited = 100;
                    int score = game.getScore();
                    if(score > bestScore){
                        bestScore = score;
                    }
                }
                break;
            }else{
                depthLimited -= 1;
                return dfsPacMan(copyGame);
            }
        }
        return bestScore;
    }


}

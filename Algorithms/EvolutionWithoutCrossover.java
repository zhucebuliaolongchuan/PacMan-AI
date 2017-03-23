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
public class EvolutionWithoutCrossover extends Controller<MOVE> {

    public MOVE getMove(Game game, long timeDue) {
        return EvolutionWithoutCrossover(game);
    }

    public MOVE EvolutionWithoutCrossover(Game game){
        Random rnd = new Random();
        ArrayList<MOVE> validMoveSequence = new ArrayList<MOVE>();
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        for(int i = 0; i < possibleMoves.length; i++){
            Game copyGame = game.copy();
            MOVE pacmanMove = possibleMoves[i];
            copyGame.updatePacMan(pacmanMove);
            copyGame.updateGame();
            validMoveSequence = getValidMoveSequence(copyGame);
            if(validMoveSequence != null) break;
        }
        //because the springs are too limited in this case, so when we get a valid move sequence, we choose it
        if(validMoveSequence != null){
            Mutate(validMoveSequence);
            return validMoveSequence.get(0);
        }else{
            return possibleMoves[rnd.nextInt(possibleMoves.length)];
        }
    }
    //mutate the valid move sequence
    public void Mutate(ArrayList<MOVE> moveSquence){
        Random rnd = new Random();
        //random pick two moves to mutate
        for(int i = 0; i < 2; i++)
            moveSquence.set(rnd.nextInt(moveSquence.size()), MOVE.NEUTRAL);
    }
    // this function pick up the valid move sequence, which is equals to the evaluation function in the evolution algorithm
    public ArrayList<MOVE> getValidMoveSequence(Game game){
        int currentScore = game.getScore();
        Game copyGame = game.copy();
        Random rnd = new Random();
        ArrayList<MOVE> moveSequence = new ArrayList<MOVE>();
        for(int i = 0; i < 20; i++){
            MOVE[] possibleMoves = copyGame.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
            MOVE pacmanMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            moveSequence.add(pacmanMove);
            copyGame.advanceGame(pacmanMove, ghostMoves);
        }
        int futureScore = copyGame.getScore();
        if(futureScore - currentScore >= 50){
            return moveSequence;
        }else{
            return null;
        }
    }

}
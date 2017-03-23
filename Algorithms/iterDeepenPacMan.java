package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class iterDeepenPacMan extends Controller<MOVE> {
    public MOVE getMove(Game game, long timeDue) {
        int bestScore = game.getScore();
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        Random rnd = new Random();
        MOVE bestMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
        for (int i = 0; i < possibleMoves.length; i++) {
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            Game copyGame = game.copy();
            MOVE pacmanMove = possibleMoves[i];
            int currentLife = copyGame.getPacmanNumberOfLivesRemaining();
            copyGame.advanceGame(pacmanMove, ghostMoves);
            int score = iterDeepenPacMan(copyGame, 50, 1000, currentLife);
            if (score > bestScore) {
                bestScore = score;
                bestMove = pacmanMove;
            }
        }
        return bestMove;
    }

    public int iterDeepenPacMan(Game game, int minDepth, int maxDepth, int currentLife) {
        int Depth = minDepth;
        int bestScore = game.getScore();
        int remainingLives = game.getPacmanNumberOfLivesRemaining();
        if (game.gameOver() || remainingLives < currentLife || game.wasPacManEaten()) return 0;
        while(maxDepth > 0) {
            Queue<Game> queue = new ArrayBlockingQueue<Game>(1000);
            queue.add(game);
            while (queue.isEmpty() == false && minDepth > 0) {
                Game openNode = queue.poll();
                MOVE[] possibleMoves = openNode.getPossibleMoves(openNode.getPacmanCurrentNodeIndex(), openNode.getPacmanLastMoveMade());
                for (int i = 0; i < possibleMoves.length; i++) {
                    EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
                    Game copyGame = openNode.copy();
                    remainingLives = copyGame.getPacmanNumberOfLivesRemaining();
                    MOVE pacmanMove = possibleMoves[i];
                    copyGame.advanceGame(pacmanMove, ghostMoves);
                    if (copyGame.gameOver() || copyGame.getPacmanNumberOfLivesRemaining() < remainingLives || copyGame.wasPacManEaten()) {
                        break;
                    } else {
                        queue.add(copyGame);
                        int score = copyGame.getScore();
                        if (score > bestScore) bestScore = score;
                    }
                }
                minDepth -= 1;
            }
            maxDepth = maxDepth - Depth;
            minDepth = Depth;
        }
        return bestScore;
    }

}
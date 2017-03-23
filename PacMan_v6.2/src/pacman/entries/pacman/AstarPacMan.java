package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import sun.tools.java.SyntaxError;

import java.util.*;

/**
 * Created by chuanlong on 10/21/16.
 */
/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class AstarPacMan extends Controller<MOVE> {

    public static Comparator<Game> scoreComparator = new Comparator<Game>() {
        @Override
        public int compare(Game c1, Game c2) {
            return c2.getScore() - c1.getScore();
        }
    };

    public MOVE getMove(Game game, long timeDue) {
        int bestScore = game.getScore();
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        Random rnd = new Random();
        MOVE bestMove = MOVE.NEUTRAL;
        //get possible moves that let pacman get away from the ghost
        for(Constants.GHOST ghost : Constants.GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
                bestMove = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost), Constants.DM.PATH);
            else
                bestMove = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost), Constants.DM.PATH);
//        MOVE bestMove = possibleMoves[rnd.nextInt(possibleMoves.length)];

        for (int i = 0; i < possibleMoves.length; i++) {
            EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
            int currentlife = game.getPacmanNumberOfLivesRemaining();
            Game copyGame = game.copy();
            MOVE pacmanMove = possibleMoves[i];
            copyGame.advanceGame(pacmanMove, ghostMoves);
            int score = aStarPacMan(copyGame, currentlife);
            if (score > bestScore) {
                bestScore = score;
                bestMove = pacmanMove;
            }
        }
        return bestMove;
    }

    public int aStarPacMan(Game game, int currentlife) {
        if (currentlife > game.getPacmanNumberOfLivesRemaining() || game.gameOver() || game.wasPacManEaten())
            return 0;
        Queue<Game> que = new PriorityQueue<Game>(1000, scoreComparator);
        que.add(game);
        int bestScore = game.getScore();
        int depthLimited = 5000;
        while (que.isEmpty() == false && depthLimited > 0) {
         //   System.out.print(que.size()+"\n");
            Game openNode = que.poll();
            que.clear();
            bestScore = openNode.getScore();
            MOVE[] possibleMoves = openNode.getPossibleMoves(openNode.getPacmanCurrentNodeIndex(), openNode.getPacmanLastMoveMade());
            for (int i = 0; i < possibleMoves.length; i++) {
                EnumMap<Constants.GHOST, MOVE> ghostMoves = new EnumMap<Constants.GHOST, MOVE>(Constants.GHOST.class);
                Game copyGame = openNode.copy();
             //   System.out.print(copyGame.getScore()+"\n");
                int remainingLives = copyGame.getPacmanNumberOfLivesRemaining();
                MOVE pacmanMove = possibleMoves[i];
                copyGame.advanceGame(pacmanMove, ghostMoves);
             //   que.add(copyGame);
                if(copyGame.gameOver() || copyGame.getPacmanNumberOfLivesRemaining() < remainingLives || copyGame.wasPacManEaten()){
                    break;
                }else{
                    que.add(copyGame);
                    int score = copyGame.getScore();
                    if (score > bestScore) bestScore = score;
                }

            }
            depthLimited -= 1;
        }
        return bestScore;
    }
}

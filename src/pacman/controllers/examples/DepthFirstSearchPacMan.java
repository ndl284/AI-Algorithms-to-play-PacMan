/**
 * @description: Implements Depth First Search as a controller for PacMan_v6.2.
 * NOTE: This is part of a comparative study on performance of algorithms as a game controller. The results of the test
 *  shows that this algorithm performs badly for the specified purpose. It takes too long to find the best move, and
 *  pacman gets stuck in the corner.
 * @author: Noel David Lobo
 * @date: 11/05/2016
 */
package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.PriorityQueue;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;
import pacman.game.util.Node;

/**
 * A simple depth first search for PacMan
 */
public class DepthFirstSearchPacMan extends Controller<MOVE> {
    private int times = 0;
    Controller<EnumMap<GHOST, MOVE>> ghostMoves;

    public DepthFirstSearchPacMan(Controller<EnumMap<GHOST, MOVE>> ghostMoves) {
        this.ghostMoves = ghostMoves;
    }

    public MOVE getMove(Game game, long timeDue) {
        PriorityQueue<Node> topStates = new PriorityQueue<>();
        Node root = new Node(game.copy(), null, null, 0,0);
        dfs(root, topStates);

        Node bestMove = topStates.remove();
        while (bestMove.depth != 1) {
            bestMove = bestMove.parent;
        }
        times = 0;
        return bestMove.move;
    }

    public void dfs(Node root, PriorityQueue<Node> topStates){
        if(times == 10){
            return;
        }
        times++;

        int index = root.game.getPacmanCurrentNodeIndex();

        for(MOVE move: root.game.getPossibleMoves(index)){
            Game copy = root.game.copy();

            for (int i = 0; i < 4; i++) {
                copy.advanceGame(move, ghostMoves.getMove(copy, -1));
            }

            Node child = new Node(copy, move, root, root.depth + 1,copy.getScore());
            topStates.add(child);
            dfs(child, topStates);
        }
    }

}

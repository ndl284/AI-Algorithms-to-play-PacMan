/**
 * @description: Implements Breadth First Search as a controller for PacMan_v6.2.
 * NOTE: This is part of a comparative study on performance of algorithms as a game controller. The results of the test
 *  shows that this algorithm performs badly for the specified purpose. It takes too long to find the best move, and
 *  pacman gets stuck in the corner.
 * @author: Noel David Lobo
 * @date: 11/05/2016
 */
package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.util.Node;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.PriorityQueue;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

public class BreadthFirstSearchPacMan extends Controller<MOVE> {
    Controller<EnumMap<GHOST, MOVE>> ghostMoves;

    public BreadthFirstSearchPacMan(Controller<EnumMap<GHOST, MOVE>> ghostMoves) {
        this.ghostMoves = ghostMoves;
    }

    public MOVE getMove(Game game, long timeDue) {
        Node root = new Node(game, null, null, 0, 0);

        ArrayList<Node> queue = new ArrayList<>();
        PriorityQueue<Node> topStates = new PriorityQueue<>();

        queue.add(root);
        while(!queue.isEmpty()) {
            Node n = queue.remove(0);

            if(n.depth+1 == 6)
                break;

            int index = n.game.getPacmanCurrentNodeIndex();
            for (MOVE move : n.game.getPossibleMoves(index)) {
                Node child = new Node(n.game.copy(), move, n, n.depth + 1, 0);
                child.game.advanceGame(MOVE.LEFT, ghostMoves.getMove());

                child.score = child.game.getScore();
                topStates.add(child);
                queue.add(child);   //adding it to the priority queue
            }
        }

        Node bestMove = topStates.remove();   //the first node in the queue will be the best one since it is sorted.
        while (bestMove.depth != 1) {
            bestMove = bestMove.parent;
        }

        return bestMove.move;
    }

}
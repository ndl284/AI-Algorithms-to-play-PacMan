/**
 * @description: Implements Iterative Deepening as a controller for PacMan_v6.2.
 * NOTE: This is part of a comparative study on performance of algorithms as a game controller. The results of the test
 *  shows that this algorithm performs badly for the specified purpose. It takes too long to find the best move, and
 *  pacman gets stuck in the corner.
 * @author: Noel David Lobo
 * @date: 11/05/2016
 */package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.util.Node;

import java.util.EnumMap;
import java.util.PriorityQueue;

import static pacman.game.Constants.*;

public class IterativeDeepening extends Controller<MOVE>{
    private int times = 1;
    private int iter_depth = 4;
    Controller<EnumMap<GHOST, MOVE>> ghostMoves;

    public IterativeDeepening(Controller<EnumMap<GHOST, MOVE>> ghostMoves) {
        this.ghostMoves = ghostMoves;
    }

    public MOVE getMove(Game game, long timeDue) {
        PriorityQueue<Node> topStates = new PriorityQueue<>();
        Node root = new Node(game.copy(), null, null, 0,0);

        while(times != iter_depth) {
            dfs(root, topStates);
            times++;
        }

        Node bestMove = topStates.remove();
        while (bestMove.depth != 1) {
            bestMove = bestMove.parent;
        }
        times = 0;
        return bestMove.move;
    }

    public void dfs(Node root, PriorityQueue<Node> topStates){
        if(root.depth+1 > times)
            return;

        int index = root.game.getPacmanCurrentNodeIndex();

        for(MOVE move: root.game.getPossibleMoves(index)){
            Game copy = root.game.copy();

            for (int i = 0; i < 4; i++) {
                copy.advanceGame(MOVE.LEFT, ghostMoves.getMove(copy.copy(), -1));
            }

            Node child = new Node(copy, MOVE.LEFT, root, root.depth + 1,copy.getScore());
            topStates.add(child);
            dfs(child, topStates);
        }
    }
}


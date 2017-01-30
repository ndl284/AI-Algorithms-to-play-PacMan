/*
*  @description: A test version of A Star for the pacman controller. NOTE: Need to select a better heuristic than the current one.
*  @author: Noel David Lobo
*  @date: 11/05/2016 
*/
package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.PriorityQueue;
import pacman.game.util.Node;
import pacman.game.Constants.MOVE;

public class AStarSearchPacMan extends Controller<Constants.MOVE> {
    private static Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostMoves;
    private static int MAX_DISTANCE_LIMIT = 15;  // the maximum distance at which we have away from the ghost
    public static int itr = 0;
    public static PriorityQueue<NodeStar> minQueue = new PriorityQueue<>();
    public static ArrayList<NodeStar> queue = new ArrayList<>();

    public AStarSearchPacMan(Controller<EnumMap<Constants.GHOST, MOVE>> ghostMoves) {
        this.ghostMoves = ghostMoves;
    }

    public Constants.MOVE getMove(Game game, long timeDue) {
        return bestMoveByAStar(game);
    }

    public Constants.MOVE bestMoveByAStar(Game game) {
        // logic to find whether the edible ghost or non-edible ghost is near to the pac man.
        int currentPacmanLoc = game.getPacmanCurrentNodeIndex();

        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                int distance = game.getManhattanDistance(currentPacmanLoc, game.getGhostCurrentNodeIndex(ghost));
                if (distance < MAX_DISTANCE_LIMIT) {
                    return game.getNextMoveAwayFromTarget(currentPacmanLoc, game.getGhostCurrentNodeIndex(ghost), Constants.DM.MANHATTAN);
                }
            }

        }
        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) > 0) {
                return game.getNextMoveTowardsTarget(currentPacmanLoc, game.getGhostCurrentNodeIndex(ghost), Constants.DM.MANHATTAN);
            }
        }
        //return null;
        Constants.MOVE bestMove = bestMoveFunction(game);
        return bestMove;
    }

    private Constants.MOVE bestMoveFunction(Game game) {

        NodeStar root = new NodeStar(game.copy(), null, null, 0, 0);
        queue.add(root);

        for (int i = 0; i < 20; i++) {
            NodeStar n = queue.get(i);
            int index = n.game.getPacmanCurrentNodeIndex();
            for (Constants.MOVE move : MOVE.values()) {
                Game newCopy = n.game.copy();
                for (int j = 0; j < 4; j++) {
                    newCopy.advanceGame(move, ghostMoves.getMove(newCopy.copy(),-1));
                }
                NodeStar newState = new NodeStar(newCopy, move, n, n.depth+1, 0);
                double costValue = eval(newState);
                newState.score = (int) costValue;
                minQueue.add(newState);
                queue.add(newState);
            }
        }

        NodeStar temp = minQueue.remove();
        while(temp.depth!=1){
            temp = temp.parent;
        }
        minQueue.clear();
        return temp.move;
    }

    private double eval(NodeStar node){
        double cost = 0;
        int index = node.game.getPacmanCurrentNodeIndex();
        double bestValue = Integer.MAX_VALUE;
        int tracker = 0;

        int[] active = node.game.getActivePillsIndices();
        for(int pos : active){
            double dist = node.game.getEuclideanDistance(index, active[tracker]);
            bestValue = dist<bestValue ? dist : bestValue;
        }
        cost+=bestValue;
        bestValue = Integer.MAX_VALUE;
        tracker = 0;

        active = node.game.getActivePowerPillsIndices();
        for(int pos : active){
            double dist = node.game.getEuclideanDistance(index, active[tracker]);
            bestValue = dist<bestValue ? dist : bestValue;
        }
        cost+=bestValue;
        bestValue = 0;

        cost+=node.game.getScore();

        int best = 0;
        for (Constants.GHOST currentGhost : Constants.GHOST.values()) {
            if (node.game.getGhostLairTime(currentGhost) == 0 && node.game.getGhostEdibleTime(currentGhost) == 0) {
                double dist = node.game.getEuclideanDistance(index, node.game.getGhostCurrentNodeIndex(currentGhost));
                bestValue = bestValue>dist? dist : bestValue;
            }
        }
        cost += bestValue;
        bestValue = 0;

        bestValue = 0;
        for (Constants.GHOST currentGhost : Constants.GHOST.values()) {
            if (node.game.getGhostEdibleTime(currentGhost)>0) {
                double dist = node.game.getEuclideanDistance(index, node.game.getGhostCurrentNodeIndex(currentGhost));
                bestValue = bestValue>dist? dist : bestValue;
            }
        }
        cost+=bestValue;

        return cost;
    }
}

class NodeStar implements Comparable<NodeStar>{
    public Game game;
    public MOVE move;
    public NodeStar parent;
    public int depth;
    public int score;

    public NodeStar(Game game, MOVE move, NodeStar parent, int depth, int score) {
        this.game = game;
        this.move = move;
        this.parent = parent;
        this.depth = depth;
        this.score = score;
    }

    public int compareTo(NodeStar n) {
        return Integer.compare(this.score,n.score);
    }
}
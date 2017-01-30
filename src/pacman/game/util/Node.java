package pacman.game.util;

import pacman.game.Game;
import static pacman.game.Constants.MOVE;

public class Node implements Comparable<Node> {
    public Game game;
    public MOVE move;
    public Node parent;
    public int depth;
    public int score;

    public Node(Game game, MOVE move, Node parent, int depth, int score) {
        this.game = game;
        this.move = move;
        this.parent = parent;
        this.depth = depth;
        this.score = score;
    }

    public int compareTo(Node n) {
        return Integer.compare(n.score, this.score);
    }
}
package pacman.game.util;

import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;

/**
 * Created by noel on 10/25/16.
 */
public class Genome implements Comparable<Genome> {
    public Game game;
    public ArrayList<Constants.MOVE> genesequence;
    public float fitness;

    public Genome(Game game , ArrayList<Constants.MOVE> genesequence, int fitness){
        this.game = game;
        this.genesequence = genesequence;
        this.fitness = fitness;
    }

    public int compareTo(Genome other) {
        return Float.compare(this.fitness, other.fitness);
    }

}
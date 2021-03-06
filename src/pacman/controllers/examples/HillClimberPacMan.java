/**
 * @description: Implements Hill Climber as a controller for PacMan_v6.2.
 * NOTE: This is part of a comparative study on performance of algorithms as a game controller. Performs a little better
 *  than the search algorithms. It finds the optimum based on action sequences.
 * @author: Noel David Lobo
 * @date: 11/05/2016
 */
package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.util.Genome;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.PriorityQueue;
import java.util.Random;

public class HillClimberPacMan extends Controller<MOVE> {
    Controller<EnumMap<GHOST, MOVE>> ghostMoves;
    Genome optima;

    public HillClimberPacMan(Controller<EnumMap<GHOST, MOVE>> ghostMoves) {
        this.ghostMoves = ghostMoves;
    }

    public MOVE getMove(Game game, long timeDue) {


        PriorityQueue<Genome> neighbours = new PriorityQueue<>();

        Genome optimum = new Genome(game.copy(), getGenes(10), 0);
        optima = null;
        neighbours.add(optimum);

        while(true){
            Genome[] around = new Genome[10];
            int x = 0;
            Genome current = neighbours.remove();

            if(current == optima)
                break;
            optima = current;
            neighbours.clear();

            while(x<10){
                around[x] = getNeighbour(current);
                x++;
            }

            for(Genome g : around){
                for(MOVE m : g.genesequence)
                    g.game.advanceGame(m, ghostMoves.getMove());

                g.fitness = evaluateState(current,g);//getScore();
                neighbours.add(g);
            }
            neighbours.add(current);
        }

        return optima.genesequence.remove(0);
    }

    public ArrayList<MOVE> getGenes(int length) {
        ArrayList<MOVE> moves = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(5);
            moves.add(MOVE.values()[index]);
        }
        return moves;
    }

    public Genome getNeighbour(Genome g){
        Random random = new Random();
        int size = g.genesequence.size();
        int times = random.nextInt(4);
        Genome neighbour = new Genome(g.game.copy(), g.genesequence, 0);

        for(int i = 0; i< times; i++){
            int index = random.nextInt(size);
            int move = random.nextInt(4);

            neighbour.genesequence.add(index, MOVE.values()[move]);
        }

        return neighbour;
    }

    private float evaluateState(Genome prev, Genome next) {
        return Math.abs(prev.game.getScore() - next.game.getScore());
    }
}

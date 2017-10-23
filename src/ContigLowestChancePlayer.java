import java.util.ArrayList;

/**
 * Created by jonathanevans on 10/22/17.
 */
public class ContigLowestChancePlayer extends ContigHighestChancePlayer {

    public ContigLowestChancePlayer() {
        super();
    }

    /*
    Literally only changed the >= to a <=
     */
    public IndexPair findPlayersProbabilityMove(ArrayList<IndexPair> listOfTiedTopScores) {
        IndexPair mostProbable = listOfTiedTopScores.get(0);
        for (int i = 1; i < listOfTiedTopScores.size(); i++) {
            if (getProbability(listOfTiedTopScores.get(i).row, listOfTiedTopScores.get(i).column) <=
                    getProbability(mostProbable.row, mostProbable.column)) {
                mostProbable = listOfTiedTopScores.get(i);
            }
        }
        return mostProbable;
    }
}

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by jonathanevans on 10/22/17.
 */
public class ContigHighestChancePlayer extends ContigPlayer {

    protected double[] probArr;

    public ContigHighestChancePlayer() {
        super();
        name = "Contig Highest Chance Player";
        probArr = setProbArray();
    }

    public IndexPair findBestValidMove(ArrayList<IndexPair> bestMoves, HashSet<Integer> validMoves) {
        int counter = 0;

        //This generates a list that contains all the moves that are tied for first
        //If nothing is found in the first set, it moves on to the next until it finds a valid
        //move that is the highest score, and the highest probability
        while (counter < bestMoves.size()) {
            IndexPair firstPair = bestMoves.get(counter);
            ArrayList<IndexPair> listOfTiedTopScores = new ArrayList<>();
            counter += 1;
            if (validMoves.contains(Contig.gameBoard[firstPair.row][firstPair.column])) {
                listOfTiedTopScores.add(firstPair);
            }

            //Here we are adding each element to the list
            int findHighScores = counter;
            while (findHighScores < bestMoves.size() && bestMoves.get(findHighScores).curScorePotential == firstPair.curScorePotential) {
                if (validMoves.contains(Contig.gameBoard[bestMoves.get(findHighScores).row][bestMoves.get(findHighScores).column])) {
                    listOfTiedTopScores.add(bestMoves.get(findHighScores));
                }
                findHighScores += 1;
            }

            //This sends the list off to the method below to find the
            //move with the highest probability (since we know they all
            //have the same score potential
            if (listOfTiedTopScores.size() > 0) {
                return findPlayersProbabilityMove(listOfTiedTopScores);
            }
        }
        return new IndexPair(-1, -1);
    }

    /*
    This method goes through the list and simply finds the element with the highest probability.
    Similiar to a find max algorithm
     */
    public IndexPair findPlayersProbabilityMove(ArrayList<IndexPair> listOfTiedTopScores) {
        IndexPair mostProbable = listOfTiedTopScores.get(0);
        for (int i = 1; i < listOfTiedTopScores.size(); i++) {
            if (getProbability(listOfTiedTopScores.get(i).row, listOfTiedTopScores.get(i).column) >=
                    getProbability(mostProbable.row, mostProbable.column)) {
                mostProbable = listOfTiedTopScores.get(i);
            }
        }
        return mostProbable;
    }

    public double getProbability(int row, int column) {
        return probArr[Contig.gameBoard[row][column]];
    }

    public double[] setProbArray() {
        double[] curArr = new double[217];

        for (double i = 1; i < 7; i++) {
            for (double j = 1; j < 7; j++) {
                for (double k = 1; k < 7; k++) {
                    HashSet<Integer> validMoves = cleanUpListAndConvertToInt(getValidMoves(i , j, k));
                    Iterator vmIterator = validMoves.iterator();
                        while (vmIterator.hasNext()) {
                            Integer hold = (Integer) vmIterator.next();
                            curArr[hold] += 1.0;
                    }
                }
            }
        }
        for (int m = 0; m < 217; m++) {
            curArr[m] = curArr[m]/216;
        }
        return curArr;
    }

    /*
    This main method was used to make sure everything was working, I left it in so that the
    probability chart for Contig is easily visible by running this main.
     */
    public static void main(String[] args) {
        Contig c = new Contig();
        ContigHighestChancePlayer ch = new ContigHighestChancePlayer();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(String.format( "%.2f", ch.probArr[c.gameBoard[i][j]]) + " ");
            }
            System.out.println();
        }
    }
}

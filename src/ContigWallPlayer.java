import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by jonathanevans on 10/23/17.
 * This class breaks ties between the highest scoring moves by selecting spots
 * that line the walls of the board, limiting their opponents chance of
 * getting high scoring potential
 */
public class ContigWallPlayer extends ContigPlayer {

    public ContigWallPlayer() {
        super();
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
            while (bestMoves.get(findHighScores).curScorePotential == firstPair.curScorePotential) {
                if (validMoves.contains(Contig.gameBoard[bestMoves.get(findHighScores).row][bestMoves.get(findHighScores).column])) {
                    listOfTiedTopScores.add(bestMoves.get(findHighScores));
                }
                findHighScores += 1;
            }
            if (listOfTiedTopScores.size() > 0) {
                return getWall(listOfTiedTopScores);
            }
        }
        return new IndexPair(-1, -1, 0);
    }

    /*
    This method simply selects the first wall spot it finds or if there is none,
    it returns the first element of listOfTiedTopScores
     */
    public IndexPair getWall(ArrayList<IndexPair> listOfTiedTopScores) {
        for (int i = 0; i < listOfTiedTopScores.size(); i++) {
            IndexPair cur = listOfTiedTopScores.get(i);
            if (cur.row == 0 || cur.row == 7 || cur.column == 0 || cur.column == 7) {
                return cur;
            }
        }
        return listOfTiedTopScores.get(0);
    }
}

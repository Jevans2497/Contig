import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by jonathanevans on 10/13/17.
 */
public class ContigRandomPlayer extends ContigPlayer {

    public ContigRandomPlayer() {
        super();
        name = "Contig Random Player";
    }

    /*
    Previously had it find the highest scoring moves and randomly select from them, but instead
    now this just picks any random valid move. I left it the old version in in case of wanting to test it later
    or just to demonstrate my process.
     */
    @Override
    public IndexPair findBestValidMove(ArrayList<IndexPair> bestMoves, HashSet<Integer> validMoves) {

        int num1 = dice.nextInt(217);

        while (findIndexByNumber(num1).row == -1 && !validMoves.contains(num1)) {
            num1 = (num1 + 1) % 217;
        }

        return findIndexByNumber(num1);
//
// int counter = 0;
//        boolean done = false;
//        while (!done) {
//            IndexPair firstPair = bestMoves.get(counter);
//            ArrayList<IndexPair> curRandomList = new ArrayList<>();
//            counter += 1;
//            if (validMoves.contains(Contig.gameBoard[firstPair.row][firstPair.column])) {
//                curRandomList.add(firstPair);
//            }
//            for (int j = counter; j < bestMoves.size(); j++) {
//                if (bestMoves.get(j).curScorePotential == firstPair.curScorePotential
//                        && validMoves.contains(Contig.gameBoard[bestMoves.get(j).row][bestMoves.get(j).column])) {
//                    counter += 1;
//                    curRandomList.add(bestMoves.get(j));
//                }
//            }
//            if (curRandomList.size() > 0) {
//                done = true;
//                return curRandomList.get(dice.nextInt(curRandomList.size()));
//            }
//        }
//        return new IndexPair(-1, -1);
    }
}

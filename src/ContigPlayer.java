import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by jonathanevans on 10/13/17.
 */
public class ContigPlayer {

    private int playerScore;
    protected Random dice;
    private boolean isFirstTurn;


    public ContigPlayer() {
        int playerScore = 0;
        dice = new Random();
        isFirstTurn = true;
    }

    /**
     * Each round, the computer will play a turn using this method which
     * first rolls the 3 die, then checks if it is the first turn that has a
     * required move, and then finally determines which moves is the best to play
     */
    public void playTurn() {
        double roll1 = (double)(dice.nextInt(6) + 1);
        double roll2 = (double)(dice.nextInt(6) + 1);
        double roll3 = (double)(dice.nextInt(6) + 1);

        System.out.println("Rolls: " + roll1 + " " + roll2 + " " + roll3);

        if (isFirstTurn) {
            playFirstTurn(roll1, roll2, roll3);
        } else {

            ArrayList<IndexPair> bestMovesList = getBestMoves();
            HashSet<Integer> validMovesList = cleanUpListAndConvertToInt(getValidMoves(roll1, roll2, roll3));

            IndexPair play = findBestValidMove(bestMovesList, validMovesList);
            playerScore += play.curScorePotential;
            if (play.row != -1) {
                Contig.gameBoard[play.row][play.column] = -1;
            }
        }
    }

    /*
    The first turn simply plays the addition of the three rolls
        Since this player moves first, they get two points as the other player
        gets the advantage of playing the last turn with the most chips on the board
     */
    private void playFirstTurn(double roll1, double roll2, double roll3) {
        double rollSum = roll1 + roll2 + roll3;
        IndexPair firstPair = findIndexByNumber((int)rollSum);
        Contig.gameBoard[firstPair.row][firstPair.column] = -1;
        playerScore+=2;
        isFirstTurn = false;
    }

    protected HashSet<Integer> cleanUpListAndConvertToInt(ArrayList<Double> list) {
        HashSet<Integer> cleanList = new HashSet<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) <= 216 && list.get(i) >= 1 && !cleanList.contains(list.get(i))) {
                if (Math.floor(list.get(i)) == list.get(i)) {
                    //For some reason, this worked while just saying list.get(i) did not
                    cleanList.add((int) Math.floor(list.get(i)));
                }
            }
        }

        return cleanList;
    }

    public ArrayList<Double> getValidMoves(double roll1, double roll2, double roll3) {
        ArrayList<Double> validMovesList = new ArrayList<Double>();
        ArrayList<Double> validMovesList1 = new ArrayList<Double>();
        ArrayList<Double> validMovesList2 = new ArrayList<Double>();
        ArrayList<Double> validMovesList3 = new ArrayList<Double>();

        validMovesList1.addAll(compute(roll1, roll2));
        validMovesList2.addAll(compute(roll1, roll3));
        validMovesList3.addAll(compute(roll2, roll3));

        for (int i = 0; i < validMovesList1.size(); i++) {
            validMovesList.addAll(compute(validMovesList1.get(i), roll3));
            validMovesList.addAll(compute(validMovesList2.get(i), roll2));
            validMovesList.addAll(compute(validMovesList3.get(i), roll1));
        }

        return validMovesList;
    }

    public ArrayList<Double> compute(double num1, double num2) {
        ArrayList<Double> values = new ArrayList<Double>();
        values.add(num1 + num2);
        values.add(num1 - num2);
        values.add(num1 * num2);
        values.add(num2 - num1);

        //NEED TO FIGURE OUT HOW ALL THIS WORKS SINCE GENERATING LISTS REQUIRES THEY ARE ARE THE SAME SIZE
        //SHOULDN'T BE TOO DIFFICULT IF I NEED TO FIX IT.

        //If we are on the third dice roll, then we want to make sure we are have a whole number
        //in the numerator, otherwise we will get useless decimal ridden numbers
        values.add(num1 / num2);
        //We don't need to do this a second time since we know num2 is a whole number
        //Could this be improved by only adding the number if it is a whole number? Maybe not, figure it out later
        values.add(num2 / num1);

        //Need to figure out later how to exclude values from this that are way out of range
        values.add(Math.pow(num1, num2));
        values.add(Math.pow(num2, num1));

        return values;
    }

    /*
This method starts at the beginning of the best moves list and checks to see if the first best
move is a valid move. If it is, we return that pair, otherwise, we check the next best element.
We do this until we have found the best valid move.
 */
    public IndexPair findBestValidMove(ArrayList<IndexPair> bestMoves, HashSet<Integer> validMoves) {
        for (int i = 0; i < bestMoves.size(); i++) {
            int numAtSpot = Contig.gameBoard[bestMoves.get(i).row][bestMoves.get(i).column];
            for (int j = 0; j < validMoves.size(); j++) {
                if (validMoves.contains(numAtSpot)) {
                    return bestMoves.get(i);
                }
            }
        }
        return new IndexPair(-1, -1, 0);
    }

    /**
     * This adds all the "score potentials" of placing a chip on any of the
     * particular positions to an ArrayList that is then sorted by
     * best moves (higher score) to worst (lower score)
     * @return ArrayList of potential scores on each position
     */
    public ArrayList<IndexPair> getBestMoves() {
        ArrayList<IndexPair> bestMovesList = new ArrayList<IndexPair>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                IndexPair curIndP = new IndexPair(i, j, Contig.getScorePotential(i, j));
                if (Contig.gameBoard[i][j] != -1) {
                    bestMovesList.add(curIndP);
                }
            }
        }

        bestMovesList.sort(new Comparator<IndexPair>() {
            @Override
            public int compare(IndexPair o1, IndexPair o2) {
                if (o1.curScorePotential > o2.curScorePotential) {
                    return -1;
                } else if (o1.curScorePotential == o2.curScorePotential) {
                    return  0;
                } else {
                    return 1;
                }
            }
        });
        return bestMovesList;
    }

    /**
     * This method finds the position of a number using that number
     * @param roll
     * @return
     */
    private IndexPair findIndexByNumber(int roll) {
        IndexPair indP = new IndexPair(-1, -1);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Contig.gameBoard[i][j] == roll) {
                    indP.row = i;
                    indP.column = j;
                }
            }
        }
        return indP;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    /**
     * This class plays a few roles and is used to keep track of Index Pairs
     * It has constructors for helping with creating the best moves ArrayList,
     * creating the valid moves ArrayList, and a normal pair
     */
    public class IndexPair {
        protected int row;
        protected int column;
        protected int curScorePotential;

        public IndexPair(int row, int column, int curScorePotential) {
            this.row = row;
            this.column = column;
            this.curScorePotential = curScorePotential;
        }

        public IndexPair(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }

}

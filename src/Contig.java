import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by jonathanevans on 8/31/17.
 *
 * This is for the algorithms project and this program is an AI that can win the game "Contig"
 */
public class Contig {

    private int[][] gameBoard;
    private Scanner scr;
    private Random dice;
    private boolean isFirstTurn;
    private int computerScore;

    //Reads the gameboard numbers from a text file. Then creates the gameboard, printing out the pre-game setup
    public Contig() {
        dice = new Random();

        try {
            scr = new Scanner(new FileReader("ContigNumbers.txt"));
        } catch (FileNotFoundException fnfe){
            System.out.println("SHit don't work bro.. It's not up to me, relax bro, I'm just tellin it how it is.");
        }

        gameBoard = createGameBoard();
        isFirstTurn = true;
        computerScore = 0;
    }

    /**
     * Creates the game board by parsing the text file.
     * @return the 2d int array gameboard
     */
    public int[][] createGameBoard() {

        int[][] setupBoard = new int[8][8];
        int counter = 1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                setupBoard[i][j] = scr.nextInt();
            }
        }
        return setupBoard;
    }

    /**
     * This simply prints out the board and replaces all the
     * "-1"'s with an X for clarity
     */
    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                if (gameBoard[i][j] > 0) {
                    System.out.print(gameBoard[i][j] + " ");
                } else {
                    System.out.print("X ");
                }
            }
        }
        System.out.println();
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

        if (isFirstTurn) {
            playFirstTurn(roll1, roll2, roll3);
        } else {

            ArrayList<IndexPair> bestMovesList = getBestMoves();
            ArrayList<Integer> validMovesList = cleanUpListAndConvertToInt(getValidMoves(roll1, roll2, roll3));

            IndexPair play = findBestValidMove(bestMovesList, validMovesList);
            gameBoard[play.row][play.column] = -1;
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
        gameBoard[firstPair.row][firstPair.column] = -1;
        computerScore+=2;
        isFirstTurn = false;
    }

    private ArrayList<Integer> cleanUpListAndConvertToInt(ArrayList<Double> list) {
        ArrayList<Integer> cleanList = new ArrayList<Integer>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) <= 216 || list.get(i) >= 1) {
                if (Math.floor(list.get(i)) == list.get(i)) {
                    //For some reason, this worked while just saying list.get(i) did not
                    cleanList.add((int) Math.floor(list.get(i)));
                }
            }
        }

        return cleanList;
    }

    /*
    This method starts at the beginning of the best moves list and checks to see if the first best
    move is a valid move. If it is, we return that pair, otherwise, we check the next best element.
    We do this until we have found the best valid move.
     */
    public IndexPair findBestValidMove(ArrayList<IndexPair> bestMoves, ArrayList<Integer> validMoves) {
        for (int i = 0; i < bestMoves.size(); i++) {
            int numAtSpot = gameBoard[bestMoves.get(i).row][bestMoves.get(i).column];
            for (int j = 0; j < validMoves.size(); j++) {
                if (numAtSpot == validMoves.get(j)) {
                    return bestMoves.get(i);
                }
            }
        }
        return new IndexPair(-1, -1);
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
                IndexPair curIndP = new IndexPair(i, j, getScorePotential(i, j));
                bestMovesList.add(curIndP);
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
     * This method checks all eight directions if possible from any particular spot
     * to determine how many adjacent chips there are, which tallys the score.
     * @param i the row of the particular position
     * @param j the column of the particular position
     * @return int indicating the scoring potential
     */
    public int getScorePotential(int i, int j) {
        int scoreCounter = 0;
        scoreCounter += checkTop(i, j);
        scoreCounter += checkBottom(i, j);
        if (j - 1 > 0 && gameBoard[i][j-1] <= 0) {
            scoreCounter++;
        }
        if (j + 1 < 8 && gameBoard[i][j+1] <= 0) {
            scoreCounter++;
        }
        return scoreCounter;
    }

    //This simply checks the top row for adjacent chips
    private int checkTop(int i, int j) {
        int topCounter = 0;
        if (i - 1 > 0) {
            if (j - 1 > 0 && gameBoard[i-1][j-1] <= 0) {
                topCounter++;
            }
            if (gameBoard[i-1][j] <= 0) {
                topCounter++;
            }
            if (j + 1 < 8 && gameBoard[i-1][j+1] <= 0) {
                topCounter++;
            }
        }
        return topCounter;
    }

    //This simply checks the bottom row for adjacent chips
    private int checkBottom(int i, int j) {
        int bottomCounter = 0;
        if (i + 1 < 8) {
            if (j - 1 > 0 && gameBoard[i+1][j-1] <= 0) {
                bottomCounter++;
            }
            if (gameBoard[i+1][j] <= 0) {
                bottomCounter++;
            }
            if (j + 1 < 8 && gameBoard[i+1][j+1] <= 0) {
                bottomCounter++;
            }
        }
        return bottomCounter;
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

    /**
     * This method finds the position of a number using that number
     * @param roll
     * @return
     */
    private IndexPair findIndexByNumber(int roll) {
        IndexPair indP = new IndexPair(-1, -1);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gameBoard[i][j] == roll) {
                    indP.row = i;
                    indP.column = j;
                }
            }
        }
        return indP;
    }

    /**
     * This class plays a few roles and is used to keep track of Index Pairs
     * It has constructors for helping with creating the best moves ArrayList,
     * creating the valid moves ArrayList, and a normal pair
     */
    private class IndexPair {
        private int row;
        private int column;
        private int curScorePotential;
        private boolean isValid;

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

    public static void main (String[] args) {
        Contig c = new Contig();
    }
}

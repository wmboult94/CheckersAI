import java.util.*;
public class TicTacToe_MM {

    private static void startGame(int player) {        
    Board b = new Board();
    Random rand = new Random();

    if(player == 1){
        Position p = new Position(rand.nextInt(3), rand.nextInt(3));
        b.placePiece(p, 1);
        b.displayBoard();
    }
    else
        b.displayBoard();
    
    while (!b.isGameOver()) {
        System.out.println("Your move (row col): ");
        Position userMove = new Position(b.scan.nextInt()-1, b.scan.nextInt()-1);

        b.placePiece(userMove, 2); //2 is O (human player)
        b.displayBoard();
        if (b.isGameOver()) {
            break;
        } 
        b.startEvaluation();
        System.out.println("\nI'm thinking...");
        
        for (PositionsAndScores pas : b.successorEvaluations) {
            System.out.println("Position: " + pas.pos + " scores " + pas.score);
        }
        
        Position bm = b.getBestMove();
        
        System.out.println("\nI think "+bm+ " will be a good move.");
        b.placePiece(bm, 1);
        b.displayBoard();
    }
    
    if (b.xHasWon()) {
        System.out.println("\nYOU LOSE!");
    } else if (b.oHasWon()) {
        System.out.print("\nYOU WIN!");
    } else {
        System.out.println("\nDRAW");
    }
    
    System.out.println("\nWould you like to play again?");
    startGame(processUserCommand(getUserCommand()));
    }
    
    public static void main(String[] args) {
        System.out.println("********************************");
        System.out.println("Hello, welcome to Tic Tac Toe!");
        System.out.println("********************************");
        startGame(processUserCommand(getUserCommand()));
    }
    private static String[] getUserCommand() {
        Scanner scanner = new Scanner(System.in);
        String[] output = new String[2];
        System.out.print("Enter '1' for the AI to begin, or '2' to make the first move, or 'q' to quit: ");
        String s = scanner.next();
        if(s.equals("q")) {
            System.out.print("Good Bye!");
            System.exit(0);
        }
        else
            output[0] = s;
        return output;
    }
  
    private static int processUserCommand(String[] c) {
        try {
            return(Integer.parseInt(c[0]));
        } catch(Exception e) {
            System.out.print("Whatever.");
            System.exit(0);
        } 
        return 0;
    }
}
    
class Position {

    int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + (x+1) + "," + (y+1) + "]";
    }
}

class PositionsAndScores {

    int score;
    Position pos;

    PositionsAndScores(int score, Position pos) {
        this.score = score;
        this.pos = pos;
    }
}

class Board {
 
    List<Position> availablePositions;
    List<PositionsAndScores> successorEvaluations;
    Scanner scan;
    int level;
    int[][] board;

    public Board() {
        scan = new Scanner(System.in);
        board = new int[3][3];
    }

    public boolean isGameOver() {
        //Game is over if any player has won, or the board is filled with pieces (a draw)
        return (xHasWon() || oHasWon() || getAvailableStates().isEmpty());
    }

    public boolean xHasWon() {
        // X Diagonal Win
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 1) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 1)) {
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            // X Row or Column win
            if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 1)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 1))) {
                return true;
            }
        }
        return false;
    }

    public boolean oHasWon() {
            // O Diagonal Win
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 2) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 2)) {
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            // O Row or Column win
            if ((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 2)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 2)) {
                return true;
            }
        }
        return false;
    }

    public List<Position> getAvailableStates() {
        availablePositions = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                // is the current position available?
                if (board[i][j] == 0) {
                    availablePositions.add(new Position(i, j));
                }
            }
        }
        return availablePositions;
    }

    public void placePiece(Position pos, int player) {
        board[pos.x][pos.y] = player;   //player == 1 is X, player == 2 is O
    }

    public Position getBestMove() {
        //Random rand = new Random();
        //List<Position> positionsAvailable = getAvailableStates();
        //return positionsAvailable.get(rand.nextInt(positionsAvailable.size()-1));
        int MAX = -100000; // score tracking
        int best = -1; // pos tracking
        for (int i=0; i < successorEvaluations.size(); ++i) {
            if (MAX < successorEvaluations.get(i).score) {
                MAX = successorEvaluations.get(i).score;
                best = i;
            }
        }
        return successorEvaluations.get(best).pos;
        
    }

    public void displayBoard() {
        System.out.println();
        // top row 1 2 3
        System.out.println("  1 2 3");
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                // left-hand side column 1 2 3
                if(j == 0)
                    System.out.print((i+1));
                // pieces belonging to player 1 get an X
                if(board[i][j] == 1)
                    System.out.print(" X");
                // pieces belonging to player 2 get an O
                else if(board[i][j] == 2) 
                    System.out.print(" O");
                // empty fields are indicated by a -
                else
                    System.out.print(" -");
            }
            System.out.println();

        }
    }
    
    public int getMin(List<Integer> list) {
        // return the minimum value in the list
        int min = Integer.MAX_VALUE;
        
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) < min) {
                min = list.get(i);
            }
        }
        return min;
    }

    public int getMax(List<Integer> list) {
        // return the maximum value in the list
        int max = Integer.MIN_VALUE;
        
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }
        return max;
    }

    public void startEvaluation(){
        successorEvaluations = new ArrayList<>();
        minimax(0, 1); // depth, player
    }
    
    public int minimax(int depth, int player) {

        // check if either player has won and return zero-sum result
        if(xHasWon())
            return +1;
        if(oHasWon())
            return -1;
       
        List<Position> positionsAvailable = getAvailableStates();
        if (positionsAvailable.isEmpty()) 
            return 0; 
        // because the game tree is only 9 levels deep, we store the results in a list
        // do not do this with more complex problems!
        List<Integer> scores = new ArrayList<>();
        
        //Loop through all available positions
        for (int i=0; i < positionsAvailable.size(); ++i)
        {
            // determine all board positions that aren't occupied
            Position pos = positionsAvailable.get(i); 
            if (player == 1) { //X's turn: get the highest result returned by minimax
                // place a piece at the first available position for player 1
                placePiece(pos, 1);
                // get the minimax evaluation result for making the previous move
                int currentScore = minimax(depth + 1, 2); // Increase depth here
                // put the score for that path in the list
                scores.add(currentScore);
                // store a mapping of complete evaluations (at depth 0) and their scores
                if (depth == 0) 
                    successorEvaluations.add(new PositionsAndScores(currentScore, pos));
            } 
            else if (player == 2) {//O's turn: get the lowest result returned by minimax
                placePiece(pos, 2);
                int currentScore = minimax(depth + 1, 1); // increase depth here
                // put the score for that path in the list
                scores.add(currentScore);
            }
            board[pos.x][pos.y] = 0; //Reset this pos
        }
        return player == 1 ? getMax(scores) : getMin(scores);
        // the above expression uses a ternary operator and is equivalent to this code:
        // if (player == 1)
        //    return getMax(scores);
        // else
        //    return getMin(scores);
    }   
}
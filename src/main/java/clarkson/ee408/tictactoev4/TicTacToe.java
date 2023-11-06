package clarkson.ee408.tictactoev4;
/**
 * The TicTacToe class represents a game of Tic-Tac-Toe with a 3x3 grid
 * Players take turns marking cells with their respective symbols (X for Player 1 and O for Player 2).
 * The game can be played until a player wins, it's a tie, or it's still ongoing.
 */
public class TicTacToe {
    public static final int SIDE = 3; // The size of the Tic-Tac-Toe grid.
    private int turn; // Tracks the current turn (1 for Player 1, 2 for Player 2).
    private int [][] game; // Represents the state of the game board.

    private int player; // Represents the current player (1 for Player 1, 2 for Player 2).
    /**
     * Constructs a new TicTacToe game with the specified starting player.
     *
     * @param startingPlayer The player (1 or 2) who starts the game.
     */
    public TicTacToe(int startingPlayer) {
        game = new int[SIDE][SIDE];
        player = startingPlayer;
        resetGame();
    }
    /**
     * Sets the current player.
     *
     * @param player The player to set (1 for Player 1, 2 for Player 2).
     */
    public void setPlayer(int player) {
        this.player = player;
    }
    /**
     * Gets the current player.
     *
     * @return The current player (1 for Player 1, 2 for Player 2).
     */
    public int getPlayer() {
        return player;
    }
    /**
     * Gets the current turn (1 for Player 1, 2 for Player 2).
     *
     * @return The current turn.
     */
    public int getTurn() {
        return turn;
    }
    /**
     * Allows a player to make a move on the game board by specifying a row and column.
     *
     * @param row The row of the cell to mark (0 to SIDE-1).
     * @param col The column of the cell to mark (0 to SIDE-1).
     * @return The player who made the move (1 for Player 1, 2 for Player 2), or 0 if the move is invalid.
     */
    public int play( int row, int col ) {
        int currentTurn = turn;
        if( row >= 0 && col >= 0 && row < SIDE && col < SIDE
                && game[row][col] == 0 ) {
            game[row][col] = turn;
            if( turn == 1 )
                turn = 2;
            else
                turn = 1;
            return currentTurn;
        }
        else
            return 0;
    }
    /**
     * Checks and returns the player who has won the game, or 0 if no player has won.
     *
     * @return The winning player (1 for Player 1, 2 for Player 2), or 0 if there is no winner yet.
     */
    public int whoWon( ) {
        int rows = checkRows( );
        if ( rows > 0 )
            return rows;
        int columns = checkColumns( );
        if( columns > 0 )
            return columns;
        int diagonals = checkDiagonals( );
        if( diagonals > 0 )
            return diagonals;
        return 0;
    }

    /**
 * Checks for a winning pattern in the rows and returns the winning player or 0 if no one has won.
     * @return The winning player (1 for Player 1, 2 for Player 2), or 0 if there is no winner in the rows.
 */
    protected int checkRows( ) {
        for( int row = 0; row < SIDE; row++ )
            if ( game[row][0] != 0 && game[row][0] == game[row][1]
                    && game[row][1] == game[row][2] )
                return game[row][0];
        return 0;
    }
    /**
     * Checks for a winning pattern in the columns and returns the winning player or 0 if no one has won.
     *
     * @return The winning player (1 for Player 1, 2 for Player 2), or 0 if there is no winner in the columns.
     */
    protected int checkColumns( ) {
        for( int col = 0; col < SIDE; col++ )
            if ( game[0][col] != 0 && game[0][col] == game[1][col]
                    && game[1][col] == game[2][col] )
                return game[0][col];
        return 0;
    }
    /**
     * Checks for a winning pattern in the diagonals and returns the winning player or 0 if no one has won.
     *
     * @return The winning player (1 for Player 1, 2 for Player 2), or 0 if there is no winner in the diagonals.
     */
    protected int checkDiagonals( ) {
        if ( game[0][0] != 0 && game[0][0] == game[1][1]
                && game[1][1] == game[2][2] )
            return game[0][0];
        if ( game[0][2] != 0 && game[0][2] == game[1][1]
                && game[1][1] == game[2][0] )
            return game[2][0];
        return 0;
    }
    /**
     * Checks if there are no more available moves on the game board.
     *
     * @return true if no more moves can be played, false otherwise.
     */
    public boolean canNotPlay( ) {
        boolean result = true;
        for (int row = 0; row < SIDE; row++)
            for( int col = 0; col < SIDE; col++ )
                if ( game[row][col] == 0 )
                    result = false;
        return result;
    }
    /**
     * Checks if the game is over, either due to a win or a tie.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver( ) {
        return canNotPlay( ) || ( whoWon( ) > 0 );
    }
    /**
     * Resets the game board to its initial state and sets the turn to Player 1.
     */
    public void resetGame( ) {
        for (int row = 0; row < SIDE; row++)
            for( int col = 0; col < SIDE; col++ )
                game[row][col] = 0;
        turn = 1;
    }
    /**
     * Returns the current result of the game as a string.
     *
     * @return A string indicating the game result, such as "You won," "You lost," "Tie Game," "Your Turn," or "Waiting for Opponent."
     */
    public String result( ) {
        int winner = whoWon();
        if (winner == player) {
            return "You won";
        } else if (winner != 0) {
            return "You lost";
        } else if (canNotPlay()) {
            return "Tie Game";
        } else if (player == turn){
            return "Your Turn";
        }
        else {
            return "Waiting for Opponent";
        }
        }
    }


package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SeymourBot implements IBot{
    final int moveTimeMs = 200;
    private static final String BOTNAME = "Seymour-Bots";
    protected int[][] preferredMoves = {
            {0, 1}, {2, 1}, {1, 0}, {1, 2}, //Outer Middles ordered across
            {0, 0}, {2, 2}, {0, 2}, {2, 0},  //Corners ordered across
            {1, 1} //Center
    };
    private Random rand = new Random();
    private IMove move = null;

    private String opponent = "";
    private String player = "";

    /*
    1. Prio: vind  selv Macrospillet
    2. prio: Stop den anden spiller fra at vinde MACRO Spillet
    3. Prio vind selv  Microspillet
    4. Prio stop den anden spiller fra at vinde MICRO  spillet
    5. Prio vind  midten
    6. Prio vind hjørnerne
    7. Prio vind siderne
     */

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    @Override
    public IMove doMove(IGameState state) {
        move = null;

        opponent = "0";
        player = "1";
        if(state.getMoveNumber()%2==0){
            player="0";
            opponent = "1";
        }

        List<IMove> moves = state.getField().getAvailableMoves();

        move = calculateWinningMoves(state, moveTimeMs);

        /*
        if(winMacro(state, move, player))
        {
            if (!winMacro(state, move, opponent)){

            }
        }*/

        if (move == null) move = preferable(state);

        if (move == null) move = random(moves);

        return move;
    }


    private boolean winMicro(IGameState state, IMove move, String player){
        String[][] board = Arrays.stream(state.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);

        board[move.getX()][move.getY()] = player;

        int localX = move.getX() % 3;
        int localY = move.getY() % 3;
        int startX = move.getX() - (localX);
        int startY = move.getY() - (localY);

        //check col
        for (int i = startY; i < startY + 3; i++) {
            if (!board[move.getX()][i].equals(player))
                break;
            if (i == startY + 2) return true;
        }

        //check row
        for (int i = startX; i < startX + 3; i++) {
            if (!board[i][move.getY()].equals(player))
                break;
            if (i == startX + 2) return true;
        }

        //check diagonal
        if (localX == localY) {
            //we're on a diagonal
            int y = startY;
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][y++].equals(player))
                    break;
                if (i == startX + 2) return true;
            }
        }

        //check anti diagonal
        if (localX + localY == 2) {
            int less = 0;
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][(startY + 2)-less++].equals(player))
                    break;
                if (i == startX + 2) return true;
            }
        }
        return false;
    }

    private boolean winMicro2(IGameState state, IMove move, String player){

        // Clones the array and all values to a new array, so we don't mess with the game
        String[][] board = Arrays.stream(state.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);

        //Places the player in the game. Sort of a simulation.
        board[move.getX()][move.getY()] = player;

        //Sout board
        /*
        for (int x = 0; x < 9; x++){
            System.out.println();
            for (int y = 0; y < 9; y++){
                System.out.print(board[y][x]+" ");
            }
        }
        System.out.println("\n");
         */

        int startX = move.getX()-(move.getX()%3);
        if(Objects.equals(board[startX][move.getY()], player))
            if (Objects.equals(board[startX][move.getY()], board[startX + 1][move.getY()]) &&
                    Objects.equals(board[startX + 1][move.getY()], board[startX + 2][move.getY()])){
                return true;
            }

        int startY = move.getY()-(move.getY()%3);
        if(Objects.equals(board[move.getX()][startY], player))
            if (Objects.equals(board[move.getX()][startY], board[move.getX()][startY + 1]) &&
                    Objects.equals(board[move.getX()][startY + 1], board[move.getX()][startY + 2])){
                return true;
            }


        if(Objects.equals(board[startX][startY], player))
            if (Objects.equals(board[startX][startY], board[startX + 1][startY + 1]) &&
                    Objects.equals(board[startX + 1][startY + 1], board[startX + 2][startY + 2])){
                return true;
            }

        if(Objects.equals(board[startX][startY + 2], player))
            if ((Objects.equals(board[startX][startY + 2], board[startX + 1][startY + 1])) &&
                    (Objects.equals(board[startX + 1][startY + 1], board[startX + 2][startY]))){
                return true;
            }

        return false;
    }

    private  IMove preferable(IGameState state){
        for (int[] move : preferredMoves)
        {
            if(state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
            {
                //find move to play
                for (int[] selectedMove : preferredMoves)
                {
                    int x = move[0]*3 + selectedMove[0];
                    int y = move[1]*3 + selectedMove[1];
                    if(state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                    {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private void checkMoves(List<IMove> moves) {
        for (IMove move: moves) {
        }
    }

    private IMove random(List<IMove> moves) {
        if (moves.size() > 0) {
            return moves.get(rand.nextInt(moves.size()));
        }

        return null;
    }

    private SeymourBot.GameSimulator createSimulator(IGameState state) {
        SeymourBot.GameSimulator simulator = new SeymourBot.GameSimulator(new GameState());
        simulator.setGameOver(SeymourBot.GameOverState.Active);
        simulator.setCurrentPlayer(state.getMoveNumber() % 2);
        simulator.getCurrentState().setRoundNumber(state.getRoundNumber());
        simulator.getCurrentState().setMoveNumber(state.getMoveNumber());
        simulator.getCurrentState().getField().setBoard(state.getField().getBoard());
        simulator.getCurrentState().getField().setMacroboard(state.getField().getMacroboard());
        return simulator;
    }

    // Plays single games until it wins and returns the first move for that. If iterations reached with no clear win, just return random valid move
    private IMove calculateWinningMoves(IGameState state, int maxTimeMs){
        long time = System.currentTimeMillis();
        ArrayList<IMove> winningMoves = new ArrayList<>();

        Random rand = new Random();
        while (System.currentTimeMillis() < time + maxTimeMs) { // check how much time has passed, stop if over maxTimeMs
            SeymourBot.GameSimulator simulator = createSimulator(state);
            IGameState gs = simulator.getCurrentState();
            //TODO What is the difference between moves and legal moves
            List<IMove> moves = gs.getField().getAvailableMoves();

            List<IMove> legalMoves = state.getField().getAvailableMoves();

            for (IMove move: moves) {
                if(!legalMoves.contains(move)){
                    moves.remove(move);
                }
            }

            IMove randomMovePlayer = moves.get(rand.nextInt(moves.size()));
            IMove winnerMove = randomMovePlayer;

            while (simulator.getGameOver()== SeymourBot.GameOverState.Active){ // Game not ended
                simulator.updateGame(randomMovePlayer);

                // Opponent plays randomly
                if (simulator.getGameOver()== SeymourBot.GameOverState.Active){ // game still going
                    moves = gs.getField().getAvailableMoves();
                    IMove randomMoveOpponent = moves.get(rand.nextInt(moves.size()));
                    simulator.updateGame(randomMoveOpponent);
                }
                if (simulator.getGameOver()== SeymourBot.GameOverState.Active){ // game still going
                    moves = gs.getField().getAvailableMoves();
                    randomMovePlayer = moves.get(rand.nextInt(moves.size()));
                }
            }

            if (simulator.getGameOver()== SeymourBot.GameOverState.Win){
                //System.out.println("Found a win, :)");

                if (winMicro2(state,winnerMove,player)) {
                    System.out.println("Win GAME----------------------");return winnerMove;}

                //Check if we can win
                //TODO never used
                if (winMicro(state,winnerMove,player)) {System.out.println("MicroWin");return winnerMove;}
                //Check for sabotage
                if (winMicro(state,winnerMove,opponent)) {System.out.println("sabotage"); return winnerMove;}

                //TODO prio do not send opponent to a micro that is won

                winningMoves.add(winnerMove); // Hint you could maybe save multiple games and pick the best? Now it just returns at a possible victory
            }
        }

        //returns the common value of the arraylist == winning move.
        //System.out.println(winningMoves.size());
        //System.out.println(winningMoves);
        //list of winning moves. retuns the most common move, that won the simulations

        IMove move = null;
        IMove savedMove = null;
        int moveCount = 0;
        int count = 0;
        for (IMove move1:winningMoves) {
            for (IMove move2:winningMoves) {
                move = move1;
                if (move2.equals(move1)){
                    count++;
                }
            }
            if (count > moveCount){
                moveCount = count;
                savedMove = move;
            }
            count = 0;
            move = null;
        }
        if (savedMove != null){

            System.out.println(savedMove.toString());
        }else {
            //return a move if null

        }
        return savedMove;

        /*
        int maxcount = 0;
        IMove element_having_max_freq = null;
        for (int i = 0; i < winningMoves.size(); i++) {
            int count = 0;
            for (int j = 0; j < winningMoves.size(); j++) {
                if (winningMoves.get(i) == winningMoves.get(j)) {
                    count++;
                }
            }
            if (count > maxcount) {
                maxcount = count;
                element_having_max_freq = winningMoves.get(i);
            }
        }

        if (element_having_max_freq != null)System.out.println("Retuned move: " + element_having_max_freq.toString());
        return element_having_max_freq;
         */
        //return winningMoves.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
    }

    /*
        The code below is a simulator for simulation of gameplay. This is needed for AI.

        It is put here to make the Bot independent of the GameManager and its subclasses/enums

        Now this class is only dependent on a few interfaces: IMove, IField, and IGameState

        You could say it is self-contained. The drawback is that if the game rules change, the simulator must be
        changed accordingly, making the code redundant.

     */

    public enum GameOverState {
        Active,
        Win,
        Tie
    }

    public class Move implements IMove {
        int x = 0;
        int y = 0;

        public Move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SeymourBot.Move move = (SeymourBot.Move) o;
            return x == move.x && y == move.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    class GameSimulator {
        private final IGameState currentState;
        private int currentPlayer = 0; //player0 == 0 && player1 == 1
        private volatile SeymourBot.GameOverState gameOver = SeymourBot.GameOverState.Active;

        public void setGameOver(SeymourBot.GameOverState state) {
            gameOver = state;
        }

        public SeymourBot.GameOverState getGameOver() {
            return gameOver;
        }

        public void setCurrentPlayer(int player) {
            currentPlayer = player;
        }

        public IGameState getCurrentState() {
            return currentState;
        }

        public GameSimulator(IGameState currentState) {
            this.currentState = currentState;
        }

        public Boolean updateGame(IMove move) {
            if (!verifyMoveLegality(move))
                return false;

            updateBoard(move);
            currentPlayer = (currentPlayer + 1) % 2;

            return true;
        }

        private Boolean verifyMoveLegality(IMove move) {
            IField field = currentState.getField();
            boolean isValid = field.isInActiveMicroboard(move.getX(), move.getY());

            if (isValid && (move.getX() < 0 || 9 <= move.getX())) isValid = false;
            if (isValid && (move.getY() < 0 || 9 <= move.getY())) isValid = false;

            if (isValid && !field.getBoard()[move.getX()][move.getY()].equals(IField.EMPTY_FIELD))
                isValid = false;

            return isValid;
        }

        private void updateBoard(IMove move) {
            String[][] board = currentState.getField().getBoard();
            board[move.getX()][move.getY()] = currentPlayer + "";
            currentState.setMoveNumber(currentState.getMoveNumber() + 1);
            if (currentState.getMoveNumber() % 2 == 0) {
                currentState.setRoundNumber(currentState.getRoundNumber() + 1);
            }
            checkAndUpdateIfWin(move);
            updateMacroboard(move);

        }

        private void checkAndUpdateIfWin(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            int macroX = move.getX() / 3;
            int macroY = move.getY() / 3;

            if (macroBoard[macroX][macroY].equals(IField.EMPTY_FIELD) ||
                    macroBoard[macroX][macroY].equals(IField.AVAILABLE_FIELD)) {

                String[][] board = getCurrentState().getField().getBoard();

                if (isWin(board, move, "" + currentPlayer))
                    macroBoard[macroX][macroY] = currentPlayer + "";
                else if (isTie(board, move))
                    macroBoard[macroX][macroY] = "TIE";

                //Check macro win
                if (isWin(macroBoard, new SeymourBot.Move(macroX, macroY), "" + currentPlayer))
                    gameOver = SeymourBot.GameOverState.Win;
                else if (isTie(macroBoard, new SeymourBot.Move(macroX, macroY)))
                    gameOver = SeymourBot.GameOverState.Tie;
            }

        }

        private boolean isTie(String[][] board, IMove move) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            for (int i = startX; i < startX + 3; i++) {
                for (int k = startY; k < startY + 3; k++) {
                    if (board[i][k].equals(IField.AVAILABLE_FIELD) ||
                            board[i][k].equals(IField.EMPTY_FIELD))
                        return false;
                }
            }
            return true;
        }


        public boolean isWin(String[][] board, IMove move, String currentPlayer) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            //check col
            for (int i = startY; i < startY + 3; i++) {
                if (!board[move.getX()][i].equals(currentPlayer))
                    break;
                if (i == startY + 3 - 1) return true;
            }

            //check row
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][move.getY()].equals(currentPlayer))
                    break;
                if (i == startX + 3 - 1) return true;
            }

            //check diagonal
            if (localX == localY) {
                //we're on a diagonal
                int y = startY;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][y++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }

            //check anti diagonal
            if (localX + localY == 3 - 1) {
                int less = 0;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][(startY + 2) - less++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }
            return false;
        }

        private void updateMacroboard(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            for (int i = 0; i < macroBoard.length; i++)
                for (int k = 0; k < macroBoard[i].length; k++) {
                    if (macroBoard[i][k].equals(IField.AVAILABLE_FIELD))
                        macroBoard[i][k] = IField.EMPTY_FIELD;
                }

            int xTrans = move.getX() % 3;
            int yTrans = move.getY() % 3;

            if (macroBoard[xTrans][yTrans].equals(IField.EMPTY_FIELD))
                macroBoard[xTrans][yTrans] = IField.AVAILABLE_FIELD;
            else {
                // Field is already won, set all fields not won to avail.
                for (int i = 0; i < macroBoard.length; i++)
                    for (int k = 0; k < macroBoard[i].length; k++) {
                        if (macroBoard[i][k].equals(IField.EMPTY_FIELD))
                            macroBoard[i][k] = IField.AVAILABLE_FIELD;
                    }
            }
        }
    }
}

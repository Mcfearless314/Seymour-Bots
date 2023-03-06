package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.List;
import java.util.Random;

public class SeymourBot implements IBot{
    private static final String BOTNAME = "Seymour-Bot";
    protected int[][] preferredMoves = {
            {0, 1}, {2, 1}, {1, 0}, {1, 2}, //Outer Middles ordered across
            {0, 0}, {2, 2}, {0, 2}, {2, 0},  //Corners ordered across
            {1, 1} //Center
    };
    private Random rand = new Random();

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    @Override
    public IMove doMove(IGameState state) {
        IMove move = null;
        List<IMove> moves = state.getField().getAvailableMoves();
        System.out.println(moves);
        move = preferable(state);

        if (move == null) return random(moves);

        return move;
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
}

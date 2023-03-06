package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.List;
import java.util.Random;

public class SeymourBot implements IBot{
    private static final String BOTNAME = "Seymour-Bot";
    private Random rand = new Random();

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();
        IMove move = random(moves);

        return move;
    }

    private IMove random(List<IMove> moves) {
        if (moves.size() > 0) {
            return moves.get(rand.nextInt(moves.size()));
        }

        return null;
    }
}

package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

public class SeymourBot implements IBot{
    @Override
    public String getBotName() {
        return "SeymourBot";
    }

    @Override
    public IMove doMove(IGameState state) {
        return null;
    }
}

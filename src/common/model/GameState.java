package common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String lobbyId;
    private final Map<String, List<QuestionCell>> board;
    private final String currentTurnSessionId;

    public GameState(String lobbyId,
                     Map<String, List<QuestionCell>> board,
                     String currentTurnSessionId) {
        this.lobbyId = lobbyId;
        this.board = board;
        this.currentTurnSessionId = currentTurnSessionId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public Map<String, List<QuestionCell>> getBoard() {
        return board;
    }

    public String getCurrentTurnSessionId() {
        return currentTurnSessionId;
    }
}
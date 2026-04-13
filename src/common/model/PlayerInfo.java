package common.model;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
   
    private static final long serialVersionUID = 1L;

    private final String sessionId;
    private final String name;
    private final int score;

    public PlayerInfo(String sessionId, String name, int score) {
        this.sessionId = sessionId;
        this.name = name;
        this.score = score;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

}

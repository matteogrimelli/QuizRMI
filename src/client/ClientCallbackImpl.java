package client;

import common.model.GameState;
import common.model.LobbyState;
import common.model.PlayerInfo;
import common.model.QuestionCell;
import common.remote.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
    private static final long serialVersionUID = 1L;

    private volatile LobbyState currentLobbyState;

    public ClientCallbackImpl() throws RemoteException {
        super();
    }

    // without ConsoleUtils
    /*
    @Override
    public void onLobbyUpdated(LobbyState lobbyState) throws RemoteException {
        this.currentLobbyState = lobbyState;

        System.out.println();
        System.out.println("=== AGGIORNAMENTO LOBBY ===");
        System.out.println("Lobby: " + lobbyState.getLobbyId());
        System.out.println("Owner session: " + lobbyState.getOwnerSessionId());
        System.out.println("Started: " + lobbyState.isStarted());
        System.out.println("Giocatori:");

        for (PlayerInfo player : lobbyState.getPlayers()) {
            System.out.println("- " + player.getName() + " | score=" + player.getScore());
        }

        System.out.println("===========================");
        printPrompt();
    }*/

    // versione con ConsoleUtils
    @Override
    public void onLobbyUpdated(LobbyState lobbyState) throws RemoteException {
        this.currentLobbyState = lobbyState;

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== AGGIORNAMENTO LOBBY ===\n");
        sb.append("Lobby: ").append(lobbyState.getLobbyId()).append("\n");
        sb.append("Owner session: ").append(lobbyState.getOwnerSessionId()).append("\n");
        sb.append("Started: ").append(lobbyState.isStarted()).append("\n");
        sb.append("Giocatori:\n");

        for (PlayerInfo player : lobbyState.getPlayers()) {
            sb.append("- ").append(player.getName())
            .append(" | score=").append(player.getScore())
            .append("\n");
        }

        sb.append("===========================\n");

        ConsoleUtils.printGameStateBlock(sb.toString());
        ConsoleUtils.printPrompt();
    }

    @Override
    public void onMessage(String message) throws RemoteException {
        //System.out.println();
        //System.out.println("[MESSAGGIO SERVER] " + message);
        //printPrompt();
        ConsoleUtils.println("\n[MESSAGGIO SERVER] " + message);
        ConsoleUtils.printPrompt();
    }
    

    // without ConsoleUtils
    /*
    @Override
    public void onGameUpdated(GameState gameState) throws RemoteException {
        System.out.println("DEBUG CLIENT: onGameUpdated ricevuto");

        if (gameState == null) {
            System.out.println("DEBUG CLIENT: gameState nullo");
            printPrompt();
            return;
        }

        System.out.println();
        System.out.println("=== GAME STATE ===");
        System.out.println("Turno di: " + gameState.getCurrentTurnSessionId());

        //versione semplificata
        for (String category : gameState.getBoard().keySet()) {
            System.out.println("[" + category + "]");
            for (QuestionCell cell : gameState.getBoard().get(category)) {
                String status = cell.isUsed() ? "X" : String.valueOf(cell.getValue());
                System.out.print(status + " ");
            }
            System.out.println();
        }

        //versione dettagliata
        for (String category : gameState.getBoard().keySet()) {
            System.out.println("Categoria: " + category);
            for (QuestionCell cell : gameState.getBoard().get(category)) {
                String status = cell.isUsed() ? "[ X ]" : "[" + cell.getValue() + "]";
                System.out.print(status + " ");
            }
            System.out.println();
            System.out.println();

            printPrompt();
        }
    }
    */

    // versione con ConsoleUtils e migliorata con nome giocatore invece di sessionId
    @Override
    public void onGameUpdated(GameState gameState) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        String currentTurnSessionId = gameState.getCurrentTurnSessionId();
        String currentTurnName = resolvePlayerName(currentTurnSessionId);

        sb.append("\n");
        sb.append("========== TABELLONE ==========\n");
        sb.append("Turno di: ").append(currentTurnSessionId);

        if (currentTurnName != null) {
            sb.append(" (").append(currentTurnName).append(")");
        }

        sb.append("\n\n");

        for (String category : gameState.getBoard().keySet()) {
            sb.append("Categoria: ").append(category).append("\n");

            for (QuestionCell cell : gameState.getBoard().get(category)) {
                String status = cell.isUsed() ? "[ X ]" : "[" + cell.getValue() + "]";
                sb.append(status).append(" ");
            }

            sb.append("\n\n");
        }

        sb.append("================================\n");

        ConsoleUtils.printGameStateBlock(sb.toString());
        ConsoleUtils.printPrompt();
    }

    public LobbyState getCurrentLobbyState() {
        return currentLobbyState;
    }

    private void printPrompt() {
        System.out.print("\nScegli comando: create | join | state | start | ping | exit\n");
    }

    private String resolvePlayerName(String sessionId) {
        if (currentLobbyState == null || sessionId == null) {
            return null;
        }

        for (PlayerInfo player : currentLobbyState.getPlayers()) {
            if (sessionId.equals(player.getSessionId())) {
                return player.getName();
            }
        }

        return null;
    }
}
package server;

import common.model.GameState;
import common.model.LobbyState;
import common.model.PlayerInfo;
import common.model.QuestionCell;
import common.remote.ClientCallback;
import common.remote.QuizService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//Implementazione concreta del servizio remoto.
public class QuizServiceImpl extends UnicastRemoteObject implements QuizService {

    private static final long serialVersionUID = 1L;

    private final ConcurrentHashMap<String, SessionData> sessions;
    private final ConcurrentHashMap<String, LobbyData> lobbies;

    public QuizServiceImpl() throws RemoteException {
        super();
        this.sessions = new ConcurrentHashMap<>();
        this.lobbies = new ConcurrentHashMap<>();
    }

    @Override
    public String ping(String name) throws RemoteException {
        System.out.println("Ricevuta chiamata ping dal client con nome: " + name);
        return "Ciao " + name + ", il server RMI funziona correttamente!";
    }

    @Override
    public synchronized String createSession(String playerName, ClientCallback callback) throws RemoteException {
        validatePlayerName(playerName);

        if (callback == null) {
            throw new RemoteException("Callback client nulla.");
        }

        String sessionId = UUID.randomUUID().toString();
        SessionData session = new SessionData(sessionId, playerName.trim(), callback);
        sessions.put(sessionId, session);

        System.out.println("Creata sessione: " + sessionId + " per player: " + playerName);
        return sessionId;
    }

    @Override
    public synchronized String createLobby(String sessionId) throws RemoteException {
        SessionData session = requireSession(sessionId);

        if (session.getLobbyId() != null) {
            throw new RemoteException("Il player è già in una lobby.");
        }

        String lobbyId = generateLobbyId();
        LobbyData lobby = new LobbyData(lobbyId, sessionId);

        lobbies.put(lobbyId, lobby);
        session.setLobbyId(lobbyId);

        System.out.println("Creata lobby: " + lobbyId + " da sessione: " + sessionId);

        notifyLobbyUpdated(lobby);
        return lobbyId;
    }

    @Override
    public synchronized LobbyState joinLobby(String sessionId, String lobbyId) throws RemoteException {
        SessionData session = requireSession(sessionId);
        LobbyData lobby = requireLobby(lobbyId);

        if (session.getLobbyId() != null) {
            throw new RemoteException("Il player è già in una lobby.");
        }

        if (lobby.isStarted()) {
            throw new RemoteException("La lobby è già stata avviata.");
        }

        lobby.addPlayer(sessionId);
        session.setLobbyId(lobbyId);

        System.out.println("Sessione " + sessionId + " entrata nella lobby " + lobbyId);

        LobbyState state = buildLobbyState(lobby);
        notifyLobbyUpdated(lobby);
        return state;
    }

    @Override
    public synchronized LobbyState getLobbyState(String sessionId) throws RemoteException {
        SessionData session = requireSession(sessionId);

        if (session.getLobbyId() == null) {
            throw new RemoteException("Il player non è in nessuna lobby.");
        }

        LobbyData lobby = requireLobby(session.getLobbyId());
        return buildLobbyState(lobby);
    }

    @Override
    public synchronized void startLobby(String sessionId) throws RemoteException {
        SessionData session = requireSession(sessionId);

        if (session.getLobbyId() == null) {
            throw new RemoteException("Il player non è in nessuna lobby.");
        }

        LobbyData lobby = requireLobby(session.getLobbyId());

        if (!lobby.getOwnerSessionId().equals(sessionId)) {
            throw new RemoteException("Solo il creatore della lobby può avviarla.");
        }

        if (lobby.isStarted()) {
            throw new RemoteException("La lobby è già avviata.");
        }

        if (lobby.getPlayerSessionIds().size() < 2) {
            throw new RemoteException("Servono almeno 2 giocatori per avviare la partita.");
        }

        lobby.setStarted(true);
        GameState gameState = createInitialGameState(lobby);
        lobby.setGameState(gameState);
        System.out.println("DEBUG: gameState creato per lobby " + lobby.getLobbyId());

        System.out.println("Lobby " + lobby.getLobbyId() + " avviata.");
        notifyLobbyUpdated(lobby);
        System.out.println("DEBUG: sto per chiamare notifyGameUpdated");
        notifyGameUpdated(lobby);
        notifyLobbyMessage(lobby, "La partita sta per iniziare.");
    }

    private void validatePlayerName(String playerName) throws RemoteException {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new RemoteException("Nome giocatore non valido.");
        }
    }

    private SessionData requireSession(String sessionId) throws RemoteException {
        SessionData session = sessions.get(sessionId);
        if (session == null) {
            throw new RemoteException("Sessione non trovata.");
        }
        return session;
    }

    private LobbyData requireLobby(String lobbyId) throws RemoteException {
        LobbyData lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            throw new RemoteException("Lobby non trovata.");
        }
        return lobby;
    }

    private String generateLobbyId() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private LobbyState buildLobbyState(LobbyData lobby) {
        List<PlayerInfo> players = new ArrayList<>();

        for (String playerSessionId : lobby.getPlayerSessionIds()) {
            SessionData session = sessions.get(playerSessionId);
            if (session != null) {
                players.add(new PlayerInfo(
                        session.getSessionId(),
                        session.getPlayerName(),
                        session.getScore()
                ));
            }
        }

        return lobby.toLobbyState(players);
    }

    private void notifyLobbyUpdated(LobbyData lobby) {
        LobbyState state = buildLobbyState(lobby);

        for (String playerSessionId : lobby.getPlayerSessionIds()) {
            SessionData session = sessions.get(playerSessionId);
            if (session == null) {
                continue;
            }

            try {
                session.getCallback().onLobbyUpdated(state);
            } catch (RemoteException e) {
                System.err.println("Errore callback onLobbyUpdated verso " + session.getPlayerName());
                e.printStackTrace();
            }
        }
    }

    private void notifyLobbyMessage(LobbyData lobby, String message) {
        for (String playerSessionId : lobby.getPlayerSessionIds()) {
            SessionData session = sessions.get(playerSessionId);
            if (session == null) {
                continue;
            }

            try {
                session.getCallback().onMessage(message);
            } catch (RemoteException e) {
                System.err.println("Errore callback onMessage verso " + session.getPlayerName());
                e.printStackTrace();
            }
        }
    }

    private GameState createInitialGameState(LobbyData lobby) {
        Map<String, List<QuestionCell>> board = new HashMap<String, List<QuestionCell>>();

        board.put("Storia", Arrays.asList(
                new QuestionCell("Storia", 100, "Anno scoperta America?", "1492"),
                new QuestionCell("Storia", 200, "Chi era Napoleone?", "Imperatore"),
                new QuestionCell("Storia", 300, "Caduta Impero Romano?", "476")
        ));

        board.put("Scienza", Arrays.asList(
                new QuestionCell("Scienza", 100, "H2O cos'è?", "Acqua"),
                new QuestionCell("Scienza", 200, "Pianeta rosso?", "Marte"),
                new QuestionCell("Scienza", 300, "Velocità luce?", "300000 km/s")
        ));

        String firstPlayer = lobby.getPlayerSessionIds().get(0);

        return new GameState(lobby.getLobbyId(), board, firstPlayer);
    }  

    private void notifyGameUpdated(LobbyData lobby) {
        GameState gameState = lobby.getGameState();

        for (String playerSessionId : lobby.getPlayerSessionIds()) {
            SessionData session = sessions.get(playerSessionId);
            if (session == null) continue;

            try {
                System.out.println("DEBUG: invio onGameUpdated a " + session.getPlayerName());
                session.getCallback().onGameUpdated(gameState);
            } catch (RemoteException e) {
                System.err.println("Errore callback onGameUpdated verso " + session.getPlayerName());
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void selectQuestion(String sessionId, String category, int value) throws RemoteException {
        SessionData session = requireSession(sessionId);
        LobbyData lobby = requireLobby(session.getLobbyId());
        GameState game = lobby.getGameState();

        if (!game.getCurrentTurnSessionId().equals(sessionId)) {
            throw new RemoteException("Non è il tuo turno.");
        }

        List<QuestionCell> cells = game.getBoard().get(category);

        QuestionCell selected = null;
        for (QuestionCell c : cells) {
            if (c.getValue() == value) {
                selected = c;
                break;
            }
        }

        if (selected == null) {
            throw new RemoteException("Domanda non trovata.");
        }

        if (selected.isUsed()) {
            throw new RemoteException("Domanda già usata.");
        }

        selected.setUsed(true);

        notifyGameUpdated(lobby);
    }

	/*
    public QuizServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String ping(String name) throws RemoteException {
        System.out.println("Ricevuta chiamata ping dal client con nome: " + name);
        return "Ciao " + name + ", il server RMI funziona correttamente!";
    }
	*/

    @Override
    public synchronized String leaveLobby(String sessionId) throws RemoteException {
        SessionData session = requireSession(sessionId);

        if (session.getLobbyId() == null) {
            throw new RemoteException("Non sei in nessuna lobby.");
        }

        LobbyData lobby = requireLobby(session.getLobbyId());
        String lobbyId = lobby.getLobbyId();

        session.setLobbyId(null);

        if (lobby.getOwnerSessionId().equals(sessionId)) {
            for (String playerSessionId : lobby.getPlayerSessionIds()) {
                if (!playerSessionId.equals(sessionId)) {
                    SessionData other = sessions.get(playerSessionId);
                    if (other != null) {
                        other.setLobbyId(null);
                        try {
                            other.getCallback().onMessage("La lobby è stata chiusa (owner uscito).");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            lobbies.remove(lobbyId);
            System.out.println("Lobby " + lobbyId + " chiusa (owner uscito).");

            return "Lobby " + lobbyId + " abbandonata. Eri l'owner, quindi la lobby è stata eliminata.";
        } else {
            lobby.removePlayer(sessionId);

            notifyLobbyUpdated(lobby);
            notifyLobbyMessage(lobby, session.getPlayerName() + " ha lasciato la lobby.");

            if (lobby.getPlayerSessionIds().isEmpty()) {
                lobbies.remove(lobbyId);
                System.out.println("Lobby eliminata perché vuota.");
            }

            return "Lobby " + lobbyId + " abbandonata.";
        }
    }

    @Override
        public synchronized void disconnect(String sessionId) throws RemoteException {
        SessionData session = sessions.get(sessionId);

        if (session == null) {
            return;
        }

        if (session.getLobbyId() != null) {
            leaveLobby(sessionId);
        }

        sessions.remove(sessionId);
        System.out.println("Sessione rimossa: " + sessionId);
    }

}

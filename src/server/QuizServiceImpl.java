package server;

import common.model.LobbyState;
import common.model.PlayerInfo;
import common.remote.ClientCallback;
import common.remote.QuizService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
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

        System.out.println("Lobby " + lobby.getLobbyId() + " avviata.");
        notifyLobbyUpdated(lobby);
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

}

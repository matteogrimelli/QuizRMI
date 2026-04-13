package client;

import java.rmi.Naming;
import java.util.Scanner;

import common.model.LobbyState;
import common.remote.QuizService;

public class QuizClientMain {

    private static void printPrompt() {
        //System.out.print("\nScegli comando: create | join | state | start | ping | exit\n");
        ConsoleUtils.printPrompt();
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            QuizService service = (QuizService) Naming.lookup("rmi://localhost/QuizService");
            ClientCallbackImpl callback = new ClientCallbackImpl();

            ConsoleUtils.print("Inserisci il tuo nome: ");
            String name = scanner.nextLine();

            String sessionId = service.createSession(name, callback);
            ConsoleUtils.println("Sessione creata con ID: " + sessionId);

            while (true) {
                printPrompt();
                String command = scanner.nextLine().trim().toLowerCase();

                try {
                    switch (command) {
                        case "create":
                            ConsoleUtils.println("Creazione Lobby - richiesta al server... ");    
                            String lobbyId = service.createLobby(sessionId);
                            ConsoleUtils.println("Lobby creata con ID: " + lobbyId);
                            break;

                        case "join":
                            ConsoleUtils.print("Inserisci lobby ID: ");
                            String joinLobbyId = scanner.nextLine().trim().toUpperCase();
                            LobbyState state = service.joinLobby(sessionId, joinLobbyId);
                            //printLobbyState(state);
                            break;

                        case "state":
                            LobbyState state2 = service.getLobbyState(sessionId);
                            printLobbyState(state2);
                            break;

                        case "start":
                            service.startLobby(sessionId);
                            ConsoleUtils.println("Richiesta di avvio inviata.");
                            break;

                        case "ping":
                            String response = service.ping(name);
                            ConsoleUtils.println(response);
                            break;

                        case "leave":
                            //service.leaveLobby(sessionId);
                            String leaveMessage = service.leaveLobby(sessionId);
                            ConsoleUtils.println(leaveMessage);
                            break;

                        case "exit":
                            try {
                                service.disconnect(sessionId);
                            } catch (Exception e) {
                                ConsoleUtils.println("Errore durante la disconnessione: " + e.getMessage());
                            }

                            ConsoleUtils.println("Chiusura client.");
                            return;

                        default:
                            ConsoleUtils.println("Comando non riconosciuto.");
                        }
                    } catch (Exception e) {
                    System.err.println("Operazione fallita: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Errore nel client:");
            e.printStackTrace();
        }
    }

    private static void printLobbyState(LobbyState state) {
        ConsoleUtils.println("\n--- STATO LOBBY ---");
        ConsoleUtils.println("Lobby ID: " + state.getLobbyId());
        ConsoleUtils.println("Owner session: " + state.getOwnerSessionId());
        ConsoleUtils.println("Started: " + state.isStarted());
        ConsoleUtils.println("Players:");

        state.getPlayers().forEach(player ->
                ConsoleUtils.println(" - " + player.getName() + " | score=" + player.getScore())
        );

        ConsoleUtils.println("-------------------\n");
    }
}
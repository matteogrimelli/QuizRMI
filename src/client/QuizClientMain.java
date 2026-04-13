package client;

import java.rmi.Naming;
import java.util.Scanner;

import common.model.LobbyState;
import common.remote.QuizService;

public class QuizClientMain {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            QuizService service = (QuizService) Naming.lookup("rmi://localhost/QuizService");
            ClientCallbackImpl callback = new ClientCallbackImpl();

            System.out.print("Inserisci il tuo nome: ");
            String name = scanner.nextLine();

            String sessionId = service.createSession(name, callback);
            System.out.println("Sessione creata con ID: " + sessionId);

            while (true) {
                System.out.println("\nScegli comando: create | join | state | start | ping | exit");
                String command = scanner.nextLine().trim().toLowerCase();

                try {
                    switch (command) {
                        case "create":
                            String lobbyId = service.createLobby(sessionId);
                            System.out.println("Lobby creata con ID: " + lobbyId);
                            break;

                        case "join":
                            System.out.print("Inserisci lobby ID: ");
                            String joinLobbyId = scanner.nextLine().trim().toUpperCase();
                            LobbyState state = service.joinLobby(sessionId, joinLobbyId);
                            printLobbyState(state);
                            break;

                        case "state":
                            LobbyState state2 = service.getLobbyState(sessionId);
                            printLobbyState(state2);
                            break;

                        case "start":
                            service.startLobby(sessionId);
                            System.out.println("Richiesta di avvio inviata.");
                            break;

                        case "ping":
                            String response = service.ping(name);
                            System.out.println(response);
                            break;

                        case "exit":
                            System.out.println("Chiusura client.");
                            return;

                        default:
                            System.out.println("Comando non riconosciuto.");
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
        System.out.println("\n--- STATO LOBBY ---");
        System.out.println("Lobby ID: " + state.getLobbyId());
        System.out.println("Owner session: " + state.getOwnerSessionId());
        System.out.println("Started: " + state.isStarted());
        System.out.println("Players:");

        state.getPlayers().forEach(player ->
                System.out.println(" - " + player.getName() + " | score=" + player.getScore())
        );

        System.out.println("-------------------\n");
    }
}
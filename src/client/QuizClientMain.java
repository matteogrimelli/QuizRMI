package client;

import common.remote.QuizService;

import java.rmi.Naming;
import java.util.Scanner;

//Client minimo per testare il collegamento al server RMI.
public class QuizClientMain {

	public static void main(String[] args) {
        try {
            // Recupera il riferimento remoto al servizio pubblicato dal server
            QuizService service = (QuizService) Naming.lookup("rmi://localhost/QuizService");

            Scanner scanner = new Scanner(System.in);

            System.out.print("Inserisci il tuo nome: ");
            String name = scanner.nextLine();

            // Invoca il metodo remoto
            String response = service.ping(name);

            // Stampa la risposta ricevuta dal server
            System.out.println("Risposta del server:");
            System.out.println(response);

        } catch (Exception e) {
            System.err.println("Errore nel client:");
            e.printStackTrace();
        }
    }
	
}

package util;

import datos.Chat;

import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket cliente = new Socket("localhost", 6000);
            Chat ventanaChat = new Chat(cliente); // Lanzo una nueva ventana con el chat
        } catch (IOException e) {
            System.out.println("Debes iniciar antes el servidor para abrir el chat");
        }
    }
}
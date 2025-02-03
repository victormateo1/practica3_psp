package util;

import datos.Chat;

import java.io.IOException;
import java.net.DatagramSocket;

public class Cliente {
    public static void main(String[] args) {
        try {
            DatagramSocket cliente = new DatagramSocket();
            Chat ventanaChat = new Chat(cliente); // Lanzo una nueva ventana con el chat
        } catch (IOException e) {
            System.out.println("Debes iniciar antes el servidor para abrir el chat");
        }
    }
}
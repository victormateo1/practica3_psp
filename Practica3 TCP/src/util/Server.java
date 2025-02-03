package util;

import datos.Intermediario;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(6000);
            System.out.println("Servidor TCP iniciado en el puerto " + server.getLocalPort());

            while (true) {
                Socket cliente = server.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());
                Thread t = new Thread(new Intermediario(cliente)); // Por cada usuario del chat, un hilo nuevo
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
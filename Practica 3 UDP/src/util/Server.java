package util;

import datos.Intermediario;

import java.net.DatagramSocket;

public class Server {
    public static void main(String[] args) {
        try {
            DatagramSocket server = new DatagramSocket(6001);
            System.out.println("Servidor UDP iniciado en el puerto " + server.getLocalPort());
            Thread t = new Thread(new Intermediario(server)); // Por cada usuario del chat, un hilo nuevo
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
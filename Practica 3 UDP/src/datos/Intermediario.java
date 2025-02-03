package datos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Intermediario extends Thread {
    private static final ArrayList<InetAddress> clienteDirecciones = new ArrayList<>();
    private static final ArrayList<Integer> clientePuertos = new ArrayList<>();
    private static final HashMap<String, Integer> usernames = new HashMap<>(); // Mapa de nombres de usuario a IDs
    private static final HashMap<String, String> clienteUsernames = new HashMap<>(); // Mapa de direcciones y puertos a nombres de usuario
    private DatagramSocket socket;
    private int idUser  = 0; // Contador para asignar IDs únicos

    public Intermediario(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] recibidos = new byte[1024];

        try {
            while (true) {
                DatagramPacket paqueteRecibido = new DatagramPacket(recibidos, recibidos.length);
                socket.receive(paqueteRecibido);
                String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                InetAddress direccion = paqueteRecibido.getAddress();
                int puerto = paqueteRecibido.getPort();
                String clave = direccion.getHostAddress() + ":" + puerto;

                if (mensaje.startsWith("USUARIO:")) {
                    String username = mensaje.split(":")[1].trim();
                    if (usernames.containsKey(username)) {
                        String respuesta = "ERROR: El nombre de usuario ya está en uso.";
                        socket.send(new DatagramPacket(respuesta.getBytes(), respuesta.length(), direccion, puerto));
                    } else {
                        usernames.put(username, idUser);
                        clienteUsernames.put(clave, username); // Asociar la dirección y puerto con el nombre de usuario
                        clienteDirecciones.add(direccion);
                        clientePuertos.add(puerto);
                        String respuesta = username + ", bienvenido al chat!";
                        socket.send(new DatagramPacket(respuesta.getBytes(), respuesta.length(), direccion, puerto));
                        idUser ++; // Incrementar el ID para el próximo usuario
                    }
                } else {
                    // Obtener el nombre de usuario correspondiente a la dirección y puerto
                    String username = clienteUsernames.get(clave);
                    if (username != null) {
                        // Enviar el mensaje a todos los clientes conectados
                        for (int i = 0; i < clienteDirecciones.size(); i++) {
                            String mensajeChat = username + ": " + mensaje;
                            socket.send(new DatagramPacket(mensajeChat.getBytes(), mensajeChat.length(), clienteDirecciones.get(i), clientePuertos.get(i)));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
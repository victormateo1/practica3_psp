package datos;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Chat extends JFrame {
    private JPanel panelMain;
    private JTextField campoMensaje;
    private JButton btnMensaje;
    private JTextField campoUsuario;
    private JButton btnUsuario;
    private JTextArea areaMensajes;
    private JLabel labelUsuario;
    private DatagramSocket socket;
    private String username;

    public Chat(DatagramSocket socket) {
        this.socket = socket;

        btnUsuario = new JButton("Iniciar Sesión");
        campoUsuario = new JTextField(20);
        campoUsuario.setVisible(true);

        btnMensaje = new JButton("Enviar Mensaje");
        campoMensaje = new JTextField(25);
        btnMensaje.setVisible(false);
        campoMensaje.setVisible(false);

        areaMensajes = new JTextArea(20, 30);
        areaMensajes.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensajes);

        labelUsuario = new JLabel("");

        panelMain = new JPanel();
        panelMain.add(campoUsuario);
        panelMain.add(btnUsuario);
        panelMain.add(labelUsuario);
        panelMain.add(campoMensaje);
        panelMain.add(btnMensaje);
        panelMain.add(scrollPane);

        btnUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = campoUsuario.getText().trim();
                if (!username.isEmpty()) {
                    try {
                        String message = "USUARIO:" + username;
                        byte[] sendData = message.getBytes();
                        InetAddress serverAddress = InetAddress.getByName("localhost");
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 6001);
                        socket.send(sendPacket);

                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);
                        String respuesta = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        if (respuesta.startsWith("ERROR:")) {
                            JOptionPane.showMessageDialog(btnUsuario, respuesta);
                        } else {
                            labelUsuario.setText("Usuario: " + username);
                            campoUsuario.setVisible(false);
                            btnUsuario.setVisible(false);
                            campoMensaje.setVisible(true);
                            btnMensaje.setVisible(true);
                            campoMensaje.requestFocus();
                            new Thread(new RecibirMensajes()).start(); // Iniciar el hilo para recibir mensajes
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(btnUsuario, "Rellene el campo por favor");
                }
            }
        });

        btnMensaje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensaje = campoMensaje.getText().trim();
                if (!mensaje.isEmpty()) {
                    try {
                        byte[] sendData = mensaje.getBytes();
                        InetAddress serverAddress = InetAddress.getByName("localhost");
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 6001);
                        socket.send(sendPacket);
                        campoMensaje.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(btnMensaje, "No se puede enviar un mensaje vacío.");
                }
            }
        });

        setContentPane(panelMain);
        setTitle("Chat");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class RecibirMensajes implements Runnable {
        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    areaMensajes.append(message + "\n");
                }
            } catch (Exception e) {
                System.out.println("Conexión cerrada: " + e.getMessage());
            }
        }
    }
}
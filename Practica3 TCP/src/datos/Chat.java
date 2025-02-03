package datos;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Chat extends JFrame {
    private JPanel panelMain;
    private JTextField campoMensaje;
    private JButton btnMensaje;
    private JTextField campoUsuario;
    private JButton btnUsuario;
    private JTextArea areaMensajes;
    private JLabel labelUsuario;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public Chat(Socket socket) {
        this.socket = socket;

        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                        out.writeUTF("USUARIO:" + username);
                        String respuesta = in.readUTF();
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
                    } catch (IOException ex) {
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
                        out.writeUTF(mensaje);
                        campoMensaje.setText("");
                    } catch (IOException ex) {
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
                while (true) {
                    areaMensajes.append(in.readUTF() + "\n");
                }
            } catch (IOException e) {
                System.out.println("Conexión cerrada: " + socket.getInetAddress());
            }
        }
    }
}
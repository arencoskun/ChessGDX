package me.aren.serverhelper.ui;

import me.aren.serverhelper.backend.ServerRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RunServerWindow {
    /*
    ServerRunner serverRunner = new ServerRunner("C:\\Program Files\\nodejs\\node.exe",
                        "C:\\Users\\Aren\\IdeaProjects\\ChessGDX\\chessgdx-server\\index.js",
                        8181,
                        serverOutputTextArea);
     */
    Thread serverThread;
    public RunServerWindow() throws IOException, InterruptedException {
        JFrame frame = new JFrame("ChessGDX Server Helper");
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        JLabel nodePathLabel = new JLabel("NodeJS executable:");
        JTextField nodePathTextField = new JTextField();
        JLabel serverPathLabel = new JLabel("Server executable:");
        JTextField serverPathTextField = new JTextField();
        JLabel portLabel = new JLabel("Port number:");
        JTextField portTextField = new JTextField();
        JButton startButton = new JButton("Start server");
        JButton stopButton = new JButton("Stop server");
        stopButton.setEnabled(false);
        JTextArea serverOutputTextArea = new JTextArea();
        serverOutputTextArea.setEditable(false);
        JScrollPane scroll = new JScrollPane (serverOutputTextArea,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        panel.setPreferredSize(new Dimension(300, 120));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(nodePathLabel);
        panel.add(nodePathTextField);
        panel.add(serverPathLabel);
        panel.add(serverPathTextField);
        panel.add(portLabel);
        panel.add(portTextField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(130, 0)));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JPanel outputPanel = new JPanel();
        outputPanel.setPreferredSize(new Dimension(300, 200));
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));

        outputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        outputPanel.add(scroll);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.add(panel);
        frame.add(buttonPanel);
        frame.add(outputPanel);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        frame.pack();

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerRunner serverRunner = new ServerRunner(nodePathTextField.getText(),
                        serverPathTextField.getText(),
                        Integer.parseInt(portTextField.getText()),
                        serverOutputTextArea);


                serverThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverRunner.runServer();
                        } catch (IOException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                serverOutputTextArea.append("--- SERVER STARTED " + dtf.format(now) + " ---\n");
                serverThread.start();
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                serverOutputTextArea.append("--- SERVER STOPPED " + dtf.format(now) + " ---\n");
                destroyNode();
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                destroyNode();
                System.exit(0);
            }
        });
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void destroyNode() {
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/IM", "node.exe");
        try {
            pb.start();
            System.out.println("DESTROYING NODE");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


    }
}

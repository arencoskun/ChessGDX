package me.aren.serverhelper.ui;

import me.aren.serverhelper.backend.ServerRunner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class RunServerWindow {
    /*
    ServerRunner serverRunner = new ServerRunner("C:\\Program Files\\nodejs\\node.exe",
                        "C:\\Users\\Aren\\IdeaProjects\\ChessGDX\\chessgdx-server\\index.js",
                        8181,
                        serverOutputTextArea);
     */
    Thread serverThread;
    Preferences pathPrefs;
    public RunServerWindow() throws IOException, InterruptedException {
        pathPrefs = Preferences.userRoot().node("ChessGDXServerHelper");
        JFrame frame = new JFrame("ChessGDX Server Helper");
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        JPanel nodePanel = new JPanel();
        JPanel serverExecutablePanel = new JPanel();
        nodePanel.setLayout(new BoxLayout(nodePanel, BoxLayout.LINE_AXIS));
        serverExecutablePanel.setLayout(new BoxLayout(serverExecutablePanel, BoxLayout.LINE_AXIS));
        JLabel nodePathLabel = new JLabel("NodeJS executable:");
        JTextField nodePathTextField = new JTextField();
        JFileChooser nodeChooser = new JFileChooser();
        nodeChooser.setDialogTitle("Select NodeJS executable");
        nodeChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        nodeChooser.setFileFilter(new FileNameExtensionFilter("*.exe", "exe"));
        nodeChooser.setAcceptAllFileFilterUsed(false);
        JFileChooser serverChooser = new JFileChooser();
        serverChooser.setDialogTitle("Select server main file");
        serverChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        serverChooser.setFileFilter(new FileNameExtensionFilter("*.js", "js"));
        serverChooser.setAcceptAllFileFilterUsed(false);
        JButton nodeBrowseButton = new JButton("Browse");
        JButton serverBrowseButton = new JButton("Browse");
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

        nodePathTextField.setText(pathPrefs.get("nodejs-path", ""));
        serverPathTextField.setText(pathPrefs.get("server-path", ""));
        portTextField.setText(String.valueOf(pathPrefs.getInt("port", 1234)));

        panel.setPreferredSize(new Dimension(300, 125));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(nodePathLabel);
        nodePanel.add(nodePathTextField);
        nodePanel.add(nodeBrowseButton);
        panel.add(nodePanel);
        panel.add(serverPathLabel);
        serverExecutablePanel.add(serverPathTextField);
        serverExecutablePanel.add(serverBrowseButton);
        panel.add(serverExecutablePanel);
        panel.add(portLabel);
        panel.add(portTextField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
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
                pathPrefs.put("nodejs-path", nodePathTextField.getText());
                pathPrefs.put("server-path", serverPathTextField.getText());
                pathPrefs.putInt("port", Integer.parseInt(portTextField.getText()));
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



        nodeBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = nodeChooser.showOpenDialog(frame);

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    nodePathTextField.setText(nodeChooser.getSelectedFile().getPath());
                }
            }
        });

        serverBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = serverChooser.showOpenDialog(frame);

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    serverPathTextField.setText(serverChooser.getSelectedFile().getPath());
                }
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

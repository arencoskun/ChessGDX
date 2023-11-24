package me.aren.serverhelper.backend;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerRunner {
    // C:\Users\Aren\IdeaProjects\ChessGDX\chessgdx-server\index.js
    // C:\Program Files\nodejs\node.exe
    String nodePath, serverPath;
    int port;
    JTextArea outputTextArea;
    public ServerRunner(String nodePath, String serverPath, int port, JTextArea outputTextArea) {
        this.nodePath = nodePath;
        this.serverPath = serverPath;
        this.port = port;
        this.outputTextArea = outputTextArea;
    }

    public void runServer() throws IOException, InterruptedException {


        ProcessBuilder pb = new ProcessBuilder("node", serverPath, "--port=" + port);
        pb.environment().put("Path", nodePath);
        Process p = pb.start();
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        Thread t = new Thread(new ServerOutputPipe(reader, p, outputTextArea));
        t.setName("ServerRunner");
        t.start();
        t.join();
    }
}

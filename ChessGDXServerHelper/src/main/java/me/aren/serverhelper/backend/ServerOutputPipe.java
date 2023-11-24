package me.aren.serverhelper.backend;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class ServerOutputPipe implements Runnable {
    BufferedReader reader;
    Process process;
    JTextArea outputTextArea;

    public ServerOutputPipe(BufferedReader reader, Process process, JTextArea outputTextArea) {
        this.reader = reader;
        this.process = process;
        this.outputTextArea = outputTextArea;
    }

    @Override
    public void run() {
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    System.out.println("The BufferedReader can no longer get input - the server is most probably closed.");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            outputTextArea.append(line);
            outputTextArea.append("\n");
            outputTextArea.setCaretPosition(outputTextArea.getText().length());
        }
    }


}

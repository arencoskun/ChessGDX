package me.aren.serverhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        ProcessBuilder pb = new ProcessBuilder("node", "C:\\Users\\Aren\\IdeaProjects\\ChessGDX\\chessgdx-server\\index.js", "--port=1234");
        pb.environment().put("Path", "C:\\Program Files\\nodejs\\node.exe");
        Process p = pb.start();
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
package me.aren.chessgdx;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.aren.chessgdx.ChessGdx;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(30);
		config.setResizable(false);
		config.setWindowedMode(768, 768);
		config.setTitle("ChessGDX");
		config.setWindowIcon(FileType.Internal, "icon.png");
		new Lwjgl3Application(new ChessGdx(), config);
	}
}

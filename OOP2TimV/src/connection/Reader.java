package connection;
import gui.*;

import java.awt.EventQueue;
import java.io.BufferedReader;

public class Reader extends Thread {

	private BufferedReader in;
	private MainFrame frame;

	public Reader(MainFrame frame, BufferedReader in) {
		this.frame = frame;
		this.in = in;
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				final String line = in.readLine();
				if (line == null) {
					break;
				}

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.onRemoteMove(line);
					}
				});

			}
		} catch (Exception ex) {
		} finally {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.onConnectionLost();
				}
			});

		}
	}
}
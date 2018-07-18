package connection;
import gui.*;

import static java.lang.System.out;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


 /**Serverska strana mrezne igre Checkers. Server osluskuje konekciju na zadatom
portu i obavestava prozor kad se klijent konektuje.**/

public class Server extends Thread {

	private MainFrame frame;
	private ServerSocket server;

	public Server(MainFrame frame, int port) throws IOException {
		try {
			this.frame = frame;
			server = new ServerSocket(port);
			out.println("Accepting connections on port " + port);
		} catch (IOException ex) {
			out.println("Could not listen on port " + port + ": " + ex.getMessage());
			throw ex;
		}
	}

	@Override
	public void run() {

		Socket client = null;
		BufferedReader in = null;
		PrintWriter socketOut = null;
		try {
			try {
				client = server.accept();
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				socketOut = new PrintWriter(client.getOutputStream(), true);
				out.println("Accepted a connection from " + client.getRemoteSocketAddress());
			} catch (IOException ex) {
				out.println("Cannot accept a connection: " + ex.getMessage());
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.onConnectionLost();
					}
				});
				return;
			}

			final Socket client2 = client;
			final BufferedReader in2 = in;
			final PrintWriter out2 = socketOut;
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.onConnectionEstablished(client2, in2, out2);
				}
			});
		} finally {
			close();
		}

	}

	public void close() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
			}
			server = null;
		}
	}
}

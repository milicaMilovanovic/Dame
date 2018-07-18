package gui;
import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import connection.Reader;
import connection.Server;
import game.Field;
import game.GameRules;
import game.GameState;


public class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static final int NUM_BUTTONS=100;
	
	private GameState state;

	private Server server;
	private Socket opponent;
	private PrintWriter socketOut;
	
	private ImageIcon iconBlack, iconWhite;  
	private ImageIcon queenBlack, queenWhite;
	
	private JButton[] buttons;     

	private boolean white;
	private boolean closing;
	private boolean myMove;
	
	//da li je kraj igre, ako jeste poruka koja treba da se ispise
	String str;
	
	//polje koje belezi da li je potez prvi ili drugi, 
	//tj da li biramo polje sa kojeg pomeramo ili na koje pomeramo
	private boolean isFirstMove;
	
	//belezenje prvog i drugog poteza
	private String firstMoveCoordinates, secondMoveCoordinates;
	
	//belezenje tipa figure prvog poteza
	boolean isFirstMoveFigureCircle;
	
	//pomocni StringBuilder preko kojeg gradimo poruke za slanje
	StringBuilder sb = new StringBuilder();
	
	//polja klasa
	private GameRules gameRules = new GameRules();

	JLabel label;
	
	//konstruktor
	public MainFrame() {
		
		iconWhite = new ImageIcon(getClass().getResource("images/white_circle.png"));
		iconBlack = new ImageIcon(getClass().getResource("images/black_circle.png"));
		queenBlack = new ImageIcon(getClass().getResource("images/queenB.png"));
		queenWhite = new ImageIcon(getClass().getResource("images/queenW.png"));
		initGui();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				closing = true;
				if (server != null) {
					server.close();
					server = null;
				}
				if (opponent != null) {
					try {
						opponent.close();
					} catch (IOException ex) {
					}
					opponent = null;
				}
			}
		});
		
	}
	
	
	private void initGui() {
		
		JPanel top = new JPanel();
		final JButton btnServer = new JButton("Server");
		top.add(btnServer);
		final JButton btnClient = new JButton("Klijent");
		top.add(btnClient);

		btnServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String val = JOptionPane.showInputDialog(MainFrame.this, "Server port:", "6060");
				if (val == null || val.length() == 0)
					return;
				try {
					int port = Integer.parseInt(val);
					// Pokreni server na ovom portu
					server = new Server(MainFrame.this, port);
					server.start();
					getContentPane().removeAll();
					add(new JLabel("Cekanje na klijenta..."));
					validate(); // Kada menjamo komponente u prozoru,
					repaint();  // moramo pozvati ove metode da bi se izmene videle
				} catch (Exception ex) {
					out.println("Greska pri startovanju servera: " + ex.getMessage());
					JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String val = JOptionPane.showInputDialog(MainFrame.this, "Adresa servera i port:", "localhost:6060");
				if ((val == null) || (val.length() == 0)) {
					return;
				}
				try {
					int n = val.indexOf(':');
					String host = val.substring(0, n);
					int port = Integer.parseInt(val.substring(n + 1));
				
					Socket client = new Socket(host, port);
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					PrintWriter out = new PrintWriter(client.getOutputStream(), true);
					// Klijent je uvek beli i igra prvi
					white = true;
					myMove = true;
					onConnectionEstablished(client, in, out);
				} catch (Exception ex) {
					out.println("Greska pri startovanju servera: " + ex.getMessage());
					JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		add(top, BorderLayout.NORTH);

	}

	public void onConnectionEstablished(Socket client, BufferedReader in, PrintWriter out) {

		this.opponent = client;
		this.socketOut = out;
		new Reader(this, in).start();
		setTitle("Dame (checkers/draughts) - " + (white ? "Bela figura" : "Crna figura"));

		getContentPane().removeAll();

		initTable();
		validate();
		repaint();

	}
	
	private void initTable() {
	
		state = new GameState();
		
		buttons=new JButton[NUM_BUTTONS];
		//true jer nismo nista uradili
		isFirstMove = true;
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10,10));
		add(panel);
		if(myMove) {
			label = new JLabel("Vi ste na potezu");
		}else {
			label = new JLabel("Protivnik je na potezu");
		}
		JPanel north = new JPanel();
		north.add(label);
		add(north, BorderLayout.NORTH);
		//brojac za dodavanje u niz
		int brojac=0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				JButton btn = new JButton();
				btn.setActionCommand(i + "," + j);
				if((i + j) % 2 == 0) {
					btn.setBackground(Color.WHITE);
				}else {
					btn.setBackground(Color.DARK_GRAY);
				}
				buttons[brojac++] = btn;
				panel.add(btn);
				if ((i + j) % 2 != 0) {
					if (i * 10 + j < 40) {
						btn.setIcon(iconBlack);
					}
					else if (i * 10 + j > 60) {
						btn.setIcon(iconWhite);
					}
				}
				btn.addActionListener(this);
			}
		}
	}
	
	public void onConnectionLost() {
		if (!closing) {
			JOptionPane.showMessageDialog(this, "Izgubljena konekcija, kraj igre.");
			dispose();
		}
	}
	
	public void endGame(String str){
		JOptionPane.showMessageDialog(this, str + "\n Kraj igre.");
		dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!myMove) {
			JOptionPane.showMessageDialog(null, "Protivnik je sada na potezu");
				return;
		}
		
		//za testiranje
		/*if(white) {   
			System.out.println("Ja sam BELI: ");
		} else {
			System.out.println("Ja sam CRNI: ");
		}*/
			
		JButton b = (JButton) e.getSource();
		String btnCoordinates = b.getActionCommand();
		if (isFirstMove) {
			if (b.getIcon() != null) {  //znaci da nije prazno polje
				firstMoveCoordinates = btnCoordinates;
				isFirstMove = false; 
				sb.append(firstMoveCoordinates);
				Field field = new Field(firstMoveCoordinates);
				//imam podatak da li je prva selektovana figura kraljica kruzic ili kraljica
				isFirstMoveFigureCircle = field.isFieldCircle(state);
			}
		}
		else {  //drugi potez
			if(isFirstMoveFigureCircle) {
				//ako je legalan potez
				secondMoveCoordinates=btnCoordinates;
				
				if(gameRules.isLegalCircleMove(firstMoveCoordinates, secondMoveCoordinates, state, white)) {
					gameRules.makeCircleMove(firstMoveCoordinates, secondMoveCoordinates, buttons, white, state, queenWhite, queenBlack);
					gameRules.transformIntoQueen(secondMoveCoordinates, state, buttons, queenWhite, queenBlack, white);
					sb.append("-");
					
					sb.append(secondMoveCoordinates);
					String move = sb.toString();
					socketOut.println(move);
					System.out.println(move);
					myMove = false;
				} 
			} else {  //znaci da je prva figura bila kraljica
				secondMoveCoordinates=btnCoordinates; 
				if(gameRules.isLegalQueenMove(firstMoveCoordinates, secondMoveCoordinates, state, white)) {
					gameRules.moveQueen(firstMoveCoordinates, secondMoveCoordinates, state, white, buttons);
					sb.append("-");
					sb.append(secondMoveCoordinates);
					String move = sb.toString();
					socketOut.println(move);
					System.out.println(move);
					myMove = false;
				}
				
			}
			//resetuje pomocne promenljive
			isFirstMove = true; 
			sb.setLength(0);
		}
	
		//ovo se proverava po odigranom bilo kom potezu, a moglo bi da proverava ko je pobednik samo po drugom
		str=state.checkState(white);
		if(!str.equals("false")){
			closing = true;
			endGame(str);
		}else if(!myMove) {
			label.setText("Protivnik je na potezu");

		}
	}

	public void onRemoteMove(String move) {
		//ovde jos treba da updejtujem stanja
		if (myMove) {
			return;
		}
		
		String[] positions = move.split("-");
		String from = positions[0];
		String to = positions[1];
		
		Field field = new Field(from);
		boolean isReceivedMoveFigureCircle = field.isFieldCircle(state);
		
		if(isReceivedMoveFigureCircle) {
			gameRules.makeCircleMove(from, to, buttons, white, state, queenWhite, queenBlack);
			gameRules.transformIntoQueenRemote(to, state, buttons, queenWhite, queenBlack, white);
		} else { //pristigli potez je kraljica
			gameRules.moveQueenRemote(from, to, state, white, buttons);
		}
		
		
		str=state.checkState(white);
		if(!str.equals("false")){
			closing = true;
			endGame(str);
		}
		myMove = true;
		
		 if(myMove) {
			label.setText("Vi ste na potezu");
		}
	}
	

	public static void main(String[] args) {
		try {
	    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	     	   	if ("Nimbus".equals(info.getName())) {
	     	   		UIManager.setLookAndFeel(info.getClassName());
	     	   		break;
	     	   	}
	    	}
		} catch (Exception e) {
	    // Ako nije instaliran, ostavimo trenutni
		}
		JFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(700, 700);
		frame.setResizable(false);
		frame.setTitle("Dame");
		frame.setVisible(true);
	}
}
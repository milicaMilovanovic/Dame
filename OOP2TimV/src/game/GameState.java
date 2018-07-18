package game;

public class GameState {
	
	//vrednosti: (-1)-belo polje, (1)-beli krug, (2)-crni krug, (3)-prazno crno, (4)-bela kraljica, (5)-crna kraljica - mozda bi bilo dobro da ovo budu enumi
	private int [][] state;   //promenila iz stanje u state
	
	private static final int NUM_COLS = 10; 
	private static final int NUM_ROWS = 10;
	
	//broj preostalih belih i crnih
	private int numWhite = 20;
	private int numBlack = 20;
	
	
	public GameState() {
		this.state = new int[NUM_ROWS][NUM_COLS];
		initState();
	}
	
	//inicijalizuje matricu
	private void initState() { //tabela je inicijalizovana naopacke
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++)  {
				if((i + j) % 2 == 1) { 
					if (i * 10 + j < 40) {       //crni igrac
						state[i][j] = 2;
					}
					else if (i * 10 + j > 60) {  //beli igrac
						state[i][j] = 1;
					}
					else {                 //prazno crno polje
						state[i][j] = 3; 
					}
				}
				else {
					state[i][j] = -1;	   //prazno belo polje
				}
			}
		}
	}

	public int[][] getState() {
		return state;
	}

	public int getStateValue(int x, int y) {
		return state[x][y];
	}
	
	public void setStateValue(int x, int y, int newValue) {
		state[x][y] = newValue;
	}
	
	public void setNumWhite() {
		this.numWhite--;
	}

	public void setNumBlack() {
		this.numBlack--;
	}
	
	
	
	//za testiranje
	/*public void print() {
		for(int i = 0; i < NUM_ROWS; i++) {
			for(int j = 0; j < NUM_COLS; j++) {
				System.out.printf("%2d  ", state[i][j]);
			}
			System.out.println();
		}
	}*/
	
	public String checkState(boolean white) {
		String str;
		if(numWhite == 0) {
			str = checkWinner(1, white);//pobedio crni
		}
		else if(numBlack == 0) {
			str = checkWinner(2,white);//pobedio beli
		}
		else if(blokiranBeli()) {
			str = checkWinner(1, white);//pobedio crni
		}
		else if(blokiranCrni()) {
			str = checkWinner(2, white);//pobedio beli
		}else {
			str = "false";//igra se nastavlja
		}
		return str;
	}
	
	private String checkWinner(int br, boolean white) {
		String str;
		if(br == 1) {
			str = white ? "Izgubio/la si! :(" : "Pobedio/la si! :)";
		}
		else {
			str = white ? "Pobedio/la si! :)" : "Izgubio/la si! :(";
		}
		return str;
	}

	//proverava da li postoji jos mogucih poteza
	private boolean blokiranBeli() {
		for(int i = 0; i < NUM_ROWS; i++) {
			for(int j = 0; j < NUM_COLS; j++) {
				//ako je bela dama na polju onda proverimo da li je blokirana
				if(state[i][j] == 4){
					if (checkQueen(i, j, 4) == true) {
						return false;
					}
				//ako je beli na polju, proverimo da li je blokiran
				}
				else if(state[i][j] == 1) {
					if(checkWhite(i, j) == true) {
						return false;
					}
				}
			}
		}
		//ako su svi beli blokirani onda je kraj igre
		return true;
	}
	
	
	private boolean blokiranCrni() {
		for(int i = 0; i < NUM_ROWS; i++) {
			for(int j = 0; j < NUM_COLS; j++) {
				//ako je crna dama na polju onda proverimo da li je blokirana
				if(state[i][j] == 5){
					if (checkQueen(i, j, 5) == true) {
						return false;
					}
				//ako je crni na polju, proverimo da li je blokiran
				}
				else if(state[i][j] == 2) {
					if(checkBlack(i, j) == true) {
						return false;
					}
				}
			}
		}
		//ako je crni blokiran onda je kraj igre
		return true;
	}

	//proverava da li bela dama ili crna dama mogu da se pomeraju
	private boolean checkQueen(int i, int j, int k) {
		int n;//suprotna dama
		if(k == 4) {
			n = 5;
		}else {
			n = 4;
		}
		//ako je bilo gde oko nje prazno onda moze da se krece
		if((exists(i - 1, j - 1) && state[i - 1][j - 1] == 3) ||
			(exists(i - 1, j + 1) && state[i - 1][j + 1] == 3) ||
			(exists(i + 1, j - 1) && state[i + 1][j - 1] == 3) ||
			(exists(i + 1, j + 1) && state[i + 1][j + 1] == 3)) {
			return true;
		}
		//ako je levo gore suprotni ili suprotna dama proveriti da li moze da ih pojede
		else if(exists(i - 1, j - 1) && (state[i - 1][j - 1] == n - 3 || state[i - 1][j - 1] == n)) {
			if(exists(i - 2, j - 2) && state[i - 2][j - 2] == 3) {
				return true;
			}
		}
		//ako je desno gore suprotni ili suprotna dama proveriti da li moze da ih pojede
		else if(exists(i - 1, j + 1) && (state[i - 1][j + 1] == n - 3 || state[i - 1][j + 1] == n)) {
			if(exists(i - 2, j + 2) && state[i - 2][j + 2] == 3) {
				return true;
			}
		}
		//ako je levo dole suprotni ili suprotna dama proveriti da li moze da ih pojede
		else if(exists(i + 1, j - 1) && (state[i + 1][j - 1] == n - 3 || state[i + 1][j - 1] == n)) {
			if(exists(i + 2, j - 2) && state[i + 2][j - 2] == 3) {
				return true;
			}
		}
		//ako je desno dole suprotni ili suprotna dama proveriti da li moze da ih pojede
		else if(exists(i + 1, j + 1) && (state[i + 1][j + 1] == n - 3 || state[i + 1][j + 1] == n)) {
			if(exists(i + 2, j + 2) && state[i + 2][j + 2] == 3) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	private boolean checkWhite(int i, int j) {
		//da li je gore prazno
		if((exists(i - 1, j - 1) && state[i - 1][j - 1] == 3) || (exists(i - 1, j + 1) && state[i - 1][j + 1] == 3)) {
			return true;
		}		
		//ako je levo gore crni ili crna dama proveriti da li moze da ih pojede
		else if(exists(i - 1, j - 1) && (state[i - 1][j - 1] == 2 || state[i - 1][j - 1] == 5)) {
			if(exists(i - 2, j - 2) && state[i - 2][j - 2] == 3) {
				return true;
			}
		}
		//ako je desno gore crni ili crna dama proveriti da li moze da ih pojede
		else if(exists(i - 1, j + 1) && (state[i - 1][j + 1] == 2 || state[i - 1][j + 1] == 5)) {
			if(exists(i - 2, j + 2) && state[i - 2][j + 2] == 3) {
				return true;
			}
		}
		//ako je levo dole crni ili crna dama proveriti da li moze da ih pojede
		else if(exists(i + 1, j - 1) && (state[i + 1][j - 1] == 2 || state[i + 1][j - 1] == 5)) {
			if(exists(i + 2, j - 2) && state[i + 2][j - 2] == 3) {
				return true;
			}
		}
		//ako je desno dole crni ili crna dama proveriti da li moze da ih pojede
		else if(exists(i + 1, j + 1) && (state[i + 1][j + 1] == 2 || state[i + 1][j + 1] == 5)) {
			if(exists(i + 1, j + 1) && state[i + 2][j + 2] == 3) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkBlack(int i, int j) {
		if((exists(i + 1, j - 1) && state[i + 1][j - 1] == 3) || (exists(i + 1, j + 1) && state[i + 1][j + 1] == 3)) {
			return true;
		}
		//ako je levo gore beli ili bela dama proveriti da li moze da ih pojede
		else if(exists(i - 1, j - 1) && (state[i - 1][j - 1] == 1 || state[i - 1][j - 1] == 4)) {
			if(exists(i - 2, j - 2) && state[i - 2][j - 2] == 3) {
				return true;
			}
		}
		//ako je desno gore bela ili bela dama proveriti da li moze da ih pojede
		else if(exists(i - 1, j + 1) && (state[i - 1][j + 1] == 1 || state[i - 1][j + 1] == 4)) {
			if(exists(i - 2, j + 2) && state[i - 2][j + 2] == 3) {
				return true;
			}
		}
		//ako je levo dole beli ili bela dama proveriti da li moze da ih pojede
		else if(exists(i + 1, j - 1) && (state[i + 1][j - 1] == 1 || state[i + 1][j - 1] == 4)) {
			if(exists(i + 2, j - 2) && state[i + 2][j - 2] == 3) {
				return true;
			}
		}
		//ako je desno dole beli ili bela dama proveriti da li moze da ih pojede
		else if(exists(i + 1, j + 1) && (state[i + 1][j + 1] == 1||state[i + 1][j + 1] == 4)) {
			if(exists(i + 1, j + 1) && state[i + 2][j + 2] == 3) {
				return true;
			}
		}
		return false;
	}
	
	private boolean exists(int i, int j) {
		if(i >= 0 && i < NUM_ROWS && j >= 0 && j < NUM_COLS) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*public static void main(String[] args) {
		GameState gs = new GameState();
		gs.print();
		System.out.println("Promenljeno stanje");
		gs.print();
		System.out.println("Vrednost na (1, 1) je " + gs.getStateValue(1, 1));
	}*/

}
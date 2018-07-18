package game;

public class Field {
	
	private int x;
	private int y;
	
	public Field(String btnCoordinates) {
		String[] coordinates = btnCoordinates.split(",");
		this.x = Integer.parseInt(coordinates[0]);
		this.y = Integer.parseInt(coordinates[1]);
	}
	
	public Field(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isFieldWhiteQueen(GameState state){
		return state.getStateValue(x, y) == 4;
	}
	
	public boolean isFieldBlackQueen(GameState state) {
		return state.getStateValue(x,y) == 5;
	}
	
	public boolean isFieldQueen(GameState state) {
		return isFieldWhiteQueen(state) || isFieldBlackQueen(state);
	}
	
	public boolean isFieldBlackCircle(GameState state) {
		return state.getStateValue(x,y) == 2;
	}
	
	public boolean isFieldWhiteCircle(GameState state) {
		return state.getStateValue(x,y) == 1;
	}
	
	public boolean isFieldCircle(GameState state) {
		return isFieldWhiteCircle(state) || isFieldBlackCircle(state);
	}
	
	public boolean isFieldEmptyBlack(GameState state) {
		return state.getStateValue(x,y) == 3;
	}

}

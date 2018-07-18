package game;


import javax.swing.ImageIcon;
import javax.swing.JButton;

public class GameRules {
	
	
	public void makeCircleMove(String firstMoveCoordinates, String secondMoveCoordinates, JButton[] buttons, boolean white, GameState state, ImageIcon queenWhite, ImageIcon queenBlack) {
		moveFigure(buttons, firstMoveCoordinates, secondMoveCoordinates);
		
		Field firstMove = new Field(firstMoveCoordinates);
		Field secondMove = new Field(secondMoveCoordinates);
		
		if(canCircleEat(firstMoveCoordinates, secondMoveCoordinates, state)) {
			int middleFigureX = (firstMove.getX() + secondMove.getX()) / 2;
			int middleFigureY = (firstMove.getY() + secondMove.getY()) / 2;
			removeFigure(middleFigureX + "," + middleFigureY, buttons, state);
		}
		updateStateAfterMove(firstMoveCoordinates, secondMoveCoordinates, state); 	
	}
	
	
	public boolean isLegalCircleMove(String firstMoveCoordinates, String secondMoveCoordinates, GameState state, boolean white) {
		Field firstMove = new Field(firstMoveCoordinates);
		Field secondMove = new Field(secondMoveCoordinates);
		
		if(!white) {  //ako je red na crnog
			return isLegalBlackCircleMove(firstMove, secondMove, state);
			
		} else { //znaci da je beli na potezu
			return isLegalWhiteCircleMove(firstMove, secondMove, state);
		}
	}
	
	public boolean isLegalWhiteCircleMove(Field firstMove, Field secondMove, GameState state) {
		if(firstMove.isFieldBlackCircle(state) || firstMove.isFieldBlackQueen(state)) {
			return false;
		}
		if(!secondMove.isFieldEmptyBlack(state)) {
			return false;
		}
		if(((firstMove.getX()-1 == secondMove.getX()) && (firstMove.getY()+1 == secondMove.getY() || firstMove.getY()-1 == secondMove.getY())) || canWhiteCircleEat(firstMove,secondMove,state)) {
			return true;
		}
		
		return false; 
	}
	
	public boolean isLegalBlackCircleMove(Field firstMove, Field secondMove, GameState state) {
		if(firstMove.isFieldWhiteCircle(state) || firstMove.isFieldWhiteQueen(state)) {
			return false;
		}
		if(!secondMove.isFieldEmptyBlack(state)) {
			return false;
		}
		if((firstMove.getX()+1 == secondMove.getX()) && (firstMove.getY()+1 == secondMove.getY() || firstMove.getY()-1 == secondMove.getY())) {
			return true;
		}
		if(canBlackCircleEat(firstMove, secondMove, state)) {
			return true;
		} 
		return false;
	}
	
	public boolean canWhiteCircleEat(Field firstMove, Field secondMove, GameState state) {
		if( (Math.abs(firstMove.getX() - secondMove.getX()) == 2) && (Math.abs(firstMove.getY() - secondMove.getY()) == 2) ) {
			int middleFigureX = (firstMove.getX() + secondMove.getX()) / 2;
			int middleFigureY = (firstMove.getY() + secondMove.getY()) / 2;
			
			Field middleFigure = new Field(middleFigureX, middleFigureY);
			
			if(middleFigure.isFieldWhiteCircle(state) || middleFigure.isFieldWhiteQueen(state) || middleFigure.isFieldEmptyBlack(state)) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean canBlackCircleEat(Field firstMove, Field secondMove, GameState state) {
		if( (Math.abs(firstMove.getX() - secondMove.getX()) == 2) && (Math.abs(firstMove.getY() - secondMove.getY()) == 2) ) {
			int middleFigureX = (firstMove.getX() + secondMove.getX()) / 2;
			int middleFigureY = (firstMove.getY() + secondMove.getY()) / 2;
			
			Field middleFigure = new Field(middleFigureX, middleFigureY);
			
			if(middleFigure.isFieldBlackCircle(state) || middleFigure.isFieldBlackQueen(state) || middleFigure.isFieldEmptyBlack(state)) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean canCircleEat(String firstMoveCoordinates, String secondMoveCoordinates, GameState state) {
		Field firstMove = new Field(firstMoveCoordinates);
		Field secondMove = new Field(secondMoveCoordinates);
		return canBlackCircleEat(firstMove,secondMove, state) || canWhiteCircleEat(firstMove, secondMove, state);
	}

	public void moveFigure(JButton [] buttons, String prvi, String drugi) {
		
		JButton b1 = null;
		JButton b2 = null;
		for (JButton b : buttons) {
			if (b.getActionCommand().equals(prvi)) {
				b1 = b;
			}
			else if(b.getActionCommand().equals(drugi)) {
				b2 = b;
			}
		}
		
		ImageIcon img=(ImageIcon) b1.getIcon();
		b1.setIcon(null);
		b2.setIcon(img);
	}
	
	public void updateStateAfterMove(String prvi, String drugi, GameState state) {	
		//state.print();
		String[] coordinatesS1 = prvi.split(",");
		String[] coordinatesS2 = drugi.split(",");
		int s1X = Integer.parseInt(coordinatesS1[0]);
		int s1Y = Integer.parseInt(coordinatesS1[1]);
		
		int s2X = Integer.parseInt(coordinatesS2[0]);
		int s2Y = Integer.parseInt(coordinatesS2[1]);
		
		state.setStateValue(s2X, s2Y, state.getStateValue(s1X, s1Y));
		state.setStateValue(s1X, s1Y, 3); //3 je prazno crno polje
		//state.print();
	}

	//ovo proverava da li se neki krug pretvara u kraljicu
	public boolean transformIntoQueen(String drugi, GameState state, JButton[] buttons, ImageIcon queenWhite, ImageIcon queenBlack, boolean white) {
		String [] secondMove=drugi.split(",");
		int x = Integer.parseInt(secondMove[0]);
		int y = Integer.parseInt(secondMove[1]);
		//ako je vec kraljica
		if (state.getStateValue(x,y) == 4 || state.getStateValue(x,y) == 5) 
			return false;
		if (white) {
			if (x == 0) {
				state.setStateValue(x, y, 4);
				String position = x + "," + y;
				for (JButton button : buttons) {
					if (button.getActionCommand().equals(position)) {
						button.setIcon(queenWhite);
						break;
					}
				}
				return true;
			}
		}
		else {
			if (x == 9) {
				state.setStateValue(x, y, 5);
				String position = x + "," + y;
				for (JButton button : buttons) {
					if (button.getActionCommand().equals(position)) {
						button.setIcon(queenBlack);
						break;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean transformIntoQueenRemote(String drugi, GameState state, JButton[] buttons, ImageIcon queenWhite, ImageIcon queenBlack, boolean white) {
		String [] secondMove = drugi.split(",");
		int x = Integer.parseInt(secondMove[0]);
		int y = Integer.parseInt(secondMove[1]);
		//ako je vec kraljica
		if (state.getStateValue(x,y) == 4 || state.getStateValue(x,y) == 5) 
			return false;
		if (!white) {
			if (x == 0) {
				state.setStateValue(x, y, 4);
				String position = x + "," + y;
				for (JButton button : buttons) {
					if (button.getActionCommand().equals(position)) {
						button.setIcon(queenWhite);
						break;
					}
				}
				return true;
			}
		}
		else {
			if (x == 9) {
				state.setStateValue(x, y, 5);
				String position = x + "," + y;
				for (JButton button : buttons) {
					if (button.getActionCommand().equals(position)) {
						button.setIcon(queenBlack);
						break;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean isLegalQueenMove(String firstMoveCoordinates, String secondMoveCoordinates, GameState state, boolean white) {
		Field firstMove = new Field(firstMoveCoordinates);
		Field secondMove = new Field(secondMoveCoordinates);
		
		//da li je slobodno drugo polje
		if(!secondMove.isFieldEmptyBlack(state)) {
			return false;
		}
		if (white) {
			if (firstMove.isFieldBlackCircle(state) || firstMove.isFieldBlackQueen(state)) {
				return false;
			}
		}
		else {
			if (firstMove.isFieldWhiteCircle(state) || firstMove.isFieldWhiteQueen(state)) {
				return false;
			}
		}
				
		//da li se krece kao lovac
		if(Math.abs(firstMove.getX()-secondMove.getX()) == (Math.abs(firstMove.getY() - secondMove.getY()))) {
			if (getInfoAboutQueenEating(firstMoveCoordinates, secondMoveCoordinates, state, white).equals("-1")) {
				return false; 
			} else {
				return true;
			}
		}
		return false;
	}
	
	public void moveQueen(String prvi, String drugi, GameState state, boolean white, JButton[] buttons) {
		//na osnovu ovih coordinata zakljucujemo da li je bilo jedenja
		String coordinatesFigureToEat = getInfoAboutQueenEating(prvi, drugi, state, white);
		moveFigure(buttons, prvi, drugi);
		//0 znaci da nije bilo jedenja
		if(!getInfoAboutQueenEating(prvi, drugi, state, white).equals("0")) {
			//znaci da jedemo
			removeFigure(coordinatesFigureToEat, buttons, state);
		}
		updateStateAfterMove(prvi, drugi, state);
	}
	
	public void moveQueenRemote(String prvi, String drugi, GameState state, boolean white, JButton[] buttons ) {
		//posto moveQueen zavisi od boolean promenljive white koja oznacava redosled, 
		//pozvacemo sve iste metode samo sa !white
		boolean turn = !white; //suprotan od onog ko je poslao potez
		String coordinatesFigureToEat = getInfoAboutQueenEating(prvi, drugi, state, turn);
		moveFigure(buttons, prvi, drugi);
		if(!getInfoAboutQueenEating(prvi, drugi, state, turn).equals("0")) {
			removeFigure(coordinatesFigureToEat, buttons, state);
		}
		updateStateAfterMove(prvi, drugi, state);
	}
	
	private String getInfoAboutQueenEating(String prvi, String drugi, GameState state, boolean white) {
		String[] coordinatesS1 = prvi.split(",");
		String[] coordinatesS2 = drugi.split(",");
		int s1X = Integer.parseInt(coordinatesS1[0]);
		int s1Y = Integer.parseInt(coordinatesS1[1]);
		
		//drugo kliknuto polje
		int s2X = Integer.parseInt(coordinatesS2[0]);
		int s2Y = Integer.parseInt(coordinatesS2[1]);
		
		 String info;
		 String direction = getDirectionOfQueenMovement(prvi, drugi);
         if (direction.equals("goreLevo")) {
         	 info=getQueenMovementInfo("goreLevo", s1X, s1Y, s2X, s2Y, white, state);
         }
         else if (direction.equals("goreDesno")) {
         	 info=getQueenMovementInfo("goreDesno", s1X, s1Y, s2X, s2Y, white, state);
         }
         else if (direction.equals("doleLevo")) {
         	 info=getQueenMovementInfo("doleLevo", s1X, s1Y, s2X, s2Y, white, state);
         }
         else {
         	 info=getQueenMovementInfo("doleDesno", s1X, s1Y, s2X, s2Y, white, state);
         }
        
         return info;
	}
	
	private String getDirectionOfQueenMovement(String prvi, String drugi) {
		String[] coordinatesS1 = prvi.split(",");
		String[] coordinatesS2 = drugi.split(",");
		int s1X = Integer.parseInt(coordinatesS1[0]);
		int s1Y = Integer.parseInt(coordinatesS1[1]);
		
		//drugo kliknuto polje
		int s2X = Integer.parseInt(coordinatesS2[0]);
		int s2Y = Integer.parseInt(coordinatesS2[1]);
		
		if (Math.abs(s1X - s2X) == (Math.abs(s1Y - s2Y))) {
			if (s1X - s2X > 0) {      
				if (s1Y - s2Y > 0) {  
					return "goreLevo";
				}
				else {           
					return "goreDesno";
				}
			}
			else {                
                if (s1Y - s2Y > 0) { 
                	return "doleLevo";
				}
				else {            
					return "doleDesno";
				}
			}
		} else {
			return null;
		}
	}
	
	//uklanja pojedenog
		private void removeFigure(String pozicija, JButton [] buttons, GameState state) {
			Field pozicijaField = new Field(pozicija);
			int x = pozicijaField.getX();
			int y = pozicijaField.getY();
			if(state.getStateValue(x, y) == 1 || state.getStateValue(x, y) == 4) {
				state.setNumWhite();
			}else if(state.getStateValue(x, y) == 2 || state.getStateValue(x, y) == 5) {
				state.setNumBlack();
			}
			for (JButton button : buttons) {
				if (button.getActionCommand().equals(pozicija)) {
					button.setIcon(null);
					state.setStateValue(x, y, 3);
					break;
				}
			}
		}
		
		//vraca poziciju pojedenog ako je ima, proverava da li je jeo uopste ili da li je otisao predaleko
		private String getQueenMovementInfo(String pozicija, int s1X, int s1Y, int s2X, int s2Y, boolean white, GameState state) {
			int y1 = s1Y;
			if (pozicija.equals("goreLevo") || pozicija.equals("goreDesno")) {
				
				//da li je presao preko svog ili preko protivnika ali daleko
				for (int i = s1X - 1; i >= s2X + 1; i--) {
					if (pozicija.equals("goreLevo"))
						y1--;
					else 
						y1++;
				
					if (i != s2X + 1) {
						if (white) {
							if (state.getStateValue(i, y1) == 2 || state.getStateValue(i, y1) == 5) {
								return "-1";
							}
						}
						else {
							if (state.getStateValue(i, y1) == 1 || state.getStateValue(i, y1) == 4) {
								return "-1";
							}
						}
					}
					if (white) {
						if (state.getStateValue(i, y1) == 1 || state.getStateValue(i, y1) == 4)
								return "-1";
					}
					else {
						if (state.getStateValue(i, y1) == 2 || state.getStateValue(i, y1) == 5)
								return "-1";
					}
			    }
				
				//da li je pojeo
				if (pozicija.equals("goreLevo")) {    //goreLevo
					if (white) {
						if(state.getStateValue(s2X + 1, s2Y + 1) == 2 || state.getStateValue(s2X + 1, s2Y + 1) == 5)
							return (s2X+1)+","+(s2Y+1);
					}
					else {
						if(state.getStateValue(s2X + 1, s2Y + 1) == 1 || state.getStateValue(s2X + 1, s2Y + 1) == 4)
							return (s2X+1)+","+(s2Y+1);
					}
				}
				else {                                //goreDesno
					if (white) {
						if(state.getStateValue(s2X + 1, s2Y - 1) == 2 || state.getStateValue(s2X + 1, s2Y - 1) == 5)
							return (s2X+1)+","+(s2Y-1);
					}
					else { 
						if(state.getStateValue(s2X + 1, s2Y - 1) == 1 || state.getStateValue(s2X + 1, s2Y - 1) == 4)
							return (s2X+1)+","+(s2Y-1);
					}
				}
				
			}
			else {
				
				//da li je presao preko svog ili preko protivnika ali daleko
				for (int i = s1X + 1; i <= s2X - 1; i++) {
					if (pozicija.equals("doleLevo"))
						y1--;
					else 
						y1++;
						
					//ako je presao preko protivnika ali daleko
					if (i != s2X - 1) {
						if (white) {
							if (state.getStateValue(i, y1) == 2 || state.getStateValue(i, y1) == 5) {
								return "-1";
							}
						}
						else {
							if (state.getStateValue(i, y1) == 1 || state.getStateValue(i, y1) == 4) {
								return "-1";
							}
						}
					}
					if (white) {
						if (state.getStateValue(i, y1) == 1 || state.getStateValue(i, y1) == 4)
							return "-1";
					}
					else {
						if (state.getStateValue(i, y1) == 2 || state.getStateValue(i, y1) == 5)
							return "-1";
					}
				}
				
				//da li je pojeo
				if (pozicija.equals("doleLevo")) { //doleLevo
					if (white) {
						if(state.getStateValue(s2X - 1, s2Y + 1) == 2 || state.getStateValue(s2X - 1, s2Y + 1) == 5)
							return (s2X-1)+","+(s2Y+1);
					}
					else {
						if(state.getStateValue(s2X - 1, s2Y + 1) == 1 || state.getStateValue(s2X - 1, s2Y + 1) == 4)
							return (s2X-1)+","+(s2Y+1);
					}
				}
				else {							   //doleDesno
					if (white) {
						if(state.getStateValue(s2X - 1, s2Y - 1) == 2 || state.getStateValue(s2X - 1, s2Y - 1) == 5)
							return (s2X-1)+","+(s2Y-1); 
					}
					else {
						if(state.getStateValue(s2X - 1, s2Y - 1) == 1 || state.getStateValue(s2X - 1, s2Y - 1) == 4)
							return (s2X-1)+","+(s2Y-1); 
					}
				}
				
			}
			return "0"; //nikog nije pojeo i potez je validan
		}
		
}

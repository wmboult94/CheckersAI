/*
Need overall playing environment class
this contains the board, the menu, the pieces, the game setup, evaluating move validity, updating gui

Need player class, this implements move ability, generate valid successors, obtain player state
subclass of player: AI. this implements the adversarial search
subclass of player: Human. this implements move help
do i need a checker class
*/

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.Desktop;
import java.net.URI;

public class CheckersServer extends JFrame {

	JPanel pane, panel1, panel2, panel3;
	JLabel label1;
	JButton btnNewGame, btnExit, btnRules, btnQuitGame;
	JToggleButton btnHelp;
	Board boardPanel;
	JComboBox diffOptions;
	Game game;
	private final static int BOARDSIZE = 512;
	private final static int WINDOWHEIGHT = (int) (BOARDSIZE/0.8);

	public CheckersServer()
	{

		game = new Game( BOARDSIZE / 8 );
		homeGUI();
		// if game.started() {
		//
		// }
		// Game game = new Game();

	}

// TODO: work out how to set up a layout with left side for board, right side for controls
	private void homeGUI()
	{
		//Create and set up the window.
		// JFrame frame = new JFrame( "CheckersServer" );
		setTitle( "Checkers GUI" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setLayout(new BorderLayout());

		pane = new JPanel();
		// pane.removeAll();
		// GridLayout layout = new GridLayout(1, 2);
		// setLayout(layout);

		// TODO: Use GridBagLayout to get 2 sections
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		panel1 = new JPanel();
		// panel1.setLayout(new BorderLayout());
		panel1.setBackground( Color.YELLOW );
		// panel1.setSize( 800, 800 );
		label1 = new JLabel( "Checkers AI" );
		// label1.setBackground(Color.CYAN );
		// label1.setSize( 800, 800 );
		panel1.add(label1);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		pane.add(panel1, c);

		panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		boardPanel = new Board(game);
		boardPanel.setPreferredSize(new Dimension(BOARDSIZE, BOARDSIZE));
		// panel2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		// panel2.setBackground(Color.CYAN );

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		// c.weighty = 0.8;
		pane.add(boardPanel, c);
		// pane.add(panel2, c);
		// pane.add(panel2, c);

		panel3 = new JPanel();
		panel3.removeAll();
		panel3.setBackground( Color.BLUE );
		// panel2.setSize( 800, 200 );

		btnNewGame = new JButton( "New Game" );
		btnNewGame.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String[] options = { "Human", "AI" };
				int n = JOptionPane.showOptionDialog(null,
															"Who gets first move?",
															"Choose Player 1",
															JOptionPane.YES_NO_OPTION,
															JOptionPane.INFORMATION_MESSAGE,
															null,
															options,
															options[0]);
				String player1 = n == 0 ? "Human" : "AI";
				System.out.println( "- Starting new game" );
				game.setPlayer( player1 );
				startGame();
			}

		});
		btnExit = new JButton( "Exit" );
		btnExit.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println( "- Exiting game" );
				setVisible( false );
				dispose();
				System.exit(0);
			}

		});
		btnRules = new JButton( "Rules" );
		btnRules.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{

				if (Desktop.isDesktopSupported())
				{

					try
					{
						Desktop.getDesktop().browse( new URI("http://www.indepthinfo.com/checkers/play.shtml" ));
					}
					catch ( Exception err )
					{
						System.err.println("Error getting rules: " + err.getMessage());
					}

				}

			}

		});

		String[] diffStrings = { "Difficulty (Default is easy):", "Easy", "Intermediate", "Hard" };
		diffOptions = new JComboBox( diffStrings );
		diffOptions.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged( ItemEvent e )
			{

				if ( e.getStateChange() == ItemEvent.SELECTED )
				{

					String item = (String) e.getItem();
					// System.out.println(item);
					game.setDifficulty( item );

				}

			}

		});

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = c.weighty = 1.0;
		panel3.add( btnNewGame, c );
		c.gridx = 1;
		c.gridy = 0;
		// c.insets = new Insets(50, 50, 50, 50);
		panel3.add( diffOptions, c );
		c.gridx = 2;
		// c.insets = new Insets(50, 50, 50, 50);
		// c.gridwidth = 1;
		// c.weighty = 1.0;
		panel3.add( btnExit, c );
		c.gridx = 3;
		c.gridy = 0;
		panel3.add( btnRules, c );
		c.gridx = 0;
		c.gridy = 2;
		// c.weighty = 0.1;
		c.insets = new Insets(0, 0, 0, 0);
		pane.add( panel3, c );
		pane.revalidate();
		pane.repaint();

		// pane.add(button, c);
		add(pane);
		// c.add(panel2);

		setResizable(false);
		pack();
		setSize( BOARDSIZE, WINDOWHEIGHT );
		setLocationRelativeTo( null );
		setVisible(true);
		validate();

		// setVisible(true);

	}

	private void startGame()
	{

		game.startGame();

		panel3.remove( btnNewGame );
		panel3.remove( diffOptions );
		panel3.remove( btnExit );

		btnQuitGame = new JButton( "Quit Game" );
		btnQuitGame.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				int n = JOptionPane.showConfirmDialog(null,
																		"Are you sure you want to quit?",
																		"Quitting Game",
																		JOptionPane.YES_NO_OPTION);
				if ( n == 0 )
				{

					game.endGame();
					panel3.removeAll();
					homeGUI();
					// panel3.updateUI();
					revalidate();
					repaint();

				}


			}

		});

		btnHelp = new JToggleButton("Help");

		btnHelp.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{

				JToggleButton tglBtn = (JToggleButton) e.getSource();

				if ( tglBtn.isSelected() )
					game.setHelp(true);
				else
					game.setHelp(false);

			}

		});
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = c.weighty = 1.0;
		panel3.add(btnQuitGame, c);
		c.gridx = 1;
		c.gridy = 0;
		panel3.add(btnHelp, c);
		c.gridx = 2;
		c.gridy = 0;
		panel3.add(btnRules, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(panel3, c);

		repaint();
		revalidate();

	}

	private void updateGUI()
	{
		// TODO: add implementation to update gui after ai/human move, might just be
		// a simple case of repainting with updated game state, multiple repaints for multicapture
	}

	private void winnerPopup()
	{
		String messageString;

		if ( game.humanHasWon() )
			messageString = "Well done, you won!!";
		else
			messageString = "Unlucky, AI won";

		JOptionPane.showMessageDialog(null,
									messageString,
									"Game Won",
									JOptionPane.PLAIN_MESSAGE);


	}

	public static void main( String[] args )
	{

	//Schedule a job for the event-dispatching thread:
	//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{

				CheckersServer gui = new CheckersServer();

				while ( game.inPlay() )
				{

					if ( game.isWon() )
					{

						winnerPopup();
						game.endGame();
						homeGUI();
						break;

					}



				}
				// gui.setVisible( true );

			}

		});

	}
}

class Board extends JComponent {
	private static int rows = 8;
	private static int columns = 8;
	private static Color green = new Color( 72, 112, 20 );
	private static Color cream = new Color( 242, 239, 208 );
	// BoardLayout board_layout;
	private Game gameState;
	private Point oldCenter;
	private Point oldPos;
	private boolean dragFlag = false;
	private Checker checkerAtPos, checkerAtNewPos;

	Board( Game game )
	{
		gameState = game;
		// createBoard();
		// Dimension dimPrefSize = new Dimension(160, 160);
		/*
		newly opened: draw empty board, buttons saying new game, quit, difficulty, rules
		new game: draw initial board, quit, help, rules
			initialise checker positions, type
			if help, highlight squares that can be moved to when a piece is clicked
		making moves: drag a piece to the new position; find which square that falls in;
		check if valid move, by creating a new Move based on proposed move, then comparing with
		list of valid successors for that position, redraw with invalid popup explain;
			exit invalid popup, redraw without popup;
		check if jump,
			then if multijump, by checking if that proposed Move is start of multijump list in successorlist,
			redraw first move, then all successive;
				update positions, type of piece
			else if single jump, redraw with piece moved to center of new pos, jumped piece removed;
				update positions, types of piece
			check if game won, if so redraw with popup;
				on exit popup, redraw home screen
		else if move, redraw with moved piece in center of new square
		switch players

		*/
		addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed( MouseEvent e )
			{
				// if (gameState.inPlay()) {

				// // Obtain mouse coordinates at time of press.
				//
				int x = e.getX();
				int y = e.getY();
				Point boardPos = convertToBoardPosition(x,y);
				checkerAtPos = gameState.checkers[boardPos.x][boardPos.y];

				if ( checkerAtPos != null )
				{

					if ( gameState.getPlayerColour() == checkerAtPos.getColour() )
					{

						System.out.println("Clicked on your piece");
						oldCenter = checkerAtPos.getCenter();
						oldPos = checkerAtPos.getPos();
						dragFlag = true;

					}

				}
				// System.out.println( "X coord: " + x + "; Y coord: " + y );
				// // int posx = x / width;
				// // TODO: find out what checker is being pressed, if playing player's, set checker center as mouse pos, repaint

			}

			@Override
			public void mouseReleased( MouseEvent e )
			{
				if ( dragFlag )
					dragFlag = false;
				else
					return;

				// TODO: when mouse is released, build proposed move, if valid, repaint snapped to
				// If invalid,
				int x = e.getX();
				int y = e.getY();
				Point boardPos = convertToBoardPosition(x,y);
				checkerAtNewPos = gameState.checkers[boardPos.x][boardPos.y];
				if ( checkerAtNewPos == null )
				{

					System.out.println("Found a piece in selected square!");
					checkerAtPos.setBoardPos( boardPos.x, boardPos.y );
					gameState.checkers[boardPos.x][boardPos.y] = checkerAtPos;
					gameState.checkers[oldPos.x][oldPos.y] = null;

				}
				else
					checkerAtPos.setBoardPos( oldPos.x, oldPos.y );

				repaint();

			}

			@Override
			public void mouseClicked( MouseEvent e )
			{
				if (gameState.inPlay())
				{

					// TODO: if mouse is clicked over a valid square (playing player's team,
					// move possible from that square), if help is on, highlight movable squares
					// Obtain mouse coordinates at time of press.

					int x = e.getX();
					int y = e.getY();
					System.out.println( "X coord: " + x + "; Y coord: " + y );
					Point boardPos = convertToBoardPosition(x,y);
					checkerAtPos = gameState.checkers[boardPos.x][boardPos.y];
					if ( checkerAtPos != null )
					{

						if ( gameState.getPlayerColour() == checkerAtPos.getColour() )
							System.out.println("Clicked on your piece");
							// TODO: highlight squares based on valid moves

					}
					// int posx = x / width;
					// TODO: find out what checker is being pressed, if playing player's, set checker center as mouse pos, repaint

					repaint();
				}

			}

		});

		addMouseMotionListener(new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged( MouseEvent e )
			{
				if ( dragFlag )
				{

					checkerAtPos.setPos( e.getX(), e.getY() );
					repaint();

				}

			}

		});
		repaint();
		revalidate();

	}


	public Point convertToBoardPosition( int xcoord, int ycoord )
	{
		int height = this.getSize().height/8;
		int width = this.getSize().width/8;

		int xboard = xcoord / width;
		int yboard = ycoord / width;

		return new Point( xboard, yboard );
	}

	@Override
	protected void paintComponent( Graphics g )
	{
		// System.out.println("Size of board panel: " + this.getSize().height);
		// this.setLayout(new GridLayout(8,8));
		// this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		// this.setVisible(true);
		// // Color color = null;
		// JPanel square = new JPanel();
		// // square.setSize(50,50);
		// square.setPreferredSize(new Dimension(50, 50));


		((Graphics2D) g).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		int height = this.getSize().height/8;
		int width = this.getSize().width/8;

		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < columns; j++)
			{
				if (j % 2 == 0)
				{

					if (i % 2 == 0)
						g.setColor( cream );
					else
						g.setColor( green );

				}
				else {

					if (i % 2 == 0)
						g.setColor( green );
					else
						g.setColor( cream );

				}
				g.fillRect( j * height, i * height, height, height );
				// if game.inPlay() {
				// 	if game.board[i][j]
				// 	drawChecker
				// }
			}
		}

		if (gameState.inPlay())
		{

			for ( int i=0; i<gameState.checkers.length; i++ )
			{
				for ( int j=0; j<gameState.checkers[i].length; j++ )
				{

					Checker checker = gameState.checkers[i][j];
					if (checker != null)
						checker.paint( g );

				}
			}
		}
		// this.repaint();
	}

}

class Game {

	private boolean inPlayFlag = false;
	private String activePlayer = "Human";
	private int difficulty = 1;
	private boolean help = false;
	Checker[][] checkers;
	private static int SQUARESIZE;
	private String playerColour = "Black";
	private String aiColour = "Red";
	// private String playerTurn;

	public Game(int squareSize)
	{

		SQUARESIZE = squareSize;
		playerColour = activePlayer == "Human" ? "Black" : "Red";
		aiColour = playerColour == "Black" ? "Red" : "Black";
		// playerTurn = player;

	}

	public void endGame()
	{

		checkers = null;
		inPlayFlag = false;
		activePlayer ="Human";
		help = false;
		difficulty = 1;

	}

	public String getPlayerColour()
	{

		return playerColour;

	}

	public String getAIColour()
	{

		return aiColour;

	}

	public ArrayList<Move> generateValidSuccessors()
	{
		String colour;
		ArrayList<Move> validMoves = new ArrayList<Move>();
		if ( this.whoseTurn() == "Human" )
		{
			colour = this.getPlayerColour();

			for ( int x = 0; x < 8; x++ )
			{
				for ( int y = 0; y < 8; y++ )
				{
					if ( checkers[x][y].getColour() == colour )
					{

						addValidCaptureMoves( x, y, colour, validMoves );
						if ( validMoves.size() == 0 ) // only add regular moves if there are no captures available
							addValidRegMoves( x, y, colour, validMoves );

					}

				}
			}
		}
		else
		{
			colour = this.getAIColour();
		}

	}

	private void addValidRegMoves( int x, int y, String colour, ArrayList<Move> validMoves )
	{
		if ( x-1 >= 0 && y-1 >= 0 && checkers[x-1][y-1] == null ) // regular left move allowed
		{

			Move move = new Move( new Point(x, y), new Point(x-1, y-1) );
			validMoves.add(move);

		}
		else if ( x+1 <= 7 && y-1 >= 0 && checkers[x+1][y-1] == null ) // regular right move allowed
		{

			Move move = new Move( new Point(x, y), new Point(x+1, y+1) );
			validMoves.add(move);

		}
		else if ( checkers[x][y].getType() == PieceType.BLACK_KING || checkers[x][y].getType() == PieceType.RED_KING ) // If king piece, also check downward moves
		{

			if ( x-1 >= 0 && y+1 <= 7 && checkers[x-1][y+1] == null ) // regular left move allowed
			{

				Move move = new Move( new Point(x, y), new Point(x-1, y+1) );
				validMoves.add(move);

			}
			else if ( x+1 <= 7 && y+1 <= 7 && checkers[x+1][y+1] == null ) // regular right move allowed
			{

				Move move = new Move( new Point(x, y), new Point(x+1, y+1) );
				validMoves.add(move);

			}

	}

	private void addValidCaptureMoves( int x, int y, String colour, ArrayList<Move> validMoves )
	{

		// TODO: do conditions for capture move, recurse to check for multijump, skip searching for regular moves if captures found
		if ( checkers[x-1][y-1].getColour() != colour && x-2 >= 0 && y-2 >= 0 && checkers[x-2][y-2] == null ) // left capture move allowed
		{

			Move move = new Move( new Point(x, y), new Point(x-2, y-2) );
			validMoves.add(move);

		}
		else if ( checkers[x+1][y-1].getColour() != colour && x+2 <= 7 && y-2 >= 0 && checkers[x+2][y-2] == null ) // right capture move allowed
		{

			Move move = new Move( new Point(x, y), new Point(x+2, y-2) );
			validMoves.add(move);

		}
		else if ( checkers[x][y].getType() == PieceType.BLACK_KING || checkers[x][y].getType() == PieceType.RED_KING ) // If king piece, also check downward moves
		{

			// TODO: do conditions for capture move, recurse to check for multijump, skip searching for regular moves if captures found
			if ( checkers[x-1][y+1].getColour() != colour && x-2 >= 0 && y+2 <= 7 && checkers[x-2][y+2] == null ) // left capture move allowed
			{

				Move move = new Move( new Point(x, y), new Point(x-2, y+2) );
				validMoves.add(move);

			}
			else if ( checkers[x+1][y+1].getColour() != colour && x+2 <= 7 && y+2 <= 7 && checkers[x+2][y+2] == null ) // right capture move allowed
			{

				Move move = new Move( new Point(x, y), new Point(x+2, y+2) );
				validMoves.add(move);

			}

		}

	}

	public boolean inPlay()
	{

		return inPlayFlag;

	}

	public void setDifficulty( String diffString )
	{

		switch ( diffString )
		{
			case "Easy":
				difficulty = 1;
			case "Intermediate":
				difficulty = 2;
			case "Hard":
				difficulty = 3;
			default:
				difficulty = 1;
		}

	}

	public void setPlayer( String playerString )
	{

		activePlayer = playerString;

	}

	public void setHelp( boolean helpFlag )
	{

		help = helpFlag;

	}

	public void startGame()
	{

		System.out.println( "Starting game" );
		inPlayFlag = true;
		checkers = new Checker[8][8];

		// Top half of board
		for ( int y = 0; y < 3; y++)
		{
			for ( int x = 1; x < 8 ; x+=2 )
			{

				int tempX = x;
				if ( (y % 2) != 0 )
					tempX = x - 1;

				checkers[tempX][y] = new Checker( SQUARESIZE, tempX, y );
				if ( activePlayer == "Human" )
					checkers[tempX][y].setType( PieceType.RED );
				else
					checkers[tempX][y].setType( PieceType.BLACK );

			}
		}

		// Bottom half of board
		for ( int y = 7; y > 4; y--)
		{
			for ( int x = 0; x < 8 ; x+=2 )
			{
				int tempX = x;

				if ( (y % 2) == 0 )
					tempX = x + 1;

				checkers[tempX][y] = new Checker( SQUARESIZE, tempX, y );
				System.out.println("cx: " + checkers[tempX][y].centerX + ", cy: " + checkers[tempX][y].centerY);

				if (activePlayer == "Human")
					checkers[tempX][y].setType( PieceType.BLACK );
				else
					checkers[tempX][y].setType( PieceType.RED );

			}
		}

	}

	public String whoseTurn()
	{

		return activePlayer;

	}
}

class Checker {

	private int boardPosX = 0;
	private int boardPosY = 0;
	public int centerX = 0;
	public int centerY = 0;
	private int coordX = 0;
	private int coordY = 0;
	private PieceType type;
	private String pieceColour;
	private static int SQUARESIZE;

	public Checker( int squareSize, int x, int y )
	{

		SQUARESIZE = squareSize;
		setBoardPos( x, y );
		// System.out.println("Sq size: " + SQUARESIZE);
		// System.out.println(centerX);

	}

	public void paint( Graphics g )
	{

		int xCoord = centerX - ( SQUARESIZE / 2 ) + 5;
		int yCoord = centerY - ( SQUARESIZE / 2 ) + 5;
		// int xCoord = coordX + 5;
		// int yCoord = coordY + 5;
		int pieceSize = (int) ( 0.8 * SQUARESIZE );

		if ( type == PieceType.BLACK || type == PieceType.BLACK_KING )
			g.setColor( Color.black );
		else
			g.setColor( Color.red );

		g.fillOval( xCoord, yCoord, pieceSize, pieceSize );

	}

	public String getColour()
	{

		return pieceColour;

	}

	public PieceType getType()
	{

		return type;

	}

	public Point getCenter()
	{

		return new Point(centerX, centerY);

	}

	public Point getPos()
	{

		return new Point(boardPosX, boardPosY);

	}

	public void setBoardPos( int x, int y )
	{

		if ( x >= 8 || x < 0 || y >= 8 || y < 0 )
			throw new IllegalArgumentException( "Piece's X or Y board position is invalid" );

		boardPosX = x;
		boardPosY = y;
		coordX = boardPosX * SQUARESIZE;
		coordY = boardPosY * SQUARESIZE;
		centerX = (int) ( SQUARESIZE * boardPosX + SQUARESIZE / 2 );
		centerY = (int) ( SQUARESIZE * boardPosY + SQUARESIZE / 2 );
		System.out.println(centerY);

	}

	public void setPos( int x, int y )
	{

		centerX = x;
		centerY = y;

	}

	public void setType( PieceType pieceType )
	{

		type = pieceType;
		if (type == PieceType.BLACK || type == pieceType.BLACK_KING)
			pieceColour = "Black";
		else
			pieceColour = "Red";

	}
}

class Move
{
	private ArrayList<BoardMove> moveList = new ArrayList<BoardMove>();
	private String moveType;

	public Move( Point pos1, Point pos1 )
	{

		moveList.add( new BoardMove( pos1, pos2 ) );
		if ( abs(pos2.x - pos1.x) == 1 && abs(pos2.y - pos1.y) == 1 )
			moveType = "Move";
		else
			moveType = "Capture";

	}

	// public void addMove( Point newMove )
	// {
	//
	// 	moveList.
	//
	// }
	//
}

class BoardMove
{

	private Point oldPos;
	private Point newPos;

	public BoardMove( Point pos1, Point pos2 )
	{

		oldPos = pos1;
		newPos = pos2;

	}

}

enum PieceType
{

	BLACK,
	BLACK_KING,
	RED,
	RED_KING

}

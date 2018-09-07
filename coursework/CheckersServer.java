/*******************************************************************
* AI Checkers. Play against an AI opponent of varying difficulty   *
* Warren Boult, University of Sussex, 2018.                        *
********************************************************************/

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import java.awt.Desktop;
import java.net.URI;

// Main class to build the gui, and handle the playing of the game.
public class CheckersServer extends JFrame {

	JPanel pane, panel1, panel2, panel3, scorePanel;
	JLabel label1, scoreLabel;
	JButton btnNewGame, btnExit, btnRules, btnQuitGame;
	JToggleButton btnHelp;
	Board boardPanel;
	JComboBox diffOptions;
	static Game game;
	private static int BOARDSIZE = 512;
	private static int WINDOWHEIGHT = (int) ( BOARDSIZE / 0.8 );
	private Point oldCenter;
	private Point oldPos;
	private boolean dragFlag = false;
	private Checker checkerAtPos, checkerAtNewPos;
	private ArrayList<Move> validMoves = new ArrayList<Move>();

	public CheckersServer()
	{

		System.out.println("Welcome to Checkers AI");
		game = new Game( BOARDSIZE / 8 );
		homeGUI( game );
		// game = gameState;

	}

	// Display the initial gui, for not started game.
	// Board not yet populated, and cannot be interacted with
	public void homeGUI( Game game )
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

		// Using gridbag layout for menu items, so GridBagConstraints must be set
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
				System.out.println( "- First player is " + game.whoseTurn() );
				System.out.println( "- Difficulty rating: " + game.getDifficulty() );
				// setVisible(false);
				newGame( game );

				// javax.swing.SwingUtilities.invokeLater(new Runnable() {
				// 	public void run() {

				revalidate();
				repaint();
				// setVisible(true);

				// 	}
				// });

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

		String[] diffStrings = { "Difficulty (Default is easy):", "Easy", "Intermediate", "Hard", "Extreme" };
		diffOptions = new JComboBox( diffStrings );
		diffOptions.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged( ItemEvent e )
			{

				if ( e.getStateChange() == ItemEvent.SELECTED )
				{

					String item = (String) e.getItem();
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
		repaint();
		validate();

		// setVisible(true);

	}

	// Display the in-game gui,
	// add event listeners to the board to drive event driven gameplay
	public void newGame( Game game )
	{

		game.startGame();
		// setVisible(false);
		// revalidate();
		// repaint();


		// Remove home screen buttons
		panel3.remove( btnNewGame );
		panel3.remove( diffOptions );
		panel3.remove( btnExit );
		panel3.remove( btnRules );

		// boardPanel.repaint();
		// boardPanel.revalidate();

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
					// panel3.removeAll();
					// boardPanel.clearHighlightSquares();
					// dispose();
					// setVisible(false);
					// // panel3.updateUI();
					// panel3.revalidate();
					// panel3.repaint();
					// javax.swing.SwingUtilities.invokeLater(new Runnable() {
					// 	public void run() {
					//
					// 		homeGUI( game );
					// 		revalidate();
					// 		repaint();
					// 		setVisible(true);
					//
					// 	}
					// });
					setVisible( false );
					dispose();
					System.exit(0);


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
					boardPanel.clearHighlightSquares();
					revalidate();
					repaint();

			}

		});

		String labelString = "Player Pieces: " + (12 - game.aiScore()) + " | AI Pieces: " + (12 - game.playerScore());
		scoreLabel = new JLabel(labelString);
		scoreLabel.setOpaque(true);
		scorePanel = new JPanel();
		scoreLabel.setBackground( new Color( 232, 189, 71 ) );
		// scorePanel.add(scoreLabel);
		// scorePanel.revalidate();
		// TODO: Add a score panel, maybe other info
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
		c.gridx = 3;
		// c.gridwidth = 3;
		panel3.add(scoreLabel, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		pane.add(panel3, c);
		panel3.revalidate();
		boardPanel.revalidate();
		pane.revalidate();
		pane.repaint();

		// Add listener functionality to the board
		boardPanel.addMouseListener(new MouseAdapter()
		{

			public void mouseEntered( MouseEvent e ) {}
			public void mouseExited( MouseEvent e ) {}
			public void mouseMoved( MouseEvent e ) {}

			// When mouse is pressed, find the board position pressed on,
			// and generate valid moves.
			// Store the board position to repaint checker there if proposed move is invalid
			@Override
			public void mousePressed( MouseEvent e )
			{

				if ( !game.inPlay() )
					return;

				if ( game.whoseTurn() == "Human" )
				{

					game.clearSuccessors( validMoves );

					// // Obtain mouse coordinates at time of press.
					//
					int x = e.getX();
					int y = e.getY();
					Point boardPos = boardPanel.convertToBoardPosition(x,y);
					checkerAtPos = game.checkers[boardPos.x][boardPos.y];

					if ( checkerAtPos != null && game.getPlayerColour() == checkerAtPos.getColour() )
					{

						// System.out.println("Clicked on your piece");
						oldCenter = checkerAtPos.getCenter();
						oldPos = checkerAtPos.getPos();
						game.clearSuccessors( validMoves );
						validMoves = game.generateAllValidSuccessors();
						dragFlag = true;

					}

				}

			}

			// When dragging of piece is finished, we check to see if this proposed move is valid
			// If invalid, redraw checker at original pos, display reason why invalid.
			// If move is valid, update state and gui to reflect move
			@Override
			public void mouseReleased( MouseEvent e )
			{

				if ( !game.inPlay() )
					return;

				if ( game.whoseTurn() == "Human" )
				{
					if ( dragFlag )
						dragFlag = false;
					else
						return;

					int x = e.getX();
					int y = e.getY();

					if ( x > 512 || y > 512 )
					{

						String messageString = "Attempting to move outside of board, try again";
						errorPopup( messageString );
						checkerAtPos.setBoardPos( oldPos.x, oldPos.y );
						return;

					}

					// TODO: add popup explaining why a move is invalid
					Point boardPos = boardPanel.convertToBoardPosition(x,y);
					checkerAtNewPos = game.checkers[boardPos.x][boardPos.y];

					if ( checkerAtNewPos == null && (boardPos.x + boardPos.y) % 2 != 0 ) // for white squares x + y is even
					{

						if ( validMoves.isEmpty() )
						{

							String messageString = "No valid moves available for that piece, try again";
							errorPopup( messageString );
							checkerAtPos.setBoardPos( oldPos.x, oldPos.y );

						}

						BoardMove newMove = new BoardMove( oldPos, boardPos );

						for ( Move move : validMoves )
						{

							if ( move.getMovePos().equals( newMove.newPos ) && move.getStartPos().equals( newMove.oldPos ) ) // If proposed end position matches one of the valid moves, use that valid move
							{

								// Do human move, then switch play to do AI move
								updateGUI( game, move );


								// Creating a Runnable instance here:
								javax.swing.SwingUtilities.invokeLater(new Runnable() {
									public void run() {

										game.setPlayer( "AI" );
										Move bestMove = game.makeAIMove();

										updateGUI( game, bestMove );
										game.setPlayer( "Human" );

									}
								});

								return;
							}

						}

						String messageString = "Proposed move not valid, try again";
						errorPopup( messageString );
						checkerAtPos.setBoardPos( oldPos.x, oldPos.y );
						// break;

					}
					else if ( boardPos.x == oldPos.x && boardPos.y == oldPos.y )
					{}
					else
					{

						String messageString = "Attempting to move to white or non-empty square, try again";
						errorPopup( messageString );
						checkerAtPos.setBoardPos( oldPos.x, oldPos.y );

					}

					revalidate();
					repaint();

				}

			}

			// Upon clicking a piece, if help is enabled squares that can be moved to
			// should get highlighted
			@Override
			public void mouseClicked( MouseEvent e )
			{

				if ( !game.inPlay() )
					return;

				if ( game.whoseTurn() == "Human" )
				{

					boardPanel.clearHighlightSquares();
					game.clearSuccessors( validMoves );

					if ( game.getHelp() )
					{

						int x = e.getX();
						int y = e.getY();
						// System.out.println( "X coord: " + x + "; Y coord: " + y );
						Point boardPos = boardPanel.convertToBoardPosition(x,y);

						checkerAtPos = game.checkers[boardPos.x][boardPos.y];
						if ( checkerAtPos != null )
						{

							if ( game.getPlayerColour() == checkerAtPos.getColour() )
							{

								// System.out.println("Clicked on your piece");
								validMoves = game.generateAllValidSuccessors();
								ArrayList<Point> highlightSquares = new ArrayList<Point>();
								for ( Move move: validMoves )
								{
									if ( move.getStartPos().equals(boardPos) )
										highlightSquares.add( move.getMovePos() );
								}

								boardPanel.setHighlightSquares( highlightSquares );

							}

						}

					}

					revalidate();
					repaint();
				}

			}
		});

		boardPanel.addMouseMotionListener(new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged( MouseEvent e )
			{

				if ( !game.inPlay() )
					return;

				if ( game.whoseTurn() == "Human" && dragFlag )
				{

					checkerAtPos.setPos( e.getX(), e.getY() );
					repaint();

				}

			}

		});

		revalidate();
		repaint();
		// If first player is AI, start by letting the AI move
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				if ( game.whoseTurn() == "AI" )
				{


					Move bestMove = game.makeAIMove();
					updateGUI( game, bestMove );
					game.setPlayer( "Human" );

				}

			}
		});

		// }


		// panel3.repaint();
		// panel3.revalidate();
		revalidate();
		repaint();
		// setVisible(true);

	}

	// Update the game state, and reflect the changes by repainting the gui.
	// Upon each update, if game is won, then display the winner popup and end game
	public void updateGUI( Game game, Move move )
	{

		// iterate through all board positions, 1 for a single capture, multiple for mutlicap
		for ( BoardMove boardMove : move.moveList )
		{

			System.out.println("- Making move " + boardMove.getMove());
			game.updateState( move.getMoveType(), boardMove );
			boardPanel.clearHighlightSquares();
			String labelString = "Player Pieces: " + (12 - game.aiScore()) + " | AI Pieces: " + (12 - game.playerScore());
			scoreLabel.setText(labelString);

			revalidate();
			repaint();
			// revalidate();
			// repaint();

			if ( game.isWon() )
			{

				winnerPopup();
				game.endGame();
				panel3.removeAll();
				boardPanel.clearHighlightSquares();
				dispose();
				setVisible( false );
				homeGUI( game );
				try
				{
					TimeUnit.SECONDS.sleep(1);
				}
				catch ( InterruptedException err )
				{
					System.err.println("Thread interrupted");
				}
				revalidate();
				repaint();
				setVisible( true );
				break;

			}

			try
			{
				TimeUnit.SECONDS.sleep(2);
			}
			catch ( InterruptedException err )
			{
				System.err.println("Thread interrupted");
			}

		}

	}

	// Display a popup if the game is won
	public void winnerPopup()
	{

		String messageString = game.humanHasWon() ? "Well done, you won!" : "Bad luck, you lost";

		// if ( game.humanHasWon() )
		// 	messageString = "Well done, you won!!";
		// else
		// 	messageString = "Unlucky, AI won";

		JOptionPane.showMessageDialog(null,
									messageString,
									"Game Won",
									JOptionPane.PLAIN_MESSAGE);

	}

	// Display a popup if move is invalid
	public void errorPopup( String messageString )
	{

		JOptionPane.showMessageDialog(null,
									messageString,
									"Invalid Move",
									JOptionPane.PLAIN_MESSAGE);

	}

	public static void main( String[] args )
	{

		new CheckersServer();

	}
}

// Board class is used to draw the board, and the checkers on the board.
// Purely a gui-related aspect, does not control any game functionality
class Board extends JComponent {
	private static int rows = 8;
	private static int columns = 8;
	private static Color green = new Color( 72, 112, 20 );
	private static Color blue = new Color( 112, 157, 229 );
	private static Color cream = new Color( 242, 239, 208 );
	// BoardLayout board_layout;
	private Game gameState;
	private Point oldCenter;
	private Point oldPos;
	private boolean dragFlag = false;
	private Checker checkerAtPos, checkerAtNewPos;
	public ArrayList<Point> highlightSquares = new ArrayList<Point>();

	Board( Game game )
	{

		gameState = game;

	}

	public void setHighlightSquares( ArrayList<Point> squares )
	{

		for ( Point square: squares )
		{

			highlightSquares.add(square);

		}

	}

	public void clearHighlightSquares()
	{

		highlightSquares.clear();

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
					{
						g.setColor( green );
						if ( highlightSquares.contains( new Point( j, i ) ) )
							g.setColor( blue );
					}

				}
				else {

					if (i % 2 == 0)
					{
						g.setColor( green );
						if ( highlightSquares.contains( new Point( j, i ) ) )
							g.setColor( blue );
					}
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

// The Game class holds all the internal game state information,
// includes the functions to build initial state, advance game state,
// generate successors, and check if game is won
class Game {

	private boolean inPlayFlag = false;
	private String activePlayer = "Human";
	private int difficulty = 1;
	private int seLevel = 1;
	private boolean help = false;
	Checker[][] checkers;
	public static int SQUARESIZE;
	private String playerColour = "Black";
	private String aiColour = "Red";
	ArrayList<Point> highlightSquares = new ArrayList<Point>();

	ArrayList<MoveAndScore> aiMoveList = new ArrayList<MoveAndScore>();
	int maxDepth = 1;
	int seCount;
	int deCount;
	int pCount;

	// private String playerTurn;

	public Game( int squareSize )
	{

		SQUARESIZE = squareSize;
		// playerTurn = player;

	}

	// Copy constructor needed to create a temporary game state for AI
	public Game( Game original )
	{

		// Just copy over all fields from original gameState
		this.inPlayFlag = original.inPlay();
		this.activePlayer = original.whoseTurn();
		this.difficulty = original.getDifficulty();
		this.help = original.getHelp();
		this.checkers = new Checker[original.checkers[0].length][original.checkers[1].length];
		for ( int x = 0; x < checkers[0].length; x++)
		{
			for ( int y = 0; y < checkers[1].length; y++)
			{
				if ( original.checkers[x][y] != null )
					checkers[x][y] = new Checker( original.checkers[x][y] );
			}
		}
		this.SQUARESIZE = original.SQUARESIZE;
		this.playerColour = original.getPlayerColour();
		this.aiColour = original.getAIColour();
		if ( !original.highlightSquares.isEmpty() )
		{
			for ( Point square : original.highlightSquares )
				this.highlightSquares.add(square);
		}
		if ( !original.aiMoveList.isEmpty() )
		{
			for ( MoveAndScore mas : original.aiMoveList )
				this.aiMoveList.add( new MoveAndScore( mas ) );
		}
		this.maxDepth = original.maxDepth;
		this.seCount = original.seCount;
		this.deCount = original.deCount;
		this.pCount = original.pCount;

	}

	// If aiScore is maximal, ie 12, then ai has won the game
	public boolean aiHasWon()
	{
		// System.out.println("AI score: " + this.aiScore() );
		if ( this.aiScore() == 12 )
		{

			inPlayFlag = false;
			return true;

		}
		else
			return false;

	}

	// AI score is max number of pieces minus number of human player's pieces remaining
	// Used to calculate leaf utility and to see if game is won
	public int aiScore()
	{

		int score = 12;
		for ( int x = 0; x < 8; x++ )
		{
			for ( int y = 0; y < 8; y++ )
			{

				if ( checkers[x][y] != null && checkers[x][y].getColour() == this.getPlayerColour() )
					score--;

			}
		}

		return score;

	}

	// Returns the valid capture moves for a given position
	// if conditions check to see if the proposed move end point is within the board bounds,
	// if the space is empty, and if the piece to be captured is of the opposite colour
	// Recurse for multiCaptures,
	// make sure we're not trying to do a multicapture forwards and backwards
	// in the same spot with (newX != oldX || newY != oldY) in conditional
	private ArrayList<Move> addValidCaptureMoves( int x, int y, String colour, PieceType type, int oldX, int oldY )
	{

		ArrayList<Move> validMoves = new ArrayList<Move>();

		if ( this.whoseTurn() == "Human" )
		{

			if ( x-2 >= 0 && y-2 >= 0 && (x-2 != oldX || y-2 != oldY) && checkers[x-1][y-1] != null && checkers[x-1][y-1].getColour() != colour && checkers[x-2][y-2] == null ) // left capture move allowed
			{

				// Move move = new Move( new Point(x, y), new Point(x-2, y-2) );
				// validMoves.add(move);
				ArrayList<Move> multiCapMoves = addValidCaptureMoves( x-2, y-2, colour, type, x, y );
				addMultiCapMoves( x, y, x-2, y-2, validMoves, multiCapMoves );

			}
			if ( x+2 <= 7 && y-2 >= 0 && (x+2 != oldX || y-2 != oldY) && checkers[x+1][y-1] != null && checkers[x+1][y-1].getColour() != colour && checkers[x+2][y-2] == null ) // right capture move allowed
			{

				// Move move = new Move( new Point(x, y), new Point(x+2, y-2) );
				// validMoves.add(move);
				ArrayList<Move> multiCapMoves = addValidCaptureMoves( x+2, y-2, colour, type, x, y );
				addMultiCapMoves( x, y, x+2, y-2, validMoves, multiCapMoves );

			}
			if ( type == PieceType.BLACK_KING || type == PieceType.RED_KING ) // If king piece, also check downward moves
			{

				if ( x-2 >= 0 && y+2 <= 7 && (x-2 != oldX || y+2 != oldY) && checkers[x-1][y+1] != null && checkers[x-1][y+1].getColour() != colour && checkers[x-2][y+2] == null ) // left capture move allowed
				{

					// Move move = new Move( new Point(x, y), new Point(x-2, y+2) );
					// validMoves.add(move);
					ArrayList<Move> multiCapMoves = addValidCaptureMoves( x-2, y+2, colour, type, x, y );
					addMultiCapMoves( x, y, x-2, y+2, validMoves, multiCapMoves );

				}
				if ( x+2 <= 7 && y+2 <= 7 && (x+2 != oldX || y+2 != oldY) && checkers[x+1][y+1] != null && checkers[x+1][y+1].getColour() != colour && checkers[x+2][y+2] == null ) // right capture move allowed
				{

					// Move move = new Move( new Point(x, y), new Point(x+2, y+2) );
					// validMoves.add(move);
					ArrayList<Move> multiCapMoves = addValidCaptureMoves( x+2, y+2, colour, type, x, y );
					addMultiCapMoves( x, y, x+2, y+2, validMoves, multiCapMoves );

				}

			}

		}
		else if ( this.whoseTurn() == "AI" )
		{

			if ( x-2 >= 0 && y+2 <= 7 && (x-2 != oldX || y+2 != oldY) && checkers[x-1][y+1] != null && checkers[x-1][y+1].getColour() != colour && checkers[x-2][y+2] == null ) // left capture move allowed
			{

				// Move move = new Move( new Point(x, y), new Point(x-2, y+2) );
				// validMoves.add(move);
				ArrayList<Move> multiCapMoves = addValidCaptureMoves( x-2, y+2, colour, type, x, y );
				addMultiCapMoves( x, y, x-2, y+2, validMoves, multiCapMoves );

			}
			if ( x+2 <= 7 && y+2 <= 7 && (x+2 != oldX || y+2 != oldY) && checkers[x+1][y+1] != null && checkers[x+1][y+1].getColour() != colour && checkers[x+2][y+2] == null ) // right capture move allowed
			{

				// Move move = new Move( new Point(x, y), new Point(x+2, y+2) );
				// validMoves.add(move);
				ArrayList<Move> multiCapMoves = addValidCaptureMoves( x+2, y+2, colour, type, x, y );
				addMultiCapMoves( x, y, x+2, y+2, validMoves, multiCapMoves );

			}
			if ( type == PieceType.BLACK_KING || type == PieceType.RED_KING ) // If king piece, also check upward moves
			{

				if ( x-2 >= 0 && y-2 >= 0 && (x-2 != oldX || y-2 != oldY) && checkers[x-1][y-1] != null && checkers[x-1][y-1].getColour() != colour && checkers[x-2][y-2] == null ) // left capture move allowed
				{

					// Move move = new Move( new Point(x, y), new Point(x-2, y-2) );
					// validMoves.add(move);
					ArrayList<Move> multiCapMoves = addValidCaptureMoves( x-2, y-2, colour, type, x, y );
					addMultiCapMoves( x, y, x-2, y-2, validMoves, multiCapMoves );

				}
				if ( x+2 <= 7 && y-2 >= 0 && (x+2 != oldX || y-2 != oldY) && checkers[x+1][y-1] != null && checkers[x+1][y-1].getColour() != colour && checkers[x+2][y-2] == null ) // right capture move allowed
				{

					// Move move = new Move( new Point(x, y), new Point(x+2, y-2) );
					// validMoves.add(move);
					ArrayList<Move> multiCapMoves = addValidCaptureMoves( x+2, y-2, colour, type, x, y );
					addMultiCapMoves( x, y, x+2, y-2, validMoves, multiCapMoves );

				}

			}

		}

		return validMoves;

	}

	public void addMultiCapMoves( int x, int y, int newX, int newY, ArrayList<Move> validMoves, ArrayList<Move> multiCapMoves )
	{


		if ( multiCapMoves.isEmpty() )
		{

			Move move = new Move( new Point(x, y), new Point(newX, newY) );
			validMoves.add(move);

		}
		else
		{

			for ( Move mcMove : multiCapMoves )
			{
				Move move = new Move( new Point(x, y), new Point(newX, newY) );
				for ( BoardMove bMove : mcMove.moveList )
					move.moveList.add( bMove );
				validMoves.add(move);
			}

		}

	}

	// Returns the valid non-capture moves for a given position
	// if conditions check to see if the proposed move end point is within the board bounds,
	// and if the space is empty
	private ArrayList<Move> addValidRegMoves( int x, int y, String colour )
	{

		// System.out.println("Generating regular moves..");
		ArrayList<Move> validMoves = new ArrayList<Move>();

		if ( this.whoseTurn() == "Human" )
		{

			if ( x-1 >= 0 && y-1 >= 0 && checkers[x-1][y-1] == null ) // regular left move allowed
			{

				// System.out.println("Found left move");
				Move move = new Move( new Point(x, y), new Point(x-1, y-1) );
				validMoves.add(move);


			}
			if ( x+1 <= 7 && y-1 >= 0 && checkers[x+1][y-1] == null ) // regular right move allowed
			{

				// System.out.println("Found right move");
				Move move = new Move( new Point(x, y), new Point(x+1, y-1) );
				validMoves.add(move);

			}
			if ( checkers[x][y].getType() == PieceType.BLACK_KING || checkers[x][y].getType() == PieceType.RED_KING ) // If king piece, also check downward moves
			{

				if ( x-1 >= 0 && y+1 <= 7 && checkers[x-1][y+1] == null ) // regular left move allowed
				{

					Move move = new Move( new Point(x, y), new Point(x-1, y+1) );
					validMoves.add(move);

				}
				if ( x+1 <= 7 && y+1 <= 7 && checkers[x+1][y+1] == null ) // regular right move allowed
				{

					Move move = new Move( new Point(x, y), new Point(x+1, y+1) );
					validMoves.add(move);

				}

			}

		}
		else if ( this.whoseTurn() == "AI" )
		{

			if ( x-1 >= 0 && y+1 <= 7 && checkers[x-1][y+1] == null ) // regular left move allowed
			{

				// System.out.println("Found left move");
				Move move = new Move( new Point(x, y), new Point(x-1, y+1) );
				validMoves.add(move);


			}
			if ( x+1 <= 7 && y+1 <= 7 && checkers[x+1][y+1] == null ) // regular right move allowed
			{

				// System.out.println("Found right move");
				Move move = new Move( new Point(x, y), new Point(x+1, y+1) );
				validMoves.add(move);

			}
			if ( checkers[x][y].getType() == PieceType.BLACK_KING || checkers[x][y].getType() == PieceType.RED_KING ) // If king piece, also check downward moves
			{

				if ( x-1 >= 0 && y-1 >= 0 && checkers[x-1][y-1] == null ) // regular left move allowed
				{

					Move move = new Move( new Point(x, y), new Point(x-1, y-1) );
					validMoves.add(move);

				}
				if ( x+1 <= 7 && y-1 >= 0 && checkers[x+1][y-1] == null ) // regular right move allowed
				{

					Move move = new Move( new Point(x, y), new Point(x+1, y-1) );
					validMoves.add(move);

				}

			}

		}

		return validMoves;

	}

	public void clearSuccessors( ArrayList<Move> validMoves )
	{

		validMoves.clear();

	}

	// If game is over, this function is called and resets the state
	public void endGame()
	{

		checkers = null;
		inPlayFlag = false;
		activePlayer ="Human";
		help = false;
		difficulty = 1;

	}

	public String getAIColour()
	{

		return aiColour;

	}

	// Captured position can be found by summing old and new pos and dividing by 2
	public Point getCapturedPos( BoardMove boardMove )
	{

		int oldX = boardMove.getOldPos().x;
		int oldY = boardMove.getOldPos().y;
		int newX = boardMove.getNewPos().x;
		int newY = boardMove.getNewPos().y;

		return new Point( (oldX + newX) / 2, (oldY + newY) / 2 );

	}

	public int getDifficulty()
	{

		return difficulty;

	}

	public String getPlayerColour()
	{

		return playerColour;

	}

	public ArrayList<Move> generateValidSuccessors( Point pos )
	{
		String colour;
		ArrayList<Move> validMoves = new ArrayList<Move>();
		int x = pos.x;
		int y = pos.y;

		if ( this.whoseTurn() == "Human" )
		{

			colour = this.getPlayerColour();

			if ( checkers[x][y] != null && checkers[x][y].getColour() == colour )
			{

				// System.out.println("Getting moves for piece (" + x + ", " + y + ")");
				validMoves.addAll( addValidCaptureMoves( x, y, colour, checkers[x][y].getType(), 100, 100 ) );
				if ( validMoves.isEmpty() ) // only add regular moves if there are no captures available
				{

					// System.out.println("No captures found");
					validMoves.addAll( addValidRegMoves( x, y, colour ) );

				}

			}

		}
		else
		{

			colour = this.getAIColour();
			if ( checkers[x][y] != null && checkers[x][y].getColour() == colour )
			{

				validMoves.addAll( addValidCaptureMoves( x, y, colour, checkers[x][y].getType(), 100, 100 ) );
				if ( validMoves.isEmpty() ) // only add regular moves if there are no captures available
				{

					// System.out.println("No captures found");
					validMoves.addAll( addValidRegMoves( x, y, colour ) );

				}

			}

		}

		return validMoves;

	}

	public ArrayList<Move> generateAllValidSuccessors()
	{

		ArrayList<Move> allValidMoves = new ArrayList<Move>();
		boolean captureFound = false;

		for ( int x = 0; x < 8; x++ )
		{
			for ( int y = 0; y < 8; y++ )
			{

				ArrayList<Move> validMoves = generateValidSuccessors( new Point( x, y ) );
				for ( Move move : validMoves )
				{
					if ( captureFound )
					{

						if ( move.getMoveType() == "Capture" )
							allValidMoves.add( move );


					}
					else if ( move.getMoveType() == "Capture" )
					{

						allValidMoves.clear();
						captureFound = true;
						allValidMoves.add(move);

					}
					else
						allValidMoves.add(move);
				}

				// System.out.println("Valid moves size: "+validMoves.size());

			}
		}

		return allValidMoves;

	}

	public boolean getHelp()
	{

		return help;

	}

	public boolean humanHasWon()
	{

		// System.out.println("Player score: " + this.playerScore() );
		if ( this.playerScore() == 12 )
		{

			inPlayFlag = false;
			return true;

		}
		else
			return false;

	}

	public boolean inPlay()
	{

		return inPlayFlag;

	}

	public boolean isWon()
	{

		if ( !inPlay() )
			return false;
		else
			return ( this.humanHasWon() || this.aiHasWon() );

	}

	// Evaluate the utility of a leaf
	// Score = (ai_score + num_ai_kings) - (player_score + num_player_kings)
	public int leafUtility()
	{

		int aiScore = aiScore();
		int playerScore = playerScore();
		int aiKings = numKings( getAIColour() );
		int playerKings = numKings( getPlayerColour() );

		return ( aiScore + aiKings ) - (playerScore + playerKings );

	}

	public int numKings( String colour )
	{

		int numKings = 0;
		for ( int x = 0; x < 8; x++ )
		{
			for ( int y = 0; y < 8; y++ )
			{

				if ( checkers[x][y] != null && checkers[x][y].getColour() == colour )
					if ( checkers[x][y].getType() == PieceType.BLACK_KING || checkers[x][y].getType() == PieceType.RED_KING )
					{

						numKings++;

					}

			}
		}

		return numKings;

	}

	public Move makeAIMove()
	{

		int max = -10000;
		int ind = -1;
		int prev = max;
		boolean allSame = true;
		ArrayList<MoveAndScore> sameList = new ArrayList<MoveAndScore>();
		seCount =0;
		deCount=0;
		pCount=0;

		if ( this.whoseTurn() != "AI" )
			return null;

		aiMoveList = new ArrayList<MoveAndScore>();
		minimax( 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE );

		// iterate over successors and return the one with the highest eval result
		for ( int i = 0; i < aiMoveList.size(); ++i )
		{

				if (max < aiMoveList.get(i).score)
						max = aiMoveList.get(i).score;

		}

		// Choose move randomly from those with same score to make AI more unpredictable
		for ( int i = 0; i < aiMoveList.size(); ++i )
		{

			if ( aiMoveList.get(i).score == max )
				sameList.add( aiMoveList.get(i) );

		}

		Random rand = new Random();
		ind = rand.nextInt(sameList.size());
		return sameList.get(ind).move;

	}

	// AI minimax evaluation
	// Generate valid moves, if depth level reached, return utility estimate,
	// else start minimax evaluation.
	// Create a temporary copy of the game state so that actual game state isn't updated
	public int minimax( int depth, int player, int alpha, int beta )
	{

		int bestScore;

		if(player == 1)
				bestScore = -24;
		else
				bestScore = 24;

		ArrayList<Move> allValidMoves = this.generateAllValidSuccessors();

		// limit static evaluations acc. to diff. level,
		// or not at all at "Impossible" (Level 4)
		if(seCount <= seLevel || this.getDifficulty() == 4) {
			// determine outcomes and increase SE cost
			if (aiHasWon())

			{
					seCount++;
					return +30; // Static evaluation result for the AI winning

			}
			if (humanHasWon())
			{

					seCount++;
					return -30; // Static evaluation result for the AI losing

			}
			if ( depth > maxDepth )
			{

				return leafUtility();

			}
		}
		else
				return leafUtility();

		System.out.println("Doing minimax evaluation");

		for ( int i = 0; i < allValidMoves.size(); i++ )
		{

			// determine all board positions that aren't occupied
			Move move = allValidMoves.get(i);

			// increment dynamic evaluation cost
			Game tempGame = new Game( this );
			deCount++;

			if ( player == 1 )
			{ //AI's turn: get the highest result returned by minimax
					// place a piece at the first available position

				for ( BoardMove boardMove : move.moveList )
				{ // iterate through all board positions, 1 for a single capture, multiple for mutlicap
					tempGame.updateState( move.getMoveType(), boardMove );
				}

				// get the minimax evaluation result for making the previous move
				tempGame.setPlayer( "Human" ); // switch player
				int currentScore = tempGame.minimax( depth + 1, 2, alpha, beta ); // Increase

				if ( currentScore > bestScore )
						bestScore = currentScore;

				alpha = Math.max( alpha, currentScore );
				// store a mapping of complete evaluations (at depth 0) and their scores
				if ( depth == 0 )
						aiMoveList.add(new MoveAndScore(currentScore, move));

			}
			else if ( player == 2 )
			{//Human's turn: get the lowest result returned by minimax

				for ( BoardMove boardMove : move.moveList ) // iterate through all board positions, 1 for a single capture, multiple for mutlicap
				{
					tempGame.updateState( move.getMoveType(), boardMove );
				}

				tempGame.setPlayer( "AI" ); // switch player
				int currentScore = tempGame.minimax(depth + 1, 1, alpha, beta);

				if ( currentScore < bestScore )
						bestScore = currentScore;

				beta = Math.min( beta, currentScore );

			}

			// Add AB pruning & count the pruning operations carried out
			if( alpha >= beta )
			{

					pCount++;
					break;

			}

		}

		return bestScore;

	}

	// Player score is max number of pieces minus number of ai player's pieces remaining
	// Used to calculate leaf utility and to see if game is won
	public int playerScore()
	{

		int score = 12;
		for ( int x = 0; x < 8; x++ )
		{
			for ( int y = 0; y < 8; y++ )
			{
				if ( checkers[x][y] != null )
				{

					if ( checkers[x][y].getColour() == this.getAIColour() )
						score--;

				}
			}
		}

		return score;

	}

	public void setColour()
	{

		playerColour = activePlayer == "Human" ? "Black" : "Red";
		aiColour = playerColour == "Black" ? "Red" : "Black";

	}

	public void setDifficulty( String diffString )
	{

		if ( diffString == "Intermediate" )
		{

			difficulty = 2;
			maxDepth = 5;
			seLevel = 3;

		}
		else if ( diffString == "Hard" )
		{

			difficulty = 3;
			maxDepth = 7;
			seLevel = 5;

		}
		else if ( diffString == "Extreme" )
		{

			difficulty = 4;
			maxDepth = 10;
			seLevel = 10;

		}
		else
		{

			difficulty = 1;
			maxDepth = 2;
			seLevel = 1;

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

	// Initialise game state
	// Set checkers in the checker board array
	// Set game to in play
	public void startGame()
	{

		inPlayFlag = true;
		checkers = new Checker[8][8];
		setColour();

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

				if (activePlayer == "Human")
					checkers[tempX][y].setType( PieceType.BLACK );
				else
					checkers[tempX][y].setType( PieceType.RED );

			}
		}

	}

	// Given a new move, update the internal game state
	// Remove captured pieces, move pieces
	public void updateState( String moveType, BoardMove boardMove )
	{

		int oldX = boardMove.oldPos.x;
		int oldY = boardMove.oldPos.y;
		int newX = boardMove.newPos.x;
		int newY = boardMove.newPos.y;

		checkers[newX][newY] = checkers[oldX][oldY];
		checkers[newX][newY].setBoardPos( newX, newY );
		checkers[oldX][oldY] = null;

		// Convert piece to king if at the opposite end of board
		if ( (this.whoseTurn() == "Human" && newY == 0) || (this.whoseTurn() == "AI" && newY == 7) )
		{

			if ( checkers[newX][newY].getType() == PieceType.BLACK )
				checkers[newX][newY].setType( PieceType.BLACK_KING );
			else if ( checkers[newX][newY].getType() == PieceType.RED )
				checkers[newX][newY].setType( PieceType.RED_KING );

		}

		// If a capture, find the position of the captured piece, remove from game
		if ( moveType == "Capture" )
		{

			Point capturedPos = this.getCapturedPos( boardMove );
			checkers[capturedPos.x][capturedPos.y] = null;

		}

	}

	public String whoseTurn()
	{

		return activePlayer;

	}

}

// Class to hold information about an individual checker
// Includes its board position, its associated center in pixel coordinates,
// its colour and piece type
class Checker {

	private int boardPosX = 0;
	private int boardPosY = 0;
	public int centerX = 0;
	public int centerY = 0;
	public int coordX = 0;
	public int coordY = 0;
	private PieceType type;
	private String pieceColour;
	public static int SQUARESIZE;

	public Checker( int squareSize, int x, int y )
	{

		SQUARESIZE = squareSize;
		setBoardPos( x, y );

	}

	// Copy constructor
	public Checker( Checker original )
	{

		Point boardPos = original.getPos();
		this.boardPosX = boardPos.x;
		this.boardPosY = boardPos.y;
		this.centerX = original.centerX;
		this.centerY = original.centerY;
		this.coordX = original.coordX;
		this.coordY = original.coordY;
		this.type = original.getType();
		this.pieceColour = original.getColour();
		this.SQUARESIZE = original.SQUARESIZE;

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

		if (type == PieceType.RED_KING || type == PieceType.BLACK_KING)
		{

			String text = "K";
			FontMetrics fm = g.getFontMetrics();
			double textWidth = fm.getStringBounds(text, g).getWidth();
			g.setColor( new Color(163, 134, 19) );
			g.drawString(text, (int) (centerX-textWidth/2), centerY);

		}

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

	// Set checker's internal board position, and update its pixel coordinate centers
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

// Class for a game move, which holds the move type ("Move" or "Capture"),
// and a list of the moves (move list size is only > 1 for a multi capture move)
class Move
{
	public ArrayList<BoardMove> moveList = new ArrayList<BoardMove>();
	public String moveType;

	public Move( Point pos1, Point pos2 )
	{

		moveList.add( new BoardMove( pos1, pos2 ) );
		if ( (pos2.x - pos1.x == 1 || pos2.x - pos1.x == -1)  && (pos2.y - pos1.y == 1 || pos2.y - pos1.y == -1) )
			moveType = "Move";
		else
			moveType = "Capture";

	}


	public String getMove()
	{

		BoardMove move = (this.moveList).get(0);
		String string = move.oldPos + " -> " + move.newPos;
		// return this.moveList;
		return string;

	}

	public Point getMovePos()
	{

		BoardMove move = (this.moveList).get(0);
		return move.newPos;

	}

	public Point getStartPos()
	{

		BoardMove move = (this.moveList).get(0);
		return move.oldPos;

	}

	public String getMoveType()
	{

		return this.moveType;

	}

}

// Board move is a helper class for move, containing the starting position of
// the move, and the end position
class BoardMove
{

	// private Point oldPos;
	// private Point newPos;
	public Point oldPos;
	public Point newPos;
	// public Point capturedPos = null;

	public BoardMove( Point pos1, Point pos2 )
	{

		oldPos = pos1;
		newPos = pos2;

	}

	public Point getOldPos()
	{

		return oldPos;

	}

	public Point getNewPos()
	{

		return newPos;

	}

	public String getMove()
	{

		String string = this.oldPos + " -> " + this.newPos;
		// return this.moveList;
		return string;

	}

}

// Used for AI move evaluation, holds a Move and its associated minimax score
class MoveAndScore {

	int score;
	Move move;

	public MoveAndScore(int score, Move move) {

		this.score = score;
		this.move = move;

	}

	// Copy constructor
	public MoveAndScore( MoveAndScore original ) {

		this.score = original.score;
		this.move = original.move;

	}

}

// Enum of the possible piece types
enum PieceType
{

	BLACK,
	BLACK_KING,
	RED,
	RED_KING

}

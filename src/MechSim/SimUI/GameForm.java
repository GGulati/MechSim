package MechSim.SimUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * A Form with updating and rendering
 * @author Gurwinder Gulati
 *
 */
@SuppressWarnings({ "serial" })
public abstract class GameForm extends JFrame implements ActionListener, KeyListener, MouseListener
{
	static final Color BG_COLOR = new Color(220, 220, 220);//pale grey
	static final double DRAWING_TIME = 1000.0 / 30.0;//30 FPS for rendering
	
	Image m_buffer;//backbuffer - used for smoother drawing
	int m_bufferWidth, m_bufferHeight;//dimensions of backbuffer
	Toolkit m_toolkit;//used to acquire system-specific information, such as screen size and how to load images from disk

	long m_startTime, m_currentTime, m_prevTime;
	Timer m_timer;
	int m_targetFPS;
	double m_currentFPS, m_elapsedTime, m_drawingCountdown;
	
	Dimension m_windowedSize, m_fullScreenSize;
	boolean m_isFullScreen;
	
	static final int HIGHEST_VK_TRACKED = 525, LOWEST_VK_TRACKED = 8;//most keyboard keys - special keys such as the windows key are ignored
	boolean[] m_keyStatesWasPressed, m_keyStatesIsPressed;
	
	double m_mouseX, m_mouseY;
	boolean m_mouseLeftIsPressed, m_mouseLeftWasPressed, m_mouseRightIsPressed, m_mouseRightWasPressed;

	/**
	 * Creates a Form with updating and rendering
	 * @param fps Frames per second to update and render at (recommended 30 FPS)
	 * @param width Width of the form in pixels
	 * @param height Height of the form in pixels 
	 */
	public GameForm(int fps, int width, int height)
	{
		this(fps, true, width, height);
	}
	
	/**
	 * Creates a Form with updating and rendering
	 * @param fps Frames per second to update at - must be at least 30 and at most 1000
	 * @param show Whether or not the form should be visible immediately after creation
	 * @param width Width of the form in pixels
	 * @param height Height of the form in pixels 
	 */
	public GameForm(int fps, boolean show, int width, int height)
	{
		m_windowedSize = new Dimension(width < 200 ? 200 : width, height < 200 ? 200 : height);//minimum size is 200, 200

        InitControls();
        InitGraphics();
		m_keyStatesWasPressed = new boolean[HIGHEST_VK_TRACKED - LOWEST_VK_TRACKED];
		m_keyStatesIsPressed = new boolean[HIGHEST_VK_TRACKED - LOWEST_VK_TRACKED];
        
        m_targetFPS = fps < 30 ? 30 : fps > 1000 ? 1000 : fps;
		m_timer = new Timer(1000 / m_targetFPS, this);
		m_timer.start();
		m_startTime = m_prevTime = System.currentTimeMillis();//track the time between frames (since the timer merely guarantees that AT LEAST tick duration elapsed)

		this.setSize(m_windowedSize);
		this.setVisible(show);
		this.setLocationRelativeTo(null);//center screen
		this.setUndecorated(true);//no border or windows forms buttons
	}
	
	private void InitControls()
	{
		this.setTitle("Mechanoid Simulation");
		this.setBackground(BG_COLOR);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setFocusTraversalKeysEnabled(false);//no TAB or SHIFT-TAB to change controls
		this.addKeyListener(this);
		this.addMouseListener(this);
	}
	
	private void InitGraphics()
	{
		m_toolkit = Toolkit.getDefaultToolkit();
		
		m_fullScreenSize = m_toolkit.getScreenSize();
		m_isFullScreen = false;

		m_bufferWidth = m_windowedSize.width;
		m_bufferHeight = m_windowedSize.height;
	}
	
	/**
	 * Renders the current state. Should be overriden by the child class.
	 * @param g Graphics used to render
	 */
	protected abstract void Render(Graphics2D g);
	
	/**
	 * Updates the current state. Should be overriden by the child class.
	 * @param timeDelta Time in milliseconds since last update
	 */
	protected void Update(double timeDelta)
	{
		m_elapsedTime = (m_currentTime - m_startTime) / 1000.0f;//time, in seconds, since creation of GameForm
		m_currentFPS = Math.floor(1000.0 / timeDelta);//estimated FPS at this instant in time, rounded down
		
		if (IsKeyTriggered(KeyEvent.VK_F11))//toggle fullscreen
		{
			m_isFullScreen = !m_isFullScreen;
			this.setSize(m_isFullScreen ? m_fullScreenSize : m_windowedSize);
			if (m_isFullScreen)
				this.setLocation(0, 0);//so that the full window is visible
			else
				this.setLocationRelativeTo(null);//center screen
		}
		if (IsKeyPressed(KeyEvent.VK_ESCAPE))
			System.exit(0);
	}
	
	private void ProcessInput()
	{
		for (int i = 0; i < m_keyStatesWasPressed.length; i++)
		{
			m_keyStatesWasPressed[i] = m_keyStatesIsPressed[i];//remember the current state of a key
		}
		
		//adjust the WasPressed, but not the IsPressed since that will be changed by an event
		m_mouseLeftWasPressed = m_mouseLeftIsPressed;
		m_mouseRightWasPressed = m_mouseRightIsPressed;
		
		//update mouse position information
		PointerInfo mouse = MouseInfo.getPointerInfo();
		m_mouseX = mouse.getLocation().getX();
		m_mouseY = mouse.getLocation().getY();
	}
	
	/**
	 * @return Current frame rate. If it is lower than GetTargetFPS(), the Update() and Render() methods are taking too long.
	 */
	public double GetCurrentFPS() { return m_currentFPS; }
	
	/**
	 * @return Time elapsed since creation of form
	 */
	public double GetElapsedTime() { return m_elapsedTime; }
	
	/**
	 * @return Target frame rate given when form was created. The frame rate the form tries to update and render at.
	 */
	public int GetTargetFPS() { return m_targetFPS; }
	
	/**
	 * @param keyCode Key code of the key, as per KeyEvent.VK_[Key Name]
	 * @return Whether or not the key is currently pressed
	 */
	public boolean IsKeyPressed(int keyCode)
	{
		if (keyCode >= LOWEST_VK_TRACKED && keyCode <= HIGHEST_VK_TRACKED)
			return m_keyStatesIsPressed[keyCode - LOWEST_VK_TRACKED];
		return false;
	}

	/**
	 * @param keyCode Key code of the key, as per KeyEvent.VK_[Key Name]
	 * @return Whether or not the key was pressed last frame
	 */
	public boolean WasKeyPressed(int keyCode)
	{
		if (keyCode >= LOWEST_VK_TRACKED && keyCode <= HIGHEST_VK_TRACKED)
			return m_keyStatesWasPressed[keyCode - LOWEST_VK_TRACKED];
		return false;
	}
	
	/**
	 * @param keyCode Key code of the key, as per KeyEvent.VK_[Key Name]
	 * @return Whether or not the key was pressed this frame, but not last frame
	 */
	public boolean IsKeyTriggered(int keyCode)
	{
		if (keyCode >= LOWEST_VK_TRACKED && keyCode <= HIGHEST_VK_TRACKED)
			return m_keyStatesIsPressed[keyCode - LOWEST_VK_TRACKED] && !m_keyStatesWasPressed[keyCode - LOWEST_VK_TRACKED];
		return false;
	}

	/**
	 * @return Whether or not the mouse's left button is currently pressed
	 */
	public boolean IsMouseLeftPressed() { return m_mouseLeftIsPressed; }
	
	/**
	 * @return Whether or not the mouse's left button was pressed last frame
	 */
	public boolean WasMouseLeftPressed() { return m_mouseLeftWasPressed; }
	
	/**
	 * @return Whether or not the mouse's left button was pressed this frame, but not last frame
	 */
	public boolean IsMouseLeftTriggered() { return m_mouseLeftIsPressed && !m_mouseLeftWasPressed; }

	/**
	 * @return Whether or not the mouse's right button is currently pressed
	 */
	public boolean IsMouseRightPressed() { return m_mouseRightIsPressed; }

	/**
	 * @return Whether or not the mouse's right button was pressed last frame
	 */
	public boolean WasMouseRightPressed() { return m_mouseRightWasPressed; }

	/**
	 * @return Whether or not the mouse's right button was pressed this frame, but not last frame
	 */
	public boolean IsMouseRightTriggered() { return m_mouseRightIsPressed && !m_mouseRightWasPressed; }
	
	/**
	 * Loads an image from a file
	 * @param file Path of the image
	 * @return Image, if created properly. Otherwise returns null.
	 */
	public Image LoadImage(String file)
	{
		return m_toolkit.getImage(file);
	}
	
	Graphics2D graphics;
	//BEGIN ActionListener
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == m_timer)
		{
			//on each tick, update all objects
			m_currentTime = System.currentTimeMillis();
			double timeDelta = m_currentTime - m_prevTime;
			Update(timeDelta);
			ProcessInput();
			m_prevTime = m_currentTime;

			//but only draw the game at 30 FPS because the human eye sees things at about 28 hertz - any faster than 30 FPS would slow the game down with no benefit
			m_drawingCountdown -= timeDelta;
			if (m_drawingCountdown <= 0.0)
			{
				m_drawingCountdown = DRAWING_TIME;
				if (m_buffer == null)
					m_buffer = this.createImage(m_bufferWidth, m_bufferHeight);
	
				if (m_buffer != null)
				{
					//render on a backbuffer, then draw the backbuffer to the background of the form
					Graphics2D graphics = (Graphics2D)m_buffer.getGraphics();
					Render(graphics);
					this.getGraphics().drawImage(m_buffer, 0, 0, this.getContentPane().getWidth(), this.getContentPane().getHeight(), null);
					
					//clear the backbuffer afterwards
					graphics.setColor(BG_COLOR);
					graphics.fillRect(0, 0, m_bufferWidth, m_bufferHeight);
				}
			}
		}
	}
	//END ActionListener

	//BEGIN KeyListener
	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if (code >= LOWEST_VK_TRACKED && code <= HIGHEST_VK_TRACKED)
			m_keyStatesIsPressed[code - LOWEST_VK_TRACKED] = true;//update current state of the key
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();
		if (code >= LOWEST_VK_TRACKED && code <= HIGHEST_VK_TRACKED)
			m_keyStatesIsPressed[code - LOWEST_VK_TRACKED] = false;//update current state of the key
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		//ignored
	}
	//END KeyListener

	//BEGIN MouseListener
	@Override
	public void mouseClicked(MouseEvent e)
	{
		//ignore
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		//ignore
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		//ignore
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			m_mouseLeftIsPressed = true;
		else if (SwingUtilities.isRightMouseButton(e))
			m_mouseRightIsPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			m_mouseLeftIsPressed = false;
		else if (SwingUtilities.isRightMouseButton(e))
			m_mouseRightIsPressed = false;
	}
	//END MouseListener
}

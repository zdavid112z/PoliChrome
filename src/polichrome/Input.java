package polichrome;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static final int MAX_KEYS = 256;
	public static final int MAX_MOUSE_BUTTONS = 8;
	public static final byte KEY_RELEASED = 0;
	public static final byte KEY_JUST_RELEASED = 1;
	public static final byte KEY_PRESSED = 2;
	public static final byte KEY_JUST_PRESSED = 3;
	
	private static byte[] m_MouseButtons = new byte[MAX_MOUSE_BUTTONS];
	private static byte[] m_Keys = new byte[MAX_KEYS];
	private static int m_MouseRawX = 0;
	private static int m_MouseRawY = 0;
	private static int m_MouseX = 0;
	private static int m_MouseY = 0;
	private static int m_MouseWheel = 0;
	
	public Input()
	{
		for(int i = 0; i < MAX_MOUSE_BUTTONS; i++)
			m_MouseButtons[i] = KEY_RELEASED;
		for(int i = 0; i < MAX_KEYS; i++)
			m_Keys[i] = KEY_RELEASED;
	}
	
	public static void Update(float fx, float fy) {
		m_MouseX = (int)((float)m_MouseRawX * fx);
		m_MouseY = (int)((float)m_MouseRawY * fy);
		for(int i = 0; i < MAX_KEYS; i++)
		{
			if(m_Keys[i] == KEY_JUST_PRESSED)
				m_Keys[i] = KEY_PRESSED;
			else if(m_Keys[i] == KEY_JUST_RELEASED)
				m_Keys[i] = KEY_RELEASED;
		}
		for(int i = 0; i < MAX_MOUSE_BUTTONS; i++)
		{
			if(m_MouseButtons[i] == KEY_JUST_PRESSED)
				m_MouseButtons[i] = KEY_PRESSED;
			else if(m_MouseButtons[i] == KEY_JUST_RELEASED)
				m_MouseButtons[i] = KEY_RELEASED;
		}
	}
	
	public static boolean IsKeyDown(int v) {
		return (m_Keys[v] & 2) != 0;
	}
	
	public static boolean WasKeyPressed(int v) {
		boolean b = (m_Keys[v] & 2) != 0;
		if(b)
			m_Keys[v] = KEY_RELEASED;
		return b;
	}
	
	/*public static byte GetKeyState(int v) {
		return m_Keys[v];
	}*/
	
	public static int GetMouseX() {
		return m_MouseX;
	}
	
	public static int GetMouseY() {
		return m_MouseY;
	}
	
	public static boolean IsMouseButtonDown(int index) {
		return (m_MouseButtons[index] & 2) != 0;
	}
	
	public static byte GetMouseButtonState(int index) {
		return m_MouseButtons[index];
	}
	
	public static int GetMouseWheel() {
		return m_MouseWheel;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		m_MouseWheel = e.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		m_MouseRawX = e.getX();
		m_MouseRawY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_MouseRawX = e.getX();
		m_MouseRawY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		m_MouseButtons[e.getButton()] = KEY_JUST_PRESSED;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		m_MouseButtons[e.getButton()] = KEY_JUST_RELEASED;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		m_Keys[e.getKeyCode()] = KEY_JUST_PRESSED;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		m_Keys[e.getKeyCode()] = KEY_JUST_RELEASED;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	public static void ResetKey(int mKeycode) {
		m_Keys[mKeycode] = KEY_RELEASED;
	}

}


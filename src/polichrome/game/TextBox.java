package polichrome.game;

import polichrome.Vector2;
import polichrome.graphics.PFont;
import polichrome.graphics.Renderer;

public class TextBox extends Entity {

	private Vector2 m_Position;
	private String m_Text;
	private int m_Colour;
	private PFont m_Font;
	
	public TextBox(Vector2 pos, String text, int colour, PFont font) {
		super("TextBox");
		m_Position = pos;
		m_Text = text;
		m_Colour = colour;
		m_Font = font;
	}

	@Override
	public void Update() {
		
	}

	@Override
	public void Render(Renderer r) {
		r.DrawText(m_Font, (int)m_Position.x, (int)m_Position.y, m_Text, m_Colour, true);
	}

}

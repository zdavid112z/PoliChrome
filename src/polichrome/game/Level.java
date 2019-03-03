package polichrome.game;

import polichrome.graphics.Renderer;

public abstract class Level {
	
	protected final String m_Name;
	
	public Level(String name) {
		m_Name = name;
	}
	
	public String GetName() { return m_Name; }
	
	public void Update() { }
	
	public void Render(Renderer r) { }
	
	public abstract String GetNextLevel();
	
	public abstract int GetEnding();
}

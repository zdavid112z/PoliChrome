package polichrome.game;

import polichrome.graphics.Renderer;

public abstract class Entity {

	protected boolean m_IsAlive;
	protected final String m_Name;
	
	public Entity(String name) {
		m_Name = name;
		m_IsAlive = true;
	}
	
	public void Die() { m_IsAlive = false; }
	public boolean IsAlive() { return m_IsAlive; }
	public String GetName() { return m_Name; }
	
	public abstract void Update();
	public abstract void Render(Renderer r);
	
}

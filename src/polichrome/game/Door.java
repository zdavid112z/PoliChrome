package polichrome.game;

import polichrome.Main;
import polichrome.Vector2;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Door extends Mob {

	private float m_Timer = 0;
	private boolean m_Locked = true;
	private PImage m_DoorLocked, m_DoorUnlocked;
	private boolean m_PermaUnlocked = false;
	
	public Door(Vector2 pos) {
		super("Door");
		m_Position = pos.Multiply(32).Subtract(new Vector2(0, 64));
		m_Size = new Vector2(32, 96);
		m_DoorLocked = new PImage("/entity/door_locked.png");
		m_DoorUnlocked = new PImage("/entity/door_unlocked.png");
	}
	
	public void UnlockFor(float time) {
		m_Locked = false;
		m_IgnoreCollision = true;
		if(time == -1)
			m_PermaUnlocked = true;
		else {
			m_Timer = time;
			m_PermaUnlocked = false;
		}
	}
	//public void SetLocked(boolean b) { m_Locked = b; if(b == false) m_IgnoreCollision = true; else m_IgnoreCollision = false; }
	public boolean IsLocked() { return m_Locked; }
	
	public void Update() {
		if(m_PermaUnlocked) {
			m_Locked = false;
			m_IgnoreCollision = true;
		}
		else if(m_Timer > 0) {
			m_Timer -= Main.g_Game.GetDeltaTime();
			m_Locked = false;
			m_IgnoreCollision = true;
		}
		else {
			m_Locked = true;
			m_IgnoreCollision = false;
		}
	}
	
	public void Render(Renderer r) {
		if(m_Locked)
			r.DrawImage(m_DoorLocked, (int)m_Position.x, (int)m_Position.y, true);
		else r.DrawImage(m_DoorUnlocked, (int)m_Position.x, (int)m_Position.y, true);
	}
	
}

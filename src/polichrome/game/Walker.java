package polichrome.game;

import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.game.tile.Tile;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Walker extends Mob {

	@SuppressWarnings("unused")
	private PImage m_Stand;
	
	private Vector2 m_Start, m_End;
	private PImage[] m_Walking;
	private int m_Frame = 0;
	private float m_Timer = 0;
	private float m_FrameInterval = 0.07f;
	private float m_Lerp = 0.01f;
	private boolean m_ToEnd = true;
	
	//Tile space
	public Walker(Vector2 start, Vector2 end, float speed) {
		super("Walker");
		m_Stand = new PImage("/entity/enemy2/stand.png");
		m_Walking = new PImage[6];
		m_Size.x = 28;
		m_ColBoxOffset.x = 114 / 2 - m_Size.x / 2;
		m_Size.y = 88;
		m_ColBoxOffset.y = 0;
		m_Speed = speed;
		m_IgnoreCollision = true;
		m_Start = new Vector2(start.Multiply(Tile.SIZE).Add(new Vector2(0, Tile.SIZE).Subtract(new Vector2(32, 88))));
		m_End = new Vector2(end.Multiply(32).Add(new Vector2(0, 32).Subtract(new Vector2(32, 88))));
		for(int i = 0; i < 5; i++) {
			m_Walking[i] = new PImage("/entity/enemy2/walk" + (i + 1) + ".png");
		}
	}
	
	public Vector2 GetCenteredCoordinates() {
		return GetCenteredPosition();
	}

	public Vector2 GetCenteredPosition() {
		Vector2 pos = m_Start.Lerp(m_End, m_Lerp);
		return new Vector2(
				pos.x + m_Size.x / 2 + m_ColBoxOffset.x,
				pos.y + m_Size.y / 2 + m_ColBoxOffset.y);
	}
	
	@Override
	public void Update() {
		super.Update();
		m_Timer += Main.g_Game.GetDeltaTime();
		if(m_Timer >= m_FrameInterval) {
			m_Timer -= m_FrameInterval;
			m_Frame++;
			if(m_Frame >= 5)
				m_Frame = 0;
		}
		
		if(m_ToEnd)
			m_Lerp += m_Speed / (m_End.x - m_Start.x);
		else m_Lerp -= m_Speed / (m_End.x - m_Start.x);
		
		if(m_Lerp <= 0) {
			m_ToEnd = true;
		}
		else if(m_Lerp >= 1) {
			m_ToEnd = false;
		}
		
		if(m_DeathState != 0)
			return;
		
		Player p = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer();
		if(p.GetPlayerPower().GetActivePower() != ParticleType.YELLOW) {
			Vector2 pp = p.GetCenteredCoordinates();
			Vector2 ps = p.GetSize();
			Vector2 pos = GetCenteredPosition();
			if(Math.abs(pp.x - pos.x) <= ps.x / 2 + m_Size.x / 2)
				if(Math.abs(pp.y - pos.y) <= ps.y / 2 + m_Size.y / 2)
					if(p.GetPlayerPower().GetActivePower() == ParticleType.PURPLE)
						Die();
					else p.Die();
		}
	}

	@Override
	public void Render(Renderer r) {
		Vector2 pos = m_Start.Lerp(m_End, m_Lerp);
		if(m_ToEnd)
			r.DrawImage(m_Walking[m_Frame], (int)pos.x, (int)pos.y, true);
		else
			r.DrawImageFlipped(m_Walking[m_Frame], (int)pos.x, (int)pos.y, true);
	}

}

package polichrome.game;

import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Saw extends Entity {

	private static final Vector2 SIZE = new Vector2(32, 32);
	private static final float m_FrameInterval = 0.04f;
	private float m_Timer = 0;
	private Vector2 m_Position;
	private PImage[] m_Images;
	private int m_Frame;
	
	public Saw(Vector2 position) {
		super("Saw");
		m_Position = position.Multiply(32);
		m_Images = new PImage[3];
		m_Images[0] = new PImage("/entity/saw1.png");
		m_Images[1] = new PImage("/entity/saw2.png");
		m_Images[2] = new PImage("/entity/saw3.png");
		m_Frame = Main.g_Game.GetRandom().nextInt(3);
	}

	@Override
	public void Update() {
		m_Timer += Main.g_Game.GetDeltaTime();
		if(m_Timer >= m_FrameInterval)
		{
			m_Frame++;
			if(m_Frame >= 3)
				m_Frame = 0;
			m_Timer -= m_FrameInterval;
		}
		
		Player p = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer();
		if(p.GetPlayerPower().GetActivePower() != ParticleType.YELLOW && p.GetPlayerPower().GetActivePower() != ParticleType.PURPLE) {
			Vector2 pp = p.GetCenteredCoordinates();
			Vector2 ps = p.GetSize();
			if(Math.abs(pp.x - m_Position.x) <= ps.x / 2 + SIZE.x / 2) {
				if(Math.abs(pp.y - m_Position.y) <= ps.y / 2 + SIZE.y / 2) {
					//if(p.GetPlayerPower().GetActivePower() == ParticleType.PURPLE)
					//	Die();
					//else 
						p.Die();
				}
			}
		}
	}

	@Override
	public void Render(Renderer r) {
		r.DrawImage(m_Images[m_Frame], (int)(m_Position.x), (int)(m_Position.y), true);
	}

	
	
}

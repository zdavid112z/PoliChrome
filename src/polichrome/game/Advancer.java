package polichrome.game;

import polichrome.Audio;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.graphics.Renderer;

public class Advancer extends Entity {

	private Vector2 m_Position;
	private Vector2 m_Size;
	private Power m_Power;
	private float m_EndDuration = 0.8f;
	private float m_Timer = 0;
	private boolean m_Ending = false;
	
	public Advancer(Vector2 pos) {
		super("Advancer");
		m_Position = pos.Multiply(32).Subtract(new Vector2(32, 32));
		m_Size = new Vector2(96, 96);
		m_Power = new Power() {
			@Override
			public Vector2 GetPowerFocalPoint() {
				return m_Position.Add(new Vector2(Main.g_Game.GetRandom().nextInt((int)m_Size.x), Main.g_Game.GetRandom().nextInt((int)m_Size.y)));
			}
		};
		
		m_Power.GetParticleDesc().changeImmortalChance = 1;
		m_Power.GetParticleDesc().minLife = 0.3f;
		m_Power.GetParticleDesc().maxLife = 1.0f;
		m_Power.GetParticleDesc().minMaxDistance = 5;
		m_Power.GetParticleDesc().maxMaxDistance = 20;
		m_Power.GetParticleDesc().minSpeed = 1;
		m_Power.GetParticleDesc().maxSpeed = 3;
		m_Power.GetParticleDesc().minRotationSpeed = 1;
		m_Power.GetParticleDesc().maxRotationSpeed = 2;
		m_Power.GetParticleDesc().minOffset = 0;
		m_Power.GetParticleDesc().maxOffset = 0;
		
		m_Power.GetPowerSet().white = 500;
	}
	
	public Vector2 GetCenteredCoords() {
		return m_Position.Add(m_Size.Multiply(0.5f));
	}

	@Override
	public void Update() {
		m_Power.Update();
		if(!m_Ending) {
			Player p = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer();
			Vector2 pp = p.GetCenteredCoordinates();
			Vector2 ps = p.GetSize();
			Vector2 cc = m_Position.Add(m_Size.Multiply(0.5f));
			if(Math.abs(pp.x - cc.x) <= ps.x / 2.0f + m_Size.x / 2.0f) {
				if(Math.abs(pp.y - cc.y) <= ps.y / 2.0f + m_Size.y / 2.0f) {
					(new Audio("/sfx/level_end.wav")).Play();
					m_Ending = true;
				}
			}
		}
		if(m_Ending) {
			m_Timer += Main.g_Game.GetDeltaTime();
			if(m_Timer >= m_EndDuration) {
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).SetEnding(2);
			}
		}
	}

	@Override
	public void Render(Renderer r) {
		
	}

}

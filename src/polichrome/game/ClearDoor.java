package polichrome.game;

import polichrome.Audio;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.tile.Tile;
import polichrome.graphics.Renderer;

public class ClearDoor extends Entity {

	protected Vector2 m_Position = new Vector2();
	protected Vector2 m_Size = new Vector2();
	protected Door m_Door;
	protected Power m_Power;
	
	public ClearDoor(Vector2 pos, Door door) {
		super("ClearDoor");
		m_Size.x = 12;
		m_Size.y = 96;
		m_Position = pos.Multiply(Tile.SIZE).Subtract(new Vector2(0, 64));
		m_Door = door;
		m_Door.UnlockFor(-1);
		
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

	@Override
	public void Update() {
		m_Power.Update();
		Player p = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer();
		Vector2 pp = p.GetCenteredCoordinates();
		Vector2 ps = p.GetSize();
		Vector2 cc = m_Position.Add(m_Size.Multiply(0.5f));
		if(Math.abs(pp.x - cc.x) <= ps.x / 2.0f + m_Size.x / 2.0f) {
			if(Math.abs(pp.y - cc.y) <= ps.y / 2.0f + m_Size.y / 2.0f) {
				(new Audio("/sfx/power_clear.wav")).Play();
				m_Door.UnlockFor(0);
				p.GetPower().GetPowerSet().SetZero();
				p.GetPlayerPower().Release();
				m_Power.GetPowerSet().white = 0;
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).SetCheckpoint(m_Position.Add(new Vector2(-114 / 2 + 36 / 2, 2)));
				Die();
			}
		}
	}

	@Override
	public void Render(Renderer r) {
		
	}

}

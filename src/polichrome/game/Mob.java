
package polichrome.game;

import polichrome.Audio;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.tile.Tile;
import polichrome.graphics.Renderer;

public abstract class Mob extends Entity {

	// 0 = alive, 1 = dying, 2 = dead = m_IsAlive = false
	protected int m_DeathState = 0;
	protected float m_DeathTimer = 0.9f;
	protected static Power m_DeathAnimation;
	protected Vector2 m_Position;
	protected Vector2 m_Velocity;
	protected Vector2 m_Size;
	protected Vector2 m_ColBoxOffset;
	protected float m_Speed;
	protected float m_JumpPower;
	protected boolean m_DirectionLeft = false;
	protected boolean m_Moving = false;
	protected boolean m_InAir = true;
	protected boolean m_IgnoreCollision = false;
	
	public Mob(String name) {
		super(name);
		m_Position = new Vector2();
		m_Velocity = new Vector2();
		m_ColBoxOffset = new Vector2();
		m_Size = new Vector2();
	}
		
	public Vector2 GetCenteredCoordinates() {
		return new Vector2(
				m_Position.x + m_Size.x / 2 + m_ColBoxOffset.x,
				m_Position.y + m_Size.y / 2 + m_ColBoxOffset.y);
	}
	
	public float CheckCanWalkHQ(float vx, float vy) {
		if(CheckCanWalk(vx, vy) == false) {
			if(CheckCanWalk(vx / 2.0f, vy / 2.0f) == false) {
				if(CheckCanWalk(vx / 4.0f, vy / 4.0f) == false) {
					if(CheckCanWalk(vx / 8.0f, vy / 8.0f) == false) {
						if(CheckCanWalk(vx / 16.0f, vy / 16.0f) == false) {
							return 0.0f;
						} else return 16.0f;
					} else return 8.0f;
				} else return 4.0f;
			} else return 2.0f;
		} else return 1.0f;
	}
	
	public boolean CheckCanWalk(float vx, float vy) {
		float nx = (m_Position.x + vx + m_Size.x / 2 + m_ColBoxOffset.x);
		float ny = (m_Position.y + vy + m_Size.y / 2 + m_ColBoxOffset.y);
		
		GameLevel l = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel());
		for(int i = Math.max((int)nx / Tile.SIZE - 4, 0); i < Math.min((int)nx / Tile.SIZE + 4, l.GetMap().GetWidth()); i++)
		{
			for(int j = Math.max((int)ny / Tile.SIZE - 4, 0); j < Math.min((int)ny / Tile.SIZE + 4, l.GetMap().GetHeight()); j++)
			{
				Tile t = l.GetTileAt(i, j);
				if(t.IsWalkable())
					continue;
				int tcx = i * Tile.SIZE + Tile.SIZE / 2;
				int tcy = j * Tile.SIZE + Tile.SIZE / 2;
				
				if(Math.abs((int)nx - tcx) <= m_Size.x / 2 + Tile.SIZE / 2)
					if(Math.abs((int)ny - tcy) <= m_Size.y / 2 + Tile.SIZE / 2)
						return false;
			}			
		}
		
		if(m_IgnoreCollision)
			return true;
		
		for(int i = 0; i < ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetNumEntities(); i++) {
			if(((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetEntityAt(i) instanceof Mob) {
				Mob b = (Mob)(((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetEntityAt(i));
				if(b.m_DeathState != 0)
					continue;
				if(b.m_IgnoreCollision)
					continue;
				if(b != this) {
					Vector2 oc = b.GetCenteredCoordinates();
					if(Math.abs(nx - oc.x) <= m_Size.x / 2 + b.GetSize().x / 2)
						if(Math.abs(ny - oc.y) <= m_Size.y / 2 + b.GetSize().y / 2)
							return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean IsStuck() {
		return !CheckCanWalk(0, 0);
	}
	
	public void Die() {
		if(m_DeathState != 0)
			return;
		m_DeathState = 1;
		if(this instanceof Player)
			(new Audio("/sfx/death.wav")).Play();
		else (new Audio("/sfx/enemy_death.wav")).Play();
		m_DeathAnimation = new Power() {
			@Override
			public Vector2 GetPowerFocalPoint() {
				return GetCenteredCoordinates();
			}
		};
		
		m_DeathAnimation.SetIntervel(1);
		m_DeathAnimation.GetParticleDesc().applyOffset = true;
		m_DeathAnimation.GetParticleDesc().immortal = false;
		m_DeathAnimation.GetParticleDesc().minLife = 2;
		m_DeathAnimation.GetParticleDesc().maxLife = 4;
		m_DeathAnimation.GetParticleDesc().minMaxDistance = 5;
		m_DeathAnimation.GetParticleDesc().maxMaxDistance = 64;
		m_DeathAnimation.GetParticleDesc().minSpeed = 1;
		m_DeathAnimation.GetParticleDesc().maxSpeed = 2;
		m_DeathAnimation.GetParticleDesc().minRotationSpeed = 3;
		m_DeathAnimation.GetParticleDesc().maxRotationSpeed = 4;
		m_DeathAnimation.GetParticleDesc().minOffset = 0;
		m_DeathAnimation.GetParticleDesc().maxOffset = 8;
		m_DeathAnimation.GetPowerSet().red = 1000;
		m_Speed /= 2.0f;
		m_JumpPower /= 2.0f;
	}
	
	public Vector2 GetPosition() {
		return m_Position;
	}
	
	public Vector2 GetSize() {
		return m_Size;
	}

	@Override
	public void Update() {
		if(m_DeathState == 1)
		{
			m_DeathTimer -= Main.g_Game.GetDeltaTime();
			m_DeathAnimation.Update();
			if(m_DeathTimer <= 0) {
				m_DeathState = 2;
				m_IsAlive = false;
			}
		}
	}

	@Override
	public void Render(Renderer r) {
		
	}

}

package polichrome.game;

import java.awt.event.KeyEvent;

import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Player extends Mob {

	private PImage m_ImageStand;
	private PImage[] m_ImageWalk;
	private PImage m_ImageJump;
	private PImage m_SmallImageStand;
	private PImage[] m_SmallImageWalk;
	private PImage m_SmallImageJump;
	protected boolean m_Small = false;
	private float m_MaxVelocity = 100;
	private int m_FramesInterval = 3;
	private int m_Timer = 0;
	private Power m_Power;
	private PlayerGUI m_GUI;
	private PlayerPower m_PlayerPower;
	
	public Player(float x, float y) {
		super("Player");
		m_Position.x = x;
		m_Position.y = y;
		m_Size.x = 36;
		m_ColBoxOffset.x = 114 / 2 - m_Size.x / 2;
		m_Size.y = 88;
		m_ColBoxOffset.y = 0;
		m_Speed = 4;
		m_JumpPower = 7;
		m_ImageStand = new PImage("/player/stand.png");
		m_ImageWalk = new PImage[9];
		for(int i = 1; i < 10; i++)
			m_ImageWalk[i - 1] = new PImage("/player/walk" + i + ".png");
		m_ImageJump= new PImage("/player/jump.png");
		
		m_SmallImageStand = new PImage("/player/small/stand.png");
		m_SmallImageWalk = new PImage[9];
		for(int i = 1; i < 10; i++)
			m_SmallImageWalk[i - 1] = new PImage("/player/small/walk" + i + ".png");
		m_SmallImageJump= new PImage("/player/small/jump.png");
		
		m_Power = new Power() {
			
			@Override
			public Vector2 GetPowerFocalPoint() {
				return GetGunCoordinates();
			}
		};
		m_Power.GetParticleDesc().changeImmortalChance = 0.9985f;
		m_Power.GetParticleDesc().minLife = 0.3f;
		m_Power.GetParticleDesc().maxLife = 1.0f;
		m_Power.GetParticleDesc().minMaxDistance = 5;
		m_Power.GetParticleDesc().maxMaxDistance = 20;
		m_Power.GetParticleDesc().minSpeed = 1;
		m_Power.GetParticleDesc().maxSpeed = 3;
		m_Power.GetParticleDesc().minRotationSpeed = 1;
		m_Power.GetParticleDesc().maxRotationSpeed = 2;
		m_Power.GetParticleDesc().minOffset = 0;
		m_Power.GetParticleDesc().maxOffset = 8;
		
		m_PlayerPower = new PlayerPower(this);
		m_GUI = new PlayerGUI(this);
	}
	
	public void Die() {
		super.Die();
		m_PlayerPower.Release();
	}
	
	public Power GetPower() { return m_Power; }
	public PlayerPower GetPlayerPower() { return m_PlayerPower; }
	
	public Vector2 GetGunCoordinates() { 
		Vector2 f = GetCenteredCoordinates();
		f.y -= 19;
		if(m_DirectionLeft)
			f.x -= 26;
		else f.x += 26;
		return f; 
	}
	
	@Override
	public void Update() {
		super.Update();
		if(m_GUI.UpdateSelected() == null)
			m_PlayerPower.Update(ParticleType.OTHER);
		else m_PlayerPower.Update(m_GUI.GetSelected().type);
		if(!m_PlayerPower.GetPowerActive())
			m_Power.Update();
		
		if(Input.IsKeyDown(KeyEvent.VK_M))
			m_Power.GetPowerSet().blue++;
		if(Input.IsKeyDown(KeyEvent.VK_N))
		{
			if(m_Power.GetPowerSet().blue > 0)			
				m_Power.GetPowerSet().blue--;
		}
		
		if(m_Velocity.y != 0)
			m_InAir = true;
		else m_InAir = false;
		
		float jump = m_PlayerPower.GetJump(m_JumpPower);
		if((Input.WasKeyPressed(KeyEvent.VK_SPACE) || Input.WasKeyPressed(KeyEvent.VK_W)) && !m_InAir)
			m_Velocity.y = -jump;
		m_Velocity.y += ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetGravity() * Main.g_Game.GetDeltaTime();
		if(m_Velocity.y < -m_MaxVelocity)
			m_Velocity.y = -m_MaxVelocity;
		if(m_Velocity.y > m_MaxVelocity)
			m_Velocity.y = m_MaxVelocity;
		
		m_Velocity.x = 0;
		float speed = m_PlayerPower.GetSpeed(m_Speed);
		if(Input.IsKeyDown(KeyEvent.VK_A))
			m_Velocity.x -= speed;
		if(Input.IsKeyDown(KeyEvent.VK_D))
			m_Velocity.x += speed;
		
		boolean stuck = IsStuck();
		if(stuck)
			Die();
		if(m_Velocity.y != 0)
		{
			float cw = CheckCanWalkHQ(0, m_Velocity.y);
			if(cw != 0) {
				m_Position.y += m_Velocity.y / cw;
				m_InAir = true;
			}
			else
			{
				if(m_Velocity.y < 0)
					m_Velocity.y = 0;
				m_Velocity.y = 0;
			}
		}
		
		m_Moving = false;
		if(m_Velocity.x != 0)
		{
			float cw = CheckCanWalkHQ(m_Velocity.x, 0);
			if(cw != 0)
			{
				m_Position.x += m_Velocity.x / cw;
				m_Moving = true;	
				m_DirectionLeft = m_Velocity.x > 0 ? false : true;
			} else m_Timer = 0;
		} else m_Timer = 0;
		//m_InAir = false;
		
		m_Timer++;
		
		//System.out.println(m_InAir + " : " + m_Velocity.y);
		
		m_GUI.Update();
	}

	@Override
	public void Render(Renderer r) {
		if(!m_Small) {
			if(m_DirectionLeft) {
				if(m_InAir)
					r.DrawImageFlipped(m_ImageJump, (int)m_Position.x, (int)m_Position.y, true);
				else if(!m_Moving)
					r.DrawImageFlipped(m_ImageStand, (int)m_Position.x, (int)m_Position.y, true);
				else
					r.DrawImageFlipped(m_ImageWalk[(m_Timer / m_FramesInterval) % m_ImageWalk.length], (int)m_Position.x, (int)m_Position.y, true);
			}
			else {
				if(m_InAir)
					r.DrawImage(m_ImageJump, (int)m_Position.x, (int)m_Position.y, true);
				else if(!m_Moving)
					r.DrawImage(m_ImageStand, (int)m_Position.x, (int)m_Position.y, true);
				else
					r.DrawImage(m_ImageWalk[(m_Timer / m_FramesInterval) % m_ImageWalk.length], (int)m_Position.x, (int)m_Position.y, true);
			}
		}
		else {
			if(m_DirectionLeft) {
				if(m_InAir)
					r.DrawImageFlipped(m_SmallImageJump, (int)m_Position.x + 36, (int)m_Position.y + 44, true);
				else if(!m_Moving)
					r.DrawImageFlipped(m_SmallImageStand, (int)m_Position.x + 36, (int)m_Position.y + 44, true);
				else
					r.DrawImageFlipped(m_SmallImageWalk[(m_Timer / m_FramesInterval) % m_ImageWalk.length], (int)m_Position.x + 36, (int)m_Position.y + 44, true);
			}
			else {
				if(m_InAir)
					r.DrawImage(m_SmallImageJump, (int)m_Position.x + 36, (int)m_Position.y + 44, true);
				else if(!m_Moving)
					r.DrawImage(m_SmallImageStand, (int)m_Position.x + 36, (int)m_Position.y + 44, true);
				else
					r.DrawImage(m_SmallImageWalk[(m_Timer / m_FramesInterval) % m_ImageWalk.length], (int)m_Position.x + 36, (int)m_Position.y + 44, true);
			}
		}
		
		m_GUI.Render(r);
		
	}

}

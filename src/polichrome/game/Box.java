package polichrome.game;

import java.awt.event.KeyEvent;

import polichrome.Audio;
import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Box extends Mob {
	
	private Power m_Power;
	private Power m_OrangePowerVFX;
	private float m_GrabDistance = 200;
	private float m_SafetyDistance = 220;
	private float m_DefaultMaxVelocity = 5;
	private float m_OrangeMaxVelocity = 8.0f;
	private float m_OrangePower = 8;
	private float m_OrangeFrictionModifier = 0.4f;
	private float m_MaxVelocity = 5;
	private PImage m_Image;
	private boolean m_Grabbed = false;
	private boolean m_Orange = false;
	
	public Box(Vector2 pos) {
		super("Box");
		m_Image = new PImage("/entity/box.png");
		m_Position = new Vector2(pos.x, pos.y);
		m_Size = new Vector2(30, 30);
		m_Velocity = new Vector2();
		//m_ColBoxOffset.y = -2;
		
		m_Power = new Power() {

			@Override
			public Vector2 GetPowerFocalPoint() {
				//return ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer().GetGunCoordinates();
				return GetCenteredCoordinates();
			}
			
		};
		m_Power.GetParticleDesc().applyOffset = true;
		m_Power.GetParticleDesc().immortal = false;
		//m_Power.GetParticleDesc().changeImmortalChance = 0.997f;
		m_Power.GetParticleDesc().minLife = 0.1f;
		m_Power.GetParticleDesc().maxLife = 1.0f;
		m_Power.GetParticleDesc().minMaxDistance = 10; 
		m_Power.GetParticleDesc().maxMaxDistance = 30;
		m_Power.GetParticleDesc().minOffset = 0;
		m_Power.GetParticleDesc().minOffset = 8;
		m_Power.GetParticleDesc().minSpeed = 1;
		m_Power.GetParticleDesc().maxSpeed = 3;
		m_Power.GetParticleDesc().minRotationSpeed = 1;
		m_Power.GetParticleDesc().maxRotationSpeed = 2;
		m_Power.SetType1(0.2f, 0.3f, 0.5f, 0.7f);
		
		m_Power.GetPowerSet().blue = 500;
		m_Power.SetIntervel(0.2f);
		
		m_OrangePowerVFX = new Power() {

			@Override
			public Vector2 GetPowerFocalPoint() {
				//return ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer().GetGunCoordinates();
				return GetCenteredCoordinates();
			}
			
		};
		m_OrangePowerVFX.GetParticleDesc().applyOffset = true;
		m_OrangePowerVFX.GetParticleDesc().immortal = false;
		//m_OrangePowerVFX.GetParticleDesc().changeImmortalChance = 0.997f;
		m_OrangePowerVFX.GetParticleDesc().minLife = 0.1f;
		m_OrangePowerVFX.GetParticleDesc().maxLife = 1.0f;
		m_OrangePowerVFX.GetParticleDesc().minMaxDistance = 10; 
		m_OrangePowerVFX.GetParticleDesc().maxMaxDistance = 30;
		m_OrangePowerVFX.GetParticleDesc().minOffset = 0;
		m_OrangePowerVFX.GetParticleDesc().minOffset = 8;
		m_OrangePowerVFX.GetParticleDesc().minSpeed = 1;
		m_OrangePowerVFX.GetParticleDesc().maxSpeed = 3;
		m_OrangePowerVFX.GetParticleDesc().minRotationSpeed = 1;
		m_OrangePowerVFX.GetParticleDesc().maxRotationSpeed = 2;
		m_OrangePowerVFX.SetType1(0.2f, 0.3f, 0.5f, 0.7f);
		
		m_OrangePowerVFX.GetPowerSet().orange = 600;
		m_OrangePowerVFX.SetIntervel(0.2f);
				
		m_MaxVelocity = m_DefaultMaxVelocity;
	}

	public boolean GetGrabbed() { return m_Grabbed; }
	public void SetGrabbed(boolean b) { m_Grabbed = b; }
	public void SetOrange(boolean b) { m_Orange = b; }
	
	private void OnDrop() {
		(new Audio("/sfx/box_drop.wav")).Play();
	}
	
	private void OnThrow() {
		(new Audio("/sfx/box_throw.wav")).Play();
	}
	
	@Override
	public void Update() {
		float fm = m_MaxVelocity == m_OrangeMaxVelocity ? m_OrangeFrictionModifier : 1;
		if(!m_Grabbed) {
			m_Velocity.y += ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetGravity() * Main.g_Game.GetDeltaTime();
			if(m_Velocity.x < 0)
			{
				m_Velocity.x += ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetFriction() * Main.g_Game.GetDeltaTime() * fm;
				if(m_Velocity.x > 0)
					m_Velocity.x = 0;
			}
			else if(m_Velocity.x > 0) {
				m_Velocity.x -= ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetFriction() * Main.g_Game.GetDeltaTime() * fm;
				if(m_Velocity.x < 0)
					m_Velocity.x = 0;
			}
		}
		else {
			if(m_Orange)
				m_OrangePowerVFX.Update();
			else
				m_Power.Update();
			Player p = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer();
			Vector2 playerpos = p.GetCenteredCoordinates();
			if(p.GetPlayerPower().GetActivePower() == ParticleType.ORANGE)
				m_MaxVelocity = m_OrangeMaxVelocity;
			if(playerpos.Distance(GetCenteredCoordinates()) >= m_SafetyDistance) {
				m_Grabbed = false;
				OnDrop();
			}
			else if(p.m_InAir && p.m_Velocity.y < 0){
				m_Velocity.x = 0;
				m_Velocity.y = 0;
				m_Grabbed = false;
				OnDrop();
			}
			else if(!p.GetPlayerPower().GetPowerActive()){
				m_Grabbed = false;
				OnDrop();
			}
			else {
				if(Input.WasKeyPressed(KeyEvent.VK_Q) && p.GetPlayerPower().GetActivePower() == ParticleType.ORANGE) {
					Vector2 playerToPos = m_Position.Subtract(p.GetCenteredCoordinates()).Normalize().Multiply(m_OrangePower);
					m_Velocity = playerToPos;
					m_Grabbed = false;
					OnThrow();
			
					Power pow = new Power() {

						@Override
						public Vector2 GetPowerFocalPoint() {
							//return ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer().GetGunCoordinates();
							return GetCenteredCoordinates();
						}
						
					};
					pow.GetParticleDesc().applyOffset = true;
					pow.GetParticleDesc().immortal = false;
					//pow.GetParticleDesc().changeImmortalChance = 0.997f;
					pow.GetParticleDesc().minLife = 0.1f;
					pow.GetParticleDesc().maxLife = 1.0f;
					pow.GetParticleDesc().minMaxDistance = 30; 
					pow.GetParticleDesc().maxMaxDistance = 60;
					pow.GetParticleDesc().minOffset = 0;
					pow.GetParticleDesc().minOffset = 8;
					pow.GetParticleDesc().minSpeed = 2;
					pow.GetParticleDesc().maxSpeed = 3.3f;
					pow.GetParticleDesc().minRotationSpeed = 1.4f;
					pow.GetParticleDesc().maxRotationSpeed = 2.6f;
					pow.SetType1(0.2f, 0.3f, 0.5f, 0.7f);
					
					pow.GetPowerSet().orange = 600;
					pow.SetIntervel(0);
					pow.Update();
				}
				else {
					Vector2 newpos = new Vector2(Input.GetMouseX() - Main.g_Game.GetRenderer().GetCameraOffsetX(), Input.GetMouseY() - Main.g_Game.GetRenderer().GetCameraOffsetY());
					Vector2 playerToNewPos = newpos.Subtract(playerpos);
					float len = playerToNewPos.Length();
					Vector2 actualPos = playerpos.Add(playerToNewPos.Normalize().Multiply(Math.min(m_GrabDistance, len)));
					m_Velocity = actualPos.Subtract(GetCenteredCoordinates());
					float vl = m_Velocity.Length();
					m_Velocity = m_Velocity.Normalize().Multiply(Math.min(vl, m_MaxVelocity));
				}
			}
		}
		if(m_Velocity.x < -m_MaxVelocity)
			m_Velocity.x = -m_MaxVelocity;
		if(m_Velocity.x > m_MaxVelocity)
			m_Velocity.x = m_MaxVelocity;
		if(m_Velocity.y < -m_MaxVelocity)
			m_Velocity.y = -m_MaxVelocity;
		if(m_Velocity.y > m_MaxVelocity)
			m_Velocity.y = m_MaxVelocity;
		
		boolean stuck = !CheckCanWalk(0, 0);
		//boolean stuck = false;
		//System.out.println(CheckCanWalk(0, 0));
		
		if(m_Velocity.x != 0)
		{
			if(stuck)
				m_Position.x += m_Velocity.x;
			else if(CheckCanWalk(m_Velocity.x, 0))
			{
				m_Position.x += m_Velocity.x;
			}
			else m_MaxVelocity = m_DefaultMaxVelocity;
		}
		if(m_Velocity.y != 0)
		{
			if(stuck)
				m_Position.y += m_Velocity.y;
			else if(CheckCanWalk(0, m_Velocity.y))
			{
				m_Position.y += m_Velocity.y;
			}
			else 
			{
				m_MaxVelocity = m_DefaultMaxVelocity;
				if(!m_Grabbed)
					m_Velocity.y = 0;
			}
		}
	}

	@Override
	public void Render(Renderer r) {
		r.DrawImage(m_Image, (int)m_Position.x, (int)m_Position.y, true);
	}

}

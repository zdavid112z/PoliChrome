package polichrome.game;

import java.awt.event.KeyEvent;

import polichrome.Audio;
import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.game.Power.PowerSet;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class PowerStation extends Entity {
	
	private Vector2 m_Center;
	private Vector2 m_Position;
	private Vector2 m_Size;
	private Vector2 m_Offset;
	private Power m_Power;
	private PImage m_Image;
	private int m_DrainRate = 350; 
	private float m_LerpTime = 0.3f;
	private float m_DrainRange = 100;
	private Audio m_Audio;
	
	public Vector2 GetCenter() {
		return m_Center;
	}
	
	public PowerStation(Vector2 position, PowerSet ps) {
		super("PowerStation");
		m_Audio = new Audio("/sfx/drain.wav");
		m_Position = position.Multiply(32).Add(new Vector2(0, -16));
		m_Size = new Vector2(32, 48);
		m_Offset = new Vector2(0, 6);
		m_Center = m_Position.Add(m_Size.Multiply(0.5f)).Subtract(m_Offset);
		m_Power = new Power() {
			
			@Override
			public Vector2 GetPowerFocalPoint() {
				return GetCenter();
		}};
		
		m_Power.SetIntervel(1.0f);
		m_Power.GetParticleDesc().minLife = 0.6f;
		m_Power.GetParticleDesc().maxLife = 1.2f;
		m_Power.GetParticleDesc().minMaxDistance = 10;
		m_Power.GetParticleDesc().maxMaxDistance = 20;
		m_Power.GetParticleDesc().minSpeed = 1;
		m_Power.GetParticleDesc().maxSpeed = 3;
		m_Power.GetParticleDesc().minRotationSpeed = 1;
		m_Power.GetParticleDesc().maxRotationSpeed = 2;
		m_Power.GetParticleDesc().minOffset = 0;
		m_Power.GetParticleDesc().maxOffset = 8;
		m_Power.GetPowerSet().Set(ps);
		m_Image = new PImage("/entity/powerstation.png");
	}

	public void SetDrainRange(float f) { m_DrainRange = f; }
	public void SetLerpTime(float f) { m_LerpTime = f; }
	
	@Override
	public void Update() {
		
		Vector2 c = GetCenter().Add(new Vector2(Main.g_Game.GetRenderer().GetCameraOffsetX(), Main.g_Game.GetRenderer().GetCameraOffsetY()));
		if(c.x + 32 < 0)
			return;
		if(c.x - 32 >= Main.g_Game.GetWidth())
			return;
		if(c.y + 32 < 0)
			return;
		if(c.y - 32 >= Main.g_Game.GetHeight())
			return;
		
		m_Power.Update();
		Player p =((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).GetPlayer();
		int np = (int)((float)m_DrainRate * (Main.g_Game.GetDeltaTime()));
		if(p.GetGunCoordinates().Distance(GetCenter()) <= m_DrainRange && Input.IsKeyDown(KeyEvent.VK_Q) && np > 0) {
			
			boolean d = false;
				
			int redp = Math.min(np, m_Power.GetPowerSet().red);
			m_Power.GetPowerSet().red -= redp;
			np -= redp;
			p.GetPower().GetPowerSet().red += redp;
			for(int i = 0; i < redp; i++)
			{
				Particle pa = new Particle(ParticleType.RED);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}
					
			int yellowp = Math.min(np, m_Power.GetPowerSet().yellow);
			m_Power.GetPowerSet().yellow-= yellowp;
			np -= yellowp;
			p.GetPower().GetPowerSet().yellow += yellowp;
			for(int i = 0; i < yellowp; i++)
			{
				Particle pa = new Particle(ParticleType.YELLOW);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}
					
			int bluep = Math.min(np, m_Power.GetPowerSet().blue);
			m_Power.GetPowerSet().blue -= bluep;
			np -= bluep;
			p.GetPower().GetPowerSet().blue += bluep;
			for(int i = 0; i < bluep; i++)
			{
				Particle pa = new Particle(ParticleType.BLUE);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}
				
			int greenp = Math.min(np, m_Power.GetPowerSet().green);
			m_Power.GetPowerSet().green -= greenp;
			np -= greenp;
			p.GetPower().GetPowerSet().green += greenp;
			for(int i = 0; i < greenp; i++)
			{
				Particle pa = new Particle(ParticleType.GREEN);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}
					
			int orangep = Math.min(np, m_Power.GetPowerSet().orange);
			m_Power.GetPowerSet().orange -= orangep;
			np -= orangep;
			p.GetPower().GetPowerSet().orange += orangep;
			for(int i = 0; i < orangep; i++)
			{
				Particle pa = new Particle(ParticleType.ORANGE);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}
					
			int purplep = Math.min(np, m_Power.GetPowerSet().purple);
			m_Power.GetPowerSet().purple -= purplep;
			np -= purplep;
			p.GetPower().GetPowerSet().purple += purplep;
			for(int i = 0; i < purplep; i++)
			{
				Particle pa = new Particle(ParticleType.PURPLE);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}
					
			int whitep = Math.min(np, m_Power.GetPowerSet().white);
			m_Power.GetPowerSet().white -= whitep;
			np -= whitep;
			p.GetPower().GetPowerSet().white += whitep;
			for(int i = 0; i < whitep; i++)
			{
				Particle pa = new Particle(ParticleType.WHITE);
				m_Power.GetParticleDesc().Set(pa);
				pa.SetFocus(GetCenter());
				pa.SetLife(m_LerpTime);
				pa.SetImmortal(false);
				pa.LerpFocusPoint(p.GetGunCoordinates(), m_LerpTime);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(pa);
				d = true;
			}	
			if(d) {
				if(!m_Audio.IsPlaying())
					m_Audio.Loop();
			} else m_Audio.Stop();
		} else m_Audio.Stop();
	}

	@Override
	public void Render(Renderer r) {
		r.DrawImage(m_Image, (int)m_Position.x, (int)m_Position.y, true);
	}

	
	
}

package polichrome.game;

import java.awt.event.KeyEvent;

import polichrome.Audio;
import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;

public class PlayerPower {

	private float m_RPTimer = 0;
	private int m_RPCost = 100;
	private float m_RPDuration = 5.0f;
	private float m_RPSpeed = 6;
	private float m_RPJumpBoost = 8.7f;
	
	private int m_BPCost = 100;
	private float m_BPDuration = 8.0f;
	private float m_BPTimer = 0;
	
	private int m_YPCost = 100;
	private float m_YPDuration = 6.0f;
	private float m_YPTimer = 0;
	
	private int m_PPYCost = 50;
	private int m_PPCost = 100;
	private float m_PPDuration = m_YPDuration;
	private float m_PPTimer = 0;
	
	private int m_OPBCost = 50;
	private int m_OPCost = 100;
	private float m_OPDuration = m_BPDuration;
	private float m_OPTimer = 0;
	
	private int m_GPRCost = 50;
	private int m_GPCost = 100;
	private float m_GPDuration = m_RPDuration;
	private float m_GPTimer = 0;
	
	private Player m_Player;
	private Power m_VFX;
	private Power m_ForceFieldVFX;
	private boolean m_PowerActive = false;
	
	public PlayerPower(Player player) {
		m_Player = player;
		m_VFX = new Power() {
			@Override
			public Vector2 GetPowerFocalPoint() {
				return m_Player.GetGunCoordinates();
			}
		};
		
		m_VFX.GetParticleDesc().changeImmortalChance = 0.985f;
		m_VFX.GetParticleDesc().minLife = 0.7f;
		m_VFX.GetParticleDesc().maxLife = 1.45f;
		m_VFX.GetParticleDesc().minMaxDistance = 20;
		m_VFX.GetParticleDesc().maxMaxDistance = 55;
		m_VFX.GetParticleDesc().minSpeed = 1.4f;
		m_VFX.GetParticleDesc().maxSpeed = 2.4f;
		m_VFX.GetParticleDesc().minRotationSpeed = 1.2f;
		m_VFX.GetParticleDesc().maxRotationSpeed = 2.0f;
		m_VFX.GetParticleDesc().minOffset = 0;
		m_VFX.GetParticleDesc().maxOffset = 20;
		m_VFX.SetIntervel(0.2f);
		
		m_ForceFieldVFX = new Power() {
			@Override
			public Vector2 GetPowerFocalPoint() {
				return m_Player.GetGunCoordinates();
			}
		};
		m_ForceFieldVFX.GetParticleDesc().changeImmortalChance = 0.9972f;
		m_ForceFieldVFX.GetParticleDesc().minLife = 0.07f;
		m_ForceFieldVFX.GetParticleDesc().maxLife = 0.15f;
		m_ForceFieldVFX.GetParticleDesc().minMaxDistance = 56;
		m_ForceFieldVFX.GetParticleDesc().maxMaxDistance = 70;
		m_ForceFieldVFX.GetParticleDesc().minSpeed = 0;
		m_ForceFieldVFX.GetParticleDesc().maxSpeed = 0;
		m_ForceFieldVFX.GetParticleDesc().minRotationSpeed = 1.2f;
		m_ForceFieldVFX.GetParticleDesc().maxRotationSpeed = 2.0f;
		m_ForceFieldVFX.GetParticleDesc().minOffset = 0;
		m_ForceFieldVFX.GetParticleDesc().maxOffset = 0;
		m_ForceFieldVFX.SetIntervel(0.1f);
		//m_ForceFieldVFX.SetType1(0.04f, 0.1f, 0.2f, 0.3f);
	}
	
	public void Release() {
		m_PowerActive = false;
		m_RPTimer = 0;
		m_YPTimer = 0;
		m_BPTimer = 0;
		m_PPTimer = 0;
		m_OPTimer = 0;
		m_GPTimer = 0;
		m_Player.m_Size.x = 36;
		m_Player.m_ColBoxOffset.x = 114 / 2 - m_Player.m_Size.x / 2;
		m_Player.m_Size.y = 88;
		m_Player.m_ColBoxOffset.y = 0;
		m_Player.m_Small = false;
	}
	
	public ParticleType GetActivePower() {
		if(!m_PowerActive)
			return ParticleType.OTHER;
		if(m_RPTimer > 0)
			return ParticleType.RED;
		if(m_BPTimer > 0)
			return ParticleType.BLUE;
		if(m_YPTimer > 0)
			return ParticleType.YELLOW;
		if(m_PPTimer > 0)
			return ParticleType.PURPLE;
		if(m_OPTimer > 0)
			return ParticleType.ORANGE;
		if(m_GPTimer > 0)
			return ParticleType.GREEN;
		return ParticleType.OTHER;
	}
	
	public float GetSpeed(float is) {
		if(m_RPTimer > 0 || m_GPTimer > 0)
			return m_RPSpeed;
		return is;
	}
	
	private void OnPowerActivate() {
		(new Audio("/sfx/power_use.wav")).Play();
	}
	
	private void OnSPowerActivate() {
		(new Audio("/sfx/power_suse.wav")).Play();
	}
	
	private void OnRunOut() {
		(new Audio("/sfx/power_run_out.wav")).Play();
	}
	
	private void OnNotEnough() {
		(new Audio("/sfx/power_not_enough.wav")).Play();
	}

	private void OnBoxPickup() {
		(new Audio("/sfx/box_pickup.wav")).Play();
	}
	
	private void OnBoxDrop() {
		(new Audio("/sfx/box_drop.wav")).Play();
	}
	
	public float GetJump(float is) {
		if(m_RPTimer > 0 || m_GPTimer > 0)
			return m_RPJumpBoost;
		return is;
	}
	
	public boolean GetPowerActive() {
		return m_PowerActive;
	}
	
	public void Update(ParticleType selected) {
		m_PowerActive = false;
		
		if(m_YPTimer > 0)
		{
			m_YPTimer -= Main.g_Game.GetDeltaTime();
			if(m_YPTimer <= 0)
				OnRunOut();
			m_PowerActive = true;
		}
		else {
			m_ForceFieldVFX.GetPowerSet().yellow = 0;
		}
		
		if(m_PPTimer > 0)
		{
			m_PPTimer -= Main.g_Game.GetDeltaTime();
			if(m_PPTimer <= 0)
				OnRunOut();
			m_PowerActive = true;
		}
		else {
			m_ForceFieldVFX.GetPowerSet().purple = 0;
		}
		
		m_VFX.Update();
		m_ForceFieldVFX.Update();
		
		if(m_RPTimer > 0)
		{
			m_RPTimer -= Main.g_Game.GetDeltaTime();
			if(m_RPTimer <= 0)
				OnRunOut();
			m_PowerActive = true;
		}
		else {
			m_VFX.GetPowerSet().red = 0;
		}
		
		if(m_GPTimer > 0)
		{
			m_GPTimer -= Main.g_Game.GetDeltaTime();
			if(m_GPTimer <= 0)
			{
				m_Player.m_Size.x = 36;
				m_Player.m_ColBoxOffset.x = 114 / 2 - m_Player.m_Size.x / 2;
				m_Player.m_Size.y = 88;
				m_Player.m_ColBoxOffset.y = 0;
				
				if(m_Player.IsStuck()) {
					m_Player.m_Size.x = 36 / 2;
					m_Player.m_ColBoxOffset.x = 114 / 2 - m_Player.m_Size.x / 2;
					m_Player.m_Size.y = 88 / 2;
					m_Player.m_ColBoxOffset.y = 88 / 2;
					m_GPTimer = 0.1f;
				}
				else {
					OnRunOut();
					m_Player.m_Small = false;
				}
			}
			m_PowerActive = true;
		}
		else {
			m_VFX.GetPowerSet().green = 0;
		}
		
		if(selected == ParticleType.RED && !m_PowerActive) {
			if(Input.WasKeyPressed(KeyEvent.VK_E))
			{
				if(m_Player.GetPower().GetPowerSet().red >= m_RPCost) {
					OnPowerActivate();
					m_Player.GetPower().GetPowerSet().red -= m_RPCost;
					m_RPTimer = m_RPDuration;
					m_VFX.GetPowerSet().red = 300;
					m_PowerActive = true;
				} else OnNotEnough();
			}
		}
		
		GameLevel l = (GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel();
		
		if(m_BPTimer > 0) {
			if(Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED || Input.WasKeyPressed(KeyEvent.VK_E)) {
				Box g = null;
				boolean op = false;
				for(int i = 0; i < l.GetNumEntities(); i++)
				{
					if(l.GetEntityAt(i).GetName().equals("Box")) {
						Box b = (Box)l.GetEntityAt(i);
						if(b.GetGrabbed())
							g = b;
						Vector2 screenpos = b.GetCenteredCoordinates().Add(new Vector2(Main.g_Game.GetRenderer().GetCameraOffsetX(), Main.g_Game.GetRenderer().GetCameraOffsetY()));
						if(Math.abs(screenpos.x - Input.GetMouseX()) < b.GetSize().x / 2 - 4)
							if(Math.abs(screenpos.y - Input.GetMouseY()) < b.GetSize().y / 2 - 4)
							{
								op = true;
								if(b.GetGrabbed())
									OnBoxDrop();
								else OnBoxPickup();
								b.SetGrabbed(!b.GetGrabbed());
								b.SetOrange(false);
							}
					}
				}
				if(!op && g != null)
					g.SetGrabbed(false);
			}
			m_BPTimer -= Main.g_Game.GetDeltaTime();
			if(m_BPTimer <= 0)
				OnRunOut();
			m_PowerActive = true;
		}
		else {
			m_VFX.GetPowerSet().blue = 0;
		}
		
		if(selected == ParticleType.BLUE && !m_PowerActive) {
			if(Input.WasKeyPressed(KeyEvent.VK_E)) {
				if(m_Player.GetPower().GetPowerSet().blue >= m_BPCost) {
					OnPowerActivate();
					m_Player.GetPower().GetPowerSet().blue -= m_BPCost;
					m_BPTimer = m_BPDuration;
					m_VFX.GetPowerSet().blue = 100;
					m_PowerActive = true;
				} else OnNotEnough();
			}
		}
		
		if(m_OPTimer > 0) {
			if(Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED || Input.WasKeyPressed(KeyEvent.VK_E)) {
				Box g = null;
				boolean op = false;
				for(int i = 0; i < l.GetNumEntities(); i++)
				{
					if(l.GetEntityAt(i).GetName().equals("Box")) {
						Box b = (Box)l.GetEntityAt(i);
						if(b.GetGrabbed())
							g = b;
						Vector2 screenpos = b.GetCenteredCoordinates().Add(new Vector2(Main.g_Game.GetRenderer().GetCameraOffsetX(), Main.g_Game.GetRenderer().GetCameraOffsetY()));
						if(Math.abs(screenpos.x - Input.GetMouseX()) < b.GetSize().x / 2 - 4)
							if(Math.abs(screenpos.y - Input.GetMouseY()) < b.GetSize().y / 2 - 4)
							{
								op = true;
								if(b.GetGrabbed())
									OnBoxDrop();
								else OnBoxPickup();
								b.SetGrabbed(!b.GetGrabbed());
								b.SetOrange(true);
							}
					}
				}
				if(!op && g != null)
					g.SetGrabbed(false);
			}
			m_OPTimer -= Main.g_Game.GetDeltaTime();
			if(m_OPTimer <= 0)
				OnRunOut();
			m_PowerActive = true;
		}
		else {
			m_VFX.GetPowerSet().orange = 0;
		}
		
		if(selected == ParticleType.ORANGE && !m_PowerActive) {
			if(Input.WasKeyPressed(KeyEvent.VK_E)) {
				if(m_Player.GetPower().GetPowerSet().blue >= m_OPBCost && m_Player.GetPower().GetPowerSet().orange >= m_OPCost) {
					OnSPowerActivate();
					m_Player.GetPower().GetPowerSet().blue -= m_OPBCost;
					m_Player.GetPower().GetPowerSet().orange -= m_OPCost;
					m_OPTimer = m_OPDuration;
					m_VFX.GetPowerSet().orange = 200;
					m_PowerActive = true;
				} else OnNotEnough();
			}
		}
		
		if(selected == ParticleType.GREEN && !m_PowerActive) {
			if(Input.WasKeyPressed(KeyEvent.VK_E)) {
				if(m_Player.GetPower().GetPowerSet().green >= m_GPCost && m_Player.GetPower().GetPowerSet().red >= m_GPRCost) {
					OnSPowerActivate();
					m_Player.GetPower().GetPowerSet().green -= m_GPCost;
					m_Player.GetPower().GetPowerSet().red -= m_GPRCost;
					m_GPTimer = m_GPDuration;
					m_VFX.GetPowerSet().green = 400;
					m_PowerActive = true;
					
					m_Player.m_Small = true;
					m_Player.m_Size.x = 36 / 2;
					m_Player.m_ColBoxOffset.x = 114 / 2 - m_Player.m_Size.x / 2;
					m_Player.m_Size.y = 88 / 2;
					m_Player.m_ColBoxOffset.y = 88 / 2;
				} else OnNotEnough();
			}
		}
		
		if(selected == ParticleType.YELLOW && !m_PowerActive) {
			if(Input.WasKeyPressed(KeyEvent.VK_E)) {
				if(m_Player.GetPower().GetPowerSet().yellow >= m_YPCost) {
					OnPowerActivate();
					m_Player.GetPower().GetPowerSet().yellow -= m_YPCost;
					m_YPTimer = m_YPDuration;
					m_ForceFieldVFX.GetPowerSet().yellow = 500;
					m_PowerActive = true;
				} else OnNotEnough();
			}
		}
		
		if(selected == ParticleType.PURPLE && !m_PowerActive) {
			if(Input.WasKeyPressed(KeyEvent.VK_E)) {
				if(m_Player.GetPower().GetPowerSet().purple >= m_PPCost && m_Player.GetPower().GetPowerSet().yellow >= m_PPYCost) {
					OnSPowerActivate();
					m_Player.GetPower().GetPowerSet().purple -= m_PPCost;
					m_Player.GetPower().GetPowerSet().yellow -= m_PPYCost;
					m_PPTimer = m_PPDuration;
					m_ForceFieldVFX.GetPowerSet().purple = 750;
					m_PowerActive = true;
				} else OnNotEnough();
			}
		}
	}
	
}

package polichrome.game;

import polichrome.Main;
import polichrome.Vector2;
import polichrome.graphics.Renderer;

public class Particle extends Entity {

	public static enum ParticleType {
		RED,
		GREEN,
		BLUE,
		PURPLE,
		YELLOW,
		ORANGE,
		WHITE,
		OTHER
	}
	
	public static String GetParticleName(ParticleType type) {
		switch(type) {
		case BLUE:
			return "ParticleBlue";
		case GREEN:
			return "ParticleGreen";
		case OTHER:
			return "ParticleOther";
		case PURPLE:
			return "ParticlePurple";
		case RED:
			return "ParticleRed";
		case WHITE:
			return "ParticleWhite";
		case YELLOW:
			return "ParticleYellow";
		case ORANGE:
			return "ParticleOrange";
		default:
			return "ParticleUnknown";
		}
	}
	
	public static int GetParticleColor(ParticleType type) {
		switch(type) {
		case BLUE:
			return 0x6666dd;
		case GREEN:
			return 0x23dd23;
		case OTHER:
			return 0;
		case PURPLE:
			return 0xbb33bb;
		case RED:
			return 0xff1111;
		case WHITE:
			return Main.g_Game.GetRandom().nextInt(0xffffff);
		case YELLOW:
			return 0xeeee33;
		case ORANGE:
			return 0xff8833;
		default:
			return 0;
		}		
	}
	
	private Vector2 m_Position;
	private Vector2 m_Focus;
	private float m_Life = 150;
	private boolean m_Immortal = false;
	private float m_Timer = 0;
	private float m_Distance;
	private float m_Speed;
	private float m_MaxDistance;
	private float m_Angle;
	private float m_RotationSpeed;
	private float m_OffsetDistance;
	private Vector2 m_Offset;
	private int m_Thickness;
	private int m_Color;
	private boolean m_ApplyOffset = true;
	private ParticleType m_Type;
	
	private boolean m_FocusLerping = false;
	private Vector2 m_FocusStart;
	private Vector2 m_FocusEnd;
	private float m_FocusTimer;
	private float m_FocusLerpDuration;
	
	private boolean m_MaxDistanceLerping = false;
	private float m_MaxDistanceStart;
	private float m_MaxDistanceEnd;
	private float m_MaxDistanceTimer;
	private float m_MaxDistanceLerpDuration;
	
	private int OffsetColor(int color, int maxOffset) {
		int b = (color) & 0xff;
		int g = (color >> 8) & 0xff;
		int r = (color >> 16) & 0xff;
		b += Main.g_Game.GetRandom().nextInt(maxOffset * 2) - maxOffset;
		b = Math.max(0, Math.min(b, 0xff));
		g += Main.g_Game.GetRandom().nextInt(maxOffset * 2) - maxOffset;
		g = Math.max(0, Math.min(g, 0xff));
		r += Main.g_Game.GetRandom().nextInt(maxOffset * 2) - maxOffset;
		r = Math.max(0, Math.min(r, 0xff));
		return b | (g << 8) | (r << 16);
	}
	
	public Particle(ParticleType type) {
		super(GetParticleName(type));
		m_Type = type;
		m_Position = new Vector2();
		m_Focus = new Vector2();
		m_Angle = Main.g_Game.GetRandom().nextFloat() * (float)Math.PI * 2.0f;
		m_Timer = (float)Math.PI / 2.0f;
		m_Color = OffsetColor(GetParticleColor(type), 0x30);
	}
	
	private void CalcOffset() {
		float angle = Main.g_Game.GetRandom().nextFloat()  * (float)Math.PI * 2.0f;
		m_Offset = new Vector2((float)Math.cos(angle) * m_OffsetDistance, (float)Math.sin(angle) * m_OffsetDistance);
	}
	
	public void SetApplyOffset(boolean b) { m_ApplyOffset = b; }
	public void SetImmortal(boolean b) { m_Immortal = b; }
	public void SetLife(float f) { m_Life = f; }
	public void SetFocus(Mob b) { m_Focus = ((Mob)b).GetCenteredCoordinates();}
	public void SetFocus(Vector2 f) { m_Focus = f;}
	public void SetMaxDistance(float f) { m_MaxDistance = f; }
	public void SetRotationSpeed(float f) { m_RotationSpeed = f; }
	public void SetSpeed(float f) { m_Speed = f; if(m_Speed == 0) m_Timer = 0; }
	public void SetOffsetDistance(float f) { m_OffsetDistance = f; CalcOffset(); }
	public void SetThickness(int i) { m_Thickness = i; }
	
	public float GetLife() { return m_Life; }
	public ParticleType GetType() { return m_Type; }
	public Vector2 GetFocusPoint() { return m_Focus; }
	public boolean GetFocusPointLerping() { return m_FocusLerping; }
	
	public void LerpMaxDistance(float end, float time) {
		m_MaxDistanceLerping = true;
		m_MaxDistanceStart = m_MaxDistance;
		m_MaxDistanceEnd = end;
		m_MaxDistanceLerpDuration = time;
		m_MaxDistanceTimer = 0;
	}
	
	public void LerpFocusPoint(Vector2 end, float time)
	{
		m_FocusLerping = true;
		m_FocusStart = m_Focus;
		m_FocusEnd = end;
		m_FocusLerpDuration = time;
		m_FocusTimer = 0;
	}
	
	@Override
	public void Update() {
		if(m_FocusLerping) {
			m_FocusTimer += Main.g_Game.GetDeltaTime();
			if(m_FocusTimer >= m_FocusLerpDuration) {
				m_FocusLerping = false;
				m_Focus = m_FocusEnd;
			}
			else
			{
				m_Focus = m_FocusStart.Lerp(m_FocusEnd, m_FocusTimer / m_FocusLerpDuration);
			}
		}
		
		if(m_MaxDistanceLerping) {
			m_MaxDistanceTimer += Main.g_Game.GetDeltaTime();
			if(m_MaxDistanceTimer >= m_MaxDistanceLerpDuration) {
				m_MaxDistanceLerping = false;
				m_MaxDistance = m_MaxDistanceEnd;
			}
			else
			{
				m_MaxDistance = m_MaxDistanceStart * (1.0f - m_MaxDistanceTimer / m_MaxDistanceLerpDuration) + 
						m_MaxDistanceEnd * (m_MaxDistanceTimer / m_MaxDistanceLerpDuration);
			}
		}
		
		if(!m_Immortal) {
			m_Life -= Main.g_Game.GetDeltaTime();
			if(m_Life <= 0)
			{
				m_FocusLerping = false;
				Die();
			}
		}
		m_Angle += m_RotationSpeed * Main.g_Game.GetDeltaTime();
		m_Timer += m_Speed * Main.g_Game.GetDeltaTime();
		m_Distance = (float)Math.cos(m_Timer) * m_MaxDistance;
		//m_MaxDistance -= m_Speed * Main.g_Game.GetDeltaTime() * 3.2f;
		float px = (float)Math.cos(m_Angle) * m_Distance;
		float py = (float)Math.sin(m_Angle) * m_Distance;
		m_Position.x = px + m_Focus.x + m_Offset.x;
		m_Position.y = py + m_Focus.y + m_Offset.y;
		//if(m_FocusType == FocusType.MOB)
		//	m_Focus = ((Mob)m_ParentMob).GetCenteredCoordinates();
	}
	
	@Override
	public void Render(Renderer r) {
		
		if(m_Thickness >= 1)
			r.DrawPixel(m_Color | 0xff000000, (int)m_Position.x, (int)m_Position.y, m_ApplyOffset);
		if(m_Thickness >= 2)
		{
			r.DrawPixel(m_Color | 0x99000000, (int)m_Position.x, (int)m_Position.y - 1, m_ApplyOffset);
			r.DrawPixel(m_Color | 0x99000000, (int)m_Position.x, (int)m_Position.y + 1, m_ApplyOffset);
			r.DrawPixel(m_Color | 0x99000000, (int)m_Position.x - 1, (int)m_Position.y, m_ApplyOffset);
			r.DrawPixel(m_Color | 0x99000000, (int)m_Position.x + 1, (int)m_Position.y, m_ApplyOffset);	
		}
		if(m_Thickness >= 3) {
			r.DrawPixel(m_Color | 0x55000000, (int)m_Position.x - 1, (int)m_Position.y - 1, m_ApplyOffset);
			r.DrawPixel(m_Color | 0x55000000, (int)m_Position.x - 1, (int)m_Position.y + 1, m_ApplyOffset);
			r.DrawPixel(m_Color | 0x55000000, (int)m_Position.x + 1, (int)m_Position.y - 1, m_ApplyOffset);
			r.DrawPixel(m_Color | 0x55000000, (int)m_Position.x + 1, (int)m_Position.y + 1, m_ApplyOffset);
		}
		
	}

}

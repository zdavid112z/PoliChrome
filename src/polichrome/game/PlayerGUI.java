package polichrome.game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import polichrome.Audio;
import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.game.Power.ParticleDesc;
import polichrome.game.Power.PowerSet;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class PlayerGUI {

	public static class PlayerPowerElement {
		public static final Vector2 POSITION = new Vector2(16, Main.g_Game.GetHeight() - 64);
		public static final Vector2 SIZE = new Vector2(48, 48);
		public static final int OFFSET = 64;
		public List<Particle> particles = new ArrayList<Particle>();
		public ParticleDesc desc;
		public ParticleType type;
		public int numParticles;
		public PowerSet powerSet;
		
		public PlayerPowerElement(ParticleDesc d, ParticleType t, PowerSet p) {
			desc = d;
			type = t;
			powerSet = p;
		}
		
		public void Update() {
			switch(type) {
			case BLUE:
				numParticles = powerSet.blue;
				break;
			case GREEN:
				numParticles = powerSet.green;
				break;
			case ORANGE:
				numParticles = powerSet.orange;
				break;
			case OTHER:
				numParticles = 0;
				break;
			case PURPLE:
				numParticles = powerSet.purple;
				break;
			case RED:
				numParticles = powerSet.red;
				break;
			case WHITE:
				numParticles = powerSet.white;
				break;
			case YELLOW:
				numParticles = powerSet.yellow;
				break;
			default:
				break;
			}
			SetSize(numParticles);
			for(int i = 0; i < particles.size(); i++)
			{
				particles.get(i).Update();
			}
		}
		
		private int GetIndex() {
			int count = 0;
			if(type == ParticleType.YELLOW)
			{
				if(powerSet.red > 0)
					count++;
			}
			else if(type == ParticleType.BLUE) 
			{
				if(powerSet.red > 0)
					count++;
				if(powerSet.yellow > 0)
					count++;
			}
			else if(type == ParticleType.GREEN) 
			{
				if(powerSet.red > 0)
					count++;
				if(powerSet.yellow > 0)
					count++;
				if(powerSet.blue > 0)
					count++;
			}
			else if(type == ParticleType.ORANGE) 
			{
				if(powerSet.red > 0)
					count++;
				if(powerSet.yellow > 0)
					count++;
				if(powerSet.blue > 0)
					count++;
				if(powerSet.green > 0)
					count++;
			}
			else if(type == ParticleType.PURPLE) 
			{
				if(powerSet.red > 0)
					count++;
				if(powerSet.yellow > 0)
					count++;
				if(powerSet.blue > 0)
					count++;
				if(powerSet.green > 0)
					count++;
				if(powerSet.orange> 0)
					count++;
			}
			else if(type == ParticleType.WHITE) 
			{
				if(powerSet.red > 0)
					count++;
				if(powerSet.yellow > 0)
					count++;
				if(powerSet.blue > 0)
					count++;
				if(powerSet.green > 0)
					count++;
				if(powerSet.orange > 0)
					count++;
				if(powerSet.purple > 0)
					count++;
			}
			return count;
		}
		
		private Vector2 GetCenter() {
			int count = GetIndex();
			return POSITION.Add(new Vector2(OFFSET * count, 0)).Add(SIZE.Multiply(0.5f));
		}

		private Vector2 GetPosition() {
			return GetCenter().Subtract(SIZE.Multiply(0.5f));
		}
		
		private void SetSize(int s) {
			if(particles.size() == s)
				return;
			if(particles.size() < s) {
				for(int i = 0; i < s - particles.size(); i++) {
					Particle p = new Particle(type);
					p.SetFocus(SIZE.Multiply(0.5f));
					desc.Set(p);
					particles.add(p);
				}
			}
			else {
				for(int i = 0; i < particles.size() - s; i++) {
					particles.remove(i);
				}
			}
		}
		
		public void Render(Renderer r, PImage circle, PImage circlef) {
			int ox = r.GetCameraOffsetX();
			int oy = r.GetCameraOffsetY();
			if(numParticles <= 0)
				return;
			Vector2 c = GetPosition();
			
			int nox = (int)c.x;
			int noy = (int)c.y;
			r.SetCameraOffsetX(nox);
			r.SetCameraOffsetY(noy);
			
			if(circlef != null)
			{
				int col = Particle.GetParticleColor(type) | 0x8f000000;
				if(type == ParticleType.WHITE)
					col = 0x44ff00ff;
				r.DrawImageMasked(circlef, 2, 2, col, true);
			}
			r.DrawImageInvert(circle, 0, 0, true);
			for(int i = 0; i < particles.size(); i++)
			{
				particles.get(i).Render(r);
			}
			
			r.SetCameraOffsetX(ox);
			r.SetCameraOffsetY(oy);
		}
	}
	
	private List<PlayerPowerElement> m_Elements = new ArrayList<PlayerPowerElement>();
	private Player m_Player;
	private PImage m_Circle;
	private PImage m_Circlef;
	private PowerSet m_PowerSet;
	private PlayerPowerElement m_Selected = null;
	private boolean[] m_Keys = new boolean[10];
	
	public PlayerGUI(Player p) {
		m_Player = p;
		m_Circle = new PImage("/entity/circle.png");
		m_Circlef = new PImage("/entity/circlef.png");
		m_PowerSet = new PowerSet();
		ParticleDesc pd = new ParticleDesc();
		
		pd.minLife = 10;
		pd.maxLife = 30;
		pd.minMaxDistance = 16;
		pd.maxMaxDistance = 20;
		pd.minSpeed = 1;
		pd.maxSpeed = 3;
		pd.minRotationSpeed = 1;
		pd.maxRotationSpeed = 2;
		pd.minOffset = 0;
		pd.maxOffset = 2;
		pd.applyOffset = true;
		pd.immortal = true;
		
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.RED, m_PowerSet));
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.YELLOW, m_PowerSet));
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.BLUE, m_PowerSet));
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.GREEN, m_PowerSet));
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.ORANGE, m_PowerSet));
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.PURPLE, m_PowerSet));
		m_Elements.add(new PlayerPowerElement(pd, ParticleType.WHITE, m_PowerSet));
	}
	
	public PlayerPowerElement GetSelected() { return m_Selected; }
	
	public PlayerPowerElement UpdateSelected() {
		for(int i = 0; i < 10; i++) {
			m_Keys[i] = Input.WasKeyPressed(KeyEvent.VK_1 + i);
		}
		for(int i = 0; i < m_Elements.size(); i++) {
			if(m_Keys[m_Elements.get(i).GetIndex()] && m_Elements.get(i).numParticles > 0 && m_Selected != m_Elements.get(i))
			{
				(new Audio("/sfx/power_select.wav")).Play();
				m_Selected = m_Elements.get(i);
				break;
			}
		}
		return m_Selected;
	}
	
	public void Update() {
		m_Player.GetPower().GetPowerSet().Update();
		m_PowerSet.Set(m_Player.GetPower().GetPowerSet());
		for(int i = 0; i < m_Elements.size(); i++)
			m_Elements.get(i).Update();
	}
	
	public void Render(Renderer r) {
		for(int i = 0; i < m_Elements.size(); i++) {
			if(m_Selected != null)
			{
				if(m_Elements.get(i) == m_Selected)
					m_Elements.get(i).Render(r, m_Circle, m_Circlef);
				else m_Elements.get(i).Render(r, m_Circle, null);
			}
			else m_Elements.get(i).Render(r, m_Circle, null); 
		}
	}
	
}

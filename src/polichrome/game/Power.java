package polichrome.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;

public abstract class Power  {
	
	public static class PowerSet {
		public int red, yellow, blue;
		public int green, orange, purple;
		public int white;
		
		public PowerSet() { }
		
		public PowerSet(int r, int y, int b, int g, int o, int p, int w) {
			red = r;
			yellow = y;
			blue = b;
			green = g;
			orange = o;
			purple = p;
			white = w;
		}
		
		public PowerSet Clone() {
			PowerSet p = new PowerSet();
			p.red = red;
			p.yellow = yellow;
			p.blue = blue;
			p.green = green;
			p.orange = orange;
			p.purple = purple;
			p.white = white;
			return p;
		}
		
		public void SetZero() {
			red = 0;
			yellow = 0;
			blue = 0;
			green = 0;
			orange = 0;
			purple = 0;
			white = 0;
		}
		
		public void Add(PowerSet ps) {
			red += ps.red;
			yellow += ps.yellow;
			blue += ps.blue;
			green += ps.green;
			orange += ps.orange;
			purple += ps.purple;
			white += ps.white;
			Update();
		}
		
		public void Set(PowerSet ps) {
			red = ps.red;
			yellow = ps.yellow;
			blue = ps.blue;
			green = ps.green;
			orange = ps.orange;
			purple = ps.purple;
			white = ps.white;
			Update();
		}
		
		public void Update() {
			int aux;
			
			aux = Math.min(red, yellow);
			red -= aux;
			yellow -= aux;
			orange += aux * 2;
			
			aux = Math.min(red, blue);
			red -= aux;
			blue -= aux;
			purple += aux * 2;
			
			aux = Math.min(blue, yellow);
			blue -= aux;
			yellow -= aux;
			green += aux * 2;
			
			/*aux = Math.min(red, green / 2);
			red -= aux;
			green -= aux * 2;
			white += aux * 3;
			
			aux = Math.min(yellow, purple / 2);
			yellow -= aux;
			purple -= aux * 2;
			white += aux * 3;
			
			aux = Math.min(blue, orange / 2);
			blue -= aux;
			orange -= aux * 2;
			white += aux * 3;
			
			aux = Math.min(green, Math.min(purple, orange));
			green -= aux;
			purple -= aux;
			orange -= aux;
			white += aux * 3;*/
		}
		
		public int GetNumParticles() {
			return red + green + yellow + blue + orange + purple + white;
		}
	}
	
	public static class ParticleDesc {
		
		public float minLife, maxLife;
		public float minMaxDistance, maxMaxDistance;
		public float minRotationSpeed, maxRotationSpeed;
		public float minSpeed, maxSpeed;
		public float minOffset, maxOffset;
		public boolean immortal = false, applyOffset = true;
		public float changeImmortalChance = 1.0f;
		
		public void Set(Particle p) {
			Random r = Main.g_Game.GetRandom();
			p.SetLife(r.nextFloat() * (maxLife - minLife) + minLife);
			p.SetMaxDistance(r.nextFloat() * (maxMaxDistance - minMaxDistance) + minMaxDistance);
			p.SetRotationSpeed(r.nextFloat() * (maxRotationSpeed - minRotationSpeed) + minRotationSpeed);
			p.SetSpeed(r.nextFloat() * (maxSpeed - minSpeed) + minSpeed);
			p.SetOffsetDistance(r.nextFloat() * (maxOffset - minOffset) + minOffset);
			p.SetThickness(r.nextInt(3) + 1);
			float f = r.nextFloat();
			if(f > changeImmortalChance)
				p.SetImmortal(!immortal);
			else p.SetImmortal(immortal);
			p.SetApplyOffset(applyOffset);
		}
	}
	
	private static class PowerParticle
	{
		private float timer = 0;
		public float updateFocusPointTime;
		public float lerpTime;
		public Particle particle;
		
		public PowerParticle(Particle p, float minTime, float maxTime, float minLt, float maxLt) {
			particle = p;
			updateFocusPointTime = Main.g_Game.GetRandom().nextFloat() * (maxTime - minTime) + minTime;
			lerpTime = Main.g_Game.GetRandom().nextFloat() * (maxLt - minLt) + minLt;
		}
		
		public void Update(Vector2 fp) {
			timer += Main.g_Game.GetDeltaTime();
			if(timer >= updateFocusPointTime) {
				timer -= updateFocusPointTime;
				particle.LerpFocusPoint(fp, lerpTime);
			}
		}
	};
	
	private void SetSize(ParticleType t, int s, int ns) {
		if(ns == s)
			return;
		if(ns < s) {
			for(int i = 0; i < s - ns; i++) {
				Particle p = new Particle(t);
				m_ParticleDesc.Set(p);
				p.SetFocus(GetPowerFocalPoint());
				m_Particles.add(new PowerParticle(p, m_MinTimeT1, m_MaxTimeT1, m_MinLerpT1, m_MaxLerpT1));
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
			}
		}
		else {
			for(int i = 0; i < ns - s; i++) {
				if(m_Particles.get(i).particle.GetType() == t)
				{
					m_Particles.get(i).particle.Die();					
					m_Particles.remove(i);
				}
			}
		}
	}
	
	protected List<PowerParticle> m_Particles = new ArrayList<PowerParticle>();
	protected PowerSet m_PowerSet;
	protected PowerSet m_ClonePowerSet;
	protected ParticleDesc m_ParticleDesc;
	protected float m_Interval = 0.7f;
	protected float m_Timer = m_Interval;
	protected int m_NumParticles;
	protected int m_Type = 0;
	
	protected float m_MinTimeT1, m_MaxTimeT1, m_MinLerpT1, m_MaxLerpT1;
	
	public void SetIntervel(float f) { m_Interval = f; m_Timer = f; }
	public PowerSet GetPowerSet() { return m_PowerSet; }
	public ParticleDesc GetParticleDesc() { return m_ParticleDesc; }
	public void SetType1(float mint, float maxt, float minl, float maxl)
	{
		m_Type = 1;
		m_MinTimeT1 = mint;
		m_MaxTimeT1 = maxt;
		m_MinLerpT1 = minl;
		m_MaxLerpT1 = maxl;
	}
	
	public Power() {
		m_PowerSet = new PowerSet();
		m_ClonePowerSet = new PowerSet();
		m_ParticleDesc = new ParticleDesc();
	}
	
	public abstract Vector2 GetPowerFocalPoint();
	
	public void Update() {
		if(m_Type == 0) {
			m_Timer += Main.g_Game.GetDeltaTime();
			
			if(m_Timer >= m_Interval) {
				m_Timer -= m_Interval;
				m_ClonePowerSet = m_PowerSet.Clone();
				m_NumParticles = m_ClonePowerSet.GetNumParticles();
			}
			int np = (int)((float)m_NumParticles * (Main.g_Game.GetDeltaTime() / m_Interval));
			
			if(np > 0)
			{ 
				int redp = Math.min(np, m_ClonePowerSet.red);
				m_ClonePowerSet.red -= redp;
				np -= redp;
				for(int i = 0; i < redp; i++) {
					Particle p = new Particle(ParticleType.RED);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
				
				int yellowp = Math.min(np, m_ClonePowerSet.yellow);
				m_ClonePowerSet.yellow-= yellowp;
				np -= yellowp;
				for(int i = 0; i < yellowp; i++) {
					Particle p = new Particle(ParticleType.YELLOW);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
				
				int bluep = Math.min(np, m_ClonePowerSet.blue);
				m_ClonePowerSet.blue -= bluep;
				np -= bluep;
				for(int i = 0; i < bluep; i++) {
					Particle p = new Particle(ParticleType.BLUE);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
				
				int greenp = Math.min(np, m_ClonePowerSet.green);
				m_ClonePowerSet.green -= greenp;
				np -= greenp;
				for(int i = 0; i < greenp; i++) {
					Particle p = new Particle(ParticleType.GREEN);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
				
				int orangep = Math.min(np, m_ClonePowerSet.orange);
				m_ClonePowerSet.orange -= orangep;
				np -= orangep;
				for(int i = 0; i < orangep; i++) {
					Particle p = new Particle(ParticleType.ORANGE);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
				
				int purplep = Math.min(np, m_ClonePowerSet.purple);
				m_ClonePowerSet.purple -= purplep;
				np -= purplep;
				for(int i = 0; i < purplep; i++) {
					Particle p = new Particle(ParticleType.PURPLE);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
				
				int whitep = Math.min(np, m_ClonePowerSet.white);
				m_ClonePowerSet.white -= whitep;
				np -= whitep;
				for(int i = 0; i < whitep; i++) {
					Particle p = new Particle(ParticleType.WHITE);
					m_ParticleDesc.Set(p);
					p.SetFocus(GetPowerFocalPoint());
					((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				}
			}
		}
		else if(m_Type == 1) 
		{
			int r = 0, y = 0, b = 0, g = 0, o = 0, p = 0, w = 0;
			for(int i = 0; i < m_Particles.size(); i++)
			{
				if(!m_Particles.get(i).particle.IsAlive())
				{
					m_Particles.remove(i);
					//i--;
					continue;
				}
				switch (m_Particles.get(i).particle.GetType()) {
				case RED:
					r++;
					break;
				case YELLOW:
					y++;
					break;
				case BLUE:
					b++;
					break;
				case GREEN:
					g++;
					break;
				case ORANGE:
					o++;
					break;
				case PURPLE:
					p++;
					break;
				case WHITE:
					w++;
					break;
				default:
					break;
				}
			}
			
			SetSize(ParticleType.RED, m_PowerSet.red, r);
			SetSize(ParticleType.YELLOW, m_PowerSet.yellow, y);
			SetSize(ParticleType.BLUE, m_PowerSet.blue, b);
			SetSize(ParticleType.GREEN, m_PowerSet.green, g);
			SetSize(ParticleType.ORANGE, m_PowerSet.orange, o);
			SetSize(ParticleType.PURPLE, m_PowerSet.purple, p);
			SetSize(ParticleType.WHITE, m_PowerSet.white, w);
			
			Vector2 v = GetPowerFocalPoint();
			for(int i = 0; i < m_Particles.size(); i++)
				m_Particles.get(i).Update(v);
		}
	}
}

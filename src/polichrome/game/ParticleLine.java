package polichrome.game;

import java.util.ArrayList;
import java.util.List;

import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.game.Power.ParticleDesc;
import polichrome.game.Power.PowerSet;

public class ParticleLine {

	public static class ParticleNode {
		public ParticleNode() {	}
		public ParticleNode(Vector2 e, float t) {
			end = e;
			time = t;
		}
		public Vector2 end;
		public float time;
	};
	
	private List<ParticleNode> m_Nodes;
	private List<Particle> m_Particles = new ArrayList<Particle>();
	
	protected PowerSet m_PowerSet;
	protected PowerSet m_ClonePowerSet;
	protected ParticleDesc m_ParticleDesc;
	protected float m_Interval = 0.7f;
	protected float m_Timer = m_Interval;
	protected int m_NumParticles;
	protected Vector2 m_Focus;
	protected float m_TrackDuration;
	
	public PowerSet GetPowerSet() { return m_PowerSet; }
	public ParticleDesc GetParticleDesc() { return m_ParticleDesc; }
	public void SetInterval(float f) { m_Interval = f; }
	public void SetFocus(Vector2 v) { m_Focus = v; }
	
	public ParticleNode GetNextNodeByTime(float t) {
		if(t >= m_TrackDuration)
			return null;
		for(int i = 0; i < m_Nodes.size(); i++) {
			if(t < m_Nodes.get(i).time) {
				return m_Nodes.get(i);
			}
			else {				
				t -= m_Nodes.get(i).time;
			}
		}
		return null;
	}
	
	public float GetTrackDuration() {
		float t = 0;
		for(ParticleNode n : m_Nodes)
			t += n.time;
		return t;
	}
	
	public ParticleLine(List<ParticleNode> nodes) {
		m_PowerSet = new PowerSet();
		m_ParticleDesc = new ParticleDesc();
		m_Nodes = nodes;
		m_TrackDuration = GetTrackDuration();
	}
	
	protected void OnParticleReach() {
		
	}
	
	public void Update() {
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
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
			
			int yellowp = Math.min(np, m_ClonePowerSet.yellow);
			m_ClonePowerSet.yellow-= yellowp;
			np -= yellowp;
			for(int i = 0; i < yellowp; i++) {
				Particle p = new Particle(ParticleType.YELLOW);
				m_ParticleDesc.Set(p);
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
			
			int bluep = Math.min(np, m_ClonePowerSet.blue);
			m_ClonePowerSet.blue -= bluep;
			np -= bluep;
			for(int i = 0; i < bluep; i++) {
				Particle p = new Particle(ParticleType.BLUE);
				m_ParticleDesc.Set(p);
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
			
			int greenp = Math.min(np, m_ClonePowerSet.green);
			m_ClonePowerSet.green -= greenp;
			np -= greenp;
			for(int i = 0; i < greenp; i++) {
				Particle p = new Particle(ParticleType.GREEN);
				m_ParticleDesc.Set(p);
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
			
			int orangep = Math.min(np, m_ClonePowerSet.orange);
			m_ClonePowerSet.orange -= orangep;
			np -= orangep;
			for(int i = 0; i < orangep; i++) {
				Particle p = new Particle(ParticleType.ORANGE);
				m_ParticleDesc.Set(p);
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
			
			int purplep = Math.min(np, m_ClonePowerSet.purple);
			m_ClonePowerSet.purple -= purplep;
			np -= purplep;
			for(int i = 0; i < purplep; i++) {
				Particle p = new Particle(ParticleType.PURPLE);
				m_ParticleDesc.Set(p);
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
			
			int whitep = Math.min(np, m_ClonePowerSet.white);
			m_ClonePowerSet.white -= whitep;
			np -= whitep;
			for(int i = 0; i < whitep; i++) {
				Particle p = new Particle(ParticleType.WHITE);
				m_ParticleDesc.Set(p);
				p.SetFocus(m_Focus);
				((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel()).AddEntity(p);
				m_Particles.add(p);
			}
		}
		
		for(int i = 0; i < m_Particles.size(); i++) {
			if(!m_Particles.get(i).GetFocusPointLerping()) {
				ParticleNode next = GetNextNodeByTime(m_TrackDuration - m_Particles.get(i).GetLife());
				if(next == null) {
					OnParticleReach();
					m_Particles.remove(i);
				}
				else m_Particles.get(i).LerpFocusPoint(next.end, next.time);
			}
		}
	}
}

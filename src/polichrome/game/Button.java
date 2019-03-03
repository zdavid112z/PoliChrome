package polichrome.game;

import java.util.List;

import polichrome.Audio;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.ParticleLine.ParticleNode;
import polichrome.game.tile.Tile;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Button extends Mob {

	protected PImage m_ButtonUnpressed, m_ButtonPressed;
	protected ParticleLine m_ParticleLine;
	protected boolean m_Pressed = false;
	protected boolean m_ConstantPress = false;
	protected Door m_Door;
	protected float m_DoorSafetyTime = 0.25f;
	
	public Button(Vector2 buttonPos, boolean constantPress, Door door, Vector2 particlesStart, List<ParticleNode> nodes, float speed) {
		super("ButtonAndDoor");
		m_Position = buttonPos.Multiply(Tile.SIZE).Add(new Vector2(0, 16));
		m_ConstantPress = constantPress;
		m_Size.x = 48;
		m_Size.y = 0;
		m_ColBoxOffset.y = 20;
		m_IgnoreCollision = true;
		if(particlesStart == null) {
			particlesStart = GetCenteredCoordinates();
		}
		m_IgnoreCollision = false;
		m_Door = door;
		Vector2 prev = particlesStart;
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).time = speed * nodes.get(i).end.Distance(new Vector2());
			nodes.get(i).end = nodes.get(i).end.Add(prev);
			prev = nodes.get(i).end;
		}
		m_ParticleLine = new ParticleLine(nodes) {
			protected void OnParticleReach() {
				m_Door.UnlockFor(m_DoorSafetyTime);
			}
		};
		m_ParticleLine.SetFocus(particlesStart);
		m_ParticleLine.SetInterval(0.1f);
		m_ParticleLine.GetParticleDesc().changeImmortalChance = 1;
		m_ParticleLine.GetParticleDesc().minLife = m_ParticleLine.GetTrackDuration();
		m_ParticleLine.GetParticleDesc().maxLife = m_ParticleLine.GetTrackDuration();
		m_ParticleLine.GetParticleDesc().minMaxDistance = 1;
		m_ParticleLine.GetParticleDesc().maxMaxDistance = 3;
		m_ParticleLine.GetParticleDesc().minSpeed = 1;
		m_ParticleLine.GetParticleDesc().maxSpeed = 2;
		m_ParticleLine.GetParticleDesc().minRotationSpeed = 1;
		m_ParticleLine.GetParticleDesc().maxRotationSpeed = 2;
		m_ParticleLine.GetParticleDesc().minOffset = 0;
		m_ParticleLine.GetParticleDesc().maxOffset = 2;
		m_ParticleLine.GetPowerSet().white = 0;
		
		m_ButtonPressed = new PImage("/entity/button_pressed.png");
		m_ButtonUnpressed = new PImage("/entity/button_unpressed.png");
	}

	private void SetPressed(boolean b) {
		if(m_Pressed == false && b == true)
			(new Audio("/sfx/button_press.wav")).Play();
		m_Pressed = b;
	}
	
	@Override
	public void Update() {
		GameLevel l = ((GameLevel)Main.g_Game.GetLevelManager().GetCurrentLevel());
		boolean p = m_Pressed;
		if(!m_ConstantPress)
			p = false;
		for(int i = 0; i < l.GetNumEntities(); i++) {
			if(l.GetEntityAt(i) instanceof Player || l.GetEntityAt(i) instanceof Box) {
				Mob b = (Mob)l.GetEntityAt(i);
				Vector2 bp = b.GetCenteredCoordinates();
				Vector2 bs = b.GetSize();
				Vector2 cc = GetCenteredCoordinates();
				if(Math.abs(cc.x - bp.x) <= bs.x / 2.0f + m_Size.x / 2.0f) {
					if(Math.abs(cc.y - bp.y) <= bs.y / 2.0f + 16 / 2.0f)
					{
						p = true;
					}
				}
			}
		}
		if(m_Pressed != p)
			SetPressed(p);
		if(!m_Pressed)
			m_ParticleLine.GetPowerSet().white = 0;
		else m_ParticleLine.GetPowerSet().white = 10;
		m_ParticleLine.Update();
	}

	@Override
	public void Render(Renderer r) {
		if(m_Pressed)
			r.DrawImage(m_ButtonPressed, (int)m_Position.x, (int)m_Position.y, true);
		else r.DrawImage(m_ButtonUnpressed, (int)m_Position.x, (int)m_Position.y, true);
	}

}

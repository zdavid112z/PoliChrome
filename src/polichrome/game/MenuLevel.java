package polichrome.game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.Particle.ParticleType;
import polichrome.game.Power.ParticleDesc;
import polichrome.graphics.PFont;
import polichrome.graphics.Renderer;

public class MenuLevel extends Level {

	private int m_Ending = 0;
	private PFont m_SmallFont;
	private PFont m_Font;
	private List<Particle> m_Particles;
	private ParticleDesc m_ParticleDesc;
	
	private boolean DrawRectAndText(Renderer r, Vector2 pos, Vector2 size, int thickness, int bcolor, int color, int hcolor, String text, int tcolor) {
		int mx = Input.GetMouseX();
		int my = Input.GetMouseY();
		boolean hovers = pos.x < mx && (pos.x + size.x) > mx && pos.y < my && (pos.y + size.y) > my;
		if(hovers)
			color = hcolor;
		r.FillRect((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, color, false);
		r.FillRect((int)pos.x - thickness, (int)pos.y - thickness, (int)size.x + thickness * 2, thickness * 2, bcolor, false);
		r.FillRect((int)pos.x - thickness, (int)pos.y + (int)size.y - thickness, (int)size.x + thickness * 2, thickness * 2, bcolor, false);
		r.FillRect((int)pos.x - thickness, (int)pos.y - thickness, thickness * 2, (int)size.y + thickness * 2, bcolor, false);
		r.FillRect((int)pos.x + (int)size.x - thickness, (int)pos.y - thickness, thickness * 2, (int)size.y + thickness * 2, bcolor, false);
		int w = (int)size.x - m_Font.GetMetrics(text);
		w /= 2;
		r.DrawText(m_Font, (int)pos.x + w, (int)pos.y + 10, text, tcolor, false);
		
		return hovers;
	}
	
	public MenuLevel(String name) {
		super(name);
		m_SmallFont = new PFont("/fonts/mfnt4.fnt", "/fonts/mfnt4.png");
		m_Font = new PFont("/fonts/mfnt3.fnt", "/fonts/mfnt3.png");
		if(name.equals("credits")) {
			
			m_ParticleDesc = new ParticleDesc();
			m_ParticleDesc.changeImmortalChance = 1;
			m_ParticleDesc.immortal = true;
			m_ParticleDesc.applyOffset = false;
			m_ParticleDesc.minLife = 0.3f;
			m_ParticleDesc.maxLife = 1.0f;
			m_ParticleDesc.minMaxDistance = 90;
			m_ParticleDesc.maxMaxDistance = 130;
			m_ParticleDesc.minSpeed = 1;
			m_ParticleDesc.maxSpeed = 3;
			m_ParticleDesc.minRotationSpeed = 1;
			m_ParticleDesc.maxRotationSpeed = 2;
			m_ParticleDesc.minOffset = 0;
			m_ParticleDesc.maxOffset = 32;
			
			m_Particles = new ArrayList<Particle>();
			for(int i = 0; i < 400; i++)
			{
				Particle p = new Particle(ParticleType.WHITE);
				m_ParticleDesc.Set(p);
				p.SetFocus(new Vector2(Main.g_Game.GetWidth() / 2, Main.g_Game.GetHeight() / 2 - 70));
				m_Particles.add(p);
			}
		}
	}

	public void Update() {
		if(m_Name.equals("credits")) {
			for(int i = 0; i < m_Particles.size(); i++)
				m_Particles.get(i).Update();
		}
 	}
	
	public void Render(Renderer r) {
		r.ClearScreen(0xffffffff);
		if(m_Name.equals("menu")) {
			boolean p = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, 50), new Vector2(270, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "Play", 0);
			boolean h = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, 150), new Vector2(270, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "Help", 0);
			boolean e = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, 250), new Vector2(270, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "Exit", 0);
			if(p && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 1;
			if(h && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 2;
			if(e && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED) {
				m_Ending = 3;
				Main.g_Game.Stop();
			}
		} else if(m_Name.equals("help")) {
			r.DrawText(m_SmallFont, 10, 10, "Move using AD and press SPACE or W to jump\nSelect an ability using the numbers 1-4\nPress E to activate it\nHold R to restart, ESCAPE to exit and P to pause\nHold Q to recharge from a power station\nPress Q to throw a cube while using orange\nYou die once an enemy touches you\nPress ESCAPE to go back to the main menu", 0xff000000, false);
			if(Input.WasKeyPressed(KeyEvent.VK_ESCAPE) || Input.WasKeyPressed(KeyEvent.VK_ENTER))
			{
				m_Ending = 4;
			}
			
			boolean m = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, 290), new Vector2(270, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "Back", 0);
			if(m && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = -1;
		} else if(m_Name.equals("selection")) {
			boolean l1 = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 190, 60), new Vector2(60, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "1", 0);
			if(l1 && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 1;
			
			boolean l2 = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 110, 60), new Vector2(60, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "2", 0);
			if(l2 && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 2;
			
			boolean l3 = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 30, 60), new Vector2(60, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "3", 0);
			if(l3 && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 3;
			
			boolean l4 = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 + 50, 60), new Vector2(60, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "4", 0);
			if(l4 && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 4;
			
			boolean l5 = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 + 130, 60), new Vector2(60, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "5", 0);
			if(l5 && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 5;
			
			if(Input.WasKeyPressed(KeyEvent.VK_ESCAPE) || Input.WasKeyPressed(KeyEvent.VK_ENTER))
			{
				m_Ending = -1;
			}
			
			boolean m = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, 290), new Vector2(270, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "Back", 0);
			if(m && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = -1;
		} else if(m_Name.equals("credits")) {
			DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, Main.g_Game.GetHeight() / 2 - 40 / 2 - 80), new Vector2(270, 40), 0, 0xffffffff, 0xffffffff, 0xffffffff, "You won!", 0xff000000);
			for(int i = 0; i < m_Particles.size(); i++)
				m_Particles.get(i).Render(r);
			boolean m = DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 270 / 2, 290), new Vector2(270, 60), 2, 0xff000000, 0xffffffff, 0xffcccccc, "Back", 0);
			if(m && Input.GetMouseButtonState(1) == Input.KEY_JUST_PRESSED)
				m_Ending = 4;
			if(Input.WasKeyPressed(KeyEvent.VK_ESCAPE))
				m_Ending = 4;
		}
	}
	
	@Override
	public String GetNextLevel() {
		if(m_Name.equals("selection")) {
			if(m_Ending == -1)
				return "menu";
			return "level" + m_Ending;
		}
		if(m_Ending == 1)
			return "selection";
		if(m_Ending == 2)
			return "help";
		if(m_Ending == 3)
			return "exit";
		if(m_Ending == 4)
			return "menu";
		return "menu";
	}

	/*
	 * 0 = ongoing
	 * 1 = level selection screen
	 * 2 = help
	 * 3 = exit
	 * 4 = main menu
	 */
	@Override
	public int GetEnding() {
		return m_Ending;
	}

}

package polichrome.game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import polichrome.Input;
import polichrome.Main;
import polichrome.Vector2;
import polichrome.game.tile.Tile;
import polichrome.graphics.PFont;
import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class GameLevel extends Level {

	// 0 = ongoing, 1 = restart, 2 = next, 3 = menu
	private int m_Ending = 0;
	private int m_LevelIndex = 0;
	private LevelData m_LevelData;
	private List<Entity> m_Entities;
	private PImage m_Map;
	private int[][] m_Tiles;
	private float m_Gravity = 10;
	private float m_Friction = 10;
	private Player m_Player;
	private float m_RestartEndTime = 1.5f;
	private float m_RTimer;
	private float m_ETimer;
	private PFont m_Font;
	private Vector2 m_Checkpoint = null;
	private boolean m_Paused;
	private PFont m_SmallFont = null;
	
	public float GetGravity() { return m_Gravity; }
	public float GetFriction() { return m_Friction; }
	public PImage GetMap() { return m_Map; }
	public Tile GetTileAt(int x, int y) { return Tile.GetTile(m_Tiles[x][y]); }
	public Player GetPlayer() { return m_Player; }
	
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
	
	private boolean IsPixel(int x, int y, int color) {
		if(x < 0)
			return false;
		if(y < 0)
			return false;
		if(x >= m_Map.GetWidth())
			return false;
		if(y >= m_Map.GetHeight())
			return false;
		if((m_Map.GetPixels()[x + y * m_Map.GetWidth()] & 0xffffff) == color)
			return true;
		return false;
	}
	
	public GameLevel(String name) {
		super(name);
		m_SmallFont = new PFont("/fonts/mfnt4.fnt", "/fonts/mfnt4.png");
		Reload(null);
	}
	
	public void SetCheckpoint(Vector2 p) {
		m_Checkpoint = new Vector2(p);
	}
	
	public void Reload(Vector2 playerPos) {
		m_Paused = false;
		m_RTimer = 0;
		m_ETimer = 0;
		m_Entities = new ArrayList<Entity>();
		m_Font = new PFont("/fonts/mfnt3.fnt", "/fonts/mfnt3.png");
		m_LevelIndex = Integer.parseInt(m_Name.substring(5));
		m_LevelData = new LevelData("/" + m_Name + "/data.dat");
		m_Map = new PImage("/" + m_Name + "/" + m_LevelData.GetMapPath());
		m_Tiles = new int[m_Map.GetWidth()][m_Map.GetHeight()];
		for(int x = 0; x < m_Map.GetWidth(); x++) {
			for(int y = 0; y < m_Map.GetHeight(); y++) {
				if((m_Map.GetPixels()[x + y * m_Map.GetWidth()] & 0xffffff) == 0)
				{
					m_Tiles[x][y] = Tile.BLACK;
					if(IsPixel(x + 1, y, 0xffffff) && IsPixel(x - 1, y, 0xffffff) && IsPixel(x, y - 1, 0xffffff) && IsPixel(x, y + 1, 0xffffff))
						m_Tiles[x][y] = Tile.BLACK_DOT;
					if(IsPixel(x + 1, y, 0) && IsPixel(x - 1, y, 0xffffff) && IsPixel(x, y - 1, 0xffffff) && IsPixel(x, y + 1, 0xffffff))
						m_Tiles[x][y] = Tile.BLACK_LEFT;
					if(IsPixel(x + 1, y, 0xffffff) && IsPixel(x - 1, y, 0) && IsPixel(x, y - 1, 0xffffff) && IsPixel(x, y + 1, 0xffffff))
						m_Tiles[x][y] = Tile.BLACK_RIGHT;
					if(IsPixel(x + 1, y, 0xffffff) && IsPixel(x - 1, y, 0xffffff) && IsPixel(x, y - 1, 0) && IsPixel(x, y + 1, 0xffffff))
						m_Tiles[x][y] = Tile.BLACK_DOWN;
					if(IsPixel(x + 1, y, 0xffffff) && IsPixel(x - 1, y, 0xffffff) && IsPixel(x, y - 1, 0xffffff) && IsPixel(x, y + 1, 0))
						m_Tiles[x][y] = Tile.BLACK_TOP;
					if(IsPixel(x + 1, y, 0) && IsPixel(x - 1, y, 0xffffff) && IsPixel(x, y - 1, 0xffffff) && IsPixel(x, y + 1, 0))
						m_Tiles[x][y] = Tile.BLACK_TOP_LEFT;
					if(IsPixel(x + 1, y, 0xffffff) && IsPixel(x - 1, y, 0) && IsPixel(x, y - 1, 0xffffff) && IsPixel(x, y + 1, 0))
						m_Tiles[x][y] = Tile.BLACK_TOP_RIGHT;
					if(IsPixel(x + 1, y, 0) && IsPixel(x - 1, y, 0xffffff) && IsPixel(x, y - 1, 0) && IsPixel(x, y + 1, 0xffffff))
						m_Tiles[x][y] = Tile.BLACK_DOWN_LEFT;
					if(IsPixel(x + 1, y, 0xffffff) && IsPixel(x - 1, y, 0) && IsPixel(x, y - 1, 0) && IsPixel(x, y + 1, 0xffffff))
						m_Tiles[x][y] = Tile.BLACK_DOWN_RIGHT;
				}
				else 
				{
					m_Tiles[x][y] = Tile.NULL;
					//if((m_Map.GetPixels()[x + y * m_Map.GetWidth()] & 0xffffff) == 0x)
				}
			}
		}
		if(playerPos == null)
			m_Player = new Player(m_LevelData.GetPlayerX(), m_LevelData.GetPlayerY());
		else m_Player = new Player(playerPos.x, playerPos.y);
		m_Entities.addAll(m_LevelData.GetEntities());
		m_Entities.add(m_Player);
	}
	
	public int GetNumEntities() {
		return m_Entities.size();
	}
	
	public Entity GetEntityAt(int index) {
		return m_Entities.get(index);
	}
	
	public Entity GetEntityByName(String name) {
		return GetEntityByName(name, 1);
	}
	
	public Entity GetEntityByName(String name, int repeat) {
		for(Entity e : m_Entities) {
			if(e.GetName().equals(name))
			{
				repeat--;
				if(repeat == 0)
					return e;
			}
		}
		return null;
	}
	
	public void AddEntity(Entity e) {
		m_Entities.add(e);
	}
	
	public void Update() {
		if(Input.WasKeyPressed(KeyEvent.VK_P)) {
			m_Paused = !m_Paused;
		}
		
		if(!m_Paused)
		{
		
			for(int i = 0; i < m_Entities.size(); i++) {
				m_Entities.get(i).Update();
				if(!m_Entities.get(i).IsAlive()) {
					if(m_Entities.get(i).GetName().equals("Player")) {
						m_Ending = 1;
					}
					m_Entities.remove(i);
					i--;
				}
			}
		
		}
		
		if(Input.IsKeyDown(KeyEvent.VK_R)) {
			m_RTimer += Main.g_Game.GetDeltaTime();
			if(m_RTimer >= m_RestartEndTime) {
				SetEnding(1);
			}
		} else m_RTimer = 0;
			
		if(Input.IsKeyDown(KeyEvent.VK_ESCAPE)) {
			m_ETimer += Main.g_Game.GetDeltaTime();
			if(m_ETimer >= m_RestartEndTime) {
				SetEnding(3);
			}
		} else m_ETimer = 0;
		if(m_Ending == 1) {
			if(m_Checkpoint != null)
				Reload(m_Checkpoint);
			else Reload(null);
			m_Ending = 0;
		}
	}
	
	public void SetEnding(int i) { m_Ending = i; }
	
	public int GetEnding() { return m_Ending; }
		
	public void Render(Renderer r) {
		
			Vector2 pos = m_Player.GetCenteredCoordinates();
			//System.out.println("Pos: " + pos.x + " : " + pos.y);
			pos = pos.Subtract(new Vector2(Main.g_Game.GetWidth() / 2, Main.g_Game.GetHeight() / 2));
			//Vector2 p = new Vector2(Main.g_Game.GetWidth() / 2, Main.g_Game.GetHeight() / 2);
			//pos = p.Subtract(pos);
			pos.x = -pos.x;
			pos.y = -pos.y;
			if(pos.x > 0)
				pos.x = 0;
			if(pos.y > 0)
				pos.y = 0;
			if(pos.x < -m_Map.GetWidth() * Tile.SIZE + Main.g_Game.GetWidth())
				pos.x = -m_Map.GetWidth() * Tile.SIZE + Main.g_Game.GetWidth();
			if(pos.y < -m_Map.GetHeight() * Tile.SIZE + Main.g_Game.GetHeight())
				pos.y = -m_Map.GetHeight() * Tile.SIZE + Main.g_Game.GetHeight();
			r.SetCameraOffsetX((int)pos.x);
			r.SetCameraOffsetY((int)pos.y);
			//r.SetCameraOffsetX((int)-pos.x);
			//r.SetCameraOffsetY((int)-pos.y);
			r.ClearScreen(0xffffffff);
			for(int x = 0; x < m_Map.GetWidth(); x++) {
				for(int y = 0; y < m_Map.GetHeight(); y++) {
					Tile.GetTile(m_Tiles[x][y]).RenderAt(r, x * Tile.SIZE, y * Tile.SIZE);
				}
			}
			for(Entity e : m_Entities) {
				e.Render(r);
			}
			
			if(m_Paused)
			{
				r.FillRect(0, 0, Main.g_Game.GetWidth(), Main.g_Game.getHeight(), 0xccffffff, false);
				DrawRectAndText(r, new Vector2(Main.g_Game.GetWidth() / 2 - 100, 10), new Vector2(200, 50), 0, 0, 0, 0, "Paused", 0xff000000);
				r.DrawText(m_SmallFont, 10, 80, "Move using AD and press SPACE or W to jump\nSelect an ability using the numbers 1-4\nPress E to activate it\nHold R to restart, ESCAPE to exit and P to pause\nHold Q to recharge from a power station\nPress Q to throw a cube while using orange\nYou die once an enemy touches you\nPress P to resume", 0xff000000, false);
			}
			
			if(m_RTimer > 0) {
				r.DrawText(m_Font, 315, 320, "Hold R to restart", 0xffff0000, false);
			}
			if(m_ETimer > 0) {
				r.DrawText(m_Font, 255, 320, "Hold ESCAPE to exit", 0xffff0000, false);
			}
	}

	@Override
	public String GetNextLevel() {
		if(m_LevelIndex == 5) {
			return "menu";
		}
		return "level" + (m_LevelIndex + 1);
		
	}

}

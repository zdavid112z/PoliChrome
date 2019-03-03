package polichrome.game.tile;

import java.util.ArrayList;
import java.util.List;

import polichrome.graphics.PImage;
import polichrome.graphics.Renderer;

public class Tile {

	public static class NullTile extends Tile {

		public NullTile() {
			m_ID = NULL;
			m_IsWalkable = true;
			m_Image = null;
		}
		
		public void RenderAt(Renderer r, int x, int y) {}
		
	}
	
	public static final int SIZE = 32;
	public static final int NULL = 0;
	public static final int BLACK = 1;
	public static final int BLACK_DOWN = 2;
	public static final int BLACK_LEFT = 3;
	public static final int BLACK_RIGHT = 4;
	public static final int BLACK_TOP = 5;
	public static final int BLACK_DOWN_LEFT = 6;
	public static final int BLACK_DOWN_RIGHT = 7;
	public static final int BLACK_TOP_LEFT = 8;
	public static final int BLACK_TOP_RIGHT = 9;
	public static final int BLACK_DOT = 10;
	
	private static List<Tile> g_Tiles;
	
	public static void Init() {
		g_Tiles = new ArrayList<Tile>();
		g_Tiles.add(new NullTile());
		g_Tiles.add(new Tile("/tiles/black.png", BLACK, false));
		g_Tiles.add(new Tile("/tiles/down.png", BLACK_DOWN, false));
		g_Tiles.add(new Tile("/tiles/left.png", BLACK_LEFT, false));
		g_Tiles.add(new Tile("/tiles/right.png", BLACK_RIGHT, false));
		g_Tiles.add(new Tile("/tiles/top.png", BLACK_TOP, false));
		g_Tiles.add(new Tile("/tiles/down_left.png", BLACK_DOWN_LEFT, false));
		g_Tiles.add(new Tile("/tiles/down_right.png", BLACK_DOWN_RIGHT, false));
		g_Tiles.add(new Tile("/tiles/top_left.png", BLACK_TOP_LEFT, false));
		g_Tiles.add(new Tile("/tiles/top_right.png", BLACK_TOP_RIGHT, false));
		g_Tiles.add(new Tile("/tiles/dot.png", BLACK_DOT, false));
	}
	
	public static List<Tile> GetTiles() { return g_Tiles; }
	public static Tile GetTile(int id) { return g_Tiles.get(id); }
	
	protected PImage m_Image;
	protected int m_ID;
	protected boolean m_IsWalkable;
	
	protected Tile() {}
	
	public Tile(String imagePath, int id, boolean iswalkable) {
		m_Image = new PImage(imagePath);
		m_ID = id;
		m_IsWalkable = iswalkable;
	}
	
	public int GetID() { return m_ID; }
	public boolean IsWalkable() { return m_IsWalkable; }
	
	public void RenderAt(Renderer r, int x, int y) {
		r.DrawImage(m_Image, x, y, true);
	}
	
}

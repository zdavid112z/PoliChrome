package polichrome;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import polichrome.game.LevelManager;
import polichrome.game.MenuLevel;
import polichrome.game.tile.Tile;
import polichrome.graphics.Renderer;

public class Main extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	
	public static Main g_Game;
	
	protected int m_Width, m_Height, m_UPS;
	protected String m_Title;
	private boolean m_Running = false;
	
	private Renderer m_Renderer;
	private JFrame m_Frame;
	private Thread m_Thread;
	private Input m_Input;
	private LevelManager m_LevelManager;
	private Random m_Random;
	
	public Random GetRandom() { return m_Random; }
	public Input GetInput() { return m_Input; }
	public Renderer GetRenderer() { return m_Renderer; }
	public int GetWidth() { return m_Width; }
	public int GetHeight() { return m_Height; }
	public int GetUPS() { return m_UPS; }
	public String GetTitle() { return m_Title; }
	public LevelManager GetLevelManager() { return m_LevelManager; }
	public float GetDeltaTime() { return 1.0f / (float)GetUPS(); }
	
	public static void main(String[] args)
	{
		g_Game = new Main(args);
		g_Game.m_Frame.setResizable(true);
		g_Game.m_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		g_Game.m_Frame.add(g_Game);
		g_Game.m_Frame.pack();
		g_Game.m_Frame.setTitle(g_Game.m_Title);
		g_Game.m_Frame.setLocationRelativeTo(null);
		g_Game.m_Frame.setVisible(true);
		g_Game.m_Frame.setEnabled(true);
		g_Game.Start();
	}
	
	public Main(String[] args)
	{
		m_Width = 640;
		m_Height = 360;
		m_UPS = 90;
		m_Title = "PoliChrome";
		m_Frame = new JFrame();
		setPreferredSize(new Dimension(m_Width, m_Height));
		
		m_Renderer = new Renderer(m_Width, m_Height);
		m_Input = new Input();
		addKeyListener(m_Input);
		addMouseListener(m_Input);
		addMouseMotionListener(m_Input);
		addMouseWheelListener(m_Input);
		m_LevelManager = new LevelManager();
		m_Random = new Random();
	}
	
	private synchronized void Start()
	{
		m_Running = true;
		m_Thread = new Thread(this, "Display");
		m_Thread.start();
	}

	public synchronized void Stop()
	{
		System.exit(0);
		m_Running = false;
	}
	
	@Override
	public void run() {
		m_Frame.setIconImage((new ImageIcon(getClass().getResource("/icon.png"))).getImage());
		Tile.Init();
		(new Audio("/sfx/music.wav")).Loop();
		
		//m_LevelManager.SetLevel(new GameLevel("level1"));
		m_LevelManager.SetLevel(new MenuLevel("menu"));
		
		int ups = 0, fps = 0;
		int interval = 1000000000 / m_UPS;
		long timer = System.nanoTime();
		long fpst = System.currentTimeMillis();
		while(m_Running)
		{
			long st = System.nanoTime();
			while(st - timer >= interval)
			{
				Update();
				timer += interval;
				ups++;
			}
			Render();
			fps++;
			if(System.currentTimeMillis() - fpst >= 1000)
			{
				fpst += 1000;
				//m_Frame.setTitle(m_Title + " | UPS: " + ups + " | FPS: " + fps);
				ups = 0;
				fps = 0;
			}
		}
	}
	
	private void Update()
	{
		m_LevelManager.Update();
		Input.Update((float)m_Width / (float)getWidth(), (float)m_Height / (float)getHeight());
	}
	
	private void Render()
	{
		BufferStrategy bs = getBufferStrategy();
		if(bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		m_Renderer.Begin(g);
		m_LevelManager.Render(m_Renderer);
		m_Renderer.End(getWidth(), getHeight());
		
		g.dispose();
		bs.show();
	}

}

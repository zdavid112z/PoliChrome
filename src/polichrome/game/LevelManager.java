package polichrome.game;

import polichrome.Main;
import polichrome.graphics.Renderer;

public class LevelManager {

	private Level m_CurrentLevel;
	
	public LevelManager() {
		
	}
	
	public Level GetCurrentLevel() { return m_CurrentLevel; }
	
	public void SetLevel(Level l) {
		m_CurrentLevel = l;
	}
	
	public void Update() {
		if(m_CurrentLevel != null)
		{
			m_CurrentLevel.Update();
			if(m_CurrentLevel.GetEnding() != 0) {
				if(m_CurrentLevel.GetName().equals("selection")) {
					if(m_CurrentLevel.GetEnding() == -1)
						m_CurrentLevel = new MenuLevel("menu");
					else m_CurrentLevel = new GameLevel(m_CurrentLevel.GetNextLevel());
				}
				else if(m_CurrentLevel instanceof MenuLevel) {
					if(m_CurrentLevel.GetNextLevel().equals("exit"))
						Main.g_Game.Stop();
					else m_CurrentLevel = new MenuLevel(m_CurrentLevel.GetNextLevel());
				}
				else if(m_CurrentLevel.GetEnding() == 1) {
					if(m_CurrentLevel instanceof GameLevel)
						m_CurrentLevel = new GameLevel(m_CurrentLevel.GetName());
				}
				else if(m_CurrentLevel.GetEnding() == 2) {
					if(m_CurrentLevel instanceof GameLevel) {
						String name = m_CurrentLevel.GetNextLevel();
						if(name.startsWith("level"))
							m_CurrentLevel = new GameLevel(name);
						else m_CurrentLevel = new MenuLevel("credits");
					}
				}
				else if(m_CurrentLevel.GetEnding() == 3) {
					if(m_CurrentLevel instanceof GameLevel) {
						m_CurrentLevel = new MenuLevel("menu");
					}
				}
			}
		}
	}
	
	public void Render(Renderer r) {
		if(m_CurrentLevel != null)
			m_CurrentLevel.Render(r);
	}
}

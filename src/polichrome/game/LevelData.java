package polichrome.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import polichrome.Vector2;
import polichrome.game.ParticleLine.ParticleNode;
import polichrome.game.Power.PowerSet;
import polichrome.graphics.PFont;

public class LevelData {

	private List<Entity> m_Entities = new ArrayList<Entity>();
	private String m_MapPath;
	private float m_PlayerX, m_PlayerY;
	
	public LevelData(String path) {
		try {
			List<PFont> fonts = new ArrayList<PFont>();
			BufferedReader br = new BufferedReader(new InputStreamReader(LevelData.class.getResourceAsStream(path)));
			String line;
			while((line = br.readLine()) != null) {
				String[] words = line.split(" ");
				if(words[0].equals("map"))
					m_MapPath = new String(words[1]);
				else if(words[0].equals("player_x"))
					m_PlayerX = Float.parseFloat(words[1]);
				else if(words[0].equals("player_y"))
					m_PlayerY = Float.parseFloat(words[1]);
				else if(words[0].equals("walker")) {
					m_Entities.add(new Walker(
							new Vector2(
									Integer.parseInt(words[1]),
									Integer.parseInt(words[2])),
							new Vector2(
									Integer.parseInt(words[3]),
									Integer.parseInt(words[4])),
							Float.parseFloat(words[5])));
				}
				else if(words[0].equals("saw")) {
					m_Entities.add(new Saw(
							new Vector2(
									Integer.parseInt(words[1]),
									Integer.parseInt(words[2]))));
				}
				else if(words[0].equals("box")) {
					m_Entities.add(new Box(
							new Vector2(
									Float.parseFloat(words[1]),
									Float.parseFloat(words[2]))));
				}
				else if(words[0].equals("button")) {
					Vector2 buttonPos = new Vector2(
							Integer.parseInt(words[1]),
							Integer.parseInt(words[2]));
					Vector2 doorPos = new Vector2(
							Integer.parseInt(words[3]),
							Integer.parseInt(words[4]));
					int con = Integer.parseInt(words[5]);
					float speed = Float.parseFloat(words[6]);
					int numStages = Integer.parseInt(words[7]);
					List<ParticleNode> n = new ArrayList<ParticleNode>();
					for(int i = 0; i < numStages; i++) {
						n.add(new ParticleNode(
								new Vector2(
										Float.parseFloat(words[8 + i * 2]),
										Float.parseFloat(words[9 + i * 2])),0));
					}
					Door d = new Door(doorPos);
					m_Entities.add(d);
					m_Entities.add(new Button(buttonPos, con == 1, d, null, n, speed));
				}
				else if(words[0].equals("power")) {
					Vector2 pos = new Vector2(
							Integer.parseInt(words[1]),
							Integer.parseInt(words[2]));
					PowerSet ps = new PowerSet(
							Integer.parseInt(words[3]),
							Integer.parseInt(words[4]),
							Integer.parseInt(words[5]),
							Integer.parseInt(words[6]),
							Integer.parseInt(words[7]),
							Integer.parseInt(words[8]),
							Integer.parseInt(words[9]));
					m_Entities.add(new PowerStation(pos, ps));
				}
				else if(words[0].equals("clear")) {
					Vector2 pos = new Vector2(
							Integer.parseInt(words[1]),
							Integer.parseInt(words[2]));
					Door d = new Door(pos.Subtract(new Vector2(3, 0)));
					m_Entities.add(d);
					m_Entities.add(new ClearDoor(pos, d));
				}
				else if(words[0].equals("advancer")) {
					Vector2 pos = new Vector2(
							Integer.parseInt(words[1]),
							Integer.parseInt(words[2]));
					m_Entities.add(new Advancer(pos));
				}
				else if(words[0].equals("text")) {
					Vector2 pos = new Vector2(
							Float.parseFloat(words[1]),
							Float.parseFloat(words[2]));
					int colour = Integer.parseInt(words[3]);
					int fontn = Integer.parseInt(words[4]);
					String text = new String(words[5]);
					text = text.replace('_', ' ');
					m_Entities.add(new TextBox(pos, text, colour, fonts.get(fontn)));
				}
				else if(words[0].equals("font")) {
					String fntp = new String(words[1]);
					String fnti = new String(words[2]);
					fonts.add(new PFont(fntp, fnti));
				}
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Entity> GetEntities() { return m_Entities; }
	public String GetMapPath() { return m_MapPath; }
	public float GetPlayerX() { return m_PlayerX; }
	public float GetPlayerY() { return m_PlayerY; }
}

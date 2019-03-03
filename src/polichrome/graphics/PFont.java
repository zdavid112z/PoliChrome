package polichrome.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PFont {

	public static class PCharacterData {
		public char id;
		public int x, y, w, h, xadv, xoff, yoff;
	}
	
	public static class PKerningData {
		public char first, second;
		public int amount;
	}
	
	private List<PKerningData> m_Kernings = new ArrayList<PKerningData>();
	private List<PCharacterData> m_Characters = new ArrayList<PCharacterData>();
	private PImage m_FontImage;
	private int[] m_Paddings;
	private int m_LineHeight, m_Base;
	
	public List<PKerningData> GetKernings() { return m_Kernings; }
	public List<PCharacterData> GetCharacters() { return m_Characters; }
	public PImage GetFontImage() { return m_FontImage; }
	public int[] GetPaddings() { return m_Paddings; }
	public int GetLineHeight() { return m_LineHeight; }
	public int GetBase() { return m_Base; }
	
	public int GetMetrics(String text) {
		int res = 0;
		for(int i = 0; i < text.length(); i++) {
			for(int j = 0; j < m_Characters.size(); j++) {
				if(m_Characters.get(j).id == text.charAt(i)) {
					res += m_Characters.get(j).xadv;
					break;
				}
			}
		}
		return res;
	}
	
	public PFont(String fnt, String image) {
		m_Paddings = new int[4];
		InputStream is = PFont.class.getResourceAsStream(fnt);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while((line = br.readLine()) != null) {
				String[] words = line.split("\\s+");
				if(words[0].equals("info")) {
					String p = words[10];
					String[] paddings = p.substring(8, p.length()).split(",");
					m_Paddings[0] = Integer.parseInt(paddings[0]);
					m_Paddings[1] = Integer.parseInt(paddings[1]);
					m_Paddings[2] = Integer.parseInt(paddings[2]);
					m_Paddings[3] = Integer.parseInt(paddings[3]);
				}
				else if(words[0].equals("common")) {
					m_LineHeight = Integer.parseInt(words[1].substring(11, words[1].length()));
					m_Base = Integer.parseInt(words[2].substring(5, words[2].length()));
				}
				else if(words[0].equals("char")){
					PCharacterData data = new PCharacterData();
					data.id = (char)Integer.parseInt(words[1].substring(3, words[1].length()));
					data.x = Integer.parseInt(words[2].substring(2, words[2].length()));
					data.y = Integer.parseInt(words[3].substring(2, words[3].length()));
					data.w = Integer.parseInt(words[4].substring(6, words[4].length()));
					data.h = Integer.parseInt(words[5].substring(7, words[5].length()));
					data.xoff = Integer.parseInt(words[6].substring(8, words[6].length()));
					data.yoff = Integer.parseInt(words[7].substring(8, words[7].length()));
					data.xadv = Integer.parseInt(words[8].substring(9, words[8].length()));
					m_Characters.add(data);
				} else if(words[0].equals("kerning")) {
					PKerningData k = new PKerningData();
					k.first = (char)Integer.parseInt(words[1].substring(6, words[1].length()));
					k.second = (char)Integer.parseInt(words[2].substring(7, words[2].length()));
					k.amount = Integer.parseInt(words[3].substring(7, words[3].length()));
					m_Kernings.add(k);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_FontImage = new PImage(image);
	}
	
}
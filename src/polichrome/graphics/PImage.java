package polichrome.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class PImage {

	private int m_Width, m_Height;
	private int[] m_Pixels;
	
	public int GetWidth() { return m_Width; }
	public int GetHeight() { return m_Height; }
	public int[] GetPixels() { return m_Pixels; }
	
	public PImage(int w, int h)
	{
		m_Width = w;
		m_Height = h;
		m_Pixels = new int[m_Width * m_Height];
	}
	
	public PImage(String path)
	{
		try {
			BufferedImage image = ImageIO.read(PImage.class.getResource(path));
			m_Width = image.getWidth();
			m_Height = image.getHeight();
			m_Pixels = image.getRGB(0, 0, m_Width, m_Height, m_Pixels, 0, m_Width);
		} catch (Exception e) {
			System.err.println("Could not load image " + path + "!");
		}
	}
	
}

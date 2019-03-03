package polichrome.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer {

	protected int m_CameraOffsetX = 0, m_CameraOffsetY = 0;
	protected int m_Width, m_Height;
	private BufferedImage m_Image;
	protected int[] m_Pixels;
	private Graphics m_Graphics;
	
	public Renderer(int w, int h)
	{
		m_Width = w;
		m_Height = h;
		m_Image = new BufferedImage(m_Width, m_Height, BufferedImage.TYPE_INT_RGB);
		m_Pixels = ((DataBufferInt)m_Image.getRaster().getDataBuffer()).getData();
	}
	
	public int GetCameraOffsetX() { return m_CameraOffsetX; }
	public int GetCameraOffsetY() { return m_CameraOffsetY; }
	public void SetCameraOffsetX(int x) { m_CameraOffsetX = x; }
	public void SetCameraOffsetY(int y) { m_CameraOffsetY = y; }
	
	public void Begin(Graphics g)
	{
		m_Graphics = g;
	}
	
	public void End(int w, int h)
	{
		m_Graphics.drawImage(m_Image, 0, 0, w, h, null);
	}
	
	public void ClearScreen(int color)
	{
		for(int i = 0; i < m_Pixels.length; i++)
			m_Pixels[i] = color;
	}
	
	public int MixColors(int c1, int c2, float c1Amount)
	{
		int bc1 = (c1 & 0xff);
		int gc1 = (c1 >> 8) & 0xff;
		int rc1 = (c1 >> 16) & 0xff;
		int ac1 = (c1 >> 24) & 0xff;
		
		int bc2 = (c2 & 0xff);
		int gc2 = (c2 >> 8) & 0xff;
		int rc2 = (c2 >> 16) & 0xff;
		int ac2 = (c2 >> 24) & 0xff;
		
		int bf = (int)((float)rc1 * c1Amount + (float)rc2 * (1.0f - c1Amount));
		int gf = (int)((float)gc1 * c1Amount + (float)gc2 * (1.0f - c1Amount));
		int rf = (int)((float)bc1 * c1Amount + (float)bc2 * (1.0f - c1Amount));
		int af = (int)((float)ac1 * c1Amount + (float)ac2 * (1.0f - c1Amount));
		
		return (rf | (gf << 8) | (bf << 16) | (af << 24));
	}
	
	public int InvertColor(int c) {
		int bf = (c & 0xff);
		int gf = (c >> 8) & 0xff;
		int rf = (c >> 16) & 0xff;
		
		rf = 0xff - rf;
		gf = 0xff - gf;
		bf = 0xff - bf;
		
		return (rf | (gf << 8) | (bf << 16) | (0xff000000));
	}
	
	public void FillRect(int x, int y, int w, int h, int color, boolean applyOffset)
	{
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(h, m_Height - y);
		int xxf = Math.min(w, m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			for(int xx = xxs; xx < xxf; xx++)
			{
				int alpha = (color >> 24) & 0xff;
				if(alpha == 0)
					continue;
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(color, m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
			}
		}
	}
	
	public void DrawImage(PImage image, int x, int y, boolean applyOffset)
	{
		DrawImage(image, x, y, 0, 0, image.GetWidth(), image.GetHeight(), applyOffset);
	}
	
	public void DrawImageNearest(PImage image, int x, int y, int w, int h, boolean applyOffset)
	{
		DrawImageNearest(image, x, y, w, h, 0, 0, image.GetWidth(), image.GetHeight(), applyOffset);
	}
	
	public void DrawImageLinear(PImage image, int x, int y, int w, int h, boolean applyOffset)
	{
		DrawImageLinear(image, x, y, w, h, 0, 0, image.GetWidth(), image.GetHeight(), applyOffset);
	}
	
	public void DrawImageFlipped(PImage image, int x, int y, boolean applyOffset) {
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(image.GetHeight(), m_Height - y);
		int xxf = Math.min(image.GetWidth(), m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			for(int xx = xxs; xx < xxf; xx++)
			{
				int pixel = image.GetPixels()[((image.GetWidth() - xx) - 1) + (yy) * image.GetWidth()];
				int alpha = (pixel >> 24) & 0xff;
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(pixel, m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
				//m_Pixels[x + xx + (y + yy) * m_Width] = pixel;
			}
		}
	}
	
	public void DrawImageInvert(PImage image, int x, int y, boolean applyOffset) {
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(image.GetHeight(), m_Height - y);
		int xxf = Math.min(image.GetWidth(), m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			for(int xx = xxs; xx < xxf; xx++)
			{
				int pixel = image.GetPixels()[xx + (yy) * image.GetWidth()];
				int alpha = (pixel >> 24) & 0xff;
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(InvertColor(m_Pixels[x + xx + (y + yy) * m_Width]), m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
				//m_Pixels[x + xx + (y + yy) * m_Width] = pixel;
			}
		}
	}
	
	public void DrawImageMasked(PImage image, int x, int y, int color, boolean applyOffset) {
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(image.GetHeight(), m_Height - y);
		int xxf = Math.min(image.GetWidth(), m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			for(int xx = xxs; xx < xxf; xx++)
			{
				int pixel = image.GetPixels()[xx + (yy) * image.GetWidth()];
				int ialpha = (pixel >> 24) & 0xff;
				int calpha = (color >> 24) & 0xff;
				int alpha = (int)((((float)ialpha / 255.0f) * ((float)calpha / 255.0f)) * 255.0f);
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(color, m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
				//m_Pixels[x + xx + (y + yy) * m_Width] = pixel;
			}
		}
	}
	
	public void DrawImage(PImage image, int x, int y, int sx, int sy, int w, int h, boolean applyOffset)
	{
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(h, m_Height - y);
		int xxf = Math.min(w, m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			for(int xx = xxs; xx < xxf; xx++)
			{
				int pixel = image.GetPixels()[xx + sx + (yy + sy) * image.GetWidth()];
				int alpha = (pixel >> 24) & 0xff;
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(pixel, m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
				//m_Pixels[x + xx + (y + yy) * m_Width] = pixel;
			}
		}
	}
		
	public void DrawImageNearest(PImage image, int x, int y, int w, int h, int sx, int sy, int iw, int ih, boolean applyOffset)
	{
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(h, m_Height - y);
		int xxf = Math.min(w, m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			for(int xx = xxs; xx < xxf; xx++)
			{
				int ix = (int)(((float)xx / (float)w) * (float)iw);
				int iy = (int)(((float)yy / (float)h) * (float)ih);
				int pixel = image.GetPixels()[sx + ix + (sy + iy) * image.GetWidth()];
				int alpha = (pixel >> 24) & 0xff;
				if(alpha == 0)
					continue;
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(pixel, m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
			}
		}
	}
	
	public void DrawPixel(int color, int x, int y, boolean applyOffset)
	{
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		if(x < 0)
			return;
		if(x >= m_Width)
			return;
		if(y < 0)
			return;
		if(y >= m_Height)
			return;
		int alpha = (color >> 24) & 0xff;
		if(alpha == 0)
			return;
		m_Pixels[x + y * m_Width] = MixColors(color, m_Pixels[x + y * m_Width], (float)alpha / 255.0f) | 0xff000000;
	}
	
	public void DrawImageLinear(PImage image, int x, int y, int w, int h, int sx, int sy, int iw, int ih, boolean applyOffset)
	{
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int yys = Math.max(0, -y);
		int xxs = Math.max(0, -x);
		int yyf = Math.min(h, m_Height - y);
		int xxf = Math.min(w, m_Width - x);
		for(int yy = yys; yy < yyf; yy++)
		{
			//if(yy + y < 0)
			//	continue;
			//if(yy + y >= m_Height)
			//	continue;
			for(int xx = xxs; xx < xxf; xx++)
			{
				//if(xx + x < 0)
				//	continue;
				//if(xx + x >= m_Width)
				//	continue;
				float fx = Math.min(((float)xx / (float)w) * (float)iw + 0.5f, iw - 2);
				float fy = Math.min(((float)yy / (float)h) * (float)ih + 0.5f, ih - 2);
				
				int pxy = image.GetPixels()[(int)(fx + sx) + (int)(fy + sy) * image.GetWidth()];
				int bpxy = pxy & 0xff; int gpxy = (pxy >> 8) & 0xff; int rpxy = (pxy >> 16) & 0xff; int apxy = (pxy >> 24) & 0xff;
				int pXy = image.GetPixels()[(int)(fx + 1 + sx) + (int)(fy + sy) * image.GetWidth()];
				int bpXy = pXy & 0xff; int gpXy = (pXy >> 8) & 0xff; int rpXy = (pXy >> 16) & 0xff; int apXy = (pXy >> 24) & 0xff;
				int pxY = image.GetPixels()[(int)(fx + sx) + (int)(fy + 1 + sy) * image.GetWidth()];
				int bpxY = pxY & 0xff; int gpxY = (pxY >> 8) & 0xff; int rpxY = (pxY >> 16) & 0xff; int apxY = (pxY >> 24) & 0xff;
				int pXY = image.GetPixels()[(int)(fx + 1 + sx) + (int)(fy + 1 + sy) * image.GetWidth()];
				int bpXY = pXY & 0xff; int gpXY = (pXY >> 8) & 0xff; int rpXY = (pXY >> 16) & 0xff; int apXY = (pXY >> 24) & 0xff;
				
				float pcx = fx - (int)fx;
				float pcy = fy - (int)fy;
				
				int rf = (int)(((float)rpxy * (1.0f - pcx) + (float)rpXy * pcx) * (1.0f - pcy) + ((float)rpxY * (1.0f - pcx) + (float)rpXY * pcx) * pcy);
				int gf = (int)(((float)gpxy * (1.0f - pcx) + (float)gpXy * pcx) * (1.0f - pcy) + ((float)gpxY * (1.0f - pcx) + (float)gpXY * pcx) * pcy);
				int bf = (int)(((float)bpxy * (1.0f - pcx) + (float)bpXy * pcx) * (1.0f - pcy) + ((float)bpxY * (1.0f - pcx) + (float)bpXY * pcx) * pcy);
				int af = (int)(((float)apxy * (1.0f - pcx) + (float)apXy * pcx) * (1.0f - pcy) + ((float)apxY * (1.0f - pcx) + (float)apXY * pcx) * pcy);
				
				int pixel = bf | (gf << 8) | (rf << 16) | (af << 24);
				int alpha = (pixel >> 24) & 0xff;
				if(alpha == 0)
					continue;
				m_Pixels[x + xx + (y + yy) * m_Width] = MixColors(pixel, m_Pixels[x + xx + (y + yy) * m_Width], (float)alpha / 255.0f) | 0xff000000;
			}
		}
	}
	
	public void DrawText(PFont font, int x, int y, String text, int color, boolean applyOffset) 
	{
		if(applyOffset)
		{
			x += m_CameraOffsetX;
			y += m_CameraOffsetY;
		}
		int cx = x;
		char prev = 0;
		int ix = x;
		int yy = y;
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(c == '\n')
			{
				cx = ix;
				yy += font.GetBase();
				continue;
			}
			for(PFont.PKerningData k : font.GetKernings()) {
				if(prev == k.first && c == k.second)
				{
					cx += k.amount;
					break;
				}
			}
			for(PFont.PCharacterData d : font.GetCharacters()) {
				if(c == d.id) {
					DrawCharacter(font, cx + d.xoff, yy + d.yoff, d, color);
					cx += d.xadv;
					prev = c;
					break;
				}
			}
		}
	}
	
	public void DrawCharacter(PFont font, int x, int y, PFont.PCharacterData c, int color) {
		for(int yy = 0; yy < c.h; yy++) {
			if(yy + y < 0 || yy + y >= m_Height)
				continue;
			for(int xx = 0; xx < c.w; xx++) {
				if(xx + x < 0 || xx + x >= m_Width)
					continue;
				int alpha = (font.GetFontImage().GetPixels()[xx + c.x + (yy + c.y) * font.GetFontImage().GetWidth()] >> 24) & 0xff;
				if(alpha == 0)
					continue;
				m_Pixels[(xx + x) + (yy + y) * m_Width] = MixColors(color, m_Pixels[(xx + x) + (yy + y) * m_Width], (float)alpha / 255.0f) | 0xff000000;
			}
		}
	}
	
}

package polichrome;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

public class Audio {

	private Clip m_Clip;
	
	public Audio(String path) {
		try {
			
			class AudioListener implements LineListener {
				@Override
				public synchronized void update(LineEvent event) {
					Type eventType = event.getType();
					if (eventType == Type.STOP || eventType == Type.CLOSE) {
						notifyAll();
					}
				}
			}
			AudioListener listener = new AudioListener();
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream(path)));
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(listener);
			clip.open(audioInputStream);
			m_Clip = clip;
			
			FloatControl gainControl = (FloatControl) m_Clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-20.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(m_Clip == null)
			System.err.println("Could not load audio file " + path + "!");
	}
	
	public synchronized void Loop() {
		if(m_Clip == null)
			return;
		m_Clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public synchronized void SetToStart() {
		if(m_Clip == null)
			return;
		m_Clip.setFramePosition(0);
	}
	
	public synchronized void Play() {
		if(m_Clip == null)
			return;
		m_Clip.start();
	}
	
	public synchronized void Pause() {
		if(m_Clip == null)
			return;
		if(m_Clip == null)
			return;
		m_Clip.stop();
	}
	
	public synchronized void Stop() {
		if(m_Clip == null)
			return;
		m_Clip.stop();
		m_Clip.setFramePosition(0);
	}
	
	public synchronized boolean IsPlaying() {
		if(m_Clip == null)
			return false;
		return m_Clip.isActive();
	}

}

// THIS CODE WAS TAKEN FROM MERCER'S JUKEBOX PROJECT, IN WHICH HE GAVE
// US THE ORIGINAL VERSION OF THIS CODE WHICH INCLUDED ENDOFSONGLISTENERS
// AND METHODS FOR THEM. THE CODE HAS BEEN EDITED TO INCLUDE THE ABILITY TO
// STOP A SONG AFTER IT HAS BEEN STARTED.

/**
 * This class allows songs to be played in separate threads so they can
 * play concurrently.  It is also possible to register and EndOfSongListener
 * to each new instance of this class so the client code knows when the song
 * song has completely finished. For this, use Rick's class SongPlayer
 * with method playSong that takes an EndOfSongListener as its first argument.
 * 
 * @author Java Zoom and Jorge Vergara
 */
package songplayer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioFilePlayer extends Thread {

  private String fileName;
  private SourceDataLine line;
  private AudioInputStream din;
  private boolean running;
  public static String baseDir = System.getProperty("user.dir")
		  + System.getProperty("file.separator") + "src"
	      + System.getProperty("file.separator") + "songs"
	      + System.getProperty("file.separator");

  public AudioFilePlayer(String audioFileName) {
    fileName = audioFileName;
    running = true;
  }

  @Override
  public void run() {
	  if (fileName.equals(baseDir + "Pshoo.mp3") ||
			  fileName.equals(baseDir + "smooth.mp3") ||
			  fileName.equals(baseDir + "triumph.mp3")) {
		  while (running) {
			  play();
		  }
	  } else
		  play();
  }

  /**
   * Note: This Code snippet is from JavaZOOM'a JLayer project
   * 
   * Write the audio file to the output line.
   * 
   * After that loop finishes, send a songFinishedPlaying to all
   * EndOfSongListener objects.
   * 
   */
  public void play() {
    AudioFormat decodedFormat = null;
    try {
      File file = new File(fileName);
      AudioInputStream in = AudioSystem.getAudioInputStream(file);
      din = null;
      AudioFormat baseFormat = in.getFormat();

      decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
          baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
          baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
          false);

      din = AudioSystem.getAudioInputStream(decodedFormat, in);
      // Play now.
      rawplay(decodedFormat, din);
      in.close();
      // stop();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // This Code snippet is from JavaZOOM
  private void rawplay(AudioFormat targetFormat, AudioInputStream din) {
    line = null;
    try {
      byte[] data = new byte[4096];
      try {
        line = getLine(targetFormat);
      } catch (LineUnavailableException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (line != null) {
        // Start
        line.start();
        int nBytesRead = 0;
        @SuppressWarnings("unused")
        int nBytesWritten = 0;
        while (nBytesRead != -1) {
          nBytesRead = din.read(data, 0, data.length);
          if (nBytesRead != -1)
            nBytesWritten = line.write(data, 0, nBytesRead);
        }
        // Stop
        line.drain();
        line.stop();
	 	line.close();
	 	din.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void terminate() throws IOException {
	  if (line != null && din != null) {
		  line.drain();
		  line.stop();
		  line.close();
		  din.close();
	  }
	  running = false;
	  Thread.currentThread().interrupt();
	  return;
  }

  private SourceDataLine getLine(AudioFormat audioFormat)
      throws LineUnavailableException {
    SourceDataLine res = null;
    DataLine.Info info = new DataLine.Info(SourceDataLine.class,
        audioFormat);
    res = (SourceDataLine) AudioSystem.getLine(info);
    res.open(audioFormat);
    return res;
  }

}
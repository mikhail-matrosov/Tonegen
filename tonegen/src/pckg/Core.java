package pckg;

import java.awt.EventQueue;
import java.nio.ByteBuffer;
import javax.sound.sampled.*;

public class Core extends UI {
	private SampleThread m_thread;
	
	static int sampleRates[] = new int[]{192000, 176400, 96000, 88200, 48000, 44100};

	// Launch the app
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				boolean flag = true;
					for(int sampleRateNo = 0; flag; sampleRateNo++) {
					try {
						SAMPLING_RATE = sampleRates[sampleRateNo];
						Core frame = new Core();
						flag = false;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		/*while(true) {
			try {
				repeatable();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
	}

	public Core() {
		// UI
		super();
		
		// thread staff
		m_thread = new SampleThread();
		m_thread.start();
	}

	class SampleThread extends Thread {
		final static int BUFFER_SIZE = 4096;
		final static int SAMPLE_SIZE = 2;

		SourceDataLine line;

		// Get the number of queued samples in the SourceDataLine buffer
		private int getLineSampleCount() {
			return line.getBufferSize() - line.available();
		}
		
		// Continually fill the audio output buffer
		public void run() {
			// Open, using 16 bit, mono, and big endian byte ordering.
			try {
				AudioFormat format = new AudioFormat(SAMPLING_RATE, 16, 1, true, true);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						format, BUFFER_SIZE * SAMPLE_SIZE);

				if (!AudioSystem.isLineSupported(info))
					throw new LineUnavailableException();

				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format);
				line.start();
			} catch (Exception e) {
				System.out.println("Line of that type is not available");
				e.printStackTrace();
				System.exit(-1);
			}

			ByteBuffer cBuf = ByteBuffer.allocate(BUFFER_SIZE*SAMPLE_SIZE);

			while (true) {
				cBuf.clear();

				for (int i = 0; i < BUFFER_SIZE; i++) {
					cBuf.putShort((short) (Short.MAX_VALUE * synth.getNextSample()));
				}

				line.write(cBuf.array(), 0, cBuf.position());

				// Wait
				try {
					while (getLineSampleCount() > BUFFER_SIZE)
						Thread.sleep(1); // Give UI a chance to run
				} catch (InterruptedException e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}

			//line.drain();
			//line.close();
		}
	}
}
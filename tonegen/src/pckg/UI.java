package pckg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.util.Date;

public class UI extends Frame {

	final int SAMPLING_RATE = 48000;
	final Synthesizer synth = new Synthesizer(SAMPLING_RATE);
	
	final int wheelsCount = 8;
	Label lArray[] = new Label[wheelsCount];
	int wheelPos = 4;
	
	Label lVolume;
	Label lShape;
	
	double frequency = 0;
	final double MAX_FREQ = 99999.999;
	final double MIN_FREQ = 0;
	
	/*double displayFreq = 0;
	double enteredFreq = 0;
	long lastKeyPressTime = 0;
	final long numericTimeOut = 2000; // Milliseconds
	boolean hasPointBeenPressed = false;*/
	
	double volume = 1;
	double unmutedVol = 0;
	
	int shape = 0; // sin, square, triangle
	
	void setupWheels() {
		if (frequency>MAX_FREQ) frequency = MAX_FREQ;
		if (frequency<MIN_FREQ) frequency = MIN_FREQ;
		
		double f = (frequency + 5e-4) / 10000;
		for (int i=0; i<wheelsCount; i++) {
			int n = ((int) Math.floor(f)) % 10;
			lArray[i].setText(""+n);
			f *= 10;
		}
		synth.setFrequency(frequency);
	}
	
	void setupVolume() {
		if (volume>1) volume=1;
		if (volume<0) volume=0;
		lVolume.setText(String.format("Volume: %3.0f%%", volume*100));
		synth.setVolume(volume);
	}
	
	void setupShape() {
		switch(shape) {
		case 0:
			lShape.setText("Sin");
			break;
		case 1:
			lShape.setText("Sqr");
			break;
		case 2:
			lShape.setText("Tri");
			break;
		}
		synth.setShape(shape);
	}
	
	double wheelIncr(int n) {
		double r = 10000;
		while(n>0) { r /= 10; n--; };
		return r;
	}
	
	void drawMultiline(Container c, String s,
	                   int x, int y, int width, int lineheight,
	                   Font f) {
		for (String line : s.split("\n")) {
			Label L = new Label(line);
			L.setFont(f);
			L.setBounds(x, y, width, lineheight);
			c.add(L);
			y += lineheight;
		}
	}
	
	public UI() {
		// UI staff
		// create window
		super("ToneGen");
		setVisible(true);
		
		// setup its size
		int width = 310, height = 260;
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));

		// create container
		Container pane = new Container() {
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillOval(137, 50, 7, 7);
				
				for (int i=0; i<8; i++) {
					int x = i*25+(i<5? 10:20) + 12;
					g.drawLine(x, 15, x-3, 18);
					g.drawLine(x, 15, x+3, 18);
					g.drawLine(x, 70, x-3, 67);
					g.drawLine(x, 70, x+3, 67);
				}
			}
		};
		pane.setLayout(null);
		add(pane);
		// setup container's position
		Insets insets = getInsets();
		width = width-insets.left-insets.right;
		height = height-insets.top-insets.bottom;
		pane.setBounds(insets.left, insets.top, width, height);
		
		// draw wheels
		Font lFont = new Font("MONOSPACED", Font.BOLD, 40);
		for(int i=0; i<wheelsCount; i++) {
			lArray[i] = new Label("0");
			lArray[i].setFont(lFont);
			lArray[i].setBounds(i*25+(i<5? 10:20), 30, 25, 30);
			pane.add(lArray[i]);
		}
		
		// draw "Hz"
		Label Hz = new Label("Hz");
		Hz.setFont(lFont);
		Hz.setBounds(230, 30, 50, 30);
		pane.add(Hz);
		
		// setup focus
		lArray[4].setBackground(Color.GRAY);
		
		// print volume
		lVolume = new Label();
		lVolume.setFont(new Font("MONOSPACED", Font.BOLD, 20));
		lVolume.setBounds(15, 90, 150, 20);
		pane.add(lVolume);
		
		// print shape
		lShape = new Label();
		lShape.setFont(new Font("MONOSPACED", Font.BOLD, 20));
		lShape.setBounds(240, 90, 50, 20);
		pane.add(lShape);
		
		// print help
		Font helpFont = new Font("MONOSPACED", Font.BOLD, 15);
		
		String helpString = 
			"Arrows : Frequency\n" +
			"+/- : Volume Up/Dn\n" + 
			"M : Mute\n" +
			"S : Shape";
		
		drawMultiline(pane, helpString, 15, 130, 250, 20, helpFont);
		
		// setup synthesizer
		setupWheels();
		setupVolume();
		setupShape();
		
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				/*int chr = e.getKeyChar();
				
				if (hasPointBeenPressed) {
					
				} else {
					
				}
				if ('0' <= chr && chr <= '9') {
					
					enteredFreq *= 10;
					enteredFreq += wheelIncr(wheelPos) * (int) (chr-'0');
				}*/
				
				switch (code) {
				
				case KeyEvent.VK_UP:
					frequency += wheelIncr(wheelPos);
					setupWheels();
					break;
					
				case KeyEvent.VK_DOWN:
					frequency -= wheelIncr(wheelPos);
					setupWheels();
					break;
				
				case KeyEvent.VK_LEFT:
					lArray[wheelPos].setBackground(Color.WHITE);
					wheelPos = wheelPos<1? 0: wheelPos-1;
					lArray[wheelPos].setBackground(Color.GRAY);
					break;
					
				case KeyEvent.VK_RIGHT:
					lArray[wheelPos].setBackground(Color.WHITE);
					wheelPos = wheelPos>wheelsCount-2 ? wheelsCount-1: wheelPos+1;
					lArray[wheelPos].setBackground(Color.GRAY);
					break;
					
				case KeyEvent.VK_EQUALS:
					volume += 0.01;
					setupVolume();
					break;
					
				case KeyEvent.VK_MINUS:
					volume -= 0.01;
					setupVolume();
					break;
				
				case KeyEvent.VK_M:
					if (volume > 0) {
						unmutedVol = volume;
						volume = 0;
					} else {
						volume = unmutedVol;
					}
					setupVolume();
					break;
					
				case KeyEvent.VK_S:
					shape = (shape+1)%3;
					setupShape();
					break;
				}
			}

			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
		

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				synth.setVolume(0);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			};
		});
	}
	
	/*void repeatable() {
		if (new Date().getTime() - lastKeyPressTime < numericTimeOut) {
			// escape input
		}
	}*/
}

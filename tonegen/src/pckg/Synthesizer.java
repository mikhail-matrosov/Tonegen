package pckg;

import java.util.Random;

public class Synthesizer {
	private final double SAMPLING_RATE;

	public Synthesizer(int samplingRate) {
		SAMPLING_RATE = samplingRate;
	}
	
	double fadeDuration = 0.05; // in seconds
	double fadeTime = 0;
	double pVol = 0;
	double cVol = 0;
	double tVol = 0;
	
	double laceDuration = 0.05; // in seconds
	double laceTime = 0;
	double pFreq = 1e-4; // previous frequency
	double cFreq = 1e-4; // current frequency
	double tFreq = 1e-4; // target frequency
	
	double noize1 = 0;
	double noize2 = 0;
	
	int shape;
	
	double phase = 0; // phase of signal
	
	Random random = new Random();
	double noizePhase = 0;
	
	public double getNextSample() {
		laceTime += 1/SAMPLING_RATE;
		if (laceTime<laceDuration) {
			double x = laceTime/laceDuration;
			cFreq = pFreq*(1-x) + tFreq*x;
		} else {
			cFreq = tFreq;
		}
		
		fadeTime += 1/SAMPLING_RATE;
		if (fadeTime<fadeDuration) {
			double x = fadeTime/fadeDuration;
			cVol = pVol*(1-x) + tVol*x;
		} else {
			cVol = tVol;
		}

		//System.out.println(""+noize1+"\t"+noize2);
		
		phase += cFreq/SAMPLING_RATE;
		phase %= 1;
		
		switch (shape) {
		case 0:
			/*noizePhase += cFreq*random.nextGaussian()*0.2/SAMPLING_RATE;
			noizePhase %= 1;
			
			double noizeRe = Math.cos(noizePhase*2*Math.PI);
			double noizeIm = Math.sin(noizePhase*2*Math.PI);
			
			double re = Math.cos(phase*2*Math.PI);
			double im = Math.sin(phase*2*Math.PI);
			
			return cVol * (re*noizeRe-im*noizeIm);*/
			return cVol * Math.sin(phase*2*Math.PI);
		case 1:
			return cVol * (phase>0.5 ? 1 : -1);
		case 2:
			return cVol* (phase<0.5 ? phase*4-1 : 3-phase*4);
		}
		
		return 0;
	}
	
	public void setFrequency(double f) {
		pFreq = cFreq;
		tFreq = f;
		laceTime = 0;
	}
	
	public void setVolume(double v) {
		v = v>1? 1 : (v<0? 0 : v);
		pVol = cVol;
		tVol = v;
		fadeTime = 0;
	}
	
	public void setShape(int s) {
		shape = s;
	}
}

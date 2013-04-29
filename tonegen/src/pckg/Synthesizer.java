package pckg;

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
		
		// quality
		double Q = 20;
		double q = 1 - 2*Math.PI/SAMPLING_RATE/cFreq*Q;
		q = q>0 ? q : 0;
		noize1 += Math.random()-0.5;
		noize1 *= q;
		noize2 += noize1;
		noize2 *= q;

		//System.out.println(""+noize1+"\t"+noize2);
		
		phase += cFreq/SAMPLING_RATE;
		phase %= 1;
		
		switch (shape) {
		case 0:
			return cVol * Math.sin(noize2/400);
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

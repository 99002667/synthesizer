package pmpshk.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SoundArrayToWavHelper {
	private static int sampleRate = 22050;
	private static int sampleSizeInBytes = 3; //8, 16, 24
	private static int channels = 1;
	private static boolean isSigned = true;
	private static boolean isBigEndian = true;

	public static void main(String[] args) throws Exception {
		final AudioFormat audioFormat = new AudioFormat(sampleRate,
				sampleSizeInBytes * 8 /*bytes to bits*/,
				channels,
				isSigned,
				isBigEndian);

		//Frequency = ... Hz for ... seconds
		int frequencyOfSignal = 500;
		int seconds = 10;
		byte[] soundBytes = generateSineWavefreq(frequencyOfSignal, seconds);

		writeToWavFile(audioFormat, soundBytes, "test.wav");

		play(audioFormat, soundBytes);

	}

	private static void writeToWavFile(AudioFormat audioFormat, byte[] soundBytes, String fileName) throws IOException {
		AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(soundBytes), audioFormat, soundBytes.length);
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(fileName));
	}

	private static byte[] generateSineWavefreq(int frequencyOfSignal, int seconds) {
		// total samples = (duration in second) * (samples per second)
		byte[] sin = new byte[seconds * sampleRate * sampleSizeInBytes];
		double samplingInterval = (double) (sampleRate / frequencyOfSignal * sampleSizeInBytes);
		System.out.println("Sampling Frequency  : " + sampleRate);
		System.out.println("Frequency of Signal : " + frequencyOfSignal);
		System.out.println("Sampling Interval   : " + samplingInterval);
		for (int i = 0; i < sin.length; i++) {
			double angle = (2.0 * Math.PI * i) / samplingInterval;
			sin[i] = (byte) (Math.sin(angle) * 127);
			System.out.println("" + sin[i]);
		}
		return sin;
	}

	private static void play(AudioFormat audioFormat, byte[] soundBytes) {
		try {
			SourceDataLine line = AudioSystem.getSourceDataLine(audioFormat);
			line.open(audioFormat);
			line.start();
			//int length = sampleRate * soundBytes.length / 1000;
			line.write(soundBytes, 0, soundBytes.length);
			line.drain();
			line.close();
		} catch (LineUnavailableException ex) {
			System.err.println("error!");
		}
	}
}


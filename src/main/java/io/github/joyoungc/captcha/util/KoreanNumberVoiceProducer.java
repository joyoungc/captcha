package io.github.joyoungc.captcha.util;

import java.util.HashMap;
import java.util.Map;

import nl.captcha.audio.Sample;
import nl.captcha.audio.producer.VoiceProducer;
import nl.captcha.util.FileUtil;

public class KoreanNumberVoiceProducer  implements VoiceProducer {

	private static final Map<Integer, String> DEFAULT_VOICES_MAP = new HashMap<Integer, String>();
	private final Map<Integer, String> _voices;

	static {
		for (int i = 0; i < 10; ++i) {
			StringBuilder sb = new StringBuilder("/sounds/ko/numbers/");
			sb.append(i);
			sb.append(".wav");
			DEFAULT_VOICES_MAP.put(Integer.valueOf(i), sb.toString());
		}
	}
	
	public KoreanNumberVoiceProducer() {
		this(DEFAULT_VOICES_MAP);
	}

	public KoreanNumberVoiceProducer(Map<Integer, String> _voices) {
		this._voices = _voices;
	}
	
	public Sample getVocalization(char num) {
		try {
			int idx = Character.getNumericValue(num);
			String filename = (String) this._voices.get(Integer.valueOf(idx));
			return FileUtil.readSample(filename);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected <num> to be a number, got '" + num + "' instead.", e);
		}
	}
	
}

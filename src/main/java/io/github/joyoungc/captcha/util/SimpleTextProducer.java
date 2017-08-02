package io.github.joyoungc.captcha.util;

import nl.captcha.text.producer.TextProducer;

public class SimpleTextProducer implements TextProducer {
	
	private final String _srcStr;

	public SimpleTextProducer(String srcStr) {
		this._srcStr = srcStr;
	}

	public String getText() {
		return this._srcStr;
	}
}

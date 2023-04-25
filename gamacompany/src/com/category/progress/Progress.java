package com.category.progress;

import javax.swing.JProgressBar;

import com.category.App;

public class Progress extends Thread {

	private final JProgressBar progressBar;
	private final String message;
	private final int rate;

	public Progress(JProgressBar progress, String message, int rate) {
		this.progressBar = progress;
		this.message = message;
		this.rate = rate;
	}

	@Override
	public void run() {
		progressBar.setValue(rate);
		if (rate > 0) progressBar.setString(App.TITLE + " - " + message + " (" + rate + "%)");
		else progressBar.setString(App.TITLE + " - " + message);
	}

	@Override
	public void interrupt() {
		super.interrupt();

		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
		progressBar.setString("");
	}
}

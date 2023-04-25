package com.category.progress;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JProgessBarExample {
	public static void main(String[] args) {
		new JprogressBar();
	}
}

class JprogressBar extends JFrame implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	Container container = getContentPane();

	JLabel label = new JLabel("Progress Bar");
	JButton b1 = new JButton("시작");
	JButton b2 = new JButton("멈춤");
	JPanel panel = new JPanel(); // Button
	JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

	boolean progress = true;
	int num = 0;

	public JprogressBar() {
		setTitle("JProgreeBar");
		setSize(400, 250);
		setLocation(300, 300);
		init();
		start();
		setVisible(true);
	}

	private void init() {
		container.setLayout(new BorderLayout());
		container.add("North", label);
		container.add("Center", progressBar);
		container.add("South", panel);

		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(b1);
		panel.add(b2);

		// set ProgressBar
		progressBar.setStringPainted(true);
		progressBar.setString("0%");

	}

	private void start() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		b1.addActionListener(this);
		b2.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b1) {
			progress = true;
			new Thread(this).start();
			b1.setEnabled(false);
			b2.setEnabled(true);
		} else if (e.getSource() == b2) {
			progress = false;
			b1.setEnabled(true);
			b2.setEnabled(false);
		}
	}

	@Override
	public void run() {
		if (num == 100) {
			num = 0;
		}
		for (int i = num; i <= 100; i++) {
			if (!progress) break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			num = i;
			progressBar.setValue(i);
			progressBar.setString(i + "%");
		}
		b1.setEnabled(true);
		b2.setEnabled(false);
	}
}
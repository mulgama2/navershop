package com.category.about;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class About extends JDialog {

	public About() {
		setMinimumSize(new Dimension(480, 230));
		setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/com/category/resource/category.png")));
		setModal(true);
		setTitle("히트스캐너 Copyright©.Gama Company. All rights reserved.");
		setResizable(false);
		getContentPane().setLayout(null);

		JLabel label = new JLabel("히트스캐너");
		label.setFont(new Font("굴림", Font.BOLD, 12));
		label.setBounds(72, 23, 299, 15);
		getContentPane().add(label);

		JLabel lblNewLabel = new JLabel("Version 1.0.0");
		lblNewLabel.setBounds(72, 41, 299, 15);
		getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("히트스캐너 Copyright©.Gama Company. All rights reserved.");
		lblNewLabel_1.setBounds(31, 79, 420, 15);
		getContentPane().add(lblNewLabel_1);

		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBackground(new Color(240, 240, 240));
		textPane.setForeground(Color.GRAY);
		textPane.setFont(new Font("굴림", Font.PLAIN, 11));
		textPane.setText("이 컴퓨터 프로그램은 저작권법과 국제 협약의 보호를 받습니다. 이 프로그램의 전부 또는 일부를 무단으로 사용, 복제, 배포하는 행위는 민사 형사법에 의해 엄격히 규제 되어 있으며, 기소 사유가 됩니다.");
		textPane.setBounds(28, 97, 420, 50);
		getContentPane().add(textPane);
		
		JTextPane textPane2 = new JTextPane();
		textPane2.setEditable(false);
		textPane2.setBackground(new Color(240, 240, 240));
		textPane2.setForeground(Color.GRAY);
		textPane2.setFont(new Font("굴림", Font.PLAIN, 11));
		textPane2.setText("개발자 Email : wkgns21v@nate.com");
		textPane2.setBounds(28, 150, 420, 22);
		getContentPane().add(textPane2);

		ImageIcon ic = new ImageIcon(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/com/category/resource/category.png")));
		JLabel icon = new JLabel(ic);
		icon.setBounds(27, 23, 33, 33);
		getContentPane().add(icon);
	}

	private static final long serialVersionUID = 1L;

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		getContentPane().setLayout(null);
	}
}

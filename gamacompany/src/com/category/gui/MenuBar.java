package com.category.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.category.about.About;
import com.category.common.Common;
import com.category.common.HttpAPI;
import com.category.login.Login;
import com.category.password.PasswordChange;
import com.category.rankingexplorer.RankingExplorer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MenuBar {
	
	public static JMenuItem mntmLogin;
	public static JMenuItem mntmPasswordChange;
	public static JMenuItem mntmRankingExplorer;

	public JMenuBar createMenuBar() {
		JMenuBar jMenuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File    ");
		jMenuBar.add(mnFile);
		
		MenuBar.mntmLogin = new JMenuItem("로그인    ");
		mntmLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Login dialog = new Login();
				dialog.setResizable(false);
				dialog.init();
				dialog.setVisible(true);
			}
		});
		mntmLogin.setHorizontalAlignment(SwingConstants.LEFT);
		mnFile.add(mntmLogin);
		
		MenuBar.mntmPasswordChange = new JMenuItem("비밀번호 변경    ");
		mntmPasswordChange.setVisible(false);
		mntmPasswordChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PasswordChange dialog = new PasswordChange();
				dialog.setResizable(false);
				dialog.init();
				dialog.setVisible(true);
			}
		});
		mntmPasswordChange.setHorizontalAlignment(SwingConstants.LEFT);
		mnFile.add(mntmPasswordChange);

		JMenuItem mntmExit = new JMenuItem("종료    ");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, "종료하시겠습니까?", "히트스캐너",
						JOptionPane.YES_NO_OPTION) == 0) {
					System.exit(0);
				}
			}
		});
		mntmExit.setHorizontalAlignment(SwingConstants.LEFT);
		mnFile.add(mntmExit);
		
		
		JMenu mnFunction = new JMenu("기능    ");
		jMenuBar.add(mnFunction);
		
		MenuBar.mntmRankingExplorer = new JMenuItem("내 순위 추적기    ");
		MenuBar.mntmRankingExplorer.setEnabled(false);
		mntmRankingExplorer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RankingExplorer dialog = new RankingExplorer();
				dialog.setResizable(false);
				dialog.init();
				dialog.setVisible(true);
			}
		});
		mntmRankingExplorer.setHorizontalAlignment(SwingConstants.LEFT);
		mnFunction.add(mntmRankingExplorer);
		
		
		JMenu mnHelp = new JMenu("Help");
		jMenuBar.add(mnHelp);

		JMenuItem mntmUpdateYn = new JMenuItem();
		JSONArray json = HttpAPI.getUpdateYn();
		JSONObject obj = json.getJSONObject(0);
		if((obj.get("UPDATEYN")).equals("Y")) {
			//mntmUpdateYn = new JMenuItem("업데이트 있음 (" + obj.get("UPDATEDT") + ")");
			mntmUpdateYn.setText("업데이트 있음 (" + obj.get("UPDATEDT") + ")");
			
			mntmUpdateYn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String urlLink = Common.nvl(obj.get("URL"));
					try {
						Desktop.getDesktop().browse(new URI(urlLink));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
		} else mntmUpdateYn.setText("업데이트 없음");
		JMenuItem mntmAboutUrlCategory = new JMenuItem("About 히트스캐너");
		
		
		
		mntmAboutUrlCategory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				About dialog = new About();
				dialog.setResizable(false);
				dialog.init();
				dialog.setVisible(true);
			}
		});
		mntmUpdateYn.setHorizontalAlignment(SwingConstants.LEFT);
		mntmAboutUrlCategory.setHorizontalAlignment(SwingConstants.LEFT);
		mnHelp.add(mntmUpdateYn);
		mnHelp.add(mntmAboutUrlCategory);
		return jMenuBar;
	}
}

package com.category.file;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.category.common.DateUtil;

public class FileChooser {

	public File fileSaveDlg() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Microsoft Excel File (*.xlsx)", "xlsx"));
		fileChooser.setDialogTitle("히트스캐너 - 엑셀 저장 경로 선택");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setSelectedFile(new File("히트스캐너_" + DateUtil.now()));
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop")); // 바탕화면으로 경로 지정
		int userSelection = fileChooser.showSaveDialog(null);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String filePath = selectedFile.getPath();
			if (!filePath.toLowerCase().endsWith(".xlsx")) {
				filePath += ".xlsx";
				selectedFile = new File(filePath);
			}
			return selectedFile;
		}
		return null;
	}
}

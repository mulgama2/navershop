package com.category;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.chrome.ChromeDriver;

import com.category.about.About;
import com.category.cate.Category;
import com.category.cate.CategoryLoader;
import com.category.driver.Dirvers;
import com.category.excel.ExcelService;
import com.category.file.FileChooser;
import com.category.gui.MenuBar;
import com.category.log.LogPanel;
import com.category.progress.Progress;

public class App extends JFrame implements Serializable {

	public static final String TITLE = "가마 Company - 쇼핑 사이트 분석";

	private static App frame;
	private static final long serialVersionUID = -6487581342941361188L;
	private static ChromeDriver driver;
	private JFileChooser chooser;
	private JProgressBar progressBar;
	private Thread progressThread;
	private static File temp;
	private static final int RIGHT_MARGIN = 40;
	private static final String FONT_NAME = "dotum";
	private JComboBox<Object> comboBox = new JComboBox<>();
	private JComboBox<Object> comboBoxLv2 = new JComboBox<>();
	private JComboBox<Object> comboBoxLv3 = new JComboBox<>();
	private JComboBox<Object> comboBoxLv4 = new JComboBox<>();
	private CategoryLoader categoryLoader;

	public JRadioButton salesTypeRadioButton = new JRadioButton("전체유형");
	// public JRadioButton salesTypeRadioLocal = new JRadioButton("국내상품");
	public JRadioButton salesTypeRadioOverseas = new JRadioButton("해외직구");

	public JRadioButton sortTypeRadioRank = new JRadioButton("네이버 랭킹순");
	public JRadioButton sortTypeRadioLowPrice = new JRadioButton("낮은 가격순");
	public JRadioButton sortTypeRadioHighPrice = new JRadioButton("높은 가격순");
	public JRadioButton sortTypeRadioHighReview = new JRadioButton("리뷰 많은순");
	public JRadioButton sortTypeRadioLikeReview = new JRadioButton("리뷰 좋은순");
	public JRadioButton sortTypeRadioLatest = new JRadioButton("등록일순");

	public JRadioButton pagingRadio1 = new JRadioButton("1");
	public JRadioButton pagingRadio2 = new JRadioButton("2");
	public JRadioButton pagingRadio3 = new JRadioButton("3");
	public JRadioButton pagingRadio4 = new JRadioButton("4");
	public JRadioButton pagingRadio5 = new JRadioButton("5");

	public static JButton executeLabel;

	/**
	 * Create the frame.
	 */
	private App() {

		setMinimumSize(new Dimension(1500, 800));

		setTitle(TITLE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/com/category/resource/category.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 890, 580);

		setJMenuBar(new MenuBar().createMenuBar());
		progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		progressBar.setStringPainted(true); // 진행 상태를 문자열로 표시
		progressBar.setString("");
		progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 25));

		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("이미지 저장경로 선택");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		categoryLoader = new CategoryLoader();
		categoryLoader.readJsonFile();

//		카테고리 선택 테스트
//		List<Category> temps = categoryLoader.getCategoryByChild("50000205");
//		for (Category cat : temps) {
//			System.out.println(cat.getCatLv() + " " + cat.getCatId() + " " + cat.getCatNm());
//		}

		guiDesign();
		initMessage();
		pack();

		setLocationRelativeTo(null);
		setVisible(true);
		// chromeDriver();
		dispose();
		setProgress("프로그램 실행 준비가 완료 되었습니다.", 0);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Look And Feel
	 */
	public static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		frame = new App();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					if (driver != null) driver.quit();
					if (temp != null) temp.delete();
				} catch (Exception e2) {
					e2.printStackTrace();
				} finally {
					System.exit(0);
				}
			}
		});
	}

	private JComboBox<Object> categoryLv1() {
		Category data = new Category();
		data.setCatNm("- 대분류 -");
		comboBox.addItem(data);

		List<Category> categorys = categoryLoader.getCatetory1();
		for (Category category : categorys) {
			comboBox.addItem(category);
		}

		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Category category = (Category) comboBox.getSelectedItem();
				categoryLv2(category.getCatId());
			}
		});
		return comboBox;
	}

	private JComboBox<Object> categoryLv2(final String catId) {
		comboBoxLv2.removeAllItems();

		Category data = new Category();
		data.setCatNm("- 중분류 -");
		comboBoxLv2.addItem(data);

		List<Category> categorys = categoryLoader.getCatetory2();
		for (Category category : categorys) {
			if (category.getCatPid().equals(catId)) {
				comboBoxLv2.addItem(category);
			}
		}

		comboBoxLv2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Category category = (Category) comboBoxLv2.getSelectedItem();
				if (category == null) return;
				categoryLv3(category.getCatId());
			}
		});
		return comboBoxLv2;
	}

	private JComboBox<Object> categoryLv3(final String catId) {
		comboBoxLv3.removeAllItems();

		Category data = new Category();
		data.setCatNm("- 소분류 -");
		comboBoxLv3.addItem(data);

		List<Category> categorys = categoryLoader.getCatetory3();
		for (Category category : categorys) {
			if (category.getCatPid().equals(catId)) {
				comboBoxLv3.addItem(category);
			}
		}

		comboBoxLv3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Category category = (Category) comboBoxLv3.getSelectedItem();
				if (category == null) return;
				categoryLv4(category.getCatId());
			}
		});
		return comboBoxLv3;
	}

	private JComboBox<Object> categoryLv4(final String catId) {
		comboBoxLv4.removeAllItems();

		Category data = new Category();
		data.setCatNm("- 세분류 -");
		comboBoxLv4.addItem(data);

		List<Category> categorys = categoryLoader.getCatetory4();
		for (Category category : categorys) {
			if (category.getCatPid().equals(catId)) {
				comboBoxLv4.addItem(category);
			}
		}

		comboBoxLv4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Category category = (Category) comboBoxLv4.getSelectedItem();
				if (category == null) return;
			}
		});
		return comboBoxLv4;
	}

	/**
	 * GUI Design
	 */
	private void guiDesign() {

		JPanel searchTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel searchTitleLabel = new JLabel("검색 조건");
		searchTitleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
		searchTitlePanel.add(searchTitleLabel);

		FlowLayout fl_searchPanel = new FlowLayout(FlowLayout.LEFT);
		fl_searchPanel.setVgap(0);
		JPanel searchPanel = new JPanel(fl_searchPanel);
		JLabel titleLabel = new JLabel("카테고리");
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, RIGHT_MARGIN)); // 오른쪽 여백 설정
		searchPanel.add(titleLabel);

		searchPanel.add(categoryLv1());
		searchPanel.add(categoryLv2(null));
		searchPanel.add(categoryLv3(null));
		searchPanel.add(categoryLv4(null));

		FlowLayout fl_searchPanel2 = new FlowLayout(FlowLayout.LEFT);
		fl_searchPanel2.setVgap(0);
		JPanel searchPanel2 = new JPanel(fl_searchPanel2);
		JLabel salesTypeLabel = new JLabel("판매유형");
		salesTypeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, RIGHT_MARGIN)); // 오른쪽 여백 설정
		searchPanel2.add(salesTypeLabel);

		ButtonGroup salesTypeGroup = new ButtonGroup();
		salesTypeGroup.add(salesTypeRadioButton);
		// salesTypeGroup.add(salesTypeRadioLocal);
		salesTypeGroup.add(salesTypeRadioOverseas);
		salesTypeRadioButton.setSelected(true);

		searchPanel2.add(salesTypeRadioButton);
		// searchPanel2.add(salesTypeRadioLocal);
		searchPanel2.add(salesTypeRadioOverseas);

		FlowLayout fl_searchPanel3 = new FlowLayout(FlowLayout.LEFT);
		fl_searchPanel3.setVgap(0);
		JPanel searchPanel3 = new JPanel(fl_searchPanel3);

		JLabel sortTypeLabel = new JLabel("정렬순서");
		sortTypeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, RIGHT_MARGIN)); // 오른쪽 여백 설정
		searchPanel3.add(sortTypeLabel);

		ButtonGroup sortTypeGroup = new ButtonGroup();
		sortTypeGroup.add(sortTypeRadioRank);
		sortTypeGroup.add(sortTypeRadioLowPrice);
		sortTypeGroup.add(sortTypeRadioHighPrice);
		sortTypeGroup.add(sortTypeRadioHighReview);
		sortTypeGroup.add(sortTypeRadioLikeReview);
		sortTypeGroup.add(sortTypeRadioLatest);

		sortTypeRadioRank.setSelected(true);
		searchPanel3.add(sortTypeRadioRank);
		searchPanel3.add(sortTypeRadioLowPrice);
		searchPanel3.add(sortTypeRadioHighPrice);
		searchPanel3.add(sortTypeRadioHighReview);
		searchPanel3.add(sortTypeRadioLikeReview);
		searchPanel3.add(sortTypeRadioLatest);

		FlowLayout fl_searchPanel4 = new FlowLayout(FlowLayout.LEFT);
		fl_searchPanel4.setVgap(0);
		JPanel searchPanel4 = new JPanel(fl_searchPanel4);

		JLabel pagingTypeLabel = new JLabel("페이지수");
		pagingTypeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, RIGHT_MARGIN)); // 오른쪽 여백 설정
		searchPanel4.add(pagingTypeLabel);

		ButtonGroup pagingTypeGroup = new ButtonGroup();
		pagingTypeGroup.add(pagingRadio1);
		pagingTypeGroup.add(pagingRadio2);
		pagingTypeGroup.add(pagingRadio3);
		pagingTypeGroup.add(pagingRadio4);
		pagingTypeGroup.add(pagingRadio5);

		pagingRadio1.setSelected(true);
		searchPanel4.add(pagingRadio1);
		searchPanel4.add(pagingRadio2);
		searchPanel4.add(pagingRadio3);
		searchPanel4.add(pagingRadio4);
		searchPanel4.add(pagingRadio5);

		JPanel executePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		executeLabel = new JButton("엑셀 내려받기");
		executeLabel.setPreferredSize(new Dimension(200, 35));
		executeLabel.setFont(new Font("굴림", Font.PLAIN, 15));
		executeLabel.setIcon(new ImageIcon(App.class.getResource("/com/category/resource/excel_icon.png")));
		executeLabel.setEnabled(false);
		executePanel.add(executeLabel);
		excelBtn(executeLabel);

		JPanel searchPanelContainer = new JPanel();
		searchPanelContainer.setLayout(new BoxLayout(searchPanelContainer, BoxLayout.Y_AXIS));
		searchPanelContainer.add(searchTitlePanel);
		searchPanelContainer.add(searchPanel);
		searchPanelContainer.add(searchPanel2);
		searchPanelContainer.add(searchPanel3);
		searchPanelContainer.add(searchPanel4);
		searchPanelContainer.add(executePanel);

		JPanel resultTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel resultTitleLabel = new JLabel("검색 결과");
		resultTitleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
		resultTitlePanel.add(resultTitleLabel);

		JScrollPane scrollPane = new JScrollPane(new LogPanel().render());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel resultPanel = new JPanel(new BorderLayout());
		resultPanel.add(resultTitlePanel, BorderLayout.NORTH);
		resultPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(searchPanelContainer, BorderLayout.NORTH);
		mainPanel.add(resultPanel, BorderLayout.CENTER);
		mainPanel.add(progressBar, BorderLayout.PAGE_END);

		getContentPane().add(mainPanel);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * 엑셀 다운로드 버튼 클릭 이벤트
	 * 
	 * @param button
	 */
	public void excelBtn(JButton button) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cate = ((Category) comboBox.getSelectedItem()).toString();

				File file = new FileChooser().fileSaveDlg();
				if (file == null) return;

				new Thread(new Runnable() {
					public void run() {
						LogPanel.append("-----------------------------------------------------------------");
						LogPanel.append("※ 검색 조건에 해당하는 데이터를 추출하여 엑셀에 데이터 생성을 시작합니다.");
						LogPanel.append("1. 파일 저장 경로 :" + file.getAbsolutePath());

						setProgress("쇼핑 사이트 분석 진행 시작", 0);
						new ExcelService(frame, file, categoryLoader, getSelectedCategoryId(), progressBar).run();
					}
				}).start();
			}
		});
	}

	private String getSelectedCategoryId() {
		Category combo1 = (Category) comboBox.getSelectedItem();
		Category combo2 = (Category) comboBoxLv2.getSelectedItem();
		Category combo3 = (Category) comboBoxLv3.getSelectedItem();
		Category combo4 = (Category) comboBoxLv4.getSelectedItem();
		if (combo4.getCatId() != null) return combo4.getCatId();
		else if (combo3.getCatId() != null) return combo3.getCatId();
		else if (combo2.getCatId() != null) return combo2.getCatId();
		else if (combo1.getCatId() != null) return combo1.getCatId();
		return "";
	}

	public void setProgress(final String msg, final int rate) {
		progressThread = new Progress(progressBar, msg, rate);
		progressThread.start();
	}

	public void stopProgress() {
		progressBar.setValue(0);
		if (progressThread != null) progressThread.interrupt();
	}

	public static void sleep() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 프로그램 초기 로깅 메시지
	 */
	public void initMessage() {
		LogPanel.append("※ 프로그램이 실행 되었습니다.");
		LogPanel.append("1. 검색 조건을 설정 하세요.");
		LogPanel.append("2. 엑셀 내려받기 버튼을 클릭하세요.");
		LogPanel.append("3. 쇼핑 사이트 분석 내용을 저장하기 위한 엑셀 파일을 선택 하세요.");
		LogPanel.append("4. 프로그램이 오래 걸릴 수 있으니 잠시 기다려 주세요.");
		LogPanel.append("-----------------------------------------------------------------");
	}

	public void chromeDriver() {
		try {
			temp = File.createTempFile("chromedriver", ".exe");
		} catch (IOException e1) {
			e1.printStackTrace();
			LogPanel.append("에러가 발생 하였습니다." + e1.getMessage());
		}
		try (InputStream in = Dirvers.class.getResourceAsStream("chromedriver.exe"); FileOutputStream out = new FileOutputStream(temp);) {
			LogPanel.append("Chrome 임시 실행파일 생성 :\n" + temp.getAbsolutePath());

			IOUtils.copy(in, out);

			System.setProperty("webdriver.chrome.driver", temp.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			LogPanel.append("에러가 발생 하였습니다." + e.getMessage());
		}
	}
}

package com.category.excel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.category.App;
import com.category.cate.Category;
import com.category.common.Common;
import com.category.common.HttpAPI;
import com.category.log.LogPanel;
import com.category.progress.Progress;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExcelService {

	private App frame;
	private File file;
	private List<Category> categorys;
	private JProgressBar progressBar;
	private Thread progressThread;
	
	
	public ExcelService(final App frame, final File file, final List<Category> categorys, JProgressBar progressBar) {
		this.frame = frame;
		this.file = file;
		this.categorys = categorys;
		this.progressBar = progressBar;
	}
	
	public void stopBtn(JButton button, ExcelWriter writer) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(HttpAPI.exitFlag) return;
				if (JOptionPane.showConfirmDialog(null, "수집을 끝내시겠습니까?", "히트스캐너",
						JOptionPane.YES_NO_OPTION) == 0) {
					HttpAPI.exitFlag = true;
					App.stopLabel.setEnabled(false);
					LogPanel.append("-----------------------------------------------------------------");
					LogPanel.append("수집 종료 중입니다.");
					LogPanel.append("다운로드 완료 알림창이 나타날 때까지 잠시 기다려주세요.");
					LogPanel.append("-----------------------------------------------------------------");
				}
			}
		});
	}
	
	public void run() {
		ExcelWriter writer = null;
		App.executeLabel.setText("수집 중...");
		App.executeLabel.setEnabled(false);
		App.stopLabel.setEnabled(true);
		
		try {
			writer = new ExcelWriter(new FileOutputStream(file));
			writer.init();
			
			stopBtn(App.stopLabel, writer);

			createHeader(writer);
			int num = 0;
			
			if(this.frame.collectTypeRadioCategory.isSelected()) {
				LogPanel.append("2. 데이터 추출 시작 (추출 카테고리 : " + this.categorys.size() + ")");
				
				Loop1:
				for (int x = 0; x < this.categorys.size(); x++) {
					Category category = this.categorys.get(x);
					Map<String, String> param = getParam(category.getCatId());
					String p = param.get("pagingIndex");
					int pagingIndex = 0;
					int a = 0;
					int b = 0;
					int cnt = 0;
					if(!Common.isOrEquals(p, "1","2","3","4","5")) {
						String[] s = p.split(",");
						a = Integer.parseInt(s[0]) - 1;
						b = Integer.parseInt(s[1]);
						pagingIndex = Integer.parseInt(s[1]) - Integer.parseInt(s[0]) + 1;
					} else {
						a = 0;
						b = Integer.parseInt(p);
						pagingIndex = b;
					}
					for (int j = a; j < b ; j++) {
						if(HttpAPI.exitFlag) break Loop1;
						param.put("pagingIndex", "" + (j+1));
						LogPanel.append(String.format("   %s\t\t\t[%s]", category.getCatNm(), (j+1)+"페이지"));
						
						
						JSONArray result = HttpAPI.getCategoryByShoppingResult(param);
						if(result == null) continue;
						for (int i = 0; i < result.size(); i++) {
							JSONObject obj = result.getJSONObject(i);
							writer.add(parserData(obj, ++num));
						}
						setProgress("쇼핑 사이트 내용 추출", Common.rate((x+1), pagingIndex, (cnt+1), (this.categorys.size()*pagingIndex)));
						cnt++;
					}
				}
			} else if(this.frame.collectTypeRadioKeyword.isSelected()) {
				String keyword = this.frame.keywordField.getText();
				
				LogPanel.append("2. 데이터 추출 시작 (추출 키워드 : " + keyword + ")");
				
				Map<String, String> param = getParamKeyword(keyword);
				String p = param.get("pagingIndex");
				int pagingIndex = 0;
				int a = 0;
				int b = 0;
				int cnt = 0;
				if(!Common.isOrEquals(p, "1","2","3","4","5")) {
					String[] s = p.split(",");
					a = Integer.parseInt(s[0]) - 1;
					b = Integer.parseInt(s[1]);
					pagingIndex = Integer.parseInt(s[1]) - Integer.parseInt(s[0]) + 1;
				} else {
					a = 0;
					b = Integer.parseInt(p);
					pagingIndex = b;
				}
				Loop1:
				for (int j = a; j < b ; j++) {
					if(HttpAPI.exitFlag) break Loop1;
					param.put("pagingIndex", "" + (j+1));
					LogPanel.append(String.format("   %s\t\t\t[%s]", keyword, (j+1)+"페이지"));
					
					JSONArray result = HttpAPI.getKeywordByShoppingResult(param);
					for (int i = 0; i < result.size(); i++) {
						JSONObject obj = Common.toJSONObject(result.getJSONObject(i).get("item"));
						if(Common.nvl(obj.get("adcrUrl")).equals("")) {
							writer.add(parserData(obj, ++num));
						}
					}
					setProgress("쇼핑 사이트 내용 추출", Common.rateKeyword((j+1), pagingIndex));
					cnt++;
				}
			}
			
			LogPanel.append("3. 데이터 추출 진행 결과 : " + num + "건");
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (writer != null) writer.close();
			LogPanel.append("-----------------------------------------------------------------");
			LogPanel.append("수집이 완료 되었습니다.");
			LogPanel.append("-----------------------------------------------------------------");
			setProgress("쇼핑 사이트 내용 추출 완료", 0);
			JOptionPane.showMessageDialog(null, "다운로드가 완료 되었습니다.");
			HttpAPI.exitFlag = false;
			App.executeLabel.setEnabled(true);
			App.stopLabel.setEnabled(false);
			App.executeLabel.setText("엑셀 내려받기");
		}
	}

	/**
	 * 추출에 필요한 카테고리 선정 현재는 선택한 카테고리 아이디에서 바로 하위 카테고리의 내용만 추출
	 * 전체 카테고리를 다 추출해야 된다면 변경 필요.
	 * 
	 * @return
	 */
	/*
	public List<Category> getCategorys() {
		return categoryLoader.getCategoryByChild(catId);
	}
*/
	/**
	 * 쇼핑 사이트 검색을 위한 파라미터 값 추출
	 * 
	 * @return
	 */
	public Map<String, String> getParam(final String catId) {
		Map<String, String> param = new HashMap<>();
		param.put("catId", catId);
		param.put("frm", "NVSHCAT");
		param.put("isOpened", "true");
		param.put("origQuery", "");
		param.put("pagingIndex", getPagingVal());
		param.put("pagingSize", "40");
		param.put("productSet", getSalesTypeVal());
		param.put("query", "review");
		param.put("sort", getSortVal());
		param.put("viewType", "list");
		param.put("timestamp", "");
		return param;
	}
	
	/**
	 * 쇼핑 사이트 검색을 위한 파라미터 값 추출
	 * 
	 * @return
	 */
	public Map<String, String> getParamKeyword(String query) {
		Map<String, String> param = new HashMap<>();
		param.put("pagingIndex", getPagingVal());
		param.put("pagingSize", "40");
		param.put("productSet", getSalesTypeVal());
		param.put("query", query);
		param.put("sort", getSortVal());
		param.put("viewType", "list");
		param.put("timestamp", "");
		return param;
	}
	
	public List<Object> parserData(final JSONObject obj, final int num) {
		Object url;
		if(Common.isNotEmpty(obj.get("mallProductUrl"))) url= obj.get("mallProductUrl");
		else if(Common.isNotEmpty(obj.get("adcrUrl"))) url = obj.get("adcrUrl");
		else url = obj.get("crUrlMore");
		
		JSONObject info = new JSONObject();
		try {
			if(url.toString().contains("smartstore.naver.com")) {
				JSONObject json = HttpAPI.getDetailPage(url.toString());
				if(json != null) info = HttpAPI.getDetailInfo(json);
			} else info = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Object> data = new ArrayList<>();
		data.add(num);
		data.add(obj.get("category1Name"));
		data.add(obj.get("category2Name"));
		data.add(obj.get("category3Name"));
		data.add(obj.get("category4Name"));
		
		data.add(obj.get("rank")); // 순위
		if(Common.isNotEmpty(obj.get("mallName"))) data.add(obj.get("mallName")); // 스토어명
		else data.add("가격비교");
		
		String grade = Common.nvl(Common.toJSONObject(obj.get("mallInfoCache")).get("mallGrade"));
		if(Common.isEquals(grade, "M44001")) data.add("플래티넘");
		else if(Common.isEquals(grade, "M44002")) data.add("프리미엄");
		else if(Common.isEquals(grade, "M44003")) data.add("빅파워");
		else if(Common.isEquals(grade, "M44004")) data.add("파워");
		else if(Common.isEquals(grade, "M44005")) data.add("새싹");
		else if(Common.isEquals(grade, "M44006")) data.add("씨앗");
		else data.add("");
		
		//data.add(obj.get("imageUrl")); //이미지
		data.add(obj.get("productTitle")); // 상품명
		data.add(obj.get("price")); // 상품가격
		data.add(obj.get("openDate").toString().substring(0, 8)); // 등록일
		
		if(url.toString().contains("smartstore.naver.com") && (info != null)) {
			data.add(Common.nvz(info.get("cnt7")));
		} else data.add(0);
		//7일 판매량
		
		data.add(obj.get("purchaseCnt")); //6개월 판매량
		data.add(obj.get("reviewCount")); // 리뷰수
		data.add(obj.get("scoreInfo")); // 평점
		data.add(obj.get("keepCnt")); // 찜수
		
		if (Common.isEquals(obj.get("overseaTp"), "0")) data.add("국내"); // 판매유형
		else if (Common.isEquals(obj.get("overseaTp"), "1")) data.add("해외"); // 판매유형
		else data.add(""); // 판매유형
		
		
		if(url.toString().contains("smartstore.naver.com") && (info != null)) {
			data.add(Common.nvl(info.get("tag")).toString()); // 상품 태그
			data.add(Common.nvl(info.get("pageTitle")).toString()); // page title 태그
			data.add(Common.nvl(info.get("metaTag")).toString()); // 메타 태그
			String jejo = Common.nvl(info.get("jejo")).toString().toLowerCase();
			String jejoGroup = "";
			if(( jejo.contains("국산") && !jejo.contains("미국산") && !jejo.contains("중국산") && !jejo.contains("태국산") && !jejo.contains("영국산") ) || jejo.contains("한국") || jejo.contains("대한민국") || jejo.contains("코리아") || jejo.contains("korea")) {
				jejoGroup = "국산";
			} else if(jejo.contains("중국") || jejo.contains("china")) {
				jejoGroup = "중국";
			} else if(jejo.contains("일본") || jejo.contains("japan")) {
				jejoGroup = "일본";
			} else if(jejo.contains("미국") || jejo.contains("america")) {
				jejoGroup = "미국";
			} else if(jejo.contains("유럽") || jejo.contains("europe")) {
				jejoGroup = "유럽";
			} else jejoGroup = "기타";
			data.add(jejoGroup);
			data.add(Common.nvl(info.get("jejo")));
		} else {
			data.add("");
			data.add("");
			data.add("");
			data.add("");
			data.add("");
		}
		
		data.add(url.toString().contains("smartstore.naver.com") ? url : ""); //상품 url
		
		//if (Common.isEquals(obj.get("categoryLevel"), "1")) data.add(obj.get("category1Id"));
		//else if (Common.isEquals(obj.get("categoryLevel"), "2")) data.add(obj.get("category2Id"));
		//else if (Common.isEquals(obj.get("categoryLevel"), "3")) data.add(obj.get("category3Id"));
		//else if (Common.isEquals(obj.get("categoryLevel"), "4")) data.add(obj.get("category4Id"));
		//else data.add("");

		//data.add(obj.get("lowPrice")); // 최저가
		//data.add(obj.get("mobilePrice")); // 모바일상품가
		//data.add(obj.get("mobileLowPrice")); // 모바일최저가

		return data;
	}
	
	/**
	 * 엑셀 헤더
	 * 
	 * @param writer
	 */
	public void createHeader(ExcelWriter writer) {
		List<String> headers = new ArrayList<>();
		headers.add("순번");
		headers.add("대분류");
		headers.add("중분류");
		headers.add("소분류");
		headers.add("세분류");
		
		headers.add("순위");
		headers.add("스토어명");
		headers.add("판매자등급");
		
		//headers.add("대표이미지");
		headers.add("상품명");
		headers.add("상품가격");
		headers.add("등록일");
		headers.add("7일 판매량");
		headers.add("판매량");
		headers.add("리뷰수");
		headers.add("평점");
		headers.add("찜수");
		headers.add("판매유형");
		
		headers.add("태그");
		headers.add("Page Title 태그");
		headers.add("Meta Description 태그");
		headers.add("제조국");
		headers.add("상세 제조국");
		headers.add("상품 url");
		//headers.add("카테고리ID");

		//headers.add("상품가");
		//headers.add("최저가");
		//headers.add("모바일상품가");
		//headers.add("모바일최저가");
		writer.createHeader(headers);
	}

	/**
	 * 판매유형 검색 조건 국내 상품 조건 값을 모르겠음...
	 * 
	 * @return
	 */
	private String getSalesTypeVal() {
		if (frame.salesTypeRadioButton.isSelected()) return "total";
		//else if (frame.salesTypeRadioLocal.isSelected()) return "korea";
		else if (frame.salesTypeRadioOverseas.isSelected()) return "overseas";
		else return "total";
	}

	/**
	 * 검색에 필요한 필드의 값을 비교하여 값을 전달
	 * 
	 * @return
	 */
	private String getSortVal() {
		if (frame.sortTypeRadioRank.isSelected()) return "rel";
		else if (frame.sortTypeRadioLowPrice.isSelected()) return "price_asc";
		else if (frame.sortTypeRadioHighPrice.isSelected()) return "price_dsc";
		else if (frame.sortTypeRadioHighReview.isSelected()) return "review";
		else if (frame.sortTypeRadioLikeReview.isSelected()) return "review_rel";
		else if (frame.sortTypeRadioLatest.isSelected()) return "date";
		else return "rel";
	}
	
	private String getPagingVal() {
		if (frame.pagingRadio1.isSelected()) return "1";
		else if (frame.pagingRadio2.isSelected()) return "2";
		else if (frame.pagingRadio3.isSelected()) return "3";
		else if (frame.pagingRadio4.isSelected()) return "4";
		else if (frame.pagingRadio5.isSelected()) return "5";
		else if (frame.pagingRadio6.isSelected()) return frame.customPaging.getText() + "," + frame.customPaging2.getText();
		else return "1";
	}

	public void setProgress(final String msg, final int rate) {
		progressThread = new Progress(progressBar, msg, rate);
		progressThread.start();
	}

	public void stopProgress() {
		progressBar.setValue(0);
		if (progressThread != null) progressThread.interrupt();
	}
}

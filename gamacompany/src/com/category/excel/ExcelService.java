package com.category.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.category.App;
import com.category.cate.Category;
import com.category.cate.CategoryLoader;
import com.category.common.Common;
import com.category.common.HttpAPI;
import com.category.log.LogPanel;
import com.category.progress.Progress;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExcelService {

	private App frame;
	private File file;
	private CategoryLoader categoryLoader;
	private String catId;
	private JProgressBar progressBar;
	private Thread progressThread;

	public ExcelService(final App frame, final File file, CategoryLoader categoryLoader, final String catId, JProgressBar progressBar) {
		this.frame = frame;
		this.file = file;
		this.categoryLoader = categoryLoader;
		this.catId = catId;
		this.progressBar = progressBar;
	}
	
	
	
	
	public void run() {
		ExcelWriter writer = null;
		try {
			writer = new ExcelWriter(new FileOutputStream(file));
			writer.init();

			createHeader(writer);
			int num = 0;
			List<Category> categorys = getCategorys();
			LogPanel.append("2. 데이터 추출 시작 (추출 카테고리 : " + categorys.size() + ")");
			
			
			
			for (int x = 0; x < categorys.size(); x++) {
				Category category = categorys.get(x);
				Map<String, String> param = getParam(category.getCatId());
				LogPanel.append(String.format("   %s > %s", category.getCatNm(), HttpAPI.getUrl(HttpAPI.SHOPPING_URL, param)));

				JSONArray result = HttpAPI.getCategoryByShoppingResult(param);
				for (int i = 0; i < result.size(); i++) {
					JSONObject obj = result.getJSONObject(i);
					writer.add(parserData(obj, ++num));
				}
				
				setProgress("쇼핑 사이트 내용 추출", Common.rate(categorys.size(), (x + 1)));
			}
			
			
			
			
			LogPanel.append("3. 데이터 추출 진행 결과 : " + num + "건");
			LogPanel.append("-----------------------------------------------------------------");
			setProgress("쇼핑 사이트 내용 추출 완료", 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (writer != null) writer.close();

			JOptionPane.showMessageDialog(null, "다운로드가 완료 되었습니다.");
		}
	}
	
	
	
	
	
	

	/*
	public void run() {
		ExcelWriter writer = null;
		try {
			writer = new ExcelWriter(new FileOutputStream(file));
			writer.init();

			createHeader(writer);
			int num = 0;
			List<Category> categorys = getCategorys();
			LogPanel.append("2. 데이터 추출 시작 (추출 카테고리 : " + categorys.size() + ")");
			for (int x = 0; x < categorys.size(); x++) {
				Category category = categorys.get(x);
				Map<String, String> param = getParam(category.getCatId());
				LogPanel.append(String.format("   %s > %s", category.getCatNm(), HttpAPI.getUrl(HttpAPI.SHOPPING_URL, param)));

				//writer.createNewSheet(category.getCatNm().replaceAll("/","_"));
				JSONArray result = HttpAPI.getCategoryByShoppingResult(param);
				for (int i = 0; i < result.size(); i++) {
					JSONObject obj = result.getJSONObject(i);
					writer.add(parserData(obj, ++num));
				}
				
				setProgress("쇼핑 사이트 내용 추출", Common.rate(categorys.size(), (x + 1)));
			}
			LogPanel.append("3. 데이터 추출 진행 결과 : " + num + "건");
			LogPanel.append("-----------------------------------------------------------------");
			setProgress("쇼핑 사이트 내용 추출 완료", 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (writer != null) writer.close();

			JOptionPane.showMessageDialog(null, "다운로드가 완료 되었습니다.");
		}
	}
	*/

	/**
	 * 추출에 필요한 카테고리 선정 현재는 선택한 카테고리 아이디에서 바로 하위 카테고리의 내용만 추출
	 * 전체 카테고리를 다 추출해야 된다면 변경 필요.
	 * 
	 * @return
	 */
	public List<Category> getCategorys() {
		return categoryLoader.getCategoryByChild(catId);
	}

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
		param.put("pagingIndex", "1");
		param.put("pagingSize", "40");
		param.put("productSet", getSalesTypeVal());
		param.put("query", "review");
		param.put("sort", getSortVal());
		param.put("viewType", "list");
		param.put("timestamp", "");
		return param;
	}

	public List<Object> parserData(final JSONObject obj, final int num) {
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
		
		data.add(obj.get("productTitle")); // 상품명
		data.add(obj.get("price")); // 상품가격
		data.add(obj.get("openDate").toString().substring(0, 8)); // 등록일
		
		Object url;
		if(Common.isNotEmpty(obj.get("mallProductUrl"))) url= obj.get("mallProductUrl");
		else if(Common.isNotEmpty(obj.get("adcrUrl"))) url = obj.get("adcrUrl");
		else url = obj.get("crUrlMore");
		
		JSONObject info = new JSONObject();
		try {
			info = HttpAPI.getDetailInfo(HttpAPI.getDetailPage(url.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(url.toString().contains("smartstore.naver.com") && info != null) {
			data.add(info.get("cnt7"));
		} else data.add(0);
		//7일 판매량
		
		data.add(obj.get("purchaseCnt")); //6개월 판매량
		data.add(obj.get("reviewCount")); // 리뷰수
		data.add(obj.get("scoreInfo")); // 평점
		data.add(obj.get("keepCnt")); // 찜수
		
		if (Common.isEquals(obj.get("overseaTp"), "0")) data.add("국내"); // 판매유형
		else if (Common.isEquals(obj.get("overseaTp"), "1")) data.add("해외"); // 판매유형
		else data.add(""); // 판매유형
		
		
		if(url.toString().contains("smartstore.naver.com") && info != null) {
			data.add(info.get("tag").toString()); // 상품 태그
			data.add(info.get("pageTitle").toString()); // page title 태그
			data.add(info.get("metaTag").toString()); // 메타 태그
			data.add(info.get("jejo"));
		} else {
			data.add("");
			data.add("");
			data.add("");
			data.add("");
		}
	
		
		data.add(url); //상품 url
		
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
		else if (frame.salesTypeRadioLocal.isSelected()) return "???";
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

	public void setProgress(final String msg, final int rate) {
		progressThread = new Progress(progressBar, msg, rate);
		progressThread.start();
	}

	public void stopProgress() {
		progressBar.setValue(0);
		if (progressThread != null) progressThread.interrupt();
	}
}

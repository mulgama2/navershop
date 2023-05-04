package com.category.common;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HttpAPI {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Whale/3.20.182.12 Safari/537.36";
	public static final String SHOPPING_URL = "https://search.shopping.naver.com/api/search/category";
	private static final String SHOPPING_CATEGORY = "https://search.shopping.naver.com/api/filter/category-summary";
	private static final String REFERER_URL = "https://search.shopping.naver.com";
	private static final String ACCEPT = "application/json, text/plain, */*";
	private static final String ACCEPT_ENCODING = "gzip, deflate, br";
	private static final String ACCEPT_LANGUAGE = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
	private static final String SBTH = "50e96fa164c989c2e243ae4dfa1e259dc2ff015684eadc758d892b693b4c889d17a3d6a781b04b921f3224ad8754e5fc";
	private static final String LOGIC = "PART";
	
	public static JSONArray getArray(final String url, final Map<String, String> param) {
		return Common.toJSONArray(get(url, param));
	}

	public static JSONObject getObject(final String url, final Map<String, String> param) {
		return Common.toJSONObject(get(url, param));
	}
	

	public static String get(final String url, final Map<String, String> param) {
		try {
			SSLUtil.setSSL();
			//LogPanel.append("HttpAPI get : " + getUrl(url, param));
			String body = Jsoup.connect(getUrl(url, param)).timeout(5000)
			.header("referer", REFERER_URL)
			.header("logic", LOGIC)
			.header("sbth", SBTH)
			.header("accept", ACCEPT)
			.header("accept-encoding", ACCEPT_ENCODING)
			.header("accept-language", ACCEPT_LANGUAGE)
			.userAgent(USER_AGENT).ignoreContentType(true).execute().body();
			return body;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//sleep(); //비정상적인 요청 감지로 중간중간 쉬는 시간을 가짐
		}
		return null;
	}
	
	public static JSONObject getDetailPage(final String url) {
		if(!url.contains("smartstore.naver.com")) return null;
		String urlStr = url.replaceAll("smartstore.naver.com", "m.smartstore.naver.com");
		try {
			Document doc = Jsoup.connect(urlStr)
					.timeout(5000)
					.header("referer", REFERER_URL)
					.header("logic", LOGIC)
					.header("sbth", SBTH)
					.header("accept", ACCEPT)
					.header("accept-encoding", ACCEPT_ENCODING)
					.header("accept-language", ACCEPT_LANGUAGE)
					.userAgent(USER_AGENT).ignoreContentType(true)
			        .post();
			
			String str = doc.getElementsByTag("script").get(1).toString();
			if(!str.contains("productDeliveryLeadTimes")) return null;
			
			JSONObject obj = Common.toJSONObject(Common.toJSONObject(Common.toJSONObject(str
					.replaceAll("<script>window.__PRELOADED_STATE__=", "").replaceAll("</script>", "")).get("product")).get("A"));
			
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//sleep(); //비정상적인 요청 감지로 중간중간 쉬는 시간을 가짐
		}
		return null;
	}
	
	
	public static void loadCategoryJson(String[] args) {
		JSONArray result = new JSONArray();
		JSONArray arrayLv1 = getCategory("ROOT");
		for (int i = 0; i < arrayLv1.size(); i++) {
			JSONObject catLv1 = arrayLv1.getJSONObject(i);
			final String catNmLv1 = catLv1.getString("title");
			final String catIdLv1 = catLv1.getString("value");

			JSONArray findCategory2 = new JSONArray();
			JSONObject lv1 = new JSONObject();
			lv1.put("catLvl", 1);
			lv1.put("catId", catIdLv1);
			lv1.put("catNm", catNmLv1);
			
			JSONArray arrayLv2 = getCategory(catIdLv1);
			for (int j = 0; j < arrayLv2.size(); j++) {
				JSONObject catLv2 = arrayLv2.getJSONObject(j);
				final String catNmLv2 = catLv2.getString("title");
				final String catIdLv2 = catLv2.getString("value");

				JSONArray findCategory3 = new JSONArray();
				JSONObject lv2 = new JSONObject();
				lv2.put("catLvl", 2);
				lv2.put("catId", catIdLv2);
				lv2.put("catNm", catNmLv2);
				
				JSONArray arrayLv3 = getCategory(catIdLv2);
				for (int x = 0; x < arrayLv3.size(); x++) {
					JSONObject catLv3 = arrayLv3.getJSONObject(x);
					final String catNmLv3 = catLv3.getString("title");
					final String catIdLv3 = catLv3.getString("value");
					
					JSONArray findCategory4 = new JSONArray();
					JSONObject lv3 = new JSONObject();
					lv3.put("catLvl", 3);
					lv3.put("catId", catIdLv3);
					lv3.put("catNm", catNmLv3);
					
					JSONArray arrayLv4 = getCategory(catIdLv3);
					for (int z = 0; z < arrayLv4.size(); z++) {
						JSONObject catLv4 = arrayLv4.getJSONObject(z);
						final String catNmLv4 = catLv4.getString("title");
						final String catIdLv4 = catLv4.getString("value");
						JSONObject lv4 = new JSONObject();
						lv4.put("catLvl", 4);
						lv4.put("catId", catIdLv4);
						lv4.put("catNm", catNmLv4);
						findCategory4.add(lv4);
					}
					lv3.put("categories", findCategory4);
					findCategory3.add(lv3);
				}
				lv2.put("categories", findCategory3);
				findCategory2.add(lv2);
			}
			lv1.put("categories", findCategory2);
			result.add(lv1);
		}
		System.out.println(result);
	}
	
	public static JSONArray getCategory(final String catId) {
		Map<String, String> param = new HashMap<>();
		param.put("catId", catId);
		param.put("frm", "NVSHCAT");
		param.put("isOpened", "true");
		param.put("origQuery", "");
		param.put("pagingIndex", "1");
		param.put("pagingSize", "40");
		param.put("productSet", "total");
		param.put("query", "review");
		param.put("sort", "rel");
		param.put("viewType", "list");
		param.put("timestamp", "");

		JSONObject item = getObject(SHOPPING_CATEGORY, param);
		return item.getJSONArray("filterValues");
	}
	
	public static void sleep() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 쇼핑 사이트 검색 리스트
	 * 
	 * @param param
	 * @return
	 */
	public static JSONArray getCategoryByShoppingResult(Map<String, String> param) {
//		Map<String, String> param = new HashMap<>();
//		param.put("catId", "50000008");
//		param.put("eq", "");
//		param.put("iq", "");
//		param.put("pagingIndex", "1");
//		param.put("pagingSize", "40");
//		param.put("productSet", "total");
//		param.put("sort", "rel");
//		param.put("viewType", "list");
//		param.put("xq", "");
		JSONObject obj = getObject(SHOPPING_URL, param);
		return obj.getJSONObject("shoppingResult").getJSONArray("products");
	}
	
	
	public static boolean login(final String loginId, final String loginPw) {
		Map<String, String> param = new HashMap<>();
		param.put("callAjaxType", "M");
		param.put("path", "com.Login");
		param.put("mode", "login");
		param.put("userId", loginId);
		param.put("userPw", Common.sha256(loginPw));
		System.out.println(param);
		JSONObject obj = Common.toJSONObject(get("http://jjsalsa1.cafe24.com/call", param));
		return obj.getBoolean("success");
	}
	
	
	public static String getEndDate(final String loginId) {
		Map<String, String> param = new HashMap<>();
		param.put("callAjaxType", "Q");
		param.put("dbType", "mysql");
		param.put("queryId", "getStaffList");
		param.put("searchArea", "I");
		param.put("searchStr", "mulgama");
		JSONObject obj = Common.toJSONObject(get("http://jjsalsa1.cafe24.com/call", param));
		JSONArray data = obj.getJSONArray("data");
		if (data.size() > 0) {
			return Common.nvl(data.getJSONObject(0).get("END_DT"));
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> param = new HashMap<>();
		param.put("callAjaxType", "M");
		param.put("path", "com.Login");
		param.put("mode", "login");
		param.put("userId", "mulgama");
		param.put("userPw", Common.sha256("jjsalsa1!"));
		System.out.println(param);
		JSONObject obj = Common.toJSONObject(get("http://jjsalsa1.cafe24.com/call", param));
		System.out.println(obj.get("success"));
		//System.out.println(getDetailPage("https://smartstore.naver.com/kapoka1/products/4867811517"));
		
		//System.out.println(getDetailPage("https://smartstore.naver.com/kapoka1/products/4867811517"));
		
		//System.out.println(getDetailPage("https://smartstore.naver.com/springeyewear/products/5607985195"));
		/*
		 * try { getDetailInfo(getDetailPage(
		 * "https://smartstore.naver.com/main/products/8165121740")); } catch (Exception
		 * e) { e.printStackTrace(); }
		 */
		/*
		 * try { String body =
		 * Jsoup.connect("https://m.smartstore.naver.com/kapoka1/products/4867811517").
		 * timeout(5000) .header("referer", REFERER_URL) .header("logic", LOGIC)
		 * .header("sbth", SBTH) .header("accept", ACCEPT) .header("accept-encoding",
		 * ACCEPT_ENCODING) .header("accept-language", ACCEPT_LANGUAGE)
		 * .userAgent(USER_AGENT).ignoreContentType(true).execute().body();
		 * 
		 * System.out.println(body);
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		
		
		//loadCategoryJson(args);
		
//		Map<String, String> param = new HashMap<>();
//		param.put("catId", "50000008");
//		param.put("eq", "");
//		param.put("iq", "");
//		param.put("pagingIndex", "1");
//		param.put("pagingSize", "40");
//		param.put("productSet", "total");
//		param.put("sort", "rel");
//		param.put("viewType", "list");
//		param.put("xq", "");
//
//		JSONObject obj = getObject(SHOPPING_URL, param);
//		System.out.println(obj);
//		JSONObject shoppingResult = obj.getJSONObject("shoppingResult");
//		JSONArray products = shoppingResult.getJSONArray("products");
//		for (int i = 0; i < products.size(); i++) {
//			System.out.println(products.get(i));
//		}
	}
	
	public static String getUrl(final String url, Map<String, String> data) throws Exception {
		StringBuilder urlBuilder = new StringBuilder(url);
		urlBuilder.append("?");
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String key = URLEncoder.encode(entry.getKey(), "UTF-8");
			String value = URLEncoder.encode(entry.getValue(), "UTF-8");
			urlBuilder.append(key).append("=").append(value).append("&");
		}
		return urlBuilder.toString().substring(0, urlBuilder.toString().length() - 1);
	}
	
	public static JSONObject getDetailInfo(final JSONObject obj) throws Exception {
		if(obj == null) return null;
		JSONObject o = new JSONObject();
		
		//7일 판매량
		int cnt = 0;
		JSONArray json = Common.toJSONArray(obj.get("productDeliveryLeadTimes"));
		for (int i = 0; i < json.size(); i++) {
			cnt = cnt + Integer.parseInt(json.getJSONObject(i).get("leadTimeCount").toString());
		}
		
		//사용자 태그
		ArrayList<String> arr = new ArrayList<>();
		JSONArray json2 = Common.toJSONArray(Common.toJSONObject(obj.get("seoInfo")).get("sellerTags"));
		for (int i = 0; i < json2.size(); i++) {
			arr.add(json2.getJSONObject(i).get("text").toString());
		}
		String arr2 = String.join(" ", arr);
		
		//제조국
		String jejo = Common.toJSONObject(obj.get("originAreaInfo")).get("content").toString();
		
		o.put("cnt7", cnt);
		o.put("tag", arr2);
		o.put("pageTitle", Common.nvl(Common.toJSONObject(obj.get("seoInfo")).get("pageTitle")));
		o.put("metaTag", Common.nvl(Common.toJSONObject(obj.get("seoInfo")).get("metaDescription")));
		o.put("jejo", jejo);
		
		return o;
	}


}

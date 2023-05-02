package com.category.cate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.category.App;
import com.category.common.Common;
import com.category.log.LogPanel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CategoryLoader {
	private JSONArray cagegory;
	private List<Category> categoryAll;
	private List<Category> category1;
	private List<Category> category2;
	private List<Category> category3;
	private List<Category> category4;

	public CategoryLoader() {
		categoryAll = new ArrayList<>();
		category1 = new ArrayList<>();
		category2 = new ArrayList<>();
		category3 = new ArrayList<>();
		category4 = new ArrayList<>();
	}

	public List<Category> getCatetoryAll() {
		return categoryAll;
	}

	public List<Category> getCatetory1() {
		return category1;
	}

	public List<Category> getCatetory2() {
		return category2;
	}

	public List<Category> getCatetory3() {
		return category3;
	}

	public List<Category> getCatetory4() {
		return category4;
	}

	/**
	 * Read json file
	 * 
	 */
	public void readJsonFile() {
		LogPanel.append("-----------------------------------------------------------------");
		LogPanel.append("※ 전체 카테고리 정보를 로드하는 중 입니다.");
		//try (InputStream in = App.class.getResourceAsStream("./resource/category.json");) {
		try (InputStream in = App.class.getResourceAsStream("./resource/category.json");) {
			String jsonStr = IOUtils.toString(in, "UTF-8");
			cagegory = JSONArray.fromObject(jsonStr);
			for (int i = 0; i < cagegory.size(); i++) {
				JSONObject obj = cagegory.getJSONObject(i);
				Category catetory = new Category();
				catetory.setCatId(Common.nvl(obj.get("catId")));
				catetory.setCatIdLv1(catetory.getCatId());
				catetory.setCatNm(Common.nvl(obj.get("catNm")));
				catetory.setCatNmLv1(catetory.getCatNm());
				catetory.setCatLv(obj.getInt("catLvl"));
				category1.add(catetory);

				JSONArray categoryLv2 = obj.getJSONArray("categories");
				for (int j = 0; j < categoryLv2.size(); j++) {
					JSONObject itemLv2 = categoryLv2.getJSONObject(j);
					Category catetoryLv2 = new Category();
					catetoryLv2.setCatId(Common.nvl(itemLv2.get("catId")));
					catetoryLv2.setCatPid(catetory.getCatId());
					catetoryLv2.setCatNm(Common.nvl(itemLv2.get("catNm")));
					catetoryLv2.setCatLv(itemLv2.getInt("catLvl"));
					catetoryLv2.setCatIdLv1(catetory.getCatId());
					catetoryLv2.setCatNmLv1(catetory.getCatNm());
					catetoryLv2.setCatIdLv2(catetoryLv2.getCatId());
					catetoryLv2.setCatNmLv2(catetoryLv2.getCatNm());

					category2.add(catetoryLv2);

					JSONArray categoryLv3 = itemLv2.getJSONArray("categories");
					for (int x = 0; x < categoryLv3.size(); x++) {
						JSONObject itemLv3 = categoryLv3.getJSONObject(x);
						Category catetoryLv3 = new Category();
						catetoryLv3.setCatId(Common.nvl(itemLv3.get("catId")));
						catetoryLv3.setCatPid(catetoryLv2.getCatId());
						catetoryLv3.setCatNm(Common.nvl(itemLv3.get("catNm")));
						catetoryLv3.setCatLv(itemLv3.getInt("catLvl"));
						catetoryLv3.setCatIdLv1(catetory.getCatId());
						catetoryLv3.setCatNmLv1(catetory.getCatNm());
						catetoryLv3.setCatIdLv2(catetoryLv2.getCatId());
						catetoryLv3.setCatNmLv2(catetoryLv2.getCatNm());
						catetoryLv3.setCatIdLv2(catetoryLv3.getCatId());
						catetoryLv3.setCatNmLv2(catetoryLv3.getCatNm());

						category3.add(catetoryLv3);

						JSONArray categoryLv4 = itemLv3.getJSONArray("categories");
						for (int z = 0; z < categoryLv4.size(); z++) {
							JSONObject itemLv4 = categoryLv4.getJSONObject(z);
							Category catetoryLv4 = new Category();
							catetoryLv4.setCatId(Common.nvl(itemLv4.get("catId")));
							catetoryLv4.setCatPid(catetoryLv3.getCatId());
							catetoryLv4.setCatNm(Common.nvl(itemLv4.get("catNm")));
							catetoryLv4.setCatLv(itemLv4.getInt("catLvl"));
							catetoryLv4.setCatIdLv1(catetory.getCatId());
							catetoryLv4.setCatNmLv1(catetory.getCatNm());
							catetoryLv4.setCatIdLv2(catetoryLv2.getCatId());
							catetoryLv4.setCatNmLv2(catetoryLv2.getCatNm());
							catetoryLv4.setCatIdLv3(catetoryLv3.getCatId());
							catetoryLv4.setCatNmLv3(catetoryLv3.getCatNm());
							catetoryLv4.setCatIdLv4(catetoryLv4.getCatId());
							catetoryLv4.setCatNmLv4(catetoryLv4.getCatNm());
							category4.add(catetoryLv4);
						}
					}
				}
			}
			categoryAll.addAll(category1);
			categoryAll.addAll(category2);
			categoryAll.addAll(category3);
			categoryAll.addAll(category4);
			
			LogPanel.append("1. 전체 카테고리 : " + categoryAll.size() + "건");
			LogPanel.append("2. 대분류 카테고리 : " + category1.size() + "건");
			LogPanel.append("3. 중분류 카테고리 : " + category2.size() + "건");
			LogPanel.append("4. 소분류 카테고리 : " + category3.size() + "건");
			LogPanel.append("5. 세분류 카테고리 : " + category4.size() + "건");
			LogPanel.append("6. 카테고리 정보 로드 완료");
			LogPanel.append("-----------------------------------------------------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CategoryLoader loader = new CategoryLoader();
		loader.readJsonFile();
		//System.out.println(loader.findCategory(loader.cagegory, "50001487"));
		
		System.out.println(loader.getCategoryByChildrenOnlyLeaf("50000008"));
	}

	public JSONObject findCategory(JSONArray findCategory, final String catId) {
		for (int i = 0; i < findCategory.size(); i++) {
			JSONObject obj = findCategory.getJSONObject(i);
			if (Common.isEquals(obj.getString("catId"), catId)) return obj;
			if (obj.get("categories") != null) {
				JSONObject item = findCategory(obj.getJSONArray("categories"), catId);
				if (item != null) return item;
			}
		}
		return null;
	}


	/**
	 * 말단 노드 선택
	 * @param target
	 * @param result
	 */
	public void findLeafNode(final JSONArray target, final JSONArray result) {
		for (int i = 0; i < target.size(); i++) {
			JSONObject obj = target.getJSONObject(i);
			if (obj.get("categories") == null) { // 말단인경우
				result.add(obj);
			} else {
				JSONArray child = obj.getJSONArray("categories");
				if (child.isEmpty()) result.add(obj);
				else findLeafNode(child, result);
			}
		}
	}


	/**
	 * 주어진 카테고리 아이디에서 바로 한단계 하위 카테고리 목록 조회
	 * 
	 * @param catId
	 * @return
	 */
	public List<Category> getCategoryByChild(final String catId) {
		List<Category> result = new ArrayList<>();
		JSONArray categories = new JSONArray();
		if (Common.isOrEquals(catId, "ROOT", "")) categories = cagegory; // 선택하지 않은 경우 전체 카테고리
		else {
			JSONObject selectedObj = findCategory(cagegory, catId);
			
			if(selectedObj.get("categories") == null) {
				categories.add(selectedObj);
			} else {
				JSONArray selectedChild = selectedObj.getJSONArray("categories");
				if (selectedChild == null || (selectedChild.size() == 0)) { // 선택한 카테고리가 말단인 경우
					categories.add(selectedObj);
				} else { // 선택한 카테고리의 하위 카테고리가 있는 경우
					findLeafNode(selectedChild, categories);
				}
			}
			
		}

		for (int i = 0; i < categories.size(); i++) {
			JSONObject obj = categories.getJSONObject(i);
			Category catetory = new Category();
			catetory.setCatId(Common.nvl(obj.get("catId")));
			catetory.setCatNm(Common.nvl(obj.get("catNm")));
			catetory.setCatLv(obj.getInt("catLvl"));
			result.add(catetory);
		}
		return result;
	}

	public List<Category> getCategoryByChildrenOnlyLeaf(final String catId) {
		List<Category> result = new ArrayList<>();
		JSONObject targetCategory = findCategory(cagegory, catId);

		Category cat = new Category();
		cat.setCatId(Common.nvl(targetCategory.get("catId")));
		cat.setCatNm(Common.nvl(targetCategory.get("catNm")));
		cat.setCatLv(targetCategory.getInt("catLvl"));
		result.add(cat);

		return result;
	}

	public void loadCategory(JSONArray categories, List<Category> result) {
		for (int i = 0; i < categories.size(); i++) {
			JSONObject obj = categories.getJSONObject(i);

			Category catetory = new Category();
			catetory.setCatId(Common.nvl(obj.get("catId")));
			catetory.setCatNm(Common.nvl(obj.get("catNm")));
			catetory.setCatLv(obj.getInt("catLvl"));
			result.add(catetory);

			if (obj.get("categories") != null) {
				loadCategory(obj.getJSONArray("categories"), result);
			}
		}
	}
}

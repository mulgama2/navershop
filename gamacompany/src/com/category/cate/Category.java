package com.category.cate;

public class Category {
	private String catIdLv1;
	private String catIdLv2;
	private String catIdLv3;
	private String catIdLv4;
	private String catNmLv1;
	private String catNmLv2;
	private String catNmLv3;
	private String catNmLv4;

	private String catId;
	private String catPid;
	private String catNm;
	private int catLv;

	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}

	public String getCatPid() {
		return catPid;
	}

	public void setCatPid(String catPid) {
		this.catPid = catPid;
	}

	public String getCatNm() {
		return catNm;
	}

	public void setCatNm(String catNm) {
		this.catNm = catNm;
	}

	public int getCatLv() {
		return catLv;
	}

	public void setCatLv(int catLv) {
		this.catLv = catLv;
	}

	public String getCatIdLv1() {
		return catIdLv1;
	}

	public void setCatIdLv1(String catIdLv1) {
		this.catIdLv1 = catIdLv1;
	}

	public String getCatIdLv2() {
		return catIdLv2;
	}

	public void setCatIdLv2(String catIdLv2) {
		this.catIdLv2 = catIdLv2;
	}

	public String getCatIdLv3() {
		return catIdLv3;
	}

	public void setCatIdLv3(String catIdLv3) {
		this.catIdLv3 = catIdLv3;
	}

	public String getCatIdLv4() {
		return catIdLv4;
	}

	public void setCatIdLv4(String catIdLv4) {
		this.catIdLv4 = catIdLv4;
	}

	public String getCatNmLv1() {
		return catNmLv1;
	}

	public void setCatNmLv1(String catNmLv1) {
		this.catNmLv1 = catNmLv1;
	}

	public String getCatNmLv2() {
		return catNmLv2;
	}

	public void setCatNmLv2(String catNmLv2) {
		this.catNmLv2 = catNmLv2;
	}

	public String getCatNmLv3() {
		return catNmLv3;
	}

	public void setCatNmLv3(String catNmLv3) {
		this.catNmLv3 = catNmLv3;
	}

	public String getCatNmLv4() {
		return catNmLv4;
	}

	public void setCatNmLv4(String catNmLv4) {
		this.catNmLv4 = catNmLv4;
	}

	@Override
	public String toString() {
		return catNm;
	}
}

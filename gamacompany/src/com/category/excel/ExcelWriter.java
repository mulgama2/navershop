package com.category.excel;

import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.category.common.Common;

public class ExcelWriter {
	private SXSSFWorkbook workbook;
	private Sheet sheet;
	private OutputStream out;
	private boolean first = true;
	private CellStyle hyperLinkStyle;

	public ExcelWriter(final OutputStream out) {
		this.out = out;
	}

	public void init() {
		this.workbook = new SXSSFWorkbook(10);
		this.workbook.setCompressTempFiles(false);
		this.sheet = this.workbook.createSheet("가마컴퍼니");
		this.sheet.createFreezePane(0, 1);
		this.sheet.setAutoFilter(CellRangeAddress.valueOf("A1:V1"));
		setStyle();
	}
	
	public void createHeader(List<String> headers) {
		Row row = nextRow();
		for (int i = 0; i < headers.size(); i++) {
			row.createCell(i).setCellValue(this.workbook.getCreationHelper().createRichTextString(headers.get(i)));
		}
	}
	
	public void setStyle() {
		this.hyperLinkStyle = this.workbook.createCellStyle();
		Font hyperLinkFont = this.workbook.createFont();
		hyperLinkFont.setUnderline(Font.U_SINGLE);
		hyperLinkFont.setColor(IndexedColors.BLUE.getIndex());
		hyperLinkStyle.setFont(hyperLinkFont);
	}
	
	public void add(List<Object> data) {
		Row row = nextRow();
		
		for (int i = 0; i < data.size(); i++) {
			String obj = Common.nvl(data.get(i));
			RichTextString val = this.workbook.getCreationHelper().createRichTextString(obj);
			Cell cell = row.createCell(i);
			
			switch (i) {
			case 0:
			case 5:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				if(!val.toString().equals("")) cell.setCellFormula("VALUE(\"" + val.toString() + "\")");
				break;
			case 21: 
				if(!val.toString().equals("")) {
					cell.setCellFormula("HYPERLINK(\"" + val + "\", \"" + val + "\")");
					cell.setCellStyle(this.hyperLinkStyle);
				}
				break;
			default:
				cell.setCellValue(this.workbook.getCreationHelper().createRichTextString(obj));
				break;
			}
		}
	}

	private Row nextRow() {
		if(first) {
			first = false;
			return this.sheet.createRow(this.sheet.getLastRowNum());
		}
		return this.sheet.createRow(this.sheet.getLastRowNum() + 1);
	}

	public void close() {
		try {
			this.workbook.write(this.out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			if (this.workbook != null) this.workbook.dispose();
		}
	}
}

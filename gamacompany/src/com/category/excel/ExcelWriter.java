package com.category.excel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
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
		this.workbook = new SXSSFWorkbook(50000);
		this.workbook.setCompressTempFiles(false);
		this.sheet = this.workbook.createSheet("가마컴퍼니");
		this.sheet.createFreezePane(0, 1);
		this.sheet.setAutoFilter(CellRangeAddress.valueOf("A1:W1"));
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
	
	public void insertImageToCell(int rowNum, int cellNum, String imageUrl) throws IOException {
		
		URL img = new URL(imageUrl);
		InputStream is = new BufferedInputStream(img.openStream());
		byte[] inputImageBytes = IOUtils.toByteArray(is);
		int pictureIdx = workbook.addPicture(inputImageBytes, Workbook.PICTURE_TYPE_JPEG);
	    is.close();
	    
	    CreationHelper helper = this.workbook.getCreationHelper();
        Drawing drawing = this.sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();
        
        // 이미지를 출력할 CELL 위치 선정
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        anchor.setCol1(cellNum);
        anchor.setRow1(rowNum);
        anchor.setCol2(cellNum);
        anchor.setRow2(rowNum);
        anchor.setDx1(Units.pixelToEMU(0));
        anchor.setDy1(Units.pixelToEMU(0));
        anchor.setDx2(Units.pixelToEMU(30));
        anchor.setDx2(Units.pixelToEMU(25));
        //anchor.setDx1(2 * XSSFShape.EMU_PER_PIXEL);
        //anchor.setDy1(2 * XSSFShape.EMU_PER_PIXEL);
        
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        
        // 이미지 사이즈 비율 설정
        pict.resize(1);
	    
    }
	
	public void add(List<Object> data) throws IOException {
		Row row = nextRow();
		
		for (int i = 0; i < data.size(); i++) {
			String obj = Common.nvl(data.get(i));
			RichTextString val = this.workbook.getCreationHelper().createRichTextString(obj);
			Cell cell = row.createCell(i);
			
			switch (i) {
			/*case 8:
				row.setHeight((short)700);
				this.sheet.setColumnWidth(i, (short)2700);
				insertImageToCell(row.getRowNum(), cell.getColumnIndex(), obj);
				break;
			*/
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
			case 22: 
				if(!val.toString().equals("")) {
					cell.setCellFormula("HYPERLINK(\"" + val + "\", \"" + val + "\")");
					cell.setCellStyle(this.hyperLinkStyle);
					this.sheet.setColumnWidth(i, (short)7000);
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

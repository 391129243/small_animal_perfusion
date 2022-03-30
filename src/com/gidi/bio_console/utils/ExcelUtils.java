package com.gidi.bio_console.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import android.util.Log;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class ExcelUtils {
	private WritableWorkbook wwb = null;
	private WritableSheet sheet;
	private File excelFile;
	private String mFileName;
	private WritableFont arial14font = null;

	private WritableCellFormat arial14format = null;
	private WritableFont arial10font = null;
	private WritableCellFormat arial10format = null;
	private WritableFont arial12font = null;
	private WritableCellFormat arial12format = null;

	public final static String UTF8_ENCODING = "UTF-8";
	public final static String GBK_ENCODING = "GBK";


	public void format() {
		try {
			arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
			arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
			arial14format = new WritableCellFormat(arial14font);
			arial14format.setAlignment(jxl.format.Alignment.CENTRE);
			arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
			arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);
			arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
			arial10format = new WritableCellFormat(arial10font);
			arial10format.setAlignment(jxl.format.Alignment.CENTRE);
			arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
			arial10format.setBackground(jxl.format.Colour.LIGHT_BLUE);
			arial12font = new WritableFont(WritableFont.ARIAL, 12);
			arial12format = new WritableCellFormat(arial12font);
			arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
		}
		catch (WriteException e) {

			e.printStackTrace();
		}
	}

	/**创建excel文件**/
	public void createExcel(File dirpath, String fileName) {
		
		try {
			mFileName = fileName;
			excelFile = new File(dirpath,fileName);
			if (!excelFile.exists()) {
				excelFile.createNewFile();
			}else {
				//删除重新生成
				excelFile.delete();
				excelFile.createNewFile();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> boolean  saveDataToExcel(String[]excelTitle,List<T> objList) {
		boolean result = false;
		format();
		if (objList != null && objList.size() > 0) {
			try {

				wwb = Workbook.createWorkbook(excelFile);

				sheet = wwb.createSheet("perfusion_record", 0);

				//写入标题
				sheet.addCell((WritableCell) new Label(0, 0,mFileName , arial14format));

				
				for (int i = 0; i < excelTitle.length; i++) {
					/**Label(x,y,z)其中x代表单元格的第x+1列，第y+1行, 单元格的内容是y
					 * 在Label对象的子对象中指明单元格的位置和内容
					 * */
					Label titlelabel;
					titlelabel = new Label(i, 0, excelTitle[i],arial10format);
					/**将定义好的单元格添加到工作表中*/
					sheet.addCell(titlelabel);
				}
				

				/**写入数据*/
				for (int j = 0; j < objList.size(); j++) {
					String[] contentList =(String[]) objList.get(j);
					for (int i = 0; i < contentList.length; i++) {

						sheet.addCell(new Label(i, j+1, contentList[i], arial12format));
					}
				}
				wwb.write();
				Log.i("PerfusionLogActivity", " success----");
				result= true;
			}
			catch (Exception e) {
				e.printStackTrace();
				result= false;
			}
			finally {
				if (wwb != null) {
					try {
						wwb.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}
				if(objList != null){
					objList.clear();
					objList = null;
				}
			}

		}
		return result;
	}

	public static Object getValueByRef(Class cls, String fieldName) {
		Object value = null;
		fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
		String getMethodName = "get" + fieldName;
		try {
			Method method = cls.getMethod(getMethodName);
			value = method.invoke(cls);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}

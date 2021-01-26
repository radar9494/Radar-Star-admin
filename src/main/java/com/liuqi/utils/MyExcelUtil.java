package com.liuqi.utils;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.liuqi.base.BaseConstant;
import com.liuqi.exception.BusinessException;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyExcelUtil {

	/**
	 * 导出数据
	 * @param dataList
	 * @param response
	 * @param defaultExportName
	 * @param headers
	 * @param columns
	 */
	public static void export(List dataList, HttpServletResponse response, String defaultExportName, String[] headers, String[] columns) {
		ExcelWriter writer = ExcelUtil.getWriter();
		try {
			Map<String, String> head = new LinkedHashMap<>();
			for (int i = 0; i < columns.length; i++) {
				head.put(columns[i], headers[i]);
			}
			writer.setHeaderAlias(head);

			List<Map> dataMapList=new ArrayList<>();
			Map<String, String> temp = new HashMap<String, String>();
			for (Object obj : dataList) {
				Map<String, String> data = new LinkedHashMap<String, String>();
				temp.clear();
				temp = BeanUtils.describe(obj);
				for (int i = 0; i < columns.length; i++) {
					data.put(columns[i], temp.get(columns[i]));
				}
				dataMapList.add(data);
			}

			int total=dataList.size();
			int base= BaseConstant.EXPORT_COUNT;
			int sheetTotal=total%base==0?total/base:total/base+1;
			int start=0;
			int end=base;
			for(int sheet=0;sheet<sheetTotal;sheet++){
				writer.setSheet(sheet);
				start=sheet*base;
				end=start+base;
				if(end>total){
					end=total;
				}
				if(end>start) {
					writer.write(dataMapList.subList(start, end), true);
				}
			}
			response.reset();
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + new String((defaultExportName + DateTimeUtils.currentDate("yyyy-MM-dd") + ".xls").getBytes(), StandardCharsets.ISO_8859_1));
			ServletOutputStream out = response.getOutputStream();
			writer.flush(out);
		} catch (Exception e) {
			throw new BusinessException("导出失败" + e.getMessage());
		} finally {
			/*if (writer != null) {
				writer.close();
			}*/
		}
	}
}

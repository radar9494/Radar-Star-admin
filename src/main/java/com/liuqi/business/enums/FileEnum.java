package com.liuqi.business.enums;

import com.alibaba.fastjson.JSONArray;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FileEnum {

	PIC("图片", 1, Arrays.asList("png","jpg","gif","bmp")),
	FILE("文件", 2,Arrays.asList("txt","rar","zip","pdf","xls","xlsx","doc","ppt")),
	VEDIO("视频", 3, Arrays.asList("rm","rmvb","mpg","mpeg","avi","wmv","mov")),
	MUSIC("音频", 4, Arrays.asList("mp3","wav","wma","mid"));

	private String name;
	private Integer code;
	private List<String> suffixs;
	FileEnum(String name, int code, List<String> suffixs) {
		this.name = name;
		this.code = code;
		this.suffixs = suffixs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<String> getSuffixs() {
		return suffixs;
	}

	public void setSuffixs(List<String> suffixs) {
		this.suffixs = suffixs;
	}

	public static FileEnum getCode(Integer code) {
		if (code != null) {
			for (FileEnum e : FileEnum.values()) {
				if (e.getCode().equals(code)) {
					return e;
				}
			}
		}
		return null;
	}
	public static String getName(Integer code) {
		if (code != null) {
			for (FileEnum e : FileEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (FileEnum e : FileEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

	public static boolean valid(FileEnum fileEnum, String suffix){
		boolean valid=false;
		if(StringUtils.isNotEmpty(suffix) && fileEnum !=null) {
			if(fileEnum.getSuffixs().contains(suffix.toLowerCase())){
				valid=true;
			}else{
				throw new BusinessException("不支持文件,"+ JSONArray.toJSONString(fileEnum.getSuffixs()));
			}
		}else{
			throw new BusinessException("文件异常:参数异常");
		}
		return valid;
	}

}

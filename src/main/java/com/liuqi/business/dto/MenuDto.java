package com.liuqi.business.dto;

import com.liuqi.business.model.PermissionModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuDto implements Serializable {

	private Long id;

	private String title;

	private String icon;
	/**
	 * 是否选择
	 */
	private String target="_self";

	private String href;

	private boolean selected;

	private Long parentId;
	/**
	 * 下级
	 */
	private List<MenuDto> child=new ArrayList<MenuDto>();
	//权限
	private List<PermissionModel> permissions=new ArrayList<>();
}

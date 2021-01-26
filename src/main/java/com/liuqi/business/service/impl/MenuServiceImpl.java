package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.MenuDto;
import com.liuqi.business.mapper.MenuMapper;
import com.liuqi.business.model.MenuModel;
import com.liuqi.business.model.MenuModelDto;
import com.liuqi.business.model.UserAdminModel;
import com.liuqi.business.service.MenuService;
import com.liuqi.business.service.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class MenuServiceImpl extends BaseServiceImpl<MenuModel, MenuModelDto> implements MenuService {

	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private UserAdminService userAdminService;

	@Override
	public BaseMapper<MenuModel, MenuModelDto> getBaseMapper() {
		return this.menuMapper;
	}



	@Override
	public List<MenuModelDto> getInfoByRoleId(Long roleId) {
		return menuMapper.getInfoByRoleId(roleId);
	}

	@Override
	public List<Long> getByRoleId(Long roleId) {
		return menuMapper.getByRoleId(roleId);
	}


	@Override
	public List<MenuModelDto> findByParentId(Long parentId) {
		return menuMapper.findByParentId(parentId);
	}


	@Override
	public List<MenuModelDto> findFirstLevel() {
		return menuMapper.findFirstLevel();
	}

	@Override
	public List<MenuDto> getAllMenuAndSelect(Long roleId) {
		//返回数据
		List<MenuDto> list = new ArrayList<MenuDto>();
		//查询一级菜单
		List<MenuModelDto> firstMenus = this.findFirstLevel();
		Map<String,Object> params=new HashMap<>();
		if (firstMenus != null && firstMenus.size() > 0) {
			//查询角色关联的数据
			List<Long> selecteds = this.getByRoleId(roleId);
			for (MenuModelDto m : firstMenus) {
				//判断一级菜单是否选择
				MenuDto dto = getMenuDto(m, selecteds.contains(m.getId()));

				params.put("parentId",m.getId());
				//查询下级
				List<MenuModelDto> childs = this.queryMenuAndPermission(params);
				List<MenuDto> clist = new ArrayList<MenuDto>();
				if (childs != null && childs.size() > 0) {
					for (MenuModelDto cm : childs) {
						MenuDto cdto = getMenuDto(cm, selecteds.contains(cm.getId()));
						clist.add(cdto);
					}
				}
				dto.setChild(clist);
				list.add(dto);
			}
		}
		return list;
	}


	private MenuDto getMenuDto(MenuModelDto menu, boolean seleted) {
		MenuDto dto = new MenuDto();
		dto.setId(menu.getId());
		dto.setParentId(menu.getParentId());
		dto.setTitle(menu.getName());
		dto.setSelected(seleted);
		if(menu.getPermissions()!=null) {
			dto.setPermissions(menu.getPermissions());
		}
		return dto;
	}

	/**
	 * 查询用户菜单
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<MenuDto> getByUserId(Long userId) {
		//返回数据
		List<MenuDto> list = new ArrayList<MenuDto>();
		UserAdminModel user = userAdminService.getById(userId);
		if (user == null || user.getRoleId() == null) {
			return list;
		}
		//查询一级菜单
		List<MenuModelDto> firstMenus = this.findFirstLevel();
		if (firstMenus != null && firstMenus.size() > 0) {
			//查询角色关联的数据
			List<Long> selecteds = this.getByRoleId(user.getRoleId());
			for (MenuModel m : firstMenus) {
				//包含一级菜单
				MenuDto dto = new MenuDto();
				if (selecteds.contains(m.getId())) {
					dto.setId(m.getId());
					dto.setTitle(m.getName());
					dto.setHref("");
					dto.setIcon("fa fa-window-maximize");
					//查询下级
					List<MenuModelDto> childs = this.findByParentId(m.getId());
					List<MenuDto> clist = new ArrayList<MenuDto>();
					if (childs != null && childs.size() > 0) {
						for (MenuModel cm : childs) {
							if (selecteds.contains(cm.getId())) {
								MenuDto cdto = new MenuDto();
								cdto.setId(cm.getId());
								cdto.setTitle(cm.getName());
								cdto.setHref(cm.getUrl());
								cdto.setIcon("fa fa-file-text");
								clist.add(cdto);
							}
						}
					}
					dto.setChild(clist);
					list.add(dto);
				}
			}
		}
		return list;
	}

	/**
	 * 获取菜单权限
	 * @param map
	 * @return
	 */
	@Override
	public List<MenuModelDto> queryMenuAndPermission(Map<String,Object> map){
		return menuMapper.queryMenuAndPermission(map);
	}

	@Override
	public List<MenuModelDto> getAllChild() {
		return menuMapper.getAllChild();
	}
}

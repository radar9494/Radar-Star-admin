package com.liuqi.base;

import java.util.List;
import java.util.Map;


public interface BaseMapper<T extends BaseModel, DTO extends T> {
	/**
	 * 新增
	 * @param t
	 */
    void insert(T t);
	/**
	 * 修改
	 * @param t
	 */
    int update(T t);
	/**
	 * 获取对象
	 * @param ids
	 * @return
	 */
	List<DTO> getByIds(List<Long> ids);
	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	DTO getById(Long id);
	
	/**
	 * 查询
	 * @param dto
	 * @return
	 */
	List<DTO> queryList(DTO dto);
}

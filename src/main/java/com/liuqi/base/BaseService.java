package com.liuqi.base;

import com.github.pagehelper.PageInfo;
import com.liuqi.response.DataResult;

import java.util.List;
import java.util.Map;

/**
 * 基础service
 * @author tanyan
 * 2017-8-3 下午3:55:13
 * @param <T>
 */
public interface BaseService<T extends BaseModel,TDO extends T> {

	/**
	 * 新增
	 * @param t
	 */
    void insert(T t);
	/**
	 * 修改
	 * @param newT
	 */
	boolean update(T newT);

	/**
	 * 获取对象
	 * @param ids
	 * @return
	 */
    List<TDO> getByIds(List<Long> ids);

	/**
	 *获取对象 key为对象id
	 * @param ids
	 * @return
	 */
	Map<String,TDO> getMapByIds(List<Long> ids);
	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	TDO getById(Long id, boolean doModel);
	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	TDO getById(Long id);

	/**
	 * 查询列表
	 * @param searchDto
	 * @param doModel 是否组装dto数据
	 * @return
	 */
	List<TDO> queryListByDto(TDO searchDto, boolean doModel);

	/**
	 * 查询列表
	 * @param searchDto
	 * @return
	 */
	DataResult<TDO> queryPageByDto(TDO searchDto, Integer pageNum, Integer pageSize);
	/**
	 * 查询列表
	 * @param searchDto
	 * @return
	 */
	PageInfo<TDO> queryFrontPageByDto(TDO searchDto, Integer pageNum, Integer pageSize);
	/**
	 * 清除缓存
	 */
	void cleanAllCache();

	void cleanCacheByModel(T t);
}

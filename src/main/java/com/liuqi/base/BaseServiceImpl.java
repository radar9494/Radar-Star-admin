package com.liuqi.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liuqi.business.model.LoggerModel;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.service.LoggerService;
import com.liuqi.exception.BusinessException;
import com.liuqi.response.DataResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 抽象服务实现类
 * @author tanyan
 * 2017-8-3 下午4:16:55
 * @param <T>
 */
public abstract class BaseServiceImpl<T extends BaseModel, TDO extends T> implements BaseService<T, TDO> {

	@Autowired
	public LoggerService loggerService;
	/**
	 * 获取查询Mapper  由实现类去提供
	 * @return
	 */
	public abstract BaseMapper<T, TDO> getBaseMapper();

	/**
	 * 获取当前操作的后台管理员id
	 *
	 * @return
	 */
	public LoggerModel getLogger() {
		return BaseAdminController.opeLogger.get();
	}
	/**
	 * 新增
	 * @param t
	 */
	@Override
	@Transactional
	public void insert(T t){
		if(t.getCreateTime()==null){
			t.setCreateTime(new Date());
		}
		if(t.getUpdateTime()==null){
			t.setUpdateTime(new Date());
		}
		if(t.getVersion()==null){
			t.setVersion(0);
		}
		if(StringUtils.isNotEmpty(t.getRemark()) && t.getRemark().length()>200){
			t.setRemark(t.getRemark().substring(0,200));
		}
		/**
		 * 检测
		 */
		this.beforeAddCheck(t);
		cleanAllCache();
		this.getBaseMapper().insert(t);
		/**
		 * 成功之后操作
		 */
		this.afterAddOperate(t);

		this.addLogger();
	}

	/**
	 * 修改
	 * @param newT
	 */
	@Override
	@Transactional
	public boolean update(T newT){
		if(StringUtils.isNotEmpty(newT.getRemark()) && newT.getRemark().length()>200){
			newT.setRemark(newT.getRemark().substring(0,200));
		}
		newT.setUpdateTime(new Date());
		this.beforeUpdateCheck(newT);
		boolean status= this.getBaseMapper().update(newT) > 0;
		if(status) {
			cleanAllCache();
			cleanCacheByModel(newT);
			/**
			 * 成功之后操作
			 */
			this.afterUpdateOperate(newT);
			this.addLogger();
			return status;
		}else{
			throw new BusinessException(newT.getClass().getName()+"-id:"+newT.getId()+"更新失败");
		}
	}
	/**
	 * 获取对象
	 * @param ids
	 * @return
	 */
	@Override
	public List<TDO> getByIds(List<Long> ids) {
		if(ids==null || ids.size()==0){
			return null;
		}
		List<TDO> list=this.getBaseMapper().getByIds(ids);
		if(list!=null && list.size()>0){
			for(TDO dto:list){
				if(dto!=null) {
					this.doMode(dto);
				}
			}
		}
		return list;
	}
	/**
	 *获取对象 key为对象id
	 * @param ids
	 * @return
	 */
	@Override
	public Map<String,TDO> getMapByIds(List<Long> ids){
		List<TDO> list=this.getByIds(ids);
		Map<String,TDO> map=new HashMap<String,TDO>();
		if(list!=null && list.size()>0){
			map=list.stream().collect(Collectors.toMap(TDO->TDO.getId()+"",TDO->TDO));
		}
		return map;
	}

	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	@Override
	public TDO getById(Long id, boolean doModel) {
		if(id==null){
			return null;
		}
		TDO dto= this.getBaseMapper().getById(id);
		if(dto!=null && doModel) {
			this.doMode(dto);
		}
		return dto;
	}

	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	@Override
	public TDO getById(Long id) {
		return this.getById(id,true);
	}

	/**
	 * 查询列表
	 * @param searchDto
	 * @return
	 */
	@Override
	public List<TDO> queryListByDto(TDO searchDto,boolean doModel) {
		List<TDO> list= this.getBaseMapper().queryList(searchDto);
		if(doModel && list!=null && list.size()>0){
			for(TDO dto:list){
				if(dto!=null) {
					this.doMode(dto);
				}
			}
		}
		return list;
	}


	/**
	 * 查询列表
	 * @param searchDto
	 * @return
	 */
	@Override
	public DataResult<TDO> queryPageByDto(TDO searchDto, Integer pageNum, Integer pageSize) {
		PageInfo<TDO> info = this.queryFrontPageByDto(searchDto,pageNum,pageSize);
		return new DataResult<>(info.getTotal(),info.getList());
	}
	/**
	 * 查询列表
	 * @param searchDto
	 * @return
	 */
	@Override
	public PageInfo<TDO> queryFrontPageByDto(TDO searchDto, Integer pageNum, Integer pageSize) {
		//设置默认值
		if(pageNum<=0){
			pageNum=1;
		}
		if(pageSize<=0){
			pageSize=20;
		}
		PageHelper.startPage(pageNum,pageSize);
		List<TDO> list = this.queryListByDto(searchDto,true);
		return new PageInfo<>(list);
	}
	@Override
	public void cleanAllCache(){}

	@Override
	public void cleanCacheByModel(T t) {
	}
	/**
	 * insert成功之后操作
	 * @param t
	 */
	@Transactional
	public void beforeAddCheck(T t){}

	/**
	 * 更新时候操作
	 * @param t
	 */
	public void beforeUpdateCheck(T t) {
	}
	/**
	 * insert成功之后操作
	 * @param t
	 */
	public void afterAddOperate(T t){}

	/**
	 * 更新时候操作
	 * @param t
	 */
	@Transactional
	public void afterUpdateOperate(T t) {
	}


	/**
	 * 新增日志
	 *
	 */
	@Transactional
	public void addLogger() {
        LoggerModelDto log = BaseAdminController.opeLogger.get();
		if (log != null &&  log.getClassName().contains(this.getClass().getName())) {
			loggerService.insert(log);
		}
	}

	/**
	 * 查询出列表处理
	 * @param dto
	 */
	protected void doMode(TDO dto){}
}

package com.liuqi.business.model;

import com.liuqi.business.enums.UserAdminStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import lombok.Data;
import org.crazycake.shiro.AuthCachePrincipal;


@Data
public class UserAdminModelDto extends UserAdminModel implements AuthCachePrincipal{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;



	private String statusStr;

	public String getStatusStr() {
		return UserAdminStatusEnum.getName(super.getStatus());
	}
	private String authStr;

	public String getAuthStr() {
		return YesNoEnum.getName(super.getAuth());
	}

	private String roleName;


	@Override
	public String getAuthCacheKey() {
		return getId()+"";
	}
}

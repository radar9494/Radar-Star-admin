package com.liuqi.business.model;

import lombok.Data;

@Data
public class TableIdModel {
	public static final String KDATA="t_k_data_id";
	public static final String TRADE_RECORD="t_trade_record_id";
	public static final String TRADE_RECORD_USER="t_trade_record_user_id";
	public static final String TRUSTEE="t_trustee_id";
	public static final String USER_WALLET_LOG="t_user_wallet_log_id";
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	//id
	private Long id;

}

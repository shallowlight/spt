package com.totrade.spt.mobile.utility;

import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;

/**
 *
 * @author huangxy
 *
 * @param <T>
 */
public interface OnDataListener<T>
{

	/**
	 * 请求服务
	 * @return
	 * @throws DBException
	 * @throws ApplicationException
	 */
	T requestService() throws DBException, ApplicationException;

	/**
	 * 响应成功
	 * @param obj
	 */
	void onDataSuccessfully(T obj);

	// void onDataFailed();

}

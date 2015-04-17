package koc.common.asynctask;

/**
* 回调接口,回调方法运行于主线程
* @ClassName: Callback   
* @author 姜涛
* @version 1.0 2012-1-16 下午5:58:16   
* @param <T>
*/
public interface Callback<T> {
	
	public void onCallback(final T pCallbackValue);
}
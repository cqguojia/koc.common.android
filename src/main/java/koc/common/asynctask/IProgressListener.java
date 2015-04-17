package koc.common.asynctask;

/**
 * 观察者
 * @ClassName: IProgressListener   
 * @author 姜涛
 * @version 1.0 2012-1-16 下午11:08:40
 */
public interface IProgressListener {
	
	/**
	 * 进度发生改变的时候调用
	 * @param pProgress
	 */
	public void onProgressChanged(final int pProgress);
}
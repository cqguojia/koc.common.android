package koc.common.asynctask;

/**
 * 被观察者
 * @ClassName: ProgressCallable
 * @author 姜涛
 * @version 1.0 2012-1-16 下午11:08:52
 * @param <T>
 */
public interface ProgressCallable<T> {

	/**
	 * 注册观察者对象
	 * @param pProgressListener
	 * @return
	 * @throws Exception
	 */
	public T call(final IProgressListener pProgressListener) throws Exception;
}
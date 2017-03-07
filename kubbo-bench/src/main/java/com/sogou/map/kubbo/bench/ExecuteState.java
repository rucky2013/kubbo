/**
 * 
 */
package com.sogou.map.kubbo.bench;

/**
 * @author liufuliang
 *
 */
public class ExecuteState implements Comparable<ExecuteState> {
    private boolean success;
    private long timeTaken; //ns
    
    
    
    public ExecuteState(boolean success, long timeTaken) {
        super();
        this.success = success;
        this.timeTaken = timeTaken;
    }

    /**
     * 
     * @return 本次执行状态是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 
     * @return 时间消耗的纳秒值
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    /**
     * sort by time taken
     */
    @Override
    public int compareTo(ExecuteState o) {
        return new Long(timeTaken).compareTo(new Long(o.getTimeTaken()));
    }

}


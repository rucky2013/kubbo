/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo.obj;

import java.io.Serializable;

/**
 * @author liufuliang
 *
 */
/*
 * 控制点,包括起终点和途经点
 */
public class Vertext implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public int		idx = 0;		//对应Edge在路线Edge序列中的下标
    public float	pct = -1;		//在对应Edge上的位置
    public boolean	via = false;	//是否是途经点
    public int midx = -1;
    public Vertext(int idx){
        this.idx = idx;
    }
    public Vertext(){
        
    }
    
    public Vertext(int idx, float pct){
        this.idx = idx;
        this.pct = pct;
    }
    public Vertext(int idx, float pct, boolean via){
        this.idx = idx;
        this.pct = pct;
        this.via = via;
    }
    public boolean isEqual(Vertext v){
        return idx == v.idx && via == v.via;
    }
}
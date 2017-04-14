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
 * 路径的边
 */
public class Edge implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public byte  direction = 1; //方向
    public int indexID = 0;
    public long navID = 0;    
    /*
     * 代价
     */
    //路况是在实时变化的, 所以每一条边都具有一个路况的快照, 以后的有关路况的数据, 都应该保持一致
    //过路口代价
    public int tc = 0;
    

    public Edge(){
        
    }
    public Edge(int i){
        this.indexID = i;
        this.navID = i;
    }

}

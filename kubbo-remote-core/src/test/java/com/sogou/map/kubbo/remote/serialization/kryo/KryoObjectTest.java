/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.java.JavaObjectOutput;
import com.sogou.map.kubbo.remote.serialization.kryo.KryoObjectOutput;
import com.sogou.map.kubbo.remote.serialization.kryo.obj.Color;
import com.sogou.map.kubbo.remote.serialization.kryo.obj.Edge;
import com.sogou.map.kubbo.remote.serialization.kryo.obj.Label;
import com.sogou.map.kubbo.remote.serialization.kryo.obj.Topology;
import com.sogou.map.kubbo.remote.serialization.kryo.obj.Vertext;

/**
 * @author liufuliang
 *
 */
public class KryoObjectTest {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        int edgeID = 0;
        //SerializableClassRegistry.registerClass(Topology.class);
        for(int j=0; j<100; ++j){
            ByteArrayOutputStream streamKryo = new ByteArrayOutputStream();
            KryoObjectOutput outKryo = new KryoObjectOutput(streamKryo);
            
            ByteArrayOutputStream streamJava = new ByteArrayOutputStream();
            ObjectOutput outJava = new JavaObjectOutput(streamJava);
            
            Topology topology = new Topology();
            topology.label = new Label("时间短", new Color(38, 162, 224), 1);
            
            for(int i=0; i<1000; i++){
                topology.addEdge(new Edge(edgeID++));
            }
            for(int i=0; i<20; i++){
                topology.addVertext(new Vertext());
            }
            
            long start1 = System.nanoTime();
            outKryo.writeObject(topology);
            long end1 = System.nanoTime();
            outKryo.release();
            
            long start2 = System.nanoTime();
            outJava.writeObject(topology);
            long end2 = System.nanoTime();

            System.out.println(streamKryo.toByteArray().length + ", time " + (end1-start1)/1000 + "us");
            System.out.println(streamJava.toByteArray().length + ", time " + (end2-start2)/1000 + "us");
        }

    }

}

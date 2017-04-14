# 序列化  

### 1. 自定义数据类型  
* 必须显式实现java.io.Serializable接口  
* 需要提供一个无参构造函数  
* 支持向前和向后兼容, 可以增加或删减字段, 但字段名称不能重复使用  

### 2. 实例  
```
public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    
    String value;
	
    public Message(){
	}
	
    public Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

```


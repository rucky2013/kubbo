# Kubbo
Kubbo是一个分布式高性能rpc框架, 支持异步调用, 底层基于kubernetes和netty。  
支持JAVA6及以上, 建议使用JAVA8


## Quick Start
下面以SampleService服务接口为例, 说明如何实现rpc服务接口以及无缝调用。  

### *添加依赖*  
maven仓库地址配置请参照[maven配置](docs/env/maven.md)
在pom.xml中添加依赖
```
<dependency>
  <groupId>com.sogou.map</groupId>
  <artifactId>kubbo-all</artifactId>
  <version>0.9</version>
</dependency>
```

### *定义API接口*  
关于自定义数据类型规范, 请阅读[自定义数据类型](docs/serialization.md)
```
public interface SampleService {
    String echo(String message);
}
```


### *服务端*    
rpc接口实现  
```
@Export(SampleService.class)
public class SampleServiceImpl implements SampleService {
    @Override
    public String echo(String message) {
        return message;
    }
}
```

程序初始化和销毁钩子  
```
@Hook
public class SampleLifecycleHook implements LifecycleHook {
    @Override
    public void destroy(ApplicationContext ctx) {
        System.out.println("<Application LifecycleHook destroy()>");     
    }
    @Override
    public void initialize(ApplicationContext ctx) {
        System.out.println("<Application LifecycleHook initialize()>");
    }
}
```

服务端部署  
下载[kubbo-boot](http://release.mssp.sogou/kubbo/kubbo-boot-latest.tar.gz) 解压  
将程序打包放到lib目录下, 运行 bin/run.sh  
可以在kubbo.properties中对服务参数进行具体配置, 具体配置项可参考[配置详解](docs/configuration.md)


### *客户端*
```
SampleService service = Kubbo.refer(SampleService.class);
String result = service.echo("helloworld");
```
- refer生成的接口对象是线程安全的.  
- 同一个远程接口应该refer一次后重复使用(线程安全), 多次refer会降低程序性能。
- 远程服务接口地址在kubbo.properties中配置, 具体配置项可参考[配置详解](docs/configuration.md)
- 客户端可以进行[异步调用](docs/async.md)


### *配置文件路径*
kubbo.properties路径可以通过以下任意一种方法指定, 推荐使用JAVA系统变量（服务端使用kubbo-boot启动, 无需配置）  
* 环境变量  
  KUBBO_CONFIGURATION=path/to/kubbo.properties
* JAVA系统变量  
  -Dkubbo.configuration=path/to/kubbo.properties
* CLASSPATH根目录  
  直接放在classpath根目录即可
* 代码设置  
  PropertiesConfigurator.configure("path/to/kubbo.properties");  

## 更多文档
- [配置详解](docs/configuration.md)
- [使用脚手架创建kubbo工程](docs/env/maven-archetype-kubbo-rpc.md)
- [异步调用](docs/async.md)
- [自定义数据类型](docs/serialization.md)
- [压力测试](docs/benchmark.md)
- [架构](docs/architecture.md)
- [FAQ](docs/faq.md)
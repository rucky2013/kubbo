# Kubbo
Kubbo是一个分布式高性能rpc框架, 支持异步调用, 底层基于kubernetes和netty。  
支持JAVA6及以上, 建议使用JAVA8


## Quick Start
### *添加依赖*  
按照[maven全局配置](http://git.sogou-inc.com/mssp/commons/blob/master/README.md)配置好maven, 然后添加以下工程依赖
```
<dependency>
  <groupId>com.sogou.map</groupId>
  <artifactId>kubbo-all</artifactId>
  <version>0.2</version>
</dependency>
```

建议使用maven管理工程, 如果不使用maven, 可以到[Maven仓库](http://repo.mssp.sogou/maven/)直接下载kubbo-all-0.2.jar和netty-all-4.1.6.Final.jar包导入工程里  

### *定义API接口*  
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
下载 http://release.mssp.sogou/kubbo/kubbo-boot-0.2.tar.gz, 解压  
将程序打包放到lib目录下, 运行 bin/run.sh  
如果需要, 可以在kubbo.propertie中对服务参数进行具体配置, 具体配置项可参考配置详解  


### *客户端*
```
SampleService service = Kubbo.refer(SampleService.class);
String result = service.echo("helloworld");
```
- 同一个远程接口应该refer一次后重复使用, 多次refer会降低程序性能。
- 远程接口地址可以在[kubbo.properties](docs/configuration.md)中描述
- 客户端可以进行[异步调用](docs/async.md)


### *配置文件路径*
kubbo.properties路径可以通过以下任意一种方法指定, 推荐使用JAVA系统变量（如果使用kubbo-boot启动, 便无需配置kubbo.properties路径）  
* 环境变量  
  KUBBO_CONFIGURATION=path/to/kubbo.properties
* JAVA系统变量  
  -Dkubbo.configuration=path/to/kubbo.properties
* CLASSPATH根目录  
  直接放在classpath根目录即可
* 代码设置  
  PropertiesConfigurator.configure("path/to/kubbo.properties");  

## More
- [kubbo.properties](docs/configuration.md)
- [异步调用](docs/async.md)
- [压力测试](docs/benchmark.md)
- [架构](docs/architecture.md)
- [FAQ](docs/faq.md)

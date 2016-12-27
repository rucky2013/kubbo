# Kubbo Project
***

## Introduction
Kubbo是一个分布式高性能rpc框架, 支持异步调用, 底层基于kubernetes和netty。

## Links
* [Documentation](http://go2map.sogou-inc.com:8890/display/go2map/Quickstart)

## Usage
### 添加依赖
`
<dependency>
  <groupId>com.sogou.map</groupId>
  <artifactId>kubbo-all</artifactId>
  <version>0.2</version>
</dependency>
`
> 1. 建议使用maven管理工程, 如果不使用maven, 可以直接下载kubbo-all-0.2.jar和netty-all-4.1.6.Final.jar包导入工程里，下载地址 http://repo.mssp.sogou/maven/
> 2. 支持JDK6及以上, 建议使用JDK8

### 定义API接口  
SampleService.java  
`
public interface SampleService {
    String echo(String message);
}
`


### 服务端  
rpc接口实现  
SampleServiceImpl.java  
`
@Export(SampleService.class)
public class SampleServiceImpl implements SampleService {
    @Override
    public String echo(String message) {
        return message;
    }
}
`

程序初始化和销毁钩子  
SampleLifecycleHook.java  
`
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
`  

服务端部署  
下载 http://release.mssp.sogou/kubbo/kubbo-boot-0.2.tar.gz, 解压  
将程序打包放到lib目录下, C/C++程序库放到lib_native目录下  
运行 bin/run.sh  
如果需要, 可以在kubbo.propertie中对服务参数进行具体配置, 具体配置项可参考配置详解  


### 客户端
Main.java
`
SampleService service = Kubbo.refer(SampleService.class);
String result = service.echo("helloworld");
`
> 1. 同一个远程接口应该refer一次后重复使用, 多次refer会降低程序性能。
> 2. 远程接口地址可以在kubbo.properties中配置, 具体配置项可参考配置详解
> 3. 客户端异步调用方法可参考异步调用


### 配置文件 kubbo.properties
kubbo.properties路径可以通过以下任意一种方法指定, 推荐使用java系统变量（如果使用kubbo-boot启动, 便无需配置kubbo.properties路径）  
* 环境变量  
  KUBBO_CONFIGURATION=x/y/z/kubbo.properties
* java系统变量  
  -Dkubbo.configuration=x/y/z/kubbo.properties
* CLASSPATH根目录  
  直接放在classpath根目录即可
* 代码设置  
  PropertiesConfigurator.configure("x/y/z/kubbo.properties");




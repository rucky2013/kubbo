- **如何配置服务发现和负载均衡**  
线下测试服务器可以使用ip地址方式直接连接调用  
线上会部署在[mssp](http://git.sogou-inc.com/mssp/mssp)微服务调度平台上, 便会自动获得服务发现和客户端负载均衡功能。

- **框架本身的并发性能如何**  
框架本身的并发性能可以达到20w/s

- **客户端refer出来的远程接口对象支持多线程并发调用吗**  
支持, 推荐把远程接口对象做成单例

- **kubbo-boot如何支持log4j2**  
kubbo框架本身支持log4j/log4j2/slf4j/jdk-logging  
kubbo-boot默认使用log4j, 如果要支持log4j2, 请下载[kubbo-boot-log4j2](http://release.mssp.sogou/kubbo/kubbo-boot-log4j2-latest.tar.gz)  

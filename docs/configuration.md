# kubbo.properties配置详解

## 服务端配置
```
server.bind = kubbo://0.0.0.0:40660?corethreads=10&maxthreads=100&queues=200
```
- corethreads: 处理线程池初始大小  
- maxthreads: 处理线程池最大数量  
- queues: 请求处理队列  
  当处理线程数大于corethreads, 超过1分钟的空闲线程会自动释放  
  当处理线程数达到maxthreads, 新的请求将被插入处理队列等待处理。  
- accepts: 最大连接数限制, 默认为不限制。  
- limiter.rate: qps限制, 限制每秒访问次数。  
  加方法名前缀可以限制单个方法，如com.sogou.map.sample.SampleService.echo.limiter.rate=1000  
- accesslog: 是否开启访问日志记录, true/false 默认为false  
  注意: 开启accesslog会影响程序的性能, 开启前请做好性能测试。  

## 客户端配置 
#### *单行配置方式*
```
reference.com.sogou.map.kubbo.SampleService = kubbo://127.0.0.1:40660?timeout=2000
```

#### *双行配置方式*
当程序中需要调用同一个接口的不同实现时, 可以使用双行配置方式。  
双行配置时可以按名称来区分同一个接口的不同实现, 如: Kubbo.refer("name1", SampleService.class)
```
reference.name1.interface = com.sogou.map.kubbo.SampleService
reference.name1.address = kubbo://10.134.77.209:40660?timeout=2000
reference.name2.interface = com.sogou.map.kubbo.SampleService
reference.name2.address = kubbo://127.0.0.1:40660?timeout=2000
```


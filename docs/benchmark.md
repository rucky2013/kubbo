# Benchmark

## kubbo-bench
kubbo-bench是对rpc接口的压力测试工具, 输出qps、响应时间等性能参数  

## kubbo-bench使用
Main.java
```
public static void main(String[] args) {
    final SampleService service = Kubbo.refer(SampleService.class);
    Benchmark.builder()
        .concurrency(50)
        .total(100000)
        .job(new Job(){
            @Override
            public boolean execute() {
                try{
                    service.echo("helloword");
                    return true;
                } catch(Throwable t){
                    return false;
                }
            }
        })
        .run();
}
```

## kubbo-bench结果  
```
Concurrency Level: 100
Time taken for tests: 12 seconds
Successful requests: 500000
Failed requests: 0
Requests per second: 41203.133
Time per request: 2.02184ms
Percentage of the requests served within a certain time (ms)
50%: 1
60%: 1
70%: 2
80%: 3
90%: 4
95%: 5
98%: 6
99%: 7
100%: 79 (longest request)
```



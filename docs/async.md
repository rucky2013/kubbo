# 异步调用

## 需要返回调用结果  
需要使用Callable封装对方法的调用
```
Future<String> future = Kubbo.callAsync(new Callable<String>() {
  public String call() throws Exception {
    return service.echo("hello");
  }
});
String result = future.get();
```

JAVA8环境可以使用lambda  
```
Future<String> future = Kubbo.callAsync(() -> service.echo("hello"));
String result = future.get();
```

## 不需要返回调用结果
需要使用Runnable封装对方法的调用
```
Kubbo.callAsync(new Runnable(){
  @Override
  public void run() {
    service.echo("hello");
  }
});
```

JAVA8环境可以使用lambda  
```
Kubbo.callAsync(() -> {service.echo("hello"); return; });
```


# listener
> 通过对请求进行重定向（302）的方式进行无缝连接 
>
- 异常：
    > 请求处理异常，解码失败异常，远程强制关闭异常等

## 参考：
- [SpringBoot之yml文件配置、日志文件配置](https://blog.csdn.net/qq_39629277/article/details/83272464)

## vert.x
- [dgate：基于Vert.x的轻量级API Gateway](https://www.jianshu.com/p/2ac4931dd69a)

- Router
    - [Abort (HTTP 500) request when exception in request's handleEnd()](https://stackoverflow.com/questions/52140214/abort-http-500-request-when-exception-in-requests-handleend)
    ```
    Router router = Router.router(vertx);
    router.route().failureHandler(handler -> handler.response().end());
    router.route().handler(routingContext -> routingContext.request().endHandler(handler -> {
      throw new NullPointerException("exception here!");
    }));
    vertx.createHttpServer().requestHandler(router::accept).listen(8085);
    ```
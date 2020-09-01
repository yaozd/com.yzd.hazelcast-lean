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
    - [vertx中web全局异常处理](https://leokongwq.github.io/2017/12/02/vertx-web-global-exception.html)
    ```
    404 已经 500 异常处理
    public class MyFirstVerticle extends AbstractVerticle {
    
        @Override
        public void start() throws Exception {
            final Router router = Router.router(vertx);
            router.get("/hello").handler(context -> {
                Integer.parseInt(context.request().getParam("age"));
                context.response().end("hello vert.x");
            }).failureHandler(context -> {
                context.response().end("Route internal error process");
            });
            router.get("/world").handler(context -> {
                Integer.parseInt(context.request().getParam("age"));
                context.response().end("hello world");
            });
            //最后一个Route
            router.route().last().handler(context -> {
                context.response().end("404");
            }).failureHandler(context -> {
               context.response().end("global error process");
            });
            vertx.createHttpServer().requestHandler(router::accept).listen(9090);
        }
    }
    ```
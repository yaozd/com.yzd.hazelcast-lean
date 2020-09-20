# vertx shell
 
## Vert.x shell 参考
 - [https://vertx.io/docs/vertx-shell/java/](https://vertx.io/docs/vertx-shell/java/)
 - [https://github.com/vert-x3/vertx-shell](https://github.com/vert-x3/vertx-shell)
 - [HttpTermServer-基本测试](https://github.com/vert-x3/vertx-shell/blob/master/src/test/java/io/vertx/ext/shell/term/HttpTermServerBase.java) 推荐参考byArvin
 - []()

## Java 调用 Shell 命令
- github demo
    - [https://github.com/SpencerZhang/ExecOSCommand](https://github.com/SpencerZhang/ExecOSCommand)
- [java 使用Process调用exe程序 及 Process.waitFor() 死锁问题了解和解决](https://blog.csdn.net/qq_27948659/article/details/80895860)
- [正确的调用系统命令——为Process.waitFor设置超时以及其他](https://blog.csdn.net/dsundsun/article/details/21103023)
- [Java 调用 Shell 命令](https://blog.csdn.net/andrewlau82/article/details/90551212)
- [Java调用shell脚本](https://blog.csdn.net/zhoudado921/article/details/80847182)
- [shell脚本调用方法 与 传递参数](https://blog.csdn.net/weixin_41171108/article/details/88633462)
```
$* 与 $@ 区别：

相同点：都是引用所有参数。
不同点：只有在双引号中体现出来。假设在脚本运行时写了三个参数 1、2、3，，
则 " * " 等价于 "1 2 3"（传递了一个参数），而 "@" 等价于 "1" "2" "3"（传递了三个参数）。
```
- 注意
```
注意：执行shell脚本是通过java的Runtime.getRuntime().exec()方法调用的。
这种调用方式可以达到目的，但是他在java虚拟机中非常消耗资源，
即使外部命令本身能很快执行完毕，频繁调用时创建进程的开销也非常可观。
ava虚拟机执行这个命令的过程是：首先克隆一下和当前虚拟机拥有一样环境变量的进程，
再用这个新的进程去执行外部命令，最后在退出这个进程，
如果频繁执行这个操作，系统的消耗会很大，不仅是CPU,内存的负担也很重。
```


## 线程池的使用
- [Java并发编程：线程池的使用](https://www.cnblogs.com/dolphin0520/p/3932921.html)
- 线程池状态 runState
```
static final变量表示线程池的各个状态：
volatile int runState;
static final int RUNNING    = 0;
static final int SHUTDOWN   = 1;
static final int STOP       = 2;
static final int TERMINATED = 3;
runState表示当前线程池的状态，它是一个volatile变量用来保证线程之间的可见性；

下面的几个static final变量表示runState可能的几个取值。

当创建线程池后，初始时，线程池处于RUNNING状态；

如果调用了shutdown()方法，则线程池处于SHUTDOWN状态，此时线程池不能够接受新的任务，它会等待所有任务执行完毕；

如果调用了shutdownNow()方法，则线程池处于STOP状态，此时线程池不能接受新的任务，并且会去尝试终止正在执行的任务；

当线程池处于SHUTDOWN或STOP状态，并且所有工作线程已经销毁，任务缓存队列已经清空或执行结束后，线程池被设置为TERMINATED状态。
```
- ThreadPoolExecutor类中其他的一些比较重要成员变量
```
private final BlockingQueue<Runnable> workQueue;              //任务缓存队列，用来存放等待执行的任务
private final ReentrantLock mainLock = new ReentrantLock();   //线程池的主要状态锁，对线程池状态（比如线程池大小
                                                              //、runState等）的改变都要使用这个锁
private final HashSet<Worker> workers = new HashSet<Worker>();  //用来存放工作集
 
private volatile long  keepAliveTime;    //线程存货时间   
private volatile boolean allowCoreThreadTimeOut;   //是否允许为核心线程设置存活时间
private volatile int   corePoolSize;     //核心池的大小（即线程池中的线程数目大于这个参数时，提交的任务会被放进任务缓存队列）
private volatile int   maximumPoolSize;   //线程池最大能容忍的线程数
 
private volatile int   poolSize;       //线程池中当前的线程数
 
private volatile RejectedExecutionHandler handler; //任务拒绝策略
 
private volatile ThreadFactory threadFactory;   //线程工厂，用来创建线程
 
private int largestPoolSize;   //用来记录线程池中曾经出现过的最大线程数
 
private long completedTaskCount;   //用来记录已经执行完毕的任务个数
```
- Executors类
```
在java doc中，并不提倡我们直接使用ThreadPoolExecutor，而是使用Executors类中提供的几个静态方法来创建线程池：
//

//
Executors.newCachedThreadPool();        //创建一个缓冲池，缓冲池容量大小为Integer.MAX_VALUE
Executors.newSingleThreadExecutor();   //创建容量为1的缓冲池
Executors.newFixedThreadPool(int);    //创建固定容量大小的缓冲池
```
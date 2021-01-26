package com.liuqi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@MapperScan("com.liuqi.business.mapper")
@EnableTransactionManagement
@WebListener
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync
public class BaseDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseDemoApplication.class, args);
	}

	/**
	 *线程池
	 * @return
	 */
	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);// 核心线程数（默认线程数）线程池创建时候初始化的线程数
		executor.setQueueCapacity(5000);// 缓冲队列数 用来缓冲执行任务的队列
		executor.setMaxPoolSize(100);// 最大线程数 线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
		executor.setKeepAliveSeconds(10);// 允许线程空闲时间（单位：默认为秒）当超过了核心线程之外的线程在空闲时间到达之后会被销毁
		executor.setWaitForTasksToCompleteOnShutdown(true);//用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
		executor.setAwaitTerminationSeconds(60);//该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。
		executor.setThreadNamePrefix("pool-");
		//设置拒绝策略，当任务源源不断的过来，而我们的系统又处理不过来的时候，我们要采取的策略是拒绝服务。
		/**
		 * 四种处理策略。
		 * CallerRunsPolicy：这个策略显然不想放弃执行任务。但是由于池中已经没有任何资源了，那么就直接使用调用该execute的线程本身来执行。（开始我总不想丢弃任务的执行，但是对某些应用场景来讲，很有可能造成当前线程也被阻塞。如果所有线程都是不能执行的，很可能导致程序没法继续跑了。需要视业务情景而定吧。）
		 * AbortPolicy这种策略直接抛出异常，丢弃任务。（jdk默认策略，队列满并线程满时直接拒绝添加新任务，并抛出异常，所以说有时候放弃也是一种勇气，为了保证后续任务的正常进行，丢弃一些也是可以接收的，记得做好记录）
		 *DiscardPolicy 这种策略和AbortPolicy几乎一样，也是丢弃任务，只不过他不抛出异常。
		 *DiscardOldestPolicy 如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）
		 */
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		//执行初始化
		executor.initialize();
		return executor;
	}

	/**
	 * 文件上传配置
	 * @return
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//单个文件最大
		factory.setMaxFileSize("10MB"); //KB,MB
		/// 设置总上传数据总大小
		factory.setMaxRequestSize("20MB");
		return factory.createMultipartConfig();
	}
}

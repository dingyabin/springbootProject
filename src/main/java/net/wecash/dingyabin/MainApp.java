package net.wecash.dingyabin;

import net.wecash.SimplePropertyPlaceholderConfigurer;
import net.wecash.web.filter.LogFilter;
import net.wecash.web.filter.TraceIdFilter;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 *
 * @author dingyabin
 * @date 2017/11/8
 */

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
@ImportResource("classpath*:/applicationContext*.xml")
public class MainApp {


    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    @Bean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }

    @Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholder() throws IOException {
        SimplePropertyPlaceholderConfigurer placeholderConfigurer = new SimplePropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocations(new ClassPathXmlApplicationContext().getResources("classpath*:*.properties"));
        return placeholderConfigurer;
    }



}

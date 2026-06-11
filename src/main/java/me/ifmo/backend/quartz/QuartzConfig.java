package me.ifmo.backend.quartz;

import me.ifmo.backend.batch.PaymentExpirationBatchLauncher;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public MethodInvokingJobDetailFactoryBean paymentExpirationJobDetail(
            PaymentExpirationBatchLauncher paymentExpirationBatchLauncher
    ) {
        MethodInvokingJobDetailFactoryBean factoryBean = new MethodInvokingJobDetailFactoryBean();

        factoryBean.setTargetObject(paymentExpirationBatchLauncher);
        factoryBean.setTargetMethod("launch");
        factoryBean.setName("paymentExpirationJobDetail");
        factoryBean.setConcurrent(false);

        return factoryBean;
    }


    @Bean
    public SimpleTriggerFactoryBean paymentExpirationTrigger(
            JobDetail paymentExpirationJobDetail
    ) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();

        factoryBean.setJobDetail(paymentExpirationJobDetail);
        factoryBean.setName("paymentExpirationTrigger");
        factoryBean.setRepeatInterval(60_000);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

        return factoryBean;
    }

}
package me.ifmo.backend.batch;

import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.enums.PaymentStatus;
import me.ifmo.backend.repositories.PaymentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Configuration
public class PaymentExpirationBatchConfig {

    @Bean
    @StepScope
    public RepositoryItemReader<Payment> paymentExpirationReader(
            PaymentRepository paymentRepository,
            @Value("#{jobParameters['expiredBefore']}") String expiredBefore
    ) {
        return new RepositoryItemReaderBuilder<Payment>()
                .name("paymentExpirationReader")
                .repository(paymentRepository)
                .methodName("findByStatusInAndExpiresAtBefore")
                .arguments(
                        List.of(PaymentStatus.CREATED, PaymentStatus.PENDING),
                        LocalDateTime.parse(expiredBefore)
                )
                .pageSize(100)
                .sorts(Map.of("expiresAt", Sort.Direction.ASC))
                .build();
    }


    @Bean
    public Step expirePaymentsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RepositoryItemReader<Payment> paymentExpirationReader,
            PaymentExpirationProcessor paymentExpirationProcessor,
            PaymentExpirationWriter paymentExpirationWriter
    ) {
        return new StepBuilder("expirePaymentsStep", jobRepository)
                .<Payment, Payment>chunk(100, transactionManager)
                .reader(paymentExpirationReader)
                .processor(paymentExpirationProcessor)
                .writer(paymentExpirationWriter)
                .build();
    }


    @Bean
    public Job expirePaymentsJob(
            JobRepository jobRepository,
            Step expirePaymentsStep,
            PaymentExpirationJobLoggingListener paymentExpirationJobLoggingListener
    ) {
        return new JobBuilder("expirePaymentsJob", jobRepository)
                .start(expirePaymentsStep)
                .listener(paymentExpirationJobLoggingListener)
                .build();
    }

}

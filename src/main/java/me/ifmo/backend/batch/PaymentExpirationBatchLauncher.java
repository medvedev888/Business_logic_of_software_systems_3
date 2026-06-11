package me.ifmo.backend.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j

@Component
public class PaymentExpirationBatchLauncher {
    private final JobLauncher jobLauncher;
    private final Job expirePaymentsJob;

    public PaymentExpirationBatchLauncher(
            JobLauncher jobLauncher,
            @Qualifier("expirePaymentsJob") Job expirePaymentsJob
    ) {
        this.jobLauncher = jobLauncher;
        this.expirePaymentsJob = expirePaymentsJob;
    }

    public void launch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("expiredBefore", LocalDateTime.now().toString())
                    .addLong("startedAt", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(expirePaymentsJob, jobParameters);

            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                log.error("Payment expiration batch job failed");
            }

        } catch (Exception exception) {
            log.error("Failed to launch payment expiration batch job", exception);
        }
    }
}

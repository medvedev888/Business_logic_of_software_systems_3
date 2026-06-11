package me.ifmo.backend.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;


@Slf4j

@Component
public class PaymentExpirationJobLoggingListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(
                "Payment expiration batch job started: jobId={}, parameters={}",
                jobExecution.getJobId(),
                jobExecution.getJobParameters()
        );
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        int readCount = 0;
        int writeCount = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
        }

        log.info(
                "Payment expiration batch job finished: status={}, readCount={}, writeCount={}",
                jobExecution.getStatus(),
                readCount,
                writeCount
        );

        if (!jobExecution.getAllFailureExceptions().isEmpty()) {
            log.error(
                    "Payment expiration batch job failures: {}",
                    jobExecution.getAllFailureExceptions()
            );
        }
    }

}
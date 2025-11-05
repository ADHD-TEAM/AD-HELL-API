package com.adhd.ad_hell.domain.user.batch;

import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class DeleteStatusBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserMapper userMapper;
    private final UserCommandRepository userCommandRepository;

    @Bean
    public Job deleteStatusUserBatchJob() {
        return new JobBuilder("deleteStatusUserBatchJob" , jobRepository)
                .start(deleteStatusUserBatchStep())
                .build();
    }

    @Bean
    public Step deleteStatusUserBatchStep() {
        return new StepBuilder("deleteStatusUserBatchStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    // 3개월 이상 탈퇴상태인 User 가져오기
                    List<UserDTO> deleteStatusUser = userMapper.getDeleteStatusUsers();

                    if(!deleteStatusUser.isEmpty()){
                        List<User> users = deleteStatusUser.stream()
                                .map(User::formUser)
                                .toList();
                        //삭제
                        log.info("[DeleteStatusBatchConfig/deleteStatusUserBatchStep] 회원 탈퇴한 사용자 {} 삭제", users.size());
                        userCommandRepository.deleteAll(users);
                    } else {
                        log.info("[DeleteStatusBatchConfig/deleteStatusUserBatchStep] 회원 탈퇴한 사용자 없음");
                    }

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}

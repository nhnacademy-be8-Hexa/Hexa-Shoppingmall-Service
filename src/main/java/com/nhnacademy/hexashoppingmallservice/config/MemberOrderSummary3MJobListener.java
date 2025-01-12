package com.nhnacademy.hexashoppingmallservice.config;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberOrderSummary3M;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberOrderSummary3MJobListener implements JobExecutionListener {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public void beforeJob(JobExecution jobExecution) {
        // 배치 작업 전에 MemberOrderSummary3M 테이블 비우기
        entityManager.createQuery("DELETE FROM MemberOrderSummary3M").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // 작업 후 후속 처리 (예: 로깅)
    }
}
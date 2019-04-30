package com.mitrais.more.repository;

import java.util.List;  

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.EmailScheduler;
import com.mitrais.more.model.EmailSchedulerStatus;

public interface EmailSchedulerRepository extends JpaRepository<EmailScheduler, Long> {
	List<EmailScheduler> findByDeleted(boolean deleted); 
	List<EmailScheduler> findByStatus(EmailSchedulerStatus status);
	List<EmailScheduler> findByStatusAndDeleted(EmailSchedulerStatus status, boolean deleted);
	List<EmailScheduler> findBySendTo(String sendTo);
}

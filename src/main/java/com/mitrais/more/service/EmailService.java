package com.mitrais.more.service;

import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.Lookup;

/**
 * 
 * Provide a service to send an Email
 *
 */
@Component
public class EmailService {
	/**
	 * Set Sender for email information
	 */
	@Value("${spring.mail.username}")
	private String sender;
	/**
	 * Set Dependencies. Used Java mail sender to send email
	 */
	@Autowired
	private JavaMailSender emailSender;
	
	/** The setting service. */
	@Autowired
	private SettingService settingService; 
	
	/**
	 * Send email.
	 *
	 * @param to the to
	 * @param subject the subject
	 * @param text the text
	 * @param applicant the applicant
	 * @throws SendFailedException the send failed exception
	 * @throws AddressException the address exception
	 */
	public void sendEmail(String to, String subject, String text, Applicant applicant) throws SendFailedException, AddressException {
		MimeMessage message = emailSender.createMimeMessage();
		try {
			// get sender name from setting
			Lookup sender = settingService.getSettingByKeyword("hr_manager_signature");
			
			String emailBody = text;
			emailBody = emailBody.replace("[CANDIDATE]", applicant.getCandidate().getName());
			emailBody = emailBody.replace("[VACANCY]", applicant.getVacancy().getPosition());
			emailBody = emailBody.replace("[SEND_DATE]", applicant.getApplyDate().toString());
			emailBody = emailBody.replace("[RECRUITER_NAME]", sender.getValue());
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("MitraisRecruitment");
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(text, true);
	        emailSender.send(message);
		} catch (SendFailedException e) {
			throw new SendFailedException("Failed send email");
		} catch (MailAuthenticationException e) {
			throw new MailAuthenticationException(e.getMessage());
		} catch (AddressException e) {
			throw new AddressException(e.getMessage());
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}
	
	/**
	 * Send email.
	 *
	 * @param to the to
	 * @param subject the subject
	 * @param text the text
	 * @param applicant the applicant
	 * @param sendDate the send date
	 * @throws SendFailedException the send failed exception
	 * @throws AddressException the address exception
	 */
	public void sendEmail(String to, String subject, String text, Applicant applicant, String sendDate) throws SendFailedException, AddressException {
		MimeMessage message = emailSender.createMimeMessage();
		try {
			// get sender name from setting
			Lookup sender = settingService.getSettingByKeyword("hr_manager_signature");
			
			String emailBody = text;
			emailBody = emailBody.replace("[CANDIDATE]", applicant.getCandidate().getName());
			emailBody = emailBody.replace("[VACANCY]", applicant.getVacancy().getPosition());
			emailBody = emailBody.replace("[SEND_DATE]", sendDate);
			emailBody = emailBody.replace("[RECRUITER_NAME]", sender.getValue());
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("MitraisRecruitment");
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(text, true);
	        emailSender.send(message);
		} catch (SendFailedException e) {
			throw new SendFailedException("Failed send email");
		} catch (MailAuthenticationException e) {
			throw new MailAuthenticationException(e.getMessage());
		} catch (AddressException e) {
			throw new AddressException(e.getMessage());
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}
}

package com.mitrais.more.service;

import java.io.ByteArrayInputStream;   
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays; 
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.SendFailedException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.ApplicantStatus;
import com.mitrais.more.model.Candidate;
import com.mitrais.more.model.CandidateDoc;
import com.mitrais.more.model.Lookup;
import com.mitrais.more.model.User;
import com.mitrais.more.payloads.CandidateRequest;
import com.mitrais.more.repository.ApplicantRepository;
import com.mitrais.more.repository.CandidateRepository;
import com.mitrais.more.repository.UserRepository;
import com.mitrais.more.security.UserPrincipal;

import javassist.NotFoundException;

@Service
public class CandidateService {
	
	/**
	 * CandidateRepository implemented from JpaRepository
	 */
	private CandidateRepository candidateRepo;
	
	/** The applicant repository. */
	private ApplicantRepository applicantRepo;
	
	/** The user repository. */
	private UserRepository userRepository;
	
	/** The email service. */
	private EmailService emailService;
	
	/** The setting service. */
	private SettingService settingService;
	
	/** The recruiter's email */
	@Value("${spring.mail.username}")
	private String recruiterEmail;
	
	/**
	 * Instantiates a new candidate service.
	 *
	 * @param candidateRepo the candidate repository
	 * @param applicantRepo the applicant repository
	 * @param userRepository the user repository
	 * @param emailService the email service
	 * @param settingService the setting service
	 */
	@Autowired
	public CandidateService(CandidateRepository candidateRepo, ApplicantRepository applicantRepo, UserRepository userRepository, 
			EmailService emailService, SettingService settingService) {
		this.candidateRepo = candidateRepo;
		this.applicantRepo = applicantRepo;
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.settingService = settingService;
	}
	
	/**
	 * Create candidate
	 * @param candidateRequest
	 * @param currentUser
	 * @return candidate repository save result
	 * @throws SendFailedException 
	 */
	@Transactional
	public List<String> createCandidate(Applicant applicant) throws SendFailedException {
		try {
			Optional<Candidate> foundCandidate = candidateRepo.findByEmail(applicant.getCandidate().getEmail());
			Candidate candidate = new Candidate();
			if (!foundCandidate.isPresent()) {
				candidate.setName(applicant.getCandidate().getName());
				candidate.setEmail(applicant.getCandidate().getEmail());
				candidate.setBirthDate(applicant.getCandidate().getBirthDate());
				candidate.setPhoneNumber(applicant.getCandidate().getPhoneNumber());
				candidate.setActive(true);
				candidateRepo.save(candidate);
			}
			
			if (!foundCandidate.isPresent()) {
				applicant.setCandidate(candidate);
			} else {
				applicant.setCandidate(foundCandidate.get());
			}
			applicantRepo.save(applicant);

			// Get default email template as notification to admin
			Lookup emailTemplateSetting = settingService.getSettingByKeyword("email_template_notif_to_admin");
			
			// Get users who wants receive a notification email
			List<User> adminUsers = userRepository.findBySendMeEmail(true);
			if(adminUsers.size() > 0) {
				for(User user : adminUsers) {
					emailService.sendEmail(user.getEmail(), "APPLICANT SUBMITTED", emailTemplateSetting.getValue(), applicant);
				}
			}
			
			//Get default email template as confirmation to applicant
			Lookup confirmationEmail = settingService.getSettingByKeyword("email_template_reply_on_apply");
			
			emailService.sendEmail(applicant.getCandidate().getEmail(), "Thanks for submit", confirmationEmail.getValue(), applicant);
			
			return Arrays.asList(applicant.getId().toString());
		} catch (SendFailedException e) {
			throw new SendFailedException("Failed send email");
		} catch (MailAuthenticationException e) {
			throw new MailAuthenticationException(e.getMessage());
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}
	
	/**
	 * Get all active candidate data
	 * @return Candidate list with List type
	 */
	public List<Candidate> getAllCandidate() {
		return candidateRepo.findByActive(true);
	}
	
	/**
	 * Get candidate by ID
	 * @param long ID
	 * @return Candidate data with List type
	 */
	public List<Candidate> getCandidateById(long id){
		Optional<Candidate> optCandidate = candidateRepo.findById(id);
		return Collections.singletonList(optCandidate.get());
	}
	
	public Candidate getCandidateByEmail(String email) throws NotFoundException {
		try {
			Candidate candidate = candidateRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Candidate Not Found"));
			return candidate;
		} catch (NotFoundException e) {
			throw new NotFoundException("Candidate Not found");
		}
	}
	
	/**
	 * Update candidate
	 * @param candidateRequest
	 * @param currentUser
	 * @param id
	 * @return candidate data with List type
	 */
	public List<Candidate> updateCandidate(CandidateRequest candidateRequest, UserPrincipal currentUser, long id){
		Candidate candidate = new Candidate();
		candidate.setUpdatedBy(currentUser.getId());
		candidate.setId(id);
		candidateRepo.save(candidate);
		return this.getCandidateById(id);
	}
	
	/**
	 * Delete candidate with soft delete strategy
	 * @param id
	 * @return candidate data with List type
	 */
	@Transactional
	public int deleteCandidate(long id) {
		try {
			int row = candidateRepo.softDeleteById(id, false);
			return row;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Id given was null");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * check availability candidate data by ID
	 * @param id
	 * @return candidate data with Optional type
	 */
	public Optional<Candidate> checkCandidate(long id){
		return candidateRepo.findById(id);
	}
	
	/**
	 * check availability applicant data by candidate email and vacancy id
	 * @param candidate email, vacancy id
	 * @return applicant data with Optional type
	 */
	public Optional<Applicant> checkApplicant(String candidateEmail, long vacancyId){
		
		return applicantRepo.findByCandidateEmailAndVacancyId(candidateEmail, vacancyId);
	}
	
	/**
	 * Hard delete candidate. remove from database
	 * @param id
	 *
	 */
	@Transactional
	public void hardDeleteCandidate(long id) {
		try {
			candidateRepo.deleteById(id);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Id given was null");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Export candidate applicant to excel.
	 *
	 * @return the byte array input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ByteArrayInputStream exportCandidateApplicantToExcel(List<Applicant> applicants) throws IOException {
		String[] COLUMNs = {"Id Applicant", "Name", "Address", "Birthdate", "Email", "Phone Number", "Apply On", "Status"};
		
		try (
				Workbook workbook = new XSSFWorkbook();
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
			){ 
			Sheet sheet = workbook.createSheet("Customers");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLUE.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			// Row for Header
			Row headerRow = sheet.createRow(0);

			// Header
			for (int col = 0; col < COLUMNs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(COLUMNs[col]);
				cell.setCellStyle(headerCellStyle);
			}
		      
			int rowIdx = 1;
			for (Applicant applicant : applicants) {
				Row row = sheet.createRow(rowIdx++);

				row.createCell(0).setCellValue(applicant.getId());
				row.createCell(1).setCellValue(applicant.getCandidate().getName());
				//row.createCell(2).setCellValue(applicant.getCandidate().getAddress());
				row.createCell(3).setCellValue(applicant.getCandidate().getBirthDate());
				row.createCell(4).setCellValue(applicant.getCandidate().getEmail());
				row.createCell(5).setCellValue(applicant.getCandidate().getPhoneNumber());
				row.createCell(6).setCellValue(applicant.getVacancy().getPosition());
				row.createCell(7).setCellValue(applicant.getStatus().name());
			}
	   
	      	workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	@Transactional
	public List<String> duplicateCandidate(Applicant applicant) {
		try {
			Optional<Candidate> foundCandidate = candidateRepo.findByEmail(applicant.getCandidate().getEmail());
			Candidate candidate = new Candidate();
			if (!foundCandidate.isPresent()) {
				candidate.setName(applicant.getCandidate().getName());
				candidate.setEmail(applicant.getCandidate().getEmail());
				candidate.setBirthDate(applicant.getCandidate().getBirthDate());
				candidate.setPhoneNumber(applicant.getCandidate().getPhoneNumber());
				candidate.setOriginalCandidateId(applicant.getCandidate().getId());
				candidate.setActive(true);
				candidateRepo.save(candidate);
			}
			
			if (!foundCandidate.isPresent()) {
				applicant.setCandidate(candidate);
			}
			
			List<CandidateDoc> temp = applicant.getDocuments();
			for (CandidateDoc doc : temp) {
				doc.setId(null);
			}
			
			Applicant duplicated = new Applicant();
			duplicated = applicant;
			duplicated.setDocuments(temp);
			duplicated.setId(100L);
			duplicated.setApplyDate(new Date());
			duplicated.setStatus(ApplicantStatus.IN_PROGRESS);
			duplicated.setReasons(null);

			Applicant newapplicant = applicantRepo.save(duplicated);
			return Arrays.asList(newapplicant.getId().toString());
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}
}

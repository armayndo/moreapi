package stepDefinitions;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.ApplicantStatus;
import com.mitrais.more.model.Candidate;
import com.mitrais.more.payloads.LoginRequest;
import com.mitrais.more.repository.ApplicantRepository;
import com.mitrais.more.repository.CandidateRepository;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CandidateModuleTestStep {
	
	private LoginRequest loginParam;
	private ResponseEntity<String> response;
	private TestRestTemplate restTemplate;
	private HttpHeaders headers;
	
	private String baseUrl = "http://localhost:5000/moreservice";
	
	private CandidateRepository candidateRepo;
	private ApplicantRepository applicantRepo;
	
	@Autowired
	public CandidateModuleTestStep(CandidateRepository candidateRepo, ApplicantRepository applicantRepo) {
		this.candidateRepo = candidateRepo;
		this.applicantRepo = applicantRepo;
	}
	
	public  CandidateModuleTestStep() {}
	
	@Before
	public void init() {
		loginParam = new LoginRequest();
		headers = new HttpHeaders();
		restTemplate = new TestRestTemplate();
	}
	
	@Given("^user upload document$")
    public void user_upload_document() throws Throwable {
    }

    @Given("^user admin logged in$")
    public void user_admin_logged_in() throws Throwable {
    	loginParam.setUserParam("admin");
		loginParam.setPassParam("123456");;
		
		HttpEntity<LoginRequest> entity = new HttpEntity<LoginRequest>(loginParam, headers);
		
		response = restTemplate.exchange(baseUrl + "/api/v1/auth", HttpMethod.POST, entity, String.class);
		JSONObject jsonObject = new JSONObject(response.getBody());
		String token = jsonObject.getString("accessToken");
		
		restTemplate.getRestTemplate().setInterceptors(
		        Collections.singletonList((request, body, execution) -> {
		            request.getHeaders()
		                    .add("Authorization", "Bearer " + token);
		            return execution.execute(request, body);
		        }));
    }

    @When("^user candidate apply with name (.+) email (.+) phone (.+) birthdate (.+) address (.+) to vacancy id (.+)$")
    public void user_candidate_apply_with_name_email_phone_birthdate_address_to_vacancy_id(String name, String email, String phone, String birthdate, String address, String vacancyid) throws Throwable {
    	Candidate candidate = new Candidate();
    	candidate.setName(name);
    	candidate.setEmail(email);
    	candidate.setPhoneNumber(phone);
    	candidate.setBirthDate(birthdate);
    	//candidate.setAddress(address);
    	
    	response = restTemplate.postForEntity(baseUrl + "/api/v1/candidate/" + vacancyid, candidate, String.class);
    }

    @When("^user admin call \"([^\"]*)\"$")
    public void user_admin_call_something(String strArg1) throws Throwable {
    	response = restTemplate.getForEntity(baseUrl + strArg1, String.class);
    }

    @When("^user admin accept candidate application$")
    public void user_admin_accept_candidate_application() throws Throwable {
    	Applicant applicant = applicantRepo.findById(Long.valueOf(1)).get();
    	applicant.setStatus(ApplicantStatus.HIRED);
    	response = restTemplate.exchange(baseUrl + "/api/v1/applicant/1", HttpMethod.PUT, new HttpEntity<Applicant>(applicant, headers), String.class);
    }

    @When("^user admin reject candidate application$")
    public void user_admin_reject_candidate_application() throws Throwable {
    	Applicant applicant = applicantRepo.findById(Long.valueOf(1)).get();
    	applicant.setStatus(ApplicantStatus.REJECT);
    	applicant.setReason("Test reason");
    	response = restTemplate.exchange(baseUrl + "/api/v1/applicant/1", HttpMethod.PUT, new HttpEntity<Applicant>(applicant, headers), String.class);
    }

    @When("^user admin delete candidate$")
    public void user_admin_delete_candidate() throws Throwable {
    	response = restTemplate.exchange(baseUrl + "/api/v1/candidate" + "/1", HttpMethod.DELETE, new HttpEntity<Candidate>(null, headers), String.class);
    	response = restTemplate.exchange(baseUrl + "/api/v1/candidate" + "/2", HttpMethod.DELETE, new HttpEntity<Candidate>(null, headers), String.class);
    }

    @Then("^user admin receives response status code 200$")
    public void user_admin_receives_response_status_code_200() throws Throwable {
    	assertThat("status code is incorrect: " + response.getBody(), response.getStatusCodeValue(), Matchers.is(200));
    }

    @And("^user admin get all candidates$")
    public void user_admin_get_all_candidates() throws Throwable {
    	JSONObject jsonObject = new JSONObject(response.getBody());
    	JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
    	assertThat("Array length is incorrect: " + response.getBody(), jsonArray.length(), Matchers.is(candidateRepo.findAll().size()));
    }

    @And("^candidate (.+) applied successfully$")
    public void candidate_applied_successfully(String email) throws Throwable {
    	assertTrue("Candidate apply fail", candidateRepo.findByEmail(email).isPresent());
    }

    @And("^document uploaded successfully$")
    public void document_uploaded_successfully() throws Throwable {
    }

    @And("^user admin receive candidate document from candidate id \"([^\"]*)\"$")
    public void user_admin_receive_candidate_document_from_candidate_id_something(String strArg1) throws Throwable {
    }

    @And("^user admin receive vacancy from candidate (\\d+)$")
    public void user_admin_receive_vacancy_from_candidate_something(int arg) throws Throwable {
    	JSONObject jsonObject = new JSONObject(response.getBody());
    	JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
    	assertThat("Wrong candidate vacancy: " + response.getBody(), jsonArray.length(), Matchers.is(applicantRepo.findByCandidate(candidateRepo.findById(Long.valueOf(arg)).get()).size()));
    }

    @And("^candidate application accepted$")
    public void candidate_application_accepted() throws Throwable {
    	JSONObject jsonObject = new JSONObject(response.getBody());
    	JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
    	
    	JSONObject jsonApplicant = jsonArray.getJSONObject(0);
    	assertTrue("Candidate not accepted: " + response.getBody(), "ACCEPT".equals(jsonApplicant.getString("status")));
    }

    @And("^candidate application rejected$")
    public void candidate_application_rejected() throws Throwable {
    	JSONObject jsonObject = new JSONObject(response.getBody());
    	JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
    	
    	JSONObject jsonApplicant = jsonArray.getJSONObject(0);
    	assertTrue("Candidate not accepted: " + response.getBody(), "REJECT".equals(jsonApplicant.getString("status")));
    }

    @And("^candidate deleted$")
    public void candidate_deleted() throws Throwable {
    	assertTrue("Candidate not deleted: " + response.getBody(), candidateRepo.findByActive(true).isEmpty());
    	
    }
}

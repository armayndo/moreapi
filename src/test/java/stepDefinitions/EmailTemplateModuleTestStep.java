package stepDefinitions;

import static org.junit.Assert.assertFalse; 
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

import com.mitrais.more.model.EmailTemplate;
import com.mitrais.more.payloads.LoginRequest;
import com.mitrais.more.repository.EmailRepository;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class EmailTemplateModuleTestStep {
	private LoginRequest loginParam;
	private ResponseEntity<String> response;
	private TestRestTemplate restTemplate;
	private HttpHeaders headers;
	
	private String baseUrl = "http://localhost:5000";
	
	private EmailRepository emailRepo;
	
	@Autowired
	public EmailTemplateModuleTestStep(EmailRepository emailRepo) {
		this.emailRepo = emailRepo;
	}
	
	public EmailTemplateModuleTestStep() {}
	
	@Before
	public void init() {
		loginParam = new LoginRequest();
		headers = new HttpHeaders();
		restTemplate = new TestRestTemplate();
	}
	
	@Given("^user logged in as admin$")
    public void user_logged_in_as_admin() throws Throwable {
		loginParam.setUserParam("admin");
		loginParam.setPassParam("123456");
		
		HttpEntity<LoginRequest> entity = new HttpEntity<LoginRequest>(loginParam, headers);
		
		response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/auth", HttpMethod.POST, entity, String.class);
		JSONObject jsonObject = new JSONObject(response.getBody());
		String token = jsonObject.getString("accessToken");
		
		restTemplate.getRestTemplate().setInterceptors(
		        Collections.singletonList((request, body, execution) -> {
		            request.getHeaders()
		                    .add("Authorization", "Bearer " + token);
		            return execution.execute(request, body);
		        }));
    }

    @When("^user email template call \"([^\"]*)\"$")
    public void user_call_something(String strArg1) throws Throwable {
    	response = restTemplate.getForEntity(baseUrl + "/moreservice" + strArg1, String.class);
    }

    @When("^user create email template with name (.+) subject (.+) body (.+)$")
    public void user_create_email_template_with_name_subject_body(String name, String subject, String body) throws Throwable {
    	EmailTemplate email = new EmailTemplate();
    	email.setName(name);
    	email.setSubject(subject);
    	email.setBody(body);
    	
    	response = restTemplate.postForEntity(baseUrl + "/moreservice/api/v1/email", email, String.class);
    }

    @When("^user edit email template with name (.+) set subject as (.+)$")
    public void user_edit_email_template_with_name_set_subject_as(String name, String subject) throws Throwable {
    	EmailTemplate email = emailRepo.findByName(name).get();
    	email.setSubject(subject);
    	
    	response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/email/" + email.getId(), HttpMethod.PUT, new HttpEntity<EmailTemplate>(email, headers), String.class);
    }

    @When("^user delete email template with name (.+)$")
    public void user_delete_email_template_with_name(String name) throws Throwable {
    	EmailTemplate email = emailRepo.findByName(name).get();
    	response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/email/" + email.getId(), HttpMethod.DELETE, new HttpEntity<EmailTemplate>(email, headers), String.class);
    }

    @Then("^user email template receives response status code (\\d+)$")
    public void user_email_template_receives_response_status_code_200(int statusCode) throws Throwable {
    	assertThat("status code is incorrect: " + response.getBody(), response.getStatusCodeValue(),Matchers.is(statusCode));
    }

    @Then("^user get all emails$")
    public void user_get_all_emails() throws Throwable {
    	JSONObject jsonObject = new JSONObject(response.getBody());
    	JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
    	assertThat("array length is incorrect: " + response.getBody(), jsonArray.length(), Matchers.is(emailRepo.findAll().size()));
    }

    @And("^user email template (.+) is created$")
    public void user_email_template_is_already_created(String name) throws Throwable {
    	assertTrue("Email template is not found", emailRepo.findByName(name).isPresent());
    }

    @And("^get email template subject name (.+) become (.+)$")
    public void get_email_template_subject_name_become(String name, String subject) throws Throwable {
    	EmailTemplate email = emailRepo.findByName(name).get();
    	assertTrue("Email template is not edited", email.getSubject().equals(subject));
    }

    @And("^email template (.+) is alreadey deleted$")
    public void email_template_is_alreadey_deleted(String name) throws Throwable {
    	assertFalse("Email template is not deleted", emailRepo.findByName(name).isPresent());
    }

	
}

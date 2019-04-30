package stepDefinitions;

import static org.hamcrest.Matchers.is; 
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.mitrais.more.payloads.LoginRequest;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LoginModuleTestStep{
	private LoginRequest loginParam;
	
	private TestRestTemplate restTemplate;
	
	private HttpHeaders headers;
	
	private ResponseEntity<String> response = null;
	
	private String baseUrl ="http://localhost:5000/moreservice";
	
	@Before
	public void init() {
		loginParam = new LoginRequest();
		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
	}

    @Then("^The server should handle it and return a success status$")
    public void the_server_should_handle_it_and_return_a_success_status() throws Throwable {
    	assertThat("status code is incorrect : " + response.getBody(), response.getStatusCodeValue(), is(200));
    }
    
	@When("^users input username \"([^\"]*)\" and password \"([^\"]*)\"$")
	public void users_input_username_something_and_password_something(String username, String password) throws Throwable {
	    String url = baseUrl + "/api/v1/auth"; 
		
	    loginParam.setUserParam(username);
		loginParam.setPassParam(password);
		
		HttpEntity<LoginRequest> entity = new HttpEntity<LoginRequest>(loginParam, headers);

		response = restTemplate.exchange(
				url,
				HttpMethod.POST, entity, String.class);
	}

	@Then("^users get response code of (\\d+)$")
	public void get_status_code_of(int statusCode) throws Throwable {
	    assertThat("status code is incorrect : " + response.getBody(), response.getStatusCodeValue(), is(statusCode));
	}
	
	@And("^users receives generated token$")
	public void get_generated_token() throws Throwable {
		JSONObject jsonObject = new JSONObject(response.getBody());
		assertTrue("Token is not generated", !jsonObject.getString("accessToken").isEmpty());
	}
	

}

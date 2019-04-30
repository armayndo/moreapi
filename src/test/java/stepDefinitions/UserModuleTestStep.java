package stepDefinitions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.mitrais.more.MoreBackEndApplication;
import com.mitrais.more.model.User;
import com.mitrais.more.payloads.LoginRequest;
import com.mitrais.more.repository.UserRepository;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@SpringBootTest(classes = MoreBackEndApplication.class)
@ActiveProfiles("INTEGRATION_TEST")
@ContextConfiguration
public class UserModuleTestStep {
	
	private LoginRequest loginParam;
	private ResponseEntity<String> response = null;
	private HttpHeaders headers;
	private TestRestTemplate restTemplate;
	
	private String baseUrl = "http://localhost:5000";
	
	private UserRepository userRepo;
	
	@Autowired
	public UserModuleTestStep(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public UserModuleTestStep() {}


	@Before
	public void init() {
		loginParam = new LoginRequest();
		headers = new HttpHeaders();
		restTemplate = new TestRestTemplate();
	}
	

    @Given("^user has logged in as admin$")
    public void user_has_logged_in_as_admin() throws Throwable {
        loginParam.setUserParam("admin");;
        loginParam.setPassParam("123456");;
        
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
    
    @Then("^user receives response status code (\\d+)$")
    public void user_receives_response_status_code_200(int statusCode) throws Throwable {
    	assertThat("status code is incorrect : " + response.getBody(), response.getStatusCodeValue(), is(statusCode));
    }

    @When("^user call \"([^\"]*)\"$")
    public void user_call_something(String strArg1) throws Throwable {
    	response = restTemplate.getForEntity(baseUrl + "/moreservice"+ strArg1, String.class);
    }

    @When("^user create user with (.+) (.+) (.+) and (.+)$")
    public void user_create_user_with_and(String username, String name, String email, String password) throws Throwable {
    	User user = new User();
    	user.setUsername(username);
    	user.setName(name);
    	user.setEmail(email);
    	user.setPassword(password);
        response = restTemplate.postForEntity(baseUrl + "moreservice/api/v1/users", user, String.class);
    }

    @When("^user edit user with username (.+) set name as (.+)$")
    public void user_edit_user_with_id_set_name_as(String username, String name) throws Throwable {
       User user = userRepo.findByUsername(username).get();
       user.setName(name);
       
       response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/users/"+user.getId(), HttpMethod.PUT, new HttpEntity<User>(user, headers), String.class);
    }

    @When("^user delete user with username (.+)$")
    public void user_delete_user_with_username(String username) throws Throwable {
    	User user = userRepo.findByUsername(username).get();
    	response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/users/" + user.getId(), HttpMethod.DELETE, new HttpEntity<User>(user, headers), String.class);
    }

    @Then("^user get all users$")
    public void user_get_all_users() throws Throwable {
    	JSONObject jsonObject = new JSONObject(response.getBody());
    	JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
    	assertThat("array length is incorrect : " + response.getBody(), jsonArray.length(), Matchers.is(userRepo.findAll().size()));
    }

    @And("^user (.+) is already created$")
    public void user_is_already_created(String username) throws Throwable {
        assertTrue("User is not found", userRepo.findByUsername(username).isPresent());
    }

    @And("^get users name username (.+) become (.+)$")
    public void get_users_name_id_become(String username, String name) throws Throwable {
        User user = userRepo.findByUsername(username).get();
        assertTrue("User is not edited", user.getName().equals(name));
    }
    
    @And("^user (.+) is alreadey deleted$")
    public void user_is_alreadey_deleted(String username) throws Throwable {
    	assertFalse("User is not deleted", userRepo.findByUsername(username).isPresent());
    }
}

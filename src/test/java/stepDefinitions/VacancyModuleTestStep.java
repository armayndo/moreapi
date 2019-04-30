package stepDefinitions;

import static org.hamcrest.Matchers.is;
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

import com.mitrais.more.model.Vacancy;
import com.mitrais.more.payloads.LoginRequest;
import com.mitrais.more.repository.VacancyRepository;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class VacancyModuleTestStep {
	private LoginRequest loginParam;

	private VacancyRepository vacantRepo;

	private TestRestTemplate restTemplate;

	private HttpHeaders headers;

	private ResponseEntity<String> response = null;

	private String baseUrl = "http://localhost:5000";

	@Autowired
	public VacancyModuleTestStep(VacancyRepository vacantRepo) {
		this.vacantRepo = vacantRepo;
	}

	public VacancyModuleTestStep() {
	}

	@Before
	public void init() {
		loginParam = new LoginRequest();

		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
	}

	@Given("^user has logged in as role user or admin for check vacancy$")
	public void user_vacancy_has_logged_in_as_role_user_or_admin() throws Throwable {
		loginParam.setUserParam("admin");;
		loginParam.setPassParam("123456");;

		HttpEntity<LoginRequest> entity = new HttpEntity<LoginRequest>(loginParam, headers);

		response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/auth", HttpMethod.POST, entity, String.class);
		JSONObject jsonObject = new JSONObject(response.getBody());
		String token = jsonObject.getString("accessToken");

		restTemplate.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
			request.getHeaders().add("Authorization", "Bearer " + token);
			return execution.execute(request, body);
		}));
	}

	@When("^user call endpoint \"([^\"]*)\"$")
	public void user_call_endpoint(String url_endpoint) throws Throwable {
		response = restTemplate.getForEntity(baseUrl + "/moreservice" + url_endpoint, String.class);
	}

	@When("^user create vacancy with (.+) and (.+)$")
	public void user_create_vacancy_with_and(String title, String descriptions) throws Throwable {
		Vacancy vacant = new Vacancy();
		vacant.setPosition(title);
		vacant.setDescription(descriptions);
		response = restTemplate.postForEntity(baseUrl + "moreservice/api/v1/vacancy", vacant, String.class);
	}

	@When("^user edit vacancy with id (.+) set title as (.+)$")
	public void user_edit_vacancy_with_id_set_title_as(String ids, String title) throws Throwable {
		long id = convertLong(ids);
		Vacancy vacant = vacantRepo.findById(id).get();
		vacant.setPosition(title);

		response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/vacancy/" + vacant.getId(), HttpMethod.PUT,
				new HttpEntity<Vacancy>(vacant, headers), String.class);

	}

	@When("^user delete vacancy with id (.+)$")
	public void user_delete_vacancy_with_id(String ids) throws Throwable {
		long id = convertLong(ids);
		Vacancy vacant = vacantRepo.findById(id).get();
		response = restTemplate.exchange(baseUrl + "/moreservice/api/v1/vacancy/" + vacant.getId(), HttpMethod.DELETE,
				new HttpEntity<Vacancy>(vacant, headers), String.class);
	}

	@Then("^user vacancy receives response status code 200$")
	public void user_vacancy_receives_response_status_code_200() throws Throwable {
		assertThat("status code is incorrect : " + response.getBody(), response.getStatusCodeValue(), is(200));
	}

	@Then("^user get all vacancy$")
	public void user_get_all_vacancy() throws Throwable {
		JSONObject jsonObject = new JSONObject(response.getBody());
		JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
		assertThat("array length is incorrect : " + response.getBody(), jsonArray.length(),
				Matchers.is(vacantRepo.findAll().size()));
	}

	@And("^get users title id (.+) become (.+)$")
	public void get_users_title_id_become(String ids, String title) throws Throwable {
		long id = Long.parseLong(ids);
		Vacancy vacant = vacantRepo.findById(id).get();
		assertTrue("User is not edited", vacant.getPosition().equals(title));
	}

	private long convertLong(String str) {
		try {
			return Long.valueOf(str);
		} catch (Exception e) {
			return 0;
		}
	}
}

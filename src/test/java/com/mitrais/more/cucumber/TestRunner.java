package com.mitrais.more.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/java/features",
		glue="stepDefinitions",
		tags= { 
				"@login,@user,@vacancy,@email,@candidate"
		},
		plugin = {"pretty","html:target/cucumber"}
		)
public class TestRunner {

}

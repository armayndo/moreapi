Feature: Testing the Candidate Module
Users should be able to use all Candidate Module Feature

@candidate
Scenario Outline: User candidate apply vacancy
When user candidate apply with name <name> email <email> phone <phone> birthdate <birthdate> address <address> to vacancy id <vacancyid>
Then user admin receives response status code 200
And candidate <email> applied successfully

Examples:
|name	|email	|phone			|birthdate	|address|vacancyid	|
|matth	|m@m.com|081234567891	|2/10/1993	|Bali	|1 			|
|test3	|t@t.com|089876543219	|22/3/1985	|Bali	|1 			|


Scenario: User candidate upload document
Given user upload document
Then user admin receives response status code 200
And document uploaded successfully

@candidate
Scenario: View all candidates
Given user admin logged in
When user admin call "/api/v1/candidate"
Then user admin receives response status code 200
And user admin get all candidates

@candidate
Scenario: View candidate's document
Given user admin logged in
When user admin call "/api/v1/candidate/doc/1"
Then user admin receives response status code 200
And user admin receive candidate document from candidate id ""



@candidate
Scenario: View candidate's vacancy applied
Given user admin logged in
When user admin call "/api/v1/candidate/1/vacancies"
Then user admin receives response status code 200
And user admin receive vacancy from candidate 1


@candidate
Scenario: Accept candidate application
Given user admin logged in
When user admin accept candidate application
Then user admin receives response status code 200
And candidate application accepted

@candidate
Scenario: Reject candidate application
Given user admin logged in
When user admin reject candidate application
Then user admin receives response status code 200
And candidate application rejected

@candidate
Scenario: Delete candidate
Given user admin logged in
When user admin delete candidate
Then user admin receives response status code 200
And candidate deleted





Feature: Testing the Vacancy Module
Users should be able to use all the Vacancy Module Features

@vacancy
Scenario: View All Vacancy
Given user has logged in as role user or admin for check vacancy
When user call endpoint "/api/v1/vacancy"
Then user vacancy receives response status code 200
Then user get all vacancy

@vacancy
Scenario Outline: Create Vacancy
Given user has logged in as role user or admin for check vacancy
When user create vacancy with <title> and <descriptions>
Then user vacancy receives response status code 200

Examples:
|title|description|
|Experienced Java Developer|We are looking for experienced developers who have been working with Java for 2 years or more|
|iOS Developers |We are looking for several iOS Developer to be based on our world class facilities in Jakarta|

@vacancy
Scenario Outline: Edit Vacancy
Given user has logged in as role user or admin for check vacancy
When user edit vacancy with id <id> set title as <title>
Then user vacancy receives response status code 200
And get users title id <id> become <title>

Examples:
|id|title|
|1|Experienced .Net Developer|
|2|Android Developers |

@vacancy
Scenario Outline: Delete Vacancy
Given user has logged in as role user or admin for check vacancy
When user delete vacancy with id <id>
Then user vacancy receives response status code 200

Examples:
|id|title|
|1|Experienced .Net Developer|
|2|Android Developers |
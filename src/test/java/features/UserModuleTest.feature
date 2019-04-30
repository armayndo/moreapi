Feature: Testing the User Module
Users should be able to use all the User Module Features

@user
Scenario: View All User
Given user has logged in as admin
When user call "/api/v1/users"
Then user receives response status code 200
Then user get all users

@user
Scenario Outline: Create User
Given user has logged in as admin
When user create user with <username> <name> <email> and <password>
Then user receives response status code 200
And user <username> is already created

Examples:
|name			|email		|password	|username	|
|matthew		|m@mail.com	|123456		|matth		|
|yudistyra		|y@mail.com	|abcdefg	|yudis		|
|adehermawan	|a@mail.com	|qwertyasdf	|adeher		|

@user
Scenario Outline: Edit User
Given user has logged in as admin
When user edit user with username <username> set name as <name>
Then user receives response status code 200
And get users name username <username> become <name>

Examples:
|username	|name			|
|matth		|matthewk		|
|yudis		|yudist			|
|adeher		|adeher			|

@user
Scenario Outline: Delete User
Given user has logged in as admin
When user delete user with username <username>
Then user receives response status code 200
And user <username> is alreadey deleted

Examples:
|username	|
|matth		|
|yudis		|
|adeher		|
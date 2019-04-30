Feature: Testing the Email Template Module
Users should be able to use all the Email Template Module Features

@email
Scenario: View All Email Template
Given user logged in as admin
When user email template call "/api/v1/email"
Then user email template receives response status code 200
Then user get all emails

@email
Scenario Outline: Create Email Template
Given user logged in as admin
When user create email template with name <name> subject <subject> body <body>
Then user email template receives response status code 200
And user email template <name> is created

Examples:
|name			|subject	|body		|
|testTemp		|Test		|123456		|
|confirm		|mySubject	|abcdefg	|
|templateEmail	|tempSubject|qwertyasdf	|

@email
Scenario Outline: Edit Email Template
Given user logged in as admin
When user edit email template with name <name> set subject as <subject>
Then user email template receives response status code 200
And get email template subject name <name> become <subject>

Examples:
|name			|subject	|
|testTemp		|Test123	|
|confirm		|mySubject1	|
|templateEmail	|tempSub	|

@email
Scenario Outline: Delete Email Template
Given user logged in as admin
When user delete email template with name <name>
Then user email template receives response status code 200
And email template <name> is alreadey deleted

Examples:
|name			|
|testTemp		|
|confirm		|
|templateEmail	|
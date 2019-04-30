Feature: Testing a login
Users should be able to login

@login
Scenario: Generate token
When users input username "admin" and password "123456"
Then users get response code of 200
And users receives generated token

@Login
Scenario: User Login
When users input username "admin" and password "123456"
Then users get response code of 200
And users receives generated token
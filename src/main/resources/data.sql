INSERT INTO user(username,password,email,name,active) VALUES ('admin', '$2a$10$6TajU85/gVrGUm5fv5Z8beVF37rlENohyLk3BEpZJFi6Av9JNkw9O', 'admin@mitrais.com','administrator',1);
INSERT INTO user(username,password,email,name,active) VALUES ('user', '$2a$10$6TajU85/gVrGUm5fv5Z8beVF37rlENohyLk3BEpZJFi6Av9JNkw9O', 'user@mitrais.com','user',1);
INSERT INTO `role` VALUES (1,'ROLE_ADMIN');
INSERT INTO `role` VALUES (2,'ROLE_RECRUITER');
INSERT INTO `role` VALUES (3,'ROLE_USER');
UPDATE user SET role_id = 1 where username = 'admin';
UPDATE user SET role_id = 3 where username = 'user';

INSERT INTO job_function(id,name,description) VALUES (1, 'Consultants', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (2, 'Designers', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (3, 'Managements', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (4, 'Software Engineers', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (5, 'Software Qualities', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (6, 'Students and Graduates', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (7, 'Support Services', 'bla bla');
INSERT INTO job_function(id,name,description) VALUES (8, 'YKIP', 'bla bla');

INSERT INTO lookup(name, keyword, value, description) VALUES ('SETTING', 'email_template_reply_on_apply', 'Email Template', 'Auto replying to candidate after submitted');
INSERT INTO lookup(name, keyword, value, description) VALUES ('SETTING', 'email_template_notif_to_admin', 'Applicant submitted', 'Send email notification to admin after candidates apply job');
INSERT INTO lookup(name, keyword, value, description) VALUES ('SETTING', 'hr_manager_signature', 'Riyan Permadi', 'Assgin HR Manager Signature when send broadcast email');

INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Experienced Java Developer','<p>Our company specialises in IT and has an exciting opportunity for an enthusiastic Experienced Java Developer to join our dynamic team. This permanent position is well suited to an individual that is looking to advance their career in marketing and gain hands-on experience in a thriving and supportive workplace.</p><p>Based within the marketing department, you will work closely with all areas of marketing, to assist with the design and production of exciting campaigns and helping the team to achieve agreed targets. This exciting position offers opportunity to progress into a bigger role.</p><p>Requirement :</p><ul><li>Has experience in Java at least 5 years</li><li>Good in communication in English</li><li>Excellent project management skills and attention detail</li></ul>', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Experienced PHP Developer','<p>Our company specialises in IT and has an exciting opportunity for an enthusiastic Experienced PHP Developer to join our dynamic team. This permanent position is well suited to an individual that is looking to advance their career in marketing and gain hands-on experience in a thriving and supportive workplace.</p><p>Based within the marketing department, you will work closely with all areas of marketing, to assist with the design and production of exciting campaigns and helping the team to achieve agreed targets. This exciting position offers opportunity to progress into a bigger role.</p><p>Requirement :</p><ul><li>Has experience in PHP at least 5 years</li><li>Good in communication in English</li><li>Excellent project management skills and attention detail</li></ul>', true, 'Vietnam',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Java Developer','<p>Our company specialises in IT and has an exciting opportunity for an enthusiastic Java Developer to join our dynamic team. This permanent position is well suited to an individual that is looking to advance their career in marketing and gain hands-on experience in a thriving and supportive workplace.</p><p>Based within the marketing department, you will work closely with all areas of marketing, to assist with the design and production of exciting campaigns and helping the team to achieve agreed targets. This exciting position offers opportunity to progress into a bigger role.</p><p>Requirement :</p><ul><li>Have experience in Java at least 1 years</li><li>Good in communication in English</li><li>Excellent project management skills and attention detail</li></ul>', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('PHP Developer','<p>Our company specialises in IT and has an exciting opportunity for an enthusiastic PHP Developer to join our dynamic team. This permanent position is well suited to an individual that is looking to advance their career in marketing and gain hands-on experience in a thriving and supportive workplace.</p><p>Based within the marketing department, you will work closely with all areas of marketing, to assist with the design and production of exciting campaigns and helping the team to achieve agreed targets. This exciting position offers opportunity to progress into a bigger role.</p><p>Requirement :</p><ul><li>Has experience in PHP at least 2 years</li><li>Good in communication in English</li><li>Excellent project management skills and attention detail</li></ul>', true, 'Vietnam',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('IOS Developer','Nulla consequat massa quis enim. ', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Android Developer','Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Experienced Tester','In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo.', true, 'Indonesia',5);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Experienced Frontend Developer','Nullam dictum felis eu pede mollis pretium. Integer tincidunt.', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Frontend Developer','Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus.', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('.NET Developer','Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim.', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Golang Developer','Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus.', true, 'Indonesia',4);
INSERT INTO vacancy(position,description,active,location,job_function_id) VALUES ('Ruby Developer','Phasellus viverra nulla ut metus varius laoreet.', true, 'Indonesia',4);

INSERT INTO email_template(name, subject, created_by, body, days_send) VALUES ('NEW', 'Interview Invitation', 1, '<p>Dear [CANDIDATE],<br><br>As a result of your application for the position of [VACANCY],<br>I would like to invite you to attend an interview on [SEND_DATE] at our office in Quincy, MA.<br><br>You will have an interview with the department manager, Edie Wilson.<br>The interview will last about 45 minutes. Please bring three references as well as a copy of your resume.<br><br>If the date or time of the interview is inconvenient, please contact me by phone (518-555-5555) or email recruitment.moreapp@gmail.com) to arrange another appointment.<br>We look forward to seeing you.<br><br><br>Best regards,&nbsp;<br><br><br>[RECRUITER_NAME]<br><br>&nbsp;</p>', 0);
INSERT INTO email_template(name, subject, created_by, body, days_send) VALUES ('HIRED', 'Hired Applicant', 1, 'Congratulations', 2);
INSERT INTO email_template(name, subject, created_by, body, days_send) VALUES ('TALENT_POOL', 'Talent Pool Applicant', 1, 'Congratulations', 2);
INSERT INTO email_template(name, subject, created_by, body, days_send) VALUES ('DECLINE', 'Decline Applicant', 1, 'Unfortunately', 2);
INSERT INTO email_template(name, subject, created_by, body, days_send) VALUES ('REJECT', 'Reject Applicant', 1, 'Unfortunately', 2);

INSERT INTO candidate(id, name, email, phone_number, birth_date, active) VALUES (1, 'Yudistyra', 'yudistyra_op355@mitrais.com', '123456', '1990-12-07', 1);
INSERT INTO candidate(id, name, email, phone_number, birth_date, active) VALUES (2, 'Matthew', 'MatthewKevin.Kalempouw@mitrais.com', '123456', '1990-12-07', 1);
INSERT INTO candidate(id, name, email, phone_number, birth_date, active) VALUES (3, 'Ade', 'ade@test.com', '123456', '1990-12-07', 1);
INSERT INTO candidate(id, name, email, phone_number, birth_date, active) VALUES (4, 'Fitri', 'fitri@test.com', '123456', '1990-12-07', 1);

INSERT INTO applicant(candidate_id, vacancy_id, apply_date, status) VALUES (1, 1, '2019-03-19', 'IN_PROGRESS');
INSERT INTO applicant(candidate_id, vacancy_id, apply_date, status) VALUES (1, 2, '2019-03-19', 'IN_PROGRESS');
INSERT INTO applicant(candidate_id, vacancy_id, apply_date, status) VALUES (2, 1, '2019-03-19', 'IN_PROGRESS');
INSERT INTO applicant(candidate_id, vacancy_id, apply_date, status) VALUES (2, 8, '2019-03-19', 'IN_PROGRESS');
INSERT INTO applicant(candidate_id, vacancy_id, apply_date, status) VALUES (3, 3, '2019-03-19', 'IN_PROGRESS');
INSERT INTO applicant(candidate_id, vacancy_id, apply_date, status) VALUES (4, 7, '2019-03-19', 'IN_PROGRESS');


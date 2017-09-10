insert into config (app_id, config_id, config_data)
values ('enterprise','admin_database',
      '{
  	"driverClass" : "com.mysql.jdbc.Driver",
	"enterprise_username" : "root",
	"enterprise_password" : "",
	"enterprise_url"	: "jdbc:mysql://localhost:3306/enterprise_admin?useSSL=false",
	"pseudonymised": false
}');

insert into config (app_id, config_id, config_data)
values ('enterprise','patient_database',
      '{
  	"driverClass" : "com.mysql.jdbc.Driver",
	"enterprise_username" : "root",
	"enterprise_password" : "",
	"enterprise_url"	: "jdbc:mysql://localhost:3306/enterprise_data_pseudonymised?useSSL=false",
	"pseudonymised": false
}');

insert into config (app_id, config_id, config_data)
values ('enterprise','data_sharing_manager',
      '{
  	"driverClass" : "com.mysql.jdbc.Driver",
	"enterprise_username" : "root",
	"enterprise_password" : "",
	"enterprise_url"	: "jdbc:mysql://localhost:3306/data_sharing_manager"
}');

insert into config (app_id, config_id, config_data)
values ('enterprise','application',
      '{ "appUrl" : "http://localhost:8080" }');
      
insert into config (app_id, config_id, config_data)
values ('enterprise','keycloak',
      '{
   "realm": "endeavour",
   "realm-public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7GdjckqAZgjxp/o7717ei5RgkW3mtG3W+LfmlboBt20NQ/Jz6yb00Xoe9dBCLsqiiompePWuBNxGdwUNHzJcng7hpTvsi7Zp8PtTJDts/EinroKEv+Gac2VB1k8aLneDOtU6FYdi7uQ4vVU4xJ4D4s1ubG0VQXqUnSUvwwRN5UDdGYLrV2KueajgsNJ3mML4aJ2rLDyUF5uvKQV1UbZAwvCUo0tIeUYoN6PMkpaUrBagWeLhNhrNU9HsiDbMUjVttDRgMlgCtYvu4GapI+0cVecAUWfg0MdTCYuUJwUtTZoatf3d2bietsS+cYPFfs9eCIm1/7GLZWwv6qFDN1a4ewIDAQAB",
   "auth-server-url": "https://devauth.endeavourhealth.net/auth",
   "ssl-required": "external",
   "resource": "eds-ui",
   "public-client": true
 }');





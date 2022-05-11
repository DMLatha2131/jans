INSERT INTO jansAppConf (doc_id,objectClass,dn,ou,jansEmail,jansSmtpConf,jansDbAuth,jansConfDyn,jansRevision)
	VALUES ('jans-client-api','jansAppConf','ou=jans-client-api,ou=configuration,o=jans','jans-client-api','{ 
  "loggingLevel":"INFO",
  "loggingLayout":"text",
  "externalLoggerConfiguration":"",
  "disableJdkLogger":true,
  "trustAllCerts": true,
  "keyStorePath":"/opt/jans/jans-client-api/keys/client-api-server.keystore",
  "keyStorePassword":"example",
  "enableJwksGeneration": true,
  "jwksExpirationInHours": 720,
  "jwksRegenerationIntervalInHours": 720,
  "cryptProviderKeyStorePath": "/opt/jans/jans-client-api/keys/client-api-jwks.keystore",
  "cryptProviderKeyStorePassword": "example",
  "cryptProviderDnName": "CN=jans-client CA Certificates",
  "mtlsEnabled": false,
  "mtlsClientKeyStorePath": "",
  "mtlsClientKeyStorePassword": "",
  "bindIpAddresses":["*"],
  "storage":"h2",
  "storageConfiguration":{
	  	"dbFileLocation":"/opt/jans/jans-client-api/data/rp_db"
  },
  "addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration":true,
  "migrationSourceFolderPath":"",
  "defaultSiteConfig":{
  	"op_configuration_endpoint":"",
  	"response_types":["code"],
  	"grant_type":["authorization_code"],
  	"acr_values":[""],
  	"scope":["openid", "profile", "email"],
  	"ui_locales":["en"],
  	"claims_locales":["en"],
  	"contacts":[],
  	"redirect_uris":[],
  	"logout_redirect_uris":[],
  	"client_name":"",
  	"client_jwks_uri":"",
  	"token_endpoint_auth_method":"",
  	"token_endpoint_auth_signing_alg":"",
  	"request_uris":[],
  	"front_channel_logout_uri":"",
  	"sector_identifier_uri":"",
  	"claims_redirect_uri":[],
  	"client_id":"",
  	"client_secret":"",
  	"trusted_client":false,
  	"access_token_as_jwt":false,
  	"access_token_signing_alg":"",
  	"rpt_as_jwt":false,
  	"logo_uri":"",
  	"client_uri":"",
  	"policy_uri":"",
  	"front_channel_logout_session_required":false,
  	"tos_uri":"",
  	"jwks":"",
  	"id_token_binding_cnf":"",
  	"tls_client_auth_subject_dn":"",
  	"run_introspection_script_beforeaccess_token_as_jwt_creation_and_include_claims":false,
  	"id_token_signed_response_alg":"",
  	"id_token_encrypted_response_alg":"",
  	"id_token_encrypted_response_enc":"",
  	"user_info_signed_response_alg":"",
  	"user_info_encrypted_response_alg":"",
  	"user_info_encrypted_response_enc":"",
  	"request_object_signing_alg":"",
  	"request_object_encryption_alg":"",
  	"request_object_encryption_enc":"",
  	"default_max_age":null,
  	"require_auth_time":false,
  	"initiate_login_uri":"",
  	"authorized_origins":[],
  	"access_token_lifetime":null,
  	"software_id":"",
  	"software_version":"",
  	"software_statement":"",
  	"custom_attributes":{}
  }
}
',1);


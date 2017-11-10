# CDN
Main functionality:
- Uploading pictures;
- Retrieving pictures;
- Usage statistics.

## API
### Uploading pictures
| | |
|----|----|
|HTTP metadata |POST|
|URI |/file|
|Request type |multipart/form-data|
|Request Headers |Authorization header with Basic authentication token|
|Request Body| File|
|Response Type| application/json|
|Response |Response parameters in case of successful upload are as follows: \Content url Example: { “url”:”http://my-service/file/12345” }|


### Retrieving pictures
| | |
|----|----|
|HTTP metadata |GET|
|URI |/file/{id}|
|Request Headers| Authorization header with Basic authentication token|
|Response Type |image/jpeg |
|Response Media |file|


### Usage statistics
| | |
|----|----|
|HTTP metadata |GET|
|URI |/statistics|
|Request Headers |Authorization header with Basic authentication token|
|Response Type |application/json|
|Response |Server should respond with json object which show statistics per file where file ID is the key and statistics are the value (see _statistic response_ example)|

#### Statistics response example
```json
{
   “file-id-1” : {
    “url” : “http://my-service/file/file-id-1”,
    “downloads” : 5
    },
   “file-id-2” : {
    “url” : “http://my-service/file/file-id-2”,
    “downloads” : 10
   }
}
```

##Implementation
The solution is implemented using:
 - Spring Boot;
 - embedded H2 as a storage for statistic info;
 - file system as a storage for pictures;
 - lombok library to rid of from boilerplate of Java language; 
 - Gradle as a build automation system. 
##### REST
The implementation follows the contract described above in ___API___ section (see package _asokol/resource_).
##### Security
The authentication is implemented using Spring Security (see package _asokol/security_ ).
To change an access to resources change __authentication.user.name__ and __authentication.user.password__ in _application.yml_.
##### Spring data
To store statistic data embedded H2 is used (see package _asokol/dao_).
H2 can be configured using appropriate properties (see _application.yml_ properties __spring.h2__ and __spring.datasource__)
To change the location of database and stored files use __storage.meta__ and __storage.files__


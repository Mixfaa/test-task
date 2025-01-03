See postman-collection

or
https://www.postman.com/lively-flare-749086/my-workspace/collection/008kkas/football-management?action=share&creator=21632373

to run:


* `cd run` 
* `docker-compose up --build`


uses 3306 port for MySQL

# Peculiarities:
### Exception factory
made for creating/accessing exception objects, different implementations can cache exceptions / disable stacktrace writing

# DbValidation
### Application has Database level integrity guards
such as MySQL Triggers and Checks

# InitialDatabaseFiller
### Fills database with initial football players and football teams

# Requests:
### FootballPlayer
### RegisterRequest
* String firstname
* String lastname
* Date dateOfBirth
* Date careerBeginning

### UpdateRequest
* String firstname
* String lastname
* Date dateOfBrith
* Date careerBeginning
* Long teamId

### FootballTeam
### RegisterRequest
* String name
* double transferCommission
* double balance
* Set<Long> playerIds

### UpdateRequest
* String name
* double transferCommission
* double balance
* Set<Long> playerIds

### FootballPlayerTransfer
### RegisterRequest
* long playerId
* long teamId
* Date date

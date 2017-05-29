# sentences-generator


### Description
A simple webapp exposing REST API acting as a generator of the sentences from the words inserted to it by rules described below. 
You can input any word to the system but you need to specify its part of speech - NOUN or VERB or OBJECTIVE.

### Prerequisites
 >- Maven
 >- Java 8 (Jdk)
 >- Git
 >- MongoDB
 >- Docker (Optional)

Tested on Ubuntu 64bit, OpenJdk 1.8, Maven 3.3.9., Git 1.9.1, MongoDB 3.4, Docker 17.05.0-ce

### Download project

```
git clone https://github.com/kafkapre/sentences-generator
```

### How to build

Go to project's directory and run command:

```
mvn package -DskipTests
```

### How to run tests
You have to run MongoDB locally (listening at port 27017) at first. E.g. you can start it with Docker command:

**!!! Important note: please be aware that test running clears repository. 
It can happen that your stored data will be deleted after tests run.** 

```
docker run --name mongo -p 27017:27017 -d mongo:3.4
```

Then you can run test by command:
```
mvn test
```

### How to run application

For example go to project's "target" directory and run command:

```
java -jar sentence-generator-1.0.jar --spring.config.location=app.yml
```

### Property file
```
server:
  port: 8080                            # webapp port
app:
  mongoHost: localhost                  # host of MongoDB server
  mongoPort: 27017                      # port of MongoDB server
  rejectedWords: ['aaaa', 'bbbb']       # list of words which which will be refused by server                         
```


### Rest API

#### Root endpoint

##### GET /api
Returns root api paths.

```
{
    "wordsPath":"/words",
    "sentencesPath":"/sentences"
}
```


#### Words endpoint

##### GET /api/words
Returns all words.

```
{[
    {
        "href":"api/words/home",
        "word":"home"
    },
    {
        "href":"api/words/and",
        "word":"and"
    }
]}
```

##### GET /api/words/{word}
Returns particular word.

```
{
    "word":"car",
    "href":"api/words/car",
    "category":"NOUN"
}
```


##### GET /api/words/refused
Returns list of words which will be refused.
```
{
    ["aaaa", "bbbb"] 
}
```

##### PUT /api/words/{word}
Sets word. Requires header: "Content-Type: application/json"

Response statuses:
>- 200 - Word was set
>- 400 - E.g. Request body does not contain "category" field  or cannot deserialize "category" value.
>- 405 - Word was refused
 
 - Request body

```
{    
    "category":"NOUN"
}
```

 - Response body
```
{
    "word":"{word}",
    "href": "api/words/{word}",
    "category":"NOUN"
}
```


#### Words endpoint

##### GET /sentences
Returns all generated sentences.

```
[
    {
        "id": "592c7d9e9e3db06eda819f4d",
        "href": "api/sentences/592c7d9e9e3db06eda819f4d",
        "text": "car is new"
    }
]
```

##### GET /sentences/{sentenceID}
Returns particular generated sentence.

```
{
    "id": "592c7d9e9e3db06eda819f4d",
    "href": "api/sentences/592c7d9e9e3db06eda819f4d",
    "text": "car is new",
    "showDisplayCount": 1,
    "generatedTimestampSec": 1496087966
}
```

##### GET /sentences/{sentenceID}/yodaTalk
Returns particular generated sentence in yoda talk format.

```
{
    "id": "592c7d9e9e3db06eda819f4d",
    "href": "api/sentences/592c7d9e9e3db06eda819f4d",
    "text": "new car is"
}
```


##### POST /sentences/generate (call without request body)
Generates random sentence.

Response statuses:
>- 201 - Sentence was created
>- 409 - Same sentence was already created. Returns previously created sentence and increments field "sameGeneratedCount".
 
 - Response
 
```
{
    "id": "592c7d9e9e3db06eda819f4d",
    "href": "api/sentences/592c7d9e9e3db06eda819f4d",
    "text": "car is new",
    "showDisplayCount": 0,
    "generatedTimestampSec": 1496087966
}
```
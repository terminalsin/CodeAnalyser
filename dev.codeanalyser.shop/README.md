# Shop
| **Service Name** 	| **Ports** 	| **Environment Variables** 	|
|------------------	|-----------	|---------------------------	|
| shop        	| 8080      	| EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localost:8761/eureka                       	|

Shop is the main API gateway for this specific infrastructure. Shop exposes a couple of API endpoints to allow for basic generation.

## Rest API Specification

All requests must contain an authorization header with a JWT token in the format of:
```
Authorization: Bearer <JWT Token>
```

### ⚒️ /my-data [GET]
---
Outputs back all the code modals pulled from the code storage module in the following format:

**Query**
```
curl http://localhost:8080/my-data -H "Authorization: Bearer <JWT Token>"
```

**Response**
```json
[
    {
        "id": "<UUID of code modal>",
        "code": "<code evaluated>",
        "fileName": "<ai generated name for the file",
        "score": "<score out of 100>",
        "results": [
            {
                "id": "<random long id>",
                "code": "<code being highlighted>",
                "highligh": "<highlight content>",
                "type": "<type of suggestion, one of: 'ERROR', 'WARNING', 'INFO', 'NONE'>"
            }
            // there can be more results here
        ]
    }
]
```

#### ☕ Java Model DTO

<details>
  <summary>CodeModalDto.java model</summary>

```java
public class CodeModalDto {
    private UUID id;
    private String ownerUsername;
    private String fileName;
    private String code;
    private List<CodeResult> results;
    private int score;

    public static class CodeResult {
        private Long id;
        private String code;
        private String highlight;
        private SuggestionType type;
    }

    public enum SuggestionType {
        ERROR,
        WARNING,
        INFO,
        NONE
    }
}
```
</details>


#### 💻 Typescript Interface
<details>
  <summary>CodeModalDto.tsx model</summary>

```ts
interface CodeModalDto {
    id: string;
    ownerUsername: string;
    code: string;
    fileName: string;
    score: number;
    reviews: CodeResult[];
}

interface CodeResult {
    id: number;
    code: string;
    highlight: string;
    type: 'NONE' | 'INFO' | 'WARNING' | 'ERROR';
}
```
</details>

### ⚒️ /generate [POST]
---
Replies back with empty query data (old REST was deprecated in favor of the WebSocket model)

**Query**
```
curl -d '<code here>' -X POST -H "Authorization: Bearer <JWT Token>" http://localhost:8080/generate
```

**Response**
```json
{
    "id": null,
    "status": null
}
```
#### ☕ Java Model DTO

<details>
  <summary>CodeModalStatusDto.java model</summary>

```java
public class CodeModalDto {
    private UUID id;
    private String status;
}
```
</details>


#### 💻 Typescript Interface
<details>
  <summary>CodeModalStatusDto.tsx model</summary>

```ts
interface CodeModalDto {
    id: string;
    status: string;
}
```
</details>

## WebSocket API Specifications

### ⚒️ /user/queue/review [WS]
---

Sends a codemodal every time it is added to an account to maintain synchronization with the frontend UI and keep everything dynamic


**Server Message format**
```json
{
    "id": "<UUID of code modal>",
    "code": "<code evaluated>",
    "fileName": "<ai generated name for the file",
    "score": "<score out of 100>",
    "results": [
        {
            "id": "<random long id>",
            "code": "<code being highlighted>",
            "highligh": "<highlight content>",
            "type": "<type of suggestion, one of: 'ERROR', 'WARNING', 'INFO', 'NONE'>"
        }
        // there can be more results here
    ]
}
```

#### ☕ Java Model DTO

<details>
  <summary>CodeModalDto.java model</summary>

```java
public class CodeModalDto {
    private UUID id;
    private String ownerUsername;
    private String fileName;
    private String code;
    private List<CodeResult> results;
    private int score;

    public static class CodeResult {
        private Long id;
        private String code;
        private String highlight;
        private SuggestionType type;
    }

    public enum SuggestionType {
        ERROR,
        WARNING,
        INFO,
        NONE
    }
}
```
</details>


#### 💻 Typescript Interface
<details>
  <summary>CodeModalDto.tsx model</summary>

```ts
interface CodeModalDto {
    id: string;
    ownerUsername: string;
    code: string;
    fileName: string;
    score: number;
    reviews: CodeResult[];
}

interface CodeResult {
    id: number;
    code: string;
    highlight: string;
    type: 'NONE' | 'INFO' | 'WARNING' | 'ERROR';
}
```
</details>

### ⚒️ /user/queue/review/status [WS]
---

Sends the current status of the operation being loaded. Is subscribed when the original "generate" request is done, the unsubscribed on the completed status.

**Server Message format**
```json
{
    "id": "<id of the code being analysed>",
    "status": "<current status>"
}
```
#### ☕ Java Model DTO

<details>
  <summary>CodeModalStatusDto.java model</summary>

```java
public class CodeModalDto {
    private UUID id;
    private String status;
}
```
</details>


#### 💻 Typescript Interface
<details>
  <summary>CodeModalStatusDto.tsx model</summary>

```ts
interface CodeModalDto {
    id: string;
    status: string;
}
```
</details>
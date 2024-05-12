# CodeAnalyser

| Module                                                                                                                     | Description                                                                                                                                                                                                                                                                                                                                                                                                               | Ports           | Author                               |
|----------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|--------------------------------------|
| [mainframe](./dev.codeanalyser.mainframe)                                                                                  | Mainframe discovery service responsible for linking all microservices together.  This microservice relies on Netflix Eureka and serves as a linking point to all others. It also serves a dashboard on the static http root path.                                                                                                                                                                                         | 8761 (Private)  | Shanyu Thibaut Juneja                |
| [modules.analysis.checkerfwk](./dev.codeanalyser.modules.analysis.java.checkerfwk) [**Experimental**]                      | Experimental Checker Framework static analysis. Currently not functional due to an  internal error, however the intent was to bring additional context analysis of java code in aggregated generated prompts.                                                                                                                                                                                                             | 8090  (Private) | Shanyu Thibaut Juneja                |
| [modules.analysis.spoon](./dev.codeanalyser.modules.analysis.java.spoon)                                                   | Functional INRIA Spoon static analyser. Currently performs null parameter checks on functional Java code and is capable of being extended. **Future work**: Hook the Spoon framework natively inside of spring boot to bring  dependency injection to the core to easily add further analysis.                                                                                                                            | 8091 (Private)  | Shanyu Thibaut Juneja                |
| [modules.code-storage](./dev.codeanalyser.modules.code-storage)                                                            | Microservice responsible for storing the code in an h2 container database. In development, redeployment will clear said database as it is stored as a temp  container.                                                                                                                                                                                                                                                    | 8799 (Private)  | Shanyu Thibaut Juneja                |
| [modules.gpt.llama](./dev.codeanalyser.modules.gpt.llama)                                                                  | OLlama Agent proxy for a local OLlama deployment to be able to interact with a Llama3 agent directly through a REST Api.                                                                                                                                                                                                                                                                                                  | 8082 (Private)  | Shanyu Thibaut Juneja                |
| [modules.gpt.openai](./dev.codeanalyser.modules.gpt.openai)                                                                | OpenAI Agent proxy for the official OpenAI api to interact with ChatGPT directly through a REST Api. This includes a personal key.                                                                                                                                                                                                                                                                                        | 8081 (Private)  | Shanyu Thibaut Juneja                |
| [shared.codemodal](./dev.codeanalyser.shared.codemodal)                                                                    | Shared API to share DTOs regarding code modal. **UNIMPLEMENTED YET**                                                                                                                                                                                                                                                                                                                                                      | n/a             | Shanyu Thibaut Juneja                |
| [shop](./dev.codeanalyser.shop)                                                                                            | Main exposed API towards the web. Handles all logic regarding the application, including the prompt, the circuit breaking, the load balancing and the application logic/testing. This is the main "core" of the app which makes use of all  microservices to best serve the frontend.                                                                                                                                     | 8080 (Public)   | Shanyu Thibaut Juneja                |
| [frontend](./code-analyser-frontend)                                                                                       | Frontend written in Next.js. Makes use of both server and client-sided components  to establish JWT based authentication using Supabase. All user logic is handled directly via supabase, and all storage is done via Spring by server side token authentication. This allows for us to maintain OAuth support for all integrations for free with high resiliency, and spares us the troubles of creating our own system. | 3000 (Public)   | Shanyu Thibaut Juneja  & Sonny Zhang |
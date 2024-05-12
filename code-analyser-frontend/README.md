# Code Analyzer

The purpose of this project is that when given a snippet of code, it would grade that given snippet of code according to an LLM analysis. This helps users understand any possible issues surrounding and relating to a given block of code.

## /app

Houses mostly routes, a few of them are legacy routes that have been rewired to be used as components instead such as /history, and due to constraints have been left as is.

- /login 
    Login credential page
- /protected 
    Main page for usage of code analysis.
- /stat
    Page for showcasing user statistics regarding code evaluation results.

## /components

Various components utilized on our routed web pages. Explanation is tedious and can be explored. A few notable components will be mentioned here.

- AveragePage
    Component responsible for displaying average code evaluation score
- NodeDetails & Node
    Components responsible for the rendering of our linted code evaluation. 
- BarGraph
    Component responsible for the render and access of the number of code blocks of a specific typeage that is specified
- ProgressStepper
    Component responsible for progression animation on code analysis.


## /utils/supabasae

Supabase initialization for setup and login authentication processing.


### User Flow Experience

Initially faced with the main page, the web page should display a banner, a nav bar and a section of text directing the user to login. Following the login, a given user can login with the use of email and password as account credentials. Supabase will send a confirmation email, allowing the user to login.

Proceeding the login, a user is directed to a new page, where they can upload code that can be prepared to be analyzed. A user should deposit code within the text area, and press analyze to trigger a response in the backend. The analysis identifies and categorizes the code into different labels and computes a code score based of the quality of the code. Following the analysis, the user can view their previous code uploads, as well as utilize the new sector of the web page meant for statistics. 

The statistics page congregates the data into computing the number of Info/Warning/Error messages in a visual medium, and also computes the average code score as computed by the analyzer.

### Design Considerations

Functionally, UI design is consisted of various components created and located in the components folder. This allows for modularity and ease of use in reapplying the same object across different web pages. There are no immediate design patterns that were utilized and/or applied to this project.

The design of the project is inspired by minimalist modern designs of web pages utilizing "AI" as their main product (usually just LLMs). Utilized Tailwind CSS and FlowbiteUI to help implement UI design. 

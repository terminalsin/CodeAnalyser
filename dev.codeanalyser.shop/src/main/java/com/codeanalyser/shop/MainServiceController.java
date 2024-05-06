package com.codeanalyser.shop;

import com.codeanalyser.codestorage.modal.CodeModal;
import com.codeanalyser.shop.gpt.ServiceDelegate;
import com.codeanalyser.shop.gpt.serializer.CodeModalJsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MainServiceController {

    @Autowired
    private ServiceDelegate serviceDelegate;


    @Autowired
    private CodeModalJsonParser codeModalJsonParser;

    @PostMapping("/generate")
    public CodeModal generateData(@RequestParam String code, @AuthenticationPrincipal UserDetails userDetails) {
        final String prompt = """
                Given the Java code snippet, Please provide a breakdown by analyzing its various components:
                1. For annotations and their usage.
                2. For relationships between entities like @OneToMany, @ManyToOne.
                3. For field declarations and their significance.
                4. For enum definitions and their roles.
                                
                For each component, specify:
                - A brief description.
                - Any suggestions for improvement or changes.
                - Highlight key areas that might need attention or are particularly well implemented.
                                
                Finally, provide an overall rating of the code on a scale from 1 to 10 based on best practices, efficiency, and clarity.
                
                The output should be EXCLUSIVELY in a json format of the following. DO NOT RESPOND WITH ANY PLAIN TEXT:
                {
                    "review": [
                        {
                            "code":"<code highlighted here, can be multiple lines>",
                            "highlight":"<recommendation, if any, for the code. Can be empty>",
                            "type":"<Type of notice. Can be 'ERROR', 'WARNING', 'INFO', 'NONE'>",
                        }
                        // do this until you've returned the entire code back in this format
                    ],
                    "score": <overall score of the code as an integer from 0-100>
                }
                
                Snippet:
                
                ```
                {input_code}
                ```
                """;

        final String username = userDetails.getUsername();
        final String output = serviceDelegate.callOpenAIService(prompt.replace("{input_code}", code));

        // Parse the output and return the CodeModal object
        System.out.println(output);

        try {
            // Parse the json output and set the results
            final CodeModal parsedCodeModal = codeModalJsonParser.parseJsonToCodeModal(output);
            parsedCodeModal.setCode(code);
            parsedCodeModal.setOwnerUsername(username);

            return parsedCodeModal;
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/use-agents")
    public String useAgents() {
        return serviceDelegate.callOpenAIService("Hello, I am a bot. How can I help you today?");
    }
}

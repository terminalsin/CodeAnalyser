package com.codeanalyser.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class MainServiceController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/use-agents")
    public String useAgents() {
        return generateText("Hello, I am a bot. How can I help you today?");
    }

    public String generateText(String prompt) {
        try {
            String response = getResponseFromOpenAI(prompt);
            if (response != null) {
                return response;
            }
        } catch (Exception ex) {
            System.out.println("OpenAI service is unavailable, switching to LLaMA.");
        }
        return getResponseFromLlama(prompt);
    }


    private String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) return null;
        return instances.get(0).getUri().toString();
    }

    public String getResponseFromOpenAI(String prompt) {
        String url = getServiceUrl("openai-service");
        if (url != null) {
            return restTemplate.postForObject(url + "/generate", prompt, String.class);
        }
        return null;
    }

    public String getResponseFromLlama(String prompt) {
        String url = getServiceUrl("llama-service");
        if (url != null) {
            return restTemplate.postForObject(url + "/generate", prompt, String.class);
        }
        return null;
    }
}

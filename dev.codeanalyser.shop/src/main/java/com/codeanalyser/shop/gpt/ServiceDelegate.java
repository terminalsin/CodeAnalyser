package com.codeanalyser.shop.gpt;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ServiceDelegate {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0).getUri().toString();
    }

    @CircuitBreaker(name = "openaiService", fallbackMethod = "fallbackOpenAI")
    public String callOpenAIService(String prompt) {
        String url = getServiceUrl("openai-service");
        if (url != null) {
            return restTemplate.postForObject(url + "/generate", prompt, String.class);
        }
        throw new RuntimeException("Service URL for OpenAI is not available");
    }

    @CircuitBreaker(name = "llamaService", fallbackMethod = "fallbackLlama")
    public String callLlamaService(String prompt) {
        String url = getServiceUrl("llama-service");
        if (url != null) {
            return restTemplate.postForObject(url + "/generate", prompt, String.class);
        }
        throw new RuntimeException("Service URL for LLaMA is not available");
    }

    public String fallbackOpenAI(String prompt, Throwable t) {
        System.out.println("Fallback due to " + t.getMessage());
        return callLlamaService(prompt);
    }

    public String fallbackLlama(String prompt, Throwable t) {
        return "All services are unavailable";
    }
}

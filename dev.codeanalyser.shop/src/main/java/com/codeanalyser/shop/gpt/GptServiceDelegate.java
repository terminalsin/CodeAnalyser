package com.codeanalyser.shop.gpt;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Consumer;

@Service
public class GptServiceDelegate {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0).getUri().toString();
    }

    @CircuitBreaker(name = "openai-service", fallbackMethod = "fallbackOpenAI")
    public String callOpenAIService(String prompt, Consumer<String> onStatus) {
        onStatus.accept("openai:start");
        System.out.println("Calling OpenAI service with prompt: " + prompt);
        String url = getServiceUrl("openai-service");
        if (url == null) {
            onStatus.accept("openai:failed");

            // circuit breaker doesn't work rn
            //throw new RestClientException("Service URL for OpenAI is not available");
            return fallbackOpenAI(prompt, onStatus, new RestClientException("Service URL for OpenAI is not available"));
        }
        try {
            final String answer = restTemplate.postForObject(url + "/generate", prompt, String.class);
            onStatus.accept("openai:completed");
            return answer;
        } catch (RestClientException e) {
            onStatus.accept("openai:failed");
            //throw e;
            // circuit breaker doesn't work rn
            return fallbackOpenAI(prompt, onStatus, e);
        }
    }

    @CircuitBreaker(name = "llama-service", fallbackMethod = "fallbackLlama")
    public String callLlamaService(String prompt, Consumer<String> onStatus) {
        System.out.println("Calling LLaMA service with prompt: " + prompt);
        onStatus.accept("llama:start");
        String url = getServiceUrl("llama-service");
        if (url != null) {
            final String answer = restTemplate.postForObject(url + "/generate", prompt, String.class);
            onStatus.accept("llama:completed");
            return answer;
        }

        onStatus.accept("llama:failed");
        throw new RestClientException("Service URL for LLaMA is not available");
    }

    public String fallbackOpenAI(String prompt, Consumer<String> onStatus, Throwable t) {
        onStatus.accept("llama:start");
        return callLlamaService(prompt, onStatus);
    }

    public String fallbackLlama(String prompt, Consumer<String> onStatus, Throwable t) {
        onStatus.accept("fallback:none");
        return "fallback:none";
    }
}

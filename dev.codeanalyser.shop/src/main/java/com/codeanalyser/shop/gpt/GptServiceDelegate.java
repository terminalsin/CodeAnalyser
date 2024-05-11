package com.codeanalyser.shop.gpt;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Consumer;

@Component
public class GptServiceDelegate {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0).getUri().toString();
    }

    @CircuitBreaker(name = "openaiService", fallbackMethod = "fallbackOpenAI")
    public String callOpenAIService(String prompt, Consumer<String> onStatus) {
        onStatus.accept("openai:start");
        String url = getServiceUrl("openai-service");
        if (url != null) {
            final String answer = restTemplate.postForObject(url + "/generate", prompt, String.class);
            onStatus.accept("openai:completed");
            return answer;
        }

        onStatus.accept("openai:failed");
        throw new RuntimeException("Service URL for OpenAI is not available");
    }

    @CircuitBreaker(name = "llamaService", fallbackMethod = "fallbackLlama")
    public String callLlamaService(String prompt, Consumer<String> onStatus) {
        onStatus.accept("llama:start");
        String url = getServiceUrl("llama-service");
        if (url != null) {
            final String answer = restTemplate.postForObject(url + "/generate", prompt, String.class);
            onStatus.accept("llama:completed");
            return answer;
        }

        onStatus.accept("llama:failed");
        throw new RuntimeException("Service URL for LLaMA is not available");
    }

    public String fallbackOpenAI(String prompt, Consumer<String> onFailure, Throwable t) {
       onFailure.accept("llama:start");
        return callLlamaService(prompt, onFailure);
    }

    public String fallbackLlama(String prompt, Throwable t) {
        return "fallback:none";
    }
}

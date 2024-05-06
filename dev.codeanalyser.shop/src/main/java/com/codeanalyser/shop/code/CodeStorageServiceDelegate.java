package com.codeanalyser.shop.code;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class CodeStorageServiceDelegate {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0).getUri().toString();
    }

    @CircuitBreaker(name = "codeStorageService", fallbackMethod = "fallback")
    public CodeModalDto saveCodeModal(CodeModalDto modal) {
        String url = getServiceUrl("code-storage-service");
        if (url != null) {
            return restTemplate.postForObject(url + "/api/v1/code", modal, CodeModalDto.class);
        }
        throw new RuntimeException("Service URL for Storage is not available");
    }

    @CircuitBreaker(name = "codeStorageService", fallbackMethod = "fallback")
    public List<CodeModalDto> getAllCodeModals(String username) {
        String url = getServiceUrl("code-storage-service");
        if (url != null) {
            return restTemplate.getForObject(url + "/api/v1/code?username=" + username, List.class);
        }
        throw new RuntimeException("Service URL for Storage is not available");
    }

    @CircuitBreaker(name = "codeStorageService", fallbackMethod = "fallback")
    public void deleteCodeModal(UUID id) {
        String url = getServiceUrl("code-storage-service");
        if (url != null) {
            restTemplate.delete(url + "/api/v1/code/" + id);
            return;
        }
        throw new RuntimeException("Service URL for Storage is not available");
    }

    public String fallback(CodeModalDto modal, Throwable t) {
        throw new RuntimeException("Service URL for Backup Storage is not available");
    }

    public String fallback(String username, Throwable t) {
        throw new RuntimeException("Service URL for Backup Storage is not available");
    }

    public String fallback(UUID id, Throwable t) {
        throw new RuntimeException("Service URL for Backup Storage is not available");
    }
}
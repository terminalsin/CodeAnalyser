package com.codeanalyser.shop;

import com.codeanalyser.shop.gpt.GptServiceDelegate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GptServiceDelegate_Test {

    @InjectMocks
    private GptServiceDelegate gptServiceDelegate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ServiceInstance serviceInstance;

    @Mock
    private DiscoveryClient discoveryClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should call OpenAI service successfully")
    public void callOpenAIServiceSuccess() {
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.singletonList(serviceInstance));
        when(serviceInstance.getUri()).thenReturn(java.net.URI.create("http://localhost:8080"));
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("response");
        String response = gptServiceDelegate.callOpenAIService("prompt", status -> {});
        assertEquals("response", response);
    }

    @Test
    @DisplayName("Should throw exception when OpenAI service URL is not available")
    public void callOpenAIServiceUrlNotAvailable() {
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.emptyList());
        assertThrows(RestClientException.class, () -> gptServiceDelegate.callOpenAIService("prompt", status -> {}));
    }

    @Test
    @DisplayName("Should call Llama service successfully")
    public void callLlamaServiceSuccess() {
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.singletonList(serviceInstance));
        when(serviceInstance.getUri()).thenReturn(java.net.URI.create("http://localhost:8080"));
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("response");
        String response = gptServiceDelegate.callLlamaService("prompt", status -> {});
        assertEquals("response", response);
    }

    @Test
    @DisplayName("Should throw exception when Llama service URL is not available")
    public void callLlamaServiceUrlNotAvailable() {
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.emptyList());
        assertThrows(RestClientException.class, () -> gptServiceDelegate.callLlamaService("prompt", status -> {}));
    }

    @Test
    @DisplayName("Should call Llama service when OpenAI service is down")
    public void callLlamaServiceWhenOpenAIServiceDown() {
        when(discoveryClient.getInstances("openai-service")).thenReturn(Collections.emptyList());
        when(discoveryClient.getInstances("llama-service")).thenReturn(Collections.singletonList(serviceInstance));
        when(serviceInstance.getUri()).thenReturn(java.net.URI.create("http://localhost:8080"));
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("response");

        String response = gptServiceDelegate.callOpenAIService("prompt", status -> {});

        assertEquals("response", response);
        verify(gptServiceDelegate, times(1)).fallbackOpenAI(any(String.class), any(Consumer.class), any(Throwable.class));
    }
}
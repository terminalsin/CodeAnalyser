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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GptServiceDelegateTest {

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
        // Setup mockito
        _mockRestTemplate();
        _mockServiceInstanceUp();

        // Cache
        final List<String> statusSink = new ArrayList<>();

        // Execute the method
        String response = gptServiceDelegate.callOpenAIService("prompt", statusSink::add);

        // Verify the results
        assertEquals("response", response);
        assertEquals(2, statusSink.size());
        assertEquals("openai:start", statusSink.get(0));
        assertEquals("openai:completed", statusSink.get(1));
    }

    @Test
    @DisplayName("Should throw exception when OpenAI & Llama service URL is not available")
    public void callAllDown() {
        // Setup mockito
        _openAiServiceDown();
        _llamaServiceDown();

        // Fallback is working
        assertThrows(RestClientException.class, () -> gptServiceDelegate.callOpenAIService("prompt", status -> {}));
    }

    @Test
    @DisplayName("Should call Llama service successfully")
    public void callLlamaServiceSuccess() {
        // Setup mockito
        _mockRestTemplate();
        _mockServiceInstanceUp();

        String response = gptServiceDelegate.callLlamaService("prompt", status -> {});
        assertEquals("response", response);
    }

    @Test
    @DisplayName("Should throw exception when Llama service URL is not available")
    public void callLlamaServiceUrlNotAvailable() {
        _mockServiceInstanceDown();

        assertThrows(RestClientException.class, () -> gptServiceDelegate.callLlamaService("prompt", status -> {}));
    }

    @Test
    @DisplayName("Should call Llama service when OpenAI service is down")
    public void callLlamaServiceWhenOpenAIServiceDown() {
        // Setup mockito
        _mockRestTemplate();
        _mockServiceInstanceUp();
        _openAiServiceDown();
        _llamaServiceUp();

        // Cache
        final List<String> statusSink = new ArrayList<>();

        // Execute the method
        String response = gptServiceDelegate.callOpenAIService("prompt", statusSink::add);

        // Verify the results
        assertEquals("response", response);

        final String[] expectedStatus = new String[] {"openai:start", "openai:failed", "llama:start", "llama:completed"};
        assertEquals(expectedStatus.length, statusSink.size());
        for (int i = 0; i < expectedStatus.length; i++) {
            assertEquals(expectedStatus[i], statusSink.get(i));
        }
    }

    private void _mockRestTemplate() {
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("response");
    }

    private void _mockServiceInstanceUp() {
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.singletonList(serviceInstance));
        when(serviceInstance.getUri()).thenReturn(java.net.URI.create("http://localhost:8080"));
    }

    private void _mockServiceInstanceDown() {
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.emptyList());
        when(serviceInstance.getUri()).thenReturn(null);
    }

    private void _openAiServiceDown() {
        when(discoveryClient.getInstances("openai-service")).thenReturn(Collections.emptyList());
    }

    private void _openAiServiceUp() {
        when(discoveryClient.getInstances("openai-service")).thenReturn(Collections.singletonList(serviceInstance));
    }

    private void _llamaServiceDown() {
        when(discoveryClient.getInstances("llama-service")).thenReturn(Collections.emptyList());
    }

    private void _llamaServiceUp() {
        when(discoveryClient.getInstances("llama-service")).thenReturn(Collections.singletonList(serviceInstance));
    }
}
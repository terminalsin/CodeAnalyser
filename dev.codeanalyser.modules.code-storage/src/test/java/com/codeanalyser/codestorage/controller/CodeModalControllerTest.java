package com.codeanalyser.codestorage.controller;

import com.codeanalyser.codestorage.modal.CodeModal;
import com.codeanalyser.codestorage.modal.CodeModalDto;
import com.codeanalyser.codestorage.service.CodeModalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CodeModalControllerTest {

    @InjectMocks
    private CodeModalController codeModalController;

    @Mock
    private CodeModalService codeModalService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(codeModalController).build();
    }

    @Test
    @DisplayName("Should save code modal successfully")
    public void saveCodeModalSuccess() throws Exception {
        when(codeModalService.saveCodeModal(any(CodeModal.class))).thenReturn(new CodeModal());

        final String codeModalDto = """
                {
                  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "fileName": "TestFile.java",
                  "ownerUsername": "testUser",
                  "code": "public class TestFile {\\n    public void testMethod() {\\n        System.out.println(\\"Hello, World!\\");\\n    }\\n}",
                  "results": [
                    {
                      "id": 1,
                      "code": "System.out.println(\\"Hello, World!\\")",
                      "highlight": "Print statement found",
                      "type": "INFO"
                    }
                  ],
                  "score": 10
                }
        """;

        mockMvc.perform(post("/api/v1/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(codeModalDto))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get code modal successfully")
    public void getCodeModalSuccess() throws Exception {
        when(codeModalService.getCodeModal(any(UUID.class))).thenReturn(java.util.Optional.of(new CodeModal()));

        mockMvc.perform(get("/api/v1/code/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return not found when getting non-existing code modal")
    public void getCodeModalNotFound() throws Exception {
        when(codeModalService.getCodeModal(any(UUID.class))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/v1/code/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all code modals successfully")
    public void getAllCodeModalsSuccess() throws Exception {
        CodeModal codeModal = new CodeModal();
        codeModal.setId(UUID.randomUUID());
        codeModal.setFileName(UUID.randomUUID().toString());
        codeModal.setOwnerUsername(UUID.randomUUID().toString());
        codeModal.setCode("public class Test { public void testMethod() { System.out.println(\"Hello, World!\"); } }");
        CodeModal.CodeResult codeResult = new CodeModal.CodeResult();
        codeResult.setId(1L);
        codeResult.setCode("System.out.println(\"Hello, World!\")");
        codeResult.setHighlight("Print statement found");
        codeResult.setType(CodeModal.SuggestionType.INFO);
        codeModal.setResults(Collections.singletonList(codeResult));
        codeModal.setScore(new Random().nextInt(100));

        when(codeModalService.getAllCodeModals(any(String.class))).thenReturn(Collections.singletonList(codeModal));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/code")
                        .param("username", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<CodeModal> returnedCodeModals = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, returnedCodeModals.size());
        CodeModal returnedCodeModal = returnedCodeModals.get(0);
        assertEquals(codeModal.getId(), returnedCodeModal.getId());
        assertEquals(codeModal.getFileName(), returnedCodeModal.getFileName());
        assertEquals(codeModal.getOwnerUsername(), returnedCodeModal.getOwnerUsername());
        assertEquals(codeModal.getCode(), returnedCodeModal.getCode());
        assertEquals(codeModal.getResults().size(), returnedCodeModal.getResults().size());
        assertEquals(codeModal.getScore(), returnedCodeModal.getScore());
    }

    @Test
    @DisplayName("Should delete code modal successfully")
    public void deleteCodeModalSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/code/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
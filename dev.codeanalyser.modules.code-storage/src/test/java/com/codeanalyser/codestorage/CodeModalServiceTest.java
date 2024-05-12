package com.codeanalyser.codestorage;

import com.codeanalyser.codestorage.modal.CodeModal;
import com.codeanalyser.codestorage.repository.CodeModalRepository;
import com.codeanalyser.codestorage.service.CodeModalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CodeModalServiceTest {

    @InjectMocks
    private CodeModalService codeModalService;

    @Mock
    private CodeModalRepository codeModalRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should save code modal successfully")
    public void saveCodeModalSuccess() {
        // Set up mockito
        CodeModal codeModal = new CodeModal();
        when(codeModalRepository.save(any(CodeModal.class))).thenReturn(codeModal);

        // Execute
        CodeModal savedCodeModal = codeModalService.saveCodeModal(codeModal);

        // Verify
        assertEquals(codeModal, savedCodeModal);
    }

    @Test
    @DisplayName("Should get code modal successfully")
    public void getCodeModalSuccess() {
        // Set up mockito
        UUID id = UUID.randomUUID();
        CodeModal codeModal = new CodeModal();
        when(codeModalRepository.findById(any(UUID.class))).thenReturn(Optional.of(codeModal));

        // Execute
        Optional<CodeModal> retrievedCodeModal = codeModalService.getCodeModal(id);

        // Verify
        assertTrue(retrievedCodeModal.isPresent());
        assertEquals(codeModal, retrievedCodeModal.get());
    }

    @Test
    @DisplayName("Should return empty when getting non-existing code modal")
    public void getCodeModalNotFound() {
        // Set up mockito
        UUID id = UUID.randomUUID();
        when(codeModalRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Execute
        Optional<CodeModal> retrievedCodeModal = codeModalService.getCodeModal(id);

        // Verify
        assertTrue(retrievedCodeModal.isEmpty());
    }

    @Test
    @DisplayName("Should get all code modals successfully")
    public void getAllCodeModalsSuccess() {
        // Set up mockito
        CodeModal codeModal = new CodeModal();
        when(codeModalRepository.findByOwnerUsername(any(String.class))).thenReturn(Collections.singletonList(codeModal));

        // Execute
        List<CodeModal> codeModals = codeModalService.getAllCodeModals("test");

        // Verify
        assertEquals(1, codeModals.size());
        assertEquals(codeModal, codeModals.get(0));
    }

    @Test
    @DisplayName("Should delete code modal successfully")
    public void deleteCodeModalSuccess() {
        UUID id = UUID.randomUUID();
        codeModalService.deleteCodeModal(id); // check there's no exception
    }
}
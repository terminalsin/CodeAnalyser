package com.codeanalyser.codestorage.service;

import com.codeanalyser.codestorage.modal.CodeModal;
import com.codeanalyser.codestorage.repository.CodeModalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CodeModalService {

    private final CodeModalRepository codeModalRepository;

    @Autowired
    public CodeModalService(CodeModalRepository codeModalRepository) {
        this.codeModalRepository = codeModalRepository;
    }

    public CodeModal saveCodeModal(CodeModal codeModal) {
        return codeModalRepository.save(codeModal);
    }

    public Optional<CodeModal> getCodeModal(UUID id) {
        return codeModalRepository.findById(id);
    }

    public List<CodeModal> getAllCodeModals(String username) {
        return codeModalRepository.findByOwnerUsername(username);
    }

    public void deleteCodeModal(UUID id) {
        codeModalRepository.deleteById(id);
    }
}
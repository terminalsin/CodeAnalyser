package com.codeanalyser.codestorage.controller;

import com.codeanalyser.codestorage.modal.CodeModal;
import com.codeanalyser.codestorage.modal.CodeModalDto;
import com.codeanalyser.codestorage.service.CodeModalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/code")
public class CodeModalController {

    private final CodeModalService codeModalService;

    @Autowired
    public CodeModalController(CodeModalService codeModalService) {
        this.codeModalService = codeModalService;
    }

    @PostMapping("")
    public ResponseEntity<CodeModal> saveCodeModal(@RequestBody CodeModalDto code) {
        CodeModal codeModal = codeModalService.saveCodeModal(code.toCodeModal());
        return ResponseEntity.ok(codeModal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodeModal> getCodeModal(@PathVariable UUID id) {
        return codeModalService.getCodeModal(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CodeModal>> getAllCodeModals(@RequestParam String username) {
        List<CodeModal> codeModals = codeModalService.getAllCodeModals(username);
        return ResponseEntity.ok(codeModals);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCodeModal(@PathVariable UUID id) {
        codeModalService.deleteCodeModal(id);
        return ResponseEntity.noContent().build();
    }
}
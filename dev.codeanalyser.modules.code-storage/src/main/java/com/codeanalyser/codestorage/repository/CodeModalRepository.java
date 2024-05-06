package com.codeanalyser.codestorage.repository;

import com.codeanalyser.codestorage.modal.CodeModal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodeModalRepository extends JpaRepository<CodeModal, UUID> {
    List<CodeModal> findByOwnerUsername(String username);
}
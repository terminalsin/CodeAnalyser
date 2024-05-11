package com.codeanalyser.codestorage.repository;

import com.codeanalyser.codestorage.modal.CodeModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodeModalRepository extends JpaRepository<CodeModal, UUID> {
    List<CodeModal> findByOwnerUsername(String username);
}
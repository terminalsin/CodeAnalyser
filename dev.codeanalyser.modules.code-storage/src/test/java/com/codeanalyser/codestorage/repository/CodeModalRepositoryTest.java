package com.codeanalyser.codestorage.repository;

import com.codeanalyser.codestorage.modal.CodeModal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CodeModalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CodeModalRepository codeModalRepository;

    @BeforeEach
    void setup() {
        // Assuming CodeModal has a constructor that initializes the UUID
        CodeModal code1 = new CodeModal();
        code1.setOwnerUsername("user1");
        code1.setCode("System.out.println('Hello, World!');");
        entityManager.persistAndFlush(code1);  // Ensure entity is managed and immediately written to the database.

        CodeModal code2 = new CodeModal();
        code2.setOwnerUsername("user2");
        code2.setCode("System.out.println('Goodbye, World!');");
        entityManager.persistAndFlush(code2);  // Use persistAndFlush to immediately synchronize with the database.
    }

    @Test
    public void whenFindByOwnerUsername_thenReturnCodeModalList() {
        List<CodeModal> foundCodes = codeModalRepository.findByOwnerUsername("user1");

        assertThat(foundCodes).hasSize(1);
        assertThat(foundCodes.get(0).getOwnerUsername()).isEqualTo("user1");
        assertThat(foundCodes.get(0).getCode()).contains("Hello, World!");
    }
}

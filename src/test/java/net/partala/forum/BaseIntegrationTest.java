package net.partala.forum;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Uncompleted, will be changed in future IntegrationTest classes.
 * [Brings up test database and cleans it before each test]
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    protected void truncateMappedObjects() {
        entityManagerFactory
                .unwrap(SessionFactoryImplementor.class)
                .getSchemaManager()
                .truncateMappedObjects();
    }
}

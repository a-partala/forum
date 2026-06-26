package net.partala.forum;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public class BaseIntegrationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    public void truncateMappedObjects() {
        entityManagerFactory
                .unwrap(SessionFactoryImplementor.class)
                .getSchemaManager()
                .truncateMappedObjects();
    }
}

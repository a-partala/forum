package net.partala.forum.realm;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RealmRepository extends JpaRepository<RealmEntity, Long> {

    @Query("""
            SELECT r FROM RealmEntity r
            WHERE
            ((r.parentId IS NULL AND :parentRealmId IS NULL)
            OR
            (r.parentId = :parentRealmId))
            
            AND (:ownerId IS NULL OR r.owner.id = :ownerId)
            """)
    List<RealmEntity> searchByFilter(Long parentRealmId,
                                     Long ownerId,
                                     Pageable pageable);

    Optional<RealmEntity> findByName(String name);
}

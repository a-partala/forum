package net.partala.forum.vote;

import lombok.Getter;
import net.partala.forum.common.RatableEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class VoteRepository {

    @Getter
    private final Class<? extends RatableEntity> entityClass;
    private final Function<Long, Long> creatorFinderById;
    protected final String tableName;
    protected final String entityIdColumnName;
    protected final NamedParameterJdbcTemplate jdbc;

    protected VoteRepository(NamedParameterJdbcTemplate jdbc,
                             String votesTableName,
                             String entityIdColumnName,
                             Class<? extends RatableEntity> entityClass,
                             Function<Long, Long> creatorFinderById) {
        this.tableName = votesTableName;
        this.entityIdColumnName = entityIdColumnName;
        this.jdbc = jdbc;
        this.entityClass = entityClass;
        this.creatorFinderById = creatorFinderById;
    }

    public void addVote(Long userId, Long entityId, VoteType voteType) {
        jdbc.update(String.format("""
                    INSERT INTO %s (user_id, %s, val)
                    VALUES (:userId, :entityId, :voteType)
                """, tableName, entityIdColumnName),
                Map.of(
                        "userId", userId,
                        "entityId", entityId,
                        "voteType", voteType.value
                ));
    }

    public void updateVote(Long userId, Long entityId, VoteType voteType) {
        jdbc.update(String.format("""
                    UPDATE %s
                    SET val = :voteType
                    WHERE user_id = :userId
                    AND %s = :entityId
                """, tableName, entityIdColumnName),
                Map.of(
                        "userId", userId,
                        "entityId", entityId,
                        "voteType", voteType.value
                ));
    }

    public void deleteVote(Long userId, Long entityId) {
        jdbc.update(String.format("""
                    DELETE FROM %s
                    WHERE user_id = :userId
                    AND %s = :entityId
                """, tableName, entityIdColumnName),
                Map.of(
                        "userId", userId,
                        "entityId", entityId
                ));
    }

    public Optional<VoteType> getVote(Long userId, Long entityId) {
        return jdbc.query(String.format("""
                    SELECT val FROM %s
                    WHERE user_id = :userId
                    AND %s = :entityId
                """, tableName, entityIdColumnName),
                Map.of(
                        "userId", userId,
                        "entityId", entityId
                ),
                rs -> rs.next() ? Optional.of(VoteType.of(rs.getShort("val"))) : Optional.empty());
    }

    public long getRating(Long entityId) {
        var result = jdbc.query(String.format("""
                    SELECT SUM(val) as "sum" FROM %s
                    WHERE %s = :entityId
                """, tableName, entityIdColumnName),
                Map.of(
                        "entityId", entityId
                ),
                rs -> rs.next() ? rs.getLong("sum") : 0);

        return result == null ? 0 : result;
    }

    public long getCreatorId(Long entityId) {
        return creatorFinderById.apply(entityId);
    }
}

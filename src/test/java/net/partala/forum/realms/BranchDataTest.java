package net.partala.forum.realms;

import net.partala.forum.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchDataTest {

    private UserEntity user = new UserEntity();

    @Test
    void of_MatchFields_WhenNullOrigin() {
        var branch = BranchData.of(null, null);

        assertAll(
                () -> assertNull(branch.origin()),
                () -> assertTrue(branch.isRoot()),
                () -> assertEquals(0, branch.size()),
                () -> assertEquals(0, branch.ancestorOwners().size())
        );
    }

    @Test
    void of_MatchAncestorOwnersAmount() {
        int size = 5;
        int ownersAmount = 2;
        var realms = createRealmsChain(size);

        UserEntity baseOwner = new UserEntity();
        ReflectionTestUtils.setField(baseOwner, "id", 0L);
        for(long i = 0; i < size; i++) {
            var realm = realms.get(i);
            realm.setOwner(baseOwner);
        }
        for(long i = 0; i < ownersAmount; i++) {
            UserEntity newOwner = new UserEntity();
            ReflectionTestUtils.setField(newOwner, "id", i);
            var realm = realms.get(i);
            realm.setOwner(newOwner);
        }

        var branch = BranchData.of(0L, id -> getById(realms, id));

        assertEquals(ownersAmount, branch.ancestorOwners().size());
    }

    @Test
    void of_MatchBranchSizes() {
        int size = 10;
        var realms = createRealmsChain(size);

        var branch = BranchData.of(0L, id -> getById(realms, id));

        assertEquals(size, branch.size());
    }

    @Test
    void of_ThrowIllegalStateException_WhenCyclicBranch() {
        int size = 10;
        var realms = createRealmsChain(size);
        var lastRealm = realms.get(realms.size() - 1L);
        ReflectionTestUtils.setField(lastRealm, "parentId", 0L);

        Executable executable = () -> BranchData.of(0L, id -> getById(realms, id));

        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void of_ThrowIllegalStateException_WhenSingleCyclicRealm() {
        int size = 1;
        var realms = createRealmsChain(size);
        var lastRealm = realms.get(realms.size() - 1L);
        ReflectionTestUtils.setField(lastRealm, "parentId", 0L);

        Executable executable = () -> BranchData.of(0L, id -> getById(realms, id));

        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void of_ThrowIllegalStateException_WhenOriginIdIsUnknown() {
        int size = 3;
        var realms = createRealmsChain(size);

        Executable executable = () -> BranchData.of(99L, id -> getById(realms, id));

        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void of_ThrowIllegalStateException_WhenSomeParentIdInChainIsUnknown() {
        int size = 3;
        var realms = createRealmsChain(size);
        var lastRealm = realms.get(realms.size() - 1L);
        ReflectionTestUtils.setField(lastRealm, "parentId", 99L);

        Executable executable = () -> BranchData.of(0L, id -> getById(realms, id));

        assertThrows(IllegalStateException.class, executable);
    }

    //root is the last realm
    private Map<Long, RealmEntity> createRealmsChain(int length) {
        Map<Long, RealmEntity> realms = HashMap.newHashMap(length);
        ReflectionTestUtils.setField(user, "id", 1L);
        for (long i = 0L; i < length; i++) {
            Long parentId = i + 1;
            if(i == length - 1) {
                parentId = null;
            }
            var realm = new RealmEntity("", "", user, parentId);
            realms.put(i, realm);
            ReflectionTestUtils.setField(realm, "id", i);
        }

        return realms;
    }

    private Optional<RealmEntity> getById(Map<Long, RealmEntity> realms, Long id) {
        if(!realms.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(realms.get(id));
    }
}
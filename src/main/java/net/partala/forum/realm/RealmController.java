package net.partala.forum.realm;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import net.partala.forum.realm.dto.CreateRealmRequest;
import net.partala.forum.realm.dto.RealmResponse;
import net.partala.forum.realm.dto.RealmSearchFilter;
import net.partala.forum.user.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/realms")
public class RealmController {

    private final RealmService realmService;

    public RealmController(RealmService realmService) {
        this.realmService = realmService;
    }

    @GetMapping
    public ResponseEntity<List<RealmResponse>> searchByFilter(
            @RequestParam(value = "parentRealmId", required = false) Long parentRealmId,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageId", required = false) Integer pageId
    ) {
        log.info("called search by filter");

        var result = realmService.searchByFilter(new RealmSearchFilter(parentRealmId, ownerId, pageSize, pageId));
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping
    public ResponseEntity<RealmResponse> createRealm(@RequestBody @Valid CreateRealmRequest request,
                                         @AuthenticationPrincipal UserContext userContext) {
        log.info("called createRealm for {}", request);

        var response = realmService.createRealm(
                request,
                userContext);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

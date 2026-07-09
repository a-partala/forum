package net.partala.forum.realm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import net.partala.forum.common.PageMapper;
import net.partala.forum.realm.dto.*;
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

    @GetMapping("/{id}")
    public ResponseEntity<RealmResponse> getRealmById(@PathVariable("id") @NotNull @Positive Long id) {

        log.info("getRealmById called with id {}", id);
        var response = realmService.getRealmById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RealmResponse>> searchByParent(
            @RequestParam(value = "parentRealmId", required = false) Long parentRealmId,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageId", required = false) Integer pageId
    ) {
        log.info("called search by filter");

        var result = realmService.searchByFilter(parentRealmId, PageMapper.pageableOf(pageSize, pageId));
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

    @PutMapping("/{id}")
    public ResponseEntity<RealmResponse> updateRealm(@PathVariable("id") @NotNull @Positive Long id,
                                                     @RequestBody @Valid RealmContentRequest request,
                                                     @AuthenticationPrincipal UserContext userContext) {
        log.info("called updateRealm for {}", request);

        var response = realmService.updateRealm(
                id,
                request,
                userContext);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

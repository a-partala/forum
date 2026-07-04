package net.partala.forum.common;

import org.springframework.data.domain.Pageable;

public class PageMapper {
    private PageMapper() {}

    public static Pageable pageableOf(Integer pageId, Integer pageSize) {
        return Pageable
                .ofSize(pageSize != null ? pageSize : 10)
                .withPage(pageId != null ? pageId : 0);
    }
}

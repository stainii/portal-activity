package be.stijnhooft.portal.activity.factory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PageRequestFactory {

    public static Optional<PageRequest> create(Integer page, Integer size, Sort sort) {
        if (page == null || size == null) {
            return Optional.empty();
        }
        if (sort == null) {
            sort = Sort.unsorted();
        }
        return Optional.of(PageRequest.of(page, size, sort));
    }

}

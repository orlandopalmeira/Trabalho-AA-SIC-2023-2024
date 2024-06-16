package com.grupo6.votingapp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginationHelper {

    private PaginationHelper() {}

    public static <T> Page<T> paginate(List<T> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<T> subList;

        if (list.size() < startItem) {
            subList = List.of(); // empty list if startItem is greater than the list size
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            subList = list.subList(startItem, toIndex);
        }

        return new PageImpl<>(subList, PageRequest.of(currentPage, pageSize), list.size());
    }
}


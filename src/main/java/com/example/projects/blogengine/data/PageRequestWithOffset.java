package com.example.projects.blogengine.data;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestWithOffset implements Pageable {
    private int limit;
    private long offset;
    private Sort sort;

    public PageRequestWithOffset(int limit, long offset, Sort sort) {
        if (limit < 1) throw new IllegalArgumentException("Limit must be positive integer greater then zero");
        if (offset < 0) throw new IllegalArgumentException("Offset index must be positive integer");
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new PageRequestWithOffset(limit, offset + limit , sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return new PageRequestWithOffset(limit, 0, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    public int getLimit() {
        return limit;
    }
}

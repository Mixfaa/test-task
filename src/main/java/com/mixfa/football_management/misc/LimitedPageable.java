package com.mixfa.football_management.misc;

import com.mixfa.football_management.exception.PageSizeException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LimitedPageable implements Pageable {
    public final static int PAGE_SIZE_LIMIT = 15;
    private final Pageable pageable;

    public LimitedPageable(Pageable pageable) throws PageSizeException {
        if (pageable.getPageSize() > PAGE_SIZE_LIMIT)
            throw PageSizeException.pageSizeException();

        this.pageable = pageable;
    }

    @Override
    public int getPageNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getPageSize() {
        return pageable.getPageSize();
    }

    @Override
    public long getOffset() {
        return pageable.getOffset();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public Pageable next() {
        return pageable.next();
    }

    @Override
    public Pageable previousOrFirst() {
        return pageable.previousOrFirst();
    }

    @Override
    public Pageable first() {
        return pageable.first();
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return pageable.withPage(pageNumber);
    }

    @Override
    public boolean hasPrevious() {
        return pageable.hasPrevious();
    }

    public static LimitedPageable toLimited(Pageable pageable) throws PageSizeException {
        return new LimitedPageable(pageable);
    }

    public static LimitedPageable of(int page, int pageSize) throws PageSizeException {
        if (pageSize > PAGE_SIZE_LIMIT) throw PageSizeException.pageSizeException();
        return new LimitedPageable(PageRequest.of(page, pageSize));
    }
}

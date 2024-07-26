package com.example.scrap.base.data;

import java.time.LocalDate;

public class DefaultData {

    public static final String PAGING_SIZE = "10";

    public static final LocalDate START_DATE = LocalDate.of(2024, 5, 1);

    public static final CharSequence AUTH_PREFIX = "Bearer ";

    public static final Long MIN_REFRESH_TOKEN_ID = 1L;
    public static final Long MAX_REFRESH_TOKEN_ID = Long.MAX_VALUE;
}

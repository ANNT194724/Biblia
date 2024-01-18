package com.biblia.utils;

public interface Constants {

    interface ACCOUNT_STATUS {
        int INACTIVE = 0;
        int ACTIVE = 1;
    }

    interface DELETE_FLAG {
        int DELETED = 0;
        int NOT_DELETED = 1;
    }

    interface ROLE_CODE {
        String ADMIN = "ADMIN";
        String MODERATOR = "MODERATOR";
        String USER = "USER";
    }

    interface BOOK_STATUS {
        int WAITING = 0;
        int VERIFIED = 1;
    }

    interface AUTHOR_STATUS {
        int WAITING = 0;
        int VERIFIED = 1;
    }

    interface SELLER_STATUS {
        int ACTIVE = 1;
        int INACTIVE = 0;
    }

    interface REVIEW_STATUS {
        int HIDDEN = 0;
        int NOT_HIDDEN = 1;
    }

    interface SORT_BY {
        String TITLE = "TITLE";
        String RATING = "RATING";
    }

    interface SORT_DIRECTION {
        int ASC = 0;
        int DESC = 1;
    }

    interface PAGINATION {
        int DEFAULT_PAGE = 1;
        int DEFAULT_SIZE = 20;
    }
}

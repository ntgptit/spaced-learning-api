package com.spacedlearning.dto.user_books;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookShareInfo {
    private UUID bookId;
    private String bookName;
    private int sharedWithUserCount;
    private List<UserShareDetail> sharedWith;

}

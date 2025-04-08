package com.spacedlearning.dto.user_books;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShareDetail {
    private UUID userId;
    private String username;
    private String email;

}

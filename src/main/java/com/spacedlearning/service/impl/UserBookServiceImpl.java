package com.spacedlearning.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.user_books.UserBookShareInfo;
import com.spacedlearning.dto.user_books.UserShareDetail;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.BookMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.UserBookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserBookService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserBookServiceImpl implements UserBookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BookSummaryResponse> getBooksForUser(UUID userId) {
        Objects.requireNonNull(userId, "User ID must not be null");
        log.debug("Getting books for user ID: {}", userId);

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User", userId));

        return user.getBooks().stream()
                .filter(book -> !book.isDeleted())
                .map(bookMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBookShareInfo> getAllSharedBooks() {
        log.debug("Getting all shared book information");

        final List<Book> allBooks = bookRepository.findAll();
        final List<UserBookShareInfo> result = new ArrayList<>();

        for (final Book book : allBooks) {
            if (book.getUsers() != null && !book.getUsers().isEmpty()) {
                final UserBookShareInfo shareInfo = new UserBookShareInfo();
                // Set book info
                shareInfo.setBookId(book.getId());
                shareInfo.setBookName(book.getName());
                shareInfo.setSharedWithUserCount(book.getUsers().size());

                // Set user details
                final List<UserShareDetail> userDetails = book.getUsers().stream()
                        .map(user -> {
                            final UserShareDetail detail = new UserShareDetail();
                            detail.setUserId(user.getId());
                            detail.setUsername(user.getUsername());
                            detail.setEmail(user.getEmail());
                            return detail;
                        })
                        .collect(Collectors.toList());

                shareInfo.setSharedWith(userDetails);
                result.add(shareInfo);
            }
        }

        return result;
    }
}
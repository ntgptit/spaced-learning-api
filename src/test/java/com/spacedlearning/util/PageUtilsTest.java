package com.spacedlearning.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.spacedlearning.dto.common.PageResponse;

class PageUtilsTest {
    @Test
    void testCreatePageResponse() {
        List<String> content = Arrays.asList("test1", "test2");
        PageRequest pageable = PageRequest.of(0, 10);
        Function<String, String> mapper = String::toUpperCase;

        PageResponse<String> response = PageUtils.createPageResponse(content, 2L, pageable, mapper);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("TEST1", response.getContent().get(0));
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(2L, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
    }

    @Test
    void testCreatePageResponseWithNullContent() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        assertThrows(NullPointerException.class, () -> PageUtils
                .<String, String>createPageResponse(null, 0L, pageRequest, String::toUpperCase));
    }

    @Test
    void testCreatePageResponse2() {
        Page<String> page =
                new PageImpl<>(Arrays.asList("test1", "test2"), PageRequest.of(0, 10), 2);
        PageRequest pageable = PageRequest.of(0, 10);

        PageResponse<String> response = PageUtils.createPageResponse(page, pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("test1", response.getContent().get(0));
        assertEquals(0, response.getPage());
        assertTrue(response.getFirst());
    }

    @Test
    void testCreatePageResponse3() {
        Page<String> page = new PageImpl<>(Arrays.asList("test1", "test2"));
        Function<String, String> mapper = String::toUpperCase;

        PageResponse<String> response = PageUtils.createPageResponse(page, mapper);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("TEST1", response.getContent().get(0));
        assertEquals(0, response.getPage());
    }

    @Test
    void testEmptyPageResponse() {
        Pageable pageable = PageRequest.of(0, 10);

        PageResponse<String> response = PageUtils.emptyPageResponse(pageable);

        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
        assertEquals(0, response.getPage());
        assertEquals(0L, response.getTotalElements());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
    }


}

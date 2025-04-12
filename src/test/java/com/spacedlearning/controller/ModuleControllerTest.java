package com.spacedlearning.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.service.ModuleService;



@WebMvcTest(ModuleController.class)
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ModuleService moduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateModule() throws Exception {
        ModuleCreateRequest request = new ModuleCreateRequest();
        ModuleDetailResponse response = new ModuleDetailResponse();

        when(moduleService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/modules").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());

        verify(moduleService, times(1)).create(any());
    }

    @Test
    void testDeleteModule() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(moduleService).delete(id);

        mockMvc.perform(delete("/api/v1/modules/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Module deleted successfully"));

        verify(moduleService, times(1)).delete(id);
    }

    @Test
    void testGetAllModules() throws Exception {
        Page<ModuleSummaryResponse> response = new PageImpl<>(List.of());

        when(moduleService.findAll(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/modules")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(moduleService, times(1)).findAll(any());
    }

    @Test
    void testGetAllModulesByBookId() throws Exception {
        UUID bookId = UUID.randomUUID();
        List<ModuleSummaryResponse> response = List.of(new ModuleSummaryResponse());

        when(moduleService.findAllByBookId(bookId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/modules/book/{bookId}/all", bookId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(moduleService, times(1)).findAllByBookId(bookId);
    }

    @Test
    void testGetModule() throws Exception {
        UUID id = UUID.randomUUID();
        ModuleDetailResponse response = new ModuleDetailResponse();

        when(moduleService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/modules/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(moduleService, times(1)).findById(id);
    }

    @Test
    void testGetModulesByBookId() throws Exception {
        UUID bookId = UUID.randomUUID();
        Page<ModuleSummaryResponse> response = new PageImpl<>(List.of());

        when(moduleService.findByBookId(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/modules/book/{bookId}", bookId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(moduleService, times(1)).findByBookId(any(), any());
    }

    @Test
    void testGetNextModuleNumber() throws Exception {
        UUID bookId = UUID.randomUUID();
        Integer nextNumber = 1;

        when(moduleService.getNextModuleNumber(bookId)).thenReturn(nextNumber);

        mockMvc.perform(get("/api/v1/modules/book/{bookId}/next-number", bookId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(nextNumber));

        verify(moduleService, times(1)).getNextModuleNumber(bookId);
    }

    @Test
    void testUpdateModule() throws Exception {
        UUID id = UUID.randomUUID();
        ModuleUpdateRequest request = new ModuleUpdateRequest();
        ModuleDetailResponse response = new ModuleDetailResponse();

        when(moduleService.update(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/modules/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(moduleService, times(1)).update(eq(id), any());
    }
}

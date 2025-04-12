package com.spacedlearning.controller;

import static org.mockito.ArgumentMatchers.any;
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
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.service.ModuleProgressService;



@WebMvcTest(ModuleProgressController.class)
class ModuleProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ModuleProgressService progressService;

    @InjectMocks
    private ModuleProgressController progressController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProgress() throws Exception {
        ModuleProgressCreateRequest request = new ModuleProgressCreateRequest();
        ModuleProgressDetailResponse response = new ModuleProgressDetailResponse();

        when(progressService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/progress").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());

        verify(progressService, times(1)).create(any());
    }

    @Test
    void testDeleteProgress() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(progressService).delete(id);

        mockMvc.perform(delete("/api/v1/progress/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Progress deleted successfully"));

        verify(progressService, times(1)).delete(id);
    }

    @Test
    void testGetAllProgress() throws Exception {
        Page<ModuleProgressSummaryResponse> response = Page.empty();

        when(progressService.findAll(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/progress")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(progressService, times(1)).findAll(any());
    }

    @Test
    void testGetDueProgress() throws Exception {
        LocalDate studyDate = LocalDate.now();
        Page<ModuleProgressSummaryResponse> response = Page.empty();

        when(progressService.findDueForStudy(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/progress/due").param("studyDate", studyDate.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());

        verify(progressService, times(1)).findDueForStudy(any(), any());
    }

    @Test
    void testGetProgressById() throws Exception {
        UUID id = UUID.randomUUID();
        ModuleProgressDetailResponse response = new ModuleProgressDetailResponse();

        when(progressService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/progress/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(progressService, times(1)).findById(id);
    }

    @Test
    void testGetProgressByModuleId() throws Exception {
        UUID moduleId = UUID.randomUUID();
        Page<ModuleProgressSummaryResponse> response = Page.empty();

        when(progressService.findByModuleId(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/progress/module/{moduleId}", moduleId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());

        verify(progressService, times(1)).findByModuleId(any(), any());
    }

    @Test
    void testUpdateProgress() throws Exception {
        UUID id = UUID.randomUUID();
        ModuleProgressUpdateRequest request = new ModuleProgressUpdateRequest();
        ModuleProgressDetailResponse response = new ModuleProgressDetailResponse();

        when(progressService.update(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/progress/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(progressService, times(1)).update(any(), any());
    }
}

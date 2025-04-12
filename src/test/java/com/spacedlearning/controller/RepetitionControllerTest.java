package com.spacedlearning.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.service.RepetitionService;
import jakarta.persistence.EntityNotFoundException;



@WebMvcTest(RepetitionController.class)
class RepetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RepetitionService repetitionService;

    @InjectMocks
    private RepetitionController repetitionController;

    private UUID progressId;
    private UUID repetitionId;
    private RepetitionCreateRequest createRequest;
    private RepetitionUpdateRequest updateRequest;
    private RepetitionResponse repetitionResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        progressId = UUID.randomUUID();
        repetitionId = UUID.randomUUID();
        createRequest = new RepetitionCreateRequest();
        updateRequest = new RepetitionUpdateRequest();
        repetitionResponse = new RepetitionResponse();
    }

    @Test
    void testCreateDefaultSchedule() throws Exception {
        List<RepetitionResponse> schedule = Collections.singletonList(repetitionResponse);
        when(repetitionService.createDefaultSchedule(progressId)).thenReturn(schedule);

        mockMvc.perform(post("/api/v1/repetitions/progress/{progressId}/schedule", progressId))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data").isArray());

        verify(repetitionService, times(1)).createDefaultSchedule(progressId);
    }

    @Test
    void testCreateRepetition() throws Exception {
        when(repetitionService.create(createRequest)).thenReturn(repetitionResponse);

        mockMvc.perform(post("/api/v1/repetitions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data").exists());

        verify(repetitionService, times(1)).create(createRequest);
    }

    @Test
    void testDeleteRepetition() throws Exception {
        doNothing().when(repetitionService).delete(repetitionId);

        mockMvc.perform(delete("/api/v1/repetitions/{id}", repetitionId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Repetition deleted successfully"));

        verify(repetitionService, times(1)).delete(repetitionId);
    }

    @Test
    void testGetAllRepetitions() throws Exception {
        mockMvc.perform(get("/api/v1/repetitions")).andExpect(status().isOk());

        verify(repetitionService, times(1)).findAll(any());
    }

    @Test
    void testGetDueRepetitions() throws Exception {
        mockMvc.perform(get("/api/v1/repetitions/user/{userId}/due", UUID.randomUUID())
                .param("reviewDate", LocalDate.now().toString())
                .param("status", RepetitionStatus.NOT_STARTED.name())).andExpect(status().isOk());

        verify(repetitionService, times(1)).findDueRepetitions(any(), any(), any(), any());
    }

    @Test
    void testGetRepetitionById() throws Exception {
        when(repetitionService.findById(repetitionId)).thenReturn(repetitionResponse);

        mockMvc.perform(get("/api/v1/repetitions/{id}", repetitionId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(repetitionService, times(1)).findById(repetitionId);
    }

    @Test
    void testGetRepetitionByProgressIdAndOrder() throws Exception {
        when(repetitionService.findByModuleProgressIdAndOrder(progressId,
                RepetitionOrder.FIRST_REPETITION)).thenReturn(repetitionResponse);

        mockMvc.perform(get("/api/v1/repetitions/progress/{progressId}/order/{order}", progressId,
                RepetitionOrder.FIRST_REPETITION)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(repetitionService, times(1)).findByModuleProgressIdAndOrder(progressId,
                RepetitionOrder.FIRST_REPETITION);
    }

    @Test
    void testGetRepetitionsByProgressId() throws Exception {
        List<RepetitionResponse> repetitions = Collections.singletonList(repetitionResponse);
        when(repetitionService.findByModuleProgressId(progressId)).thenReturn(repetitions);

        mockMvc.perform(get("/api/v1/repetitions/progress/{progressId}", progressId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());

        verify(repetitionService, times(1)).findByModuleProgressId(progressId);
    }

    @Test
    void testUpdateRepetition() throws Exception {
        when(repetitionService.update(repetitionId, updateRequest)).thenReturn(repetitionResponse);

        mockMvc.perform(put("/api/v1/repetitions/{id}", repetitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(repetitionService, times(1)).update(repetitionId, updateRequest);
    }

    @Test
    void testGetRepetitionById_NotFound() throws Exception {
        when(repetitionService.findById(repetitionId))
                .thenThrow(new EntityNotFoundException("Repetition not found"));

        mockMvc.perform(get("/api/v1/repetitions/{id}", repetitionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repetition not found"));

        verify(repetitionService, times(1)).findById(repetitionId);
    }

    @Test
    void testGetDueRepetitions_InvalidStatus() throws Exception {
        mockMvc.perform(get("/api/v1/repetitions/user/{userId}/due", UUID.randomUUID())
                .param("status", "INVALID_STATUS")).andExpect(status().isBadRequest());
    }

    @Test
    void testCreateRepetition_InvalidRequest() throws Exception {
        createRequest = new RepetitionCreateRequest(); // Empty or invalid request

        mockMvc.perform(post("/api/v1/repetitions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRepetition_NotFound() throws Exception {
        when(repetitionService.update(repetitionId, updateRequest))
                .thenThrow(new EntityNotFoundException("Repetition not found"));

        mockMvc.perform(put("/api/v1/repetitions/{id}", repetitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repetition not found"));

        verify(repetitionService, times(1)).update(repetitionId, updateRequest);
    }

    @Test
    void testDeleteRepetition_NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Repetition not found")).when(repetitionService)
                .delete(repetitionId);

        mockMvc.perform(delete("/api/v1/repetitions/{id}", repetitionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Repetition not found"));

        verify(repetitionService, times(1)).delete(repetitionId);
    }

}

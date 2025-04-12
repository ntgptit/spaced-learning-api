package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Module;
import com.spacedlearning.mapper.ModuleMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.ModuleRepository;



class ModuleServiceImplTest {

    @InjectMocks
    private ModuleServiceImpl moduleService;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateModule_Success() {
        ModuleCreateRequest request = new ModuleCreateRequest();
        request.setBookId(UUID.randomUUID());
        request.setModuleNo(1);

        Book book = new Book();
        Module module = new Module();
        Module savedModule = new Module();
        ModuleDetailResponse response = new ModuleDetailResponse();

        when(bookRepository.findById(request.getBookId())).thenReturn(Optional.of(book));
        when(moduleRepository.existsByBookIdAndModuleNo(request.getBookId(), request.getModuleNo()))
                .thenReturn(false);
        when(moduleMapper.toEntity(request, book)).thenReturn(module);
        when(moduleRepository.save(module)).thenReturn(savedModule);
        when(moduleMapper.toDto(savedModule)).thenReturn(response);

        ModuleDetailResponse result = moduleService.create(request);

        assertNotNull(result);
        verify(moduleRepository).save(module);
    }

    @Test
    void testDeleteModule_Success() {
        UUID moduleId = UUID.randomUUID();
        Module module = new Module();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        moduleService.delete(moduleId);

        verify(moduleRepository).save(module);
    }

    @Test
    void testFindAllModules_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Module module = new Module();
        Page<Module> modulePage = new PageImpl<>(List.of(module));
        ModuleSummaryResponse summaryResponse = new ModuleSummaryResponse();

        when(moduleRepository.findAll(pageable)).thenReturn(modulePage);
        when(moduleMapper.toSummaryDto(module)).thenReturn(summaryResponse);

        Page<ModuleSummaryResponse> result = moduleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindAllByBookId_Success() {
        UUID bookId = UUID.randomUUID();
        Module module = new Module();
        List<Module> modules = List.of(module);
        ModuleSummaryResponse summaryResponse = new ModuleSummaryResponse();

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(moduleRepository.findByBookIdOrderByModuleNo(bookId)).thenReturn(modules);
        when(moduleMapper.toSummaryDtoList(modules)).thenReturn(List.of(summaryResponse));

        List<ModuleSummaryResponse> result = moduleService.findAllByBookId(bookId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindById_Success() {
        UUID moduleId = UUID.randomUUID();
        Module module = new Module();
        ModuleDetailResponse response = new ModuleDetailResponse();

        when(moduleRepository.findWithProgressById(moduleId)).thenReturn(Optional.of(module));
        when(moduleMapper.toDto(module)).thenReturn(response);

        ModuleDetailResponse result = moduleService.findById(moduleId);

        assertNotNull(result);
    }

    @Test
    void testGetNextModuleNumber_Success() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(moduleRepository.findMaxModuleNoByBookId(bookId)).thenReturn(5);

        Integer nextModuleNumber = moduleService.getNextModuleNumber(bookId);

        assertEquals(6, nextModuleNumber);
    }

    @Test
    void testUpdateModule_Success() {
        UUID moduleId = UUID.randomUUID();
        ModuleUpdateRequest request = new ModuleUpdateRequest();
        request.setModuleNo(2);

        Module module = new Module();
        Module updatedModule = new Module();
        ModuleDetailResponse response = new ModuleDetailResponse();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(moduleRepository.existsByBookIdAndModuleNo(module.getBook().getId(),
                request.getModuleNo())).thenReturn(false);
        doNothing().when(moduleMapper).updateFromDto(request, module);
        when(moduleRepository.save(module)).thenReturn(updatedModule);
        when(moduleMapper.toDto(updatedModule)).thenReturn(response);

        ModuleDetailResponse result = moduleService.update(moduleId, request);

        assertNotNull(result);
        verify(moduleRepository).save(module);
    }
}

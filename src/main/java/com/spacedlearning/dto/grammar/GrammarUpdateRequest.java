// GrammarUpdateRequest.java
package com.spacedlearning.dto.grammar;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarUpdateRequest {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String explanation;

    private String usageNote;

    private String example;
}
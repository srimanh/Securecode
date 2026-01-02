package com.securecode.backend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    @JsonProperty("isSecure")
    private boolean isSecure;
    private String issue;
    private String explanation;
    private String policyCategory;
    private String safeAlternative;
    private String risk;
    private String exploit;
    private String severity;

    public static AnalysisResult refusal() {
        return new AnalysisResult(
                true,
                "Refused",
                "This input is not covered by the current security guidelines. SecureCode avoids speculative advice to ensure accuracy.",
                "NONE",
                "Consult original documentation for uncovered scenarios.",
                "None",
                "None",
                "LOW");
    }
}

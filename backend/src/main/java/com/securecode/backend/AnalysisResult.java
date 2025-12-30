package com.securecode.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    private boolean isSecure;
    private String issue;
    private String explanation;
    private String policyCategory;
    private String safeAlternative;

    public static AnalysisResult refusal() {
        return new AnalysisResult(
                true,
                "Refused",
                "Not covered by provided security guidelines.",
                "NONE",
                "Consult original documentation for uncovered scenarios.");
    }
}

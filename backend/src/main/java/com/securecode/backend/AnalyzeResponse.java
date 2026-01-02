package com.securecode.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeResponse {
    private Boolean isSecure;
    private String issue;
    private String explanation;
    private String policyCategory;
    private String safeAlternative;
    private String risk;
    private String exploit;
    private String severity;
    private String message;

    public static AnalyzeResponse refusal(String message) {
        AnalyzeResponse response = new AnalyzeResponse();
        response.setMessage(message);
        return response;
    }
}

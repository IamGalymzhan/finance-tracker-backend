package org.galymzhan.financetrackerbackend.dto.background;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaggingResult {
    private int totalProcessed;
    private int totalTagged;
    private int totalFailed;
    private boolean success;
    private String errorMessage;
    private List<BatchResult> batchResults = new ArrayList<>();

    public void addBatchResult(BatchResult batch) {
        this.batchResults.add(batch);
        this.totalProcessed += batch.getProcessed();
        this.totalTagged += batch.getTagged();
        this.totalFailed += batch.getFailed();
    }

    public static TaggingResult noRules() {
        return TaggingResult.builder()
                .success(true)
                .totalProcessed(0)
                .totalTagged(0)
                .totalFailed(0)
                .build();
    }

    public static TaggingResult failed(String error) {
        return TaggingResult.builder()
                .success(false)
                .errorMessage(error)
                .build();
    }
}
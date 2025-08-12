package org.galymzhan.financetrackerbackend.dto.background;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatchResult {
    private int processed = 0;
    private int tagged = 0;
    private int failed = 0;

    public void incrementProcessed() { this.processed++; }
    public void incrementTagged() { this.tagged++; }
    public void incrementFailed() { this.failed++; }
}
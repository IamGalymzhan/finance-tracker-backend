package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.entity.Operation;

public interface AccountBalanceService {
    
    void applyBalanceChange(Operation operation);
    
    void revertBalanceChange(Operation operation);
} 
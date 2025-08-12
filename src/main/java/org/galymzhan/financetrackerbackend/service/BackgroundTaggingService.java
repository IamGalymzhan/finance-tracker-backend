package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.background.TaggingResult;
import org.galymzhan.financetrackerbackend.entity.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BackgroundTaggingService {

    CompletableFuture<TaggingResult> processOperationsInBackground(User user, List<Long> operationIds);

    CompletableFuture<Void> reapplyAllRules(User user);
}
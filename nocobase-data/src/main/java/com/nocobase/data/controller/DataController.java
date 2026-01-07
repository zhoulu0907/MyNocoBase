package com.nocobase.data.controller;

import com.nocobase.common.response.ApiResponse;
import com.nocobase.data.service.DataRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dynamic Data CRUD Controller
 * Provides REST API for dynamic table operations
 */
@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Tag(name = "Dynamic Data", description = "Dynamic data CRUD operations")
public class DataController {

    private final DataRecordService dataRecordService;

    /**
     * Create a new record in the specified collection
     *
     * @param collectionName Collection name (also used as table path variable)
     * @param data Data to insert (JSON body)
     * @return Created record ID
     */
    @PostMapping("/{collectionName}")
    @Operation(summary = "Create record", description = "Create a new record in the specified collection")
    public ApiResponse<Long> createRecord(
            @Parameter(description = "Collection name") @PathVariable String collectionName,
            @RequestBody Map<String, Object> data) {

        try {
            Long id = dataRecordService.createRecord(collectionName, data);
            return ApiResponse.success("Record created successfully", id);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to create record: " + e.getMessage());
        }
    }

    /**
     * Query records from the specified collection
     *
     * @param collectionName Collection name
     * @param condition Optional query conditions (request parameters)
     * @return List of records
     */
    @GetMapping("/{collectionName}")
    @Operation(summary = "Query records", description = "Query records from the specified collection")
    public ApiResponse<List<Map<String, Object>>> queryRecords(
            @Parameter(description = "Collection name") @PathVariable String collectionName,
            @RequestParam(required = false) Map<String, Object> condition) {

        try {
            List<Map<String, Object>> records;
            if (condition == null || condition.isEmpty()) {
                records = dataRecordService.queryAllRecords(collectionName);
            } else {
                records = dataRecordService.queryRecords(collectionName, condition);
            }
            return ApiResponse.success(records);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to query records: " + e.getMessage());
        }
    }

    /**
     * Query a single record by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @return Record data
     */
    @GetMapping("/{collectionName}/{id}")
    @Operation(summary = "Get record by ID", description = "Query a single record by ID")
    public ApiResponse<Map<String, Object>> queryById(
            @Parameter(description = "Collection name") @PathVariable String collectionName,
            @Parameter(description = "Record ID") @PathVariable Long id) {

        try {
            Map<String, Object> record = dataRecordService.queryById(collectionName, id);
            if (record == null) {
                return ApiResponse.notFound("Record not found with id: " + id);
            }
            return ApiResponse.success(record);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to query record: " + e.getMessage());
        }
    }

    /**
     * Update a record by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @param data Data to update (JSON body)
     * @return Number of affected rows
     */
    @PutMapping("/{collectionName}/{id}")
    @Operation(summary = "Update record", description = "Update a record by ID")
    public ApiResponse<Integer> updateRecord(
            @Parameter(description = "Collection name") @PathVariable String collectionName,
            @Parameter(description = "Record ID") @PathVariable Long id,
            @RequestBody Map<String, Object> data) {

        try {
            int affected = dataRecordService.updateRecord(collectionName, id, data);
            if (affected == 0) {
                return ApiResponse.notFound("Record not found or no changes made");
            }
            return ApiResponse.success("Record updated successfully", affected);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update record: " + e.getMessage());
        }
    }

    /**
     * Delete a record by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @return Number of affected rows
     */
    @DeleteMapping("/{collectionName}/{id}")
    @Operation(summary = "Delete record", description = "Delete a record by ID")
    public ApiResponse<Integer> deleteRecord(
            @Parameter(description = "Collection name") @PathVariable String collectionName,
            @Parameter(description = "Record ID") @PathVariable Long id) {

        try {
            int affected = dataRecordService.deleteRecord(collectionName, id);
            if (affected == 0) {
                return ApiResponse.notFound("Record not found");
            }
            return ApiResponse.success("Record deleted successfully", affected);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete record: " + e.getMessage());
        }
    }

    /**
     * Count records in the specified collection
     *
     * @param collectionName Collection name
     * @return Number of records
     */
    @GetMapping("/{collectionName}/count")
    @Operation(summary = "Count records", description = "Count records in the specified collection")
    public ApiResponse<Long> countRecords(
            @Parameter(description = "Collection name") @PathVariable String collectionName) {

        try {
            long count = dataRecordService.countRecords(collectionName);
            return ApiResponse.success(count);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to count records: " + e.getMessage());
        }
    }

    /**
     * Check if a record exists by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @return true if exists, false otherwise
     */
    @GetMapping("/{collectionName}/{id}/exists")
    @Operation(summary = "Check record existence", description = "Check if a record exists by ID")
    public ApiResponse<Boolean> exists(
            @Parameter(description = "Collection name") @PathVariable String collectionName,
            @Parameter(description = "Record ID") @PathVariable Long id) {

        try {
            boolean exists = dataRecordService.exists(collectionName, id);
            return ApiResponse.success(exists);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to check existence: " + e.getMessage());
        }
    }
}

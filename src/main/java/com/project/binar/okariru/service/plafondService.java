package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.PlafondRequest;
import com.project.binar.okariru.dto.PlafondResponse;

import java.util.List;

public interface PlafondService {

    List<PlafondResponse.getPlafondResponse> getAllPlafond();

    PlafondResponse.getPlafondResponse getPlafondById(Integer id);

    PlafondResponse.getPlafondResponse addPlafond(PlafondRequest.plafondAddRequest addRequest);

    void updatePlafond(Integer id, Integer userId, Integer totalPlafond, String deskripsiPlafond, Integer updatedBy);

    String deletePlafond(Integer id);
}

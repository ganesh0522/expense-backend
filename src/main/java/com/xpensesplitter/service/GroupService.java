package com.xpensesplitter.service;

import com.xpensesplitter.dto.request.CreateGroupRequest;
import com.xpensesplitter.dto.response.GroupResponse;

import java.util.List;

public interface GroupService {

    GroupResponse createGroup(CreateGroupRequest request, String userEmail);

    List<GroupResponse> getUserGroups(String userEmail);

    GroupResponse getGroupById(Long groupId);
}

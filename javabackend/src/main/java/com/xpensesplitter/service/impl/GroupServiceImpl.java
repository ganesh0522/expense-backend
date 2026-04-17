package com.xpensesplitter.service.impl;

import com.xpensesplitter.dto.request.CreateGroupRequest;
import com.xpensesplitter.dto.response.GroupResponse;
import com.xpensesplitter.dto.response.UserResponse;
import com.xpensesplitter.entity.Group;
import com.xpensesplitter.entity.GroupMember;
import com.xpensesplitter.entity.User;
import com.xpensesplitter.repository.GroupMemberRepository;
import com.xpensesplitter.repository.GroupRepository;
import com.xpensesplitter.repository.UserRepository;
import com.xpensesplitter.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    public GroupResponse createGroup(CreateGroupRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = Group.builder()
                .name(request.getName())
                .createdBy(user.getId())
                .createdAt(LocalDateTime.now())
                .build();

        groupRepository.save(group);

        // add creator
        groupMemberRepository.save(
                GroupMember.builder()
                        .groupId(group.getId())
                        .userId(user.getId())
                        .build()
        );

        // add members
        for (Long memberId : request.getMemberIds()) {
            groupMemberRepository.save(
                    GroupMember.builder()
                            .groupId(group.getId())
                            .userId(memberId)
                            .build()
            );
        }

        return new GroupResponse(group.getId(), group.getName());
    }

    @Override
    public List<GroupResponse> getUserGroups(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GroupMember> memberships = groupMemberRepository.findByUserId(user.getId());

        return memberships.stream()
                .map(m -> groupRepository.findById(m.getGroupId()).orElse(null))
                .filter(Objects::nonNull)
                .map(g -> new GroupResponse(g.getId(), g.getName()))
                .toList();
    }

    @Override
    public GroupResponse getGroupById(Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // 🔥 STEP 1: get group members
        List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(groupId);

        // 🔥 STEP 2: convert to users
        List<UserResponse> members = groupMembers.stream()
                .map(gm -> userRepository.findById(gm.getUserId()).orElse(null))
                .filter(Objects::nonNull)
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()))
                .toList();

        // 🔥 STEP 3: build response
        GroupResponse response = new GroupResponse(group.getId(), group.getName());
        response.setMembers(members);

        return response;
    }
}

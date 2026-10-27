package com.kh.chamreunvira.springboot.service.impl;

import com.kh.chamreunvira.springboot.dto.FriendResponse;
import com.kh.chamreunvira.springboot.dto.FriendSenderResponse;
import com.kh.chamreunvira.springboot.enumz.FriendRequestStatus;
import com.kh.chamreunvira.springboot.mapper.exception.CustomMessageException;
import com.kh.chamreunvira.springboot.model.FriendRequest;
import com.kh.chamreunvira.springboot.model.User;
import com.kh.chamreunvira.springboot.repository.FriendRequestRepository;
import com.kh.chamreunvira.springboot.repository.UserRepository;
import com.kh.chamreunvira.springboot.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Override
    public void sendRequest(Long sender, Long receiver) {

        User userSender = userRepository.findById(sender)
                .orElseThrow(() -> new UsernameNotFoundException("User sender not found."));
        User userReceiver = userRepository.findById(receiver)
                .orElseThrow(() -> new UsernameNotFoundException("User receiver not found."));

        if(friendRequestRepository.findBySenderAndReceiver(userSender , userReceiver).isPresent()
            || friendRequestRepository.findBySenderAndReceiver(userReceiver , userSender).isPresent()
        ) {
            throw new CustomMessageException("Friend is already request" , HttpStatus.BAD_REQUEST.value());
        }
        if(userSender.equals(userReceiver)) {
            throw new CustomMessageException("You can request friend to your self." , HttpStatus.BAD_REQUEST.value());
        }
        FriendRequest friend = new FriendRequest();
        friend.setSender(userSender);
        friend.setReceiver(userReceiver);
        friend.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(friend);
    }

    @Override
    public void acceptFriend(Long requestId, Long userId) {
        FriendRequest friend = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend Request not found."));
        if(!friend.getReceiver().getId().equals(userId)) {
            throw new CustomMessageException("You don't have permission to accept this friend." , HttpStatus.BAD_REQUEST.value());
        }
        friend.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(friend);
    }

    @Override
    public void rejectFriend(Long requestId, Long userId) {
        FriendRequest friend = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found with requestId: " + requestId));
        if (!friend.getReceiver().getId().equals(userId)) {
            throw new CustomMessageException("You don't have permission to accept this friend.", HttpStatus.BAD_REQUEST.value());
        }
        friend.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(friend);
    }

    @Override
    public List<FriendResponse> getFriend(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        List<FriendRequest> relations = friendRequestRepository.findBySenderAndStatusOrReceiverAndStatus(
                user,
                FriendRequestStatus.ACCEPTED,
                user,
                FriendRequestStatus.ACCEPTED
        );

        return relations
                .stream()
                .map(fr -> {
                    FriendResponse response = new FriendResponse();
                    User targetUser = fr.getSender().equals(user) ? fr.getReceiver() : fr.getSender();
                    response.setUserId(targetUser.getId());
                    response.setUsername(targetUser.getFullName());
                    response.setNickname(targetUser.getNickname());
                    response.setEmail(targetUser.getEmail());
                    response.setProfile(targetUser.getProfile());
                    return response;
                })
                .toList();
    }

    @Override
    public List<FriendSenderResponse> getRequestPending(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: "+ userId));
        List<FriendRequest> friendRequests = friendRequestRepository.findByReceiverAndStatus(user , FriendRequestStatus.PENDING);
        return friendRequests
                .stream()
                .map(fr -> {
                    FriendSenderResponse senderResponse = new FriendSenderResponse();
                    senderResponse.setId(fr.getId());
                    FriendResponse response = new FriendResponse();
                    response.setUserId(fr.getSender().getId());
                    response.setUsername(fr.getSender().getFullName());
                    response.setNickname(fr.getSender().getNickname());
                    response.setEmail(fr.getSender().getEmail());
                    response.setProfile(fr.getSender().getProfile());
                    senderResponse.setSender(response);
                    senderResponse.setStatus(fr.getStatus());
                    return senderResponse;
                })
                .toList();
    }

    @Override
    public List<FriendResponse> getFriendSuggestions(Long userId) {
        List<User> allUsers = userRepository.findAll();
        List<FriendRequest> allRequests = friendRequestRepository.findAll();

        // Get IDs of users who are already friends or have a pending request with the user
        List<Long> excludedUserIds = allRequests.stream()
                .filter(fr -> (fr.getSender().getId().equals(userId) || fr.getReceiver().getId().equals(userId))
                        && (fr.getStatus() == FriendRequestStatus.ACCEPTED || fr.getStatus() == FriendRequestStatus.PENDING))
                .map(fr -> fr.getSender().getId().equals(userId) ? fr.getReceiver().getId() : fr.getSender().getId())
                .toList();

        return allUsers.stream()
                .filter(u -> !u.getId().equals(userId) && !excludedUserIds.contains(u.getId()))
                .map(u -> FriendResponse.builder()
                        .userId(u.getId())
                        .username(u.getFullName())
                        .nickname(u.getNickname())
                        .email(u.getEmail())
                        .profile(u.getProfile())
                        .build())
                .toList();
    }

    @Override
    public void deleteFriend(long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UsernameNotFoundException("Friend not found with id: " + friendId));

        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findBySenderAndReceiverAndStatus(user, friend, FriendRequestStatus.ACCEPTED);
        if (friendRequestOpt.isEmpty()) {
            friendRequestOpt = friendRequestRepository.findBySenderAndReceiverAndStatus(friend, user, FriendRequestStatus.ACCEPTED);
        }

        FriendRequest friendRequest = friendRequestOpt.orElseThrow(() -> new CustomMessageException("Users are not friends.", HttpStatus.BAD_REQUEST.value()));

        friendRequestRepository.delete(friendRequest);
    }

}

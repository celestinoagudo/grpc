package com.grind.two.four.seven.grpc.service.handler;

import com.grind.two.four.seven.grpc.exceptions.UnknownUserException;
import com.grind.two.four.seven.grpc.repository.PortfolioItemRepository;
import com.grind.two.four.seven.grpc.repository.UserRepository;
import com.grind.two.four.seven.grpc.user.UserInformation;
import com.grind.two.four.seven.grpc.user.UserInformationRequest;
import com.grind.two.four.seven.grpc.util.EntityMessageMapper;
import org.springframework.stereotype.Service;

@Service
public class UserInformationRequestHandler {

    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public UserInformationRequestHandler(final UserRepository userRepository,
                                         final PortfolioItemRepository portfolioItemRepository) {
        this.userRepository = userRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public UserInformation getUserInformation(final UserInformationRequest userInformationRequest) {
        var user = userRepository.findById(userInformationRequest.getUserId())
                .orElseThrow(() -> new UnknownUserException(userInformationRequest.getUserId()));
        var portfolioItems = portfolioItemRepository.findAllByUserId(userInformationRequest.getUserId());
        return EntityMessageMapper.toUserInformation(user, portfolioItems);
    }
}

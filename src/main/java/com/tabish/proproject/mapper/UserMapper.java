package com.tabish.proproject.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.tabish.proproject.dto.request.RegisterRequest;
import com.tabish.proproject.dto.request.UpdateUserRequest;
import com.tabish.proproject.dto.response.UserResponse;
import com.tabish.proproject.entity.User;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {
	UserResponse toUserResponse(User user);

	List<UserResponse> toUserResponseList(List<User> users);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "provider", constant = "LOCAL")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	User toEntity(RegisterRequest request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEntityFromDto(UpdateUserRequest request, @MappingTarget User user);
}

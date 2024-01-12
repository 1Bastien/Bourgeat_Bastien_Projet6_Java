package com.paymybuddy.app.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.paymybuddy.app.DTO.UserDTO;
import com.paymybuddy.app.model.MyUser;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyUserMapper {

	MyUserMapper INSTANCE = Mappers.getMapper(MyUserMapper.class);

	void updateMyUserFromDTO(UserDTO userDTO, @MappingTarget MyUser myUser);

	void updateMyUserFromDTO(String password, @MappingTarget MyUser myUser);
	
	void updateMyUserFromDTO(BigDecimal balance, @MappingTarget MyUser myUser);

	UserDTO myUserToUserDTO(MyUser myUser);
}
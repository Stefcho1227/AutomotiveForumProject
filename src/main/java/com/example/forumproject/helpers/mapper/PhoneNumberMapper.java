package com.example.forumproject.helpers.mapper;

import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.models.dtos.in.PhoneNumberDto;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberMapper {

    public UserPhoneNumber fromDto(PhoneNumberDto phoneNumberDto) {
        UserPhoneNumber userPhoneNumber = new UserPhoneNumber();
        userPhoneNumber.setValue(phoneNumberDto.getPhoneNumber());

        return userPhoneNumber;
    }
}

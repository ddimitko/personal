package com.ddimitko.personal.configs;

import com.ddimitko.personal.DTOs.UserDto;
import com.ddimitko.personal.models.Picture;
import com.ddimitko.personal.models.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        Converter<Picture, String> pictureToBase64Converter = context -> {
            Picture source = context.getSource();
            return source != null && source.getImageData() != null
                    ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(source.getImageData())
                    : null;
        };

        modelMapper.typeMap(User.class, UserDto.class).addMappings(mapper -> {
            mapper.using(pictureToBase64Converter).map(User::getProfilePicture, UserDto::setProfilePicture);
        });

        return modelMapper;
    }

}

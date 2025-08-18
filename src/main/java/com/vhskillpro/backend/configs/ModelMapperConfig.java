package com.vhskillpro.backend.configs;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
    customMapping(modelMapper);
    return modelMapper;
  }

  /**
   * Configures custom mappings for the provided {@link ModelMapper} instance. Use this method to
   * define specific property mappings, converters, or type maps to customize how objects are mapped
   * within the application.
   *
   * @param modelMapper the {@link ModelMapper} instance to configure
   */
  private void customMapping(ModelMapper modelMapper) {}
}

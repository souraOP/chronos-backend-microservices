package com.chronos.common.autoconfigure;

import com.chronos.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GlobalExceptionHandler.class)
public class CommonExceptionAutoConfiguration {}
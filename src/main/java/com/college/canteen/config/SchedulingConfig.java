package com.college.canteen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// turns on @Scheduled jobs (the no-show checker)
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

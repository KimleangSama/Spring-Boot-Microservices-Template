package com.keakimleang.configserver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class LoggingProps {
    @Value("${logging.loki.url}")
    private String lokiUrl;
}

package net.quombat.dododart.infrastructure.serial;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "serialport")
public record SerialPortConfigProperties(String portDescriptor) {
}

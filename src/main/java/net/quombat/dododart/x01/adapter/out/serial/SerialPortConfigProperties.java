package net.quombat.dododart.x01.adapter.out.serial;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "serialport")
public record SerialPortConfigProperties(String portDescriptor) {
}

package net.quombat.dododart.infrastructure;

import org.springframework.context.PayloadApplicationEvent;

public interface SerialPortInterface {
    void send(PayloadApplicationEvent<String> event);
}

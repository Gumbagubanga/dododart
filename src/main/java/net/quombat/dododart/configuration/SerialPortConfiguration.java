package net.quombat.dododart.configuration;

import com.google.gson.Gson;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import net.quombat.dododart.x01.domain.ButtonPressedEvent;
import net.quombat.dododart.x01.domain.ButtonStartBlink;
import net.quombat.dododart.x01.domain.ButtonStopBlink;
import net.quombat.dododart.x01.domain.DartHitEvent;
import net.quombat.dododart.x01.domain.DartSegment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.nio.charset.StandardCharsets;

import javax.annotation.PreDestroy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class SerialPortConfiguration {

    private final Gson gson = new Gson();

    private final ApplicationEventPublisher publisher;
    private final SerialPort comPort;

    public SerialPortConfiguration(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.comPort = initSerialPort(0);
    }

    @PreDestroy
    void onDestroy() {
        comPort.closePort();
    }

    private SerialPort initSerialPort(int port) {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        SerialPort comPort = commPorts[port];
        comPort.openPort();
        comPort.addDataListener(new SerialPortMessageListener() {
            @Override
            public byte[] getMessageDelimiter() {
                return "\n".getBytes();
            }

            @Override
            public boolean delimiterIndicatesEndOfMessage() {
                return true;
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @SneakyThrows
            @Override
            public void serialEvent(SerialPortEvent event) {
                String json = new String(event.getReceivedData());
                log.debug("Serial Event: {}", json);

                DartBoardDatagram boardDatagram = gson.fromJson(json, DartBoardDatagram.class);

                if (boardDatagram.isButtonPressed()) {
                    publisher.publishEvent(new ButtonPressedEvent());
                } else {
                    publisher.publishEvent(new DartHitEvent(DartSegment.from(boardDatagram.getDart())));
                }
            }
        });
        return comPort;
    }

    @EventListener
    public void startButtonBlink(ButtonStartBlink button) {
        var event = new PayloadApplicationEvent<>(this, button.getClass().getSimpleName());
        byte[] bytes = gson.toJson(event).getBytes(StandardCharsets.UTF_8);
        comPort.writeBytes(bytes, bytes.length);
    }

    @EventListener
    public void stopButtonBlink(ButtonStopBlink button) {
        var event = new PayloadApplicationEvent<>(this, button.getClass().getSimpleName());
        byte[] bytes = gson.toJson(event).getBytes(StandardCharsets.UTF_8);
        comPort.writeBytes(bytes, bytes.length);
    }
}

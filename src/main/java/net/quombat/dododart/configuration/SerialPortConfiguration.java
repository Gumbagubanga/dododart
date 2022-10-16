package net.quombat.dododart.configuration;

import com.google.gson.Gson;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import net.quombat.dododart.shared.domain.ButtonStartBlink;
import net.quombat.dododart.shared.domain.ButtonStopBlink;
import net.quombat.dododart.shared.domain.Dart;
import net.quombat.dododart.shared.domain.DartSegment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.nio.charset.StandardCharsets;

import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class SerialPortConfiguration {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final Gson gson;
    private final SerialPort comPort;

    public SerialPortConfiguration(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.gson = new Gson();
        this.comPort = initSerialPort();
    }

    @PreDestroy
    void onDestroy() {
        comPort.closePort();
    }

    private SerialPort initSerialPort() {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        SerialPort comPort = commPorts[0];
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

            @Override
            public void serialEvent(SerialPortEvent event) {
                String json = new String(event.getReceivedData());
                log.debug("Serial Event: {}", json);

                DartBoardDatagram boardDatagram = gson.fromJson(json, DartBoardDatagram.class);

                if (boardDatagram.isButtonPressed()) {
                    applicationEventPublisher.publishEvent(new ButtonPressed());
                } else {
                    Dart dart = new Dart(DartSegment.from(boardDatagram.getDart()));

                    try {
                        applicationEventPublisher.publishEvent(dart);
                    } catch (RuntimeException e) {
                        log.error("", e);
                    }
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

package net.quombat.dododart.infrastructure.serial;

import com.google.gson.Gson;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

import net.quombat.dododart.adapter.in.serial.ButtonPressedEvent;
import net.quombat.dododart.adapter.in.serial.DartHitEvent;
import net.quombat.dododart.domain.ScoreSegment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class SerialPortDriver implements SerialPortInterface {

    private static final Gson gson = new Gson();

    private final ApplicationEventPublisher publisher;
    private final String portDescriptor;

    private SerialPort serialPort;

    public SerialPortDriver(ApplicationEventPublisher publisher,
                            @Value("${serialport.portDescriptor:#{null}}") String portDescriptor) {
        this.publisher = publisher;
        this.portDescriptor = portDescriptor;
    }

    @PostConstruct
    void setup() {
        if (portDescriptor == null) {
            this.serialPort = null;
            return;
        }

        log.info("Connecting to port descriptor '{}'", portDescriptor);
        SerialPort serialPort = SerialPort.getCommPort(portDescriptor);
        if (serialPort.openPort()) {
            log.info("Serial connection successfully established");
            serialPort.addDataListener(getListener());
        } else {
            String portDescriptors = Arrays.stream(SerialPort.getCommPorts())
                .map(SerialPort::getPortDescription)
                .collect(Collectors.joining(", "));
            log.error("Serial connection failed. Available ports: {}", portDescriptors);
        }
        this.serialPort = serialPort;

    }

    private AbstractSerialPortMessageListener getListener() {
        return new AbstractSerialPortMessageListener() {

            @SneakyThrows
            @Override
            public void serialEvent(SerialPortEvent event) {
                String json = new String(event.getReceivedData());
                log.debug("Serial Event: {}", json);

                DartBoardDatagram boardDatagram = gson.fromJson(json, DartBoardDatagram.class);

                if (boardDatagram.isButtonPressed()) {
                    publisher.publishEvent(new ButtonPressedEvent());
                } else {
                    ScoreSegment segment = ScoreSegment.from(boardDatagram.getDart());
                    publisher.publishEvent(new DartHitEvent(segment));
                }
            }
        };
    }

    @PreDestroy
    void onDestroy() {
        if (serialPort != null) {
            serialPort.closePort();
        }
    }

    @Override
    public void send(DartBoardEvent event) {
        byte[] bytes = gson.toJson(event).getBytes(StandardCharsets.UTF_8);
        if (serialPort != null) {
            serialPort.writeBytes(bytes, bytes.length);
        }
    }

    @Data
    static class DartBoardDatagram {
        int dart;
        boolean buttonPressed;
    }
}

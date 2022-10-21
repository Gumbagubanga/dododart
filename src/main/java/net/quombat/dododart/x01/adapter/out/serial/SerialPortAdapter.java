package net.quombat.dododart.x01.adapter.out.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.quombat.dododart.x01.application.ports.out.UartSendPort;
import net.quombat.dododart.x01.domain.ButtonPressedEvent;
import net.quombat.dododart.x01.domain.DartHitEvent;
import net.quombat.dododart.x01.domain.DartSegment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
class SerialPortAdapter implements UartSendPort {

    private static final Gson gson = new Gson();

    private final ApplicationEventPublisher publisher;
    private final SerialPort serialPort;

    public SerialPortAdapter(ApplicationEventPublisher publisher,
                             @Value("${serialport.portDescriptor}") String portDescriptor) {
        this.publisher = publisher;
        this.serialPort = initSerialPort(portDescriptor);
    }

    @PreDestroy
    void onDestroy() {
        serialPort.closePort();
    }

    private SerialPort initSerialPort(String portDescriptor) {
        SerialPort comPort = SerialPort.getCommPort(portDescriptor);
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

    @Override
    public void startButtonBlink() {
        PayloadApplicationEvent<String> event = new PayloadApplicationEvent<>(this, "ButtonStartBlink");
        byte[] bytes = gson.toJson(event).getBytes(StandardCharsets.UTF_8);
        serialPort.writeBytes(bytes, bytes.length);
    }

    @Override
    public void stopButtonBlink() {
        PayloadApplicationEvent<String> event = new PayloadApplicationEvent<>(this, "ButtonStopBlink");
        byte[] bytes = gson.toJson(event).getBytes(StandardCharsets.UTF_8);
        serialPort.writeBytes(bytes, bytes.length);
    }
}

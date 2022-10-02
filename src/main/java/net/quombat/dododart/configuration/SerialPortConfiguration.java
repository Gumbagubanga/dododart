package net.quombat.dododart.configuration;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.gson.Gson;
import net.quombat.dododart.shared.domain.DartBoardEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
class SerialPortConfiguration implements SerialPortMessageListener {

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
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.addDataListener(this);
        return comPort;
    }

    @Override
    public byte[] getMessageDelimiter() {
        return "\r\n".getBytes();
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
        byte[] receivedData = event.getReceivedData();
        DartBoardEvent dartBoardEvent = gson.fromJson(new String(receivedData), DartBoardEvent.class);
        applicationEventPublisher.publishEvent(dartBoardEvent);
    }
}

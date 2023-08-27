package net.quombat.dododart.infrastructure.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortMessageListener;

abstract class AbstractSerialPortMessageListener implements SerialPortMessageListener {
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

}

package net.quombat.dododart.infrastructure.serial;

public interface SerialPortInterface {
    void send(DartBoardEvent event);
}

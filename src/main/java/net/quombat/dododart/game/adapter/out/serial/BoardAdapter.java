package net.quombat.dododart.game.adapter.out.serial;

import net.quombat.dododart.game.application.ports.out.BoardPort;
import net.quombat.dododart.infrastructure.SerialPortInterface;

import org.springframework.context.PayloadApplicationEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class BoardAdapter implements BoardPort {

    private final SerialPortInterface serialPort;

    @Override
    public void startButtonBlink() {
        serialPort.send(new PayloadApplicationEvent<>(this, "ButtonStartBlink"));
    }

    @Override
    public void stopButtonBlink() {
        serialPort.send(new PayloadApplicationEvent<>(this, "ButtonStopBlink"));
    }
}
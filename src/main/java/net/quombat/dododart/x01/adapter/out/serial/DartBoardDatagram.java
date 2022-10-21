package net.quombat.dododart.x01.adapter.out.serial;

import lombok.Data;

@Data
class DartBoardDatagram {
    int dart;
    boolean buttonPressed;
}

package net.quombat.dododart.game.adapter.out.serial;

import lombok.Data;

@Data
class DartBoardDatagram {
    int dart;
    boolean buttonPressed;
}

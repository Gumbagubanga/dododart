package net.quombat.dododart.shared.domain;

import lombok.Data;

@Data
public class DartBoardEvent {
    int dart;
    boolean hit;
}

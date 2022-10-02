package net.quombat.dododart.five_oh_one.application.port.in;

import net.quombat.dododart.shared.domain.DartSegment;

public interface FiveOhOneUseCase {
    void hit(HitCommand hit);

    record HitCommand(DartSegment segment, boolean miss) {

    }
}

package net.quombat.dododart.adapter.out.web;

import net.quombat.dododart.application.ports.out.RenderPort;
import net.quombat.dododart.infrastructure.web.SseDriver;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
class WebRenderAdapter implements RenderPort {

    private final SseDriver sseDriver;

    @Override
    public void render() {
        sseDriver.update("");
    }

}

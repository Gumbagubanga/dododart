package net.quombat.dododart.adapter.out.web;

import net.quombat.dododart.application.ports.out.RenderPort;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
class WebRenderAdapter implements RenderPort {

    private SseEmitter sseEmitter;

    @GetMapping("/register")
    public SseEmitter registerSseEmitter() {
        sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        sseEmitter.onError((ex) -> log.info("SseEmitter got error:", ex));

        return sseEmitter;
    }

    @SneakyThrows
    @Override
    public void render() {
        sseEmitter.send("");
    }
}

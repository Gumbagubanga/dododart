package net.quombat.dododart.infrastructure.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SseDriver {

    private final List<SseEmitter> sseEmitters = new ArrayList<>();

    public SseEmitter registerSseEmitter() {
        for (SseEmitter sseEmitter : sseEmitters) {
            sseEmitter.complete();
        }
        sseEmitters.clear();

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        sseEmitter.onError((ex) -> log.info("SseEmitter got error: {}", ex.getMessage()));

        sseEmitters.add(sseEmitter);

        return sseEmitter;
    }

    public void update(String object) {
        sseEmitters.forEach(s -> getSend(s, object));
    }

    @SneakyThrows
    private static void getSend(SseEmitter sseEmitter, String json) {
        sseEmitter.send(json);
    }

}

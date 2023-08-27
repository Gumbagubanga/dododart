package net.quombat.dododart.adapter.out.web;

import com.google.gson.Gson;

import net.quombat.dododart.application.ports.out.RenderPort;
import net.quombat.dododart.domain.DomainEvent;
import net.quombat.dododart.infrastructure.web.SseDriver;

import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
class WebRenderAdapter implements RenderPort {

    private final SseDriver sseDriver;

    @Override
    public void render(List<? super DomainEvent> domainEvents) {
        List<String> collect = domainEvents.stream()
            .map(Object::getClass)
            .map(Class::getSimpleName)
            .collect(Collectors.toList());
        String object = new Gson().toJson(collect);

        sseDriver.update(object);
    }

}

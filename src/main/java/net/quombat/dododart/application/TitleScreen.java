package net.quombat.dododart.application;

import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TitleScreen implements Screen {

    @Override
    public ModelAndView render() {
        return new ModelAndView("fragments/title");
    }
}

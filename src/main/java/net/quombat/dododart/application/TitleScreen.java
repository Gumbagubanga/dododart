package net.quombat.dododart.application;

import org.springframework.web.servlet.ModelAndView;

public class TitleScreen implements Screen {
    @Override
    public ModelAndView render() {
        return new ModelAndView("fragments/title");
    }
}

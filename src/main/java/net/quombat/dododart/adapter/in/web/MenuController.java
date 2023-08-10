package net.quombat.dododart.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
class MenuController {

    @GetMapping("/")
    public String menu(Model model) {
        model.addAttribute("inputModel", new InputModel());
        return "menu";
    }
}

package net.quombat.dododart.mainmenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
class MainMenuController {

    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    @PostMapping("/startGame")
    public String startGame(GameMode gameMode) {
        return "redirect:" + String.format("/game/%s/%d", gameMode.getGameType(), gameMode.getNumberOfPlayers());
    }
}

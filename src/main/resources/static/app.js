dodo = (function (root) {
    function loadAudio(root) {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function () {
            if (this.readyState === 4 && this.status === 200) {
                const data = JSON.parse(this.responseText);
                root.sound = new Howl(data);
                root.sound.volume(0.75);
            }
        };
        xmlhttp.open("GET", "./dodoaudio.json");
        xmlhttp.send();
    }

    function enterFullscreen() {
        let element = document.querySelector("body");
        if (element.requestFullscreen) {
            element.requestFullscreen();
        } else if (element.webkitRequestFullscreen) {  // iOS Safari
            element.webkitRequestFullscreen();
        }
    }

    let gameLoop;
    let gameLoopId;

    function handleLoops() {
        let loop = htmx.find('#audio').className.split(' ')
            .filter(c => c.startsWith('sprite-'))
            .map(c => c.slice(7))
            .filter(c => c.endsWith('-loop'))
            .map(c => c.slice(0, -5))[0];

        if (gameLoop !== loop) {
            if (gameLoopId !== undefined) {
                console.log(`stopping ${gameLoopId}`);
                window.sound.stop(gameLoopId);
            }
            gameLoop = loop;

            if (loop === undefined) {
                gameLoopId = undefined;
            } else {
                console.log(`starting ${loop}`);
                gameLoopId = window.sound.play(loop);
                window.sound.loop(true, gameLoopId);
                window.sound.volume(0.5, gameLoopId);
            }

        }
    }

    function handleSounds() {
        let sounds = htmx.find('#audio').className.split(' ')
            .filter(c => c.startsWith('sprite-'))
            .filter(c => !c.endsWith('-loop'))
            .map(c => c.slice(7))

        if (sounds.length > 0) {
            if (gameLoopId !== undefined) {
                window.sound.volume(0.2, gameLoopId);
            }
            sounds.forEach(s => {
                let sound = window.sound.play(s);
                if (gameLoopId !== undefined) {
                    window.sound.once('end', function () {
                        window.sound.volume(0.5, gameLoopId);
                    }, sound);
                }
            });
        }

    }

    return {
        init() {
            loadAudio(root);

            htmx.on('click', enterFullscreen);
            htmx.on('htmx:afterSettle', handleLoops);
            htmx.on('htmx:afterSettle', handleSounds);
        }
    }
})(window);

dodo.init();
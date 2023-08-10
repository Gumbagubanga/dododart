(function loadAudio() {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            const data = JSON.parse(this.responseText);
            window.sound = new Howl(data);
            window.sound.volume(0.75);

            const loopId = window.sound.play('2-Background');
            window.sound.volume(0.5, loopId);

            Howl.prototype.playString = function (arr) {
                if (arr.length > 1) {
                    this._onend[0] = function () {
                        this.playString(arr.slice(1, arr.length));
                    };
                } else {
                    this._onend[0] = function () {
                    };
                }
                if (arr.length > 0) {
                    this.play(arr[0]);
                }
            };
        }
    };
    xmlhttp.open("GET", "./dodoaudio.json", false);
    xmlhttp.send();
})();

function enterFullscreen() {
    let element = document.querySelector("body");
    if (element.requestFullscreen) {
        element.requestFullscreen();
    } else if (element.webkitRequestFullscreen) {  // iOS Safari
        element.webkitRequestFullscreen();
    }
}

document.addEventListener('click', enterFullscreen);

const eliminationSounds = ['BYEBYE', 'DRAGONPUNCH', 'FATALITY', 'FLAWLESS', 'ILLGETYOU',
                           'JUSTYOUWAIT', 'LAUGH', 'NOOO', 'OHDEAR', 'OOPS', 'OUCH', 'PERFECT',
                           'RUNAWAY', 'STUPID', 'TRAITOR', 'UH-OH', 'YOULLREGRETTHAT',
                           'WHATTHE'];
const highTripleSounds = ['BRILLIANT', 'COLLECT', 'FLAWLESS', 'PERFECT'];

document.addEventListener('sse:message', function (evt) {
    let events = JSON.parse(evt.detail.data);
    let samples = [];
    events.forEach(evt => {
        switch (evt) {
            case 'PlayerEliminatedEvent':
                samples.push(eliminationSounds[Math.floor(
                    Math.random() * eliminationSounds.length)]);
                break;
            case 'GameOverEvent':
                samples.push('VICTORY');
                break;
            case 'BustEvent':
                samples.push('BUMMER');
                break;
            case 'HighTripleHitEvent':
                samples.push(highTripleSounds[Math.floor(
                    Math.random() * highTripleSounds.length)]);
                break;
            default:
                break;
        }
    });

    window.sound.playString(samples);
});
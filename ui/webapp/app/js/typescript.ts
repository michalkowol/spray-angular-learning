class Animal {
    constructor(public name: string) { }
    move(meters: number) {
        alert(this.name + " moved " + meters + "m.");
    }
}

class Snake extends Animal {
    constructor(name: string) { super(name); }
    move() {
        alert("Slithering...");
        super.move(5);
    }
}

class Horse extends Animal {
    constructor(name: string) { super(name); }
    move() {
        alert("Galloping...");
        super.move(45);
    }
}

var sam = new Snake("Sammy the Python");
var tom: Animal = new Horse("Tommy the Palomino");

sam.move();
tom.move(34);


module Sayings {
    export class Greeter {
        greeting: string;
        constructor(message: string) {
            this.greeting = message;
        }
        greet() {
            return "Hello, " + this.greeting;
        }
    }
}
var greeter = new Sayings.Greeter("world");

var button = document.createElement('button');
button.textContent = "Say Hello";
button.onclick = function() {
    alert(greeter.greet());
};

document.body.appendChild(button);

interface Ray {
    start: string;
    dir: number;
}

class Sphere implements Ray {
    dir = 3;
    start = "aaa";
}

"use strict";
function passwordShow() {
    let pw = document.getElementById("password");
    let pwButton = document.getElementById("showpassword");
    if (pw.type === "password") {
        pw.type = "text";
        pwButton.innerHTML = 'Passwort verstecken';
        pwButton.style.width = '115px';
    }
    else {
        pw.type = "password";
        pwButton.innerHTML = 'Passwort anzeigen';
        pwButton.style.width = '110px';
    }
}

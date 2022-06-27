function passwordShow() {
    let pw: HTMLInputElement = document.getElementById("password") as HTMLInputElement;
    let pwButton: HTMLButtonElement = document.getElementById("showpassword") as HTMLButtonElement;
    if (pw.type === "password") {
        pw.type = "text";
        pwButton.innerHTML = 'Passwort verstecken';
        pwButton.style.width = '115px';
    } else {
        pw.type = "password";
        pwButton.innerHTML = 'Passwort anzeigen';
        pwButton.style.width = '110px';
    }
}
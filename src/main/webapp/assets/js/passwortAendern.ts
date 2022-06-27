let oldpw: HTMLInputElement = document.getElementById("oldpassword") as HTMLInputElement;
let newpw: HTMLInputElement = document.getElementById("newpassword") as HTMLInputElement;
let newpw2: HTMLInputElement = document.getElementById("newpassword2") as HTMLInputElement;
let button: HTMLInputElement = document.getElementById("enter") as HTMLInputElement;

function showPassword() {
    let check: HTMLInputElement = document.getElementById("showpassword") as HTMLInputElement;
    if (check.checked) {
        oldpw.type = "text";
        newpw.type = "text";
        newpw2.type = "text";
    } else {
        oldpw.type = "password";
        newpw.type = "password";
        newpw2.type = "password";

    }
}

function valide() {
    if (newpw.value == newpw2.value) {
        document.getElementById('message')!.style.color = 'green';
        document.getElementById('message')!.innerHTML = 'stimmt überein';
        button.disabled = false;
    } else if (newpw.value != newpw2.value) {
        document.getElementById('message')!.style.color = 'red';
        document.getElementById('message')!.innerHTML = 'stimmt nicht überein';
        button.disabled = true
    }
}


function changePassword(oldpw: string, newpw: string, user: string) {
    let data = new FormData();
    data.append('oldpw', encodeURIComponent(oldpw));
    data.append('newpw', encodeURIComponent(newpw));
    data.append("user", encodeURIComponent(user));
    try {
        fetch(`${server}/services/qna/changePassword/`, {body: data, method: 'POST'}).then()
    } catch (e) {
        popup(false, 'Passwort konnte aus unerwarteten Gründen nicht geändert werden!')
    }
}
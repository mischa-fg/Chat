"use strict";
let oldpw = document.getElementById("oldpassword");
let newpw = document.getElementById("newpassword");
let newpw2 = document.getElementById("newpassword2");
let button = document.getElementById("enter");
function showPassword() {
    let check = document.getElementById("showpassword");
    if (check.checked) {
        oldpw.type = "text";
        newpw.type = "text";
        newpw2.type = "text";
    }
    else {
        oldpw.type = "password";
        newpw.type = "password";
        newpw2.type = "password";
    }
}
function valide() {
    if (newpw.value == newpw2.value) {
        document.getElementById('message').style.color = 'green';
        document.getElementById('message').innerHTML = 'stimmt überein';
        button.disabled = false;
    }
    else if (newpw.value != newpw2.value) {
        document.getElementById('message').style.color = 'red';
        document.getElementById('message').innerHTML = 'stimmt nicht überein';
        button.disabled = true;
    }
}
function changePassword(oldpw, newpw, user) {
    let data = new FormData();
    data.append('oldpw', encodeURIComponent(oldpw));
    data.append('newpw', encodeURIComponent(newpw));
    data.append("user", encodeURIComponent(user));
    try {
        fetch(`${server}/services/qna/changePassword/`, { body: data, method: 'POST' }).then();
    }
    catch (e) {
        popup(false, 'Passwort konnte aus unerwarteten Gründen nicht geändert werden!');
    }
}

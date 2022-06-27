"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
function startScript() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        yield initMaxLengths(page, [{
                elem: document.getElementById('oldPass'),
                name: 'USER_PASSWORD'
            }, {
                elem: document.getElementById('newPass'),
                name: 'USER_PASSWORD'
            }, {
                elem: document.getElementById('newPassConfirm'),
                name: 'USER_PASSWORD'
            }, { elem: document.getElementById('newUserEmailAddress'), name: 'USER_EMAIL' }]);
        let cpf = document.getElementById('changePasswordForm');
        cpf.addEventListener("keyup", check);
    });
}
function check() {
    let npassword = document.getElementById('newPass');
    let npasswordc = document.getElementById('newPassConfirm');
    if (npassword.value !== npasswordc.value)
        document.getElementById("notSame").innerHTML = 'Das neue Passwort stimmt nicht überein';
    else
        document.getElementById("notSame").innerHTML = '';
}
function changePW() {
    return __awaiter(this, void 0, void 0, function* () {
        let oldPass = document.getElementById('oldPass').value;
        let newPass = document.getElementById('newPass').value;
        try {
            let response = yield fetch(`${server}/services/adminTool/changePassword`, {
                method: 'post',
                body: `oldPass=${encodeURIComponent(oldPass)}&newPass=${encodeURIComponent(newPass)}`
            });
            let json = yield response.json();
            let changed = false;
            if (json.changed === 'true')
                changed = true;
            if (!changed) {
                document.getElementById('notSame').innerHTML = 'Das alte Passwort ist Falsch oder überprüfe deine Internetverbindung';
            }
            else {
                yield logout(new URLSearchParams(window.location.search));
            }
        }
        catch (e) {
        }
    });
}
document.title = 'AdminTool - Einstellungen';

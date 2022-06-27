async function startScript() {
    const page = pageCheck;
    await initMaxLengths(page, [{
        elem: document.getElementById('oldPass') as HTMLInputElement,
        name: 'USER_PASSWORD'
    }, {
        elem: document.getElementById('newPass') as HTMLInputElement,
        name: 'USER_PASSWORD'
    }, {
        elem: document.getElementById('newPassConfirm') as HTMLInputElement,
        name: 'USER_PASSWORD'
    }, {elem: document.getElementById('newUserEmailAddress') as HTMLInputElement, name: 'USER_EMAIL'}])
    let cpf = document.getElementById('changePasswordForm') as HTMLElement;
    cpf.addEventListener("keyup", check);
}

function check() {
    let npassword = document.getElementById('newPass') as HTMLInputElement;
    let npasswordc = document.getElementById('newPassConfirm') as HTMLInputElement;
    if (npassword.value !== npasswordc.value)
        document.getElementById("notSame")!.innerHTML = 'Das neue Passwort stimmt nicht überein';
    else
        document.getElementById("notSame")!.innerHTML = '';
}

async function changePW() {
    let oldPass = (document.getElementById('oldPass') as HTMLInputElement).value;
    let newPass = (document.getElementById('newPass') as HTMLInputElement).value;

    try {
        let response = await fetch(`${server}/services/adminTool/changePassword`, {
            method: 'post',
            body: `oldPass=${encodeURIComponent(oldPass)}&newPass=${encodeURIComponent(newPass)}`
        });
        let json = await response.json();

        let changed = false;
        if (json.changed === 'true') changed = true;

        if (!changed) {
            document.getElementById('notSame')!.innerHTML = 'Das alte Passwort ist Falsch oder überprüfe deine Internetverbindung';
        } else {
            await logout(new URLSearchParams(window.location.search));
        }
    } catch (e) {

    }
}

document.title = 'AdminTool - Einstellungen';
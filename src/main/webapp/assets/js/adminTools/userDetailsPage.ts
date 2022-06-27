async function loadUser() {
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    let userID: number = +urlParams.get("objectID")!;
    users = [];

    if (userID != null && !isNaN(userID) && userID > 0) {
        try {
            let response = await fetch(`${server}/services/get/singleUser?userID=${userID}`);
            let json = await response.json();


            let user: MyUser = new MyUser(json[0].id, json[0].email, json[0].canCreate, json[0].lastLoggedIn, json[0].isCurrentUser);
            users.push(user);
            dataClass = user;
            if (user.isCurrentUser) {
                loggedInUser = user;
            }

            await initialiseUserEditPage(user);
        } catch (e) {

            popup(false, 'User konnte nicht geladen werden!');
        }
    }
}

// Edit Tage Page
async function initialiseUserEditPage(user: MyUser) {
    const userNameInput = document.getElementById("userName") as HTMLInputElement;
    const userCanCreateInput = (document.getElementById("canCreateUserInput") as HTMLInputElement);
    const resetPasswordInput = document.getElementById('resetPassword') as HTMLButtonElement;
    const labelOfUserCanCreateInput = document.getElementById('labelOfCanCreateUserInput') as HTMLElement;

    document.title = adminToolPageTitleName + ' - Benutzer bearbeiten';

    if (user != null) {

        if (user.isCurrentUser) {
            userCanCreateInput.remove();
            resetPasswordInput.remove();
            labelOfUserCanCreateInput.remove();

            popup(false, "Du Bearbeitest dich selber! Möglichkeiten wurden eingeschränkt!", 5000);
        } else {
            userCanCreateInput.checked = user.canCreateUser;
        }

        let submitButton = document.getElementById("userFormSubmitButton") as HTMLButtonElement;
        submitButton.setAttribute("onClick", "updateUser();");
        userNameInput.value = user.email;

        disableLoader();
    } else {
        await loadPage("settings.jsp", "settingsButton", false, -1, true);
    }
}

async function updateUser() {
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);

    let userId: number = +urlParams.get("objectID")!;
    let inputCreate = 0;
    let currentUser: MyUser = dataClass as MyUser;
    if (!currentUser.isCurrentUser) {
        if ((document.getElementById("canCreateUserInput") as HTMLInputElement).checked) {
            inputCreate = 1;
        } else {
            inputCreate = 2;
        }
    }


    let userName = (document.getElementById("userName") as HTMLInputElement).value;

    if (userName != '' && userName != null) {
        try {
            let response = await fetch(server + '/services/adminTool/editUser', {
                method: 'post',
                body: `userId=${userId}&email=${userName}&canCreateUser=${inputCreate}`
            });
            if (response.ok) {
                await loadPage('userDetails.jsp', 'settingsButton', true, -1, true);
            } else {
                popup(false, 'Dieser Nutzer konnte nicht aktualisiert werden!');
            }
        } catch (e) {
            popup(false, 'Dieser Nutzer konnte nicht aktualisiert werden!');
        }
    } else {
        document.getElementById('errorTag')!.innerHTML = 'Es wurde kein Nutzer gefunden';
    }
}

(document.getElementById('resetPassword') as HTMLButtonElement).onclick = async () => {
    function copyToClipboard(content: string) {
        navigator.clipboard.writeText(content).then(() => {
        }, () => {

        })
    }

    if (confirm("Bist du sicher du willst das Passwort von diesem User zurücksetzen?")) {
        try {
            let fullSearchParams: string = window.location.search;
            const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);

            let userId: number = +urlParams.get("objectID")!;
            let response = await fetch(`${server}/ResetPassword?userId=${userId}`);
            let json = await response.json();

            const emailDiv = document.createElement('div');
            const emailTitle = document.createElement('span')
            emailTitle.innerHTML = 'Email: ';
            emailTitle.style.fontWeight = 'bold';
            const email = document.createElement('span');
            email.innerHTML = json.email;
            const copyEmailIcon = document.createElement('img') as HTMLImageElement;
            copyEmailIcon.src = `${server}/assets/images/copy.svg`;
            copyEmailIcon.classList.add('copyTextIcon');
            copyEmailIcon.onclick = () => {
                copyToClipboard(json.email);
            }
            emailDiv.append(emailTitle, email, copyEmailIcon);

            const newPasswordData = document.getElementById('newPasswordData');
            newPasswordData!.innerHTML = '';
            const passwordDiv = document.createElement('div');
            const passwordTitle = document.createElement('span');
            passwordTitle.innerHTML = 'Passwort: ';
            passwordTitle.style.fontWeight = 'bold';
            const password = document.createElement('span');
            password.innerHTML = json.password;
            const copyPasswordIcon = document.createElement('img');
            copyPasswordIcon.src = `${server}/assets/images/copy.svg`;
            copyPasswordIcon.classList.add('copyTextIcon');
            copyPasswordIcon.onclick = () => {
                copyToClipboard(json.password);
            }
            passwordDiv.append(passwordTitle, password, copyPasswordIcon);
            newPasswordData?.append(emailDiv, passwordDiv);
        } catch (e) {
            popup(false, 'Nutzer konnte nicht aktualisiert werden!');
        }
    }
}
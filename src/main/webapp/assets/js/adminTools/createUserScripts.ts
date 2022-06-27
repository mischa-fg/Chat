function selectText(element: HTMLSpanElement) {
    let range = document.createRange()
    range.selectNodeContents(element)
    let windowSelection = window.getSelection()
    windowSelection?.removeAllRanges()
    windowSelection?.addRange(range)
}

(document.getElementById('createUserButton') as HTMLButtonElement).onclick = async () => {
    const userEmail = (document.getElementById('newUserEmailAddress') as HTMLInputElement).value;
    const userCanCreateUsers = (document.getElementById('canCreateUserInput') as HTMLInputElement).checked;

    try {
        const response = await fetch(`${server}/CreateUser?email=${userEmail}&canCreateUser=${userCanCreateUsers}`);
        const json = await response.json();

        const newUserData = document.getElementById('newUserData');
        newUserData!.innerHTML = '';

        if (json.error === '') {
            const emailContainer = document.createElement('div');
            const emailTitle = document.createElement('span')
            emailTitle.innerHTML = 'Email: ';
            emailTitle.style.fontWeight = 'bold';
            const email = document.createElement('span');
            email.innerHTML = json.email;
            emailContainer.append(emailTitle, email);

            const passwordContainer = document.createElement('div');
            const passwordTitle = document.createElement('span');
            passwordTitle.innerHTML = 'Passwort: ';
            passwordTitle.style.fontWeight = 'bold';
            const password = document.createElement('span');
            password.innerHTML = json.password;
            passwordContainer.append(passwordTitle, password);

            newUserData?.append(emailContainer, passwordContainer);
        } else {
            const error = document.createElement('p');
            error.innerHTML = json.error;
            error.style.fontWeight = 'bold';
            newUserData?.append(error);
        }
    } catch (e) {
        popup(false, 'Nutzer konnte aus unerwarteten gründen nicht erstellt werden!');
    }
    
    loadUsers();
}
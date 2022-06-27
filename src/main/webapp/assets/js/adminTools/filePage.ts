document.title = adminToolPageTitleName + ' - Dateien';

interface ResponseFile {
    name: string,
    type: string,
    id: number,
    answerCount: number
}

async function getFilesFromServer() {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/files`);
        let json = await response.json() as { files: ResponseFile[] };
        let noFiles = document.getElementById('noFiles');

        disableLoader();

        let container = document.getElementById('fileContainer');
        container!.innerHTML = '';
        noFiles!.innerHTML = '';

        if (json.files.length <= 0) {
            noFiles!.innerHTML = 'Die Datenbank enthält keine Dateien.';
        }
        let files = [];
        for (let file of json.files) {
            files.push(new MyFile(file.id, file.name, file.type));
            

            let cell = document.createElement('div') as HTMLDivElement;
            let img = document.createElement('img') as HTMLImageElement;
            let anchor = document.createElement('a') as HTMLAnchorElement;
            let title = document.createElement('p') as HTMLParagraphElement;
            let deleteButton = document.createElement('button') as HTMLButtonElement;

            // Prepare Image
            img.classList.add('file__img');
            img.src = `${server}/assets/images/files/${file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()}.svg`;

            // Prepare Title
            title.classList.add('file__title');
            title.innerHTML = file.name;

            // Prepare Anchor
            anchor.appendChild(img);
            anchor.appendChild(title);
            anchor.target = '_blank';
            anchor.classList.add('file__anchor');
            anchor.href = `${server}/file?id=${file.id}`;

            // Prepare Cell
            cell.classList.add('file__cell');
            cell.append(anchor);

            // Prepare delete Button
            deleteButton.innerHTML = 'Löschen';
            deleteButton.classList.add('btn');
            deleteButton.classList.add('buttonSubmit');
            deleteButton.classList.add('file__delete');
            deleteButton.onclick = async () => {


                if (confirm(`Willst du die Datei "${file.name}" wirklich löschen und von ${file.answerCount} Antworten entfernen?`)) {
                    let success = true;

                    try {
                        await fetch(`${server}/services/adminTool/removeFile`, {
                            method: 'post',
                            body: `fileID=${file.id}`
                        });
                        checkPageExecute(async () => {
                            await getFilesFromServer();
                        }, 'files')
                    } catch (e) {

                        success = false;
                    }

                    checkPageExecute(() => popup(success, file.name + ' ' + ((success) ? 'wurde erfolgreich gelöscht!' : 'konnte nicht gelöscht werden!')), 'files');
                }
            }

            checkPageExecute(() => {
                cell.appendChild(deleteButton);
                container?.appendChild(cell);
            }, 'files')
        }
    } catch (e) {
        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), 'files');
    }
}

// Uploads file to the database
async function submitFileForm() {
    let form = document.getElementById('fileAddForm') as HTMLFormElement;
    let statusParagraph = document.getElementById('uploadStatus') as HTMLParagraphElement;
    const page = pageCheck;

    let fd = new FormData(form);
    let file = fd.get('file') as File;

    let blob = file.slice(0, file.size, file.type);
    let name = file.name.replace('ä', 'ae');
    name = name.replace('Ä', 'Ae');
    name = name.replace('ö', 'oe');
    name = name.replace('Ö', 'Oe');
    name = name.replace('ü', 'ue');
    name = name.replace('Ü', 'Ue');
    name = name.replace(/[^a-zA-Z0-9.\-_]/g, '');

    statusParagraph.innerHTML = 'Datei wird hochgeladen...';

    fd.set('file', new File([blob], name, {type: file.type}));
    try {
        let response = await fetch(`${server}/FileUpload`, {method: 'post', body: fd});
        let json = await response.json() as { code: number, comment: string };
        statusParagraph.innerHTML = json.comment;

        checkPageExecute(async () => {
            if (json.code === 200) {
                await getFilesFromServer();
                (document.getElementById('fileInput') as HTMLInputElement).value = '';
                document.getElementById('fileSelectButton')!.innerHTML = 'Datei auswählen';
                popup(true, json.comment);
            } else {
                popup(false, json.comment);
            }
        }, 'files')

    } catch (e) {
        checkPageExecute(() => popup(false, 'Datei konnte aus unerwarteten Gründen nicht hochgeladen werden!'), 'files');
    }


}

function setFileText(inp: HTMLInputElement) {
    let e = document.getElementById('fileSelectButton');
    e!.innerHTML = inp.value.split(/([\\/])/g).pop() as string;
}

getFilesFromServer().then();

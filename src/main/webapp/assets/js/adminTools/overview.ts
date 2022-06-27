document.title = adminToolPageTitleName + ' - Startseite';

async function loadEverything(timeRange: string, buttonElement: HTMLSpanElement, initialization: boolean = false) {
    await loadSentAnswers(timeRange, buttonElement, initialization);
    await loadAnsweredVsUnanswered(timeRange, buttonElement, initialization);
}

async function loadSentAnswers(timeRange: string, buttonElement: HTMLSpanElement, initialization: boolean = false, elementIdOfLoader: string = 'loaderAnimationContainer1') {
    if (!initialization) {
        $('#sentAnswersTime .active').removeClass('active'); // Remove the "active" class there where we had it now
        buttonElement.classList.add('active'); // Add the active class to the new element
    }

    initLoadingAnimation(elementIdOfLoader);

    await getSentAnswersTime(timeRange, initialization);

    disableLoader();
}

async function loadAnsweredVsUnanswered(timeRange: string, buttonElement: HTMLSpanElement, initialization: boolean = false, elementIdOfLoader: string = 'loaderAnimationContainer2') {
    if (!initialization) {
        $('#answeredVsUnansweredTime .active').removeClass('active'); // Remove the "active" class there where we had it now
        buttonElement.classList.add('active'); // Add the active class to the new element
    }

    initLoadingAnimation(elementIdOfLoader);

    await answeredAndUnansweredQuestionsTime(timeRange, initialization);

    disableLoader();
}

async function answeredAndUnansweredQuestionsTime(timeRange: string, initialization: boolean = false, canvasId: string = "chartAnsweredToUnanswered") {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/getStatistics/answeredVsUnansweredPerTime?timeRange=${timeRange}`);
        let responeJson = await response.json();
        
        let jsonAnswered = await responeJson.answered;
        let jsonUnanswered = await responeJson.unanswered;
        let dates = await responeJson.dates;
        checkPageExecute(() => createAnsweredQuestionChart(canvasId, [jsonAnswered, jsonUnanswered], dates, initialization), 'overview');
    } catch (e) {
        
        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), 'overview');
    }
}

async function getSentAnswersTime(timeRange: string, initialization: boolean, canvasId: string = "myChart") {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/getStatistics/sentAnswersPerTime?timeRange=${timeRange}`);
        let json = await response.json();
        checkPageExecute(() => createSentAnswersChart(canvasId, json, initialization), 'overview');
    } catch (e) {
        
        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), 'overview');
    }
}

async function createSentAnswersChart(canvasId: string, data: any, initialization: boolean) {
    let canvasElement = document.getElementById(canvasId) as HTMLCanvasElement;
    let ctx = canvasElement.getContext('2d');

    let labels: string[] = [];
    let amounts: number[] = [];
    for (let dataSet of data) {
        labels.push(dataSet.date)
        amounts.push(dataSet.amount)
    }
    let totalThisTime: number = 0;
    for (let num of amounts) {
        totalThisTime += num;
    }

    let totalAnswersElement = document.getElementById('totalAnswersInThisTime') as HTMLSpanElement;
    totalAnswersElement.innerHTML = "Total gesendete Antworten in dieser Zeit = " + totalThisTime;

    if (initialization) {
        // @ts-ignore
        answerChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Anzahl gesendeter Antworten',
                    data: amounts,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    } else {
        answerChart.data.datasets[0].data = amounts;
        answerChart.data.labels = labels;
        answerChart.update();
    }
}

async function createAnsweredQuestionChart(canvasId: string, data: any, dates: any, initialization: boolean) {
    let canvasElement = document.getElementById(canvasId) as HTMLCanvasElement;
    let ctx = canvasElement.getContext('2d');

    let amountsAnswered: number[] = [];
    for (let dataSet of data[0]) {
        amountsAnswered.push(dataSet.amount)
    }
    let totalAnsweredThisTime: number = 0;
    for (let num of amountsAnswered) {
        totalAnsweredThisTime += num;
    }

    let amountsUnanswered: number[] = [];
    for (let dataSet of data[1]) {
        amountsUnanswered.push(dataSet.amount)
    }
    let totalUnansweredThisTime: number = 0;
    for (let num of amountsUnanswered) {
        totalUnansweredThisTime += num;
    }
    let labels: string[] = [];
    for (let date of dates) {
        labels.push(date.date);
    }

    let totalAnsweredElement = document.getElementById('totalAnsweredInThisTime') as HTMLSpanElement;
    totalAnsweredElement.innerHTML = "Total beantwortete Fragen in dieser Zeit = " + totalAnsweredThisTime;

    let totalUnansweredElement = document.getElementById('totalUnansweredInThisTime') as HTMLSpanElement;
    totalUnansweredElement.innerHTML = "Total unbeantwortete Fragen in dieser Zeit = " + totalUnansweredThisTime;

    if (initialization) {
        // @ts-ignore
        answeredQuestionVsUnansweredQuestionChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Anzahl beantworteter Fragen',
                    data: amountsAnswered,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.9)',
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                    ],
                    borderWidth: 1
                }, {
                    label: 'Anzahl unbeantworteter Fragen',
                    data: amountsUnanswered,
                    backgroundColor: [
                        'rgba(54, 162, 235, 0.9)',
                    ],
                    borderColor: [
                        'rgba(54, 162, 235, 1)',
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    } else {
        answeredQuestionVsUnansweredQuestionChart.data.datasets[0].data = amountsAnswered;
        answeredQuestionVsUnansweredQuestionChart.data.datasets[1].data = amountsUnanswered;
        answeredQuestionVsUnansweredQuestionChart.data.labels = labels;
        answeredQuestionVsUnansweredQuestionChart.update();
    }
}
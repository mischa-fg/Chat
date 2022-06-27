package com.ubs.backend.demo;

import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.questions.DefaultQuestion;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.classes.enums.DataTypeInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.ubs.backend.util.PrepareString.prepareString;

/**
 * @author Marc
 * @author Magnus
 * @author Tim Irmler
 * @author Sarah
 * @since 17.07.2021
 */
public class DBData {
    /**
     * @return all set type tags
     * @author Tim Irmler
     * @since 05.08.2021
     */
    public List<TypeTag> getTypeTags() {
        Tag facts = new Tag("facts");
        Tag fakten = new Tag("fakten");
        Tag statistik = new Tag("statistik");
        Tag witz = new Tag("witz");
        Tag joke = new Tag("joke");

        ArrayList<TypeTag> typeTags = new ArrayList<>();
        typeTags.add(new TypeTag(facts, AnswerType.FACTS));
        typeTags.add(new TypeTag(fakten, AnswerType.FACTS));
        typeTags.add(new TypeTag(statistik, AnswerType.STATISTICS));
        typeTags.add(new TypeTag(witz, AnswerType.JOKE));
        typeTags.add(new TypeTag(joke, AnswerType.JOKE));

        return typeTags;
    }

    /**
     * @return all set type tag answers, like jokes
     * @author Tim Irmler
     */
    public List<Answer> getTypeTagAnswers() {
        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(new Answer(prepareString("Fakten über uns", DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false),
                prepareString("Wir sind ein Team aus vier Personen welche im Rahmen des Project Office an STIMA gearbeitet haben.\n\n" +
                                "Das Team\n" +
                                "<ul>" +
                                "<li>Tim Irmler</li>" +
                                "<li>Marc Andri Fuchs</li>" +
                                "<li>Magnus Götz</li>" +
                                "<li>Sarah Ambi</li>" +
                                "</ul>",
                        DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.FACTS));

        answers.add(new Answer(prepareString("Namen des Chatbots", DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false),
                prepareString("Der Namen des Chatbots ist eine Mischung aus den Namen der Entwickler!", DataTypeInfo.ANSWER_TEXT.getMaxLength(),
                        false, false), AnswerType.FACTS));

        answers.add(new Answer(prepareString("Windows ist wie ein U-Boot", DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false), prepareString("Windows ist wie ein U-Boot, sobald man ein Fenster öffnet, fangen die Probleme an", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.JOKE));
        answers.add(new Answer(prepareString("Bester Ort um eine Leiche zu verstecken", DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false), prepareString("Wo ist der beste Ort um eine Leiche zu verstecken? Seite 2 auf Google", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.JOKE));
        answers.add(new Answer(prepareString("Ein Softwareentwickler und seine Frau", DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false), prepareString("Ein Softwareentwickler und seine Frau.\n" +
                "\n" +
                "Sie: „Schatz, wir haben kein Brot mehr. Könntest du bitte zum Kiosk an der Ecke gehen und eins holen? Und wenn sie Eier haben, bring 6 Stück mit.“\n" +
                "\n" +
                "Nach kurzer Zeit kommt er wieder zurück und hat 6 Brote dabei.\n" +
                "\n" +
                "Sie: „Warum nur hast du 6 Brote gekauft?“\n" +
                "\n" +
                "Er: „Sie hatten Eier.“", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.JOKE));

        return answers;
    }

    /**
     * @return all set results with their corresponding tags and answers
     * @author Marc
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.202
     */
    public List<Result> getResults() {
        Tag gtrsTag = new Tag("gtrs");
        Tag zeit = new Tag("zeit");
        Tag arbeitszeit = new Tag("arbeitszeit");

        Tag hriTag = new Tag("hri");
        Tag ferien = new Tag("ferien");
        Tag absenzen = new Tag("absenzen");
        Tag krank = new Tag("krank");

        Tag payslipTag = new Tag("payslip");
        Tag lohnAbrechnung = new Tag("lohnabrechnung");
        Tag abrechnung = new Tag("abrechnung");

        Tag wifiTag = new Tag("wifi");
        Tag wlan = new Tag("wlan");

        Tag mashopTag = new Tag("mashop");
        Tag verguenstigung = new Tag("vergünstigung");
        Tag rabatt = new Tag("rabatt");
        Tag angebot = new Tag("angebot");

        Tag suiTag = new Tag("sui");
        Tag vouchen = new Tag("vouchen");
        Tag hinterlegen = new Tag("hinterlegen");
        Tag buergen = new Tag("bürgen");
        Tag identifizieren = new Tag("identifizieren");

        Tag templafyTag = new Tag("templafy");

        Tag myticketsTag = new Tag("mytickets");
        Tag ticket = new Tag("ticket");
        Tag service = new Tag("service");
        Tag anfragen = new Tag("anfragen");
        Tag support = new Tag("support");

        Tag bbsTag = new Tag("bbs");
        Tag berechtigung = new Tag("berechtigung");
        Tag laufwerk = new Tag("laufwerk");
        Tag benutzerberechtigungssystem = new Tag("benutzerberechtigungssystem");
        Tag antrag = new Tag("antrag");

        Tag mycampusTag = new Tag("mycampus");
        Tag wbt = new Tag("wbt");

        Tag lernJournalTag = new Tag("lernjournal");

        Tag meetingReservationTag = new Tag("meeting-reservation");
        Tag meeting = new Tag("meeting");
        Tag reservation = new Tag("reservation");
        Tag raum = new Tag("raum");
        Tag zimmer = new Tag("zimmer");
        Tag mieten = new Tag("mieten");
        Tag buchen = new Tag("buchen");

        Tag admin = new Tag("admin");
        Tag adminTool = new Tag("admintool");

        Tag helfer = new Tag("helfer");
        Tag freiwilliger = new Tag("freiwilliger");
        Tag infortage = new Tag("infotag");

        Tag bildungziel = new Tag("bildungsziele");
        Tag praxisausbildung = new Tag("praxisausbildung");

        Tag richtlinien = new Tag("richtlinien");

        Tag vergessen = new Tag("vergessen");
        Tag passwort = new Tag("passwort");
        Tag smartCard = new Tag("smart-card");

        Tag noten = new Tag("noten");
        Tag noteneintrag = new Tag("noteneintrag");

        Tag feedbackTag = new Tag("feedback");
        Tag rueckmeldungTag = new Tag("rückmeldung");
        Tag modul = new Tag("modul");

        Tag monatsberichtTag = new Tag("monatsbericht");

        Tag mobilepass = new Tag("mobilepass");

        Tag blockieren = new Tag("blockieren");
        Tag entblocken = new Tag("entblocken");

        Tag ausserhaus = new Tag("ausserhaus");

        Tag kudosTag = new Tag("kudos");

        Tag stunde = new Tag("stunde");

        Tag datei = new Tag("datei");

        Tag arztzeugnis = new Tag("arztzeugnis");

        Tag time2learnTag = new Tag("time2learn");

        Tag ausbildungsplanTag = new Tag("ausbildungsplan");

        Tag programm = new Tag("programm");
        Tag installationTag = new Tag("installation");

        Tag blockLeave = new Tag("blockleave");
        Tag stueck = new Tag("stück");

        Tag verschluesseln = new Tag("verschlüsseln");

        Tag wetter = new Tag("Wetter");
        Tag temp = new Tag("Temperatur");

        Answer gtrs = new Answer(prepareString("Arbeitszeit", DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false), prepareString("Deine Arbeitszeit kannst du unter <a href=\"https://goto/gtrs\" target=\"_blank\">goto/gtrs</a> erfassen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer hri = new Answer("goto/hri", prepareString("Deine Ferien kannst du unter <a href=\"https://goto/hri\" target=\"_blank\">goto/hri</a> erfassen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer payslip = new Answer("Lohnabrechnung", prepareString("Deine Lohnabrechnung kannst du unter <a href=\"https://goto/payslip\" target=\"_blank\">goto/payslip</a> einsehen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer wifi = new Answer("goto/wifi", prepareString("Unter <a href=\"https://goto/wifi\" target=\"_blank\">goto/wifi</a> kannst du ein WLAN-Konto beantragen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer mashop = new Answer("Vergünstigung", prepareString("Unter <a href=\"https://goto/mashop\" target=\"_blank\">goto/mashop</a> kannst du alle Vergünstigungen und Angebote die du als UBS-Mitarbeiter bekommst einsehen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer sui = new Answer("Vouchen", prepareString("Unter <a href=\"https://goto/sui\" target=\"_blank\">goto/sui</a> kannst du für einen Kollegen bürgen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer templafy = new Answer("Templafy", prepareString("Unter <a href=\"https://goto/templafy\" target=\"_blank\">goto/templafy</a> findest du die Informationen zum UBS Templafy.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer mytickets = new Answer("Ticket erstellen", prepareString("Unter <a href=\"https://goto/mytickets\" target=\"_blank\">goto/mytickets</a> kannst du ein Support Ticket eröffnen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer bbs = new Answer("Antrag-Berechtigung", prepareString("Berechtigungen, wie z.B zugriff auf Laufwerke kannst du unter <a href=\"https://goto/bbs\" target=\"_blank\">goto/bbs</a> beantragen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer mycampus = new Answer("WBT", prepareString("Unter <a hre=\"https://goto/mycampus\" target=\"_blank\">goto/mycampus</a> siehst du deine offenen WBTs.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer meetingReservation = new Answer("Raum reservieren", prepareString("Wenn du für ein Meeting einen Raum benötigst, kannst du unter <a href=\"https://goto/meeting-reservation\" target=\"_blank\">goto/meeting-reservation</a> einen reservieren.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer adminToolLink = new Answer("Admin Tool", prepareString("<a href=\"/chatbot/pages/adminTool/adminTool.jsp\" target=\"_blank\">Admin Tool</a>", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT, true);
        Answer lernJournal = new Answer("Lernjournal", prepareString("Der Link zum lernjournal ist <a href=\"https://goto/lernjournal\" target=\"_blank\">goto/lernjournal</a>.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer volunteer = new Answer("Helfer für Infotage", prepareString("Unter <a href=\"https://goto/lev\" target=\"_blank\">goto/lev</a> kannst du dich als Freiwilliger für Infotage anmelden.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer bildungsziele = new Answer("Bildungsziele", prepareString("Unter <a href=\"https://goto/praxisausbildung\" target=\"_blank\">goto/praxisausbildung</a> findest du die Zielkataloge, welche deine Bildungsziele beschreiben.\nDiese kannst du für <a href=\"https://goto/time2learn\">goto/time2learn</a> verwenden.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer richtlinen = new Answer("Richtlinien", prepareString("Die UBS Richtlinen findest du unter <a href=\"https://goto/bdp\" target=\"_blank\">goto/bdp</a>.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer passwordForgot = new Answer("Smart Card Passwort vergessen", prepareString("<a href = \"https://bw.assist.ubs.com/node/234039\" target=\"_blank\">Hier</a> findest du eine Anleitung, wie du vorgehen musst wenn du dein Smart-Card Passwort vergessen hast.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer note = new Answer("Noten", prepareString("Unter <a href=\"www.it-university.ch\" target=\"_blank\">it-university</a> trägst du deine Noten ein und kannst alle bereits eingetragenen Noten ansehen.\nNoten von den Modulen werden automatisch von deinen Dozenten eingetragen und können auch hier eingesehen werden.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer feedback = new Answer("Feedback", prepareString("<a href=\"https://findmind.ch/c/\" target=\"_blank\">Hier</a> findest du die Seite um Feedback zu den Modulen zu geben. Du musst noch deine Klassenbezeichnung in der URL eingeben. Beispiel https://findmind.ch/c/UBSAPPIA20", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer monatsbericht = new Answer("Monatsbericht", prepareString("Gehe zu <a href=\"https://goto/gtrs\" target=\"_blank\">goto/gtrs</a> und klicke auf \"Berichte\", oben rechts siehst du ein Knopf mit einem PDF Symbol. Klicke auf diesen Knopf um den Monatsbericht als PDF herunterzuladen. Bitte stelle sicher das du den richtigen Monat ausgewählt hast!.\nDanach schreibst du eine E-mail an admin_ypm@ubs.com und im CC fügst du andreas.ilg@ubs.com hinzu.\nDu brauchst keinen Inhalt aber vergiss nicht die Datei anzuhängen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer mobilePass = new Answer("Mobilepass", prepareString("<a href=\"https://bw.assist.ubs.com/node/232169\" target=\"_blank\">Hier</a> findest du eine Anleitung, um deinen UBS MobilePass einzurichten.\nWeitere Infos findest du unter <a href=\"https://goto/mobilepass\">goto/mobilepass</a>.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer cardBlocked = new Answer("Smart Card blockiert", prepareString("<a href=\"https://bw.assist.ubs.com/node/225493\" target=\"_blank\">Hier</a> findest du eine Anleitung, wie du deine Smart Card entblocken kannst.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer outlookAusserhaus = new Answer("Outlook Ausserhaus", prepareString("<a href=\"https://bw.assist.ubs.com/node/56098\" target=\"_blank\">Hier</a> gibt es eine Anleitung, wie du dein Outlook einrichten kannst um auch ausserhalb von der UBS auf deine Mails zugreifen zu können.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer ill = new Answer("Krank", prepareString("<a href=\"https://hrsnow.ubs.net/sp?id=kb_article&kb=KB0011161\" target=\"_blank\">Hier</a> findest du eine Anleitung was du machen musst wenn du krank bist.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer kudos = new Answer("Kudos", prepareString("Unter <a href=\"https://goto/kudos\" target=\"_blank\">goto/kudos</a> kannst du Kudos-Punkte an Kollegen verschicken. Wenn du Kudos-Punkte verschicken willst, gehst du auf die Kudos Home-Seite und gibst den Namen der Person ein der du die Punkte zuschicken willst, zuletzt gibst du die anzahl Punkte an, wählst eine Karte aus und schreibst eine nette Nachricht.\nEs müssen immer mindestens 10, maximal 50 Punkte verschickt werden.\nDu hast immer nur ein begrenztes Budget an Kudos Punkte die du verschenken darfst.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer dateien = new Answer("Datei senden", prepareString("Grundsätzlich darf man Dateien welche als \"Öffentlich\" gekennzeichnet sind an aussenstehende zuschicken, jedoch keine Kundendaten oder Projektdaten.\nBitte gehe immer sicher und frage deinen Junior Talent Manager oder deinen Dozenten.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer maxStunde = new Answer("Max. Arbeitszeit", prepareString("Die maximale Arbeitszeit beträgt 9h pro Tag. Überstunden sollten generell vermieden werden.\nDie Mittagspause muss mindestens 30 Minuten betragen", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer arztZeugnis = new Answer("Arztzeugnis", prepareString("Wenn du mehr als fünf Arbeitstage krank bist musst du ein Arztzeugnis vorzeigen können.\nMehr Informationen findest du unter <a href=\"https://hrsnow.ubs.net/sp?id=kb_article&kb=KB0011161\" target=\"_blank\">https://hrsnow.ubs.net/</a>", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer time2learn = new Answer("time2learn", prepareString("Unter <a href=\"https://goto/time2learn\" target=\"_blank\">goto/time2learn</a> kannst du deine Lerndokumentation erfassen.\nDie dazugehörigen Dokumente findest du unter <a href=\"https://goto/praxisausbildung\" target=\"_blank\">goto/praxisausbildung<a> in den Zielkatalogen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer ausbildungsplan = new Answer("Ausbildungsplan", prepareString("Auf <a href=\"https://ubs.trainingracoon.ch/\" target=\"_blank\">ubs.trainingracoon.ch</a> findest deinen Ausbildungsplan von der UBS. Bitte frage bei deinem Junior talent manager wegen dem Passwort nach.\nFalls du mehr über die einzelnen Module wissen willst gehe auf <a href=\"https://www.ict-berufsbildung.ch/fileadmin/user_upload/01_Deutsch/Grafiken/Chart_Modulbaukasten_defD15_inkl_URL.pdf\" target=\"_blank\">www.ict-berufsbildung.ch</a>", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer installation = new Answer("Installation", prepareString("<a href=\"https://intranet.ubs.net/en/ubs-wealth-management/chief-investment-officer/global-investment-management/advisory-mandates/atk-art-advisory-solutions/apac/authorization-installation.html\" target=\"_blank\">Hier</a> kannst du die Installation von gewünschten Programmen auf deinen UBS-Workspace beantragen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer ferienStueck = new Answer("Ferienblock", prepareString("Einmal im Jahr musst du zwei Wochen hintereinander nehmen.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer mailVerschluesseln = new Answer("Mail verschlüsseln", prepareString("Öffne dein Outlook und gehe oben links zu \"File\"/\"Datei\" und klicke auf \"Options\"/\"Optionen\", dort findest du die Einstellung \"Encrypt\"/\"Verschlüsseln\". Dort kannst du dein Mail verschlüsseln.", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), AnswerType.DEFAULT);
        Answer wetterA = new Answer("Wetter", prepareString(
                "<div style=\"width:560px;height:460px\"><iframe scrolling=\"no\" style=\"border:0;width:100%;height:100%\" src=\"https://meteo.search.ch/widget/Zurich.de.html\"></iframe>\n" +
                        "</div>", DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false
        ), AnswerType.DEFAULT);

        ArrayList<Result> rs = new ArrayList<>();

        rs.add(new Result(gtrs, gtrsTag));
        rs.add(new Result(gtrs, zeit));
        rs.add(new Result(gtrs, arbeitszeit));

        rs.add(new Result(hri, hriTag));
        rs.add(new Result(hri, ferien));
        rs.add(new Result(hri, absenzen));
        rs.add(new Result(hri, krank));

        rs.add(new Result(payslip, payslipTag));
        rs.add(new Result(payslip, lohnAbrechnung));
        rs.add(new Result(payslip, abrechnung));

        rs.add(new Result(wifi, wifiTag));
        rs.add(new Result(wifi, wlan));

        rs.add(new Result(mashop, mashopTag));
        rs.add(new Result(mashop, verguenstigung));
        rs.add(new Result(mashop, rabatt));
        rs.add(new Result(mashop, angebot));

        rs.add(new Result(sui, suiTag));
        rs.add(new Result(sui, vouchen));
        rs.add(new Result(sui, hinterlegen));
        rs.add(new Result(sui, buergen));
        rs.add(new Result(sui, identifizieren));

        rs.add(new Result(templafy, templafyTag));

        rs.add(new Result(mytickets, myticketsTag));
        rs.add(new Result(mytickets, ticket));
        rs.add(new Result(mytickets, service));
        rs.add(new Result(mytickets, anfragen));
        rs.add(new Result(mytickets, support));

        rs.add(new Result(bbs, bbsTag));
        rs.add(new Result(bbs, berechtigung));
        rs.add(new Result(bbs, laufwerk));
        rs.add(new Result(bbs, benutzerberechtigungssystem));
        rs.add(new Result(bbs, antrag));

        rs.add(new Result(mycampus, mycampusTag));
        rs.add(new Result(mycampus, wbt));

        rs.add(new Result(meetingReservation, meetingReservationTag));
        rs.add(new Result(meetingReservation, meeting));
        rs.add(new Result(meetingReservation, reservation));
        rs.add(new Result(meetingReservation, raum));
        rs.add(new Result(meetingReservation, zimmer));
        rs.add(new Result(meetingReservation, mieten));
        rs.add(new Result(meetingReservation, buchen));

        rs.add(new Result(adminToolLink, admin));
        rs.add(new Result(adminToolLink, adminTool));

        rs.add(new Result(lernJournal, lernJournalTag));

        rs.add(new Result(volunteer, helfer));
        rs.add(new Result(volunteer, freiwilliger));
        rs.add(new Result(volunteer, infortage));

        rs.add(new Result(bildungsziele, bildungziel));
        rs.add(new Result(bildungsziele, praxisausbildung));

        rs.add(new Result(richtlinen, richtlinien));

        rs.add(new Result(passwordForgot, vergessen));
        rs.add(new Result(passwordForgot, passwort));
        rs.add(new Result(passwordForgot, smartCard));

        rs.add(new Result(note, noten));
        rs.add(new Result(note, noteneintrag));

        rs.add(new Result(feedback, feedbackTag));
        rs.add(new Result(feedback, rueckmeldungTag));
        rs.add(new Result(feedback, modul));

        rs.add(new Result(monatsbericht, monatsberichtTag));

        rs.add(new Result(mobilePass, mobilepass));

        rs.add(new Result(cardBlocked, blockieren));
        rs.add(new Result(cardBlocked, entblocken));

        rs.add(new Result(outlookAusserhaus, ausserhaus));

        rs.add(new Result(ill, krank));

        rs.add(new Result(kudos, kudosTag));

        rs.add(new Result(dateien, datei));

        rs.add(new Result(maxStunde, stunde));

        rs.add(new Result(arztZeugnis, arztzeugnis));

        rs.add(new Result(time2learn, time2learnTag));

        rs.add(new Result(ausbildungsplan, modul));
        rs.add(new Result(ausbildungsplan, ausbildungsplanTag));

        rs.add(new Result(installation, installationTag));
        rs.add(new Result(installation, programm));

        rs.add(new Result(ferienStueck, blockLeave));
        rs.add(new Result(ferienStueck, stueck));

        rs.add(new Result(mailVerschluesseln, verschluesseln));

        rs.add(new Result(wetterA, wetter));
        rs.add(new Result(wetterA, temp));

        return rs;
    }

    /**
     * @return all words that should be blacklisted, read from a file (every line one word)
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.202
     */
    public ArrayList<BlacklistEntry> getBlackList() {
        ArrayList<BlacklistEntry> list = new ArrayList<>();
        try {
            ArrayList<String> words = readFileLines("./data/badwords.txt");
            for (String s : words) {
                list.add(new BlacklistEntry(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * @return all set user logins
     * @author Marc
     * @author Magnus
     * @since 17.07.202
     */
    public List<UserLogin> getUserLogins() {
        ArrayList<UserLogin> out = new ArrayList<>();
        out.add(new UserLogin("Admin", "admin", true));
        out.add(new UserLogin("lea.hellbach@ubs.com", "root", true));
        out.add(new UserLogin("tim.irmler@ubs.com", "root", true));
        out.add(new UserLogin("marc-ZA.fuchs@ubs.com", "root", true));
        out.add(new UserLogin("magnus.goetz@ubs.com", "root", true));
        out.add(new UserLogin("sarah.ambi@ubs.com", "root", true));
        out.add(new UserLogin("david.middelmann@ubs.com", "root", true));
        out.add(new UserLogin("patrick-ze.mueller@ubs.com", "root", true));

        return out;
    }

    /**
     * @return all set default questions
     * @author Sarah
     * @since 17.07.202
     */
    public List<DefaultQuestion> getDefaultQuestions() {
        ArrayList<DefaultQuestion> out = new ArrayList<>();
        out.add(new DefaultQuestion("Wo kann ich meine Arbeitszeit eintragen?"));
        out.add(new DefaultQuestion("Wo kann ich meine Ferien eintragen?"));
        out.add(new DefaultQuestion("Wo kann ich mich krank melden?"));

        return out;
    }


    /**
     * @param filepath the path to the file
     * @return a list of words, every line in the file is one entry in the list
     * @throws FileNotFoundException if the file cannot be found
     * @throws IOException           if something went wrong
     */
    private ArrayList<String> readFileLines(String filepath) throws FileNotFoundException, IOException {
        File fp = new File(filepath);
        FileReader fr = new FileReader(fp);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(prepareString(line, 255, true, false));
        }

        fr.close();
        return lines;
    }
}

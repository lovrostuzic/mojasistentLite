package com.mojasistent;

import com.mojasistent.model.Record;
import com.mojasistent.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class PatientController {

    @Autowired
    PatientRepository patientRepository;
    @Autowired
    RecordRepository recordRepository;
    Patient currPatient;


    @Autowired
    MedicineRepository medicineRepository;
    @Autowired
    private ReminderRecordRepository reminderRecordRepository;

    @GetMapping("/patient-dashboard")
    public String showDashboard(Model model, Long id) {
        currPatient = patientRepository.findById(id).get();
        List<Record> recordsList = recordRepository.findByPatient(currPatient);
        model.addAttribute("currPatient", currPatient);
        model.addAttribute("recordsList", recordsList);
        return "patient-dashboard.html";
    }

    //forma za emoji osjecaja
    @GetMapping("/form-1")
    public String showForm1(Model model, Long id, HttpSession session) {
        currPatient = patientRepository.findById(id).get();
        model.addAttribute("currPatient", currPatient);
        return "form-1.html";
    }

    @GetMapping("/form-01")
    public String showForm01(Model model, HttpSession session) {
        Patient currPatient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", currPatient);
        Record record = new Record();
        record.setPatient(currPatient);
        session.setAttribute("record", record);
        return "form-01.html";
    }

    //forma za razinu boli
    @GetMapping("/form-2")
    public String showForm2(Model model, int happinessLevel, HttpSession session) {
        Patient currPatient = (Patient) session.getAttribute("loggedPatient");
        Record record = new Record();
        record.setPatient(currPatient);
        if (record.getHappinessLevel() == -1) {
            if (happinessLevel < 20) {
                record.setHappinessLevel(1);
            } else if (happinessLevel < 40) {
                record.setHappinessLevel(2);
            } else if (happinessLevel < 60) {
                record.setHappinessLevel(3);
            } else if (happinessLevel < 80) {
                record.setHappinessLevel(4);
            } else {
                record.setHappinessLevel(5);
            }
        }

        record.setDate(new Date());
        model.addAttribute("currPatient", record.getPatient());
        return "form-2.html";
    }

    //forma za unos teksta ako pacijenti zele, neobavezno, ovdje se nalazi form koji submite na server

    @GetMapping("/form-21")
    public String showForm21(Model model, HttpSession session) {
        Record record = (Record) session.getAttribute("record");
        model.addAttribute("currPatient", record.getPatient());
        return "form-21.html";
    }

    @GetMapping("/form-22")
    public String showForm22(Model model, HttpSession session) {
        Record record = (Record) session.getAttribute("record");
        model.addAttribute("currPatient", record.getPatient());
        return "form-22.html";
    }

    @GetMapping("/form-23")
    public String showForm23(Model model, HttpSession session) {
        Record record = (Record) session.getAttribute("record");
        model.addAttribute("currPatient", record.getPatient());
        return "form-23.html";
    }

    @GetMapping("/form-3")
    public String showForm3(Model model, HttpSession session) {
        Record record = (Record) session.getAttribute("record");
        Optional<Integer> painLevelOptional = Optional.ofNullable((Integer) session.getAttribute("painLevel"));
        int painLevel = painLevelOptional.orElse(0);
        model.addAttribute("currPatient", record.getPatient());
        record.setPainLevel(painLevel);
        return "form-3.html";
    }

    @GetMapping("/form-end")
    public String formEnd(Model model, String text, @RequestParam Map<String, String> allParams, HttpSession session) {
        String newText = text + "\n";
        Record record = (Record) session.getAttribute("record");


        newText += "\n\n/\n";
        if (allParams.get("\uD83D\uDC6A Podrška obitelji") != null)
            newText += "[Podrška obitelji]\n";
        if (allParams.get("\uD83D\uDC69\u200D⚕️Podrška medicinskog tima") != null)
            newText += "[Podrška medicinskog tima]\n";
        if (allParams.get("\uD83D\uDD6F️ Duhovna podrška") != null)
            newText += "[Duhovna podrška]\n";
        if (allParams.get("\uD83D\uDC69\uD83C\uDFFB\u200D⚕ Psihološka podrška") != null)
            newText += "[Psihološka podrška]\n";
        if (allParams.get("\uD83D\uDC8A Lijek") != null)
            newText += "[Lijek]\n";
        record.setText(newText);



        Patient currPatient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", currPatient);

        return "activity_input_form.html";
    }

    @GetMapping("/showLegend")
    public String showLegend(Model model, HttpSession session, int ref) {
        Record record = (Record) session.getAttribute("record");
        model.addAttribute("record", record);
        model.addAttribute("ref", ref);
        return "legenda.html";
    }

    @PostMapping("/saveCauseofPain")
    public String obradiPodatke(@RequestParam(name = "physicalPain", required = false) String physicalPain,
                                @RequestParam(name = "mentalPain", required = false) String mentalPain,
                                @RequestParam(name = "socialPain", required = false) String socialPain,
                                @RequestParam(name = "spiritualPain", required = false) String spiritualPain,
                                @RequestParam(name = "anotherCauseOfPain", required = false) String anotherCauseOfPain,
                                HttpSession session) {
        List<String> nonNullValues = new ArrayList<>();
        if (physicalPain != null) nonNullValues.add(physicalPain);
        if (mentalPain != null) nonNullValues.add(mentalPain);
        if (socialPain != null) nonNullValues.add(socialPain);
        if (spiritualPain != null) nonNullValues.add(spiritualPain);
        if (anotherCauseOfPain != null) nonNullValues.add(anotherCauseOfPain);

        // Kombiniranje vrijednosti u niz odvojen zarezima
        String causeofPain = String.join(", ", nonNullValues);

        Record record = (Record) session.getAttribute("record");
        record.setCauseofPain(causeofPain);
        return "redirect:/form-3";
    }

    @GetMapping("/toCauseOfPain")
    public String toCauseOfPain(HttpSession session, int painLevel, Model model) {
        session.setAttribute("painLevel", painLevel);
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        return "causeOfPain.html";
    }

    @GetMapping("/activity-input-form")
    public String showActivityInputForm(Model model, HttpSession session) {
        Patient currPatient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", currPatient);
        Record record = new Record();
        record.setPatient(currPatient);
        session.setAttribute("activity_record", record);
        return "activity_input_form.html";
    }


    @GetMapping("/end-form")
    public String endForm(Model model, @RequestParam(name = "text1", required = false) String activity, @RequestParam(name = "text", required = false) String newActivity
            , HttpSession session) {
        List<String> nonNullValues = new ArrayList<>();
        if (activity != null) nonNullValues.add(activity);
        if (newActivity != null) nonNullValues.add(newActivity);
        String setActivity = String.join(", ", nonNullValues);
        Patient patient = (Patient) session.getAttribute("loggedPatient");


        Record record = (Record) session.getAttribute("record");
        record.setDate(new Date(System.currentTimeMillis() + 3600 * 1000));
        record.setActivity(setActivity);
        recordRepository.save(record);
                model.addAttribute("currPatient", patient);
        return "form-endgreet.html";

    }

    @GetMapping("/add_new_medication")
    public String addNewMedication(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        return "add_new_medication.html";
    }

    @GetMapping("/reminderTimeSet")
    private String reminderTimeSet(HttpSession session, Model model,
                                   @RequestParam("mTime") String mTimeString,
                                   @RequestParam("aTime") String aTimeString,
                                   @RequestParam("eTime") String eTimeString) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");


        LocalTime mLocalTime = LocalTime.parse(mTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime aLocalTime = LocalTime.parse(aTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime eLocalTime = LocalTime.parse(eTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        mLocalTime = mLocalTime.minusHours(1);
        aLocalTime = aLocalTime.minusHours(1);
        eLocalTime = eLocalTime.minusHours(1);


        // Ažuriranje vremena u bazi podataka
        patientRepository.updateMorningReminderTimeAndNoonReminderTimeAndEveningReminderTimeById(mLocalTime, aLocalTime, eLocalTime, patient.getId());

        return "redirect:/form-01";
    }
    @GetMapping("/toReminderTimeSet")
    private String toReminderTimeSet(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");

        LocalTime mTime = patient.getMorningReminderTime() != null ? (patient.getMorningReminderTime()) : LocalTime.of(0, 0);
        LocalTime aTime = patient.getNoonReminderTime() != null ? (patient.getNoonReminderTime()) : LocalTime.of(0, 0);
        LocalTime eTime = patient.getEveningReminderTime() != null ?(patient.getEveningReminderTime()) : LocalTime.of(0, 0);

        // Podešavanje vremena ako je potrebno
        mTime = mTime.plusHours(1);
        aTime = aTime.plusHours(1);
        eTime = eTime.plusHours(1);

        String morningTime = mTime.toString().substring(0, 5); // Format: "HH:mm"
        String afternoonTime = aTime.toString().substring(0, 5);
        String eveningTime = eTime.toString().substring(0, 5);

        model.addAttribute("currPatient", patient);
        model.addAttribute("morningTime", morningTime);
        model.addAttribute("afternoonTime", afternoonTime);
        model.addAttribute("eveningTime", eveningTime);

        return "reminderPost.html";
    }

    @GetMapping("/toUpute")
    public String upute(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        return "upute.html";
    }


    @GetMapping("/overview-of-activities")
    public String overviewofactivities(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        List<Record> recordList = recordRepository.findByPatient(patient);
        model.addAttribute("recordList", recordList);
        return "overview_of_activities.html";
    }
    @GetMapping("/toIzbornik")
    public String toIzbornik(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        List<Record> recordList = recordRepository.findByPatient(patient);
        model.addAttribute("recordList", recordList);
        return "izbornik.html";
    }

    @GetMapping("/pp")
    public String pp(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        String p = "pp";
        List<Medicine> medicineList = medicineRepository.findByPatientAndTakingTime(patient,p);
        model.addAttribute("medicineList", medicineList);
        return "pp.html";
    }


    @GetMapping("/ppSave")
    public String ppSave(Model model, @RequestParam(name = "text1", required = false) String medicine1, @RequestParam(name = "text", required = false) String medicine2
            , HttpSession session) {
        List<String> nonNullValues = new ArrayList<>();
        if (medicine1 != null) nonNullValues.add(medicine1);
        if (medicine2 != null) nonNullValues.add(medicine2);
        String setPp = String.join(", ", nonNullValues);
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        model.addAttribute("currPatient", patient);
        LocalDateTime noW = LocalDateTime.now();
        String p = "pp";
        ReminderRecord reminderRecord = new ReminderRecord(setPp, noW.plusHours(1),p, patient);
        reminderRecordRepository.save(reminderRecord);
        return "form-endgreet.html";

    }
    @GetMapping("/deleteMedicine")
    public String deleteMedicinee(Model model, HttpSession session, @RequestParam("id") long id) {
        medicineRepository.deleteById(id);
        return "redirect:/overview_of_medications";
    }

}



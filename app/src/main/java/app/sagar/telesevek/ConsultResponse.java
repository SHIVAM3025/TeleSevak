package app.sagar.telesevek;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ConsultResponse {
    private String Block;
    private String ConsultationId;
    private String DateTime;
    private String DoctorId;
    private String PName;
    private String PatientId;
    private String PatientPhone;
    private String PatientCard;
    private String ItemId;
    private String Status;
    private String Time;
    private String DoctorName;
    private String Age;
    private String Gender;
    private String Symtoms;

    public ConsultResponse(String Block, String ConsultationId, String DateTime, String DoctorId, String PName, String PatientId, String PatientCard, String PatientPhone, String Status, String ItemId, String Time, String DoctorName, String Age, String Gender, String Symtoms) {
        this.Block = Block;
        this.ConsultationId = ConsultationId;
        this.DateTime = DateTime;
        this.DoctorId = DoctorId;
        this.PName = PName;
        this.PatientId = PatientId;
        this.PatientPhone = PatientPhone;
        this.PatientCard = PatientCard;
        this.Status = Status;
        this.Time = Time;
        this.DoctorName = DoctorName;
        this.Age = Age;
        this.Gender = Gender;
        this.Symtoms = Symtoms;
        this.ItemId= ItemId;


    }



    public ConsultResponse() {
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getConsultationId() {
        return ConsultationId;
    }

    public void setConsultationId(String consultationId) {
        ConsultationId = consultationId;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getDoctorId() {
        return DoctorId;
    }

    public void setDoctorId(String doctorId) {
        DoctorId = doctorId;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public String getPatientPhone() {
        return PatientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        PatientPhone = patientPhone;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
    public String getDoctorName() {
        return DoctorName;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getSymtoms() {
        return Symtoms;
    }

    public void setSymtoms(String symtoms) {
        Symtoms = symtoms;
    }

    public String getPatientCard() {
        return PatientCard;
    }

    public void setPatientCard(String patientCard) {
        PatientCard = patientCard;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

}

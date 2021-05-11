package app.telesevek;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DoctorResponse {
    private String DateOfOnboarding;
    private String District;
    private String DoctorId;
    private String FullAddress;
    private String IsActive;
    private String Name;
    private String PhoneNumber;
    private String TimeSlotAvaiability;
    private String degree;
    private String exp;
    private String url;
    private String TypeOfDoctor;

    public String getTimeSlotAvaiability() {
        return TimeSlotAvaiability;
    }

    public void setTimeSlotAvaiability(String timeSlotAvaiability) {
        TimeSlotAvaiability = timeSlotAvaiability;
    }

    public DoctorResponse(String DateOfOnboarding, String District, String DoctorId, String FullAddress, String IsActive, String Name, String PhoneNumber, String TimeSlotAvaiability, String degree, String exp, String url, String TypeOfDoctor) {
        this.DateOfOnboarding = DateOfOnboarding;
        this.District = District;
        this.DoctorId = DoctorId;
        this.FullAddress = FullAddress;
        this.IsActive = IsActive;
        this.Name = Name;
        this.PhoneNumber = PhoneNumber;
        this.TimeSlotAvaiability = TimeSlotAvaiability;
        this.degree = degree;
        this.exp = exp;
        this.url = url;
        this.TypeOfDoctor = TypeOfDoctor;
    }




    public String getDateOfOnboarding() {
        return DateOfOnboarding;
    }

    public void setDateOfOnboarding(String dateOfOnboarding) {
        DateOfOnboarding = dateOfOnboarding;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getDoctorId() {
        return DoctorId;
    }

    public void setDoctorId(String doctorId) {
        DoctorId = doctorId;
    }

    public String getFullAddress() {
        return FullAddress;
    }

    public void setFullAddress(String fullAddress) {
        FullAddress = fullAddress;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getTimeSlotAvaiabilit() {
        return TimeSlotAvaiability;
    }

    public void setTimeSlotAvaiabilit(String timeSlotAvaiabilit) {
        TimeSlotAvaiability = timeSlotAvaiabilit;
    }
    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTypeOfDoctor() {
        return TypeOfDoctor;
    }

    public void setTypeOfDoctor(String typeOfDoctor) {
        TypeOfDoctor = typeOfDoctor;
    }


    public DoctorResponse() {
    }


}

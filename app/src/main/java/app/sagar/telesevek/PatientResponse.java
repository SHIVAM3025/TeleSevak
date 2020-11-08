package app.sagar.telesevek;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.sql.Time;

@IgnoreExtraProperties
public class PatientResponse {
    private String Age;
    private String Card;
    private String DateTime;
    private String DateTime2;
    private String DateTime3;
    private String Gender;
    private String Name;
    private String PhoneNumber;
    private String Symptoms;
    private String ItemId;
    private String Time;

    public PatientResponse() {
    }

    public PatientResponse(String Age, String Card, String DateTime, String DateTime2, String DateTime3, String Gender, String Name, String PhoneNumber, String Symptoms, String ItemId, String Time) {
        this.Age = Age;
        this.Card = Card;
        this.DateTime = DateTime;
        this.DateTime2 = DateTime2;
        this.DateTime3 = DateTime3;
        this.Gender = Gender;
        this.Name = Name;
        this.PhoneNumber = PhoneNumber;
        this.Symptoms = Symptoms;
        this.ItemId = ItemId;
        this.Time = Time;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getCard() {
        return Card;
    }

    public void setCard(String card) {
        Card = card;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getDateTime2() {
        return DateTime2;
    }

    public void setDateTime2(String dateTime2) {
        DateTime2 = dateTime2;
    }

    public String getDateTime3() {
        return DateTime3;
    }

    public void setDateTime3(String dateTime3) {
        DateTime3 = dateTime3;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
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

    public String getSymptoms() {
        return Symptoms;
    }

    public void setSymptoms(String symptoms) {
        Symptoms = symptoms;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

}

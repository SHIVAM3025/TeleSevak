package app.telesevek;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChemistResponse {
    private String BlockName;
    private String ChemistId;
    private String ChemistName;
    private String ChemistPhoneNumber;
    private String DateOfOnboarding;
    private String FullAddress;


    public ChemistResponse(String BlockName, String ChemistId, String ChemistName, String ChemistPhoneNumber, String DateOfOnboarding, String FullAddress) {
        this.BlockName = BlockName;
        this.ChemistId = ChemistId;
        this.ChemistName = ChemistName;
        this.FullAddress = FullAddress;
        this.ChemistPhoneNumber = ChemistPhoneNumber;
        this.DateOfOnboarding = DateOfOnboarding;
        this.FullAddress = FullAddress;
    }


    public String getBlockName() {
        return BlockName;
    }

    public void setBlockName(String blockName) {
        BlockName = blockName;
    }

    public String getChemistId() {
        return ChemistId;
    }

    public void setChemistId(String chemistId) {
        ChemistId = chemistId;
    }

    public String getChemistName() {
        return ChemistName;
    }

    public void setChemistName(String chemistName) {
        ChemistName = chemistName;
    }

    public String getChemistPhoneNumber() {
        return ChemistPhoneNumber;
    }

    public void setChemistPhoneNumber(String chemistPhoneNumber) {
        ChemistPhoneNumber = chemistPhoneNumber;
    }

    public String getDateOfOnboarding() {
        return DateOfOnboarding;
    }

    public void setDateOfOnboarding(String dateOfOnboarding) {
        DateOfOnboarding = dateOfOnboarding;
    }

    public String getFullAddress() {
        return FullAddress;
    }

    public void setFullAddress(String fullAddress) {
        FullAddress = fullAddress;
    }

    public ChemistResponse() {
    }
}

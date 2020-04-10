
package com.shufti.shuftipro.shuftipro_demo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.shufti.shuftipro.shuftipro_demo.R;
import com.shutipro.sdk.Shuftipro;
import com.shutipro.sdk.listeners.ShuftiVerifyListener;
import com.shutipro.sdk.models.AddressVerification;
import com.shutipro.sdk.models.ConsentVerification;
import com.shutipro.sdk.models.DocumentVerification;
import com.shutipro.sdk.models.FaceVerification;
import com.shutipro.sdk.models.ShuftiproVerification;
import com.shutipro.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout faceRelativeLayout;
    private boolean isFaceChecked = false, isDocChecked = false, isAddressChecked = false, isConsentChecked = false;
    private boolean isToVerifyName = false;
    private ImageView faceCheckImageView;
    private EditText firstNameEditText, lastNameEditText, dobEditText, docNumberEditText, issueDateEditText, expiryDateEditText, addressEditText, consentEditText;
    private Button continueButton;
    private String firstName, lastName, dob, documentNumber, issueDate, expiryDate, fullAddress, consentText;

    private String clientId = ""; //Set your client Id here
    private String secretKey = ""; //Set your secret key here.
    private String accessToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Initializing views
        faceRelativeLayout = findViewById(R.id.faceRelativeLayout);
        faceCheckImageView = findViewById(R.id.faceCheckImageView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        docNumberEditText = findViewById(R.id.docNumberEditText);
        issueDateEditText = findViewById(R.id.issueDateEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        addressEditText = findViewById(R.id.addressEditText);
        continueButton = findViewById(R.id.continueButton);
        consentEditText = findViewById(R.id.consentEditText);

        //Setting click listeners for the layouts
        faceRelativeLayout.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        // optional
        accessToken = this.getAccessToken();
    }

    @Override
    public void onClick(View v) {

        if (v == faceRelativeLayout) {
            if (!isFaceChecked) {
                isFaceChecked = true;
                faceCheckImageView.setImageResource(R.drawable.check_radio_icon);
            } else {
                isFaceChecked = false;
                faceCheckImageView.setImageResource(R.drawable.uncheck_radio_icon);
            }
        }

        if (v == continueButton) {

            firstName = firstNameEditText.getText().toString();
            lastName = lastNameEditText.getText().toString();
            dob = dobEditText.getText().toString();
            documentNumber = docNumberEditText.getText().toString();
            issueDate = issueDateEditText.getText().toString();
            expiryDate = expiryDateEditText.getText().toString();
            fullAddress = addressEditText.getText().toString();
            consentText = consentEditText.getText().toString();

            //If none of verification is requested display alert message
            if (!isFaceChecked && firstName.isEmpty() && lastName.isEmpty() && dob.isEmpty() && documentNumber.isEmpty()
                    && issueDate.isEmpty() && expiryDate.isEmpty() && fullAddress.isEmpty() && consentText.isEmpty()) {
                showErrorMessageDialog(getString(R.string.methods_of_verifications));
                return;
            }

            //Check if user has check for document verification
            if (!firstName.isEmpty() || !lastName.isEmpty() || !dob.isEmpty() || !documentNumber.isEmpty() ||
                    !issueDate.isEmpty() || !expiryDate.isEmpty()) {

                isDocChecked = true;
                if (!firstName.isEmpty()) {
                    isToVerifyName = true;
                }
            } else {
                isDocChecked = false;
            }

            //Check if user has check for document verification
            isAddressChecked = !fullAddress.isEmpty();

            //Check if user has check for consent verification
            isConsentChecked = !consentText.isEmpty();
            requestSDKForVerification();
        }
    }

    private void requestSDKForVerification() {
        if (accessToken.isEmpty() && (clientId.isEmpty() || secretKey.isEmpty())) {
            showErrorMessageDialog(getString(R.string.provide_credentials));
            return;
        }
        sendVerificationRequest();
    }

    private void sendVerificationRequest() {

        final String country = "GB";
        final String lng = "EN";
        final String email = "yourmail@gmail.com";
        final String callback_url = "https://www.yourdomain.com";
        final String redirect_url = "https://www.yourdomain.com";
        final String verification_mode = "video";

        //Get unique reference using SDK utils (You can use your own reference)
        final String reference = Utils.getUniqueReference(this);

        /*
         * FOR FACE VERIFICATION SERVICE
         * Make an instance and set the face verification to true
         */

        FaceVerification faceVerification = FaceVerification.getInstance();
        faceVerification.setFaceVerificationService(true);

        /*
         * FOR DOCUMENTATION VERIFICATION SERVICE
         * Make an instance and set the supported types & required fields for verification
         */

        DocumentVerification documentVerification = DocumentVerification.getInstance();
        ArrayList<String> doc_supported_types = new ArrayList<>();
        doc_supported_types.add("id_card");
        doc_supported_types.add("credit_or_debit_card");
        doc_supported_types.add("passport");
        doc_supported_types.add("driving_license");

        documentVerification.setSupportedTypes(doc_supported_types);
        documentVerification.setFirstName(firstName);
        documentVerification.setMiddleName("");
        documentVerification.setLastName(lastName);
        documentVerification.setDob(dob);
        documentVerification.setDocumentNumber(documentNumber);
        documentVerification.setIssueDate(issueDate);
        documentVerification.setExpiryDate(expiryDate);
    //  documentVerification.setFetchEnhancedData(true);

        /*
         * FOR ADDRESS VERIFICATION SERVICE
         * Make an instance, set the supported types & required fields for verification
         */

        AddressVerification addressVerification = AddressVerification.getInstance();
        ArrayList<String> supported_types = new ArrayList<>();
        supported_types.add("id_card");
        supported_types.add("passport");
        supported_types.add("bank_statement");
        supported_types.add("utility_bill");

        addressVerification.setSupportedTypes(supported_types);
        addressVerification.setFullAddress(fullAddress);
        addressVerification.setFirstName(firstName);
        addressVerification.setMiddleName("");
        addressVerification.setLastName(lastName);
        addressVerification.setFuzzyMatch(false);

        /*
         * FOR CONSENT VERIFICATION SERVICE
         * Make an instance, set the supported types & required fields for verification
         */

        ConsentVerification consentVerification = ConsentVerification.getInstance();
        ArrayList<String> consent_supported_types = new ArrayList<>();
        consent_supported_types.add("handwritten");
        consent_supported_types.add("printed");

        consentVerification.setSupportedTypes(consent_supported_types);
        consentVerification.setConsentText(consentText);

        //Make an instance and method call
        Shuftipro instance;

        if (clientId.isEmpty() || secretKey.isEmpty()) {
            instance = Shuftipro.getInstance(accessToken, false);
        } else {
            instance = Shuftipro.getInstance(clientId, secretKey, false);
        }


        ShuftiproVerification.RequestBuilder requestBuilder = new ShuftiproVerification.RequestBuilder(reference, country, callback_url,
                this, verification_mode, new ShuftiVerifyListener() {
            @Override
            public void verificationStatus(HashMap<String, String> responseSet) {
                Log.e("Response", responseSet.toString());
                uncheckAllOptions();
            }
        });

        requestBuilder.withFaceVerification(isFaceChecked ? faceVerification : null);
        requestBuilder.withAddressVerification(isAddressChecked ? addressVerification : null);
        requestBuilder.withDocumentVerification(isDocChecked ? documentVerification : null);
        requestBuilder.withConsentVerification(isConsentChecked ? consentVerification : null);
        requestBuilder.withEmail(email);
        requestBuilder.withLanguage(lng);
        requestBuilder.withRedirectUrl(redirect_url);
        instance.shuftiproVerification(requestBuilder.buildShuftiModel());
    }

    //Deselect all of the pre selected values
    private void uncheckAllOptions() {
        isFaceChecked = false;
        isDocChecked = false;
        isAddressChecked = false;
        isToVerifyName = false;
        faceCheckImageView.setImageResource(R.drawable.uncheck_radio_icon);
        resetAllFields();
    }

    //Display an alert dialog to show the error messages
    public void showErrorMessageDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                uncheckAllOptions();
                dialogInterface.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void resetAllFields() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        dobEditText.setText("");
        docNumberEditText.setText("");
        issueDateEditText.setText("");
        expiryDateEditText.setText("");
        addressEditText.setText("");
        consentEditText.setText("");
    }



    /**
     *
     * @Optional | clientId + secretKey can also be used instead accessToken
     *
     * return access token
     */
    private String getAccessToken() {
        String accessToken = "fjdabf";

        // implement logic to get accessToken from server side


        return accessToken;
    }
}

package com.example.festivalapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private String sysSprache;
    private String error;
    private char spracheBekannt;
    private TextView loginTitle;
    private TextView loginReg;
    private TextView loginError;
    private TextInputLayout loginTitlePW;
    private TextInputLayout loginTitleMail;
    private EditText etLoginMail;
    private EditText etLoginPW;
    private Button loginOk;
    private Button loginEnd;
    private Translator trans;
    private boolean pwOK;
    private DownloadConditions downCond;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTitle = findViewById(R.id.tVLoginTitle);
        loginReg = findViewById(R.id.tvLoginReg);
        loginError = findViewById(R.id.tvLoginError);
        loginTitleMail = findViewById(R.id.titleLoginMail);
        loginTitlePW = findViewById(R.id.titleLoginPW);
        loginOk = findViewById(R.id.btnLoginOk);
        loginEnd = findViewById(R.id.btnLoginEnd);
        etLoginMail = findViewById(R.id.etLoginMail);
        etLoginPW = findViewById(R.id.etLoginPassword);


        sysSprache = Locale.getDefault().getLanguage();

        String email = "E-Mail-Adresse";
        String passwort = "Passwort";
        String ok = "Login";
        String nok = "Beenden";
        String reg = "Registrieren";
        String titel = "Willkommen";
        error = "E-Mail oder Passwort stimmen nicht";

        if (sysSprache.equals("de")) {
            loginTitle.setText(titel);
            loginTitleMail.setHint(email);
            loginTitlePW.setHint(passwort);
            loginOk.setText(ok);
            loginEnd.setText(nok);
            loginReg.setText(reg);
        } else{
            loadSprache();
            //Log.d(TAG, "Sprache bekannt: " + spracheBekannt);
            if (spracheBekannt == 'N'){
                sysSprache = "en";
                AlertDialog.Builder errorSpracheBuild = new AlertDialog.Builder(this);
                errorSpracheBuild.setMessage("Sprache wird nicht unterstütz. Es wird Englisch verwendet.");
                errorSpracheBuild.setCancelable(true);
                errorSpracheBuild.setNeutralButton("OK", (dialog, which) -> dialog.cancel());

                AlertDialog errorSprache = errorSpracheBuild.create();
                errorSprache.show();
            }
            TranslatorOptions transOption = new TranslatorOptions.Builder()
                    .setSourceLanguage("de")
                    .setTargetLanguage(sysSprache)
                    .build();
            trans = Translation.getClient(transOption);
             downCond = new DownloadConditions.Builder()
                    .requireWifi().build();

            uebersetzung("Titel", downCond, titel);
            uebersetzung("Mail", downCond, email);
            uebersetzung("PW", downCond, passwort);
            uebersetzung("OK", downCond, ok);
            uebersetzung("NOK", downCond, nok);
            uebersetzung("Reg", downCond, reg);

        }
        etLoginMail.setOnClickListener(view -> {
            String testError = loginError.getText().toString();
            if(!testError.isEmpty()){
                loginError.setText("");
            }
        });
        etLoginPW.setOnClickListener(view -> {
            String testPW = loginError.getText().toString();
            if(!testPW.isEmpty()){
                loginError.setText("");
            }
        });
        loginEnd.setOnClickListener(view -> finish());
        loginReg.setOnClickListener(view -> {
            Intent regAct = new Intent(LoginActivity.this, RegisActivity.class);
            regAct.putExtra("sprache", sysSprache);
            LoginActivity.this.startActivity(regAct);
        });
        loginOk.setOnClickListener(view -> {
            //checkOk = true;
            String loginMail = etLoginMail.getText().toString().trim();
            String loginPW = etLoginPW.getText().toString();

            boolean mailOK = true;
            pwOK = true;

            if(loginMail.isEmpty()){
                error = "Bitte geben Sie eine gültige E-Mail-Adresse ein";
                if(sysSprache.equals("DE")){
                    loginError.setText(error);
                }else{
                    uebersetzung("Error", downCond, error);
                }

                mailOK = false;
            }
            else{
                mailOK = checkMail(loginMail);
                if(!mailOK){
                    error = "Bitte geben Sie eine gültige E-Mail-Adresse ein";
                    if(sysSprache.equals("DE")){
                        loginError.setText(error);
                    }else{
                        uebersetzung("Error", downCond, error);
                    }
                }
            }

            if (mailOK && loginPW.isEmpty()){
                pwOK = false;
                error = "Bitte geben Sie das Passwort ein";
                if(sysSprache.equals("DE")){
                    loginError.setText(error);
                }else{
                    uebersetzung("Error", downCond, error);
                }
            }else{
                FirebaseAuth user = FirebaseAuth.getInstance();
                user.signInWithEmailAndPassword(loginMail, loginPW).addOnCompleteListener(this, task -> {
                    if(!task.isSuccessful()){
                        pwOK = false;
                        error = "E-Mail-Adresse oder Passwort stimmen nicht.";
                        if(sysSprache.equals("DE")){
                            loginError.setText(error);
                        }else{
                            uebersetzung("Error", downCond, error);
                        }
                    }
                });

            }

            if (mailOK && pwOK){
                Intent logIn = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(logIn);
            }

        });
    }

    private void uebersetzung(String typ, DownloadConditions downloadConditions, String quellText) {
        trans.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Verbindung hergestellt");
                    trans.translate(quellText)
                            .addOnSuccessListener(s -> {
                                Log.d(TAG, "Übersetzung erfolgreich: " + s);
                                switch (typ){
                                    case "Titel"    -> loginTitle.setText(s);
                                    case "Mail"     -> loginTitleMail.setHint(s);
                                    case "PW"       -> loginTitlePW.setHint(s);
                                    case "OK"       -> loginOk.setText(s);
                                    case "NOK"      -> loginEnd.setText(s);
                                    case "Reg"      -> loginReg.setText(s);
                                    case "Error"    -> loginError.setText(s);
                                    default         -> Log.d(TAG, "Falscher Typ: " + typ);
                                }
                            })
                            .addOnFailureListener(e -> Log.d(TAG, "Fehler bei Übersetzung: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fehler bei Verbindung: " + e.getMessage()));
    }

    private void loadSprache() {
        //Log.d(TAG, "Methode loadSprache");
        spracheBekannt = 'N';

        List<String> ListeSprachen = TranslateLanguage.getAllLanguages();
        //Log.d(TAG, "Arraylänge: " +ListeSprachen.size());
        int i = 0;
        while (i < ListeSprachen.size() && spracheBekannt != 'J') {
            //Log.d(TAG, "Stelle: " + i + " Sprache: " + ListeSprachen.get(i).toString());
            if (ListeSprachen.get(i).equals(sysSprache)){
                //Log.d(TAG, "Sprache gefunden");
                spracheBekannt = 'J';
            }
            i++;
        }
    }

    private boolean checkMail(String email) {
        if(email == null){
            return false;
        }else{
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
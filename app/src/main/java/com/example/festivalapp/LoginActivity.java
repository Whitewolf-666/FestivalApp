package com.example.festivalapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
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
    private char spracheBekannt;
    private TextView loginTitle;
    private TextView loginReg;
    private TextInputLayout loginTilMail;
    private TextInputLayout loginTilPw;
    private Button loginOk;
    private Button loginFalse;
    private Translator trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTitle = findViewById(R.id.tVLoginTitle);
        loginTilMail = findViewById(R.id.tilLoginMail);
        loginTilPw = findViewById(R.id.tilLoginPw);
        //EditText loginMail = findViewById(R.id.edLoginMail);
        //EditText loginPW = findViewById(R.id.etLoginPassword);
        loginOk = findViewById(R.id.btnLoginOk);
        loginFalse = findViewById(R.id.btnLoginFalse);
        loginReg = findViewById(R.id.tvLoginReg);

        sysSprache = Locale.getDefault().getLanguage();

        String email = "E-Mail-Adresse";
        String passwort = "Passwort";
        String ok = "Login";
        String nok = "Beenden";
        String reg = "Registrieren";
        String titel = "Willkommen";
        if (sysSprache.equals("de")) {
            loginTitle.setText(titel);
            loginTilMail.setHint(email);
            loginTilPw.setHint(passwort);
            loginOk.setText(ok);
            loginFalse.setText(nok);
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
            DownloadConditions downCond = new DownloadConditions.Builder()
                    .requireWifi().build();

            uebersetzung("Titel", downCond, titel);
            uebersetzung("Mail", downCond, email);
            uebersetzung("PW", downCond, passwort);
            uebersetzung("OK", downCond, ok);
            uebersetzung("NOK", downCond, nok);
            uebersetzung("Reg", downCond, reg);

        }
        loginFalse.setOnClickListener(view -> finish());
        loginReg.setOnClickListener(view -> {
            Intent regAct = new Intent(LoginActivity.this, RegisActivity.class);
            regAct.putExtra("sprache", sysSprache);
            LoginActivity.this.startActivity(regAct);
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
                                    case "Mail"     -> loginTilMail.setHint(s);
                                    case "PW"       -> loginTilPw.setHint(s);
                                    case "OK"       -> loginOk.setText(s);
                                    case "NOK"      -> loginFalse.setText(s);
                                    case "Reg"      -> loginReg.setText(s);
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
}
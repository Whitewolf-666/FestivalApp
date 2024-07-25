package com.example.festivalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView logintvPW;
    private TextView logintvMail;
    private TextView loginReg;
    private EditText loginMail;
    private EditText loginPW;
    private Button loginOk;
    private Button loginFalse;
    private String email = "E-Mail-Adresse";
    private String passwort = "Passwort";
    private String ok = "Login";
    private String nok = "Beenden";
    private String reg = "Registrieren";
    private String fehler;
    private String titel = "Willkommen";
    private Translator trans;
    private TranslatorOptions transOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTitle = findViewById(R.id.tVLoginTitle);
        logintvMail = findViewById(R.id.tvLoginMail);
        logintvPW = findViewById(R.id.tvLoginMail);
        loginMail = findViewById(R.id.etLoginMail);
        loginPW = findViewById(R.id.etLoginPassword);
        loginOk = findViewById(R.id.btnLoginOk);
        loginFalse = findViewById(R.id.btnLoginFalse);
        loginReg = findViewById(R.id.tvLoginReg);

        sysSprache = Locale.getDefault().getLanguage();

        if (sysSprache.equals("de")) {
            loginTitle.setText(titel);
            logintvMail.setText(email);
            logintvPW.setText(passwort);
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
                errorSpracheBuild.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog errorSprache = errorSpracheBuild.create();
                errorSprache.show();
            }
            transOption = new TranslatorOptions.Builder()
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
        loginFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regAct = new Intent(LoginActivity.this, RegisActivity.class);
                regAct.putExtra("sprache", sysSprache);
                LoginActivity.this.startActivity(regAct);
            }
        });
    }

    private void uebersetzung(String typ, DownloadConditions downloadConditions, String quellText) {
        /*transOption = new TranslatorOptions.Builder()
                .setSourceLanguage("de")
                .setTargetLanguage(zielSprache)
                .build();
        trans = Translation.getClient(transOption);
        DownloadConditions downCond = new DownloadConditions.Builder()
                .requireWifi().build();*/

        trans.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Verbindung hergestellt");
                        trans.translate(quellText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Log.d(TAG, "Übersetzung erfolgreich: " + s);
                                        switch (typ){
                                            case "Titel":
                                                titel = getString(R.string.title);
                                                titel = s;
                                                loginTitle.setText(titel);
                                            case "Mail":
                                                email = getString(R.string.mail);
                                                email = s;
                                                logintvMail.setText(email);
                                            case "PW":
                                                passwort = getString(R.string.passwort);
                                                passwort = s;
                                                logintvPW.setText(passwort);
                                            case "OK":
                                                ok = getString(R.string.ok);
                                                ok = s;
                                                loginOk.setText(ok);
                                            case "NOK":
                                                nok = getString(R.string.nok);
                                                nok = s;
                                                loginFalse.setText(nok);
                                            case "Reg":
                                                reg = getString(R.string.register);
                                                reg = s;
                                                loginReg.setText(reg);
                                            default:
                                                fehler = "Fehler";
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Fehler bei Übersetzung: " + e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fehler bei Verbindung: " + e.getMessage());
                    }
                });
    }

    private void loadSprache() {
        //Log.d(TAG, "Methode loadSprache");
        spracheBekannt = 'N';

        List<String> ListeSprachen = TranslateLanguage.getAllLanguages();
        //Log.d(TAG, "Arraylänge: " +ListeSprachen.size());
        int i = 0;
        while (i < ListeSprachen.size() && spracheBekannt != 'J') {
            //Log.d(TAG, "Stelle: " + i + " Sprache: " + ListeSprachen.get(i).toString());
            if (ListeSprachen.get(i).toString().equals(sysSprache)){
                //Log.d(TAG, "Sprache gefunden");
                spracheBekannt = 'J';
            }
            i++;
        }
    }
}
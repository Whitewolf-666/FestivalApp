package com.example.festivalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
    private EditText loginMail;
    private Button loginOk;
    private Button loginFalse;
    private String willkommen = "Willkommen";
    private String email = "E-Mail-Adresse";
    private String passwort = "Passwort";
    private String anmelden = "Anmelden";
    private String beenden = "Beenden";
    private String zielText;
    private Translator trans;
    private DownloadConditions downCond;
    private TranslatorOptions transOption;
    private String typ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTitle = findViewById(R.id.tVLoginTitle);
        loginMail = findViewById(R.id.etLoginMail);
        loginOk = findViewById(R.id.btnLoginOk);
        loginFalse = findViewById(R.id.btnLoginFalse);

        sysSprache = Locale.getDefault().getLanguage();

        Log.d(TAG, "Systemsprache: " + sysSprache);
        if (sysSprache.equals("de")) {
            loginTitle.setText(willkommen);
            loginMail.setText(email);
            loginOk.setText(anmelden);
            loginFalse.setText(beenden);
        }else
        {
            loadSprache();
            //Log.d(TAG, "Sprache bekannt: " + spracheBekannt);
            if (spracheBekannt == 'N'){
                sysSprache = "en";
                AlertDialog.Builder errorSpracheBuild = new AlertDialog.Builder(this);
                errorSpracheBuild.setMessage("Sprache wird nicht unterstütz. Es wird Englich verwendet.");
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
            uebersetzung("Titel", sysSprache, willkommen);
            uebersetzung("Mail", sysSprache, email);
            uebersetzung("Start", sysSprache, anmelden);
            uebersetzung("Ende", sysSprache, beenden);
        }
    }

    private void uebersetzung(String typ, String zielSprache, String quellText) {
        transOption = new TranslatorOptions.Builder()
                .setSourceLanguage("de")
                .setTargetLanguage(zielSprache)
                .build();
        trans = Translation.getClient(transOption);
        DownloadConditions downCond = new DownloadConditions.Builder()
                .requireWifi().build();

        trans.downloadModelIfNeeded(downCond)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Verbindung hergestellt");
                        trans.translate(quellText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Log.d(TAG, "Übersetzung erfolgreich: " + s);
                                        if (typ.equals("Titel")) {
                                            loginTitle.setText(s);
                                        }
                                        if (typ.equals("Mail")) {
                                            loginMail.setText(s);
                                        }
                                        if (typ.equals("Start")) {
                                            loginOk.setText(s);
                                        }
                                        if (typ.equals("Ende")) {
                                            loginFalse.setText(s);
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
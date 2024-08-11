package com.example.festivalapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class RegisActivity extends AppCompatActivity {

    private final String TAG = "RegisActivity";
    private DownloadConditions downCond;
    private Translator trans;
    private TranslatorOptions transOption;
    private AlertDialog.Builder messageScrren;
    private TextView regTvTitel;
    private TextView regIcMin;
    private TextView regICSond;
    private TextView regICNum;
    private TextView regICCaps;
    private TextView regTvMin;
    private TextView regTvSond;
    private TextView regTvNum;
    private TextView regTVCaps;
    private TextView regTvPwAnf;
    private EditText regEdMail;
    private EditText regEdPw;
    private EditText regEdPwSecond;
    private Button regBtnOk;
    private Button regBtnNok;
    private String sysSprache;
    private TextInputLayout regTilPw;
    private TextInputLayout regTilPwSec;
    private TextInputLayout regTilMail;
    private String regTitel = "Registrierung";
    private String regMail = "E-Mail: ";
    private String regPw = "Passwort: ";
    private String regPwSecond = "Passwort wiederholen: ";
    private String regPwAnf = "Anforderungen an das Passwort:";
    private String regMin = "Mindestens 8 Zeichen";
    private String regSond = "Mindestens 1 Sonderzeichen";
    private String regNum = "Mindestens 1 Zahl";
    private String regCaps = "Mindestens 1 Großbuchstabe";
    private String btnOk = "Speichern";
    private String btnNok = "Zurück";
    private String message;
    private boolean mailOK;
    private boolean pwOK;
    private boolean checkCapsOk = false;
    private boolean checkNumOk = false;
    private boolean checkSondOk = false;
    private boolean checkLenOk = false;
    private boolean enterPW = false;
    private int stelleCap = 0;
    private int stelleNum = 0;
    private int stelleSond = 0;
    private FirebaseAuth regFbAut;
    private FirebaseUser regFbUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        Intent regActivity = getIntent();
        Bundle bundleRegActivity = regActivity.getExtras();

        if (bundleRegActivity!=null){
            sysSprache = (String) bundleRegActivity.get("sprache");
        }
        Log.d(TAG, "Sprache: " + sysSprache);

        regTvTitel = findViewById(R.id.tvRegTitel);
        regBtnOk = findViewById(R.id.btnRegOk);
        regBtnNok = findViewById(R.id.btnRegNok);
        regTvPwAnf = findViewById(R.id.tvRegPWAnf);
        regIcMin = findViewById(R.id.tvRegIcMin);
        regICCaps = findViewById(R.id.tvRegIcCaps);
        regICNum = findViewById(R.id.tvRegIcNum);
        regICSond = findViewById(R.id.tvRegIcSond);
        regTvMin = findViewById(R.id.tvRegMinZeichen);
        regTVCaps = findViewById(R.id.tvRegCaps);
        regTvNum = findViewById(R.id.tvRegNum);
        regTvSond = findViewById(R.id.tvRegSonder);
        regEdMail = findViewById(R.id.edRegMail);
        regEdPw = findViewById(R.id.edRegPw);
        regEdPwSecond = findViewById(R.id.edRegPwSec);
        regTilPw = findViewById(R.id.tilRegPw);
        regTilMail = findViewById(R.id.tilRegMail);
        regTilPwSec = findViewById(R.id.tilRegPwSec);

        messageScrren = new AlertDialog.Builder(RegisActivity.this);

        if(sysSprache.equals("de")){
            regTvTitel.setText(regTitel);
            regTvPwAnf.setText(regPwAnf);
            regTvMin.setText(regMin);
            regTvNum.setText(regNum);
            regTVCaps.setText(regCaps);
            regTvSond.setText(regSond);
            regBtnOk.setText(btnOk);
            regBtnNok.setText(btnNok);
            regTilMail.setHint(regMail);
            regTilPw.setHint(regPw);
            regTilPwSec.setHint(regPwSecond);

        }else{
            transOption = new TranslatorOptions.Builder()
                    .setSourceLanguage("de")
                    .setTargetLanguage(sysSprache)
                    .build();
            trans = Translation.getClient(transOption);
            downCond = new DownloadConditions.Builder()
                    .requireWifi().build();

            uebersetzung("Titel", downCond, regTitel);
            uebersetzung("Mail", downCond, regMail);
            uebersetzung("PW", downCond, regPw);
            uebersetzung("PW2", downCond, regPwSecond);
            uebersetzung("PWAnf", downCond, regPwAnf);
            uebersetzung("PWMin", downCond, regMin);
            uebersetzung("PWNum", downCond, regNum);
            uebersetzung("PWSond", downCond, regSond);
            uebersetzung("PWCaps", downCond, regCaps);
            uebersetzung("BtnOk", downCond, btnOk);
            uebersetzung("BtnNok", downCond, btnNok);
        }

        regEdPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence input, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = String.valueOf(s.subSequence(start, start + count));
                boolean numOK;
                boolean capsOK;
                boolean sondOK;
                Drawable regIcOk = getDrawable(R.drawable.ic_done);
                Drawable regIcNok = getDrawable(R.drawable.ic_cancel);

                if (regEdPw.getText().toString().trim().length() >= 8){
                    if (enterPW==true){
                        regIcMin.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcOk, null);
                        checkLenOk = true;
                    }
                }else{
                    regIcMin.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcNok, null);
                    checkLenOk = false;
                }

                enterPW = true;

                numOK = checkPwNum(input);
                if (numOK == true) {
                    if (stelleNum == 0){
                        stelleNum = regEdPw.getText().length();
                    }
                }else{
                    if(stelleNum > regEdPw.getText().length()){
                        stelleNum = 0;
                    }
                }
                if (regEdPw.getText().length() == 0){
                    stelleNum = 0;
                }
                if (stelleNum > 0){
                    regICNum.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcOk, null);
                    checkNumOk = true;
                }else{
                    regICNum.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcNok, null);
                    checkNumOk = false;
                }

                capsOK = checkPwCaps(input);
                if (capsOK == true) {
                    if (stelleCap == 0){
                        stelleCap = regEdPw.getText().length();
                    }
                }else{
                    if(stelleCap > regEdPw.getText().length()){
                        stelleCap = 0;
                    }
                }
                if (regEdPw.getText().length() == 0){
                    stelleCap = 0;
                }
                if (stelleCap > 0){
                    regICCaps.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcOk, null);
                    checkCapsOk = true;
                }else{
                    regICCaps.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcNok, null);
                    checkCapsOk = false;
                }

                sondOK = checkPwSond(input);
                if (sondOK == true) {
                    if (stelleSond == 0){
                        stelleSond = regEdPw.getText().length();
                    }
                }else{
                    if(stelleSond > regEdPw.getText().length()){
                        stelleSond = 0;
                    }
                }
                if (regEdPw.getText().length() == 0){
                    stelleSond = 0;
                }
                if (stelleSond > 0){
                    regICSond.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcOk, null);
                    checkSondOk = true;
                }else{
                    regICSond.setCompoundDrawablesWithIntrinsicBounds(null, null, regIcNok, null);
                    checkSondOk = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        regBtnNok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                RegisActivity.super.getOnBackPressedDispatcher();
            }
        });

        regBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               regMail = regEdMail.getText().toString();
               mailOK = checkMail(regMail);
               if (mailOK == false){
                   message = "Bitte geben Sie eine gültige E-Mail-Adresse ein";
                    if (sysSprache.equals("de")){
                       messageScrren.setMessage(message);
                       messageScrren.setCancelable(true);
                       messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                           }
                       });
                       AlertDialog errorSprache = messageScrren.create();
                       errorSprache.show();
                   }else{
                        uebersetzung("Error", downCond, message);
                   }
               }else{
                   pwOK = checkPW(regEdPw.getText().toString().trim(), regEdPwSecond.getText().toString().trim());
               }

               if (mailOK == true && pwOK == true){
                   createAccount();
               }
            }
        });
    }

    private void createAccount() {
        regFbAut = FirebaseAuth.getInstance();
        String user = regEdMail.getText().toString().trim();
        String pw = regEdPw.getText().toString().trim();

        regFbAut.createUserWithEmailAndPassword(user, pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser veryfiyUser = regFbAut.getCurrentUser();
                            veryfiyUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    message = "Sie wurden erfoglreich registriert. Eine Bestätigungsmail wurde an die angegebene E-Mail-Adresse versendet.";
                                    if (sysSprache.equals("de")){
                                        messageScrren.setMessage(message);
                                        messageScrren.setCancelable(true);
                                        messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                RegisActivity.super.getOnBackPressedDispatcher();
                                            }
                                        });
                                        AlertDialog errorSprache = messageScrren.create();
                                        errorSprache.show();
                                    }else{
                                        uebersetzung("OK", downCond, message);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    message = "Es konnte keine Mail an die angegeben E-Mail-Adresse versendet werden: " + e.getMessage();
                                    if (sysSprache.equals("de")){
                                        messageScrren.setMessage(message);
                                        messageScrren.setCancelable(true);
                                        messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog errorSprache = messageScrren.create();
                                        errorSprache.show();
                                    }else{
                                        uebersetzung("Error", downCond, message);
                                    }
                                }
                            });

                        }else{
                            message = "Ihr Profil konnte nicht angelegt werden: " + task.getException().getMessage();
                            if (sysSprache.equals("de")){
                                messageScrren.setMessage(message);
                                messageScrren.setCancelable(true);
                                messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog errorSprache = messageScrren.create();
                                errorSprache.show();
                            }else{
                                uebersetzung("Error", downCond, message);
                            }
                        }
                    }
                });
    }

    private boolean checkPwSond(String passwort) {
        int pwDez = 0;
        if (!passwort.equals("")){
            pwDez = (int)passwort.charAt(0);
        }
        if (pwDez > 33 && pwDez < 48
            || pwDez > 57 && pwDez < 65
            || pwDez > 90 && pwDez < 97
            || pwDez > 123){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkPwCaps(String passwort) {
        final String regexCaps = "[A-Z]";

        if (passwort.matches(regexCaps)){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkPwNum(String passwort) {
        final String regexNum = "[0-9]";

        if (passwort.matches(regexNum)){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkPW(String pw, String pwWieder) {

        if (checkCapsOk == false
         || checkNumOk == false
         || checkSondOk == false
         || checkLenOk == false){
            message = "Anforderungen an das Passwort wurden nicht erfüllt!";
            if (sysSprache.equals("de")){

                messageScrren.setMessage(message);
                messageScrren.setCancelable(true);
                messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog errorSprache = messageScrren.create();
                errorSprache.show();
            }else{
                uebersetzung("Error", downCond, message);
            }
            return false;
        }else if (pw.equals("") || pwWieder.equals("")){
            message = "Beide Felder für die Passwörter müssen gefüllt sein!";
            if (sysSprache.equals("de")){

                messageScrren.setMessage(message);
                messageScrren.setCancelable(true);
                messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog errorSprache = messageScrren.create();
                errorSprache.show();
            }else{
                uebersetzung("Error", downCond, message);
            }
            return false;
        }else{
            if (!pw.equals(pwWieder)){
                message = "Passwörter stimmen nicht überein. Bitte Passwörter prüfen!";
                if (sysSprache.equals("de")){
                    messageScrren.setMessage(message);
                    messageScrren.setCancelable(true);
                    messageScrren.setNeutralButton("Error", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog errorSprache = messageScrren.create();
                    errorSprache.show();
                }else{
                    uebersetzung("Error", downCond, message);
                }
                return false;
            }else{
                return true;
            }
        }
    }

    private boolean checkMail(String email) {
        if(email == null){
            return false;
        }else{
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private void uebersetzung(String typ, DownloadConditions downloadConditions, String quellText) {
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
                                        if(typ.equals("Titel")){
                                            regTvTitel.setText(s);
                                        }
                                        if(typ.equals("Mail")){
                                            regTilMail.setHint(s);
                                        }
                                        if(typ.equals("PW")){
                                            regTilPw.setHint(s);
                                        }
                                        if(typ.equals("PW2")){
                                            regTilPwSec.setHint(s);
                                        }
                                        if(typ.equals("PWAnf")){
                                            regTvPwAnf.setText(s);
                                        }
                                        if(typ.equals("PWMin")){
                                            regTvMin.setText(s);
                                        }
                                        if(typ.equals("PWCaps")){
                                            regTVCaps.setText(s);
                                        }
                                        if(typ.equals("PWNum")){
                                            regTvNum.setText(s);
                                        }
                                        if(typ.equals("PWSond")){
                                            regTvSond.setText(s);
                                        }
                                        if(typ.equals("BtnOk")){
                                            regBtnOk.setText(s);
                                        }
                                        if(typ.equals("BtnNok")) {
                                            regBtnNok.setText(s);
                                        }
                                        if (typ.equals("Error")){
                                            messageScrren.setMessage(s);
                                            messageScrren.setCancelable(true);
                                            messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            AlertDialog errorSprache = messageScrren.create();
                                            errorSprache.show();
                                        }
                                        if (typ.equals("OK")){
                                            messageScrren.setMessage(s);
                                            messageScrren.setCancelable(true);
                                            messageScrren.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                    RegisActivity.super.getOnBackPressedDispatcher();
                                                }
                                            });
                                            AlertDialog errorSprache = messageScrren.create();
                                            errorSprache.show();
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
}
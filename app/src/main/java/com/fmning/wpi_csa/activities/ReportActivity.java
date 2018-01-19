package com.fmning.wpi_csa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCService;

/**
 * Created by Fangming
 * On 12/25/2017.
 */

public class ReportActivity extends AppCompatActivity {

    private EditText reportText;
    private int currentMenuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        currentMenuId = getIntent().getIntExtra("menuId", 0);

        LinearLayout reportView = (LinearLayout) findViewById(R.id.reportView);
        final EditText reporterEmail = (EditText) findViewById(R.id.reporterEmail);
        View separator = findViewById(R.id.reportSeparator);
        reportText = (EditText)findViewById(R.id.reportText);

        Button cancelButton = (Button) findViewById(R.id.reportCancelButton);
        Button doneButton = (Button) findViewById(R.id.reportDoneButton);

        if (Utils.appMode == AppMode.LOGGED_ON) {
            reportView.removeView(separator);
            reportView.removeView(reporterEmail);
        } else {
            String value = Utils.getParam(Utils.reportEmail);
            if (value != null) {
                reporterEmail.setText(value);
                reportText.requestFocus();
            }
        }

        DisplayMetrics window = getResources().getDisplayMetrics();
        final int otherHeight = (int)(window.density * 150);
        final int otherHeightLoggedin = (int)(window.density * 115);



        final Window mRootWindow = getWindow();
        View mRootView = mRootWindow.getDecorView().findViewById(android.R.id.content);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout(){
                        Rect r = new Rect();
                        View view = mRootWindow.getDecorView();
                        view.getWindowVisibleDisplayFrame(r);

                        if (Utils.appMode == AppMode.LOGGED_ON) {
                            reportText.getLayoutParams().height = r.bottom - otherHeightLoggedin;
                        } else {
                            reportText.getLayoutParams().height = r.bottom - otherHeight;
                        }
                    }
                });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessToken = null;
                final String email;

                if (Utils.appMode == AppMode.LOGGED_ON) {
                    accessToken = WCService.currentUser.accessToken;
                    email = WCService.currentUser.username;
                } else {
                    email = reporterEmail.getText().toString().trim();
                    if (!email.equals("") && Utils.isEmailAddress(email)) {
                        Utils.setParam(Utils.reportEmail, email);
                    } else if (!email.equals("")) {
                        Utils.showAlertMessage(ReportActivity.this, getString(R.string.report_email_error));
                        return;
                    }
                }

                String report = reportText.getText().toString().trim();
                if (report.equals("")) {
                    Utils.showAlertMessage(ReportActivity.this, getString(R.string.report_empty_error));
                    return;
                }

                Utils.showLoadingIndicator(ReportActivity.this);
                WCService.reportSGProblem(ReportActivity.this, currentMenuId, accessToken, email,
                        report, new WCService.OnReportProblemListener() {
                    @Override
                    public void OnReportProblemDone(String error) {
                        if (error.equals("")) {
                            Utils.hideLoadingIndicator();
                            finish();
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                            String message = email.equals("") ? getString(R.string.report_sent_whtout_email) :
                                    getString(R.string.report_sent_with_email);
                            Toast toast = Toast.makeText(ReportActivity.this, message, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Utils.hideLoadingIndicator();
                            Utils.processErrorMessage(ReportActivity.this, error, true);
                        }
                    }
                });
            }
        });

    }

    private void cancelClicked() {
        if (reportText.getText().toString().trim().equals("")) {
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(null)
                    .setMessage(getString(R.string.report_cancel_confirm)).setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            reportText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(reportText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    })
                    .show();
        }

    }

    @Override
    public void onBackPressed() {
        cancelClicked();
    }
}

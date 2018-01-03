package com.fmning.wpi_csa.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.FeedListAdapter;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.LoadingView;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCFeedManager;
import com.fmning.wpi_csa.webService.WCPaymentManager;
import com.fmning.wpi_csa.webService.WCService;
import com.fmning.wpi_csa.webService.objects.WCEvent;
import com.fmning.wpi_csa.webService.objects.WCFeed;

/**
 * Created by Fangming
 * On 12/30/2017.
 */

public class FeedFragment extends Fragment {

    private static int feedId;
    private WCEvent eventToPay;

    private static int EXT_STORE_REQUEST_CODE = 100;

    private FeedListAdapter tableViewAdapter;

    public FeedFragment(){}

    public static FeedFragment withFeed(Context context, WCFeed feed) {
        FeedFragment fragment = new FeedFragment();
        feedId = feed.id;
        fragment.tableViewAdapter = new FeedListAdapter(context, feed);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView tableView = (RecyclerView) view.findViewById(R.id.feedList);
        final LoadingView loadingView = (LoadingView) view.findViewById(R.id.feedLoadingView);

        view.findViewById(R.id.feedBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        WCFeedManager.getFeed(getActivity(), feedId, new WCFeedManager.OnGetFeedListener() {
            @Override
            public void OnGetFeedDone(String error, WCFeed feed) {
                if (!error.equals("")) {
                    Utils.processErrorMessage(getActivity(), error, true);
                } else {
                    tableViewAdapter.setAndProcessFeed(feed);
                    tableViewAdapter.notifyDataSetChanged();
                    loadingView.setVisibility(View.GONE);
                }
            }
        });

        tableViewAdapter.setOnFeedListener(new FeedListAdapter.FeedListListener() {
            @Override
            public void addToCalendar(WCEvent event) {

//                Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
//                ContentUris.appendId(eventsUriBuilder, event.startTime.getTime());
//                ContentUris.appendId(eventsUriBuilder, event.endTime.getTime());
//                Uri eventsUri = eventsUriBuilder.build();
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//                    Utils.logMsg("REQUESTING");
//                    requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 100);
//                }
//
//
//                Cursor cursor = getActivity().getContentResolver().query(
//                        eventsUri,
//                        new String[] {CalendarContract.Instances.DTSTART, CalendarContract.Instances.TITLE},
//                        CalendarContract.Instances.DTSTART + " >= " + event.startTime.getTime() + " and " + CalendarContract.Instances.DTSTART
//                                + " <= " + event.endTime.getTime() + " and " + CalendarContract.Instances.VISIBLE + " = 1",
//                        null,
//                        CalendarContract.Instances.DTSTART + " ASC");
//
//                try {
//                    if (cursor.getCount() > 0) {
//                        while (cursor.moveToNext()) {
//                            String name = cursor.getString(0);
//                            String displayName = cursor.getString(1);
//                            // This is actually a better pattern:
//                            String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
//                            Boolean selected = !cursor.getString(3).equals("0");
//                            //calendars.add(displayName);
//                        }
//                    }
//                } catch (AssertionError ex) { }
//
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, event.title);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, event.description);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTime.getTime());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTime.getTime());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.location);

                startActivity(intent);
            }

            @Override
            public void payAndGetTicket(WCEvent event) {
                if (event.fee > 0) {
                    Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_not_implemented_error));
                } else if (Utils.appMode != AppMode.LOGGED_ON) {
                    Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_not_loggedin_error));
                } else {
                    if (!WCService.currentUser.username.trim().toLowerCase().endsWith("@wpi.edu")) {
                        Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_not_wpi_email_error));
                    } else if (!WCService.currentUser.emailConfirmed) {
                        Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_email_unconfirmed_error));
                    } else {
                        eventToPay = event;
                        //TODO: M is 23. Currently built for 21 but many fearures require 23. So change to 23???????
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXT_STORE_REQUEST_CODE);
                        } else {
                            FeedFragment.this.payAndGetTicket();
                        }

                    }
                }
            }
        });
        tableView.setAdapter(tableViewAdapter);

        return view;
    }

    private void payAndGetTicket() {
        Utils.showLoadingIndicator(getActivity());
        WCPaymentManager.makePayment(getActivity(), "Event", eventToPay.id, eventToPay.fee, new WCPaymentManager.OnMakePaymentListener() {
            @SuppressWarnings("IfCanBeSwitch")
            @Override
            public void OnMakePaymentDone(String error, String status, String ticketStatus, final int ticketId, String ticket) {
                Utils.hideLoadingIndicator();
                if (!error.equals("")) {
                    Utils.processErrorMessage(getActivity(), error, true);
                } else {
                    if (status.equals("ok")) {
                        if (ticketStatus.equals("ok")) {
                            Uri uri = CacheManager.saveTicket(getActivity(), ticket);
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "application/pkpass");
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Utils.showAlertMessage(getActivity(), getString(R.string.no_ticket_app_error));
                            }
                        } else {
                            Utils.showAlertMessage(getActivity(), String.format(getString(R.string.payment_ticket_failed), ticketStatus));
                        }
                    } else if (status.equals("AlreadyPaid")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(null).setCancelable(false).setMessage(getString(R.string.get_ticket_again))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.showLoadingIndicator(getActivity());
                                        WCService.getTicket(getActivity(), ticketId, new WCService.OnGetTicketListener() {
                                            @Override
                                            public void OnGetTicketDone(String error, String ticket) {
                                                Utils.hideLoadingIndicator();
                                                if (!error.equals("")) {
                                                    Utils.processErrorMessage(getActivity(), error, true);
                                                } else {
                                                    Uri uri = CacheManager.saveTicket(getActivity(), ticket);

                                                    try {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(uri, "application/pkpass");
                                                        startActivity(intent);
                                                    } catch (ActivityNotFoundException e) {
                                                        Utils.showAlertMessage(getActivity(), getString(R.string.no_ticket_app_error));
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), null)
                                .show();
                    } else {
                        Utils.showAlertMessage(getActivity(), String.format(getString(R.string.payment_status_unknown), status));
                    }
                }
            }
        });


    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == EXT_STORE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                payAndGetTicket();
            } else {
                Utils.showAlertMessage(getActivity(), getString(R.string.storage_permission_error));
            }
        }
    }
}

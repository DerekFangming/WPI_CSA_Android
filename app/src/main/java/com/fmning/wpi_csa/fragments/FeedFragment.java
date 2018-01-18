package com.fmning.wpi_csa.fragments;

import android.Manifest;
import android.app.Activity;
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

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.FeedListAdapter;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.LoadingView;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCFeedManager;
import com.fmning.wpi_csa.webService.WCPaymentManager;
import com.fmning.wpi_csa.webService.WCService;
import com.fmning.wpi_csa.webService.WCUtils;
import com.fmning.wpi_csa.webService.objects.WCEvent;
import com.fmning.wpi_csa.webService.objects.WCFeed;

/**
 * Created by Fangming
 * On 12/30/2017.
 */

public class FeedFragment extends Fragment {

    private static WCFeed feed;

    private String paymentMethod = null;
    private String paymentNonce = null;

    private static int EXT_STORE_REQUEST_CODE = 100;
    private static int BRAIN_TREE_REQUEST_CODE = 101;

    private FeedListAdapter tableViewAdapter;

    public FeedFragment(){}

    public static FeedFragment withFeed(Context context, WCFeed feed) {
        FeedFragment fragment = new FeedFragment();
        FeedFragment.feed = feed;
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

        WCFeedManager.getFeed(getActivity(), feed.id, new WCFeedManager.OnGetFeedListener() {
            @Override
            public void OnGetFeedDone(String error, WCFeed feedWithBodyAndEvent) {
                if (!error.equals("")) {
                    Utils.processErrorMessage(getActivity(), error, true);
                } else {
                    feed.body = feedWithBodyAndEvent.body;
                    feed.event = feedWithBodyAndEvent.event;
                    tableViewAdapter.setAndProcessFeed(feed);
                    tableViewAdapter.notifyDataSetChanged();
                    loadingView.setVisibility(View.GONE);
                }
            }
        });

        tableViewAdapter.setOnFeedListener(new FeedListAdapter.FeedListListener() {
            @Override
            public void addToCalendar() {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, feed.event.title);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, feed.event.description);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, feed.event.startTime.getTime());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, feed.event.endTime.getTime());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, feed.event.location);

                startActivity(intent);
            }

            @Override
            public void payAndGetTicket() {
                if (Utils.appMode != AppMode.LOGGED_ON) {
                    Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_not_loggedin_error));
                } else if (!WCService.currentUser.emailConfirmed) {
                    Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_email_unconfirmed_error));
                } else if (feed.event.fee > 0) {
                    Utils.showLoadingIndicator(getActivity());
                    WCPaymentManager.checkPaymentStatus(getActivity(), "Event", feed.event.id, new WCPaymentManager.OnCheckPaymentStatusListener() {
                        @Override
                        public void OnCheckPaymentStatusDone(String error, String status, int ticketId) {
                            Utils.hideLoadingIndicator();
                            if (!error.equals("")) {
                                Utils.processErrorMessage(getActivity(), error, true);
                            } else if (status.equals("AlreadyPaid")) {
                                promptToDownloadTicket(ticketId);
                            } else if (status.equals("NotExist") || status.equals("Rejected")) {
                                DropInRequest dropInRequest = new DropInRequest().clientToken(WCUtils.clientToken);
                                startActivityForResult(dropInRequest.getIntent(getActivity()), BRAIN_TREE_REQUEST_CODE);
                            } else {
                                Utils.showAlertMessage(getActivity(), String.format(getString(R.string.payment_status_unknown), status));
                            }
                        }
                    });
                } else if (feed.event.fee == 0) {
                    if (!WCService.currentUser.username.trim().toLowerCase().endsWith("@wpi.edu")) {
                        Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_not_wpi_email_error));
                    } else if (!WCService.currentUser.emailConfirmed) {
                        Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.ticket_email_unconfirmed_error));
                    } else {
                        //TODO: M is 23. Currently built for 21 but many fearures require 23. So change to 23???????
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXT_STORE_REQUEST_CODE);
                        } else {
                            makePaymentRequest();
                        }

                    }
                } else {
                    Utils.showAlertMessage(getActivity(), getString(R.string.internal_error));
                }
            }
        });

        tableView.setAdapter(tableViewAdapter);

        return view;
    }
    private void makePaymentRequest() {
        String method = paymentMethod;
        String nonce = paymentNonce;

        if (paymentNonce != null) {
            paymentNonce = null;
            paymentMethod = null;
        }

        Utils.showLoadingIndicator(getActivity());
        WCPaymentManager.makePayment(getActivity(), "Event", feed.event.id, feed.event.fee, method, nonce,
                new WCPaymentManager.OnMakePaymentListener() {
            @SuppressWarnings("IfCanBeSwitch")
            @Override
            public void OnMakePaymentDone(String error, String status, final int ticketId, String ticket) {
                Utils.hideLoadingIndicator();
                if (!error.equals("")) {
                    Utils.processErrorMessage(getActivity(), error, true);
                } else if (status.equals("Done")) {
                    Uri uri = CacheManager.saveTicket(getActivity(), ticket);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "application/pkpass");
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Utils.showAlertMessage(getActivity(), getString(R.string.no_ticket_app_error));
                    } catch (Exception e) {
                        Utils.showAlertMessage(getActivity(), getString(R.string.internal_error) + e.getMessage());
                    }
                } else if (status.equals("AlreadyPaid")) {
                    promptToDownloadTicket(ticketId);
                } else {
                    Utils.showAlertMessage(getActivity(), String.format(getString(R.string.payment_status_unknown), status));
                }
            }
        });


    }

    private void promptToDownloadTicket (final int ticketId) {
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
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == EXT_STORE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePaymentRequest();
            } else {
                Utils.showAlertMessage(getActivity(), getString(R.string.storage_permission_error));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BRAIN_TREE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                try {
                    paymentNonce = result.getPaymentMethodNonce().getNonce();
                    try {
                        paymentMethod = result.getPaymentMethodType().getCanonicalName();
                    } catch (Exception e) {
                        paymentMethod = "Unknown";
                    }
                    if (paymentMethod.equals("American Express"))
                        paymentMethod = "AMEX";
                } catch (NullPointerException e) {
                    Utils.showAlertMessage(getActivity(), getString(R.string.brain_tree_error));
                }
                //TODO: M is 23. Currently built for 21 but many fearures require 23. So change to 23???????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXT_STORE_REQUEST_CODE);
                } else {
                    makePaymentRequest();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Utils.logMsg("CANCELLED");
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Utils.showAlertMessage(getActivity(), String.format(getString(R.string.brain_tree_error_with_message), error.getMessage()));
            }
        }
    }

}
